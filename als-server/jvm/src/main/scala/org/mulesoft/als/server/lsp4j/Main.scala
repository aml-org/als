package org.mulesoft.als.server.lsp4j

import java.io.StringWriter
import java.net.{ServerSocket, Socket}

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.als.server.JvmSerializationProps
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.lsp.feature.serialization.SerializationMessage

object Main {
  case class Options(port: Int,
                     listen: Boolean,
                     dialectPath: Option[String],
                     dialectName: Option[String],
                     vocabularyPath: Option[String])
  val DefaultOptions = Options(4000, listen = false, dialectPath = None, dialectName = None, vocabularyPath = None)

  def readOptions(args: Array[String]): Options = {
    def innerReadOptions(options: Options, list: List[String]): Options =
      list match {
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
    case Options(port, true, _, _, _) =>
      new ServerSocket(port).accept()
    case Options(port, false, _, _, _) =>
      new Socket("localhost", port)
  }

  def main(args: Array[String]): Unit = {
    val options = readOptions(args)

    try {
      val socket = createSocket(options)

      val in  = socket.getInputStream
      val out = socket.getOutputStream

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
        override def notifySerialization(params: SerializationMessage[StringWriter]): Unit = {}

        override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}
      }) // example

      launcher.startListening
      println("ALS started")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

}
