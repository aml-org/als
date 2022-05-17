package org.mulesoft.als.common

import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

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

    def containsYPart(yPartBranch: YPartBranch): Boolean =
      amfElement match {
        case AmfArray(values, annotations) =>
          annotations
            .containsYPart(yPartBranch)
            .getOrElse(
              annotations.isVirtual &&
                values.exists(_.containsYPart(yPartBranch))
            )
        case amfObject: AmfObject =>
          (amfObject.annotations.containsYPart(yPartBranch).getOrElse(false) ||
            amfObject.annotations.containsJsonSchemaPosition(yPartBranch).getOrElse(false)) ||
          // look inside if some value is part of the branch
          amfObject.fields.fields().exists { fe =>
            fe.value.annotations
              .containsYPart(yPartBranch)
              .getOrElse(
                fe.value.value.containsYPart(yPartBranch)
              ) // todo: this should work to cut early, but there are cases in which a son is not contained in
          }
        case AmfScalar(_, annotations) => annotations.containsYPart(yPartBranch).getOrElse(false)
        case _                         => false
      }

  }
}
