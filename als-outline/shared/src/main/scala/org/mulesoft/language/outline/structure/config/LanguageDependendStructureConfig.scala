package org.mulesoft.language.outline.structure.config

import org.mulesoft.language.outline.common.commonInterfaces._
import org.mulesoft.language.outline.structure.structureInterfaces.ContentProvider


case class LanguageDependendStructureConfig (
  labelProvider: LabelProvider,
  contentProvider: ContentProvider,
  categories: Map[String, CategoryFilter],
  decorators: Seq[Decorator],
  visibilityFilter: VisibilityFilter
)
{
}
