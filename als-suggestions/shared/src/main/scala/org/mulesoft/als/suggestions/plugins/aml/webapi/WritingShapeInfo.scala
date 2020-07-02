package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.YPartBranch

trait WritingShapeInfo {
  protected def isWritingFacet(yPartBranch: YPartBranch, shape: Shape, stack: Seq[AmfObject]): Boolean =
    yPartBranch.isKey && !yPartBranch.isKeyDescendantOf("required") && !writingShapeName(shape, yPartBranch) && !writingParamName(
      stack,
      yPartBranch) && !yPartBranch.parentEntryIs("properties")

  protected def writingShapeName(shape: Shape, yPartBranch: YPartBranch) =
    shape.name.value() == yPartBranch.stringValue

  protected def writingParamName(stack: Seq[AmfObject], yPartBranch: YPartBranch) =
    stack.headOption.exists {
      case p: Parameter => p.name.value() == yPartBranch.stringValue
      case _            => false
    }
}
