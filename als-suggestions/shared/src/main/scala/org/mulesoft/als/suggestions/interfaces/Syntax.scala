package org.mulesoft.als.suggestions.interfaces

sealed class Syntax private (label:String) {
    override def toString:String = label
}

object Syntax {

    private def apply(name:String):Syntax = new Syntax(name)

    val YAML = Syntax("YAML")

    val JSON = Syntax("JSON")
}
