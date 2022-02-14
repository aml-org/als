package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.JvmSerializationProps
import org.mulesoft.als.server.client.platform
import org.mulesoft.als.server.client.platform.{ClientConnection, AlsLanguageServerFactory}
import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.als.server.lsp4j.internal.GsonConsumerBuilder
import org.mulesoft.als.server.protocol.client.AlsLanguageClient

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
      val launcher = new Launcher.Builder[LanguageClient]()
        .configureGson(new GsonConsumerBuilder())
        .setLocalService(server)
        .setRemoteInterface(classOf[LanguageClient])
        .setInput(in)
        .setOutput(out)
        .create()

      val client = launcher.getRemoteProxy
      clientConnection.connect(LanguageClientWrapper(client))

      logger.debug("Connecting Client", "Main", "main")

      clientConnection.connectAls(new AlsLanguageClient[StringWriter] {
        override def notifySerialization(params: SerializationResult[StringWriter]): Unit = {}

        override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}
      }) // example

      launcher.startListening
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

}
