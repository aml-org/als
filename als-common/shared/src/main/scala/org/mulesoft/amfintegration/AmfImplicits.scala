package org.mulesoft.amfmanager

import amf.core.annotations.{LexicalInformation, ReferenceTargets, SynthesizedField}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser
import amf.core.parser.{Annotations, Value}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, NodeMapping}
import amf.plugins.domain.webapi.metamodel.AbstractModel

import scala.collection.mutable

object AmfImplicits {

  implicit class AmfAnnotationsImp(ann: Annotations) {
    def range(): Option[parser.Range] = ann.find(classOf[LexicalInformation]).map(_.range)

    def isSynthesized: Boolean = ann.contains(classOf[SynthesizedField])

    def targets(): Map[String, parser.Range] = ann.find(classOf[ReferenceTargets]).map(_.targets).getOrElse(Map.empty)
  }

  implicit class AmfObjectImp(amfObject: AmfObject) {
    def metaURIs: List[String] = amfObject.meta.`type` match {
      case head :: tail if isAbstract => (head.iri() + "Abstract") +: (tail.map(_.iri()))
      case l                          => l.map(_.iri())
    }

    lazy val isAbstract: Boolean = amfObject.fields
      .getValueAsOption(AbstractModel.IsAbstract)
      .collect({
        case Value(scalar: AmfScalar, _) => scalar
      })
      .exists(_.toBool)
  }

  implicit class BaseUnitImp(bu: BaseUnit) {
    def flatRefs: Seq[BaseUnit] = {
      val set: mutable.Set[BaseUnit] = mutable.Set.empty

      def innerRefs(refs: Seq[BaseUnit]): Unit =
        refs.foreach { bu =>
          if (set.add(bu)) innerRefs(bu.references)
        }

      innerRefs(bu.references)
      set.toSeq
    }

    def identifier: String = bu.location().getOrElse(bu.id)
  }

  implicit class DialectImplicits(d: Dialect) extends BaseUnitImp(d) {
    def declarationsMapTerms: Map[String, String] = {
      d.documents()
        .root()
        .declaredNodes()
        .flatMap { pnm =>
          d.declares
            .find(_.id == pnm.mappedNode().value())
            .collect({ case nm: NodeMapping => nm })
            .map { declared =>
              declared.nodetypeMapping.value() -> pnm.name().value()
            }
        }
        .toMap
    }
  }
}
