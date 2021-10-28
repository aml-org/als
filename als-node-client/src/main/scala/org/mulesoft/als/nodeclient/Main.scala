package org.mulesoft.als.nodeclient

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import io.scalajs.nodejs.process
import org.mulesoft.als.server.{
  ClientNotifierFactory,
  JsSerializationProps,
  LanguageServerFactory,
  ProtocolConnectionBinder
}
import org.mulesoft.als.vscode.{ProtocolConnection, ServerSocketTransport}

import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

object Main extends PlatformSecrets {
  case class Options(port: Int)
  val DefaultOptions: Options = Options(4000)

  def readOptions(args: Array[String]): Options = {
    def innerReadOptions(options: Options, list: List[String]): Options =
      list match {
        case Nil => options
        case "--port" :: value :: tail =>
          innerReadOptions(options.copy(port = value.toInt), tail)
        case e :: tail =>
          println(s"[WARN] Unrecognized option: $e")
          innerReadOptions(options, tail)
      }

    innerReadOptions(DefaultOptions, args.toList)
  }

  def main(args: Array[String]): Unit = {
    println("Starting ALS...")
    val options = readOptions(process.argv.drop(2).toArray)

    try {
      println("Starting in port " + options.port)

      val logger = JsPrintLnLogger()

      val clientConnection   = ClientNotifierFactory.createWithClientAware(logger)
      val serializationProps = JsSerializationProps(clientConnection)
      val languageServer = LanguageServerFactory.fromLoaders(
        clientConnection,
        serializationProps,
        clientLoaders = platform.loaders().asClient.asInstanceOf[ClientList[ClientResourceLoader]],
        clientDirResolver = new ClientPlatformDirectoryResolver(platform),
        amfCustomValidator = AmfCustomValidatorNode
      )

      val transport  = ServerSocketTransport(options.port)
      val reader     = transport._1
      val writer     = transport._2
      val connection = ProtocolConnection(reader, writer, logger)

      ProtocolConnectionBinder.bind(connection, languageServer, clientConnection, serializationProps)

      connection.listen()

      println("ALS started")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}

// $COVERAGE-ON$
