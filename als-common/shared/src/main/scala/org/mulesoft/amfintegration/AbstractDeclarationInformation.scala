package org.mulesoft.amfintegration

import amf.apicontract.client.scala.AMFConfiguration
import amf.apicontract.client.scala.model.domain.templates.{ResourceType, Trait}
import amf.apicontract.client.scala.transform.AbstractElementTransformer
import amf.apicontract.internal.spec.common.transformation.ExtendsHelper
import amf.apicontract.internal.transformation.BaseUnitSourceLocationIndex
import amf.core.client.scala.errorhandling.UnhandledErrorHandler
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.model.domain.templates.AbstractDeclaration
import amf.core.internal.annotations.{SourceAST, SourceYPart}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.amfconfiguration.ProfileMatcher.profile
import org.yaml.model.{YMap, YMapEntry, YNode}

object AbstractDeclarationInformation {

  def extractInformation(
      declaration: Option[AbstractDeclaration],
      bu: BaseUnit,
      amfConfiguration: AMFConfiguration
  ): Option[ElementInfo] = {
    declaration match {
      case Some(value) => extractInformation(value, bu, amfConfiguration)
      case _           => None
    }

  }

  def extractInformation(
      declaration: AbstractDeclaration,
      bu: BaseUnit,
      amfConfiguration: AMFConfiguration
  ): Option[ElementInfo] = {
    getTarget(declaration) match {
      case r: ResourceType =>
        val resolved =
          getSourceEntry(r, "resourceType").fold(
            AbstractElementTransformer
              .asEndpoint(bu, r, amfConfiguration, errorHandler = LocalIgnoreErrorHandler)
          )(e => {
            AbstractElementTransformer.entryAsEndpoint(bu, r, r.dataNode, e, amfConfiguration, LocalIgnoreErrorHandler)
          })
        Some(ElementInfo(resolved, r, r.name.value(), r.metaURIs.head))

      case t: Trait =>
        val resolved =
          getSourceEntry(t, "trait").fold(AbstractElementTransformer.asOperation(bu, t, amfConfiguration))(e => {
            val extendsHelper =
              ExtendsHelper(
                profile(bu),
                keepEditingInfo = false,
                UnhandledErrorHandler,
                amfConfiguration,
                BaseUnitSourceLocationIndex.build(bu)
              )
            extendsHelper.parseOperation(bu, t.name.option().getOrElse(""), "AbstractDeclarationInformation", e)
          })
        Some(ElementInfo(resolved, t, t.name.value(), t.metaURIs.head))
      case _ => None
    }
  }

  private def getSourceEntry(a: AbstractDeclaration, defaultName: String) =
    a.annotations.find(classOf[SourceYPart]).map(_.ast) match {
      case Some(m: YMap) =>
        Some(YMapEntry(YNode(a.name.option().getOrElse(defaultName)), m))
      case Some(entry: YMapEntry) => Some(entry)
      case _                      => None
    }

  private def getTarget(original: AbstractDeclaration) =
    original.effectiveLinkTarget().asInstanceOf[AbstractDeclaration] match {
      case d if d.name.isNull => d.withName("AbstractDeclaration")
      case other              => other
    }

  case class ElementInfo(element: DomainElement, original: DomainElement, name: String, iri: String)
}
