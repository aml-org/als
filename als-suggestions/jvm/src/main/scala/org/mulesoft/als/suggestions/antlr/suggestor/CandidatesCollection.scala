package org.mulesoft.als.suggestions.antlr.suggestor

import scala.collection.mutable

class CandidatesCollection {
  val tokens = new mutable.HashMap[Int, (Option[String], TokenList)]()
  val rules = new mutable.HashMap[Int, CandidateRule]()

  def clear(): Unit = {
    rules.clear()
    tokens.clear()
  }
}
