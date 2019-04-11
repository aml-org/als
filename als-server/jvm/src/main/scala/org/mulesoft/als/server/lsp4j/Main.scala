package org.mulesoft.als.server.lsp4j

import java.io.{File, PrintStream}
import java.net.Socket

import org.eclipse.lsp4j.launch.LSPLauncher
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}

object Main {
  def main(args: Array[String]): Unit = {
    val o = new PrintStream(new File(s"${System.getProperty("user.home")}/Downloads/log.txt"))

    System.setOut(o)
    System.setErr(o)
    System.out.println("This will be written to the text file")

    val port = if (args.length >= 1) args(0) else "4000"
    try {
      val socket = new Socket("localhost", port.toInt)
      val in     = socket.getInputStream
      val out    = socket.getOutputStream

      val logger: Logger = PrintLnLogger
      val clientConnection = ClientConnection(logger)

      val server = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

      val launcher = LSPLauncher.createServerLauncher(server, in, out)
      val client   = launcher.getRemoteProxy
      clientConnection.connect(LanguageClientWrapper(client))
      launcher.startListening
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
