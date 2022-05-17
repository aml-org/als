package org.mulesoft.lsp.feature.codeactions

/** A set of predefined code action kinds.
  */
case object CodeActionKind extends Enumeration {
  type CodeActionKind = Value

  /** Empty kind.
    */
  val Empty: CodeActionKind = Value("")

  /** Base kind for quickfix actions: 'quickfix'.
    */
  val QuickFix: CodeActionKind = Value("quickfix")

  /** Base kind for refactoring actions: 'refactor'.
    */
  val Refactor: CodeActionKind = Value("refactor")

  /** Base kind for refactoring extraction actions: 'refactor.extract'.
    *
    * Example extract actions:
    *
    *   - Extract method
    *   - Extract function
    *   - Extract variable
    *   - Extract interface from class
    *   - ...
    */
  val RefactorExtract: CodeActionKind = Value("refactor.extract")

  /** Base kind for refactoring inline actions: 'refactor.inline'.
    *
    * Example inline actions:
    *
    *   - Inline function
    *   - Inline variable
    *   - Inline constant
    *   - ...
    */
  val RefactorInline: CodeActionKind = Value("refactor.inline")

  /** Base kind for refactoring rewrite actions: 'refactor.rewrite'.
    *
    * Example rewrite actions:
    *
    *   - Convert JavaScript function to class
    *   - Add or remove parameter
    *   - Encapsulate field
    *   - Make method static
    *   - Move method to base class
    *   - ...
    */
  val RefactorRewrite: CodeActionKind = Value("refactor.rewrite")

  /** Base kind for source actions: `source`.
    *
    * Source code actions apply to the entire file.
    */
  val Source: CodeActionKind = Value("source")

  /** Base kind for an organize imports source action: `source.organizeImports`.
    */
  val SourceOrganizeImports: CodeActionKind = Value("source.organizeImports")

  /** Base kind for Test functionality (should not be asked in production by the client)
    */
  val Test: CodeActionKind = Value("test")
}
