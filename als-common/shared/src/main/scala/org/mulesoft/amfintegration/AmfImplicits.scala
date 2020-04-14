package org.mulesoft.amfmanager

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.Value
import amf.plugins.domain.webapi.metamodel.AbstractModel

import scala.collection.mutable

object AmfImplicits {

  implicit class AmfObjectImp(amfObject: AmfObject) {
    def metaURIs: List[String] = amfObject.meta.`type` match {
      case head :: tail if isAbstract => (amfObject.abstractURI) +: (tail.map(_.iri()))
      case l                          => l.map(_.iri())
    }

    def abstractURI: String = {
      amfObject.meta.`type`.head.iri() + "Abstract"
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

}
