package org.mulesoft.language.common.typeInterfaces

/**
  * Categories of structure elements.
  */
object StructureCategories extends Enumeration {

  /**
    * Resources
    */
  val ResourcesCategory = Value("Resources")

  /**
    * Schemas and types
    */
  val SchemasAndTypesCategory = Value("Schemas & Types")

  /**
    * Resource types and traits
    */
  val ResourceTypesAndTraitsCategory = Value("Resource Types & Traits")

  /**
    * All other elements.
    */
  val OtherCategory = Value("Other")
}
