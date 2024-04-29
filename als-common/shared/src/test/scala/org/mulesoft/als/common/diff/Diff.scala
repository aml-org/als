package org.mulesoft.als.common.diff

import org.mulesoft.als.common.diff.Diff.{Delta, Equals, PathNode}

import java.io.{File, PrintWriter, Reader, StringWriter}

/** Diff class to compare objects.
  */
class Diff[T](equalsComparator: Equals[T]) {

  /** Computes the difference between the 2 sequences and returns it as a List of Delta objects.
    */
  def diff(a: List[T], b: List[T]): List[Delta[T]] = {
    val path: PathNode = buildPath(a, b)
    buildRevision(path, a, b)
  }

  protected def extractSubList(a: List[T], from: Int, to: Int): List[T] = {
    a.slice(from, to)
  }

  def buildPath(a: List[T], b: List[T]): PathNode = {
    // these are local constants
    val aSize = a.size
    val bSize = b.size

    val max      = aSize + bSize + 1
    val size     = 1 + 2 * max
    val middle   = size / 2
    val diagonal = new Array[PathNode](size)

    diagonal(middle + 1) = PathNode.snake(0, -1, null)
    (0 until max).foreach(d => {
      (-d to d by 2).foreach(k => {
        val kMiddle        = middle + k
        val kPlus          = kMiddle + 1
        val kMinus         = kMiddle - 1
        var prev: PathNode = null

        var i = 0
        if ((k == -d) || (k != d && diagonal(kMinus).i < diagonal(kPlus).i)) {
          i = diagonal(kPlus).i
          prev = diagonal(kPlus)
        } else {
          i = diagonal(kMinus).i + 1
          prev = diagonal(kMinus)
        }

        var j = i - k

        var node = PathNode.diffNode(i, j, prev)

        // orig and rev are zero-based
        // but the algorithm is one-based
        // that's why there's no +1 when indexing the sequences
        while (i < aSize && j < bSize && equalsComparator.equal(a(i), b(j))) {
          i = i + 1
          j = j + 1
        }
        if (i > node.i) node = PathNode.snake(i, j, node)

        diagonal(kMiddle) = node

        if (i >= aSize && j >= bSize) return diagonal(kMiddle)
      })
    })
    null
  }

  /** Constructs a List of Deltas from a difference path. */
  private def buildRevision(originalPath: PathNode, a: List[T], b: List[T]): List[Delta[T]] = {
    var patch = List[Delta[T]]()

    var path: PathNode =
      Option(originalPath).map(op => if (op.isSnake) op.prev else op).orNull

    while (Option(path).isDefined && Option(path.prev).isDefined && path.prev.j >= 0) {
      if (path.isSnake) throw new IllegalStateException()

      val i = path.i
      val j = path.j

      path = path.prev

      val aSubList: List[T] = extractSubList(a, path.i, i)
      val bSubList: List[T] = extractSubList(b, path.j, j)
      if (aSubList.nonEmpty || bSubList.nonEmpty) patch = new Delta(path.i, aSubList, path.j, bSubList) +: patch

      if (path.isSnake) path = path.prev
    }
    patch
  }
}

object Diff {

  /** Creates a case insensitive diff for Strings. */
  def caseInsensitive: Diff.Str = {
    new Str(STRING_CASE_INSENSITIVE, false)
  }

  /** Creates a case sensitive diff for Strings. */
  def caseSensitive: Diff.Str = {
    new Str(STRING_EQUALS, false)
  }

  /** Create a differ with the specified comparator. */
  def apply[T](comparator: Equals[T]) = new Diff(comparator)

  /** Creates a diff Strings that ignore all space. */
  def ignoreAllSpace: Diff.Str = {
    new Str(STRING_EQUALS, true).ignoreSpaces
  }

  /** Convert all the list of deltas to a single string. */
  def makeString[T](deltas: Iterable[Delta[T]]): String = {
    val writer = new StringWriter()
    val out    = new PrintWriter(writer)
    deltas.foreach(_.print(out))
    writer.toString
      .replace("\r\n", "\n")
  }

  /** Create a differ with the specified String comparator. */
  def stringDiffer(comparator: Equals[String]): Str = {
    new Str(comparator, false)
  }

  /** Creates a case sensitive diff that trim Strings, before comparing them. */
  def trimming: Str = new Str(TRIM_COMPARATOR, false)

  private val STRING_CASE_INSENSITIVE: Equals[String] = new Equals[String]() {
    override def doEqualComparison(a: String, b: String): Boolean = {
      a equalsIgnoreCase b
    }
  }

  private val STRING_EQUALS = new Equals[String]()

