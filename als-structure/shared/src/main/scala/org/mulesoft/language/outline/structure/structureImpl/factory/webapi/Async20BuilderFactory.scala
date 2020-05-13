package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders.{
  AsyncPayloadFieldSymbolCompanion,
  BindingsArrayFieldCompanion,
  ChannelBindingsSymbolBuilderCompanion,
  MessageBindingsSymbolBuilderCompanion,
  OperationBindingsSymbolBuilderCompanion,
  ResponseSymbolBuilderCompanion,
  ResponsesArrayFieldCompanion,
  ServerBindingsSymbolBuilderCompanion
}

object Async20BuilderFactory extends EventedLikeBuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion +
      AsyncPayloadFieldSymbolCompanion +
      BindingsArrayFieldCompanion +
      OperationBindingsSymbolBuilderCompanion +
      MessageBindingsSymbolBuilderCompanion +
      ChannelBindingsSymbolBuilderCompanion +
      ServerBindingsSymbolBuilderCompanion +
      ResponseSymbolBuilderCompanion +
      ResponsesArrayFieldCompanion
}
