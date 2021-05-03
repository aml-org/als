package org.mulesoft.lsp.edit

import org.mulesoft.lsp.feature.common.Range

/** currently not used */
case class InsertReplaceEdit(newText: String, insert: Range, replace: Range)
