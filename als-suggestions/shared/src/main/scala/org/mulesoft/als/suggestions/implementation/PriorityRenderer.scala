package org.mulesoft.als.suggestions.implementation

object PriorityRenderer {

  def sortValue(
      isMandatory: Boolean = false,
      isTemplate: Boolean,
      isAnnotation: Boolean,
      isTopLevel: Boolean = false
  ): Int =
    if (isTopLevel) 0
    else {
      (if (isTemplate) 20 else 10) + { if (isAnnotation) 10 else 0 } + { if (isMandatory) 0 else 1 }
    }

}
