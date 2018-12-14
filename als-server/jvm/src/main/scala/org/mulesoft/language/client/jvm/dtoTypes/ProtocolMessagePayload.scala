package org.mulesoft.language.client.jvm.dtoTypes

import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON;

sealed trait ProtocolMessagePayload;

case class GetStructureRequest(url: String) extends ProtocolMessagePayload;

case class GetStructureResponse(structure: Map[String, StructureNodeJSON]) extends ProtocolMessagePayload;

case class GetCompletionRequest(uri: String, position: Int) extends ProtocolMessagePayload;

object GetStructureRequest;

object GetStructureResponse;

object GetCompletionRequest;
