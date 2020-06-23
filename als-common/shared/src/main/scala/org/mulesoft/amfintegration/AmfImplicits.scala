package org.mulesoft.amfintegration

import amf.core.annotations.{LexicalInformation, ReferenceTargets, SynthesizedField}
import amf.core.metamodel.Field
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement}
import amf.core.parser
import amf.core.parser.{Annotations, Value}
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, NodeMapping, PropertyTerm}
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

  implicit class DomainElementImp(d: DomainElement) extends AmfObjectImp(d) {
    def getLiteralProperty(f: Field): Option[Any] =
      d.fields.getValueAsOption(f).collect({ case Value(v: AmfScalar, _) => v.value })
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
    def vocabulary(base: String): Option[Vocabulary] = {
      d.references.collectFirst { case v: Vocabulary if v.base.option().contains(base) => v }
    }
  }

  implicit class VocabularyImplicit(v: Vocabulary) extends BaseUnitImp(v) {
    val properties: Seq[PropertyTerm] = v.declares.collect { case p: PropertyTerm => p }
    val classes: Seq[ClassTerm]       = v.declares.collect { case c: ClassTerm    => c }

    def getPropertyTerm(n: String): Option[PropertyTerm] = v.properties.find(p => p.name.option().contains(n))

    def getClassTerm(n: String): Option[ClassTerm] = v.classes.find(p => p.name.option().contains(n))
  }
}
