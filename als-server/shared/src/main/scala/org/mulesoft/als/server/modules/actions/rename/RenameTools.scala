package org.mulesoft.als.server.modules.actions.rename

import amf.core.client.scala.model.document.Document
import org.mulesoft.als.common.YamlWrapper.AlsYScalarOps
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.UnitWorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.relationships.AliasRelationships
import org.yaml.model.YScalar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RenameTools {
  protected def branch(cu: CompilableUnit, position: Position, uri: String): YPartBranch =
    cu.yPartBranch.getCachedOrNew(position, uri)
  protected def tree(cu: CompilableUnit, position: Position, uri: String): ObjectInTree =
    cu.tree.getCachedOrNew(position, uri)
  protected def isDeclarable(cu: CompilableUnit, tree: ObjectInTree): Boolean =
    (!tree.obj.isAbstract &&
      !tree.obj.isInstanceOf[Document] &&
      tree.obj.declarableKey(cu.definedBy).isDefined)

  protected def isDeclarableKey(cu: CompilableUnit, position: Position, uri: String): Boolean =
    (isDeclarable(cu, tree(cu, position, uri)) || cu.definedBy == MetaDialect.dialect) &&
      branch(cu, position, uri).isKey

  protected def keyCleanRange(uri: String, position: Position, bu: CompilableUnit): PositionRange =
    bu.yPartBranch.getCachedOrNew(position, uri).node match {
      case s: YScalar => PositionRange(s.unmarkedRange())
      case o          => PositionRange(o.range)
    }

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
