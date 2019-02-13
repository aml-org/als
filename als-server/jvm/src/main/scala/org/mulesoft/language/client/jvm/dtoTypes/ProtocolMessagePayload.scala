package org.mulesoft.language.client.jvm.dtoTypes

import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.common.dtoTypes.Position
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON

sealed trait ProtocolMessagePayload

case class GetStructureRequest(url: String) extends ProtocolMessagePayload

case class GetStructureResponse(structure: List[DocumentSymbol]) extends ProtocolMessagePayload

case class GetCompletionRequest(uri: String, position: Position) extends ProtocolMessagePayload

object GetStructureRequest

object GetStructureResponse

object GetCompletionRequest
