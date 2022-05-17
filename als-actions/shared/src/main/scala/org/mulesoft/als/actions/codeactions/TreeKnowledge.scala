package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionRequestParams
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}

trait TreeKnowledge {
  protected val params: CodeActionRequestParams

  protected val maybeTree: Option[ObjectInTree] =
    params.tree.treeWithUpperElement(params.range, params.uri)

  /** Based on the chosen position from the range
    */
  protected lazy val position: Option[Position] =
    maybeTree.map(_.yPartBranch.position).map(Position(_))

  /** Information about the AST for the chosen position
    */
  protected lazy val yPartBranch: Option[YPartBranch] =
    position.map(params.yPartBranch.getCachedOrNew(_, params.uri))

}
