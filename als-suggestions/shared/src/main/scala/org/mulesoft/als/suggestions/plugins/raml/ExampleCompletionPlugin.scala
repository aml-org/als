package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.positioning.{PositionsMapper, YamlLocation, YamlSearch}
import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition
import org.yaml.model._
import org.yaml.parser.YamlParser

import scala.concurrent.{Future, Promise}

class ExampleCompletionPlugin extends ICompletionPlugin with ExampleCompletionTools {
  override def id: String = ExampleCompletionPlugin.ID

  override def languages: Seq[Vendor] =
    FacetsCompletionPlugin.supportedLanguages

  private def positionMarker = "$$$positionMarker$$$"

  override def isApplicable(request: ICompletionRequest): Boolean =
    request.config.astProvider match {
      case Some(astProvider) =>
        languages.indexOf(astProvider.language) >= 0 && isFullRequest(request) && isExample(request) && isInKey(
          request);

      case _ => false;
    }

  def isFullRequest(request: ICompletionRequest): Boolean = {
    request.astNode.isDefined && request.astNode.get != null && request.astNode.get.property.isDefined && request.astNode.get.property.get != null;
  }

  def isInKey(request: ICompletionRequest): Boolean =
    if (request.kind == LocationKind.KEY_COMPLETION && extractScalarNode(request).isEmpty) {
      true;
    } else
      extractParsedScalar(request) match {
        case Some(node) => {
          findLocationInParsedScalar(request, node) match {
            case Some(location) => location.keyValue.nonEmpty;

            case _ => false
          }
        }

        case _ => false;
      }

  def findIndent(scalarNode: YScalar): Int = scalarNode.children match {
    case children if children.nonEmpty =>
      children(0) match {
        case child: YNonContent =>
          child.tokens
            .filter(_.tokenType.name == "Indent")
            .map(_.text.length)
            .min;

        case _ => -1;
      }

    case _ => -1;
  }

  def findFirstIndent(scalarNode: YScalar): Int = scalarNode.children match {
    case children if children.nonEmpty =>
      children(0) match {
        case child: YNonContent =>
          child.tokens
            .filter(_.tokenType.name == "Indent")
            .reverse
            .last
            .text
            .length;

        case _ => -1;
      }

    case _ => -1;
  }

  def findLocationInParsedScalar(request: ICompletionRequest, parsedScalar: YNode): Option[YamlLocation] =
    extractScalarNode(request) match {
      case Some(node) =>
        Some(
          YamlSearch.getLocation(relativePosition(request),
                                 parsedScalar,
                                 PositionsMapper("").withText(node.text),
                                 List(),
                                 true));

      case _ => Option.empty;
    }

  def relativePosition(request: ICompletionRequest): Int = {
    var scalarPosition = request.yamlLocation.get.value.get.range.start.position;

    var text = getContent(request);

    var leftPart = text.substring(scalarPosition).trim;

    var actualLeftPart = leftPart;

    if (actualLeftPart.startsWith("|") && actualLeftPart.length > 1) {
      actualLeftPart = actualLeftPart.substring(1).trim
    }

    var valueShift = leftPart.length - actualLeftPart.length;

    var indentedPosition = request.position - scalarPosition - valueShift;

    var indent = findIndent(extractScalarNode(request).get);

    var firstIndent = findFirstIndent(extractScalarNode(request).get);

    actualLeftPart = 0
      .to(firstIndent - 1)
      .map(_ => " ")
      .mkString + actualLeftPart;

    indentedPosition = indentedPosition + firstIndent;

    actualLeftPart = actualLeftPart.substring(0, indentedPosition) + positionMarker + actualLeftPart
      .substring(indentedPosition);

    var notIndentedLeftPart =
      actualLeftPart.lines.map(_.drop(firstIndent) + "\n").mkString

    notIndentedLeftPart.indexOf(positionMarker);
  }

  def getContent(request: ICompletionRequest): String = {
    request.config.editorStateProvider match {
      case Some(provider) => provider.getText;

      case _ => ""
    }
  }

  def extractScalarNode(request: ICompletionRequest): Option[YScalar] = {
    request.yamlLocation match {
      case Some(location) =>
        location.value match {
          case Some(value) =>
            value.yPart match {
              case scalar: YScalar => Some(scalar);

              case _ => Option.empty;
            }

          case _ => Option.empty;
        }

      case _ => Option.empty;
    }
  }

