package org.mulesoft.als.server

import org.mulesoft.als.server.client.platform.AlsClientNotifier
import org.mulesoft.als.server.feature.serialization.{SerializationParams, SerializationResult}
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.lsp.feature.RequestType
import org.yaml.builder.DocBuilder

abstract class SerializationProps[S](val alsClientNotifier: AlsClientNotifier[S]) {

  def newDocBuilder(prettyPrint: Boolean): DocBuilder[S]
  val requestType: RequestType[SerializationParams, SerializationResult[S]] =
    new RequestType[SerializationParams, SerializationResult[S]] {}
}

class EmptySerializationProps
    extends SerializationProps[String](
      new AlsClientNotifier[String] {
        override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}

        override def notifySerialization(params: SerializationResult[String]): Unit = {}
      }
    ) {
  override def newDocBuilder(prettyPrint: Boolean): DocBuilder[String] = new DocBuilder[String] {

    /** Return the result document */
    override def result: String = ""

    /** Build a List document */
    override def list(f: DocBuilder.Part[String] => Unit): String = ""

    /** Build an Object document */
    override def obj(f: DocBuilder.Entry[String] => Unit): String = ""

    /** Build a document */
    override def doc(f: DocBuilder.Part[String] => Unit): String = ""
  }
}
