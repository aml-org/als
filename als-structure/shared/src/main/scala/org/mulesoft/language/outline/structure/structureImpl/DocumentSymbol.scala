package org.mulesoft.language.outline.structure.structureImpl

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.SymbolKinds.SymbolKind

case class DocumentSymbol private (
    name: String,
    kind: SymbolKind,
    deprecated: Boolean,
    range: PositionRange,
    selectionRange: PositionRange,
    children: List[DocumentSymbol]
)

object DocumentSymbol {
  def apply(name: String, kind: SymbolKind, range: PositionRange, children: List[DocumentSymbol]): DocumentSymbol = {

    new DocumentSymbol(name, kind, false, range, range, children)
  }
}

object SymbolKinds {
  case class SymbolKind(index: Int)
  //
  object File          extends SymbolKind(1)
  object Module        extends SymbolKind(2)
  object Namespace     extends SymbolKind(3)
  object Package       extends SymbolKind(4)
  object Class         extends SymbolKind(5)
  object Method        extends SymbolKind(6)
  object Property      extends SymbolKind(7)
  object Field         extends SymbolKind(8)
  object Constructor   extends SymbolKind(9)
  object Enum          extends SymbolKind(10)
  object Interface     extends SymbolKind(11)
  object Function      extends SymbolKind(12)
  object Variable      extends SymbolKind(13)
  object Constant      extends SymbolKind(14)
  object String        extends SymbolKind(15)
  object Number        extends SymbolKind(16)
  object Boolean       extends SymbolKind(17)
  object Array         extends SymbolKind(18)
  object Object        extends SymbolKind(19)
  object Key           extends SymbolKind(20)
  object Null          extends SymbolKind(21)
  object EnumMember    extends SymbolKind(22)
  object Struct        extends SymbolKind(23)
  object Event         extends SymbolKind(24)
  object Operator      extends SymbolKind(25)
  object TypeParameter extends SymbolKind(26)

}