  def extractParsedScalar(request: ICompletionRequest): Option[YNode] =
    extractScalarNode(request) match {
      case Some(scalar: YScalar) =>
        YamlParser(scalar.text) match {
          case parser =>
            parser.parse(true) match {
              case documents if documents.nonEmpty =>
                documents.last match {
                  case document: YDocument => Some(document.node);

                  case _ => Option.empty;
                }

              case _ => Option.empty;
            }

          case _ => Option.empty;
        }

      case _ => Option.empty;
    }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    val suggestions = findProperties(extractAstPath(request), extractLocalType(request), request)
    var response =
      CompletionResponse(suggestions, LocationKind.KEY_COMPLETION, request)
    Promise.successful(response).future
  }

  def findProperties(path: Seq[String],
                     localType: Option[ITypeDefinition],
                     request: ICompletionRequest): Seq[Suggestion] =
    if (localType.isEmpty) {
      Seq();
    } else if (path.isEmpty) {
      var siblingNames = extractSiblingNames(request, extractAstPath(request));

      localType.get.allProperties
        .filter(item => !siblingNames.contains(item.nameId.get))
        .map(prop => {
          var propertyName = prop.nameId.get
          var ws: String   = ""
          if (prop.range.exists(!_.isValueType)) {
            request.astNode
              .map(_.astUnit.positionsMapper)
              .foreach(pm => {
                val point = pm.point(request.position)
                val line  = pm.line(point.line).getOrElse("")
                ws = "\n" + " " * pm.lineOffset(line) + "  "
              })
          }

          Suggestion(propertyName, s"'$propertyName' property", propertyName, request.prefix)
            .withTrailingWhitespace(ws)
        });
    } else
      localType.get.property(path.last) match {
        case Some(property) =>
          findProperties(path.dropRight(1), property.range, request);

        case _ => Seq();
      }

  def extractSiblingNames(request: ICompletionRequest, path: Seq[String]): Seq[String] = {
    request.astNode match {
      case Some(requestNode) =>
        requestNode.parent match {
          case Some(parent) =>
            if (parent.sourceInfo.yamlSources.length > 0) {
              parent.sourceInfo.yamlSources.head match {
                case entry: YMapEntry =>
                  yNodeByPath(entry.value, path) match {
                    case Some(ynodeValue: YNode) =>
                      ynodeValue.value match {
                        case value: YMap =>
                          value.entries
                            .map(entry =>
                              entry.key.value match {
                                case scalar: YScalar => scalar.text

                                case _ => null
                            })
                            .filter(_ != null)

                        case _ => Seq()
                      }

                    case _ => Seq()
                  }
                case _ => Seq()
              }
            } else {
              Seq()
            }

          case _ => Seq()
        }

      case _ => Seq()
    }
  }

  def yNodeByPath(root: YNode, path: Seq[String]): Option[YNode] = {
    if (path.isEmpty) {
      return Some(root);
    }

    var current = path.last;

    root.value match {
      case value: YMap =>
        value.entries.find(entry =>
          entry.key.value match {
            case scalar: YScalar => current.equals(scalar.text);

            case _ => false;
        }) match {
          case Some(entry) =>
            entry.value match {
              case node: YNode => yNodeByPath(node, path.dropRight(1));

              case _ => None;
            };

          case _ => None;
        };

      case _ => None;
    }
  }

  def extractLocalType(request: ICompletionRequest): Option[ITypeDefinition] = {
    var node =
      if (request.astNode.get.isElement) request.astNode
      else request.astNode.get.parent
    node.flatMap(_.parent).flatMap(_.localType)
  }

  def extractExampleNode(request: ICompletionRequest): Option[IHighLevelNode] =
    if (request.astNode.get.isElement) request.astNode.get.asElement
    else request.astNode.get.parent;

  def extractAstPath(request: ICompletionRequest): Seq[String] =
    (extractScalarNode(request) match {
      case Some(node) =>
        findLocationInParsedScalar(request, extractParsedScalar(request).get);

      case _ => request.actualYamlLocation
    }) match {
      case Some(location) =>
        location.parentStack
          .filter(_.keyValue.isDefined)
          .map(_.keyValue.get.yPart)
          .filter(_ match {
            case n: YScalar => true;

            case _ => false
          })
          .map(_.asInstanceOf[YScalar].text)

      case _ => Seq();
    }

}

object ExampleCompletionPlugin {
  val ID = "example.completion";

  val supportedLanguages: List[Vendor] = List(Raml10);

  def apply(): ExampleCompletionPlugin = new ExampleCompletionPlugin();
}
