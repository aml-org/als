package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.DomainElement
import amf.core.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import amf.core.annotations.ErrorDeclaration
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{ArrayRange, ObjectRange, RawSuggestion, StringScalarRange}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.yaml.model.{YMapEntry, YNode}

import scala.concurrent.Future

trait RamlAbstractDeclarationReference extends AMLCompletionPlugin {

  protected val elementClass: Class[_ <: DomainElement]
  protected val abstractDeclarationClass: Class[_ <: ParametrizedDeclaration]
  protected val errorDeclarationClass: Class[_ <: ErrorDeclaration]

  protected def entryKey: String

  protected def iriDeclaration: String

  protected def isArray(yPartBranch: YPartBranch) = false

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if ((elementClass.isInstance(params.amfObject)
          || abstractDeclarationClass.isInstance(params.amfObject)
          || errorDeclarationClass.isInstance(params.amfObject))
          && isTypeDef(params.yPartBranch)) {

        val siblings = getSiblings(params)

        val suggestions =
          new AMLRamlStyleDeclarationsReferences(Seq(iriDeclaration),
                                                 stringValue(params.yPartBranch),
                                                 params.declarationProvider,
                                                 None).resolve().filter(r => !siblings.contains(r.newText))
        suggestions.map {
          s =>
            val vars = extractChildren(params, s)
            if (params.yPartBranch.isKey)
              s.copy(options = s.options.copy(isKey = true,
                                              rangeKind =
                                                if (isArray(params.yPartBranch)) ArrayRange else ObjectRange),
                     children = vars)
            else
              s.copy(
                children = vars,
                options = s.options.copy(isKey = vars.nonEmpty,
                                         rangeKind =
                                           if (isArray(params.yPartBranch)) ArrayRange
                                           else if (vars.nonEmpty) ObjectRange
                                           else StringScalarRange)
              )
        }
      } else Nil)

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

  private def isTypeDef(yPartBranch: YPartBranch) =
    isValueInType(yPartBranch) || isKeyInTypeMap(yPartBranch)

  protected def isValue(yPartBranch: YPartBranch): Boolean

  /**
    * /endpoint:
    * type: * || type: res*
    * case type 1 is object endpoint other cases are parametrized declaration parser
    */
  private def isValueInType(yPartBranch: YPartBranch) =
    isValue(yPartBranch) && yPartBranch.parentEntryIs(entryKey)

  /**
    * /endpoint:
    * type:
    * res*
    */
  private def isKeyInTypeMap(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKey && yPartBranch
      .ancestorOf(classOf[YMapEntry])
      .exists(_.key.asScalar.exists(_.text == entryKey))

  private def stringValue(yPart: YPartBranch) = {
    yPart.node match {
      case n: YNode => n.asScalar.map(_.text).getOrElse("")
      case _        => ""
    }
  }
}
