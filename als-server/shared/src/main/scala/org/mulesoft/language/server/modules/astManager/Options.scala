//package org.mulesoft.language.server.server.modules.astManager
//
//import org.mulesoft.language.server.modules.path.PathIndex
//import org.mulesoft.language.server.modules.raml_1_parser.Raml1ParserIndex
//import org.mulesoft.language.server.modules....core.connections.IServerConnection;
//import org.mulesoft.language.server.modules.......common.typeInterfaces.IChangedDocument;
//import org.mulesoft.language.server.modules.......common.typeInterfaces.ILogger;
//import org.mulesoft.language.server.modules.......common.typeInterfaces.IOpenedDocument;
//import org.mulesoft.language.server.modules.......common.reconciler.Reconciler;
//import org.mulesoft.language.server.modules.......common.reconciler.Runnable;
//import org.mulesoft.language.server.modules.editorManager.IEditorManagerModule;
//import org.mulesoft.language.server.modules.commonInterfaces.IDisposableModule;
//import org.mulesoft.language.server.modules.shortid.ShortidIndex
//import org.mulesoft.language.server.modules.promise_polyfill.PromisePolyfillIndex
//import org.mulesoft.language.server.modules.astManager.IASTListener;
//import org.mulesoft.language.server.modules.astManager.IASTManagerModule;
//import org.mulesoft.language.server.modules.astManager.Options;
//import org.mulesoft.language.server.modules.astManager.ParseDocumentRunnable;
//import org.mulesoft.language.server.modules.astManager.ASTManager;
//
//trait Options {
//  var fsResolver: Any
//  var httpResolver: Any
//  var rejectOnErrors: Boolean
//  var attributeDefaults: Boolean
//  var filePath: String
//}
