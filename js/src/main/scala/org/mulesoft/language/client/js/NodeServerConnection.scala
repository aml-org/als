package org.mulesoft.language.client.js

import amf.core.client.ParserConfig
import amf.core.model.document.{BaseUnit, Document}
import amf.core.unsafe.TrunkPlatform
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.language.client.js.CustomPicklerConfig.write
import org.mulesoft.language.common.logger.{AbstractLogger, IPrintlnLogger, MessageSeverity, PrintlnLogger}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.core.connectionsImpl.AbstractServerConnection
import org.mulesoft.language.server.server.modules.astManager.{ParseResult, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.IPoint
import org.mulesoft.language.server.server.modules.editorManager.TextBufferInfo

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class NodeServerConnection extends IPrintlnLogger
  with NodeMessageDispatcher with AbstractServerConnection {

  var lastStructureReport: Option[StructureReport] = None

  initialize()

  protected def initialize(): Unit = {

    this.newVoidHandler("OPEN_DOCUMENT", this.handleOpenDocument _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.OpenedDocument")))

    this.newVoidHandler("CHANGE_DOCUMENT", this.handleChangedDocument _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.ChangedDocument")))

    this.newFutureHandler("GET_STRUCTURE", this.handleGetStructure _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.GetStructure", true, true)))

    this.newVoidHandler("SET_LOGGER_CONFIGURATION", this.handleSetLoggerConfiguration _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.LoggerSettings")))
  }

  protected def internalSendJSONMessage(message: js.Any): Unit = {

    Globals.process.send(message)
  }

  def handleGetStructure(getStructure: GetStructureRequest) : Future[GetStructureResponse] = {
    val firstOpt = this.documentStructureListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(getStructure.wrapped).map(resultMap=>{
          GetStructureResponse(resultMap.map{case (key, value) => (key, value.asInstanceOf[StructureNode])})
        })
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleOpenDocument(document: OpenedDocument) : Unit = {
    val firstOpt = this.openDocumentListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No open document providers found"))
    }
  }

  def handleChangedDocument(document: ChangedDocument) : Unit = {
    val firstOpt = this.changeDocumentListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No change document providers found"))
    }
  }

  def handleSetLoggerConfiguration(loggerSettings: LoggerSettings) : Unit = {
    this.setLoggerConfiguration(LoggerSettings.transportToShared(loggerSettings))

  }

  /**
    * Reports new calculated structure when available.
    * @param report - structure report.
    */
  def structureAvailable(report: IStructureReport): Unit = {
    this.send("STRUCTURE_REPORT", report.asInstanceOf[StructureReport])
  }

  /**
    * Reports latest validation results
    *
    * @param report
    */
  override def validated(report: IValidationReport): Unit = {
    this.send("VALIDATION_REPORT", report.asInstanceOf[ValidationReport])
  }

  /**
    * Returns whether path/url exists.
    *
    * @param path
    */
  override def exists(path: String): Future[Boolean] = ???

  /**
    * Returns directory content list.
    *
    * @param path
    */
  override def readDir(path: String): Future[Seq[String]] = ???

  /**
    * Returns whether path/url represents a directory
    *
    * @param path
    */
  override def isDirectory(path: String): Future[Boolean] = ???

  /**
    * File contents by full path/url.
    *
    * @param fullPath
    */
  override def content(fullPath: String): Future[String] = ???

  /**
    * Adds a listener to document details request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String, position: Int) => Future[DetailsItemJSON]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean): Unit = ???

  /**
    * Reports new calculated details when available.
    *
    * @param report - details report.
    */
  override def detailsAvailable(report: IDetailsReport): Unit = ???

  /**
    * Adds a listener to display action UI.
    *
    * @param uiDisplayRequest - display request
    * @return final UI state.
    */
  override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = ???
}
