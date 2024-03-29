package org.mulesoft.als.suggestions.interfaces

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.scala.model.document.Fragment
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.amfintegration.AmfImplicits.{AlsLexicalInformation, AmfObjectImp}

import scala.concurrent.Future

trait AMLCompletionPlugin extends CompletionPlugin[AmlCompletionRequest] with AmfObjectKnowledge {
  protected def emptySuggestion: Future[Seq[RawSuggestion]] = Future.successful(Seq())
}

trait AmfObjectKnowledge {
  protected def isEncodes(amfObject: AmfObject, dialect: Dialect, branchStack: Seq[AmfObject]): Boolean = {
    val iri = amfObject.metaURIs.head

    dialect.declares
      .find(nm => dialect.documents().root().encoded().option().contains(nm.id))
      .collectFirst({ case d: NodeMapping if d.nodetypeMapping.option().contains(iri) => d })
      .isDefined || hasFragmentParent(branchStack)
  }

  protected def isInFieldValue(params: AmlCompletionRequest): Boolean =
    params.fieldEntry
      .exists(
        _.value.value
          .position()
          .exists(li => li.contains(params.position.toAmfPosition))
      )

  // check if this is still necessary once the fix on fragment lexicals  is done W-11338054
  private def hasFragmentParent(branchStack: Seq[AmfObject]): Boolean =
    branchStack.headOption.exists(b => b.isInstanceOf[Fragment])
}
