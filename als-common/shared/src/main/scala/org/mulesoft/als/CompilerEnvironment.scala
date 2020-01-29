package org.mulesoft.als

import scala.concurrent.Future

trait CompilerEnvironment[T, E] {

  def modelBuiler(): ModelBuilder[T, E]
  def init(): Future[Unit]
}

trait ModelBuilder[T, E] {
  def parse(uri: String): Future[T]
  def parse(uri: String, env: E): Future[T]
}
