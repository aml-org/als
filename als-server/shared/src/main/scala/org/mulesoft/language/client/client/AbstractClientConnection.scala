// $COVERAGE-OFF$
package org.mulesoft.language.client.client

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.client.client.IClientConnection
import org.mulesoft.language.common.dtoTypes.{IDetailsReport, _}
import org.mulesoft.language.common.logger.{ILoggerSettings, MessageSeverity}
import org.mulesoft.language.entryPoints.common.ProtocolMessage
import org.mulesoft.language.server.common.configuration.IServerConfiguration

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

trait AbstractClientConnection extends IClientConnection {

//    private loggerSettings: ILoggerSettings;
//
  protected var validationReportListeners: Buffer[(IValidationReport) => Unit] = ArrayBuffer()
  protected var structureReportListeners: Buffer[(IStructureReport) => Unit]   = ArrayBuffer()
//    private versionManager: VersionedDocumentManager;
//
  protected var onExistsListeners: Buffer[String => Future[Boolean]]                 = ArrayBuffer()
  protected var onReadDirListeners: Buffer[String => Future[Seq[String]]]            = ArrayBuffer()
  protected var onIsDirectoryListeners: Buffer[String => Future[Boolean]]            = ArrayBuffer()
  protected var onContentListeners: Buffer[String => Future[String]]                 = ArrayBuffer()
  protected var onDetailsReportListeners: Buffer[IDetailsReport => Unit]             = ArrayBuffer()
  protected var onDisplayActionUIListeners: Buffer[IUIDisplayRequest => Future[Any]] = ArrayBuffer()

  def setServerConfiguration(serverSettings: IServerConfiguration): Unit = {

//        // changing server configuration
//        this.send({
//        type : "SET_SERVER_CONFIGURATION",
//        payload : serverSettings
//    });
  }

  /**
    * Adds a listener for validation report coming from the server.
    *
    * @param listener
    */
  def onValidationReport(listener: (IValidationReport) => Unit, unsubscribe: Boolean = false): Unit = {
    this.addListener(this.validationReportListeners, listener, unsubscribe)
  }

  /**
    * Instead of calling getStructure to get immediate structure report for the document,
    * this method allows to launch to the new structure reports when those are available.
    *
    * @param listener
    */
  def onStructureReport(listener: (IStructureReport) => Unit, unsubscribe: Boolean = false): Unit = {
    this.addListener(this.structureReportListeners, listener, unsubscribe)
  }

  /**
    * Listens to the server requests for FS path existence, answering whether
    * a particular path exists on FS.
    */
  def onExists(listener: String => Future[Boolean], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.onExistsListeners, listener, unsubscribe)
  }

  /**
    * Listens to the server requests for directory contents, answering with a list
    * of files in a directory.
    */
  def onReadDir(listener: String => Future[Seq[String]], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.onReadDirListeners, listener, unsubscribe)
  }

  /**
    * Listens to the server requests for directory check, answering whether
    * a particular path is a directory.
    */
  def onIsDirectory(listener: String => Future[Boolean], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.onIsDirectoryListeners, listener, unsubscribe)
  }

  /**
    * Listens to the server requests for file contents, answering what contents file has.
    */
  def onContent(listener: String => Future[String], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.onContentListeners, listener, unsubscribe)
  }

  /**
    * Report from the server that the new details are calculated
    * for particular document and position.
    *
    * @param listener
    */
  def onDetailsReport(listener: IDetailsReport => Unit, unsubscribe: Boolean = false): Unit = {
    this.addListener(onDetailsReportListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to display action UI.
    *
    * @param listener - accepts UI display request, should result in a promise
    *                 returning final UI state to be transferred to the server.
    */
  def onDisplayActionUI(listener: IUIDisplayRequest => Future[Any], unsubscribe: Boolean = false): Unit = {
    this.addListener(onDisplayActionUIListeners, listener, unsubscribe)
  }

  def addListener[T](memberListeners: Buffer[T], listener: T, unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = memberListeners.indexOf(listener)
      if (index != -1) {
        memberListeners.remove(index)
      }

    } else {

      memberListeners += listener

    }

  }
}
// $COVERAGE-ON$
