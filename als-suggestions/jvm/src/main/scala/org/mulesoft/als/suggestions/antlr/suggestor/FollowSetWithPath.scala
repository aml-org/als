package org.mulesoft.als.suggestions.antlr.suggestor

import org.antlr.v4.runtime.misc.IntervalSet
import org.mulesoft.als.suggestions.antlr.suggestor.{RuleList, TokenList}

/**
 * A record for a follow set along with the path at which this set was found.
 * If there is only a single symbol in the interval set then we also collect and store tokens which follow
 * this symbol directly in its rule (i.e. there is no intermediate rule transition). Only single maybeLabel transitions
 * are considered. This is useful if you have a chain of tokens which can be suggested as a whole, because there is
 * a fixed sequence in the grammar.
 */

class FollowSetWithPath {
  var intervals: IntervalSet = new IntervalSet()
  var path: RuleList = IndexedSeq.empty
  var following: TokenList = IndexedSeq.empty
}
