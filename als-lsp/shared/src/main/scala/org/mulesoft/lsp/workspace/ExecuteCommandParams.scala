package org.mulesoft.lsp.workspace

case class ExecuteCommandParams(command: String, args: List[String]) {
  val arguments: List[String] = args.map(s => {
    if (s.endsWith("\"") && s.endsWith("\"")) {
      s.substring(1, s.length - 1)
    } else {
      s
    }
  })
}
