package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Aml, Oas, Oas20, Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion, SuggestionCategoryRegistry}
import org.mulesoft.als.suggestions.interfaces.{
  ICompletionPlugin,
  ICompletionRequest,
  ICompletionResponse,
  LocationKind,
  Syntax
}
import org.mulesoft.als.suggestions.plugins.raml.{
  AnnotationReferencesCompletionPlugin,
  ExampleStructureCompletionPlugin
}
import org.mulesoft.high.level.builder.ProjectBuilder
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.positioning.{YamlLocation, YamlPartWithRange}
import org.mulesoft.typesystem.nominal_interfaces.extras.{DescriptionExtra, PropertySyntaxExtra}
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty}
import org.yaml.model.{YMap, YMapEntry, YNode, YScalar}

import scala.concurrent.{Future, Promise}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import amf.core.parser._

class StructureCompletionPlugin extends ICompletionPlugin {

  override def id: String = StructureCompletionPlugin.ID

  override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages

  override def isApplicable(request: ICompletionRequest): Boolean =
    request.config.astProvider match {
      case Some(astProvider) =>
        if (request.astNode.isEmpty || request.astNode.get == null)
          false
        else if (AnnotationReferencesCompletionPlugin().isApplicable(request) ||
                 ExampleStructureCompletionPlugin().isApplicable(request))
          false
        else if (languages.indexOf(astProvider.language) < 0)
          false
        else if (isContentType(request))
          true
        else if (isDiscriminatorValue(request))
          true
        else
          request.actualYamlLocation match {
            case Some(l) =>
              if (l.inKey(request.position)) {
                if (request.astNode.get.isAttr)
                  true
                else if (request.yamlLocation.get.mapEntry.exists(x => x.yPart == l.mapEntry.get.yPart))
                  true
                else
                  request.yamlLocation.get.value.get.yPart match {
                    case m: YMap =>
                      l.mapEntry match {
                        case Some(me) =>
                          m.entries.contains(me.yPart)
                        case _ => false
                      }
                    case _ => false
                  }
              } else if (isDefinitionRequired(request))
                true
              else if (isSecurityReference(request))
                true
              else if (isRequestParameterName(request))
                true
              else if (isMethodKey(request)) true
              else isEmptyMap(l)
            case _ => false
          }
      case _ => false
    }

  def isEmptyMap(yamlLocation: YamlLocation): Boolean = {
    yamlLocation.node.flatMap(n => n.yPart.toOption[YMap]) match {
      case Some(m) => m.entries.isEmpty
      case _       => false
    }
  }

  def isRequestParameterName(request: ICompletionRequest): Boolean =
    request.astNode
      .flatMap(_.asAttr)
      .flatMap(
        node =>
          node.parent.flatMap(
            parent =>
              node.property.flatMap(prop =>
                prop.nameId.map(name =>
                  "name".equals(name) &&
                    parent.definition.isAssignableFrom("BodyParameterObject") &&
                    parent.attribute("in").flatMap(inAttr => inAttr.value).exists("query".equals)))))
      .getOrElse(false)

  def isSecurityReference(request: ICompletionRequest): Boolean =
    request.astNode
      .flatMap(_.asElement)
      .exists(_.definition.isAssignableFrom("SecurityRequirementObject"))

  def isDefinitionRequired(request: ICompletionRequest): Boolean =
    request.astNode
      .map(_.asElement)
      .flatMap(
        node =>
          request.actualYamlLocation.flatMap(
            _.keyNode.map(kn =>
              kn.yPart match {
                case yPart: YNode =>
                  "required".equals(yPart.toString()) &&
                    node.exists(_.definition.isAssignableFrom("DefinitionObject"))
                case _ => false
            })
        ))
      .getOrElse(false)

  def isMethodKey(request: ICompletionRequest): Boolean =
    request.astNode
      .flatMap(node => node.property.flatMap(property => property.domain.map(_.isAssignableFrom("Method"))))
      .getOrElse(false)

