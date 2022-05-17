package org.mulesoft.als.common.diff

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.common.io.{AsyncFile, FileSystem}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait FileAssertionTest extends PlatformSecrets {

  private implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  protected lazy val fs: FileSystem = platform.fs

  protected def writeTemporaryFile(golden: String)(content: String): Future[AsyncFile] = {
    val file = tmp(s"${golden
        .stripPrefix("file://")
        .replaceAllLiterally(fs.separatorChar.toString, "/")
        .replaceAllLiterally("/", "-")}.tmp")
    val actual = fs.asyncFile(file)
    actual.write(content).map(_ => actual)
  }

  protected def assertDifferences(actual: AsyncFile, golden: String): Future[Assertion] = {
    val expected =
      fs.asyncFile(golden.stripPrefix("file://")) // TODO: check if this works with encoded URI (f.e. spaces)
    Tests.checkDiff(actual, expected)
  }

  private def endWithLineSeparator(path: String, separator: Char) =
    if (path.endsWith(s"$separator")) path
    else path + separator

  /** Return random temporary file name for testing. */
  def tmp(name: String = ""): String =
    endWithLineSeparator(platform.tmpdir(), platform.fs.separatorChar) + System.nanoTime() + "-" + name
}
