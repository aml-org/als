package org.mulesoft.high.level.builder

import amf.core.metamodel.document.{BaseUnitModel, DocumentModel, ExtensionLikeModel}
import amf.core.metamodel.domain.{DomainElementModel, ExternalSourceElementModel, ShapeModel}
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, DomainExtensionModel, PropertyShapeModel, ShapeExtensionModel}
import amf.core.model.document.{BaseUnit, Document, Fragment, Module}
import amf.core.model.domain._
import amf.core.remote.{Raml10, Vendor}
import amf.plugins.document.webapi.metamodel.FragmentsTypesModels._
import amf.plugins.document.webapi.metamodel.{ExtensionModel, OverlayModel}
import amf.plugins.document.webapi.model.{AnnotationTypeDeclarationFragment, DataTypeFragment}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.webapi.metamodel.security._
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.metamodel._
import org.mulesoft.high.level.implementation.{BasicValueBuffer, JSONValueBuffer}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.nominal_interfaces.extras.UserDefinedExtra
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.{AbstractType, StructuredType}
import org.mulesoft.typesystem.project.{ITypeCollectionBundle, TypeCollectionBundle}

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

class RAML10ASTFactory private extends RAMLASTFactory {



    val shapeMatcher:IPropertyMatcher =
        ((ThisMatcher() ifType PayloadModel) + PayloadModel.Schema
            | (ThisMatcher()  ifType ParameterModel) + ParameterModel.Schema
            | ((ThisMatcher() ifType CustomDomainPropertyModel) + CustomDomainPropertyModel.Schema)
            | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range)
            | (ThisMatcher() ifSubtype ShapeModel))

    def format:Vendor = Raml10

    override protected def init():Future[Unit] = Future {

        super.init()

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
            | ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.MinCount).withCustomBuffer((e, hl) => RequiredPropertyValueBuffer(e, hl)))

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

        registerPropertyMatcher("AnnotationRef", "name",
            (ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.Name)
        registerPropertyMatcher("AnnotationRef", "annotation",
            (ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.DefinedBy)
        registerPropertyMatcher("AnnotationRef", "value",
            ((ThisMatcher() ifType DomainExtensionModel) + DomainExtensionModel.Extension).withCustomBuffer((e,n)=>JSONValueBuffer(e,n)))

        registerPropertyMatcher("XMLFacetInfo", "attribute", XMLSerializerModel.Attribute)
        registerPropertyMatcher("XMLFacetInfo", "wrapped", XMLSerializerModel.Wrapped)
        registerPropertyMatcher("XMLFacetInfo", "name", XMLSerializerModel.Name)
        registerPropertyMatcher("XMLFacetInfo", "namespace", XMLSerializerModel.Namespace)
        registerPropertyMatcher("XMLFacetInfo", "prefix", XMLSerializerModel.Prefix)

        registerPropertyMatcher("ExampleSpec", "value", ThisMatcher() + ExampleModel.ExternalValue | ExternalSourceElementModel.Raw)
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
                    .map(RAMLASTFactory.dataTypeToString).getOrElse("string")
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

object RequiredPropertyValueBuffer {
    def apply(element: AmfObject, hlNode: IHighLevelNode): RequiredPropertyValueBuffer = new RequiredPropertyValueBuffer(element, hlNode)
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
