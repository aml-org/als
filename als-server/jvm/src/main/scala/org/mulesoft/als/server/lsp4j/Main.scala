package org.mulesoft.als.server.lsp4j

import java.io.StringWriter
import java.net.{ServerSocket, Socket}

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.als.server.JvmSerializationProps
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.protocol.client.AlsLanguageClient
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

object Main {

  case class Options(port: Int,
                     listen: Boolean,
                     dialectPath: Option[String],
                     dialectName: Option[String],
                     vocabularyPath: Option[String],
                     systemStream: Boolean = false)

  val DefaultOptions: Options =
    Options(4000, listen = false, dialectPath = None, dialectName = None, vocabularyPath = None)

  private def readOptions(args: Array[String]): Options = {
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
    case Options(port, true, _, _, _, false) =>
      new ServerSocket(port).accept()
    case Options(port, false, _, _, _, false) =>
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
      val clientConnection = ClientConnection[StringWriter](logger)

      val server = new LanguageServerImpl(
        new LanguageServerFactory(clientConnection)
          .withSerializationProps(JvmSerializationProps(clientConnection))
          .build()
      )

      val launcher = new Launcher.Builder[LanguageClient]()
        .setLocalService(server)
        .setRemoteInterface(classOf[LanguageClient])
        .setInput(in)
        .setOutput(out)
        .create()

      val client = launcher.getRemoteProxy
      clientConnection.connect(LanguageClientWrapper(client))

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
