package common.diff

import amf.core.utils.TSort
import amf.core.utils.TSort.tsort
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * [[TSort]] test
  */
class TSortTest extends FunSuite {

  test("Topological sort with edges") {
    val edges = List(
      ("a0", "b1"),
      ("a0", "b2"),
      ("b1", "c1"),
      ("b2", "c2"),
      ("a1", "b3"),
      ("b3", "c3"),
      ("b3", "c4")
    )
    tsort(edges) should be(List("a0", "a1", "b2", "b3", "b1", "c3", "c4", "c2", "c1"))
  }

  test("Topological sort with map") {
    val graph: Map[String, Set[String]] = Map(
      ("a0", Set()),
      ("b1", Set("a0")),
      ("b2", Set("a0")),
      ("c1", Set("b1")),
      ("c2", Set("b2")),
      ("a1", Set()),
      ("b3", Set("a1")),
      ("c3", Set("b3")),
      ("c4", Set("b3"))
    )
    tsort(graph, Seq()) should be(List("a0", "a1", "b2", "b3", "b1", "c3", "c4", "c2", "c1"))
  }
}
