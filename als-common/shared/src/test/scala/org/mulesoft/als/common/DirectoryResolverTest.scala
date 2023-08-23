package org.mulesoft.als.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class DirectoryResolverTest extends AnyFlatSpec with Matchers {

  private val dr = new DirectoryResolver {
    override def exists(path: String): Future[Boolean] = ???

    override def readDir(path: String): Future[Seq[String]] = ???

    override def isDirectory(path: String): Future[Boolean] = ???
  }

  behavior of "Directory Resolver"

  it should "transform simple absolute URI to Path" in {
    dr.toPath("file:///my/path") shouldBe "/my/path"
  }

  it should "transform simple relative URI to Path" in {
    dr.toPath("file://my/path") shouldBe "my/path"
  }

  it should "transform URI with spaces to Path" in {
    dr.toPath("file:///my/path%20with%20spaces") shouldBe "/my/path with spaces"
  }
}
