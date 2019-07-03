package org.mulesoft.als.server

import org.mulesoft.lsp.{ConfigHandler, ConfigType}

trait ConfigMap {
  def put[T, V](key: ConfigType[T, V], value: ConfigHandler[T, V]): ConfigMap

  def apply[T, V](key: ConfigType[T, V]): Option[ConfigHandler[T, V]]
}

object ConfigMap {
  private class WrappedMap(m: Map[ConfigType[_, _], ConfigHandler[_, _]]) extends ConfigMap {
    def put[T, V](key: ConfigType[T, V], value: ConfigHandler[T, V]): ConfigMap =
      new WrappedMap(m + (key -> value.asInstanceOf[ConfigHandler[_, _]]))

    def apply[T, V](key: ConfigType[T, V]): Option[ConfigHandler[T, V]] =
      m.get(key)
        .asInstanceOf[Option[ConfigHandler[T, V]]]
  }

  def empty: ConfigMap = new WrappedMap(Map())
}
