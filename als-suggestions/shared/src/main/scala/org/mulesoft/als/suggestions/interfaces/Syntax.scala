package org.mulesoft.als.suggestions.interfaces

sealed class Syntax private (label: String) {
  override def toString: String = label
}

object Syntax {

  def apply(name: String): Syntax = {
    if (name == YAML.toString) YAML
    else if (name == JSON.toString) JSON
    else new Syntax(name)
  }

  val YAML = new Syntax("YAML")

  val JSON = new Syntax("JSON")
}
