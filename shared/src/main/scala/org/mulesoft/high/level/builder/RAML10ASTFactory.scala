package org.mulesoft.high.level.builder

import amf.core.annotations.SourceAST
import amf.core.metamodel.document.{BaseUnitModel, DocumentModel, ExtensionLikeModel}
import amf.core.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, DomainExtensionModel, PropertyShapeModel, ShapeExtensionModel}
import amf.core.metamodel.domain.templates.{AbstractDeclarationModel, ParametrizedDeclarationModel, VariableValueModel}
import amf.core.model.document.{BaseUnit, Document, Fragment, Module}
import amf.core.model.domain.{AmfElement, _}
import amf.core.parser.{Annotations, Fields}
import amf.core.remote.{Raml10, Vendor}
import amf.plugins.document.webapi.metamodel.FragmentsTypesModels._
import amf.plugins.document.webapi.metamodel.{ExtensionModel, OverlayModel}
import amf.plugins.document.webapi.model.{AnnotationTypeDeclarationFragment, DataTypeFragment}
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorDeclaration
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.models.{Example, NodeShape}
import amf.plugins.domain.webapi.annotations.ParentEndPoint
import amf.plugins.domain.webapi.metamodel.security._
import amf.plugins.domain.webapi.metamodel.templates.{ParametrizedResourceTypeModel, ParametrizedTraitModel, ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.models.EndPoint
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import org.mulesoft.high.level.implementation.{ASTPropImpl, BasicValueBuffer, IValueBuffer, JSONValueBuffer}
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode}
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, NodeRange}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.nominal_interfaces.extras.UserDefinedExtra
import org.mulesoft.typesystem.syaml.to.json.{YJSONWrapper, YRange}
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.{AbstractType, BuiltinUniverse, Property, StructuredType}
import org.mulesoft.typesystem.project.{ITypeCollectionBundle, TypeCollectionBundle}
import org.yaml.model.{YMap, YNode, YPart, YScalar}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RAML10ASTFactory {
    private var _instance:Option[RAML10ASTFactory] = None
    def instance:RAML10ASTFactory = _instance.get

    def init():Future[Unit] = {
        if(_instance.isDefined){
            Future {}
        }
        else {
            _instance = Some(new RAML10ASTFactory)
            _instance.get.init()
        }
    }
}

class RAML10ASTFactory private extends DefaultASTFactory {

    var STRICT:Boolean = true

    var universe:Option[IUniverse] = None

    val builtinFacetMatchers:Map[String,IPropertyMatcher] = Map(
        "maximum"              -> FieldMatcher(ScalarShapeModel.Maximum),
        "minimum"              -> FieldMatcher(ScalarShapeModel.Minimum),
        "enum"                 -> FieldMatcher(ScalarShapeModel.Values),
        "format"               -> FieldMatcher(ScalarShapeModel.Format),
        "multipleOf"           -> FieldMatcher(ScalarShapeModel.MultipleOf),
        "minLength"            -> FieldMatcher(ScalarShapeModel.MinLength),
        "maxLength"            -> FieldMatcher(ScalarShapeModel.MaxLength),
        "pattern"              -> FieldMatcher(ScalarShapeModel.Pattern),

        "minItems"             -> FieldMatcher(ArrayShapeModel.MinItems),
        "maxItems"             -> FieldMatcher(ArrayShapeModel.MaxItems),
        "uniqueItems"          -> FieldMatcher(ArrayShapeModel.UniqueItems),

        "discriminator"        -> FieldMatcher(NodeShapeModel.Discriminator),
        "discriminatorValue"   -> FieldMatcher(NodeShapeModel.DiscriminatorValue),
        "additionalProperties" -> FieldMatcher(NodeShapeModel.Closed)
                .withCustomBuffer((e,n)=> AdditionalPropertiesValueBuffer(e,n)),
        "minProperties"        -> FieldMatcher(NodeShapeModel.MinProperties),
        "maxProperties"        -> FieldMatcher(NodeShapeModel.MaxProperties),

        "fileTypes"            -> FieldMatcher(FileShapeModel.FileTypes),

        "default"               -> FieldMatcher(ShapeModel.Default),
        "description"          -> FieldMatcher(ShapeModel.Description),
        "displayName"          -> FieldMatcher(ShapeModel.DisplayName)
    )

    val shapeMatcher:IPropertyMatcher =
        ((ThisMatcher() ifType PayloadModel) + PayloadModel.Schema
            | (ThisMatcher()  ifType ParameterModel) + ParameterModel.Schema
            | ((ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Schema)
            | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range)
            | (ThisMatcher() ifSubtype ShapeModel))

    val thisResourceBaseMatcher:IPropertyMatcher = ThisResourceBaseMatcher()
    val thisMethodBaseMatcher:IPropertyMatcher = ThisMethodBaseMatcher()

    def format:Vendor = Raml10

    def builtInFacetValue(name:String,shape:Shape):Option[Any]
        = builtinFacetMatchers.get(name).flatMap(matcher => {
                var results = matcher.operate(shape,null)
                if(results.isEmpty){
                    None
                }
                else{
                    results.head match {
                        case amr:AttributeMatchResult => amr.buffer.getValue
                        case _ => None
                    }
                }
            }
    )

