package org.mulesoft.als.server

import io.scalajs.nodejs.process
import org.mulesoft.als.vscode.ServerSocketTransport

object Main {
  case class Options(port: Int)
  val DefaultOptions = Options(4000)

  def readOptions(args: Array[String]): Options = {
    def innerReadOptions(options: Options, list: List[String]): Options =
      list match {
        case Nil => options
        case "--port" :: value :: tail =>
          innerReadOptions(options.copy(port = value.toInt), tail)
        case _ =>
          throw new IllegalArgumentException()
      }

    innerReadOptions(DefaultOptions, args.toList)
  }


  def main(args: Array[String]): Unit = {
    println("Starting ALS...")
    val options = readOptions(process.argv.drop(2).toArray)

    try {
      println("Starting in port " + options.port)

      val factory = new AlsConnectionFactory()
      val transport = ServerSocketTransport(options.port)
      val reader = transport._1
      val writer = transport._2

      factory.fromReaders(reader, writer)

      println("ALS started")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
