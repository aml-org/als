package org.mulesoft.als.suggestions.antlr.suggestor

import org.antlr.v4.runtime.atn.ATNState

case class PipelineEntry(state: ATNState, tokenListIndex: Int)
