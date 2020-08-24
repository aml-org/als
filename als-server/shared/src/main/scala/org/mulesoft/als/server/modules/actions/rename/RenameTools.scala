package org.mulesoft.als.server.modules.actions.rename

import amf.core.model.document.Document
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

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

  protected val renameThroughReferenceEnabled = false
}
