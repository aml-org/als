package org.mulesoft.als.actions.links

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.link.DocumentLink
import org.scalatest.{Assertion, FlatSpec, Matchers}
import org.yaml.model.YPart
import org.yaml.parser.YamlParser

class FindLinksTests extends FlatSpec with Matchers with FindLinks with PlatformSecrets {
  behavior of "FindLinks"

  private def runLinkTest(text: String,
                          fn: (YPart, Platform) => Seq[DocumentLink],
                          range: Seq[PositionRange]): Assertion = {
    YamlParser(text, "file:///root/file")
      .parse(false)
      .head
      .foreach(fn(_, platform)) should be(
      range.map(r => DocumentLink(LspRangeConverter.toLspRange(r), "file:///root/link", None)))
  }

  it should "extractUsesLinks should find a link" in {
    runLinkTest(
      """uses:
        |  name: link
        |""".stripMargin,
      extractUsesLinks,
      Seq(
        PositionRange(
          Position(1, 8),
          Position(1, 12)
        ))
    )
  }

  it should "extractUsesLinks should not find a link" in {
    runLinkTest("""root:
        |  uses:
        |    name: link
        |""".stripMargin,
                extractUsesLinks,
                Nil)
  }

  it should "extractRamlIncludes should find a link" in {
    runLinkTest("node: !include link",
                extractRamlIncludes,
                Seq(
                  PositionRange(
                    Position(0, 15),
                    Position(0, 19)
                  )))
  }

  it should "extractJsonRefs should find a link" in {
    runLinkTest("$ref: link",
                extractJsonRefs,
                Seq(
                  PositionRange(
                    Position(0, 6),
                    Position(0, 10)
                  )))
  }
}
