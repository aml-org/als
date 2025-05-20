package org.mulesoft.als.suggestions.antlr

import scala.collection.mutable

package object suggestor {
  // Token stream position info after a rule was processed.
  type RuleEndStatus = Set[Int]

  type TokenList = IndexedSeq[Int]
  type RuleList = IndexedSeq[Int]

  type PipelineEntryList = mutable.ListBuffer[PipelineEntry]
  type RuleWithStartTokenList = mutable.ListBuffer[RuleWithStartToken]
  type FollowSetsPerState = mutable.LinkedHashMap[Int, FollowSetsHolder]
}
