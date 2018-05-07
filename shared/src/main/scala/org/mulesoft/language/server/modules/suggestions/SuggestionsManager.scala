package org.mulesoft.language.server.modules.suggestions

import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.hlastManager.{HLASTManager, IHLASTListener}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IAbstractTextEditorWithCursor, IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.project.IProject
import org.mulesoft.high.level.interfaces.IParseResultFactory
import org.mulesoft.language.common.dtoTypes.IStructureReport
import org.mulesoft.als.suggestions.implementation.CompletionConfig
import org.mulesoft.als.suggestions.CompletionProvider

class SuggestionsManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "SUGGESTIONS_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "HL_AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  val onDocumentCompletionListener: (String, Int) => Future[Seq[ISuggestion]] = onDocumentCompletion

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getHLASTManager: HLASTManager = {

    this.getDependencyById(HLASTManager.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {

      this.connection.onDocumentCompletion(this.onDocumentCompletionListener)

      Success(this)
    } else {

      superLaunch
    }
  }


  override def stop(): Unit = {

    super.stop()

    this.connection.onDocumentCompletion(this.onDocumentCompletionListener, true)
  }


  protected def onDocumentCompletion(url: String, position: Int) : Future[Seq[ISuggestion]] = {

    val editorOption: Option[IAbstractTextEditorWithCursor] =
      this.getEditorManager.getEditor(url)

    if (editorOption.isDefined) {
      val text = editorOption.get.text

      this.buildCompletionProviderAST(text, url, position).map(provider=>{
        provider.suggest.map(suggestions => suggestions.map(suggestion => {
          suggestion.text,
          suggestion.description,
          suggestion.displayText,
          suggestion.prefix,
          suggestion.category
        }))
      })
    } else {
      Promise.successful( Seq.empty[ISuggestion]).future
    }
  }

  def buildCompletionProviderAST(text:String, url: String, position: Int): Future[CompletionProvider] = {

    this.getHLASTManager.forceGetCurrentAST(url).map(hlAST=>{
      val baseName = url.substring(url.lastIndexOf('/') + 1)

      val astProvider = new ASTProvider(hlAST)

      val editorStateProvider = new EditorStateProvider(text, url, baseName, position)

      val platformFSProvider = FsProvider(this.connection)

      val completionConfig = new CompletionConfig()
        .withEditorStateProvider(editorStateProvider)
        .withASTProvider(astProvider)
        .withFsProvider(platformFSProvider)

      CompletionProvider().withConfig(completionConfig)
    })


  }
}

