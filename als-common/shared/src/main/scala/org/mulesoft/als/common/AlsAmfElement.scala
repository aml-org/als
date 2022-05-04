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

    def containsYPart(astBranch: ASTPartBranch): Boolean =
      amfElement match {
        case AmfArray(values, annotations) =>
          annotations
            .containsAstBranch(astBranch)
            .getOrElse(
              annotations.isVirtual &&
                values.exists(_.containsYPart(astBranch))
            )
        case amfObject: AmfObject =>
          (amfObject.annotations.containsAstBranch(astBranch).getOrElse(false) ||
            amfObject.annotations.containsJsonSchemaPosition(astBranch).getOrElse(false)) ||
          // look inside if some value is part of the branch
          amfObject.fields.fields().exists { fe =>
            fe.value.annotations
              .containsAstBranch(astBranch)
              .getOrElse(
                fe.value.value.containsYPart(astBranch)
              ) // todo: this should work to cut early, but there are cases in which a son is not contained in
          }
        case AmfScalar(_, annotations) => annotations.containsAstBranch(astBranch).getOrElse(false)
        case _                         => false
      }

  }
}
