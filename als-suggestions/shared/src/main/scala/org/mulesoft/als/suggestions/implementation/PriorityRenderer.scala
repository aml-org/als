package org.mulesoft.als.suggestions.implementation

object PriorityRenderer {

  def sortValue(
      isHackathon: Boolean = false,
      isMandatory: Boolean = false,
      isTemplate: Boolean,
      isAnnotation: Boolean,
      isTopLevel: Boolean = false
  ): Int = {
    if (isHackathon) 0
    else if (isTopLevel) 10
    else {
      (if (isTemplate) 200 else 100) + { if (isAnnotation) 100 else 10 } + { if (isMandatory) 10 else 10 }
    }
  }

}
