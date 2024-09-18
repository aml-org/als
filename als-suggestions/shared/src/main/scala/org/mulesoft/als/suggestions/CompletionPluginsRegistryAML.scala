package org.mulesoft.als.suggestions

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.validationprofiles.ValidationProfileTermsSuggestions
import org.mulesoft.als.suggestions.plugins.aml.webapi.extensions.OasLikeSemanticExtensionsFlavour
import org.mulesoft.als.suggestions.plugins.aml.{StructureCompletionPlugin, _}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompletionPluginsRegistryAML {

  def filter(ignoredPlugins: Set[AMLCompletionPlugin]): CompletionPluginsRegistryAML = {
    val cloned = new CompletionPluginsRegistryAML()
    pluginsSet.diff(ignoredPlugins).foreach(cloned.registerPlugin)
    cloned
  }

  private val pluginsSet: mutable.Set[AMLCompletionPlugin] = mutable.Set()

  def registerPlugin(plugin: AMLCompletionPlugin): CompletionPluginsRegistryAML = {
    if (pluginsSet.contains(plugin)) pluginsSet.remove(plugin)
    pluginsSet += plugin
    this
  }

  def cleanPlugins(): Unit = pluginsSet.clear()

  def suggests(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = suggest(params, pluginsSet.toSet)

  private def suggest(
      params: AmlCompletionRequest,
      pluginsSet: Set[AMLCompletionPlugin]
  ): Future[Seq[RawSuggestion]] = {
    val seq: Seq[Future[Seq[RawSuggestion]]] = pluginsSet
      .map(p => p.resolve(params)
//      used for debug <- to check origin plugin for suggestion. TAGS: uncomment, debug
//          .map(r => {
//            if (r.nonEmpty) {
//              println(s"${p.id} => ${r.length}")
//              r.foreach(s => s"${s.newText} => ${s.category}")
//            }
//            r
//          })
      )
      .toSeq
    Future.sequence(seq).map(_.flatten)
  }
}

class CompletionsPluginHandler {

  def filter(ignoredPlugins: Set[AMLCompletionPlugin]): CompletionsPluginHandler = {
    val cloned = new CompletionsPluginHandler()
    registries.foreach { case (d, plugins) =>
      cloned.registries.update(d, plugins.filter(ignoredPlugins))
    }
    cloned
  }

  private val registries: mutable.Map[String, CompletionPluginsRegistryAML] =
    mutable.Map()

  def pluginSuggestions(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    getRegistryForDialect(params.actualDialect)
      .suggests(params)

  private def getRegistryForDialect(dialect: Dialect) =
    registries
      .getOrElse(dialect.id, AMLBaseCompletionPlugins.base)

  def registerPlugin(plugin: AMLCompletionPlugin, dialect: String): Unit =
    registries.get(dialect) match {
      case Some(registry) => registry.registerPlugin(plugin)
      case _ =>
        registries.put(dialect, new CompletionPluginsRegistryAML().registerPlugin(plugin))
    }

  def registerPlugins(plugins: Seq[AMLCompletionPlugin], dialect: String): Unit = registries.get(dialect) match {
    case Some(registry) => plugins.foreach(registry.registerPlugin)
    case _ =>
      val p = new CompletionPluginsRegistryAML()
      plugins.foreach(p.registerPlugin)
      registries.put(dialect, p)
  }

  def cleanIndex(): Unit = registries.clear()
}

object CustomBaseCompletionPlugins {
  var custom: Seq[AMLCompletionPlugin] = Seq.empty
}

object AMLBaseCompletionPlugins {
  lazy val all: Seq[AMLCompletionPlugin] = CustomBaseCompletionPlugins.custom ++ Seq(
    StructureCompletionPlugin(
      List(
        AMLUnionNodeCompletionPlugin,
        AMLUnionRangeCompletionPlugin,
        ValidationProfileTermsSuggestions,
        ResolveDefault
      )
    ),
    AMLEnumCompletionPlugin,
    AMLRootDeclarationsCompletionPlugin,
    AMLRamlStyleDeclarationsReferences,
    AMLKnownValueCompletionPlugin,
    AMLComponentKeyCompletionPlugin,
    AMLRefTagCompletionPlugin,
    AMLPathCompletionPlugin,
    AMLBooleanPropertyValue,
    AMLJsonSchemaStyleDeclarationReferences,
    AMLUnionDiscriminatorCompletionPlugin,
    OasLikeSemanticExtensionsFlavour
  )

  val base: CompletionPluginsRegistryAML = {
    val b = new CompletionPluginsRegistryAML
    all.foreach(b.registerPlugin)
    b
  }
}
