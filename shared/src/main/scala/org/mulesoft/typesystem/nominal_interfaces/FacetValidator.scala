package org.mulesoft.typesystem.nominal_interfaces

trait FacetValidator {
    def apply(value: Any, facetValue: Any): String
}
