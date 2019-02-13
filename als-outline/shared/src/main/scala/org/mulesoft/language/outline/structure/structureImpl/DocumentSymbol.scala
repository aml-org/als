package org.mulesoft.language.outline.structure.structureImpl

import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind

case class DocumentSymbol(name: String,
                          kind: SymbolKind,
                          deprecated: Boolean,
                          range: amf.core.parser.Range,
                          selectionRange: amf.core.parser.Range,
                          children: List[DocumentSymbol])

object SymbolKind {
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
//object SymbolKind extends Enumeration {
//  type SymbolKind = Value
//
//  val File          = Value(1, "File")
//  val Module        = Value(2, "Module")
//  val Namespace     = Value(3, "Namespace")
//  val Package       = Value(4, "Package")
//  val Class         = Value(5, "Class")
//  val Method        = Value(6, "Method")
//  val Property      = Value(7, "Property")
//  val Field         = Value(8, "Field")
//  val Constructor   = Value(9, "Constructor")
//  val Enum          = Value(10, "Enum")
//  val Interface     = Value(11, "Interface")
//  val Function      = Value(12, "Function")
//  val Variable      = Value(13, "Variable")
//  val Constant      = Value(14, "Constant")
//  val String        = Value(15, "String")
//  val Number        = Value(16, "Number")
//  val Boolean       = Value(17, "Boolean")
//  val Array         = Value(18, "Array")
//  val Object        = Value(19, "Object")
//  val Key           = Value(20, "Key")
//  val Null          = Value(21, "Null")
//  val EnumMember    = Value(22, "EnumMember")
//  val Struct        = Value(23, "Struct")
//  val Event         = Value(24, "Event")
//  val Operator      = Value(25, "Operator")
//  val TypeParameter = Value(26, "TypeParameter")
//}
