package org.mulesoft.language.server.js

import scalajs.js
import js.annotation._

import org.mulesoft.language.server.core
import org.mulesoft.language.server.server.modules.astManager.ASTManager
import org.mulesoft.language.server.server.modules.editorManager.EditorManager
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager

@js.native
@JSImport("amf-shacl-node", JSImport.Default)
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
class JSTextDocumentPositionParams extends js.Any {

}

@js.native
class JSDocumentSymbolParams extends js.Any {
	var textDocument: JSTextDocument = js.native;
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
class CompletionItem extends js.Object {
	var label: String = null;
	var detail: String = null;
	var documentation: String = null;
	
	var kind: Int = 0;
	var data: Int = 0;
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
class WEBLSPServerProcess extends js.Object {
	var scalaConnection: WEBLSPServerConnection = new WEBLSPServerConnection();
	
	def start() {
		Globals.SHACLValidator = SHACLValidator;
		
		println("configure connection");
		
		var server = new core.Server(this.scalaConnection, WebJSHttpFetcher);
		
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
	}
}