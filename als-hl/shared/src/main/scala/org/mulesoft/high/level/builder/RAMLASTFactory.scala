package org.mulesoft.high.level.builder

import amf.core.annotations.SourceAST
import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.metamodel.domain.extensions.DomainExtensionModel
import amf.core.metamodel.domain.templates.{AbstractDeclarationModel, ParametrizedDeclarationModel, VariableValueModel}
import amf.core.model.domain._
import amf.core.parser.{Annotations, Fields}
import amf.core.remote.Vendor
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorDeclaration
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.models.{Example, NodeShape}
import amf.plugins.domain.webapi.annotations.ParentEndPoint
import amf.plugins.domain.webapi.metamodel.security._
import amf.plugins.domain.webapi.metamodel.templates.{ParametrizedResourceTypeModel, ParametrizedTraitModel, ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.models.{EndPoint, Operation}
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import org.mulesoft.high.level.implementation.{ASTPropImpl, BasicValueBuffer}
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode}
import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.BuiltinUniverse
import org.mulesoft.typesystem.typesystem_interfaces.Extra

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class RAMLASTFactory protected extends DefaultASTFactory {

    var STRICT:Boolean = true

    var universe:Option[IUniverse] = None

    val thisResourceBaseMatcher:IPropertyMatcher = ThisResourceBaseMatcher()
    val thisMethodBaseMatcher:IPropertyMatcher = ThisMethodBaseMatcher()

    def format:Vendor

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
}

object RAMLASTFactory {
    def dataTypeToString(dt: AmfElement): String = {
        var dataType = dt.asInstanceOf[AmfScalar].value.toString
        dataType.substring(dataType.indexOf("#") + 1)
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

    override def doAppendNewValue(cfg:NodeCreationConfig):Option[MatchResult] = None
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

    override def doAppendNewValue(cfg:NodeCreationConfig):Option[MatchResult] = {
        var isApi: Boolean = false
        var resource: Option[EndPoint] = None
        cfg.obj match {
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
            var oldResources = Helpers.allResources(cfg.hlNode)
            var newResources:ListBuffer[EndPoint] = ListBuffer() ++= oldResources
            newResources += newResource

            Helpers.rootApi(cfg.hlNode).foreach(api=>api.fields.setWithoutId(WebApiModel.EndPoints,AmfArray(newResources)))
            var result = Some(ElementMatchResult(newResource))
            result
        }
    }
}

object ResourceExtractor {
    def apply():ResourceExtractor = new ResourceExtractor()
}

class ThisResourceBaseMatcher() extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {

        obj match {
            case de:DomainElement =>
                de.meta match {
                    case  EndPointModel => List(ElementMatchResult(de))
                    case ResourceTypeModel =>
                        try{
                            val matchedNode = hlNode.getExtra(AsEndPoint) match {
                                case Some(op) => op
                                case _ => de match {
                                    case ed: ErrorDeclaration => de
                                    case rt: ResourceType => {
                                        val stored = rt.asEndpoint(hlNode.amfBaseUnit)
                                        hlNode.putExtra(AsEndPoint,stored)
                                        stored
                                    }
                                    case _ => de
                                }
                            }
                            List(ElementMatchResult(matchedNode))
                        }
                        catch {
                            case e:Throwable => Seq()
                        }
                    case _ => Seq()
                }
            case _ => Seq()
        }
    }

    override def doAppendNewValue(cfg:NodeCreationConfig):Option[MatchResult] = doOperate(cfg.obj,cfg.hlNode).headOption
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
                        try {
                            val matchedNode = hlNode.getExtra(AsOperation) match {
                                case Some(op) => op
                                case _ => de match {
                                    case ed: ErrorDeclaration => de
                                    case tr: Trait => {
                                        val stored = tr.asOperation(hlNode.amfBaseUnit)
                                        hlNode.putExtra(AsOperation,stored)
                                        stored
                                    }
                                    case _ => de
                                }
                            }
                            List(ElementMatchResult(matchedNode))
                        }
                        catch {
                            case e:Throwable => Seq()
                        }
                    case _ => Seq()
                }
            case _ => Seq()
        }
    }

    override def doAppendNewValue(cfg:NodeCreationConfig):Option[MatchResult] = doOperate(cfg.obj,cfg.hlNode).headOption
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

    def parentResource(de:AmfElement):Option[EndPoint] = de.annotations.find(classOf[ParentEndPoint]).flatMap(_.parent)

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

object AsOperation extends Extra[Operation] {
    override def name: String = "AsOperation"

    override def clazz: Class[Operation] = classOf[Operation]

    override def default: Option[Operation] = None
}

object AsEndPoint extends Extra[EndPoint] {
    override def name: String = "AsEndPoint"

    override def clazz: Class[EndPoint] = classOf[EndPoint]

    override def default: Option[EndPoint] = None
}


