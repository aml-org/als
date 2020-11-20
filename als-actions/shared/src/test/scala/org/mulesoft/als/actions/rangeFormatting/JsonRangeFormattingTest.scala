package org.mulesoft.als.actions.rangeFormatting
import org.yaml.model.YDocument
import org.yaml.parser.JsonParser

class JsonRangeFormattingTest extends RangeFormattingTest {
  override def basePath: String = "als-actions/shared/src/test/resources/actions/documentFormatting/json"

  override def parse(content: String): YDocument = JsonParser(content).document(true)

  override def isJson: Boolean = true

  override def fileExtensions: Seq[String] = Seq(".json")

  s"Range formatting test for JSON by directory" - {
    forDirectory(dir, "", mustHaveMarker = false)
  }
}
