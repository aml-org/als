package org.mulesoft.lsp

import scala.concurrent.Future

trait Initializable {
  def initialize(): Future[Unit]
}
