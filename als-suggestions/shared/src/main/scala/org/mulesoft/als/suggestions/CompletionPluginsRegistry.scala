package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionPluginsRegistry}

import scala.collection.mutable

class CompletionPluginsRegistry private extends ICompletionPluginsRegistry{

    var pluginsMap:mutable.Map[String,ICompletionPlugin] = mutable.Map()

    override def plugins: Seq[ICompletionPlugin] = pluginsMap.values.toList

    override def registerPlugin(plugin: ICompletionPlugin): Unit = pluginsMap(plugin.id) = plugin

    override def plugin(id: String): Option[ICompletionPlugin] = pluginsMap.get(id)
}

object CompletionPluginsRegistry {

    private var _instance = new CompletionPluginsRegistry

    def instance:CompletionPluginsRegistry = _instance

    def registerPlugin(plugin:ICompletionPlugin):Unit = _instance.registerPlugin(plugin)
}