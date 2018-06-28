package org.mulesoft.high.level.builder

import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.metamodel.domain.{ExternalSourceElementModel, ShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.remote.{Raml08, Vendor}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.models.ArrayShape
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.security._
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.extras.UserDefinedExtra
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.StructuredType
import org.mulesoft.typesystem.project.ITypeCollectionBundle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object RAML08ASTFactory {
    private var _instance:Option[RAML08ASTFactory] = None
    def instance:RAML08ASTFactory = _instance.get

    def init():Future[Unit] = {
        if(_instance.isDefined){
            Future {}
        }
        else {
            _instance = Some(new RAML08ASTFactory)
            _instance.get.init()
        }
    }
}

class RAML08ASTFactory private extends RAMLASTFactory {

    def format:Vendor = Raml08

    private val parameterShapeMatcher =
        (((((ThisMatcher() ifType ParameterModel) + ParameterModel.Schema) |
            ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range))
            ifType ArrayShapeModel) + ArrayShapeModel.Items) |
        (((ThisMatcher() ifType ParameterModel) + ParameterModel.Schema) |
            ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Range))

    private val payloadSchemaMatcher = ThisMatcher() + PayloadModel.Schema

    override protected def init():Future[Unit] = Future {

        super.init()

        registerPropertyMatcher("Api", "schemas", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype ShapeModel)
        registerPropertyMatcher("Api", "traits", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype TraitModel)
        registerPropertyMatcher("Api", "resourceTypes", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype ResourceTypeModel)
        registerPropertyMatcher("Api", "securitySchemes", BaseUnitMatcher()
            + DocumentModel.Declares ifSubtype SecuritySchemeModel)

        registerPropertyMatcher("GlobalSchema", "key", SchemaShapeModel.Name)
        //registerPropertyMatcher("GlobalSchema", "value", SchemaShapeModel.Name)

        registerPropertyMatcher("Parameter", "name",
            ((ThisMatcher() ifType ParameterModel) + ParameterModel.Name) |
            ((ThisMatcher() ifType PropertyShapeModel) + PropertyShapeModel.Name))

        //registerPropertyMatcher("Parameter", "type", ThisMatcher() + TypePropertyMatcher())


        registerPropertyMatcher("Parameter", "displayName", parameterShapeMatcher + ShapeModel.DisplayName)
        registerPropertyMatcher("Parameter", "description", parameterShapeMatcher + ShapeModel.Description)
        registerPropertyMatcher("Parameter", "default",parameterShapeMatcher + ShapeModel.Default)
        registerPropertyMatcher("Parameter", "required", ParameterModel.Required)
        registerPropertyMatcher("Parameter", "example", parameterShapeMatcher +  AnyShapeModel.Examples + ExamplesFilter(true) + ExternalSourceElementModel.Raw)

        registerPropertyMatcher("Parameter", "pattern", parameterShapeMatcher + ScalarShapeModel.Pattern)
        registerPropertyMatcher("Parameter", "minLength", parameterShapeMatcher + ScalarShapeModel.MinLength)
        registerPropertyMatcher("Parameter", "maxLength", parameterShapeMatcher + ScalarShapeModel.MaxLength)
        registerPropertyMatcher("Parameter", "enum", parameterShapeMatcher + ScalarShapeModel.Values)

        registerPropertyMatcher("BodyLike", "name", PayloadModel.MediaType)
        registerPropertyMatcher("BodyLike", "example", payloadSchemaMatcher + AnyShapeModel.Examples + ExamplesFilter(true) + ExternalSourceElementModel.Raw )
        registerPropertyMatcher("BodyLike", "formParameters", payloadSchemaMatcher + NodeShapeModel.Properties )

//        registerPropertyMatcher("BodyLike", "schema", payloadSchemaMatcher + ExamplesFilter(true) + ExternalSourceElementModel.Raw )

   }

    override def discriminate(clazz: ITypeDefinition, amfNode: AmfObject, nominalType:Option[ITypeDefinition]): ITypeDefinition = {

        var opt: Option[ITypeDefinition] = None
        amfNode match {
            case dElement: DomainElement =>
                var universe = clazz.universe
                if (clazz.isAssignableFrom("Parameter")) {
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
            case AnyShapeModel => universe.`type`("Parameter")
            case NodeShapeModel => universe.`type`("Parameter")
            case FileShapeModel => universe.`type`("FileTypeDeclaration")
            case ArrayShapeModel => Option(shape.asInstanceOf[ArrayShape].items).flatMap(discriminateShape(_,universe))
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
                    case "date" => universe.`type`("DateTypeDeclaration")
                    case _ => None
                }
            case _ => universe.`type`("Parameter")
        }
    }

    override def determineRootType(baseUnit: BaseUnit, nominalType:Option[ITypeDefinition]): Option[ITypeDefinition] = universe.get.`type`("Api")

    def determineUserType(amfNode:AmfObject, nodeProperty:Option[IProperty], parent:Option[IHighLevelNode], _bundle:ITypeCollectionBundle):Option[ITypeDefinition] = None
}
