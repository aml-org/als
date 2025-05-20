package org.mulesoft.als.suggestions.antlr.suggestor

import org.antlr.v4.runtime.misc.IntervalSet

import scala.collection.mutable

/**
 * A list of follow sets (for a given state number) + all of them combined for quick hit tests.
 * This data is static in nature (because the used ATN states are part of a static struct: the ATN).
 * Hence it can be shared between all C3 instances, however it depends on the actual parser class (type).
 */
class FollowSetsHolder {
  val sets: mutable.ListBuffer[FollowSetWithPath] = mutable.ListBuffer.empty
  val combined: IntervalSet = new IntervalSet()
}