    protected def init():Future[Unit] = Future {

        universe = UniverseProvider.universe(format)

        registerPropertyMatcher("LibraryBase", "types", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype ShapeModel)
        registerPropertyMatcher("LibraryBase", "traits", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype TraitModel)
        registerPropertyMatcher("LibraryBase", "resourceTypes", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype ResourceTypeModel)
        registerPropertyMatcher("LibraryBase", "annotationTypes", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype CustomDomainPropertyModel)
        registerPropertyMatcher("LibraryBase", "securitySchemes", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype SecuritySchemeModel)

        registerPropertyMatcher("Api", "title", WebApiModel.Name)
        registerPropertyMatcher("Api", "description", WebApiModel.Description)
        registerPropertyMatcher("Api", "version", WebApiModel.Version)
        registerPropertyMatcher("Api", "resources", ResourceExtractor())
        registerPropertyMatcher("Api", "securedBy", WebApiModel.Security)
        registerPropertyMatcher("Api", "baseUriParameters", ThisMatcher() + WebApiModel.Servers + ServerModel.Variables)
        registerPropertyMatcher("Api", "protocols", WebApiModel.Schemes)
        registerPropertyMatcher("Api", "documentation", WebApiModel.Documentations)
        registerPropertyMatcher("Api", "mediaType", ThisMatcher() + WebApiModel.ContentType
            & ThisMatcher() + WebApiModel.Accepts)
        registerPropertyMatcher("Api", "baseUri", ThisMatcher() + WebApiModel.Servers + ServerModel.Url)


        registerPropertyMatcher("DocumentationItem", "title", CreativeWorkModel.Title)
        registerPropertyMatcher("DocumentationItem", "content", CreativeWorkModel.Description)

        registerPropertyMatcher("MethodBase", "queryParameters", thisMethodBaseMatcher + OperationModel.Request + RequestModel.QueryParameters)
        registerPropertyMatcher("MethodBase", "headers", thisMethodBaseMatcher + OperationModel.Request + RequestModel.Headers)
        registerPropertyMatcher("MethodBase", "queryString", thisMethodBaseMatcher + OperationModel.Request + RequestModel.QueryString)
        registerPropertyMatcher("MethodBase", "responses", thisMethodBaseMatcher + OperationModel.Responses)
        registerPropertyMatcher("MethodBase", "body", thisMethodBaseMatcher + OperationModel.Request + RequestModel.Payloads)
        registerPropertyMatcher("MethodBase", "protocols", thisMethodBaseMatcher + OperationModel.Schemes)
        registerPropertyMatcher("MethodBase", "securedBy", thisMethodBaseMatcher + OperationModel.Security)
        registerPropertyMatcher("MethodBase", "description", thisMethodBaseMatcher + OperationModel.Description)
        registerPropertyMatcher("MethodBase", "displayName", thisMethodBaseMatcher + OperationModel.Name)
        registerPropertyMatcher("MethodBase", "is", thisMethodBaseMatcher + DomainElementModel.Extends ifType ParametrizedTraitModel)

        registerPropertyMatcher("Method", "method", OperationModel.Method)

        registerPropertyMatcher("Response", "code", ResponseModel.StatusCode)
        registerPropertyMatcher("Response", "headers", ResponseModel.Headers)
        registerPropertyMatcher("Response", "body", ResponseModel.Payloads)
        registerPropertyMatcher("Response", "description", ResponseModel.Description)

        registerPropertyMatcher("ResourceBase", "methods", thisResourceBaseMatcher + EndPointModel.Operations)
        registerPropertyMatcher("ResourceBase", "is", thisResourceBaseMatcher + DomainElementModel.Extends ifType ParametrizedTraitModel)
        registerPropertyMatcher("ResourceBase", "type", thisResourceBaseMatcher + DomainElementModel.Extends ifType ParametrizedResourceTypeModel)
        registerPropertyMatcher("ResourceBase", "description", thisResourceBaseMatcher + EndPointModel.Description)
        registerPropertyMatcher("ResourceBase", "securedBy", thisResourceBaseMatcher + EndPointModel.Security)
        registerPropertyMatcher("ResourceBase", "uriParameters", thisResourceBaseMatcher + EndPointModel.Parameters)
        registerPropertyMatcher("ResourceBase", "displayName", thisResourceBaseMatcher + EndPointModel.Name)

        registerPropertyMatcher("Resource", "relativeUri", ThisMatcher().withCustomBuffer((e, hl) => new RelativeUriValueBuffer(e, hl)))
        registerPropertyMatcher("Resource", "resources", ResourceExtractor())

        registerPropertyMatcher("ResourceType", "name", AbstractDeclarationModel.Name)
        registerPropertyMatcher("ResourceType", "parameters", AbstractDeclarationModel.Variables)
        //TODO:registerPropertyMatcher("ResourceType", "usage", AbstractDeclarationModel.Usage)

        registerPropertyMatcher("Trait", "name", AbstractDeclarationModel.Name)
        registerPropertyMatcher("Trait", "parameters", AbstractDeclarationModel.Variables)
        //TODO:registerPropertyMatcher("Trait", "usage", AbstractDeclarationModel.Usage)

        registerPropertyMatcher("AbstractSecurityScheme", "name", SecuritySchemeModel.Name)
        registerPropertyMatcher("AbstractSecurityScheme", "type", SecuritySchemeModel.Type)
        registerPropertyMatcher("AbstractSecurityScheme", "description", SecuritySchemeModel.Description)
        registerPropertyMatcher("AbstractSecurityScheme", "describedBy", ThisMatcher().withYamlPath("describedBy"))
        registerPropertyMatcher("AbstractSecurityScheme", "displayName", SecuritySchemeModel.DisplayName)
        registerPropertyMatcher("AbstractSecurityScheme", "settings", SecuritySchemeModel.Settings)

        registerPropertyMatcher("SecuritySchemePart", "queryParameters", SecuritySchemeModel.QueryParameters)
        registerPropertyMatcher("SecuritySchemePart", "headers", SecuritySchemeModel.Headers)
        registerPropertyMatcher("SecuritySchemePart", "queryString", SecuritySchemeModel.QueryString)
        registerPropertyMatcher("SecuritySchemePart", "responses", SecuritySchemeModel.Responses)



        registerPropertyMatcher("Scope", "name", ScopeModel.Name)


        registerPropertyMatcher("TypeDeclaration", "name",
            (ThisMatcher() ifType PayloadModel) + PayloadModel.MediaType
                | (ThisMatcher() ifType PayloadModel) + PayloadModel.Schema + ShapeModel.Name
                | (ThisMatcher() ifType ParameterModel) + ParameterModel.Name
                | ((ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Name
                & (ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Schema + ShapeModel.Name)
                | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Name
                & (ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range + ShapeModel.Name)
                | (ThisMatcher() ifSubtype ShapeModel) + ShapeModel.Name)

        registerPropertyMatcher("TypeDeclaration", "type", ThisMatcher() + TypePropertyMatcher())

        registerPropertyMatcher("TypeDeclaration", "displayName", shapeMatcher + builtinFacetMatchers("displayName"))
        registerPropertyMatcher("TypeDeclaration", "description",
            shapeMatcher + builtinFacetMatchers("description")
            & (ThisMatcher() ifType ParameterModel) + ShapeModel.DisplayName)
        registerPropertyMatcher("TypeDeclaration", "default",shapeMatcher + builtinFacetMatchers("default"))
        registerPropertyMatcher("TypeDeclaration", "required",
            (ThisMatcher() ifType ParameterModel) + ParameterModel.Required
            | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.MinCount).withCustomBuffer((e, hl) => new RequiredPropertyValueBuffer(e, hl)))

        registerPropertyMatcher("TypeDeclaration", "facets", shapeMatcher + ShapeModel.CustomShapePropertyDefinitions)
        registerPropertyMatcher("TypeDeclaration", "fixedFacets", shapeMatcher + ShapeModel.CustomShapeProperties)
        registerPropertyMatcher("TypeDeclaration", "examples", shapeMatcher +  AnyShapeModel.Examples + ExamplesFilter())
        registerPropertyMatcher("TypeDeclaration", "example", shapeMatcher +  AnyShapeModel.Examples + ExamplesFilter(true))
        registerPropertyMatcher("TypeDeclaration", "xml", shapeMatcher +  AnyShapeModel.XMLSerialization)
        registerPropertyMatcher("TypeDeclaration", "allowedTargets", CustomDomainPropertyModel.Domain)

        registerPropertyMatcher("ObjectTypeDeclaration", "properties",
            shapeMatcher + NodeShapeModel.Properties)
        registerPropertyMatcher("ObjectTypeDeclaration", "minProperties",
            shapeMatcher + builtinFacetMatchers("minProperties"))
        registerPropertyMatcher("ObjectTypeDeclaration", "maxProperties",
            shapeMatcher + builtinFacetMatchers("maxProperties"))
        registerPropertyMatcher("ObjectTypeDeclaration", "additionalProperties",
            shapeMatcher + builtinFacetMatchers("additionalProperties"))
        registerPropertyMatcher("ObjectTypeDeclaration", "discriminator",
            shapeMatcher + builtinFacetMatchers("discriminator"))
        registerPropertyMatcher("ObjectTypeDeclaration", "discriminatorValue",
            shapeMatcher + builtinFacetMatchers("discriminatorValue"))
//        registerPropertyMatcher("ObjectTypeDeclaration", "enum",
//            shapeMatcher + builtinFacetMatchers("enum"))

        registerPropertyMatcher("FileTypeDeclaration", "fileTypes",
            shapeMatcher + builtinFacetMatchers("fileTypes"))
        registerPropertyMatcher("FileTypeDeclaration", "minLength",
            shapeMatcher + builtinFacetMatchers("minLength"))
        registerPropertyMatcher("FileTypeDeclaration", "maxLength",
            shapeMatcher + builtinFacetMatchers("maxLength"))

        registerPropertyMatcher("StringTypeDeclaration", "pattern",
            shapeMatcher + builtinFacetMatchers("pattern"))
        registerPropertyMatcher("StringTypeDeclaration", "minLength",
            shapeMatcher + builtinFacetMatchers("minLength"))
        registerPropertyMatcher("StringTypeDeclaration", "maxLength",
            shapeMatcher + builtinFacetMatchers("maxLength"))
        registerPropertyMatcher("StringTypeDeclaration", "enum",
            shapeMatcher + builtinFacetMatchers("enum"))

        registerPropertyMatcher("ArrayTypeDeclaration", "items",
            shapeMatcher + ArrayShapeModel.Items)
        registerPropertyMatcher("ArrayTypeDeclaration", "uniqueItems",
            shapeMatcher + builtinFacetMatchers("uniqueItems"))
        registerPropertyMatcher("ArrayTypeDeclaration", "minItems",
            shapeMatcher + builtinFacetMatchers("minItems"))
        registerPropertyMatcher("ArrayTypeDeclaration", "maxItems",
            shapeMatcher + builtinFacetMatchers("maxItems"))

        registerPropertyMatcher("UnionTypeDeclaration", "anyOf",
            shapeMatcher + UnionShapeModel.AnyOf)

        registerPropertyMatcher("DateTimeTypeDeclaration", "format",
            shapeMatcher + builtinFacetMatchers("format"))

        registerPropertyMatcher("NumberTypeDeclaration", "minimum",
            shapeMatcher + builtinFacetMatchers("minimum"))
        registerPropertyMatcher("NumberTypeDeclaration", "maximum",
            shapeMatcher + builtinFacetMatchers("maximum"))
        registerPropertyMatcher("NumberTypeDeclaration", "format",
            shapeMatcher + builtinFacetMatchers("format"))
        registerPropertyMatcher("NumberTypeDeclaration", "multipleOf",
            shapeMatcher + builtinFacetMatchers("multipleOf"))
        registerPropertyMatcher("NumberTypeDeclaration", "enum",
            shapeMatcher + builtinFacetMatchers("enum"))

        registerPropertyMatcher("BooleanTypeDeclaration", "enum",
            shapeMatcher + builtinFacetMatchers("enum"))

        //user facet fix case
        registerPropertyMatcher("TypeExtension", "name",ThisMatcher() + ShapeExtensionModel.DefinedBy + PropertyShapeModel.Name)
        registerPropertyMatcher("TypeExtension", "value", (ThisMatcher()+ShapeExtensionModel.Extension).withCustomBuffer((e,n)=>JSONValueBuffer(e,n)))

        registerPropertyMatcher("Annotable", "annotations",
            ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range + DomainElementModel.CustomDomainProperties)
                | ((ThisMatcher() ifType CustomDomainPropertyModel) + DomainElementModel.CustomDomainProperties)
                | ((ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Schema + DomainElementModel.CustomDomainProperties)
                | ((ThisMatcher() ifType ParameterModel) + DomainElementModel.CustomDomainProperties)
                | ((ThisMatcher() ifType ParameterModel) + ParameterModel.Schema + DomainElementModel.CustomDomainProperties)
                | ((ThisMatcher() ifType PayloadModel) + PayloadModel.Schema + DomainElementModel.CustomDomainProperties)
                | DomainElementModel.CustomDomainProperties)

        registerPropertyMatcher("Reference", "name",
            (ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.Name
                | (ThisMatcher() ifSubtype ParametrizedDeclarationModel) + ParametrizedDeclarationModel.Name
                | (ThisMatcher() ifType ParametrizedSecuritySchemeModel) + ParametrizedSecuritySchemeModel.Name)

        registerPropertyMatcher("TemplateRef", "parameters",
            (ThisMatcher() ifSubtype ParametrizedDeclarationModel) + ParametrizedDeclarationModel.Variables)

        registerPropertyMatcher("TraitRef", "trait",
            (ThisMatcher() ifType ParametrizedTraitModel) + ParametrizedDeclarationModel.Target)

        registerPropertyMatcher("ResourceTypeRef", "resourceType",
            (ThisMatcher() ifType ParametrizedResourceTypeModel) + ParametrizedDeclarationModel.Target)

        registerPropertyMatcher("AnnotationRef", "name",
            (ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.Name)
        registerPropertyMatcher("AnnotationRef", "annotation",
            (ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.DefinedBy)
        registerPropertyMatcher("AnnotationRef", "value",
            ((ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.Extension).withCustomBuffer((e,n)=>JSONValueBuffer(e,n)))

        registerPropertyMatcher("TemplateParameter", "name",
            (ThisMatcher() ifType VariableValueModel) + VariableValueModel.Name)
        registerPropertyMatcher("TemplateParameter", "value",
            (ThisMatcher() ifType VariableValueModel) + VariableValueModel.Value)

        registerPropertyMatcher("SecuritySchemeRef", "securityScheme",
            (ThisMatcher() ifType ParametrizedSecuritySchemeModel) + ParametrizedSecuritySchemeModel.Scheme)
        registerPropertyMatcher("SecuritySchemeRef", "settings",
            (ThisMatcher() ifType ParametrizedSecuritySchemeModel) + ParametrizedSecuritySchemeModel.Settings)

        registerPropertyMatcher("OAuth1SecuritySchemeSettings", "authorizationUri", OAuth1SettingsModel.AuthorizationUri)
        registerPropertyMatcher("OAuth1SecuritySchemeSettings", "requestTokenUri", OAuth1SettingsModel.RequestTokenUri)
        registerPropertyMatcher("OAuth1SecuritySchemeSettings", "tokenCredentialsUri", OAuth1SettingsModel.TokenCredentialsUri)
        registerPropertyMatcher("OAuth1SecuritySchemeSettings", "signatures", OAuth1SettingsModel.Signatures)

        registerPropertyMatcher("OAuth2SecuritySchemeSettings", "accessTokenUri", OAuth2SettingsModel.AccessTokenUri)
        registerPropertyMatcher("OAuth2SecuritySchemeSettings", "authorizationUri", OAuth2SettingsModel.AuthorizationUri)
        registerPropertyMatcher("OAuth2SecuritySchemeSettings", "scopes", OAuth2SettingsModel.Scopes)
        registerPropertyMatcher("OAuth2SecuritySchemeSettings", "authorizationGrants", OAuth2SettingsModel.AuthorizationGrants)

        registerPropertyMatcher("XMLFacetInfo", "attribute", XMLSerializerModel.Attribute)
        registerPropertyMatcher("XMLFacetInfo", "wrapped", XMLSerializerModel.Wrapped)
        registerPropertyMatcher("XMLFacetInfo", "name", XMLSerializerModel.Name)
        registerPropertyMatcher("XMLFacetInfo", "namespace", XMLSerializerModel.Namespace)
        registerPropertyMatcher("XMLFacetInfo", "prefix", XMLSerializerModel.Prefix)

        registerPropertyMatcher("ExampleSpec", "value", ExampleModel.ExternalValue)
        registerPropertyMatcher("ExampleSpec", "strict", ExampleModel.Strict)
        registerPropertyMatcher("ExampleSpec", "name", ExampleModel.Name)
        registerPropertyMatcher("ExampleSpec", "displayName", ExampleModel.DisplayName)
        registerPropertyMatcher("ExampleSpec", "description", ExampleModel.Description)

        registerPropertyMatcher("Overlay", "usage", BaseUnitMatcher() + BaseUnitModel.Usage)
        registerPropertyMatcher("Overlay", "extends", BaseUnitMatcher() + ExtensionLikeModel.Extends)

        registerPropertyMatcher("Extension", "usage", BaseUnitMatcher() + BaseUnitModel.Usage)
        registerPropertyMatcher("Extension", "extends", BaseUnitMatcher() + ExtensionLikeModel.Extends)

        registerPropertyMatcher("Library", "usage", BaseUnitMatcher() + BaseUnitModel.Usage)
    }

    override def discriminate(clazz: ITypeDefinition, amfNode: AmfObject, nominalType:Option[ITypeDefinition]): ITypeDefinition = {

        var opt: Option[ITypeDefinition] = None
        amfNode match {
            case dElement: DomainElement =>
                var universe = clazz.universe
                if (clazz.isAssignableFrom("TypeDeclaration")) {
                    var shapeOpt: Option[Shape] = DefaultASTFactory.extractShape(dElement)
                    opt = shapeOpt.flatMap(shape=>discriminateShape(shape,universe)).map(builtInDeclaration=>{

                        var result = new StructuredType(shapeOpt.get.name.value(),builtInDeclaration.universe)
                        result.addSuperType(builtInDeclaration)
                        result.putExtra(UserDefinedExtra)

                        nominalType.foreach(_.allFacets.filter(f=>f.nameId.isDefined && f.range.isDefined)foreach(f=>{
                            result.addProperty(f.nameId.get,f.range.get)
                        }))
                        result
                    })
                }
                else if (clazz.isAssignableFrom("SecuritySchemeSettings")) {
                    opt = dElement.meta match {
                        case OAuth1SettingsModel => universe.`type`("OAuth1SecuritySchemeSettings")
                        case OAuth2SettingsModel => universe.`type`("OAuth2SecuritySchemeSettings")
                        //case ApiKeySettingsModel => None //seems like there is no such scheme in RAML
                        case _ => None
                    }
                }
            case _ =>
        }
        opt.getOrElse(clazz)
    }

    override def discriminateShape(shape:Shape, universe:IUniverse):Option[ITypeDefinition] = {
        shape.meta match {
            case AnyShapeModel => universe.`type`("TypeDeclaration")
            case NodeShapeModel => universe.`type`("ObjectTypeDeclaration")
            case FileShapeModel => universe.`type`("FileTypeDeclaration")
            case UnionShapeModel => universe.`type`("UnionTypeDeclaration")
            case ArrayShapeModel => universe.`type`("ArrayTypeDeclaration")
            case NilShapeModel => universe.`type`("NilTypeDeclaration")
            case ScalarShapeModel =>
                var simpleType = Option(shape.fields.get(ScalarShapeModel.DataType))
                    .map(dt=>{
                        var dataType = dt.asInstanceOf[AmfScalar].value.toString
                        dataType.substring(dataType.indexOf("#") + 1)
                    }).getOrElse("string")
                simpleType match {
                    case "string" => universe.`type`("StringTypeDeclaration")
                    case "number" => universe.`type`("NumberTypeDeclaration")
                    case "float" => universe.`type`("NumberTypeDeclaration")
                    case "integer" => universe.`type`("IntegerTypeDeclaration")
                    case "boolean" => universe.`type`("BooleanTypeDeclaration")
                    case "date" => universe.`type`("DateOnlyTypeDeclaration")
                    case "time" => universe.`type`("TimeOnlyTypeDeclaration")
                    case "dateTime" => universe.`type`("DateTimeTypeDeclaration")
                    case "dateTimeOnly" => universe.`type`("DateTimeOnlyTypeDeclaration")
                    case _ => None
                }
            case _ => universe.`type`("TypeDeclaration")
        }
    }

    override def builtinSuperType(shape:Shape):Option[ITypeDefinition] = {
        shape.meta match {
            case AnyShapeModel => Some(BuiltinUniverse.ANY)
            case NodeShapeModel => Some(BuiltinUniverse.OBJECT)
            case FileShapeModel => Some(BuiltinUniverse.FILE)
            case UnionShapeModel => Some(BuiltinUniverse.UNION)
            case ArrayShapeModel => Some(BuiltinUniverse.ARRAY)
            case NilShapeModel => Some(BuiltinUniverse.NIL)
            case ScalarShapeModel =>
                var simpleType = Option(shape.fields.get(ScalarShapeModel.DataType))
                    .map(dt=>{
                        var dataType = dt.asInstanceOf[AmfScalar].value.toString
                        dataType.substring(dataType.indexOf("#") + 1)
                    }).getOrElse("string")
                simpleType match {
                    case "string" => Some(BuiltinUniverse.STRING)
                    case "number" => Some(BuiltinUniverse.NUMBER)
                    case "float" => Some(BuiltinUniverse.FLOAT)
                    case "integer" => Some(BuiltinUniverse.INTEGER)
                    case "boolean" => Some(BuiltinUniverse.BOOLEAN)
                    case "date" => Some(BuiltinUniverse.DATE_ONLY)
                    case "time" => Some(BuiltinUniverse.TIME_ONLY)
                    case "dateTime" => Some(BuiltinUniverse.DATETIME)
                    case "dateTimeOnly" => Some(BuiltinUniverse.DATETIME_ONLY)
                    case _ => None
                }
            case _ => Some(BuiltinUniverse.ANY)
        }
    }

    override def determineRootType(baseUnit: BaseUnit, nominalType:Option[ITypeDefinition]): Option[ITypeDefinition] = {
        var u = universe.get
        var uName = u.name.orNull
        if (uName == "RAML") {
            baseUnit match {
                case fragment: Fragment =>
                    var typeOpt = fragment.meta match {
                        case SecuritySchemeFragmentModel => u.`type`("AbstractSecurityScheme")
                        case DataTypeFragmentModel => u.`type`("TypeDeclaration")
                        case ResourceTypeFragmentModel => u.`type`("ResourceType")
                        case TraitFragmentModel => u.`type`("Trait")
                        case NamedExampleFragmentModel => u.`type`("ExampleSpec")
                        case DocumentationItemFragmentModel => u.`type`("DocumentationItem")
                        case AnnotationTypeDeclarationFragmentModel => u.`type`("TypeDeclaration")
                        case _ => None
                    }
                    typeOpt match {
                        case Some(t) =>
                            var discriminated = discriminate(t, fragment.encodes, nominalType)
                            var fragmentTypeOpt = u.`type`("FragmentDeclaration")
                            fragmentTypeOpt match {
                                case Some(ft) =>
                                    if (discriminated.isInstanceOf[AbstractType]
                                        && ft.isInstanceOf[AbstractType]) {
                                        var result = new StructuredType(discriminated.nameId.get + "Fragment", u)
                                        result.addSuperType(discriminated.asInstanceOf[AbstractType])
                                        result.addSuperType(ft.asInstanceOf[AbstractType])
                                        Some(result)
                                    }
                                    else None
                                case _ => None
                            }
                        case _ => None

                    }
                case document: Document => document.meta match {
                    case ExtensionModel => u.`type`("Extension")
                    case OverlayModel => u.`type`("Overlay")
                    case _ => document.encodes match {
                        case de: DomainElement => de.meta match {
                            case WebApiModel => u.`type`("Api")
                            case _ => None
                        }
                    }
                }
                case module: Module => u.`type`("Library")
                case _ => None
            }
        }
        else {
            None
        }
    }

    def determineUserType(amfNode:AmfObject, nodeProperty:Option[IProperty], parent:Option[IHighLevelNode], _bundle:ITypeCollectionBundle):Option[ITypeDefinition] = {

        var shapeOpt = DefaultASTFactory.extractShape(amfNode)
        if(shapeOpt.isEmpty){
            None
        }
        else if(!_bundle.isInstanceOf[ITypeCollectionBundle]){
            None
        }
        else {
            var shape = shapeOpt.get
            var bundle = _bundle.asInstanceOf[TypeCollectionBundle]
            var result = amfNode match {
                case bu: BaseUnit => bu match {
                    case atdf:AnnotationTypeDeclarationFragment =>
                        var t = TypeBuilder.getOrCreate(shape,universe.get,bundle,this)
                        t.fixFacet("allowedTargets",atdf.encodes.domain,TypeBuilder.BUILTIN)
                        Some(t)
                    case dtf:DataTypeFragment =>
                        var t = TypeBuilder.getOrCreate(shape,universe.get,bundle,this)
                        Some(t)
                    case _ => None
                }
                case de: DomainElement =>
                    nodeProperty.flatMap(_.nameId).orNull match {
                        case "types" => bundle.getType(shape.id,shape.name.value())
                        case "annotationTypes" => bundle.getAnnotationType(shape.id,shape.name.value())
                        case "properties" =>
                            if(shape.name.nonEmpty) {
                                var pName = shape.name.value()
                                parent.flatMap(_.localType).flatMap(_.properties.find(_.nameId.get == pName)).flatMap(_.range)
                            }
                            else{
                                None
                            }
                        case "items" =>
                            parent.flatMap(_.localType).flatMap({
                                case at:IArrayType => at.componentType
                                case _ => None
                            })
                        case "facets" =>
                            if(shape.name.nonEmpty) {
                                var pName = shape.name.value()
                                parent.flatMap(_.localType).flatMap(_.facets.find(_.nameId.get == pName)).flatMap(_.range)
                            }
                            else {
                                None
                            }
                        case "headers" | "queryParameners" | "uriParameters" | "queryString" | "body" =>
                            Some(TypeBuilder.getOrCreate(shape,universe.get,bundle,this))
                        case _ => None
                    }
                case _ => None
            }
            result
        }
    }
}

//class BaseUriValueBuffer(element:AmfObject,hlNode:IHighLevelNode) extends IValueBuffer {
//
//    override def getValue: Option[Any] = {
//        element match {
//            case de:DomainElement =>
//                var hostOpt = Option(de.fields.get(WebApiModel.Host))
//                hostOpt match {
//                    case Some(host) => host.annotations.find (classOf[SourceAST] ).map (_.ast.asInstanceOf[YNode].value.asInstanceOf[YScalar].value)
//                    case _ => None
//                }
//            case _ => None
//        }
//    }
//
//    override def setValue(value: Any): Unit = {}
//
//    override def yamlNodes: Seq[YPart] = getFildValues.flatMap(_.annotations.find(classOf[SourceAST])).map(_.ast)
//
//    def getFildValues:List[AmfElement] = List(
//        element.fields.getValue(WebApiModel.Schemes),
//        element.fields.getValue(WebApiModel.Host),
//        element.fields.getValue(WebApiModel.BasePath)).filter(_ != null).map(_.value)
//}
//
//object BaseUriValueBuffer{
//    def apply (element:AmfObject,hlNode:IHighLevelNode):BaseUriValueBuffer = new BaseUriValueBuffer(element,hlNode)
//}

class RequiredPropertyValueBuffer(element:AmfObject,hlNode:IHighLevelNode) extends BasicValueBuffer(element,PropertyShapeModel.MinCount) {

    override def getValue: Option[Any] = {
        getMinCount match {
            case Some(mc) => Some(mc>0)
            case _ => None
        }
    }

    private def getMinCount = {
        super.getValue match {
            case Some(value) => value match {
                case minCount: Int => Some(minCount)
                case _ => None
            }
            case _ => None
        }
    }

    override def setValue(value: Any): Unit = {
        value match {
            case required:Boolean =>
                var newMinCount = if(required) 1 else 0
                getMinCount match {
                    case Some(mc) =>
                        if((mc>0) != required){
                            super.setValue(newMinCount)
                        }
                    case _ => super.setValue(newMinCount)
                }
            case _ =>
        }
    }
}


class RelativeUriValueBuffer(element:AmfObject,hlNode:IHighLevelNode) extends
    BasicValueBuffer(element,EndPointModel.Path) {

    private val UPDATE_CHILDREN:Boolean = true

    private val DO_NOT_UPDATE_CHILDREN:Boolean = false

    override def getValue: Option[Any] = {
        element match {
            case de:AmfObject =>
                Helpers.resourcePath(de) match {
                    case Some(ownPath) => Helpers.parentResource(element) match {
                        case Some(parent) => Helpers.resourcePath(parent) match {
                            case Some(parentPath) => Some(ownPath.substring(parentPath.length))
                            case None => Some(ownPath)
                        }
                        case None => Some(ownPath)
                    }
                    case _ => None
                }
            case _ => None
        }
    }

    override def setValue(value: Any): Unit = setValue(value,UPDATE_CHILDREN)

    def setValue(value: Any, updateChildren:Boolean): Unit = {
        element match {
            case de:AmfObject =>
                var pathValue = Helpers.parentResource(element) match {
                    case Some(parent) => Helpers.resourcePath(parent) match {
                        case Some(parentPath) => parentPath + value.toString
                        case None => value.toString
                    }
                    case None => value.toString
                }
                var toUpdate = collectResourceTree
                toAmfElement(pathValue,field.`type`,Annotations()).map(element.set(EndPointModel.Path, _))
                var apiOpt = Helpers.rootApi(hlNode)
                apiOpt.foreach(api=>{
                    element.adopted(api.id)
                })
                toUpdate.foreach(e=>e._2.setValue(e._1,DO_NOT_UPDATE_CHILDREN))
            case _ =>
        }
    }

    def collectResourceTree: Seq[(String,RelativeUriValueBuffer,IAttribute,IHighLevelNode)] = {
        var result:ListBuffer[(String,RelativeUriValueBuffer,IAttribute,IHighLevelNode)] = ListBuffer() ++= extractTuples(hlNode)
        var i = 0
        while(result.lengthCompare(i)>0){
            result ++= extractTuples(result(i)._4)
            i += 1
        }
        result
    }

    def extractTuples(n: IHighLevelNode): Seq[(String, RelativeUriValueBuffer, IAttribute, IHighLevelNode)]
    = {
        n.elements("resources").map(x => (x.attribute("relativeUri").get,x))
            .filter(e => e._1 match {
                case a: ASTPropImpl =>
                    a.buffer match {
                        case b: RelativeUriValueBuffer => true
                        case _ => false
                    }
                case _ => false
            }).map(e => (e._1.value.get.toString, e._1.asInstanceOf[ASTPropImpl].buffer.asInstanceOf[RelativeUriValueBuffer], e._1, e._2))
    }
}

class AdditionalPropertiesValueBuffer(element:AmfObject,hlNode:IHighLevelNode)
    extends BasicValueBuffer(element,NodeShapeModel.Closed) {

    override def getValue: Option[Boolean] = super.getValue match {
        case Some(value) => value match {
            case b:Boolean => Some(!b)
            case _ => None
        }
        case _ => None
    }

    override def setValue(value: Any): Unit = {
        value match {
            case b:Boolean => super.setValue(!b)
            case _ =>
        }
    }
}

object AdditionalPropertiesValueBuffer{
    def apply (element:AmfObject,hlNode:IHighLevelNode):AdditionalPropertiesValueBuffer = new AdditionalPropertiesValueBuffer(element,hlNode)
}

class ExamplesFilter(single:Boolean) extends IPropertyMatcher {
    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {
        obj match {
            case e:Example =>
                var hasNullName = Option(e.name.value()).isEmpty
                if(single == hasNullName){
                    List(ElementMatchResult(e))
                }
                else{
                    Seq()
                }
            case _ => Seq()
        }
    }

    override def doAppendNewValue(obj: AmfObject, hlNode: IHighLevelNode):Option[MatchResult] = None
}

object ExamplesFilter{
    def apply (single:Boolean=false):ExamplesFilter = new ExamplesFilter(single)
}

class ResourceExtractor extends IPropertyMatcher {



    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {
        var isApi: Boolean = false
        var resource: Option[EndPoint] = None
        obj match {
            case de: DomainElement =>
                de.meta match {
                    case EndPointModel => resource = Some(de.asInstanceOf[EndPoint])
                    case WebApiModel => isApi = true
                    case _ =>
                }
            case _ =>
        }
        if (!isApi && resource.isEmpty) {
            Seq()
        }
        else {
            var result: Seq[MatchResult] = Seq()
            var rawResult = Helpers.allResources(hlNode)
            if (isApi) {
                result = rawResult.flatMap(x => Helpers.parentResource(x) match {
                    case None => Some(ElementMatchResult(x))
                    case _ => None
                })
            }
            else if (resource.isDefined) {
                var resourceID = resource.get.id
                result = rawResult.flatMap(
                    x => Helpers.parentResource(x) match {
                        case Some(parent) =>
                            if(parent.id == resourceID){
                                Some(ElementMatchResult(x))
                            }
                            else None
                        case _ => None
                    })
            }
            result
        }
    }

    override def doAppendNewValue(obj: AmfObject, hlNode: IHighLevelNode):Option[MatchResult] = {
        var isApi: Boolean = false
        var resource: Option[EndPoint] = None
        obj match {
            case de: DomainElement =>
                de.meta match {
                    case EndPointModel => resource = Some(de.asInstanceOf[EndPoint])
                    case WebApiModel => isApi = true
                    case _ =>
                }
            case _ =>
        }
        if (!isApi && resource.isEmpty) {
            None
        }
        else {
            var annoations:Annotations = Annotations() ++= resource.map(ParentEndPoint(_))
            var newResource = EndPoint(annoations)
            var oldResources = Helpers.allResources(hlNode)
            var newResources:ListBuffer[EndPoint] = ListBuffer() ++= oldResources
            newResources += newResource

            Helpers.rootApi(hlNode).foreach(api=>api.fields.setWithoutId(WebApiModel.EndPoints,AmfArray(newResources)))
            var result = Some(ElementMatchResult(newResource))
            result
        }
    }
}

object ResourceExtractor {
    def apply():ResourceExtractor = new ResourceExtractor()
}

class TypePropertyMatcher() extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode):Seq[MatchResult] = doOperate(obj,hlNode,false)
    def doOperate(obj: AmfObject, hlNode: IHighLevelNode, forceCreate:Boolean=false): Seq[MatchResult] = {
        var jsonNodes: Option[Seq[JSONWrapper]] = obj.annotations.find(classOf[SourceAST]).flatMap(yn => YJSONWrapper(yn.ast)).map(w => w.kind match {
                      case STRING => List(w)
                      case OBJECT => w.propertyValue("type") match {
                          case Some(t) => t.kind match {
                              case ARRAY => t.value(ARRAY).get
                              case _ => List(t)
                          }
                          case _ => Seq()
                      }
                      case ARRAY => w.value(ARRAY).get
                      case _ => Seq()
        })
        if(forceCreate && (jsonNodes.isEmpty || jsonNodes.get.isEmpty)){
            jsonNodes = Some(List(YJSONWrapper(YScalar(""))))
        }
        jsonNodes match {
            case Some(nodes) => nodes.map(x => AttributeMatchResult(obj, new JSONValueBuffer(obj, hlNode, Option(x)){

                override def setValue(value:Any): Unit = {
                    var a = ParsedFromTypeExpression(value.toString)
                    var annotations = Annotations(a)
                    var shape = NodeShape(Fields(), annotations)
                    var arr = AmfArray(List(shape))
                    DefaultASTFactory.extractShape(hlNode.amfNode).foreach(x=>
                        x.fields.setWithoutId(ShapeModel.Inherits,arr))
                }
            }))
            case _ => Seq()
        }
    }

    override def doAppendNewValue(obj: AmfObject, hlNode: IHighLevelNode):Option[MatchResult]
        = doOperate(obj,hlNode,true).headOption
}

object TypePropertyMatcher{
    def apply():TypePropertyMatcher = new TypePropertyMatcher()
}

class ThisResourceBaseMatcher() extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {

        obj match {
            case de:DomainElement =>
                de.meta match {
                    case  EndPointModel => List(ElementMatchResult(de))
                    case ResourceTypeModel =>
                        val matchedNode = de match {
                            case ed:ErrorDeclaration => de
                            case rt:ResourceType => rt.asEndpoint(hlNode.amfBaseUnit)
                            case _ => de
                        }
                        List(ElementMatchResult(matchedNode))
                    case _ => Seq()
                }
            case _ => Seq()
        }
    }

    override def doAppendNewValue(obj: AmfObject, hlNode: IHighLevelNode):Option[MatchResult] = doOperate(obj,hlNode).headOption
}

object ThisResourceBaseMatcher {
    def apply():ThisResourceBaseMatcher = new ThisResourceBaseMatcher()
}

class ThisMethodBaseMatcher() extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {

        obj match {
            case de:DomainElement =>
                de.meta match {
                    case  OperationModel => List(ElementMatchResult(de))
                    case TraitModel =>
                        val matchedNode = de match {
                            case ed:ErrorDeclaration => de
                            case tr:Trait => tr.asOperation(hlNode.amfBaseUnit)
                            case _ => de
                        }
                        List(ElementMatchResult(matchedNode))
                    case _ => Seq()
                }
            case _ => Seq()
        }
    }

    override def doAppendNewValue(obj: AmfObject, hlNode: IHighLevelNode):Option[MatchResult] = doOperate(obj,hlNode).headOption
}

object ThisMethodBaseMatcher {
    def apply():ThisMethodBaseMatcher = new ThisMethodBaseMatcher()
}


object Helpers {

