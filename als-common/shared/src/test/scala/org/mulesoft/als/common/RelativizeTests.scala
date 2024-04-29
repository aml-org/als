package org.mulesoft.als.common

import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RelativizeTests extends AnyFlatSpec with Matchers {
  behavior of "path relativization"

  it should "relativize on same level folder" in {
    "file:///a/b/c".relativize("file:///a/b/d") shouldBe "c"
  }

  it should "relativize on upper level folder" in {
    "file:///a/b/c/d".relativize("file:///a/b/d") shouldBe "c/d"
  }

  it should "relativize on lower level folder" in {
    "file:///a/c".relativize("file:///a/b/d") shouldBe "../c"
  }

  it should "relativize on different folders" in {
    "file:///a1/b/c".relativize("file:///a2/b/c") shouldBe "../../a1/b/c"
  }
}
