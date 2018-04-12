package org.mulesoft.high.level.builder

import amf.core.annotations.SourceAST
import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.remote.{Oas, Vendor}
import amf.plugins.document.webapi.model.{AnnotationTypeDeclarationFragment, DataTypeFragment}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.security._
import org.mulesoft.high.level.implementation.BasicValueBuffer
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.project.{ITypeCollectionBundle, TypeCollectionBundle}
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object OAS20ASTFactory {
    private var _instance:Option[OAS20ASTFactory] = None
    def instance:OAS20ASTFactory = _instance.get

    def init():Future[Unit] = {
        if(_instance.isDefined){
            Future {}
        }
        else {
            _instance = Some(new OAS20ASTFactory)
            _instance.get.init()
        }
    }
}

class OAS20ASTFactory private extends DefaultASTFactory {

    var universe:Option[IUniverse] = None

    def format:Vendor = Oas



    val builtinFacetMatchers:Map[String,IPropertyMatcher] = Map(
        "format"            -> FieldMatcher(ScalarShapeModel.Format),
        "maximum"           -> FieldMatcher(ScalarShapeModel.Maximum),
        "exclusiveMaximum"  -> FieldMatcher(ScalarShapeModel.ExclusiveMaximum),
        "minimum"           -> FieldMatcher(ScalarShapeModel.Minimum),
        "exclusiveMinimum"  -> FieldMatcher(ScalarShapeModel.ExclusiveMinimum),
        "maxLength"         -> FieldMatcher(ScalarShapeModel.MaxLength),
        "minLength"         -> FieldMatcher(ScalarShapeModel.MinLength),
        "pattern"           -> FieldMatcher(ScalarShapeModel.Pattern),
        "enum"              -> FieldMatcher(ScalarShapeModel.Values),
        "multipleOf"        -> FieldMatcher(ScalarShapeModel.MultipleOf),

        "discriminator"     -> FieldMatcher(NodeShapeModel.Discriminator),
        "additionalProperties" -> FieldMatcher(NodeShapeModel.Closed)
            .withCustomBuffer((e,n)=> AdditionalPropertiesValueBuffer(e,n)),

        "maxItems"          -> FieldMatcher(ArrayShapeModel.MaxItems),
        "minItems"          -> FieldMatcher(ArrayShapeModel.MinItems),
        "uniqueItems"       -> FieldMatcher(ArrayShapeModel.UniqueItems),

        "description"       -> FieldMatcher(ShapeModel.Description),
        "default"           -> FieldMatcher(AnyShapeModel.Default),
        "displayName"             -> FieldMatcher(AnyShapeModel.DisplayName),
        "collectionFormat"  -> FieldMatcher(ArrayShapeModel.CollectionFormat)
        //TODO:"readOnly"          -> FieldMatcher(AnyShapeModel.ReDisplayName),
    )

    val shapeMatcher:IPropertyMatcher = createShapeMatcher

