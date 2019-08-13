package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.plugins.headers.{AMLHeadersCompletionPlugin, KeyPropertyHeaderCompletionPlugin}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompletionPluginsRegistryHeaders {

  private var pluginsSet: mutable.Set[HeaderCompletionPlugin] = mutable.Set()

  def plugins: Seq[HeaderCompletionPlugin] = pluginsSet.toSeq

  def registerPlugin(plugin: HeaderCompletionPlugin): Unit = pluginsSet += plugin

  def cleanPlugins(): Unit = pluginsSet = mutable.Set()
}

object CompletionPluginsRegistryHeaders {

  private val registry = new CompletionPluginsRegistryHeaders

  def pluginSuggestions(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.sequence(
      registry.plugins.map(p => p.resolve(params))
    ) map { _.flatten }

  def registerPlugin(plugin: HeaderCompletionPlugin): Unit = registry.registerPlugin(plugin)

  def cleanPlugins(): Unit = registry.cleanPlugins()
}

object HeaderBaseCompletionPlugins {
  private val all = Seq(
    AMLHeadersCompletionPlugin,
    KeyPropertyHeaderCompletionPlugin
  )

  def get(): Seq[HeaderCompletionPlugin] = all

  def initAll(): Unit = {
    CompletionPluginsRegistryHeaders.cleanPlugins()
    all.foreach(CompletionPluginsRegistryHeaders.registerPlugin)
  }
}