  def contentTypes(request: ICompletionRequest): Seq[String] =
    request.astNode
      .flatMap(_.asElement)
      .flatMap(_.definition.universe.`type`("Api"))
      .flatMap(_.property("mediaType"))
      .flatMap(_.getExtra(PropertySyntaxExtra))
      .map(_.oftenValues.map(_.asInstanceOf[String]))
      .getOrElse(Nil)

  def isBody(node: IParseResult): Boolean =
    node.property
      .flatMap(_.nameId)
      .exists("body".equals)

  def isDiscriminatorValue(request: ICompletionRequest): Boolean =
    !(request.astNode.exists(_.property.isEmpty) || request.astNode.flatMap(_.property).contains(null)) &&
      !request.astNode.flatMap(_.property).flatMap(_.nameId).exists(_ != "discriminator") &&
      request.astNode.flatMap(_.property).flatMap(_.domain).exists(_.isAssignableFrom("TypeDeclaration"))

  def isContentType(request: ICompletionRequest): Boolean = {
    getBody(request).exists(isBody) &&
    (request.actualYamlLocation.flatMap(_.keyValue.map(_.toString == "body")) getOrElse false) &&
    (request.actualYamlLocation.get.keyValue.get.yPart.asInstanceOf[YScalar].text == "body")
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    var responseKind: LocationKind = LocationKind.KEY_COMPLETION
    val result = request.astNode match {
      case Some(_n) =>
        var n = _n

        if (isMethodKey(request)) {
          n.parent match {
            case Some(parent) =>
              if (parent.definition.isAssignableFrom("Method")) {
                parent.parent match {
                  case Some(resource) =>
                    if (resource.definition.isAssignableFrom("ResourceBase")) {
                      n = resource
                    }
                  case _ =>
                }
              }

            case _ =>
          }
        }

        if (n.isAttr) {
          if (n.parent.isDefined && n.property.isDefined) {
            val property = n.property.get
            val parent   = n.parent.get
            if (property.nameId.contains("type") && parent.definition.isAssignableFrom("TypeDeclaration")) {
              if (n.sourceInfo.yamlSources.headOption.contains(n.parent.get.sourceInfo.yamlSources.head)) {
                n = _n.parent.get
              }
            } else if (!property.isMultiValue && n.sourceInfo.yamlSources.nonEmpty) {
              val isInKey =
                YamlLocation(n.sourceInfo.yamlSources.head, n.astUnit.positionsMapper).inKey(request.position)
              if (isInKey) {
                n = _n.parent.get
              }
            }
          }
        }
        val isInKey = YamlLocation(n.sourceInfo.yamlSources.head, n.astUnit.positionsMapper).inKey(request.position)
        if (isInKey) {
          n = _n.parent.get
        }
        val isYAML = request.config.astProvider.map(_.syntax).contains(Syntax.YAML)

        if (isRequestParameterName(request)) {
          responseKind = LocationKind.VALUE_COMPLETION

          extractPathItemObject(request) match {
            case Some(pathItemObject) =>
              pathItemObject.attribute("path") match {
                case Some(pathAttr) =>
                  pathAttr.value match {
                    case Some(pathValue: String) =>
                      new Regex("[\\?&](\\w+\\d*)=###")
                        .findAllIn(pathValue)
                        .matchData
                        .map(item => item.group(1))
                        .map(name => Suggestion(name, "query parameter", name, request.prefix))
                        .toSeq

                    case _ => Seq()
                  }

                case _ => Seq()
              }

            case _ => Seq()
          }
        } else if (isSecurityReference(request)) {
          var nameList: ListBuffer[String] = ListBuffer()

          request.astNode.get.astUnit.rootNode
            .elements("securityDefinitions")
            .foreach(item => {
              item.attribute("name") match {
                case Some(nameAttr) =>
                  nameAttr.value match {
                    case Some(nameValue: String) => nameList += nameValue.toString

                    case _ =>
                  }

                case _ =>
              }
            })

          nameList.map(name => Suggestion(name, "security definition reference", name, request.prefix))
        } else if (isDefinitionRequired(request)) {
          var nameList: ListBuffer[String] = ListBuffer()

          request.astNode.get.asElement.get
            .elements("properties")
            .foreach {
              case propsNode: IHighLevelNode => {
                propsNode
                  .attributes("name")
                  .map(attr => attr.value)
                  .filter(item =>
                    item match {
                      case Some(_) => true

                      case _ => false
                  })
                  .foreach(item => item.foreach(nameList += _.toString))
              }

              case _ => Seq()
            }

          responseKind = LocationKind.VALUE_COMPLETION

          nameList.map(name => Suggestion(name, "required property", name, request.prefix))
        } else if (isContentType(request)) {
          contentTypes(request).map(value => Suggestion(value, id, value, request.prefix))
        } else if (isDiscriminatorValue(request)) {
          var a           = extractFirstLevelScalars(request)
          val description = "Possible discriminating property"
          responseKind = LocationKind.VALUE_COMPLETION
          extractFirstLevelScalars(request).map(name => Suggestion(name, description, name, request.prefix))
        } else if (n.isElement) {
          val element = n.asElement.get

          extractSuggestableProperties(element).map(prop => {
            val pName       = prop.nameId.get
            val description = prop.getExtra(DescriptionExtra).map(_.text).getOrElse("")
            val text        = if (isYAML && pName.startsWith("$")) s""""$pName"""" else pName
            val suggestion  = Suggestion(text, description, pName, request.prefix)
            ProjectBuilder
              .determineFormat(element.astUnit.baseUnit)
              .flatMap(SuggestionCategoryRegistry.getCategory(_, pName, Option(element.definition), prop.range))
              .foreach(suggestion.withCategory)
            val needBrake =
              !(pName == "items" && element.definition.universe.name.exists(s => s.contains("RAML"))) &&
                !(pName == "schemes" && element.definition.universe.name.exists(s => s.contains("OAS"))) &&
                pName != "enum" && pName != "mediaType" && pName != "securedBy" && pName != "protocols" &&
                prop.range
                  .exists(r => !r.isValueType && (!r.isAssignableFrom("Reference") || prop.isMultiValue))

            if (needBrake) {
              val off = element.sourceInfo.valueOffset.getOrElse(0) + 2
              val ws  = "\n" + " " * off
              suggestion.withTrailingWhitespace(ws)
            }
            suggestion
          })
        } else {
          Seq()
        }

      case _ => Seq()
    }
    val response = CompletionResponse(result, responseKind, request)
    Promise.successful(response).future
  }

