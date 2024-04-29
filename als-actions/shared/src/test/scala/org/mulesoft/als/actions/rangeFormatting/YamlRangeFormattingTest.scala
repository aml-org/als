package org.mulesoft.als.actions.rangeFormatting

import org.yaml.model.{ParseErrorHandler, YDocument}
import org.yaml.parser.YamlParser

class YamlRangeFormattingTest extends RangeFormattingTest {
  override def basePath: String = "als-actions/shared/src/test/resources/actions/documentFormatting/yaml"

  override def parse(content: String)(implicit eh: ParseErrorHandler): YDocument =
    YamlParser(content)(eh).document(true)

  override def isJson: Boolean = false

  override def fileExtensions: Seq[String] = Seq(".yaml", ".raml")

  s"Range formatting test for YAML by directory" - {
    forDirectory(dir, "", mustHaveMarker = false)
  }
}
