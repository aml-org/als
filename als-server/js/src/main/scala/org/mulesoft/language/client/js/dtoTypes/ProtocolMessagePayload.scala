package org.mulesoft.language.client.js.dtoTypes

import org.mulesoft.als.suggestions.interfaces.{Suggestion => SharedSuggestion}
import org.mulesoft.language.client.js.CustomPicklerConfig.{macroRW, ReadWriter => RW}
import org.mulesoft.language.common.dtoTypes.{
  ChangedDocument => SharedChangedDocument,
  IChangedPosition => SharedChangedPosition,
  IFindRequest => SharedFindRequest,
  ILocation => SharedLocation,
  OpenedDocument => SharedOpenDocument,
  Range => SharedRange,
  StructureReport => SharedStructureReport,
  TextEdit => SharedTextEdit,
  ValidationIssue => SharedValidationIssue,
  ValidationReport => SharedValidationReport
}
import org.mulesoft.language.common.logger.{
  LoggerSettings => SharedLoggerSettings,
  MessageSeverity => SharedMessageSeverity
}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureInterfaces.{StructureNodeJSON => SharedStructureNode}
import common.dtoTypes.{Position => SharedPosition, PositionRange => SharedPositionRange}

import scala.language.implicitConversions

/**
  * Tag for potential payloads, in order to serialize/deserialize to JSON
  */
sealed trait ProtocolMessagePayload {}

object ProtocolMessagePayload {
  implicit def rw: RW[ProtocolMessagePayload] = macroRW
}

/**
  * Document being opened.
  */
case class OpenedDocument(
    /**
      * Document URI
      */
    var uri: String,
    /**
      * Optional document version.
      */
    var version: Int,
    /**
      * Optional document content
      */
    var text: String
) extends ProtocolMessagePayload {}

object OpenedDocument {
  implicit def rw: RW[OpenedDocument] = macroRW

  implicit def transportToShared(from: OpenedDocument): SharedOpenDocument =
    SharedOpenDocument(from.uri, from.version, from.text)
}

case class FindDeclarationRequest(var uri: String, var position: Int) extends ProtocolMessagePayload

object FindDeclarationRequest {
  implicit def rw: RW[FindDeclarationRequest] = macroRW

  implicit def transportToShared(from: FindDeclarationRequest): SharedFindRequest =
    SharedFindRequest(from.uri, from.position)
}

case class FindReferencesRequest(var uri: String, var position: Int) extends ProtocolMessagePayload

object FindReferencesRequest {
  implicit def rw: RW[FindReferencesRequest] = macroRW

  implicit def transportToShared(from: FindReferencesRequest): SharedFindRequest =
    SharedFindRequest(from.uri, from.position)
}

case class Location(var uri: String, var range: Range, version: Int)

object Location {
  implicit def rw: RW[Location] = macroRW

  implicit def sharedToTransport(from: SharedLocation): Location =
    Location(from.uri, Range(from.range.start, from.range.end), from.version)
}

case class ClosedDocument(var wrapped: String) extends ProtocolMessagePayload

object ClosedDocument {
  implicit def rw: RW[ClosedDocument] = macroRW
}

/**
  * Document being opened.
  */
case class ChangedDocument(
    /**
      * Document URI
      */
    var uri: String,
    /**
      * Optional document version.
      */
    var version: Int,
    /**
      * Optional document content
      */
    var text: Option[String]

    /**
      * Optional set of text edits instead of complete text replacement.
      * Is only taken into account if text is null.
      */
    //var textEdits: Option[Seq[TextEdit]]
) extends ProtocolMessagePayload {}

object ChangedDocument {
  implicit def rw: RW[ChangedDocument] = macroRW

  implicit def transportToShared(from: ChangedDocument): SharedChangedDocument = {
    SharedChangedDocument(from.uri, from.version, from.text, None)
  }
}

case class ChangedPosition(var uri: String, var position: Int) extends ProtocolMessagePayload

object ChangedPosition {
  implicit def rw: RW[ChangedPosition] = macroRW

  implicit def transportToShared(from: ChangedPosition): SharedChangedPosition =
    SharedChangedPosition(from.uri, from.position)
}

/**
  * Validation report.
  */
case class ValidationReport(
    /**
      * This is the "point of view" uri, actual reported unit paths are located
      * in the particular issues.
      */
    var pointOfViewUri: String,
    /**
      * Optional document version of the point of view.
      */
    var version: Int,
    /**
      * Validation issues.
      */
    var issues: Seq[ValidationIssue]
) extends ProtocolMessagePayload

object ValidationReport {
  implicit def rw: RW[ValidationReport] = macroRW

  implicit def sharedToTransport(from: SharedValidationReport): ValidationReport = {
    ValidationReport(
      from.pointOfViewUri,
      from.version,
      from.issues.map(issue => ValidationIssue.sharedToTransport(issue))
    )
  }
}

/**
  * Validation issue: error or warning
  */
