package org.mulesoft.language.common.typeInterfaces

import scala.concurrent.Future

/**
  * Able to execute document change.
  */
trait IDocumentChangeExecutor {

  /**
    * Changes document in accordance with the change.
    * @param change - change to apply.
    * @return future indicating the change is applied.
    */
  def changeDocument(change: IChangedDocument): Future[Unit]
}