    private val allResourcesMatcher: IPropertyMatcher = BaseUnitMatcher() + DocumentModel.Encodes + WebApiModel.EndPoints

    private val rootApiMatcher: IPropertyMatcher = BaseUnitMatcher() + DocumentModel.Encodes

    def rootApi(hlNode:IHighLevelNode):Option[AmfObject] =
        rootApiMatcher.operate(hlNode.amfNode, hlNode).headOption.map(_.node)

    def allResources(hlNode:IHighLevelNode):Seq[EndPoint] = {
        allResourcesMatcher.operate(hlNode.amfBaseUnit,hlNode).flatMap({
            case em: ElementMatchResult => em.node match {
                case de:DomainElement => de.meta match {
                    case EndPointModel => Some(de.asInstanceOf[EndPoint])
                    case _ => None
                }
                case _ => None
            }
            case _ => None
        })
    }

    def parentResource(de:AmfElement):Option[EndPoint] = {
        de.annotations.find(classOf[ParentEndPoint]) match {
            case Some(pep) => Option(pep.parent)
            case _ => None
        }
    }

    def resourcePath(element:AmfObject):Option[String] = {
        Option(element.fields.get(EndPointModel.Path)) match {
            case Some(ownPath) => ownPath match {
                case sc: AmfScalar => Option(sc.value) match {
                    case Some(v) => Some(v.toString)
                    case _ => None
                }
                case _ => None
            }
            case _ => None
        }
    }
}