case class ValidationIssue(
    /**
      * Error code
      */
    var code: String,
    /**
      * Error type.
      */
    var `type`: String,
    /**
      * Document uri. Legacy: to be renamed to uri.
      */
    var filePath: String,
    /**
      * Issue human-readable text.
      */
    var text: String,
    /**
      * Range producing the issue.
      */
    var range: PositionRange,
    /**
      * Subsequent validation issues
      */
    var trace: Seq[ValidationIssue]
)

object ValidationIssue {
  implicit def rw: RW[ValidationIssue] = macroRW

  implicit def sharedToTransport(from: SharedValidationIssue): ValidationIssue =
    ValidationIssue(
      from.code,
      from.`type`.toString,
      from.filePath,
      from.text,
      from.range,
      from.trace.map(issue => ValidationIssue.sharedToTransport(issue))
    )
}

case class Position(line: Int, column: Int)

object Position {
  implicit def rw: RW[Position] = macroRW

  implicit def convert(position: Position): SharedPosition = SharedPosition(position.line, position.column)

  implicit def convert(position: SharedPosition): Position = Position(position.line, position.column)
}

case class PositionRange(start: Position, end: Position)

object PositionRange {
  implicit def rw: RW[PositionRange] = macroRW

  implicit def convert(range: PositionRange): SharedPositionRange = SharedPositionRange(range.start, range.end)

  implicit def convert(range: SharedPositionRange): PositionRange = PositionRange(range.start, range.end)
}

/**
  * Single text edit in a document.
  */
case class TextEdit(
    /**
      * Range to replace. Range start==end==0 => insert into the beginning of the document,
      * start==end==document end => insert into the end of the document
      */
    var range: Range,
    /**
      * Text to replace given range with.
      */
    var text: String
) {}

object TextEdit {
  implicit def rw: RW[TextEdit] = macroRW

  implicit def transportToShared(from: TextEdit): SharedTextEdit = SharedTextEdit(from.range, from.text)

  implicit def sharedToTransport(from: SharedTextEdit): TextEdit = TextEdit(from.range, from.text)
}

/**
  * Range in the document.
  */
case class Range(
    /**
      * Range start position, counting from 0
      */
    var start: Int,
    /**
      * Range end position, counting from 0
      */
    var end: Int
)

object Range {
  implicit def rw: RW[Range] = macroRW

  implicit def transportToShared(from: Range): SharedRange = SharedRange(from.start, from.end)

  implicit def sharedToTransport(from: SharedRange): Range = Range(from.start, from.end)
}

case class DocumentSymbolPosition(line: Int, character: Int)
object DocumentSymbolPosition {

  implicit def rw: RW[DocumentSymbolPosition] = macroRW

  implicit def sharedToTransport(from: Position): DocumentSymbolPosition = {
    DocumentSymbolPosition(from.line, from.column)
  }
}
case class DocumentSymbolRange(start: DocumentSymbolPosition, end: DocumentSymbolPosition)

object DocumentSymbolRange {

  implicit def rw: RW[DocumentSymbolRange] = macroRW

  implicit def sharedToTransport(from: PositionRange): DocumentSymbolRange = {

    DocumentSymbolRange(
      from.start,
      from.end
    )
  }
}

case class DocumentSymbolNode(name: String,
                              //                              detail: Option[String],
                              kind: Int,
                              deprecated: Boolean,
                              range: DocumentSymbolRange,
                              selectionRange: DocumentSymbolRange,
                              children: Seq[DocumentSymbolNode])

object DocumentSymbolNode {

  implicit def rw: RW[DocumentSymbolNode] = macroRW

  implicit def sharedToTransport(from: DocumentSymbol): DocumentSymbolNode = {

    val result = DocumentSymbolNode(
      from.name,
      from.kind.index,
      from.deprecated,
      DocumentSymbolRange.sharedToTransport(from.range),
      DocumentSymbolRange.sharedToTransport(from.selectionRange),
      from.children.map(child => DocumentSymbolNode.sharedToTransport(child))
    )
    result
  }
}

/**
  * Report for document structure.
  */
case class StructureReport(
    /**
      * Document uri.
      */
    var uri: String,
    /**
      * Optional document version.
      */
    var version: Int,
    /**
      * Document structure.
      */
    var structure: List[DocumentSymbolNode]
) extends ProtocolMessagePayload {}

object StructureReport {
  implicit def rw: RW[StructureReport] = macroRW

  implicit def sharedToTransport(from: SharedStructureReport): StructureReport =
    StructureReport(
      from.uri,
      from.version,
      from.structure.map(DocumentSymbolNode.sharedToTransport)
    )
}

case class StructureNode(
    /**
      * Node label text to be displayed.
      */
    text: String,
    /**
      * Node type label, if any.
      */
    typeText: Option[String],
    /**
      * Node icon. Structure module is not setting up, how icons are represented in the client
      * system, or what icons exist,
      * instead the client is responsible to configure the mapping from nodes to icon identifiers.
      */
    icon: String,
    /**
      * Text style of the node. Structure module is not setting up, how text styles are represented in the client
      * system, or what text styles exist,
      * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
      */
    textStyle: String,
    /**
      * Unique node identifier.
      */
    key: String,
    /**
      * Node start position from the beginning of the document.
      */
    start: Int,
    /**
      * Node end position from the beginning of the document.
      */
    end: Int,
    /**
      * Whether the node is selected.
      */
    selected: Boolean,
    /**
      * Node children.
      */
    children: Seq[StructureNode],
    /**
      * Node category, if determined by a category filter.
      */
    category: String
)

