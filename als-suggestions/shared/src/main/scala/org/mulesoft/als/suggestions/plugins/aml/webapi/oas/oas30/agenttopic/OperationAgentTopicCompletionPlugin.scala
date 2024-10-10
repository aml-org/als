package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.agenttopic

import amf.apicontract.internal.metamodel.domain.OperationModel
import org.mulesoft.als.suggestions._


object OperationAgentTopicCompletionPlugin extends AgentTopicCompletionPlugin {
  override def id: String = "OperationAgentTopicCompletionPlugin"

  override protected val targetIri: String = OperationModel.`type`.head.iri()

  override protected def structure: StructureSuggestionNode =
    StructureSuggestionNode("x-sfdc",
      Seq(StructureSuggestionNode("genai",
        Seq(
          StructureSuggestionNode("isAction", Seq(
            StructureSuggestionNode("true", Seq.empty, range = BoolScalarRange, isKey = false),
            StructureSuggestionNode("false", Seq.empty, range = BoolScalarRange,isKey = false),
          ), BoolScalarRange),
          StructureSuggestionNode("instructions", Seq.empty, StringScalarRange)
        )
      )
    )
  )

}

