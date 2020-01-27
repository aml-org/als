package org.mulesoft.als.common.diff

import amf.core.metamodel.Field
import amf.core.model.domain.{AmfObject, Annotation}
import org.scalatest._

/**
  *
  */
trait AmfObjectTestMatcher extends Assertions {

  /** [[AmfObject]] matcher ignoring all [[Annotation]]s */
  case class AmfObjectMatcher(expected: AmfObject) {

    def assert(actual: AmfObject): Unit = {
      if (actual.fields.size != expected.fields.size) {
        println(s"HERE ${actual.fields.size}")
        actual.fields.foreach(println(_))
        println("-----")
        println(s"THERE ${expected.fields.size}")
        expected.fields.foreach(println(_))
        fail(s"Expected ${expected.fields} but ${actual.fields} fields have different sizes")
      }

      expected.fields.foreach({
        case (field, _) =>
          val a: Any = actual.fields.raw(field).orNull
          val e: Any = expected.fields.raw(field).orNull
          assertRaw(field, a, e)
      })
    }

    private def assertRaw(field: Field, a: Any, e: Any): Unit = {
      e match {
        case _: String | _: Boolean | _: Integer =>
          if (a != e) {
            fail(s"Expected scalar '$e' but '$a' found for $field")
          }
        case obj: AmfObject => AmfObjectMatcher(obj).assert(a.asInstanceOf[AmfObject])
        case values: Seq[_] =>
          val other = a.asInstanceOf[Seq[_]]

          if (values.size != other.size) {
            fail(s"Expected $values but $other fields have different sizes")
          }

          values
            .zip(other)
            .foreach({
              case (exp, act) => assertRaw(field, act, exp)
            })
      }
    }
  }
}
