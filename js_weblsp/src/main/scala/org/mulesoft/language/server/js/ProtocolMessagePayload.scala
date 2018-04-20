package org.mulesoft.language.server.js

import CustomPicklerConfig.{macroRW, ReadWriter => RW}
import org.mulesoft.language.common.logger.{ILoggerSettings, MessageSeverity => SharedMessageSeverity}
import org.mulesoft.language.common.typeInterfaces.{IChangedPosition => SharedChangedPosition, IChangedDocument => SharedChangedDocument, IOpenedDocument => SharedOpenDocument, IRange => SharedRange, IStructureReport => SharedStructureReport, ITextEdit => SharedTextEdit, IValidationIssue => SharedValidationIssue, IValidationReport => SharedValidationReport, StructureNode => SharedStructureNode}

sealed trait ProtocolMessagePayload;

object ProtocolMessagePayload {
	implicit def rw: RW[ProtocolMessagePayload] = macroRW;
}

case class OpenedDocument(var uri: String, var version: Int, var text: String) extends ProtocolMessagePayload;

case class ChangedDocument(var uri: String, var version: Int, var text: Option[String], var textEdits: Option[Seq[TextEdit]]) extends ProtocolMessagePayload;

case class ChangedPosition(var uri: String, var position: Int) extends ProtocolMessagePayload;

case class ValidationReport(var pointOfViewUri: String, var version: Int, var issues: Seq[ValidationIssue]) extends ProtocolMessagePayload;

case class ValidationIssue(var code: String, var `type`: String, var filePath: String, var text: String, var range: Range, var trace: Seq[ValidationIssue]);

case class TextEdit(var range: Range, var text: String);

case class Range(var start: Int, var end: Int);

case class StructureReport(var uri: String, var version: Int, var structure: Map[String, StructureNode]) extends ProtocolMessagePayload;

case class StructureNode(text: String, typeText: Option[String], icon: String, textStyle: String, key: String, start: Int, end: Int, selected: Boolean, children: Seq[StructureNode], category: String);

case class GetStructureRequest(wrapped: String) extends ProtocolMessagePayload;

case class GetStructureResponse(wrapped: Map[String, StructureNode]) extends ProtocolMessagePayload;

case class LoggerSettings(var disabled: Option[Boolean], var allowedComponents: Option[Seq[String]], var deniedComponents: Option[Seq[String]], var maxSeverity: Option[Int], var maxMessageLength: Option[Int]);

object OpenedDocument {
	implicit def rw: RW[OpenedDocument] = macroRW;
	
	implicit def transportToShared(from: OpenedDocument): SharedOpenDocument = SharedOpenDocument(from.uri, from.version, from.text);
}

object ChangedDocument {
	implicit def rw: RW[ChangedDocument] = macroRW;
	
	implicit def transportToShared(from: ChangedDocument): SharedChangedDocument = SharedChangedDocument(from.uri, from.version, from.text, if(from.textEdits.isDefined) Some(from.textEdits.get.map(edit => TextEdit.transportToShared(edit))) else None);
}

object ChangedPosition {
	implicit def rw: RW[ChangedPosition] = macroRW;
	
	implicit def transportToShared(from: ChangedPosition) = SharedChangedPosition(from.uri, from.position);
}

object ValidationReport {
	implicit def rw: RW[ValidationReport] = macroRW;
	
	implicit def sharedToTransport(from: SharedValidationReport): ValidationReport = ValidationReport(from.pointOfViewUri, from.version, from.issues.map(issue => ValidationIssue.sharedToTransport(issue)));
}

object ValidationIssue {
	implicit def rw: RW[ValidationIssue] = macroRW
	
	implicit def sharedToTransport(from: SharedValidationIssue): ValidationIssue = ValidationIssue(from.code, from.`type`, from.filePath, from.text, from.range, from.trace.map(issue=>ValidationIssue.sharedToTransport(issue)));
}

object TextEdit {
	implicit def rw: RW[TextEdit] = macroRW;
	
	implicit def transportToShared(from: TextEdit): SharedTextEdit = SharedTextEdit(from.range, from.text);
	
	implicit def sharedToTransport(from: SharedTextEdit): TextEdit = TextEdit(from.range, from.text);
}

object Range {
	implicit def rw: RW[Range] = macroRW;
	
	implicit def transportToShared(from: Range): SharedRange = SharedRange(from.start, from.end);
	
	implicit def sharedToTransport(from: SharedRange): Range = Range(from.start, from.end);
}

object StructureReport {
	implicit def rw: RW[StructureReport] = macroRW;
	
	implicit def sharedToTransport(from: SharedStructureReport): StructureReport = StructureReport(from.uri, from.version, from.structure.map {
		case (key, value) => (key, StructureNode.sharedToTransport(value));
	});
}

object StructureNode {
	implicit def rw: RW[StructureNode] = macroRW;
	
	implicit def sharedToTransport(from: SharedStructureNode): StructureNode = StructureNode(
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
	);
}

object GetStructureRequest {
	implicit def rw: RW[GetStructureRequest] = macroRW
}

object GetStructureResponse {
	implicit def rw: RW[GetStructureResponse] = macroRW
}

object LoggerSettings {
	implicit def rw: RW[LoggerSettings] = macroRW;
	
	def transportToShared(from: LoggerSettings): ILoggerSettings = new ILoggerSettings() {
		var disabled = from.disabled;
		var allowedComponents = from.allowedComponents;
		var deniedComponents = from.deniedComponents;
		var maxSeverity = if(from.maxSeverity.isDefined) Some(MessageSeverity.sharedToTransport(from.maxSeverity.get)) else None;
		var maxMessageLength = from.maxMessageLength;
	}
	
}

object MessageSeverity {
	implicit def sharedToTransport(from: Int): SharedMessageSeverity.Value = from match {
		case 0 => SharedMessageSeverity.DEBUG_DETAIL;
		case 1 => SharedMessageSeverity.DEBUG;
		case 2 => SharedMessageSeverity.DEBUG_OVERVIEW;
		case 3 => SharedMessageSeverity.WARNING;
		case 4 => SharedMessageSeverity.ERROR;
		case _ => SharedMessageSeverity.DEBUG;
	}
}
