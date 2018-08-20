package org.mulesoft.language.server.js

import org.mulesoft.language.client.js.MSLSPHttpFetcher

import scalajs.js
import js.annotation._
import js.JSConverters._
import concurrent.ExecutionContext.Implicits.global
import org.mulesoft.language.server.core
import org.mulesoft.language.server.server.modules.astManager.ASTManager
import org.mulesoft.language.server.server.modules.editorManager.EditorManager
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager

@JSImport("vscode-languageserver", "TextDocuments")
class JSTextDocuments extends js.Object {
	def all(): js.Array[JSTextDocument] = js.native;
	
	def listen(connection: VSCConnection): js.Any = js.native;
	
	def onDidOpen(arg: js.Function1[JSDocumentChange, js.Any]): js.Any = js.native;
	
	def onDidChangeContent(arg: js.Function1[JSDocumentChange, js.Any]): js.Any = js.native;
	
	def syncKind: js.Any = js.native;
	
	def get(uri: String): JSTextDocument = js.native;
}

@js.native
@JSImport("aml-shacl-node", JSImport.Default)
object SHACLValidator extends js.Any {

}

@js.native
@JSGlobalScope
object Globals extends js.Object {
	var process: js.Any = js.native;
	var SHACLValidator: js.Any = js.native;
}

@js.native
class JSTextDocument extends js.Any {
	def _uri: String = js.native;
	def _version: Int = js.native;
	def _content: String = js.native;
	
	def positionAt(offset: Int): JSPosition = js.native;
}

@js.native
class JSDocumentChange extends js.Any {
	def document: JSTextDocument = js.native;
}

@JSImport("vscode-languageserver", "IPCMessageReader")
class JSIPCMessageReader(arg: js.Any) extends js.Object {

}

@JSImport("vscode-languageserver", "IPCMessageWriter")
class JSIPCMessageWriter(arg: js.Any) extends js.Object {

}

@js.native
@JSImport("vscode-languageserver", JSImport.Namespace)
object VSCodeServer extends js.Any {
	def createConnection(reader: JSIPCMessageReader, writer: JSIPCMessageWriter): VSCConnection = js.native;
}

@js.native
class JSTextDocumentPositionParams extends js.Any {

}

@js.native
class JSDocumentSymbolParams extends js.Any {
	var textDocument: JSTextDocument = js.native;
}

@js.native
class VSCConnection extends js.Any {
	def onInitialize(arg: js.Function1[js.Any, InitializeResult]): js.Any = js.native;
	def sendDiagnostics(diagnostics: SendDiagnostics): String = js.native;
	def onDidChangeConfiguration(arg: js.Function0[js.Any]): js.Any = js.native;
	def onCompletion(arg: js.Function1[JSTextDocumentPositionParams, js.Array[CompletionItem]]): js.Any = js.native;
	def onCompletionResolve(arg: js.Function1[CompletionItem, CompletionItem]): js.Any = js.native;
	def onDocumentSymbol(arg: js.Function1[JSDocumentSymbolParams, js.Promise[js.Array[JSSymbolInformation]]]): js.Any = js.native;
	
	def listen(): js.Any = js.native;
}

@ScalaJSDefined
class JSLocation extends js.Object {
	var uri: String = null;
	
	var range: JSClientRange = null;
}

@ScalaJSDefined
class JSSymbolInformation extends js.Object {
	var name: String = null;
	
	var kind: Int = 0;
	
	var location: JSLocation = null;
}

@ScalaJSDefined
class InitializeResult extends js.Object {
	var capabilities: InitializeResultCapabilities = null;
}

@ScalaJSDefined
class InitializeResultCapabilities extends js.Object {
	var textDocumentSync: js.Any = null;
	
	var completionProvider: InitializeResultCapabilitiesCompletionProvider = null;
}

@ScalaJSDefined
class InitializeResultCapabilitiesCompletionProvider extends js.Object {
	var resolveProvider: Boolean = false;
}

@ScalaJSDefined
class SendDiagnostics extends js.Object {
	var diagnostics: js.Array[Diagnostic] = null;
	var uri: String = null;
}

@ScalaJSDefined
class CompletionItem extends js.Object {
	var label: String = null;
	var detail: String = null;
	var documentation: String = null;
	
	var kind: Int = 0;
	var data: Int = 0;
}

@ScalaJSDefined
class Diagnostic extends js.Object {
	var severety: Int = 0;
	var range: JSClientRange = null;
	var message: String = null;
	var source: String = null;
}

@ScalaJSDefined
class JSPosition extends js.Object {
	var line: Int = 0;
	var character: Int = 0;
}

@ScalaJSDefined
class JSClientRange extends js.Object {
	var start: JSPosition = null;
	var end: JSPosition = null;
}

