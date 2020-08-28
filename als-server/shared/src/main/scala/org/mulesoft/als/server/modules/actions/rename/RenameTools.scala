package org.mulesoft.als.server.modules.actions.rename

import amf.core.model.document.Document
import org.mulesoft.als.actions.common.AliasRelationships
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.UnitWorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait RenameTools {
  protected def branch(cu: CompilableUnit, position: Position, uri: String): YPartBranch =
    cu.yPartBranch.getCachedOrNew(position, uri)
  protected def tree(cu: CompilableUnit, position: Position, uri: String): ObjectInTree =
    cu.tree.getCachedOrNew(position, uri)
  protected def isDeclarable(cu: CompilableUnit, tree: ObjectInTree): Boolean =
    (!tree.obj.isAbstract &&
      !tree.obj.isInstanceOf[Document] &&
      cu.definedBy.forall(d => tree.obj.declarableKey(d).isDefined))

  protected def isDeclarableKey(cu: CompilableUnit, position: Position, uri: String): Boolean =
    (isDeclarable(cu, tree(cu, position, uri)) || cu.definedBy.contains(MetaDialect.dialect)) &&
      branch(cu, position, uri).isKey

  protected def keyCleanRange(uri: String, position: Position, bu: CompilableUnit): PositionRange = {
    val branch   = bu.yPartBranch.getCachedOrNew(position, uri)
    val node     = branch.node
    val realText = branch.stringValue

    val range      = PositionRange(branch.node.range)
    val cleanStart = range.start.moveColumn(node.toString.indexOf(realText))
    val cleanEnd   = cleanStart.moveColumn(realText.length)
    val cleanRange = PositionRange(cleanStart, cleanEnd)
    cleanRange
  }

  protected def keyCleanText(uri: String, position: Position, bu: CompilableUnit): String =
    bu.yPartBranch.getCachedOrNew(position, uri).stringValue

  protected def withIsAliases(bu: CompilableUnit,
                              uri: String,
                              uuid: String,
                              position: Position,
                              workspace: UnitWorkspaceManager): Future[(CompilableUnit, Boolean)] =
    workspace
      .getAliases(uri, uuid)
      .map(AliasRelationships.isAliasDeclaration(_, position, bu.yPartBranch))
      .map((bu, _))
  protected val renameThroughReferenceEnabled = false
}
