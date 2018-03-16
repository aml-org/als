package org.mulesoft.als.suggestions.interfaces

sealed class Syntax private (label:String) {
    override def toString:String = label
}

object Syntax {

    private def apply():Syntax = new Syntax("YAML")

    val YAML = Syntax()
}