  private val TRIM_COMPARATOR: Equals[String] = new Equals[String]() {
    override def doEqualComparison(a: String, b: String): Boolean = {
      a.trim equals b.trim
    }
  }

  class Delta[T](aPosition: Int, val aLines: List[T], bPosition: Int, val bLines: List[T]) {
    val t: Type = if (aLines.isEmpty) Add else if (bLines.isEmpty) Delete else Change

    /** Print the Delta using the standard Diff format.
      *
      * @param out
      *   The Print Stream to print the delta to
      */
    def print(out: PrintWriter): Unit = {
      if (t == Add) out.print(aPosition)
      else printRange(out, aPosition, aLines)
      out.print(t)
      if (t == Delete) out.print(bPosition)
      else printRange(out, bPosition, bLines)
      out.println()

      aLines.foreach((l: Any) => out.printf("< %s\n", l.asInstanceOf[Object]))
      if (t == Change) out.println("---")
      bLines.foreach((l: Any) => out.printf("> %s\n", l.asInstanceOf[Object]))
    }

    def fromList(value: Any): String = value match {
      case null       => "null"
      case l: List[_] => l.map(fromList).mkString("(", ", ", ")")
      case a: Any     => a.toString
    }

    override def toString: String = {
      Seq(aPosition, t, bPosition, fromList(aLines), fromList(bLines)).mkString("Diff.Delta(", ", ", ")")
    }

    def printRange(out: PrintWriter, pos: Int, lines: List[T]): Unit = {
      out.print(pos + 1)
      val n = lines.size
      if (n > 1) {
        out.print(',')
        out.print(pos + n)
      }
    }
  }

  class Equals[T] {

    /** Method that perform the actual comparison for equal between the 2 values You can override this method to
      * implement other type of comparisons (i.e. case insensitive, ignore spaces, etc)
      */
    def doEqualComparison(a: T, b: T): Boolean = {
      a equals b
    }

    def equal(a: T, b: T): Boolean = {
      a == b || Option(b).isDefined && doEqualComparison(a, b)
    }
  }

  class IgnoreSpace(val cmp: Equals[String]) extends Equals[String] {
    override def doEqualComparison(a: String, b: String): Boolean = {
      cmp.doEqualComparison(removeSpaces(a), removeSpaces(b))
    }

    def removeSpaces(a: String): String = {
      val strBuilder = new StringBuilder()
      for {
        c <- a
      } {
        if (!Character.isWhitespace(c)) {
          strBuilder.append(c)
        }
      }
      a.filter(!Character.isWhitespace(_))
      strBuilder.result()
    }
  }

  class PathNode(val i: Int, val j: Int, val prev: PathNode) {
    var snake: Boolean = false

    def isSnake: Boolean = snake

    def previousSnake: PathNode =
      if (i < 0 || j < 0) null else if (!snake && Option(prev).isDefined) prev.previousSnake else this
  }

  object PathNode {
    def snake(i: Int, j: Int, prev: PathNode): PathNode = {
      val node = new PathNode(i, j, prev)
      node.snake = true
      node
    }

    def diffNode(i: Int, j: Int, prev: PathNode): PathNode = {
      new PathNode(i, j, Option(prev).map(_.previousSnake).orNull)
    }
  }

  class Str(val equalsComparator: Equals[String], ignoreEmptyLine: Boolean) extends Diff[String](equalsComparator) {

    /** Computes the difference between the 2 strings split by end of line and returns it as a List of Delta objects.
      */
    def diff(a: String, b: String): List[Delta[String]] = diff(a.linesIterator.toList, b.linesIterator.toList)

    /** Computes the difference between the 2 Readers and returns it as a List of Delta objects.
      */
    def diff(a: Reader, b: Reader): List[Delta[String]] = {
      diff(Files.readLines(a), Files.readLines(b))
    }

    /** Computes the difference between the 2 Files and returns it as a List of Delta objects.
      */
    def diff(a: File, b: File): List[Delta[String]] = {
      diff(Files.readLines(a), Files.readLines(b))
    }

    /** Ignore empty lines. */
    def ignoreEmptyLines: Str = {
      new Str(equalsComparator, true)
    }

    /** Ignore empty lines. */
    def ignoreSpaces: Str = {
      new Str(new IgnoreSpace(equalsComparator), ignoreEmptyLine)
    }

    override protected def extractSubList(a: List[String], from: Int, to: Int): List[String] = {
      val ts: List[String] = super.extractSubList(a, from, to)
      if (ignoreEmptyLine) {
        if (ts.exists(_.nonEmpty)) ts else List()
      } else ts
    }
  }

  case class Type(str: String) {
    override def toString: String = str
  }

  object Add    extends Type("a")
  object Delete extends Type("d")
  object Change extends Type("c")
}
