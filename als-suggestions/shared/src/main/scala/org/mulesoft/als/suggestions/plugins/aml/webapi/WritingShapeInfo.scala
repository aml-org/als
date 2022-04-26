package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.Parameter
import amf.core.client.scala.model.domain.{AmfObject, Shape}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.dialect.DialectKnowledge

trait WritingShapeInfo {
  protected def isWritingFacet(
      yPartBranch: YPartBranch,
      shape: Shape,
      stack: Seq[AmfObject],
      actualDialect: Dialect
  ): Boolean =
    yPartBranch.isKeyLike &&
      !yPartBranch.isKeyDescendantOf("required") && !writingShapeName(shape, yPartBranch) && !writingParamName(
        stack,
        yPartBranch
      ) && !yPartBranch.parentEntryIs("properties") &&
      !DialectKnowledge.isInclusion(yPartBranch, actualDialect)

  protected def writingShapeName(shape: Shape, yPartBranch: YPartBranch): Boolean =
    shape.name.value() == yPartBranch.stringValue

  protected def writingParamName(stack: Seq[AmfObject], yPartBranch: YPartBranch): Boolean =
    stack.headOption.exists {
      case p: Parameter => p.name.value() == yPartBranch.stringValue
      case _            => false
    }
}
