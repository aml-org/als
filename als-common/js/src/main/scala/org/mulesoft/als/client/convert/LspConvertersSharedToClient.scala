package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.common.{
  ClientLocationLink,
  ClientPosition,
  ClientRange,
  ClientTextDocumentIdentifier,
  ClientTextDocumentItem,
  ClientTextDocumentPositionParams,
  ClientVersionedTextDocumentIdentifier
}
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbolClientCapabilities,
  ClientSymbolKindClientCapabilities
}
import org.mulesoft.lsp.command.Command
import org.mulesoft.lsp.common.{
  LocationLink,
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolClientCapabilities, SymbolKindClientCapabilities}

import scala.language.implicitConversions

object LspConvertersSharedToClient {

  implicit class ClientSymbolKindClientCapabilitiesConverter(v: SymbolKindClientCapabilities) {
    def toClient: ClientSymbolKindClientCapabilities =
      ClientSymbolKindClientCapabilities(v)
  }

  implicit class ClientDocumentSymbolClientCapabilitiesConverter(v: DocumentSymbolClientCapabilities) {
    def toClient: ClientDocumentSymbolClientCapabilities =
      ClientDocumentSymbolClientCapabilities(v)
  }

  implicit class ClientCommandConverter(v: Command) {
    def toClient: ClientCommand =
      ClientCommand(v)
  }

  implicit class ClientLocationLinkConverter(v: LocationLink) {
    def toClient: ClientLocationLink =
      ClientLocationLink(v)
  }

  implicit class ClientPositionConverter(v: Position) {
    def toClient: ClientPosition =
      ClientPosition(v)
  }

  implicit class ClientRangeConverter(v: Range) {
    def toClient: ClientRange =
      ClientRange(v)
  }

  implicit class ClientTextDocumentIdentifierConverter(v: TextDocumentIdentifier) {
    def toClient: ClientTextDocumentIdentifier =
      ClientTextDocumentIdentifier(v)
  }

  implicit class ClientVersionedTextDocumentIdentifierConverter(v: VersionedTextDocumentIdentifier) {
    def toClient: ClientVersionedTextDocumentIdentifier =
      ClientVersionedTextDocumentIdentifier(v)
  }

  implicit class ClientTextDocumentItemConverter(v: TextDocumentItem) {
    def toClient: ClientTextDocumentItem =
      ClientTextDocumentItem(v)
  }

  implicit class ClientTextDocumentPositionParamsConverter(v: TextDocumentPositionParams) {
    def toClient: ClientTextDocumentPositionParams =
      ClientTextDocumentPositionParams(v)
  }
}
