package org.mulesoft.language.outline.test.amfmigrated.common

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.common.io.{AsyncFile, FileSystem}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait FileAssertionTest extends PlatformSecrets {

  private implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  protected lazy val fs: FileSystem = platform.fs

  protected def writeTemporaryFile(golden: String)(content: String): Future[AsyncFile] = {
    val file   = tmp(s"${golden.replaceAll("/", "-")}.tmp")
    val actual = fs.asyncFile(file)
    actual.write(content).map(_ => actual)
  }

  protected def assertDifferences(actual: AsyncFile, golden: String): Future[Assertion] = {
    val expected = fs.asyncFile(golden.stripPrefix("file://"))
    expected.read().flatMap(_ => Tests.checkDiff(actual, expected))
  }

  /** Return random temporary file name for testing. */
  def tmp(name: String = ""): String =
    (platform.tmpdir() + platform.fs.separatorChar + System.nanoTime() + "-" + name)
      .replaceAll(s"${platform.fs.separatorChar}${platform.fs.separatorChar}", s"${platform.fs.separatorChar}")
}
