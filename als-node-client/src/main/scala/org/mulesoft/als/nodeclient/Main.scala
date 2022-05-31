package org.mulesoft.als.nodeclient

import amf.core.internal.unsafe.PlatformSecrets
import amf.custom.validation.client.platform.CustomValidator
import amf.custom.validation.client.scala.validator.JsCustomValidator
import amf.custom.validation.internal.unsafe.AmfCustomValidatorNode
import io.scalajs.nodejs.process
import org.mulesoft.als.server.client.platform.AlsLanguageServerFactory
import org.mulesoft.als.server.{ClientNotifierFactory, JsSerializationProps, ProtocolConnectionBinder}
import org.mulesoft.als.vscode.{ProtocolConnection, ServerSocketTransport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise

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

      val languageServer = new AlsLanguageServerFactory(clientConnection)
        .withSerializationProps(serializationProps)
        .withAmfCustomValidator(new CustomValidator {
          override def validate(document: String, profile: String): Promise[String] =
            new JsCustomValidator(AmfCustomValidatorNode).validate(document, profile).toJSPromise
        })
        .withDirectoryResolver(new ClientPlatformDirectoryResolver(platform))
        .withLogger(logger)
        .build()

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
