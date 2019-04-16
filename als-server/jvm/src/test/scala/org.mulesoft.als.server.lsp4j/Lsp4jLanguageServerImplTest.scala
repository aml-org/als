package org.mulesoft.als.server.lsp4j

import java.io._

import amf.core.unsafe.PlatformSecrets
import org.eclipse.lsp4j.InitializeParams
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.scalatest.AsyncFlatSpec

import scala.compat.java8.FutureConverters._

class Lsp4jLanguageServerImplTest extends AsyncFlatSpec with PlatformSecrets {

  behavior of "Lsp4j LanguageServerImpl"
  it should "initialize correctly" in {

    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection(logger)

    val server = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

    server.initialize(new InitializeParams()).toScala.map(_ => succeed)
  }

  behavior of "Lsp4j LanguageServerImpl with null params"
  it should "initialize should not fail" in {

    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection(logger)

    val server = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

    server.initialize(null).toScala.map(_ => succeed)
  }

}
