package org.mulesoft.als.server.workspace.command

import amf.core.internal.parser.YNodeLikeOps
import org.mulesoft.als.logger.Logger
import org.mulesoft.lsp.workspace.ExecuteCommandParams
import org.yaml.model.{YDocument, YMap, YNode}
import org.yaml.parser.JsonParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CommandExecutor[P, R] {
  protected def buildParamFromMap(ast: YMap): Option[P]

  protected def runCommand(param: P): Future[R]

  protected val logger: Logger

  private def buildParam(arguments: List[String]): Option[P] = {
    val maybeNode: Option[YNode] = arguments.headOption
      .map(c => JsonParser(c).parse(false))
      .getOrElse(Nil)
      .collectFirst({ case d: YDocument => d.node })
    maybeNode.flatMap(n => n.toOption[YMap]).flatMap(buildParamFromMap)
  }

  protected def treatParams(arguments: List[String]): List[String] = arguments

  def runCommand(params: ExecuteCommandParams): Future[Option[R]] = {
    val className = "org.mulesoft.als.server.workspace.command.CommandExecutor"
    logger.debug(params.toString, className, "executeCommand")
    buildParam(treatParams(params.arguments)) match {
      case Some(parsedParam) =>
        logger.debug(parsedParam.toString, className, "executeCommand")
        runCommand(parsedParam).map(c => Some(c))
      case _ =>
        logger.error(
          s"Cannot build params for ${params.command}: ${params.arguments.toString()}",
          className,
          "executeCommand"
        )
        Future.successful(None)
    }
  }
}
