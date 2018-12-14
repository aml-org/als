package org.mulesoft.language.server.js

import CustomPicklerConfig.{macroRW, ReadWriter => RW}
import org.mulesoft.language.common.logger.{ILoggerSettings, MessageSeverity => SharedMessageSeverity}
import org.mulesoft.language.common.dtoTypes.{IChangedDocument => SharedChangedDocument, IOpenedDocument => SharedOpenDocument, IRange => SharedRange, IStructureReport => SharedStructureReport, ITextEdit => SharedTextEdit, IValidationIssue => SharedValidationIssue, IValidationReport => SharedValidationReport}
import org.mulesoft.language.outline.structure.structureInterfaces.{StructureNodeJSON => SharedStructureNode}

//import upickle.default.{ReadWriter => RW, macroRW}

/**
  * Tag for potential payloads, in order to serialize/deserialize to JSON
  */
sealed trait ProtocolMessagePayload {

}

object ProtocolMessagePayload {
  implicit def rw: RW[ProtocolMessagePayload] = macroRW

  //  implicit val readWriter: RW[ProtocolMessagePayload] = RW.merge(
  //    macroRW[OpenedDocument], macroRW[ChangedDocument]
  //  )
}

/**
  * Document being opened.
  */
case class OpenedDocument (

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

) extends ProtocolMessagePayload
{
}



object OpenedDocument {
  implicit def rw: RW[OpenedDocument] = macroRW

  implicit def transportToShared(
    from: OpenedDocument): SharedOpenDocument = {

    SharedOpenDocument(
      from.uri,
      from.version,
      from.text
    )
  }

//  implicit def sharedToTransport(
//    from: SharedOpenDocument): OpenedDocument = {
//
//    val genFrom = Generic[SharedOpenDocument]
//    val genTo = Generic[OpenedDocument]
//
//    genTo.from(genFrom.to(from))
//  }
}

/**
  * Document being opened.
  */
case class ChangedDocument (

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
    var text: Option[String],

    /**
      * Optional set of text edits instead of complete text replacement.
      * Is only taken into account if text is null.
      */
    var textEdits: Option[Seq[TextEdit]]

  ) extends ProtocolMessagePayload
{
}

object ChangedDocument {
  implicit def rw: RW[ChangedDocument] = macroRW

  implicit def transportToShared(
    from: ChangedDocument): SharedChangedDocument = {

    SharedChangedDocument(
      from.uri,
      from.version,
      from.text,
      if(from.textEdits.isDefined)
        Some(from.textEdits.get.map(edit=>TextEdit.transportToShared(edit)))
      else None
    )
  }

//  implicit def sharedToTransport(
//    from: SharedChangedDocument): ChangedDocument = {
//
//    val genFrom = Generic[SharedChangedDocument]
//    val genTo = Generic[ChangedDocument]
//
//    genTo.from(genFrom.to(from))
//  }
}

/**
  * Validation report.
  */
