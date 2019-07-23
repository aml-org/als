package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLEnumCompletionPlugin,
  AMLKnownValueCompletions,
  AMLDeclarationsReferencesCompletionPlugin,
  AMLRootDeclarationsCompletionPlugin,
  AMLStructureCompletionPlugin
}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompletionPluginsRegistryAML {

  private val pluginsSet: mutable.Set[CompletionPlugin] = mutable.Set()

  def registerPlugin(plugin: CompletionPlugin): CompletionPluginsRegistryAML = {
    pluginsSet += plugin
    this
  }

  def cleanPlugins(): Unit = pluginsSet.clear()

  def suggests(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val seq: Seq[Future[Seq[RawSuggestion]]] = pluginsSet.map(_.resolve(params)).toSeq
    Future
      .sequence(seq)
      .map(s => {
        s
        s.flatten
      })
  }
}

object CompletionsPluginHandler {

  private val registries: mutable.Map[String, CompletionPluginsRegistryAML] = mutable.Map()

  def pluginSuggestions(params: CompletionParams): Future[Seq[RawSuggestion]] =
    registries.getOrElse(params.actualDialect.id, AMLBaseCompletionPlugins.base).suggests(params)

  def registerPlugin(plugin: CompletionPlugin, dialect: String): Unit = registries.get(dialect) match {
    case Some(registry) => registry.registerPlugin(plugin)
    case _              => registries.put(dialect, new CompletionPluginsRegistryAML().registerPlugin(plugin))
  }

  def registerPlugins(plugins: Seq[CompletionPlugin], dialect: String): Unit = registries.get(dialect) match {
    case Some(registry) => plugins.foreach(registry.registerPlugin)
    case _ =>
      val p = new CompletionPluginsRegistryAML()
      plugins.foreach(p.registerPlugin)
      registries.put(dialect, p)
  }

  def cleanIndex(): Unit = registries.keys.foreach(registries.remove)
}

object AMLBaseCompletionPlugins {
  val all: Seq[CompletionPlugin] = Seq(
    AMLStructureCompletionPlugin,
    AMLEnumCompletionPlugin,
    AMLRootDeclarationsCompletionPlugin,
    AMLDeclarationsReferencesCompletionPlugin,
    AMLKnownValueCompletions
  )

  val base: CompletionPluginsRegistryAML = {
    val b = new CompletionPluginsRegistryAML
    all.foreach(b.registerPlugin)
    b
  }
}
