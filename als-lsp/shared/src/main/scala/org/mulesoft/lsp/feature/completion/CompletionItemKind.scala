package org.mulesoft.lsp.feature.completion

/** The kind of a completion entry.
  */

case object CompletionItemKind extends Enumeration {
  type CompletionItemKind = Value

  val Text: Value          = Value(1)
  val Method: Value        = Value(2)
  val Function: Value      = Value(3)
  val Constructor: Value   = Value(4)
  val Field: Value         = Value(5)
  val Variable: Value      = Value(6)
  val Class: Value         = Value(7)
  val Interface: Value     = Value(8)
  val Module: Value        = Value(9)
  val Property: Value      = Value(10)
  val Unit: Value          = Value(11)
  val ValueKind: Value     = Value(12)
  val Enum: Value          = Value(13)
  val Keyword: Value       = Value(14)
  val Snippet: Value       = Value(15)
  val Color: Value         = Value(16)
  val File: Value          = Value(17)
  val Reference: Value     = Value(18)
  val Folder: Value        = Value(19)
  val EnumMember: Value    = Value(20)
  val Constant: Value      = Value(21)
  val Struct: Value        = Value(22)
  val Event: Value         = Value(23)
  val Operator: Value      = Value(24)
  val TypeParameter: Value = Value(25)
}
