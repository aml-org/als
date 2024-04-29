package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.Parameter
import amf.core.client.scala.model.domain.{AmfObject, Shape}
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.amfintegration.dialect.DialectKnowledge

trait WritingShapeInfo {
  protected def isWritingFacet(
      astPartBranch: ASTPartBranch,
      shape: Shape,
      stack: Seq[AmfObject],
      actualDialect: Dialect
  ): Boolean =
    astPartBranch.isKeyLike &&
      !astPartBranch.parentEntryIs("required") && !writingShapeName(shape, astPartBranch) && !writingParamName(
        stack,
        astPartBranch
      ) && !astPartBranch.parentEntryIs("properties") &&
      !DialectKnowledge.isInclusion(astPartBranch, actualDialect)

  protected def writingShapeName(shape: Shape, astPartBranch: ASTPartBranch): Boolean =
    shape.name.value() == astPartBranch.stringValue

  protected def writingParamName(stack: Seq[AmfObject], astPartBranch: ASTPartBranch): Boolean =
    stack.headOption.exists {
      case p: Parameter => p.name.value() == astPartBranch.stringValue
      case _            => false
    }
}
