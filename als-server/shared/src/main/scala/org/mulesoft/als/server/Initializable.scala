package org.mulesoft.als.server

import scala.concurrent.Future

trait Initializable {
  def initialize(): Future[Unit]
}
