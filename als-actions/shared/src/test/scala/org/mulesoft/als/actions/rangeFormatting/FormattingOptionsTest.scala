package org.mulesoft.als.actions.rangeFormatting

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.lsp.configuration.FormattingOptions
import org.scalatest.{FlatSpec, Matchers}
import org.yaml.model.YMap

class FormattingOptionsTest extends FlatSpec with Matchers with PlatformSecrets {
  behavior of "Formatting Options"

  private val baseTest = "my:    \n  test: lorem ipsum       \n\n\n\n\n"

  it should "trim trailing whitespaces" in {
    val expected = "my:\n  test: lorem ipsum\n\n\n\n\n"

    val result = RangeFormatting(
      YMap.empty,
      FormattingOptions(0, true, Some(true), Some(false), Some(false)),
      false,
      ErrorsCollected(Nil),
      Some("")
    )
      .applyOptions(baseTest)
    result should be(expected)
  }

  it should "trim final new lines" in {
    val expected = "my:    \n  test: lorem ipsum       \n"

    val result = RangeFormatting(
      YMap.empty,
      FormattingOptions(0, true, Some(false), Some(false), Some(true)),
      false,
      ErrorsCollected(Nil),
      Some("")
    )
      .applyOptions(baseTest)
    result should be(expected)
  }

  it should "don't add final new line if it already has" in {
    val result = RangeFormatting(
      YMap.empty,
      FormattingOptions(0, true, Some(false), Some(true), Some(false)),
      false,
      ErrorsCollected(Nil),
      Some("")
    )
      .applyOptions(baseTest)
    result should be(baseTest)
  }

  it should "add final new line if it does not have" in {
    val baseNoEOL = "my:    \n  test: lorem ipsum       "
    val expected  = s"$baseNoEOL\n"

    val result = RangeFormatting(
      YMap.empty,
      FormattingOptions(0, true, Some(false), Some(true), Some(false)),
      false,
      ErrorsCollected(Nil),
      Some("")
    )
      .applyOptions(baseNoEOL)
    result should be(expected)
  }

  it should "combine all three options" in {
    val expected = "my:\n  test: lorem ipsum\n"

    val result = RangeFormatting(
      YMap.empty,
      FormattingOptions(0, true, Some(true), Some(true), Some(true)),
      false,
      ErrorsCollected(Nil),
      Some("")
    )
      .applyOptions(baseTest)
    result should be(expected)
  }

}
