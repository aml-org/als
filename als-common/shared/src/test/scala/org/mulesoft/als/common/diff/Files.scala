package org.mulesoft.als.common.diff

import java.io._
import java.util.regex.Pattern

/** Common utility classes to deal with Files and IO in general.
  */
object Files {

  /** Get the content of a <code>Reader</code> as a list of Strings, one entry per line. Returns an empty List if an
    * IOException is raised during reading
    */
  def readLines(input: Reader): List[String] =
    Option(input)
      .map(i => {
        try {
          val reader              = new BufferedReader(i)
          var lines: List[String] = List()
          var line: String        = reader.readLine()
          while (Option(line).isDefined) {
            lines = lines :+ line
            line = reader.readLine()
          }
          lines
        } catch {
          case _: IOException => List();
        }
      })
      .getOrElse(Nil)

  /** Get the content of a <code>File</code> as a list of Strings, one entry per line. Returns an empty List if an
    * IOException is raised during reading
    */
  def readLines(file: File): List[String] = {
    try {
      readLines(new FileReader(file))
    } catch {
      case _: FileNotFoundException => List()
    }
  }

  /** Recursively select all files that verify the specified pattern. */
  def list(root: File, pattern: String): List[String] = {
    val p: Pattern = Pattern.compile(pattern)
    val filter = new FileFilter {
      override def accept(f: File): Boolean = p.matcher(f.getPath).matches()
    }

    list(root, filter)
  }

  /** Recursively select all files that verify the specified condition. */
  def list(root: File, filter: FileFilter): List[String] = {
    list(List(), root, filter)
  }

  private def list(result: List[String], file: File, filter: FileFilter): List[String] = {
    if (file.isFile && filter.accept(file)) result +: file.getAbsolutePath
    else if (file.isDirectory) {
      val fileList: Array[String] = file.list()
      Option(fileList).foreach(l => l.foldLeft(result)((result, value) => list(result, new File(file, value), filter)))
    }
    result
  }
}
