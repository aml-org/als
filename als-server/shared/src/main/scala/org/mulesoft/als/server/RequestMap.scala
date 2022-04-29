package org.mulesoft.als.server

import org.mulesoft.lsp.feature.{RequestHandler, RequestType}

trait RequestMap {
  def put[T, V](key: RequestType[T, V], value: RequestHandler[T, V]): RequestMap

  def apply[T, V](key: RequestType[T, V]): Option[RequestHandler[T, V]]
}

object RequestMap {

  private class WrappedMap(m: Map[RequestType[_, _], RequestHandler[_, _]]) extends RequestMap {
    def put[T, V](key: RequestType[T, V], value: RequestHandler[T, V]): RequestMap =
      new WrappedMap(m + (key -> value.asInstanceOf[RequestHandler[_, _]]))

    def apply[T, V](key: RequestType[T, V]): Option[RequestHandler[T, V]] = m
      .get(key)
      .asInstanceOf[Option[RequestHandler[T, V]]]
  }

  def empty: RequestMap = new WrappedMap(Map())
}
