package org.mulesoft.language.server.modules.suggestions

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.hlastManager.{HLASTManager, IHLASTListener, IHLASTManagerModule}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IAbstractTextEditorWithCursor, IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.IStructureReport
import org.mulesoft.als.suggestions.implementation.CompletionConfig
import org.mulesoft.als.suggestions.{CompletionProvider, PlatformBasedExtendedFSProvider}
import org.mulesoft.als.suggestions.interfaces.Syntax

class SuggestionsManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "SUGGESTIONS_MANAGER"

  val moduleDependencies: Array[String] = Array(
    IEditorManagerModule.moduleId,
    IHLASTManagerModule.moduleId
  )

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
      val editor = editorOption.get

      val text = editor.text

      val vendorOption = Vendor.unapply(editor.language)
      val vendor: Vendor = vendorOption.getOrElse(Raml10)

      //TODO add unapply to suggestion's Syntax
      val syntax = if (editor.syntax == "YAML") Syntax.YAML else Syntax.JSON

      this.buildCompletionProviderAST(text, url, position,
        vendor, syntax).flatMap(provider=>{

        provider.suggest.map(suggestions => suggestions.map(suggestion => ISuggestion(
          suggestion.text,
          if(suggestion.description!=null) Some(suggestion.description) else None,
          if(suggestion.displayText!=null) Some(suggestion.displayText) else None,
          if(suggestion.prefix!=null) Some(suggestion.prefix) else None,
          if(suggestion.category!=null) Some(suggestion.category) else None
        )))

      })
    } else {
      Promise.successful( Seq.empty[ISuggestion]).future
    }
  }

  def buildCompletionProviderAST(text:String, url: String, position: Int,
                                 vendor: Vendor, syntax: Syntax): Future[CompletionProvider] = {

    this.getHLASTManager.forceGetCurrentAST(url).map(hlAST=>{
      val baseName = url.substring(url.lastIndexOf('/') + 1)

      val astProvider = new ASTProvider(hlAST.rootASTUnit.rootNode, vendor, syntax)

      val editorStateProvider = new EditorStateProvider(text, url, baseName, position)

      val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

      val completionConfig = new CompletionConfig()
        .withEditorStateProvider(editorStateProvider)
        .withAstProvider(astProvider)
        .withFsProvider(platformFSProvider)

      CompletionProvider().withConfig(completionConfig)
    })


  }
}

