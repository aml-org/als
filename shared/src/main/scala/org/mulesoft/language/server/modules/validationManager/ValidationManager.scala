package org.mulesoft.language.server.server.modules.validationManager

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.language.common.dtoTypes.{IRange, IValidationIssue, IValidationReport}
import org.mulesoft.language.server.common.reconciler.Reconciler
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.validationManager.ValidationRunnable
import org.mulesoft.language.server.server.modules.astManager.{IASTListener, IASTManagerModule, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

class ValidationManager extends AbstractServerModule {

	val moduleId: String = "VALIDATION_MANAGER";
	
	private var reconciler: Reconciler = new Reconciler(connection, 1000);
	
	val moduleDependencies: Array[String] = Array(IEditorManagerModule.moduleId, IASTManagerModule.moduleId);
	
	val onNewASTAvailableListener: IASTListener = new IASTListener {
		override def apply(uri: String, version: Int, ast: BaseUnit) {
			ValidationManager.this.newASTAvailable(uri, version, ast);
		}
	}
	
	protected def getEditorManager(): IEditorManagerModule = this.getDependencyById(IEditorManagerModule.moduleId).get;
	
	protected def getASTManager(): IASTManagerModule = this.getDependencyById(IASTManagerModule.moduleId).get;
	
	override def launch(): Try[IServerModule] = {
		val superLaunch = super.launch();
		
		if(superLaunch.isSuccess) {
			this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener);
			
			Success(this);
		} else {
			superLaunch;
		}
	}
	
	override def stop() {
		super.stop();
		
		this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true);
	}
	
	def newASTAvailable(uri: String, version: Int, ast: BaseUnit) {
		this.connection.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable");
		
		reconciler.shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, version, ast))).future andThen {
			case Success(report) => {
				this.connection.debug("Number of errors is:\n" + report.issues.length, "ValidationManager", "newASTAvailable");
				
				this.connection.validated(report);
			}
			
			case Failure(exception) => {
				exception.printStackTrace();
				
				this.connection.error("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable");
				
				this.connection.validated(new IValidationReport(uri, 0, Seq()));
			}
		}
	}
	
	private def gatherValidationErrors(docUri: String, docVersion: Int, astNode: BaseUnit): Future[IValidationReport] = {
		val editorOption = this.getEditorManager.getEditor(docUri);
		
		if(editorOption.isDefined) {
			val startTime = System.currentTimeMillis();
			
			this.report(docUri, astNode).map(report => {
				val endTime = System.currentTimeMillis();
				
				this.connection.debugDetail(s"It took ${endTime-startTime} milliseconds to validate", "ValidationManager", "gatherValidationErrors");
				
				val issues = report.results.map(validationResult => this.amfValidationResultToIssue(docUri, validationResult, editorOption.get.buffer));
				
				IValidationReport(docUri, docVersion, issues);
			});
		} else {
			Future.failed(new Exception("Cant find the editor for uri " + docUri));
		}
	}
	
	def amfValidationResultToIssue(uri: String, validationResult: AMFValidationResult, buffer: IEditorTextBuffer): IValidationIssue = {
		val messageText = validationResult.message;
		
		var resultRange = IRange(0, 0);
		
		if(validationResult.position.isDefined) {
			val startLine = validationResult.position.get.range.start.line - 1;
			val startColumn = validationResult.position.get.range.start.column;
			
			val startOffset = buffer.characterIndexForPosition(IPoint(startLine,  startColumn));
			
			val endLine = validationResult.position.get.range.end.line - 1;
			val endColumn = validationResult.position.get.range.end.column;
			
			val endOffset = buffer.characterIndexForPosition(IPoint(endLine, endColumn));
			
			resultRange = IRange(startOffset, endOffset);
		}

		if (validationResult.level == "Violation") {
			IValidationIssue("PROPERTY_UNUSED", "Error", uri, messageText, resultRange, List());
		} else {
			IValidationIssue("PROPERTY_UNUSED", "Warning", uri, messageText, resultRange, List());
		}
	}
	
	private def report(uri: String, baseUnit: BaseUnit): Future[AMFValidationReport] = {
		val language = if (uri.endsWith(".raml")) "RAML 1.0" else "OAS 2.0";
		
		var config = new ParserConfig(
			Some(ParserConfig.VALIDATE),
			Some(uri),
			Some(language),
			Some("application/yaml"),
			None,
			Some(language),
			Some("application/yaml"),
			false,
			true);
		
//		val helper = ParserHelper(platform);
		
		val customProfileLoaded = if(config.customProfile.isDefined) {
			RuntimeValidator.loadValidationProfile(config.customProfile.get);
		} else {
			Future.successful(config.profile);
		};
		
		customProfileLoaded.flatMap(profile => RuntimeValidator(baseUnit, profile));
	}
}

object ValidationManager {
	val moduleId: String = "VALIDATION_MANAGER";
}
