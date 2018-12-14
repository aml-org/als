package org.mulesoft.als.suggestions.interfaces

trait ICompletionPluginsRegistry {

    def plugins:Seq[ICompletionPlugin]

    def registerPlugin(plugin:ICompletionPlugin)

    def plugin(id:String):Option[ICompletionPlugin]
}
