package org.mulesoft.als.suggestions.plugins.aml.categories

case class CategoryIndex(classTerm: String, property: String)

case class CategoryField(classTerm: String, property: String, category: String)

object CategoryRegistry extends RAML10CategoryRegistry {

  private val allCategories = allRamlCategories

  def apply(classTerm: String, property: String): String = {
    val str = allCategories
      .find(p => p.classTerm == classTerm && p.property == property)
      .map(_.category)
      .getOrElse("unknown")
    str
  }
}
