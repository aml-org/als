package org.mulesoft.als.suggestions.interfaces

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

import scala.concurrent.Future

trait AMLCompletionPlugin extends CompletionPlugin[AmlCompletionRequest] {
  protected def emptySuggestion: Future[Seq[RawSuggestion]] = Future.successful(Seq())

  protected def isEncodes(amfObject: AmfObject, dialect: Dialect): Boolean = {
    val iri = amfObject.meta.`type`.head.iri()

    dialect.declares
      .find(nm => dialect.documents().root().encoded().option().contains(nm.id))
      .collectFirst({ case d: NodeMapping if d.nodetypeMapping.option().contains(iri) => d })
      .isDefined
  }

  protected def isInFieldValue(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry
      .exists(
        _.value.value
          .position()
          .exists(li => li.contains(params.position.toAmfPosition)))
  }
}