    protected def init():Future[Unit] = Future {
        universe = UniverseProvider.universe(format)
        registerPropertyMatcher("SwaggerObject", "info", ThisMatcher().withYamlPath("info"))
        registerPropertyMatcher("SwaggerObject", "host", ThisMatcher() + WebApiModel.Servers + ServerModel.Url)
        registerPropertyMatcher("SwaggerObject", "basePath", ThisMatcher() + WebApiModel.Servers + ServerModel.Url)
        registerPropertyMatcher("SwaggerObject", "schemes", WebApiModel.Schemes)
        registerPropertyMatcher("SwaggerObject", "consumes", WebApiModel.Accepts)
        registerPropertyMatcher("SwaggerObject", "produces", WebApiModel.ContentType)
        registerPropertyMatcher("SwaggerObject", "paths", ThisMatcher().withYamlPath("paths"))
        registerPropertyMatcher("SwaggerObject", "definitions", BaseUnitMatcher() + DocumentModel.Declares ifSubtype ShapeModel)
        registerPropertyMatcher("SwaggerObject", "parameters", BaseUnitMatcher() + DocumentModel.Declares ifType ParameterModel)
        registerPropertyMatcher("SwaggerObject", "responses", BaseUnitMatcher() + DocumentModel.Declares ifType ResponseModel)
        registerPropertyMatcher("SwaggerObject", "securityDefinitions", BaseUnitMatcher() + DocumentModel.Declares ifType SecuritySchemeModel)
        registerPropertyMatcher("SwaggerObject", "security", WebApiModel.Security)
        registerPropertyMatcher("SwaggerObject", "tags", WebApiModel.Tags)
        registerPropertyMatcher("SwaggerObject", "externalDocs", WebApiModel.Documentations)

        registerPropertyMatcher("InfoObject", "title", WebApiModel.Name)
        registerPropertyMatcher("InfoObject", "version", WebApiModel.Version)
        registerPropertyMatcher("InfoObject", "description", WebApiModel.Description)
        registerPropertyMatcher("InfoObject", "termsOfService", WebApiModel.TermsOfService)
        registerPropertyMatcher("InfoObject", "license", WebApiModel.License)
        registerPropertyMatcher("InfoObject", "contact", WebApiModel.Provider)

        registerPropertyMatcher("LicenseObject", "name", LicenseModel.Name)
        registerPropertyMatcher("LicenseObject", "url", LicenseModel.Url)

        registerPropertyMatcher("ContactObject", "name", OrganizationModel.Name)
        registerPropertyMatcher("ContactObject", "url", OrganizationModel.Url)
        registerPropertyMatcher("ContactObject", "email", OrganizationModel.Email)

        registerPropertyMatcher("ExternalDocumentationObject", "url", CreativeWorkModel.Url)
        registerPropertyMatcher("ExternalDocumentationObject", "description", CreativeWorkModel.Description)

        registerPropertyMatcher("PathsObject", "paths", WebApiModel.EndPoints)

        registerPropertyMatcher("PathItemObject", "path", EndPointModel.Path)
        registerPropertyMatcher("PathItemObject", "operations", EndPointModel.Operations)
        registerPropertyMatcher("PathItemObject", "parameters", (ThisMatcher() + EndPointModel.Parameters)
            * EndPointModel.Payloads)

        //TODO:registerPropertyMatcher("OperationObject", "tags", OperationModel.Description)
        registerPropertyMatcher("OperationObject", "summary", OperationModel.Summary)
        registerPropertyMatcher("OperationObject", "description", OperationModel.Description)
        registerPropertyMatcher("OperationObject", "externalDocs", OperationModel.Documentation)
        registerPropertyMatcher("OperationObject", "operationId", OperationModel.Name)
        registerPropertyMatcher("OperationObject", "method", OperationModel.Method)
        registerPropertyMatcher("OperationObject", "consumes", OperationModel.Accepts)
        registerPropertyMatcher("OperationObject", "produces", OperationModel.ContentType)
        registerPropertyMatcher("OperationObject", "parameters", ThisMatcher() + OperationModel.Request
            + (ThisMatcher() + RequestModel.Headers) * RequestModel.QueryParameters * RequestModel.Payloads)
        registerPropertyMatcher("OperationObject", "responses", ThisMatcher().withYamlPath("responses"))
        registerPropertyMatcher("OperationObject", "schemes", OperationModel.Schemes)
        registerPropertyMatcher("OperationObject", "deprecated", OperationModel.Deprecated)
        registerPropertyMatcher("OperationObject", "security", OperationModel.Security)

        registerPropertyMatcher("ResponsesObject", "responses", OperationModel.Responses)

        registerPropertyMatcher("ResponseDefinitionObject", "key", ResponseModel.Name)
        registerPropertyMatcher("Response", "code", ResponseModel.StatusCode)
        registerPropertyMatcher("ResponseObject", "description", ResponseModel.Description)
        registerPropertyMatcher("ResponseObject", "schema", ThisMatcher() + ResponseModel.Payloads + PayloadModel.Schema)
        registerPropertyMatcher("ResponseObject", "headers", ResponseModel.Headers)
        registerPropertyMatcher("ResponseObject", "example", ResponseModel.Examples)


        registerPropertyMatcher("ParameterDefinitionObject", "key", ParameterModel.Name)
        registerPropertyMatcher("ParameterObject", "name", ParameterModel.ParameterName)
        registerPropertyMatcher("ParameterObject", "description", ParameterModel.Description)
        registerPropertyMatcher("ParameterObject", "in", ParameterModel.Binding)
        registerPropertyMatcher("ParameterObject", "required", ParameterModel.Required)
        //TODO:registerPropertyMatcher("CommonParameterObject", "allowEmptyValue", ParameterModel.Binding)

        registerPropertyMatcher("BodyParameterObject", "schema", ParameterModel.Schema)
        registerPropertyMatcher("BodyParameterObject", "name", ThisMatcher() + ParameterModel.Schema + SchemaShapeModel.Name)


        registerPropertyMatcher("DefinitionObject", "name", ShapeModel.Name)

        registerPropertyMatcher("ItemsObject", "type", createShapeMatcher.withCustomBuffer((e,n)=>TypePropertyValueBuffer(e,n)))
        registerPropertyMatcher("ItemsObject", "format", shapeMatcher + builtinFacetMatchers("format"))
        registerPropertyMatcher("ItemsObject", "default", shapeMatcher + builtinFacetMatchers("default"))
        registerPropertyMatcher("ItemsObject", "maximum", shapeMatcher + builtinFacetMatchers("maximum"))
        registerPropertyMatcher("ItemsObject", "exclusiveMaximum", shapeMatcher + builtinFacetMatchers("exclusiveMaximum"))
        registerPropertyMatcher("ItemsObject", "minimum", shapeMatcher + builtinFacetMatchers("minimum"))
        registerPropertyMatcher("ItemsObject", "exclusiveMinimum", shapeMatcher + builtinFacetMatchers("exclusiveMinimum"))
        registerPropertyMatcher("ItemsObject", "maxLength", shapeMatcher + builtinFacetMatchers("maxLength"))
        registerPropertyMatcher("ItemsObject", "minLength", shapeMatcher + builtinFacetMatchers("minLength"))
        registerPropertyMatcher("ItemsObject", "pattern", shapeMatcher + builtinFacetMatchers("pattern"))
        registerPropertyMatcher("ItemsObject", "maxItems", shapeMatcher + builtinFacetMatchers("maxItems"))
        registerPropertyMatcher("ItemsObject", "minItems", shapeMatcher + builtinFacetMatchers("minItems"))
        registerPropertyMatcher("ItemsObject", "uniqueItems", shapeMatcher + builtinFacetMatchers("uniqueItems"))
        registerPropertyMatcher("ItemsObject", "enum", shapeMatcher + builtinFacetMatchers("enum"))
        registerPropertyMatcher("ItemsObject", "multipleOf", shapeMatcher + builtinFacetMatchers("multipleOf"))
        registerPropertyMatcher("ItemsObject", "example", shapeMatcher + AnyShapeModel.Examples)
        registerPropertyMatcher("ItemsObject", "items", shapeMatcher + ArrayShapeModel.Items)
        registerPropertyMatcher("ItemsObject", "collectionFormat", shapeMatcher + builtinFacetMatchers("collectionFormat"))
        //TODO:registerPropertyMatcher("ItemsObject", "collectionFormat", ParameterModel.Binding)

        registerPropertyMatcher("SchemaObject", "name",
            ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Name)
                | ((ThisMatcher() ifType ParameterModel) + ParameterModel.Name)
                | ((ThisMatcher() ifType PayloadModel) + PayloadModel.Schema + ShapeModel.Name)
                | ((ThisMatcher() ifSubtype ShapeModel) + ShapeModel.Name))

