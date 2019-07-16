package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLEnumCompletionPlugin,
  AMLKnownValueCompletions,
  AMLRootDeclarationsCompletionPlugin,
  AMLStructureCompletionPlugin
}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompletionPluginsRegistryAML {

  private var pluginsSet: mutable.Set[CompletionPlugin] = mutable.Set()

  def plugins: Seq[CompletionPlugin] = pluginsSet.toSeq

  def registerPlugin(plugin: CompletionPlugin): Unit = pluginsSet += plugin

  def cleanPlugins(): Unit = pluginsSet = mutable.Set()
}

object CompletionPluginsRegistryAML {

  private val registry = new CompletionPluginsRegistryAML

  def pluginSuggestions(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    Future.sequence(
      registry.plugins.map(p => p.resolve(params))
    ) map { _.flatten }
  }

  def registerPlugin(plugin: CompletionPlugin): Unit = registry.registerPlugin(plugin)

  def cleanPlugins(): Unit = registry.cleanPlugins()
}

object AMLBaseCompletionPlugins {
  private val all = Seq(AMLStructureCompletionPlugin,
                        AMLEnumCompletionPlugin,
                        AMLKnownValueCompletions,
                        AMLRootDeclarationsCompletionPlugin)

  def get(): Seq[CompletionPlugin] = all

  def initAll(): Unit = {
    CompletionPluginsRegistryAML.cleanPlugins()
    all.foreach(CompletionPluginsRegistryAML.registerPlugin)
  }
}
