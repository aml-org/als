package org.mulesoft.als.server.lsp4j

import java.net.{ServerSocket, Socket}

import org.eclipse.lsp4j.launch.LSPLauncher
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}

object Main {
  case class Options(port: Int, listen: Boolean)
  val DefaultOptions = Options(4000, listen = false)

  def readOptions(args: Array[String]): Options = {
    def innerReadOptions(options: Options, list: List[String]): Options = list match {
        case Nil => options
        case "--port" :: value :: tail =>
          innerReadOptions(options.copy(port = value.toInt), tail)
        case "--listen" :: tail =>
          innerReadOptions(options.copy(listen = true), tail)

        case _ =>
          throw new IllegalArgumentException()
      }

    innerReadOptions(DefaultOptions, args.toList)
  }

  def createSocket(options: Options): Socket = options match {
    case Options(port, true) =>
      new ServerSocket(port).accept()
    case Options(port, false) =>
      new Socket("localhost", port)
  }

  def main(args: Array[String]): Unit = {
    val options = readOptions(args)

    try {
      val socket = createSocket(options)

        val in  = socket.getInputStream
        val out = socket.getOutputStream

        val logger: Logger   = PrintLnLogger
        val clientConnection = ClientConnection(logger)
        val server           = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

        val launcher = LSPLauncher.createServerLauncher(server, in, out)
        val client   = launcher.getRemoteProxy
        clientConnection.connect(LanguageClientWrapper(client))
        launcher.startListening
        println("ALS started")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
