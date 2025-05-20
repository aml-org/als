package org.mulesoft.als.server.modules.completion.grpc

import org.mulesoft.als.server.modules.completion.ServerSuggestionsTest

trait GRPCSuggestionTestServer extends ServerSuggestionsTest {

  def rootPath: String = "suggestions/grpc"

  def format: String = "GRPC"
}
