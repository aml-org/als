package org.mulesoft.als.common.diff

import java.io.{File, FileNotFoundException, FileReader, Reader}
import java.lang.System.getProperty
import java.net.{InetAddress, UnknownHostException}

import org.mulesoft.common.io.{AsyncFile, Utf8}
import org.scalatest.Matchers._
import org.scalatest.{Assertion, Succeeded}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import Diff.makeString

/** */
object Tests {

  private implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  /** Assert against a specified golden File. */
  def assertEquals(outFile: File, goldenFile: File): Boolean = {
    try {
      Diff.trimming.diff(new FileReader(outFile), new FileReader(goldenFile)).isEmpty
    } catch {
      case _: FileNotFoundException => false
    }
  }

  /** Assert that the field is not null and return it. */
  def assertNotNull[T](e: T): T = {
    e should not equal null
    e
  }

  /** Check against a specified golden File and fail. */
  def checkDiff(outFile: File, goldenFile: File): Unit = {
    val difference: Option[String] = diff(outFile, goldenFile)
    if (difference.isDefined) {
      println("DIFF!!")
      println(difference.get)
    }
    difference.map(s => fail(s))
  }

  def checkDiff(tuple: (String, String)): Assertion = tuple match {
    case (actual, expected) =>
      checkDiff(actual, expected)
      Succeeded
  }

  def checkDiffIgnoreAllSpaces(tuple: (String, String)): Assertion = tuple match {
    case (actual, expected) =>
      checkDiffIgnoreAllSpaces(actual, expected)
      Succeeded
  }

  /** Diff between 2 strings. */
  def checkDiff(actual: String, expected: String): Unit = {
    val diffs: List[Diff.Delta[String]] = Diff.trimming.ignoreEmptyLines.diff(actual, expected)
    if (diffs.nonEmpty) {
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      println(expected)
      println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
      println(actual)
      println("==============================================")
      fail("\n" + makeString(diffs))
    }
  }

  def checkDiffIgnoreAllSpaces(actual: String, expected: String): Unit = {
    val diffs: List[Diff.Delta[String]] = Diff.ignoreAllSpace.diff(actual, expected)
    if (diffs.nonEmpty) {
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      println(expected)
      println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
      println(actual)
      println("==============================================")
      fail("\n" + makeString(diffs))
    }
  }

  /** Check against a specified golden File. */
  def checkDiff(outFile: File, goldenFile: File, comparator: Diff.Equals[String]): Unit = {
    val diffs: List[Diff.Delta[String]] = Diff.stringDiffer(comparator).ignoreEmptyLines.diff(outFile, goldenFile)
    if (diffs.nonEmpty) fail("\ndiff -y -W 150 " + outFile + " " + goldenFile + "\n" + makeString(diffs))
  }

  /** Return optional diff with specified golden file. */
  def diff(outFile: File, goldenFile: File): Option[String] = {
    if (!goldenFile.exists())
      fail(
        String.format(
          "Cannot Open Golden File: \n%s\nfor comparing \n%s",
          goldenFile.getAbsolutePath,
          outFile.getAbsolutePath
        )
      )
    try {
      diff(new FileReader(outFile), outFile.toString, new FileReader(goldenFile), goldenFile.toString)
    } catch {
      case e: FileNotFoundException => fail("Cannot Open Target or Golden File: " + e.getMessage)
    }
    Option.empty
  }

  /** Get the files in the specified directory as parameters. */
  def listFiles(dirName: String, pattern: String): List[Array[_]] = {
    listFiles(new File(dirName), pattern)
  }

  /** Get the files in the specified directory as parameters. */
  def listFiles(dir: File, pattern: String): List[Array[_]] = {
    Files.list(dir, pattern).map(path => Array(new File(path)))
  }

  /** Return a random string with the specified length. */
  def randomString(length: Int): String = {
    val r: Random             = new Random()
    val result: StringBuilder = new StringBuilder()
    (0 until length).foreach(result.append(('a' + r.nextInt('z' - 'a')).toChar))
    result.toString
  }

