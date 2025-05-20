package org.mulesoft.als.suggestions.antlr.grpc

import amf.core.internal.remote.{GrpcProtoHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class GRPCByDirectoryTest extends SuggestionByDirectoryTest {

  override def basePath: String = "als-suggestions/jvm/src/test/resources/grpc/by-directory"

  override def origin: Hint = GrpcProtoHint

  override def fileExtensions: Seq[String] = Seq(".proto")
}
