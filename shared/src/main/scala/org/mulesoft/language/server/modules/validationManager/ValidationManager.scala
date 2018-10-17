package org.mulesoft.language.server.server.modules.validationManager

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.language.common.dtoTypes.{IRange, IValidationIssue, IValidationReport}
import org.mulesoft.language.server.common.reconciler.Reconciler
import org.mulesoft.language.server.common.utils.PathRefine
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
        val uri = PathRefine.refinePath(docUri, platform)
		val editorOption = this.getEditorManager().getEditor(uri);
		
		if(editorOption.isDefined) {
			val startTime = System.currentTimeMillis();
			
			this.report(uri, astNode).map(report => {
				val endTime = System.currentTimeMillis();
				
				this.connection.debugDetail(s"It took ${endTime-startTime} milliseconds to validate", "ValidationManager", "gatherValidationErrors");
				
				val issues = report.results.map(validationResult => this.amfValidationResultToIssue(docUri, validationResult, editorOption.get.buffer));
				
				IValidationReport(docUri, docVersion, issues);
			});
		} else {
			Future.failed(new Exception("Cant find the editor for uri " + uri));
		}
	}
	
	def amfValidationResultToIssue(uri: String, validationResult: AMFValidationResult, buffer: IEditorTextBuffer): IValidationIssue = {
		val messageText = validationResult.message;
		
		var resultRange = IRange(0, 0);
		
		if(validationResult.position.isDefined) {
      try {
//				println(s"Analyzing validation issue positions")

        val startLine = validationResult.position.get.range.start.line - 1;
//				println(s"Start line is: ${startLine}")
        val startColumn = validationResult.position.get.range.start.column;

        val startOffset = buffer.characterIndexForPosition(IPoint(startLine,  startColumn));
//				println(s"Start offset: ${startOffset}")

        var endLine = validationResult.position.get.range.end.line - 1;
//				println(s"End line is: ${endLine}")
        val endColumn = validationResult.position.get.range.end.column;

        val endOffset = buffer.characterIndexForPosition(IPoint(endLine, endColumn));
//				println(s"End offset: ${endOffset}")

				endLine = buffer.lineByOffset(endOffset)
//				println(s"Recalculated end line: ${endLine}")

        val originalText = buffer.getText()

        val startLineStartOffset =
          buffer.characterIndexForPosition(buffer.rangeForRow(startLine, false).start)
//				println(s"Start line start offset: ${startLineStartOffset}")

        val lastLineRange = buffer.rangeForRow(endLine, false)
        val endLineStartOffset =
          buffer.characterIndexForPosition(lastLineRange.start)
//				println(s"End line start offset: ${endLineStartOffset}")
        val endLineEndOffset =
          buffer.characterIndexForPosition(lastLineRange.end)
//				println(s"End line end offset: ${endLineEndOffset}")

        val endLineStartText = originalText.substring(endLineStartOffset, endOffset)
        val endLineStartTextTrimmed = endLineStartText.trim

        val detectionRangeStart = startLineStartOffset

        val detectionRangeEnd =
          if (endLineStartTextTrimmed.length > 0) {

            endLineEndOffset
          } else {

            endLineStartOffset
          }

//				println(s"Detection range is: ${detectionRangeStart} , ${detectionRangeEnd}")
        val textInRange = originalText.substring(detectionRangeStart, detectionRangeEnd)
//				println("Text in original range:[")
//				println(originalText.substring(startOffset, endOffset))
//				println("]:Text in original range")
//				println("Text in detection range:[")
//				println(textInRange)
//				println("]:Text in range")
        val trimmed = textInRange.trim

        val indexInOriginal = textInRange.indexOf(trimmed)

        val startModifier = indexInOriginal
        val endModifier = textInRange.length - (indexInOriginal + trimmed.length)

				val resultingStartOffset = detectionRangeStart + startModifier
				val resultingEndOffset = detectionRangeEnd - endModifier

//				println(s"Final range is: ${resultingStartOffset} , ${resultingEndOffset}")
        resultRange = IRange(resultingStartOffset, resultingEndOffset);
      } catch {
          // $COVERAGE-OFF$
        case e: Throwable=> {
					val startLine = validationResult.position.get.range.start.line - 1;
					val startColumn = validationResult.position.get.range.start.column;

					val startOffset = buffer.characterIndexForPosition(IPoint(startLine, startColumn));

					val endLine = validationResult.position.get.range.end.line - 1;
					val endColumn = validationResult.position.get.range.end.column;

					val endOffset = buffer.characterIndexForPosition(IPoint(endLine, endColumn));

					resultRange = IRange(startOffset, endOffset)
				}
          // $COVERAGE-ON$
      }
		}

		if (validationResult.level == "Violation") {
			IValidationIssue("PROPERTY_UNUSED", "Error", uri, messageText, resultRange, List());
		} else {
			IValidationIssue("PROPERTY_UNUSED", "Warning", uri, messageText, resultRange, List());
		}
	}
	
	private def report(uri: String, baseUnit: BaseUnit): Future[AMFValidationReport] = {
        val language = getEditorManager().getEditor(uri).map(_.language).getOrElse("OAS 2.0");
		
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