object StructureNode {
  implicit def rw: RW[StructureNode] = macroRW

  implicit def sharedToTransport(from: SharedStructureNode): StructureNode =
    StructureNode(
      from.text,
      from.typeText,
      from.icon,
      from.textStyle,
      from.key,
      from.start,
      from.end,
      from.selected,
      from.children.map(child => StructureNode.sharedToTransport(child)),
      from.category
    )
}

/**
  * Request from client to server to obtain structure
  *
  * @param wrapped Url
  */
case class GetStructureRequest(wrapped: String) extends ProtocolMessagePayload

object GetStructureRequest {
  implicit def rw: RW[GetStructureRequest] = macroRW
}

/**
  * Request from client to server to obtain completion
  *
  * @param uri      Url
  * @param position Completion position
  *
  */
case class GetCompletionRequest(uri: String, position: Position) extends ProtocolMessagePayload

object GetCompletionRequest {
  implicit def rw: RW[GetCompletionRequest] = macroRW
}

/**
  * Request from client to server to obtain structure
  *
  * @param wrapped Document structure.
  */
case class GetStructureResponse(wrapped: Seq[DocumentSymbolNode]) extends ProtocolMessagePayload

object GetStructureResponse {
  implicit def rw: RW[GetStructureResponse] = macroRW
}

/**
  * Suggestion item for response
  *
  * @param text        Full text to insert, including the index.
  * @param description Description of the suggestion.
  * @param displayText Text to display.
  * @param prefix      Detected suggestion prefix.
  * @param category    Suggestion category.
  */
case class Suggestion(text: String,
                      description: Option[String],
                      displayText: Option[String],
                      prefix: Option[String],
                      category: Option[String])
    extends ProtocolMessagePayload

object Suggestion {
  implicit def rw: RW[Suggestion] = macroRW

  implicit def sharedToTransport(from: SharedSuggestion): Suggestion =
    Suggestion(from.text,
               Option(from.description),
               Option(from.displayText),
               Option(from.prefix),
               Option(from.category))
}

case class LocationsResponse(wrapped: Seq[Location]) extends ProtocolMessagePayload

object LocationsResponse {
  implicit def rw: RW[LocationsResponse] = macroRW
}

case class ClientPathRequest(wrapped: String) extends ProtocolMessagePayload

object ClientPathRequest {
  implicit def rw: RW[ClientPathRequest] = macroRW
}

case class ClientBoolResponse(wrapped: Boolean) extends ProtocolMessagePayload

object ClientBoolResponse {
  implicit def rw: RW[ClientBoolResponse] = macroRW
}

case class ClientStringResponse(wrapped: String) extends ProtocolMessagePayload

object ClientStringResponse {
  implicit def rw: RW[ClientStringResponse] = macroRW
}

case class ClientStringSeqResponse(wrapped: Seq[String]) extends ProtocolMessagePayload

object ClientStringSeqResponse {
  implicit def rw: RW[ClientStringSeqResponse] = macroRW
}

/**
  * Logger configuration / settings
  */
case class LoggerSettings(
    /**
      * If true, disables all logging.
      */
    //var disabled: Option[Boolean],

    /**
      * List of components, which are allowed to appear in log.
      * If empty or absent, all components are allowed (except those excplicitly denied).
      */
    var allowedComponents: Option[Seq[String]],
    /**
      * Components, which never appear in the log
      */
    //var deniedComponents: Option[Seq[String]],

    /**
      * Messages with lower severity will not appear in log.
      */
    var maxSeverity: Option[Int],
    /**
      * Messages having more length will be cut off to this number.
      */
    var maxMessageLength: Option[Int]
) extends ProtocolMessagePayload

object LoggerSettings {
  implicit def rw: RW[LoggerSettings] = macroRW

  def transportToShared(from: LoggerSettings): SharedLoggerSettings =
    new SharedLoggerSettings() {
      var disabled: Option[Boolean]              = None.asInstanceOf[Option[Boolean]]
      var allowedComponents: Option[Seq[String]] = from.allowedComponents
      var deniedComponents: Option[Seq[String]]  = None.asInstanceOf[Option[Seq[String]]]
      var maxSeverity: Option[SharedMessageSeverity.Value] =
        if (from.maxSeverity.isDefined) Some(MessageSeverity.sharedToTransport(from.maxSeverity.get)) else None
      var maxMessageLength: Option[Int] = from.maxMessageLength
    }
}

object MessageSeverity {
  implicit def sharedToTransport(from: Int): SharedMessageSeverity.Value = from match {
    case 0 => SharedMessageSeverity.DEBUG_DETAIL
    case 1 => SharedMessageSeverity.DEBUG
    case 2 => SharedMessageSeverity.DEBUG_OVERVIEW
    case 3 => SharedMessageSeverity.WARNING
    case 4 => SharedMessageSeverity.ERROR
    case _ => SharedMessageSeverity.DEBUG
  }
}
