package org.mulesoft.als.server.workspace

import org.yaml.model.{YDocument, YMap}
import org.yaml.parser.JsonParser

class ExtractFromJsonRoot(key: String) extends MainFileExtractable {
  override def extractMainFile(cs: CharSequence): Option[String] =
    JsonParser(cs).parse(false).headOption match {
      case Some(d: YDocument) =>
        d.node.value match {
          case y: YMap =>
            y.entries
              .find(e => e.key.asScalar.exists(_.text == key))
              .flatMap(_.value.asScalar.map(_.text))
          case _ => None
        }
      case _ => None
    }
}
