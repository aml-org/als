package org.mulesoft.lsp.feature.documentsymbol

/** A symbol kind.
  */
case object SymbolKind extends Enumeration {
  type SymbolKind = Value

  val File: Value          = Value(1)
  val Module: Value        = Value(2)
  val Namespace: Value     = Value(3)
  val Package: Value       = Value(4)
  val Class: Value         = Value(5)
  val Method: Value        = Value(6)
  val Property: Value      = Value(7)
  val Field: Value         = Value(8)
  val Constructor: Value   = Value(9)
  val Enum: Value          = Value(10)
  val Interface: Value     = Value(11)
  val Function: Value      = Value(12)
  val Variable: Value      = Value(13)
  val Constant: Value      = Value(14)
  val String: Value        = Value(15)
  val Number: Value        = Value(16)
  val Boolean: Value       = Value(17)
  val Array: Value         = Value(18)
  val Object: Value        = Value(19)
  val Key: Value           = Value(20)
  val Null: Value          = Value(21)
  val EnumMember: Value    = Value(22)
  val Struct: Value        = Value(23)
  val Event: Value         = Value(24)
  val Operator: Value      = Value(25)
  val TypeParameter: Value = Value(26)
}
