package org.mulesoft.language.server.lsp4j

import java.io.{File, IOException, InputStreamReader, PrintStream}
import java.net.Socket

import org.eclipse.lsp4j.launch.LSPLauncher

object Main {
  def main(args: Array[String]): Unit = {
    val o = new PrintStream(new File("/Users/jisoldi/Downloads/log.txt"))

    System.setOut(o)
    System.setErr(o)
    System.out.println("This will be written to the text file")

    val port = if (args.length >= 1) args(0) else "4000"
    try {
      val socket = new Socket("localhost", port.toInt)
      val in = socket.getInputStream
      val out = socket.getOutputStream

      val textDocumentServiceImpl = new TextDocumentServiceImpl(None)
      val workspaceService = new WorkspaceServiceImpl()

      val server = new LanguageServerImpl(textDocumentServiceImpl, textDocumentServiceImpl, workspaceService)

      val launcher = LSPLauncher.createServerLauncher(server, in, out)
      val client = launcher.getRemoteProxy
      textDocumentServiceImpl.connect(client)
      launcher.startListening
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
