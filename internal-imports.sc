import java.io.{File, PrintWriter}
import java.nio.file.Files
import scala.collection.mutable

val alsPath = s"${System.getProperty("user.home")}/mulesoft/als"

def recursiveListFiles(f: File): Seq[File] = {
  val these = f.listFiles
  these.filter(_.getName.endsWith(".scala")) ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
}

def isInternal(l: String) =
  l.contains(".internal.") &&
    l.contains("import ") &&
    l.contains("amf.")

def flattenImports(str: String): Seq[String] =
  if (str.contains("{")) {
    val prefix = str.substring(0, str.indexOf("{"))
    val rest   = str.stripPrefix(prefix).stripPrefix("{").stripSuffix("}")
    rest
      .split(',')
      .map(_.trim)
      .filterNot(_.isBlank)
      .map(o => s"$prefix$o")
  } else Seq(str)

def getInternalImport(f: File): Seq[String] = {
  val acc: mutable.ListBuffer[String] = mutable.ListBuffer()
  Files.readAllLines(f.toPath).forEach(acc.append(_))
  acc
    .flatMap(flattenImports)
    .filter(l => isInternal(l))
}

val allImports = recursiveListFiles(new File(alsPath))
  .flatMap(getInternalImport)
  .distinct
  .sorted
  .mkString("\n")

new PrintWriter(s"$alsPath/internal-imports.txt") { write(allImports); close }
