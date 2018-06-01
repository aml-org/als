package org.mulesoft.language.outline.structure.structureInterfaces

import org.mulesoft.language.outline.common.commonInterfaces._

case class StructureConfiguration (
  astProvider: IASTProvider,
  labelProvider: LabelProvider,
  contentProvider: ContentProvider,
  categories: Map[String, CategoryFilter],
  decorators: Seq[Decorator],
  keyProvider: KeyProvider,
  visibilityFilter: VisibilityFilter
)
{
}
