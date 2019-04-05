package org.mulesoft.lsp.feature

import scala.concurrent.Future

trait RequestHandler[-P, +R] {
  def `type`: RequestType[P, R]

  def apply(params: P): Future[R]
}
