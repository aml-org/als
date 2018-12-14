// $COVERAGE-OFF$
package org.mulesoft.language.client.js

trait IClientConnection {

  /**
    * Notifies the server that document is opened.
    * @param document - opened document
    */
  def documentOpened(document: IOpenedDocument): Unit;
}
// $COVERAGE-ON$