@ScalaJSDefined
class MSLSPServerProcess extends js.Object {
	var vscConnection: VSCConnection = VSCodeServer.createConnection(new JSIPCMessageReader(Globals.process), new JSIPCMessageWriter(Globals.process));
	
	var documents: JSTextDocuments = new JSTextDocuments();
	
	var scalaConnection: MSLSPServerConnection = new MSLSPServerConnection();
	
	def start() {
		Globals.SHACLValidator = SHACLValidator;
		
		println("configure connection");
		
		var server = new core.Server(this.scalaConnection,
			MSLSPHttpFetcher);
		
		
		var editorManager = new EditorManager();
		var astManager = new ASTManager();
		var validationManager = new ValidationManager();
		
		server.registerModule(editorManager);
		server.registerModule(astManager);
		server.registerModule(validationManager);
		
		server.enableModule(editorManager.moduleId);
		server.enableModule(astManager.moduleId);
		server.enableModule(validationManager.moduleId);
		
		editorManager.launch();
		astManager.launch();
		validationManager.launch();
		
		this.documents.listen(this.vscConnection);
		
		documents.onDidOpen((event: JSDocumentChange) => {
			var openedDocument = new OpenedDocument(event.document._uri, event.document._version, event.document._content);
			
			scalaConnection.handleOpenDocument(openedDocument);
		})
		
		this.documents.onDidChangeContent((event: JSDocumentChange) => {
			var changedDocument = new ChangedDocument(event.document._uri, event.document._version, Some(event.document._content), None);
			
			scalaConnection.handleChangedDocument(changedDocument);
		});
		
		this.scalaConnection.onValidationReport((report: ValidationReport) => {
			val diagnosticsByUri: scala.collection.mutable.Map[String, js.Array[Diagnostic]] = scala.collection.mutable.Map[String, js.Array[Diagnostic]]();
			
			report.issues.foreach(issue => {
				var diagnostics: js.Array[Diagnostic] = diagnosticsByUri.get(issue.filePath).orNull;
				
				if(diagnostics == null) {
					diagnostics = new js.Array();
					
					diagnosticsByUri.put(issue.filePath, diagnostics);
				}
				
				var document = this.documents.get(issue.filePath);
				
				var range = new JSClientRange();
				
				range.start = document.positionAt(issue.range.start);
				range.end = document.positionAt(issue.range.end);
				
				var diagnostic = new Diagnostic();
				
				diagnostic.severety = 2;
				diagnostic.range = range;
				diagnostic.message = issue.text;
				
				diagnostics.push(diagnostic)
			});
			
			diagnosticsByUri.keys.foreach(key => {
				var sendDiagnostics = new SendDiagnostics();
				
				sendDiagnostics.uri = key;
				sendDiagnostics.diagnostics = diagnosticsByUri.get(key).orNull;
				
				this.vscConnection.sendDiagnostics(sendDiagnostics);
			})
		});
		
		this.vscConnection.onDocumentSymbol((symbolParams: JSDocumentSymbolParams) => {
			var future = this.scalaConnection.getStructure(symbolParams.textDocument._uri);
			
			future.flatMap((structureResponse: GetStructureResponse) => {
				concurrent.Future {
					var result: js.Array[JSSymbolInformation] = new js.Array();
					
					var nodes = structureResponse.wrapped;
					
					nodes.keys.foreach(categoryName => {
						var topLevelNode = nodes.get(categoryName).orNull;
						
						var kind = categoryName match {
							case "Resources" => 12;
							case "Schemas & Types" => 5;
							case "Resource Types & Traits" => 11;
							case "Other" => 14;
						}
						
						topLevelNode.children.foreach(child => {
							var item = new JSSymbolInformation();
							
							item.name = child.text;
							item.kind = kind;
							item.location = new JSLocation();
							
							item.location.uri = symbolParams.textDocument._uri;
							
							item.location.range = new JSClientRange();
							
							item.location.range.start = symbolParams.textDocument.positionAt(child.start);
							item.location.range.end = symbolParams.textDocument.positionAt(child.end);
							
							result.push(item);
						})
					})
					
					result;
				}
			}).toJSPromise;
		});
		
		this.vscConnection.onInitialize((params) => {
			var result = new InitializeResult();
			
			var capabilities = new InitializeResultCapabilities();
			
			var completionProvider = new InitializeResultCapabilitiesCompletionProvider();
			
			completionProvider.resolveProvider = true;
			
			capabilities.textDocumentSync = this.documents.syncKind;
			capabilities.completionProvider = completionProvider;
			
			result.capabilities = capabilities;
			
			result;
		});
		
		this.vscConnection.onCompletion((textDocumentPosition: JSTextDocumentPositionParams) => {
			new js.Array();
		});
		
		this.vscConnection.onCompletionResolve((item: CompletionItem) => {
			item;
		});
		
		this.vscConnection.listen();
	}
}