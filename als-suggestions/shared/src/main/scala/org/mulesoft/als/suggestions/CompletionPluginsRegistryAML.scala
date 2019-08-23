package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.AMLLibraryPathCompletion

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompletionPluginsRegistryAML {

  private val pluginsSet: mutable.Set[AMLCompletionPlugin] = mutable.Set()

  def registerPlugin(plugin: AMLCompletionPlugin): CompletionPluginsRegistryAML = {
    if (pluginsSet.contains(plugin)) pluginsSet.remove(plugin)
    pluginsSet += plugin
    this
  }

  def cleanPlugins(): Unit = pluginsSet.clear()

  def suggests(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val seq: Seq[Future[Seq[RawSuggestion]]] = pluginsSet
      .map(
        p =>
          p.resolve(params)
//       used for debug <- to check origin plugin for suggestion
            .map(r => {
              if (r.nonEmpty) {
                println(s"${p.id} => ${r.length}")
                r.foreach(s => s"${s.newText} => ${s.category}")
              }
              r
            })
      )
      .toSeq
    Future.sequence(seq).map(_.flatten)
  }
}

object CompletionsPluginHandler {

  private val registries: mutable.Map[String, CompletionPluginsRegistryAML] = mutable.Map()

  def pluginSuggestions(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    registries.getOrElse(params.actualDialect.id, AMLBaseCompletionPlugins.base).suggests(params)

  def registerPlugin(plugin: AMLCompletionPlugin, dialect: String): Unit = registries.get(dialect) match {
    case Some(registry) => registry.registerPlugin(plugin)
    case _              => registries.put(dialect, new CompletionPluginsRegistryAML().registerPlugin(plugin))
  }

  def registerPlugins(plugins: Seq[AMLCompletionPlugin], dialect: String): Unit = registries.get(dialect) match {
    case Some(registry) => plugins.foreach(registry.registerPlugin)
    case _ =>
      val p = new CompletionPluginsRegistryAML()
      plugins.foreach(p.registerPlugin)
      registries.put(dialect, p)
  }

  def cleanIndex(): Unit = registries.keys.foreach(registries.remove)
}

object AMLBaseCompletionPlugins {
  val all: Seq[AMLCompletionPlugin] = Seq(
    AMLStructureCompletionPlugin,
    AMLEnumCompletionPlugin,
    AMLRootDeclarationsCompletionPlugin,
    AMLRamlStyleDeclarationsReferences,
    AMLKnownValueCompletionPlugin,
    AMLComponentKeyCompletionPlugin,
    AMLRefTagCompletionPlugin,
    AMLPathCompletionPlugin,
    AMLLibraryPathCompletion,
    AMLBooleanPropertyValue,
    AMLJsonSchemaStyleDeclarationReferences
  )

  val base: CompletionPluginsRegistryAML = {
    val b = new CompletionPluginsRegistryAML
    all.foreach(b.registerPlugin)
    b
  }
}