  /** Sleep during the specified number of milliseconds. */
  def sleep(millis: Int): Unit = {
    System.out.printf("Sleeping %s ms....", millis.toString)
    Thread.sleep(millis)

    System.out.println("\r ")
  }

  /** Wrap single parameter as a List of objects (To be used by @Params in junit. */
  def wrapForParameters(args: Array[_]): List[Array[_]] = {
    args.map(arg => Array(arg)).toList
  }

  /** Returns the hostname. */
  def getHostName: String = {
    try {
      val host: String = InetAddress.getLocalHost.getHostName.replaceAll("-", "")
      host.substring(0, Math.min(index(host, '.'), Math.min(index(host, '_'), index(host, '-'))));
    } catch {
      case e: UnknownHostException => "localhost";
    }
  }

  def checkDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] = {
    def replaceEOL(s: String): String =
      s.replace("\r\n", "\n")

    a.read(encoding).zip(e.read(encoding)).map { case (actual, expected) =>
      val diffs = Diff.caseSensitive
        .diff(replaceEOL(actual.toString), replaceEOL(expected.toString))
      if (diffs.nonEmpty) {
        if (goldenOverride) {
          a.read(encoding).map(content => e.write(content.toString, encoding))
        } else {
          fail(s"\ndiff -y -W 150 $a $e \n\n${makeString(diffs)}")
        }
      }
      succeed
    }
  }

  /** Force golden override. */
  private def goldenOverride: Boolean = Option(getProperty("golden.override")).isDefined

  def checkDiff(a: String, fileA: String, b: String, fileB: String): Assertion = {
    val diffs: List[Diff.Delta[String]] = Diff.ignoreAllSpace.diff(a, b)
    if (diffs.nonEmpty) {
      println(s"A: $fileA")
      println(a)
      println("\n\n\n\n\n\n")
      println(s"B: $fileB")
      println(b)
      fail("\ndiff -y -W 150 " + trimFileProtocol(fileA) + " " + trimFileProtocol(fileB) + "\n" + makeString(diffs))
    }
    Succeeded
  }

  def diff(a: Reader, aName: String, b: Reader, bName: String): Option[String] = {
    val diffs: List[Diff.Delta[String]] = Diff.ignoreAllSpace.diff(a, b)
    if (diffs.isEmpty) Option.empty
    else Option("\ndiff -y -W 150 " + aName + " " + bName + "\n" + makeString(diffs))
  }

  private def index(s: String, chr: Char): Int = {
    val n: Int = s.indexOf(chr)
    if (n <= 0) s.length() else n
  }

  private def trimFileProtocol(name: String) = name.stripPrefix("file://").stripPrefix("file:/")

  // ~ Inner Classes ................................................................................................................................

  private class GoldenTest(val outputFile: File, val goldenFile: File) {

    /** Check the output against the golden file. */
    def check(): Unit = {
      checkDiff(outputFile, goldenFile)
    }
  }

  private object GoldenTest {
    def apply(testFile: File, outputDirName: String): GoldenTest = {
      val name: String     = testFile.getName
      val outputFile: File = createOutputFile(outputDirName, name)
      val dot: Int         = name.lastIndexOf('.')
      val bareName: String = if (dot == -1) name else name.substring(0, dot)
      val goldenFile: File = new File(testFile.getParent, bareName + ".golden")
      new GoldenTest(outputFile, goldenFile)
    }

    def apply(testName: String, outputDirName: String, goldenDirName: String): GoldenTest = {
      new GoldenTest(createOutputFile(outputDirName, testName), new File(goldenDirName, testName))
    }

    def createOutputFile(outputDirName: String, name: String): File = {
      val outputDir: File = new File(outputDirName)
      outputDir.mkdirs()
      new File(outputDir, name)
    }
  }
}
