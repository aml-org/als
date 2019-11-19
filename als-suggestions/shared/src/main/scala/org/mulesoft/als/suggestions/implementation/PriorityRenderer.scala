package org.mulesoft.als.suggestions.implementation

object PriorityRenderer {

  def sortValue(mandatory: Boolean = false, isTemplate: Boolean, isAnnotation: Boolean): Int =
    (if (isTemplate) 30 else if (mandatory) 10 else 20) + { if (isAnnotation) 10 else 0 }

}
