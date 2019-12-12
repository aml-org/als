package org.mulesoft.als.server.workspace.command

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.lsp.workspace.ExecuteCommandParams
import org.yaml.model.{YDocument, YMap, YNode}
import org.yaml.parser.JsonParser
import amf.core.parser.YNodeLikeOps

trait CommandExecutor[P] {
  protected def buildParamFromMap(ast: YMap): Option[P]
  protected def runCommand(param: P): Unit
  protected val logger: Logger

  private def buildParam(params: ExecuteCommandParams): Option[P] = {
    val maybeNode: Option[YNode] = params.arguments.headOption
      .map(c => JsonParser(c).parse(false))
      .getOrElse(Nil)
      .collectFirst({ case d: YDocument => d.node })
    maybeNode.flatMap(n => n.toOption[YMap]).flatMap(buildParamFromMap)
  }

  def runCommand(params: ExecuteCommandParams): Unit = {
    buildParam(params) match {
      case Some(parsedParam) => runCommand(parsedParam)
      case _ =>
        logger.error(s"Cannot build params for focus: ${params.arguments.toString()}",
                     "org.mulesoft.als.server.workspace.command.CommandExecutor",
                     "executeCommand")
    }

  }
}
