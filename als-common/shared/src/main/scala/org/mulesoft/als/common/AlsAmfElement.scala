package org.mulesoft.als.common

import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar}

object AlsAmfElement {

  implicit class AmfElementWrapper(amfElement: AmfElement) {
    private val (scalar, obj, array) = amfElement match {
      case s: AmfScalar => (Some(s), None, None)
      case o: AmfObject => (None, Some(o), None)
      case a: AmfArray  => (None, None, Some(a))
      case _            => (None, None, None)
    }

    def toScalar: Option[AmfScalar] = scalar

    def toObject: Option[AmfObject] = obj

    def toArray: Option[AmfArray] = array
  }
}
