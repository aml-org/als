package org.mulesoft.language.client.jvm.serverConnection

import java.util

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.client.jvm.{FS, ValidationHandler}
import org.mulesoft.language.client.jvm.dtoTypes.{GetCompletionRequest, GetStructureRequest, GetStructureResponse, ProtocolMessagePayload}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.{ILoggerSettings, IPrintlnLogger, MessageSeverity}
import org.mulesoft.language.server.server.core.connectionsImpl.AbstractServerConnection

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future;

class JAVAServerConnection extends JAVAMessageDispatcher with AbstractServerConnection {
	var lastStructureReport: Option[IStructureReport] = None;
	var fs: FS = null;
	var validationHandler: ValidationHandler = null;
	
	initialize();
	
	protected def initialize() {
		//		this.newMeta("READ_DIR", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringSeqResponse", true)));
		//		this.newMeta("IS_DIRECTORY", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
		//		this.newMeta("CONTENT", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringResponse", true)));
		//
		//		this.newVoidHandler("CHANGE_POSITION", handleChangedPosition _, Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ChangedPosition")));
		//
		//		this.newVoidHandler("OPEN_DOCUMENT", this.handleOpenDocument _,
		//			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.OpenedDocument")))
		//
		//		this.newVoidHandler("CLOSE_DOCUMENT", (document: ClosedDocument) => Unit, Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClosedDocument", true)));
		//
		//		this.newVoidHandler("CHANGE_DOCUMENT", this.handleChangedDocument _,
		//			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ChangedDocument")))
		//
		//		//    this.newFutureHandler("GET_STRUCTURE", this.handleGetStructure _,
		//		//      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetStructureRequest", true, true)))
		//
		//		this.newVoidHandler("SET_LOGGER_CONFIGURATION", this.handleSetLoggerConfiguration _,
		//			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.LoggerSettings")))
		//
		//		this.newFutureSeqHandler("GET_SUGGESTIONS", this.handleGetSuggestions _,
		//			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetCompletionRequest")))
		//
		//		this.newFutureHandler[FindDeclarationRequest, LocationsResponse]("OPEN_DECLARATION", (request: FindDeclarationRequest) => openDeclarationListeners.head(request.uri, request.position).map(result => {
		//			var m = Map[String, Location]();
		//
		//			var i = 0;
		//
		//			result.foreach(r => {
		//				m += (("a" + i) -> Location.sharedToTransport(r));
		//
		//				i = i + 1;
		//			});
		//
		//			new LocationsResponse(m);
		//		}), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindDeclarationRequest")));
		//		//this.newFutureHandler[FindReferencesRequest, LocationsResponse]("FIND_REFERENCES", (request: FindReferencesRequest) => findReferencesListeners.head(request.uri, request.position).map(result => new LocationsResponse(result.map(location => Location.sharedToTransport(location)))), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindReferencesRequest")));this.newMeta("EXISTS", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
	}
	
	def handleGetStructure(getStructure: GetStructureRequest): Future[GetStructureResponse] = {
		val firstOpt = this.documentStructureListeners.find(_ => true);
		
		firstOpt match  {
			case Some(listener) => listener(getStructure.url).map(resultMap=> GetStructureResponse(resultMap));
			
			case _ => Future.failed(new Exception("No structure providers found"));
		}
	}
	
	def handleGetSuggestions(getCompletion: GetCompletionRequest): Future[Seq[ISuggestion]] = {
		val firstOpt = this.documentCompletionListeners.find(_ => true);
		
		firstOpt match  {
			case Some(listener) => listener(getCompletion.uri, getCompletion.position);
			
			case _ => Future.failed(new Exception("No structure providers found"));
		}
	}
	
	def handleOpenDocument(document: IOpenedDocument) {
		val firstOpt = this.openDocumentListeners.find(_ => true);
		
		firstOpt match  {
			case Some(listener) => listener(document);
			
			case _ => Future.failed(new Exception("No open document providers found"));
		}
	}
	
	def handleChangedDocument(document: IChangedDocument) {
		val firstOpt = this.changeDocumentListeners.find(_ => true);
		
		firstOpt match  {
			case Some(listener) => listener(document);
			
			case _ => Future.failed(new Exception("No change document providers found"));
		}
	}
	
	def handleSetLoggerConfiguration(loggerSettings: ILoggerSettings) {
		this.setLoggerConfiguration(loggerSettings);
	}
	
	def structureAvailable(report: IStructureReport) {
		//this.send("STRUCTURE_REPORT", StructureReport.sharedToTransport(report))
	}
	
	override def validated(report: IValidationReport) {
		if(validationHandler == null) {
			return;
		}
		
		var list = new util.ArrayList[IValidationIssue]();
		
		report.issues.foreach(reportIssue => collectIssues(reportIssue).foreach(collectedIssue => list.add(collectedIssue)));
		
		validationHandler.success(report.pointOfViewUri, list);
	}
	
	private def collectIssues(issue: IValidationIssue): Seq[IValidationIssue] = {
		var result = mutable.MutableList(issue);
		
		issue.trace.foreach(traceIssue => {
			collectIssues(traceIssue).foreach(collected => result += collected);
		})
		
		result;
	}
	
	override def exists(path: String): Future[Boolean] = Future.successful(false);
	
	override def readDir(path: String): Future[Seq[String]] = Future.successful(Seq());
	
	override def isDirectory(path: String): Future[Boolean] = Future.successful(false);
	
	override def content(fullPath: String): Future[String] = {
		if(fs != null) {
			Future.successful(fs.content(fullPath));
		} else {
			Future.successful("")
		};
	};
	
	override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean) {
	
	}
	
	override def detailsAvailable(report: IDetailsReport) {
	
	}
	
	override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = Future.successful(null);
	
	override def setLoggerConfiguration(loggerSettings: ILoggerSettings) {
	
	}
	
	override def log(message: String, severity: MessageSeverity.Value, component: String, subcomponent: String) {
	
	}
	
	override def debugDetail(message: String, component: String, subcomponent: String) {
	
	}
	
	override def warning(message: String, component: String, subcomponent: String) {
	
	}
	
	override def debugOverview(message: String, component: String, subcomponent: String) {
	
	}
	
	override def error(message: String, component: String, subcomponent: String) {
	
	}
	
	override def debug(message: String, component: String, subcomponent: String) {
	
	}
}