        registerPropertyMatcher("SchemaObject", "title", shapeMatcher + builtinFacetMatchers("displayName"))
        registerPropertyMatcher("SchemaObject", "description", shapeMatcher + builtinFacetMatchers("description"))
        registerPropertyMatcher("SchemaObject", "properties", shapeMatcher + NodeShapeModel.Properties)
        registerPropertyMatcher("SchemaObject", "discriminator", shapeMatcher + builtinFacetMatchers("discriminator"))
        registerPropertyMatcher("SchemaObject", "xml", shapeMatcher + AnyShapeModel.XMLSerialization)
        registerPropertyMatcher("SchemaObject", "externalDocs", shapeMatcher + AnyShapeModel.Documentation)
        registerPropertyMatcher("SchemaObject", "allOf", shapeMatcher + ShapeModel.Inherits)
        registerPropertyMatcher("SchemaObject", "required", ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.MinCount).withCustomBuffer((e, hl) => new RequiredPropertyValueBuffer(e, hl)))
        registerPropertyMatcher("SchemaObject", "additionalProperties", builtinFacetMatchers("additionalProperties"))
        registerPropertyMatcher("SchemaObject", "additionalPropertiesSchema", shapeMatcher + NodeShapeModel.AdditionalPropertiesSchema)
        //TODO: registerPropertyMatcher("SchemaObject", "readOnly", ShapeModel.Name)
        //TODO:registerPropertyMatcher("SchemaObject", "$ref", ShapeModel.Name)

        registerPropertyMatcher("HeaderObject", "name", ParameterModel.Name)
        registerPropertyMatcher("HeaderObject", "description", shapeMatcher + SchemaShapeModel.Description & ParameterModel.Description)

        registerPropertyMatcher("WithSpecificationExtensions", "specificationExtensions", DomainElementModel.CustomDomainProperties)
