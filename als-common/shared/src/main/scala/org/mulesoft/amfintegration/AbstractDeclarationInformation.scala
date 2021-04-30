package org.mulesoft.amfintegration

import amf.RamlProfile
import amf.core.annotations.SourceAST
import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.model.domain.DomainElement
import amf.core.model.domain.templates.AbstractDeclaration
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import amf.plugins.domain.webapi.resolution.ExtendsHelper
import org.mulesoft.amfintegration.AmfImplicits._
import org.yaml.model.{YMap, YMapEntry, YNode}

object AbstractDeclarationInformation {

  def extractInformation(declaration: Option[AbstractDeclaration], bu: BaseUnit): Option[ElementInfo] = {
    declaration match {
      case Some(value) => extractInformation(value, bu)
      case _           => None
    }

  }

  def extractInformation(declaration: AbstractDeclaration, bu: BaseUnit): Option[ElementInfo] = {
    getTarget(declaration) match {
      case r: ResourceType =>
        val resolved =
          getSourceEntry(r, "resourceType").fold(r.asEndpoint(bu, errorHandler = LocalIgnoreErrorHandler))(e => {
            r.entryAsEndpoint(bu,
                              node = r.dataNode,
                              entry = e,
                              errorHandler = LocalIgnoreErrorHandler,
                              annotations = r.annotations)
          })
        Some(ElementInfo(resolved, r, r.name.value(), r.metaURIs.head))

      case t: Trait =>
        val resolved =
          getSourceEntry(t, "trait").fold(t.asOperation(bu))(e => {
            val extendsHelper = ExtendsHelper(RamlProfile, keepEditingInfo = false, UnhandledErrorHandler)
            extendsHelper.parseOperation(bu, t.name.option().getOrElse(""), "AbstractDeclarationInformation", e)
          })
        Some(ElementInfo(resolved, t, t.name.value(), t.metaURIs.head))
      case _ => None
    }
  }

  private def getSourceEntry(a: AbstractDeclaration, defaultName: String) =
    a.annotations.find(classOf[SourceAST]).map(_.ast) match {
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
