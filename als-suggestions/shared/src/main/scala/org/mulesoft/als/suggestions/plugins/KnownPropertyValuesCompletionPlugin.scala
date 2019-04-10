package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Aml, Oas, Oas20, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{YMap, YNode, YScalar, YSequence}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set
import scala.concurrent.{Future, Promise}
import amf.core.parser._
class KnownPropertyValuesCompletionPlugin extends ICompletionPlugin {

  override def id: String = KnownPropertyValuesCompletionPlugin.ID

  override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages

  override def isApplicable(request: ICompletionRequest): Boolean = {

    if (request.astNode.isEmpty)
      false
    else {
      val prop = request.astNode.get.property
      if (request.actualYamlLocation.isEmpty)
        false
      else if (request.kind == LocationKind.KEY_COMPLETION
               && !prop.flatMap(_.range).exists(_.isArray))
        false
      else if (request.actualYamlLocation.isEmpty || request.yamlLocation.isEmpty)
        false
      else
        true
    }
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    var prop: Option[IProperty] = None
    val astNode                 = request.astNode.get
    var parentNode              = astNode.asElement
    if (astNode.isAttr) {
      prop = astNode.property
      parentNode = astNode.parent
    } else if (request.actualYamlLocation.get.hasSameValue(request.yamlLocation.get)) {
      if (astNode.isElement) {
        val valueLocation            = request.yamlLocation.get.value.get
        var propName: Option[String] = None
        valueLocation.yPart match {
          case yMap: YMap =>
            yMap.entries
              .find(e =>
                YamlLocation(e, astNode.astUnit.positionsMapper).mapEntry.get.containsPosition(request.position))
              .foreach(e =>
                e.key.value match {
                  case sc: YScalar => propName = Some(sc.value.toString)
                  case _           =>
              })
          case _ =>
        }
        prop = propName.flatMap(pName => astNode.asElement.get.definition.property(pName))
      }
    } else if (astNode.isElement) {
      val definition = astNode.asElement.get.definition
      request.actualYamlLocation
        .flatMap(_.keyValue)
        .map(_.yPart)
        .foreach({
          case sc: YScalar =>
            Option(sc.value).foreach(pName => {
              prop = definition.property(pName.toString)
            })
          case _ =>
        })

    }
    var result: ListBuffer[Suggestion] = ListBuffer()
    prop.foreach(p => {
      var existing: Set[String] = Set()
      if (p.isMultiValue) {
        parentNode.foreach(pn => {
          pn.attributes(p.nameId.get)
            .filter(x => x != astNode)
            .foreach(a => {
              a.value.foreach(av => existing += av.toString)
            })
        })
      }
      var resultText: ListBuffer[String] = ListBuffer()
      if (p.enumOptions.nonEmpty) {
        p.enumOptions.get
          .map(_.toString)
          .filter(!existing.contains(_))
          .foreach(text => {
            resultText += text
            existing += text
          })
      } else {
        p.getExtra(PropertySyntaxExtra)
          .foreach(extra => {
            extra.enum
              .map(_.toString)
              .filter(!existing.contains(_))
              .foreach(text => {
                resultText += text
                existing += text
              })
            extra.oftenValues
              .map(_.toString)
              .filter(!existing.contains(_))
              .foreach(text => {
                resultText += text
                existing += text
              })
          })
      }
      val isSequence  = KnownPropertyValuesCompletionPlugin.isSequence(parentNode.get, prop.get.nameId.get)
      val description = s"Possible '${p.nameId.get}' value"
      if (p.isMultiValue && !isSequence) {
        val withBrackets = resultText.map(x => s"[ $x ]").filter(_.startsWith(request.prefix))
        if (withBrackets.isEmpty)
          result ++= resultText
            .filter(_.startsWith(request.prefix))
            .map(x => Suggestion(x, description, x, request.prefix))
        else
          result ++= withBrackets.map(x => Suggestion(x, description, x, request.prefix))
      } else
        result ++= resultText.map(x => Suggestion(x, description, x, request.prefix))
    })
    val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }
}

object KnownPropertyValuesCompletionPlugin {

  // move to some common object if necessary
  implicit class NodeKeyComparetor(k: YNode) {
    def isThisKey(expected: String): Boolean = k.toOption[YScalar] match {
      case Some(s) => s.text == expected
      case _       => false
    }
  }

  val ID = "known.property.values.completion"

  val supportedLanguages: List[Vendor] = List(Raml10, Oas, Oas20, Aml)

  def apply(): KnownPropertyValuesCompletionPlugin = new KnownPropertyValuesCompletionPlugin()

  def isSequence(n: IHighLevelNode, pName: String): Boolean = {
    var pm = n.astUnit.positionsMapper
    n.sourceInfo.yamlSources.headOption.map(YamlLocation(_, pm)).flatMap(_.value) match {
      case Some(x) =>
        x.yPart match {
          case me: YMap =>
            me.entries.find(x => x.key.isThisKey(pName)) match {
              case Some(me) =>
                me.value.value match {
                  case _: YSequence => true
                  case _            => false
                }
              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }

}