//
//TODO: tags
//        registerPropertyMatcher("TagObject", "name", ShapeModel.Name)
//        registerPropertyMatcher("TagObject", "description", ShapeModel.Name)
//        registerPropertyMatcher("TagObject", "externalDocs", ShapeModel.Name)

        registerPropertyMatcher("SecurityDefinitionObject", "name", SecuritySchemeModel.Name)
        registerPropertyMatcher("SecurityDefinitionObject", "type", SecuritySchemeModel.Type)
        registerPropertyMatcher("SecurityDefinitionObject", "description", SecuritySchemeModel.Description)

        registerPropertyMatcher("ApiKey", "name", ThisMatcher() + SecuritySchemeModel.Settings + ApiKeySettingsModel.Name)
        registerPropertyMatcher("ApiKey", "in", ThisMatcher() + SecuritySchemeModel.Settings + ApiKeySettingsModel.In)

        registerPropertyMatcher("OAuth2", "flow", ThisMatcher() + SecuritySchemeModel.Settings + OAuth2SettingsModel.Flow)
        registerPropertyMatcher("OAuth2", "authorizationUrl", ThisMatcher() + SecuritySchemeModel.Settings + OAuth2SettingsModel.AuthorizationUri)
        registerPropertyMatcher("OAuth2", "tokenUrl", ThisMatcher() + SecuritySchemeModel.Settings + OAuth2SettingsModel.AccessTokenUri)
        registerPropertyMatcher("OAuth2", "scopes", (ThisMatcher() + SecuritySchemeModel.Settings).withYamlPath("scopes"))

        registerPropertyMatcher("ScopesObject", "scopes", OAuth2SettingsModel.Scopes)

        registerPropertyMatcher("ScopeObject", "name", ScopeModel.Name)
        registerPropertyMatcher("ScopeObject", "description", ScopeModel.Description)

        registerPropertyMatcher("XMLObject", "attribute", XMLSerializerModel.Attribute)
        registerPropertyMatcher("XMLObject", "wrapped", XMLSerializerModel.Wrapped)
        registerPropertyMatcher("XMLObject", "name", XMLSerializerModel.Name)
        registerPropertyMatcher("XMLObject", "namespace", XMLSerializerModel.Namespace)
        registerPropertyMatcher("XMLObject", "prefix", XMLSerializerModel.Prefix)

        registerPropertyMatcher("TagObject", "name", TagModel.Name)
        registerPropertyMatcher("TagObject", "description", TagModel.Description)
        registerPropertyMatcher("TagObject", "externalDocs", TagModel.Documentation)
    }


    override def determineRootType(baseUnit: BaseUnit, nominalType:Option[ITypeDefinition]): Option[ITypeDefinition] = universe.get.`type`("SwaggerObject")

    override def discriminate(clazz: ITypeDefinition, amfNode: AmfObject, nominalType:Option[ITypeDefinition]): ITypeDefinition = {

        var opt: Option[ITypeDefinition] = None
        amfNode match {
            case dElement: DomainElement =>
                var universe = clazz.universe
                if(clazz.isAssignableFrom("ParameterObject")){
                    var isDefinition = clazz.isAssignableFrom("ParameterDefinitionObject")
                    var isInBody = false
                    Option(amfNode.fields.get(ParameterModel.Binding)) match {
                        case Some(x) => x match {
                            case scalar:AmfScalar => isInBody = scalar.value == "body"
                            case _ =>
                        }
                        case None =>
                    }
                    opt = if(dElement.meta == PayloadModel || isInBody){
                        if (isDefinition) {
                            universe.`type`("BodyParameterDefinitionObject")
                        }
                        else {
                            universe.`type`("BodyParameterObject")
                        }
                    }
                    else {
                        Option(dElement.fields.getValue(ParameterModel.Schema)) match {
                            case Some(fieldValue) => fieldValue.value match {
                                case sch: Shape => sch.meta match {
                                    case NodeShapeModel =>
                                        if (isDefinition) {
                                            universe.`type`("BodyParameterDefinitionObject")
                                        }
                                        else {
                                            universe.`type`("BodyParameterObject")
                                        }
                                    case _ =>
                                        if (isDefinition) {
                                            universe.`type`("CommonParameterDefinitionObject")
                                        }
                                        else {
                                            universe.`type`("CommonParameterObject")
                                        }
                                }
                                case _ => None
                            }
                            case _ => None
                        }
                    }
                }
                else if (clazz.isAssignableFrom("SecurityDefinitionObject")) {
                    opt = Option(dElement.fields.getValue(SecuritySchemeModel.Settings)) match {
                        case Some(fieldValue) => fieldValue.value match {
                            case settingsElement:DomainElement => settingsElement.meta match {
                                //case OAuth1SettingsModel => universe.`type`("OAuth1SecuritySchemeSettings") no support for oauth 1 in swagger
                                case OAuth2SettingsModel => universe.`type`("OAuth2")
                                case ApiKeySettingsModel => universe.`type`("ApiKey")
                            }
                            case _ => Option(dElement.fields.getValue(SecuritySchemeModel.Type)) match {
                                case Some(fieldValue1) => fieldValue1.value match {
                                    case amfScalar:AmfScalar => amfScalar.value match {
                                        case "oauth2" => universe.`type`("OAuth2")
                                        case "apiKey" => universe.`type`("ApiKey")
                                        case "basic" => universe.`type`("Basic")
                                    }
                                    case _ => None
                                }
                                case _ => None
                            }
                        }
                        case _ => None
                    }
                }
            case _ =>
        }
        opt.getOrElse(clazz)
    }

    override def discriminateShape(shape: Shape, universe: IUniverse): Option[ITypeDefinition] = None

    override def builtinSuperType(shape:Shape):Option[ITypeDefinition] = None

    def builtInFacetValue(name:String,shape:Shape):Option[Any] = None

    def determineUserType(amfNode:AmfObject, nodeProperty:Option[IProperty], parent:Option[IHighLevelNode], _bundle:ITypeCollectionBundle):Option[ITypeDefinition] = {

        var shapeOpt = extractShape(amfNode)
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
                case de: DomainElement =>
                    nodeProperty.flatMap(_.nameId).orNull match {
                        case "parameters" | "declarations" | "headers" =>
                            Option(TypeBuilder.getOrCreate(shape,universe.get,bundle,this))
                        case "properties" => None
                            var pName = shape.name
                            parent.flatMap(_.localType).flatMap(_.properties.find(_.nameId.get == pName)).flatMap(_.range)
                        case "items" => None
                            var pName = shape.name
                            parent.flatMap(_.localType).flatMap({
                                case at:IArrayType => at.componentType
                                case _ => None
                            })
                        case _ => None
                    }
                case _ => None
            }
            result
        }
    }

    def createShapeMatcher:IPropertyMatcher =
        ((ThisMatcher() ifType PayloadModel) + PayloadModel.Schema
            | (ThisMatcher()  ifType ParameterModel) + ParameterModel.Schema
            //            | ((ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Schema)
            | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range)
            | (ThisMatcher() ifSubtype ShapeModel))
}

class TypePropertyValueBuffer(element:AmfObject,hlNode:IHighLevelNode) extends BasicValueBuffer(element,ScalarShapeModel.DataType) {

    override def getValue: Option[Any] = {

        var resultOpt = super.getValue.flatMap({
            case Some(dt) => dt match {
                case dataType:String =>
                    var ind = dataType.indexOf("#")
                    if(ind>=0){
                        Some(dataType.substring(dataType.indexOf("#") + 1))
                    }
                    else None
                case _ => None
            }
            case _ => None
        })
        if(resultOpt.isDefined){
            resultOpt
        }
        else{
            element.annotations.find(classOf[SourceAST]) match {
                case Some(src) => YJSONWrapper(src.ast).flatMap(_.propertyValue("type",STRING))
                case _ => None
            }
        }
    }

    override def setValue(value: Any): Unit = {
    }
}

object TypePropertyValueBuffer{
    def apply(element:AmfObject,hlNode:IHighLevelNode):TypePropertyValueBuffer = new TypePropertyValueBuffer(element,hlNode)
}