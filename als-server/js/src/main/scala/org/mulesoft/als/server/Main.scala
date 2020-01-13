package org.mulesoft.als.server

import io.scalajs.nodejs.process
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.PrintLnLogger
import org.mulesoft.als.vscode.{Logger, ProtocolConnection, ServerSocketTransport}

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

object JsPrintLnLogger {
  def apply(): ClientLogger =
    js.Dynamic
      .literal(
        error = (message: String) => println(message),
        warn = (message: String) => println(message),
        info = (message: String) => println(message),
        log = (message: String) => println(message),
      )
      .asInstanceOf[ClientLogger]
}

object Main {
  case class Options(port: Int)
  val DefaultOptions = Options(4000)

  def readOptions(args: Array[String]): Options = {
    def innerReadOptions(options: Options, list: List[String]): Options =
      list match {
        case Nil => options
        case "--port" :: value :: tail =>
          innerReadOptions(options.copy(port = value.toInt), tail)
        case _ =>
          throw new IllegalArgumentException()
      }

    innerReadOptions(DefaultOptions, args.toList)
  }

  def main(args: Array[String]): Unit = {
    println("Starting ALS...")
    val options = readOptions(process.argv.drop(2).toArray)

    try {
      println("Starting in port " + options.port)

      val logger = JsPrintLnLogger()

      val clientConnection = ClientNotifierFactory.createWithClientAware(logger)
      val languageServer = LanguageServerFactory.fromLoaders(clientConnection)

      val transport = ServerSocketTransport(options.port)
      val reader    = transport._1
      val writer    = transport._2
      val connection = ProtocolConnection(reader, writer, logger)

      ProtocolConnectionBinder.bind(connection, languageServer, clientConnection)

      connection.listen()

      println("ALS started")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
// $COVERAGE-ON$
