package org.mulesoft.als.common

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.common.io.{Fs, SyncFile}
import org.scalatest.AsyncFreeSpec

import scala.concurrent.{ExecutionContext, Future}

trait ByDirectoryTest extends AsyncFreeSpec with FileAssertionTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  def fileExtensions: Seq[String]
  def ignoredFiles: Seq[String] = Seq(".ignore")

  def testFile(content: String, file: SyncFile, parent: String): Unit

  def forDirectory(dir: SyncFile, parent: String, mustHaveMarker: Boolean = true): Unit = {
    val (subDirs, files) =
      dir.list
        .filterNot(_ == "expected")
        .map(l => Fs.syncFile(s"${dir.path}${fs.separatorChar}$l"))
        .partition(_.isDirectory)
    val validFiles = files.filter(f =>
      fileExtensions.exists(fileExtension =>
        f.name.endsWith(fileExtension) || f.name.endsWith(fileExtension + ".ignore")))
    if (subDirs.nonEmpty || validFiles.nonEmpty) {
      s"in directory: ${dir.name}" - {
        subDirs.foreach(forDirectory(_, parent + dir.name + "/", mustHaveMarker))
        validFiles.foreach { f =>
          val content = f.read()
          if (content.toString.contains("*") || !mustHaveMarker) {
            if (f.name.endsWith(".ignore")) s"Golden: ${f.name}" ignore {
              Future.successful(succeed)
            } else {
              testFile(content.toString, f, parent)
            }
          } else Future.successful(succeed)
        }
      }
    }
  }
}