  def extractPathItemObject(request: ICompletionRequest): Option[IHighLevelNode] =
    request.astNode
      .flatMap(_.parent)
      .flatMap(_.parent)
      .flatMap(_.parent)
      .filter(_.definition.isAssignableFrom("PathItemObject"))

  def extractFirstLevelScalars(request: ICompletionRequest): Seq[String] =
    request.astNode
      .flatMap(_.parent)
      .map(
        _.elements("properties")
          .filter(f =>
            Seq("StringTypeDeclaration", "NumberTypeDeclaration", "BooleanTypeDeclaration", "ArrayTypeDeclaration")
              .exists(f.definition.isAssignableFrom(_)))
          .map(p => p.definition.nameId.get))
      .getOrElse(Nil)

  def getBody(request: ICompletionRequest): Option[IParseResult] = {
    //TODO: implement when node searching will be fixed.
    Option.empty
  }

  def extractSuggestableProperties(node: IHighLevelNode): Seq[IProperty] = {
    val existingProperties: mutable.Map[String, IProperty] = mutable.Map()

    val isTypeDeclaration = node.definition.isAssignableFrom("TypeDeclaration")

    node.children.foreach(x => {
      if (x.sourceInfo.yamlSources.nonEmpty) x.property match {
        case Some(p) =>
          p.nameId match {
            case Some(n) =>
              var append = true
              if (n == "required" && p.domain.flatMap(_.nameId).contains("TypeDeclaration"))
                append = x.sourceInfo.yamlSources.nonEmpty
              if (append) existingProperties(n) = p

            case _ =>
          }

        case _ =>
      }
    })

    if (node.sourceInfo.yamlSources.length > 0) node.sourceInfo.yamlSources.head match {
      case mapEntry: YMapEntry =>
        mapEntry.value match {
          case ynodeValue: YNode =>
            ynodeValue.value match {
              case value: YMap =>
                value.entries.foreach(entry =>
                  entry.key.value match {
                    case scalar: YScalar =>
                      val name = scalar.text

                      if (!existingProperties.keySet.contains(name))
                        node.definition.allProperties.find(prop => prop.nameId.get.equals(name)) match {
                          case Some(property) =>
                            existingProperties(name) = property

                          case _ =>
                        }

                    case _ =>
                })

              case _ =>
            }

          case _ =>
        }
      case value: YMap =>
        value.entries.foreach(entry =>
          entry.key.value match {
            case scalar: YScalar =>
              val name = scalar.text

              if (!existingProperties.keySet.contains(name))
                node.definition.allProperties.find(prop => prop.nameId.get.equals(name)) match {
                  case Some(property) =>
                    existingProperties(name) = property

                  case _ =>
                }

            case _ =>
        })
      case _ =>
    }

    if (isTypeDeclaration) {
      if (existingProperties.keySet.contains("type")) {
        existingProperties("schema") = node.definition.property("schema").get
      } else if (existingProperties.keySet.contains("schema")) {
        existingProperties("type") = node.definition.property("type").get
      }
    }

    val definition                    = node.definition
    val propName                      = node.property.flatMap(_.nameId)
    var result: ListBuffer[IProperty] = ListBuffer()
    result ++= definition.allProperties
      .filter(p =>
        p.nameId match {
          case Some(n) =>
            if (!existingProperties.contains(n)) {
              val e = p.getExtra(PropertySyntaxExtra).getOrElse(PropertySyntaxExtra.empty)
              if (p.domain.exists(_.isUserDefined)) {
                false
              } else if (!e.allowsParentProperty(propName)) {
                false
              } else if (e.isKey || e.isValue || e.isHiddenFromUI) {
                false
              } else if (p.isMultiValue) {
                val isPrimitive = p.range match {
                  case Some(t) =>
                    t match {
                      case arr: IArrayType =>
                        arr.componentType match {
                          case Some(ct) => ct.isValueType
                          case _        => false
                        }
                      case _ => false
                    }
                  case _ => false
                }
                (isPrimitive || e.isEmbeddedInArray || e.isEmbeddedInMaps)
              } else true
            } else false
          case _ => false
      })
    if (definition.isAssignableFrom("TypeDeclaration")) {
      var typeIsSet = false
      node.sourceInfo.yamlSources.foreach(ys => {
        YamlLocation(ys, node.astUnit.positionsMapper).value.foreach(f = v => {
          v.yPart match {
            case map: YMap =>
              map.entries.find(e => {
                e.key.value match {
                  case sc: YScalar =>
                    sc.value match {
                      case "type" => e.value != null
                      case _      => false
                    }
                  case _ => false
                }
              }) match {
                case Some(_) => typeIsSet = true
                case _       =>
              }
            case _ =>
          }
        })
      })
      if (!typeIsSet) {
        definition.universe
          .`type`("ObjectTypeDeclaration")
          .flatMap(_.property("properties"))
          .foreach(p => result += p)

        definition.universe
          .`type`("ArrayTypeDeclaration")
          .flatMap(_.property("items"))
          .foreach(p => result += p)

      }
    }
    result
  }
}

object StructureCompletionPlugin {
  val ID = "structure.completion"

  val supportedLanguages: List[Vendor] = List(Raml08, Raml10, Oas, Oas20, Aml)

  def apply(): StructureCompletionPlugin = new StructureCompletionPlugin()
}
