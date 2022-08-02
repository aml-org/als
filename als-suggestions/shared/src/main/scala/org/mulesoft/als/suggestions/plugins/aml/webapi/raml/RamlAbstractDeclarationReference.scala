package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import amf.core.internal.annotations.ErrorDeclaration
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.plugins.aml.templates.TemplateTools
import org.mulesoft.als.suggestions.{ObjectRange, RawSuggestion}
import org.yaml.model.YNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RamlAbstractDeclarationReference extends AMLCompletionPlugin {

  protected val elementClass: Class[_ <: DomainElement]
  protected val abstractDeclarationClass: Class[_ <: ParametrizedDeclaration]
  protected val errorDeclarationClass: Class[_ <: ErrorDeclaration[_]]

  protected def entryKey: String

  protected def iriDeclaration: String

  protected def isArray(yPartBranch: YPartBranch) = false

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (
        (elementClass.isInstance(params.amfObject)
          || abstractDeclarationClass.isInstance(params.amfObject)
          || errorDeclarationClass.isInstance(params.amfObject))
        && isTypeDef(params.astPartBranch)
      ) {

        val siblings = getSiblings(params)

        val suggestions =
          new AMLRamlStyleDeclarationsReferences(
            Seq(iriDeclaration),
            stringValue(params.astPartBranch),
            params.declarationProvider,
            None
          ).resolve().filter(r => !siblings.contains(r.newText))
        suggestions.flatMap { s =>
          val vars = extractChildren(params, s)
          if (vars.nonEmpty) {
            if (
              params.configurationReader.getTemplateType != TemplateTypes.NONE &&
              canTemplate(params.astPartBranch)
            )
              Some(
                s.copy(
                  options = s.options.copy(isKey = true, rangeKind = ObjectRange),
                  children = vars,
                  displayText = s"${TemplateTools.defaultPrefix} ${s.displayText}",
                  category = TemplateTools.category
                )
              )
            else None
          } else Some(s)
        }
      } else Nil
    }

  }

  private def extractChildren(params: AmlCompletionRequest, s: RawSuggestion): Seq[RawSuggestion] = {
    val maybeElement: Option[DomainElement] =
      params.declarationProvider.findElement(s.newText, iriDeclaration)
    val vars = maybeElement
      .collect({ case p: AbstractDeclaration => p })
      .map(_.variables.flatMap(_.option()))
      .getOrElse(Nil)
    vars.map(RawSuggestion.forKey(_, mandatory = true))
  }

  private def getSiblings(params: AmlCompletionRequest): Seq[String] = {
    val element =
      if (elementClass.isInstance(params.amfObject))
        Some(params.amfObject.asInstanceOf[DomainElement])
      else if (abstractDeclarationClass.isInstance(params.amfObject))
        params.branchStack.headOption.collectFirst({
          case e if elementClass.isInstance(e) => e.asInstanceOf[DomainElement]
        })
      else None

    element.map(_.extend).getOrElse(Nil).collect({ case pm: ParametrizedDeclaration => pm }).flatMap(_.name.option())
  }

  private def isTypeDef(astPart: ASTPartBranch) =
    isValueInType(astPart) || isKeyInTypeMap(astPart)

  protected def isValue(astPartBranch: ASTPartBranch): Boolean

  /** /endpoint: type: * || type: res* case type 1 is object endpoint other cases are parametrized declaration parser
    */
  private def isValueInType(astPartBranch: ASTPartBranch) =
    isValue(astPartBranch) && astPartBranch.parentEntryIs(entryKey)

  private def canTemplate(astPartBranch: ASTPartBranch) = astPartBranch.isKey || astPartBranch.isInArray

  /** /endpoint: type: res*
    */
  private def isKeyInTypeMap(astPart: ASTPartBranch): Boolean =
    astPart.isKey && astPart.parentKey.contains(entryKey)

  private def stringValue(astPart: ASTPartBranch) = {
    astPart.node match {
      case n: YNode => n.asScalar.map(_.text).getOrElse("")
      case _        => ""
    }
  }
}
