package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.JvmSerializationProps
import org.mulesoft.als.server.client.{AlsLanguageClientExtensions, AlsLanguageClientWrapper, platform}
import org.mulesoft.als.server.client.platform.AlsLanguageServerFactory
import org.mulesoft.als.server.lsp4j.internal.GsonConsumerBuilder

import java.io.StringWriter
import java.net.{ServerSocket, Socket}
import scala.annotation.tailrec

object Main {

  case class Options(port: Int, listen: Boolean, systemStream: Boolean = false)

  val DefaultOptions: Options =
    Options(4000, listen = false)

  private def readOptions(args: Array[String]): Options = {
    @tailrec
    def innerReadOptions(options: Options, list: List[String]): Options =
      list match {
        case Nil => options
        case "--port" :: value :: tail =>
          innerReadOptions(options.copy(port = value.toInt), tail)
        case "--systemStream" :: tail => // intellij lsp plugin only supports stdio at the moment
          innerReadOptions(options.copy(systemStream = true), tail)
        case "--listen" :: tail =>
          innerReadOptions(options.copy(listen = true), tail)
        case _ =>
          throw new IllegalArgumentException()
      }

    innerReadOptions(DefaultOptions, args.toList)
  }

  def createSocket(options: Options): Socket = options match {
    case Options(port, true, _) =>
      new ServerSocket(port).accept()
    case Options(port, false, _) =>
      new Socket("localhost", port)
  }

  def main(args: Array[String]): Unit = {
    val options = readOptions(args)

    try {
      val (in, out) =
        if (options.systemStream)
          (System.in, System.out)
        else {
          val socket = createSocket(options)
          (socket.getInputStream, socket.getOutputStream)
        }

      val logger: Logger   = PrintLnLogger
      val clientConnection = platform.ClientConnection[StringWriter](logger)

      logger.debug("Building LanguageServerImpl", "Main", "main")
      val server = new LanguageServerImpl(
        new AlsLanguageServerFactory(clientConnection)
          .withSerializationProps(JvmSerializationProps(clientConnection))
          .build()
      )

      logger.debug("Launching services", "Main", "main")
      val launcher = new Launcher.Builder[AlsLanguageClientExtensions]()
        .configureGson(new GsonConsumerBuilder())
        .setLocalService(server)
        .setRemoteInterface(classOf[AlsLanguageClientExtensions])
        .setInput(in)
        .setOutput(out)
        .create()

      val client = launcher.getRemoteProxy
      clientConnection.connect(AlsLanguageClientWrapper(client))

      logger.debug("Connecting Client", "Main", "main")

      clientConnection.connectAls(AlsLanguageClientWrapper(client))

      launcher.startListening
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

}