case class ValidationReport (

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
{

}

object ValidationReport {
  implicit def rw: RW[ValidationReport] = macroRW

//  implicit def transportToShared(
//    from: ValidationReport): SharedValidationReport = {
//
//    val genFrom = Generic[ValidationReport]
//    val genTo = Generic[SharedValidationReport]
//
//    genTo.from(genFrom.to(from))
//  }

  implicit def sharedToTransport(
    from: SharedValidationReport): ValidationReport = {

    ValidationReport(
      from.pointOfViewUri,
      from.version,
      from.issues.map(issue=>ValidationIssue.sharedToTransport(issue))
    )
  }
}

/**
  * Validation issue: error or warning
  */
case class ValidationIssue (

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
  var range: Range,

  /**
    * Subsequent validation issues
    */
  var trace: Seq[ValidationIssue]
)
{
}

object ValidationIssue {
  implicit def rw: RW[ValidationIssue] = macroRW

  implicit def sharedToTransport(
    from: SharedValidationIssue): ValidationIssue = {

    ValidationIssue(
      from.code,
      from.`type`,
      from.filePath,
      from.text,
      from.range,
      from.trace.map(issue=>ValidationIssue.sharedToTransport(issue))
    )
  }
}

/**
  * Single text edit in a document.
  */
case class TextEdit (

  /**
    * Range to replace. Range start==end==0 => insert into the beginning of the document,
    * start==end==document end => insert into the end of the document
    */
  var range: Range,

  /**
    * Text to replace given range with.
    */
  var text: String
)
{
}

object TextEdit {
  implicit def rw: RW[TextEdit] = macroRW

  implicit def transportToShared(
                                  from: TextEdit): SharedTextEdit = {

    SharedTextEdit(
      from.range,
      from.text
    )
  }

  implicit def sharedToTransport(
                                  from: SharedTextEdit): TextEdit = {

    TextEdit(
      from.range,
      from.text
    )
  }
}

/**
  * Range in the document.
  */
case class Range (

  /**
    * Range start position, counting from 0
    */
  var start: Int,

  /**
    * Range end position, counting from 0
    */
  var end: Int
)
{
}

object Range {
  implicit def rw: RW[Range] = macroRW

  implicit def transportToShared(
    from: Range): SharedRange = {

    SharedRange(
      from.start,
      from.end
    )
  }

  implicit def sharedToTransport(
    from: SharedRange): Range = {

    Range(
      from.start,
      from.end
    )
  }
}

/**
  * Report for document structure.
  */
case class StructureReport (

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
  var structure: Map[String, StructureNode]
) extends ProtocolMessagePayload
{

}

object StructureReport {
  implicit def rw: RW[StructureReport] = macroRW


  implicit def sharedToTransport(
    from: SharedStructureReport): StructureReport = {

    StructureReport(
      from.uri,
      from.version,
      from.structure.map{case (key, value) => (key, StructureNode.sharedToTransport(value))}
    )
  }
}

case class StructureNode (
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
{

}

object StructureNode {
  implicit def rw: RW[StructureNode] = macroRW


  implicit def sharedToTransport(
    from: SharedStructureNode): StructureNode = {

    StructureNode(
      from.text,
      from.typeText,
      from.icon,
      from.textStyle,
      from.key,
      from.start,
      from.end,
      from.selected,
      from.children.map(child=>StructureNode.sharedToTransport(child)),
      from.category
    )
  }
}

/**
  * Request from client to server to obtain structure
  * @param wrapped
  */
case class GetStructureRequest (
   /**
     * Url
     */
   wrapped: String

) extends ProtocolMessagePayload
{
}

object GetStructureRequest {
  implicit def rw: RW[GetStructureRequest] = macroRW
}

/**
  * Request from client to server to obtain structure
  * @param wrapped
  */
case class GetStructureResponse (

 /**
   * Document structure.
   */
 wrapped: Map[String, StructureNode]

) extends ProtocolMessagePayload
{
}

object GetStructureResponse {
  implicit def rw: RW[GetStructureResponse] = macroRW
}

/**
  * Logger configuration / settings
  */
case class LoggerSettings (

   /**
     * If true, disables all logging.
     */
   var disabled: Option[Boolean],

   /**
     * List of components, which are allowed to appear in log.
     * If empty or absent, all components are allowed (except those excplicitly denied).
     */
   var allowedComponents: Option[Seq[String]],

   /**
     * Components, which never appear in the log
     */
   var deniedComponents: Option[Seq[String]],

   /**
     * Messages with lower severity will not appear in log.
     */
   var maxSeverity: Option[Int],

   /**
     * Messages having more length will be cut off to this number.
     */
   var maxMessageLength: Option[Int]
 )
{

}

object LoggerSettings {
  implicit def rw: RW[LoggerSettings] = macroRW

  def transportToShared(
    from: LoggerSettings): ILoggerSettings = {

    new ILoggerSettings() {
      var disabled = from.disabled
      var allowedComponents = from.allowedComponents
      var deniedComponents = from.deniedComponents
      var maxSeverity = if(from.maxSeverity.isDefined) Some(MessageSeverity.sharedToTransport(from.maxSeverity.get)) else None
      var maxMessageLength = from.maxMessageLength
    }
  }
}

object MessageSeverity {


  implicit def sharedToTransport(
    from: Int): SharedMessageSeverity.Value = {

    from match {
      case 0 => SharedMessageSeverity.DEBUG_DETAIL
      case 1 => SharedMessageSeverity.DEBUG
      case 2 => SharedMessageSeverity.DEBUG_OVERVIEW
      case 3 => SharedMessageSeverity.WARNING
      case 4 => SharedMessageSeverity.ERROR
      case _ => SharedMessageSeverity.DEBUG
    }
  }

}