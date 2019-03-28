package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.Search
import org.mulesoft.high.level.implementation.ASTNodeImpl
import org.mulesoft.high.level.interfaces.IParseResult
import org.yaml.model.{YMapEntry, YNode, YScalar}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

class TypeReferencesCompletionPlugin extends ICompletionPlugin {
  override def id: String = TypeReferencesCompletionPlugin.ID

  override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages

  override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {
    case Some(astProvider) =>
      languages.indexOf(astProvider.language) >= 0 && request.astNode.isDefined && request.astNode.get != null &&
        isInTypeTypeProperty(request) &&
        !IncludeCompletionPlugin
          .apply()
          .isApplicable(request)

    case _ => false
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    val result   = TypeReferencesCompletionPlugin.typeSuggestions(request, id)
    val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }

  def isInTypeTypeProperty(request: ICompletionRequest): Boolean = {
    val node = request.astNode.get
    if (node.isElement) elementIsPropertyType(node)
    else
      node.property.exists(_.nameId.exists(_ == "type")) && node.property.exists(
        _.domain.exists(_.nameId.exists(_ == "TypeDeclaration")))
  }

  private def elementIsPropertyType(node: IParseResult) = {
    node.sourceInfo.yamlSources.headOption
      .filter(_.isInstanceOf[YMapEntry])
      .map(_.asInstanceOf[YMapEntry])
      .exists(p = me => {
        val value       = me.value
        val valueScalar = value.value

        val valueTag = value match {
          case node: YNode.MutRef => Option(node.origTag)
          case node: YNode        => Option(node.tag)
          case _                  => None
        }
        node.property match {
          case Some(prop) if prop.nameId.contains("methods") => false
          case _                                             => (valueTag.exists(_.text != "!include")) && valueScalar.isInstanceOf[YScalar]
        }
      })
  }
}

object TypeReferencesCompletionPlugin {
  val ID = "typeRef.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): TypeReferencesCompletionPlugin = new TypeReferencesCompletionPlugin()

  def typeSuggestions(request: ICompletionRequest, id: String): Seq[Suggestion] = {
    val node     = request.astNode.get
    val element  = if (node.isElement) node.asElement.get else node.parent.get
    val typeName = element.attribute("name").flatMap(_.value).map(_.toString).getOrElse("")
    val builtIns = node.astUnit.rootNode.definition.universe.builtInNames() :+ "object"
    val result = ListBuffer[Suggestion]() ++= builtIns
      .filter(_.startsWith(request.prefix))
      .map(name => Suggestion(name, "Builtin type", name, request.prefix))
    request.astNode
      .map(_.astUnit)
      .foreach(u => {
        Search
          .getDeclarations(u, "TypeDeclaration")
          .foreach(d => {
            d.node
              .attribute("name")
              .flatMap(_.value)
              .filter(_ != typeName)
              .foreach(name => {
                var proposal: String = name.toString
                d.namespace.foreach(ns => proposal = s"$ns.$proposal")
                if (proposal.startsWith(request.prefix))
                  result += Suggestion(proposal, "User type", proposal, request.prefix)
              })
          })
      })
    result
  }
}
