package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfObject, DomainElement}
import amf.plugins.document.vocabularies.annotations.FromUnionNodeMapping
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.UnionNodeMapping

trait UnionSuggestions {
  protected val amfObject: AmfObject
  protected val dialect: Dialect

  protected def getUnionType: Option[UnionNodeMapping] =
    amfObject.annotations.find(classOf[FromUnionNodeMapping]).flatMap { ann: FromUnionNodeMapping =>
      dialect.declares.collectFirst({
        case d: UnionNodeMapping if d.id == ann.id => d
      })
    }

  protected def getDeclaredDomainElement(id: String): Option[DomainElement] =
    dialect.declares.collectFirst({
      case d if d.id == id => d
    })

}
