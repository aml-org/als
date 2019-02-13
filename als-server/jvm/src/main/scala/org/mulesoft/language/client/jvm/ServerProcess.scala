package org.mulesoft.language.client.jvm

import java.util
import java.util.function.Consumer

import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.client.jvm.DocumentSymbolConverter.AsJavaList
import org.mulesoft.language.client.jvm.dtoTypes.{GetCompletionRequest, GetStructureRequest}
import org.mulesoft.language.client.jvm.serverConnection.{JAVALogger, JAVAServerConnection}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.astManager.{ASTManager, ASTManagerModule}
import org.mulesoft.language.server.modules.dialectManager.{DialectManager, DialectManagerModule}
import org.mulesoft.language.server.modules.findDeclaration.FindDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HlAstManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.rename.RenameModule
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.validationManager.ValidationManager

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ServerProcess {
  private val connection = new JAVAServerConnection()

  def init() {
    val server = new Server(connection, JAVAPlatformDependentPart)

    server.registerModule(new ASTManager())
    server.registerModule(new DialectManager())
    server.registerModule(new HlAstManager())
    server.registerModule(new ValidationManager())
    server.registerModule(new SuggestionsManager())
    server.registerModule(new StructureManager())

    server.registerModule(new FindReferencesModule())
    server.registerModule(new FindDeclarationModule())
    server.registerModule(new RenameModule())

    server.enableModule(ASTManagerModule.moduleId)
    server.enableModule(DialectManagerModule.moduleId)
    server.enableModule(HlAstManager.moduleId)
    server.enableModule(ValidationManager.moduleId)
    server.enableModule(SuggestionsManager.moduleId)
    server.enableModule(StructureManager.moduleId)

    server.enableModule(FindReferencesModule.moduleId)
    server.enableModule(FindDeclarationModule.moduleId)

    server.enableModule(RenameModule.moduleId)
  }

  def setLogger(logger: JAVALogger) {
    connection.setLogger(logger)
  }

  def documentOpened(document: OpenedDocument) {
    connection.handleOpenDocument(document)
  }

  def documentChanged(uri: String, text: String, version: Int) {

    connection.handleChangedDocument(ChangedDocument(uri, version, Some(text), None))
  }

  def documentClosed(uri: String) {
    connection.handleCloseDocument(uri)
  }

  def getUnit(url: String): Unit = {}

  def getSuggestions(uri: String, position: Position, suggestionsHandler: SuggestionsHandler) {
    connection.handleGetSuggestions(new GetCompletionRequest(uri, position)) andThen {
      case Success(result) => {
        val list = new util.ArrayList[Suggestion]()

        result.map(new SuggestionComparableWrapper(_)).distinct.map(_.suggestion).foreach(list.add(_))

        suggestionsHandler.success(list)
      }

      case Failure(error) => suggestionsHandler.failure(error)
    }
  }

  def getStructure(uri: String, structureHandler: StructureHandler) {
    connection.handleGetStructure(new GetStructureRequest(uri)) andThen {
      case Success(result) => {

        structureHandler.success(result.structure.asJava)
      }

      case Failure(_) => structureHandler.success(Nil.asJava)
    }
  }

  def openDeclaration(uri: String, position: Int, locationsHandler: LocationsHandler) {
    connection.findDeclaration(uri, position) andThen {
      case Success(result) => {
        val list: util.List[ILocation] = new util.ArrayList[ILocation]()

        result.foreach(location => list.add(location))

        locationsHandler.success(list)
      }
    }
  }

  def findReferences(uri: String, position: Int, locationsHandler: LocationsHandler) {
    connection.findReferences(uri, position) andThen {
      case Success(result) => {
        var list: util.List[ILocation] = new util.ArrayList[ILocation]()

        result.foreach(location => list.add(location))

        locationsHandler.success(list)
      }
    }
  }

  def rename(uri: String, position: Int, newName: String, renameHandler: LocationsHandler) {
    connection.rename(uri, position, newName) andThen {
      case Success(result) => {
        var list: util.List[ILocation] = new util.ArrayList[ILocation]()

        result
          .map(item =>
            new ILocation {
              override var range: Range = item.textEdits.get.head.range

              override var uri: String = item.uri

              override var version: Int = 0
          })
          .foreach(location => list.add(location))

        renameHandler.success(list)
      }
    }
  }

  def setFS(fs: FS) {
    connection.fs = fs
  }

  def onValidation(handler: ValidationHandler) {
    connection.validationHandler = handler
  }
}

trait ValidationHandler {
  def success(pointOfView: String, issues: java.util.List[ValidationIssue])
}

trait SuggestionsHandler {
  def success(list: java.util.List[Suggestion])

  def failure(throwable: Throwable)
}

trait StructureHandler {
  def success(map: util.List[JavaDocumentSymbol])

  def failure(throwable: Throwable)
}

trait LocationsHandler {
  def success(list: util.List[ILocation])

  def failure(throwable: Throwable)
}

trait FS {
  def exists(uri: String, onData: Consumer[java.lang.Boolean])

  def readDir(uri: String, onData: Consumer[java.util.List[String]])

  def isDirectory(uri: String, onData: Consumer[java.lang.Boolean])

  def content(uri: String, onData: Consumer[String])
}

class JAVAStructureNode(var node: StructureNodeJSON) {
  var children: util.List[JAVAStructureNode] = new util.ArrayList[JAVAStructureNode]()

  override def toString: String = "(" + node.text + ", " + node.start + ", " + node.end + "): " + children.toString
}

object JAVAStructureNode {
  def apply(node: StructureNodeJSON): JAVAStructureNode = {
    var result = new JAVAStructureNode(node)

    node.children.foreach(child => result.children.add(JAVAStructureNode(child)))

    result
  }
}

//todo : change to use typings, wrapped and implicit convertions

object DocumentSymbolConverter {
  implicit class AsJavaConverter(internal: DocumentSymbol) {
    def asJava: JavaDocumentSymbol = new JavaDocumentSymbol(internal)
  }

  implicit class AsJavaList(internal: List[DocumentSymbol]) {
    def asJava: java.util.List[JavaDocumentSymbol] = internal.map(i => new JavaDocumentSymbol(i)).asJava
  }
}

class JavaDocumentSymbol(private val _internal: DocumentSymbol) {
  def name: String                                 = _internal.name
  def kind: Int                                    = _internal.kind.index
  def deprecated: Boolean                          = _internal.deprecated
  def range: amf.core.parser.Range                 = _internal.range
  def selectionRange: amf.core.parser.Range        = _internal.selectionRange
  def children: java.util.List[JavaDocumentSymbol] = _internal.children.toList.asJava
}

class SuggestionComparableWrapper(var suggestion: Suggestion) {
  override def toString(): String =
    suggestion.category + ", " + suggestion.description + ", " + suggestion.displayText + ", " + suggestion.prefix + ", " + suggestion.text

  override def equals(another: scala.Any): Boolean = toString().equals(another.toString)

  override def hashCode(): Int = toString().hashCode()
}
