package org.mulesoft.language.server.js

object Main {
	def main(args: Array[String]): Unit = {
		new MSLSPServerProcess().start();
	}
}