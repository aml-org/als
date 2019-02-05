// $COVERAGE-OFF$
package org.mulesoft.language.common.dtoTypes

import org.mulesoft.language.common.dtoTypes

/**
  * Categories of structure elements.
  */
object StructureCategories extends Enumeration {
  type StructureCategories = Value

  /**
    * Resources
    */
  val ResourcesCategory: dtoTypes.StructureCategories.Value = Value("Resources")

  /**
    * Schemas and types
    */
  val SchemasAndTypesCategory: dtoTypes.StructureCategories.Value = Value("Schemas & Types")

  /**
    * Resource types and traits
    */
  val ResourceTypesAndTraitsCategory: dtoTypes.StructureCategories.Value = Value("Resource Types & Traits")

  /**
    * All other elements.
    */
  val OtherCategory: dtoTypes.StructureCategories.Value = Value("Other")
}

// $COVERAGE-ON$