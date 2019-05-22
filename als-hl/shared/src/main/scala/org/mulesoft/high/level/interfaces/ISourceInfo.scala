package org.mulesoft.high.level.interfaces

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.positioning.{IPositionsMapper, YamlLocation}
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, NodeRange}
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.YPart

trait ISourceInfo {

  def yamlSources: Seq[YPart]

  def ranges: Seq[NodeRange]

  def jsonNodes: Seq[JSONWrapper] = yamlSources.flatMap(YJSONWrapper(_))

  def offset: Option[Int]

  def valueOffset: Option[Int]

  def containsPosition(pos: Int): Boolean = ranges.exists(_.containsPosition(pos))

  def containsPosition(position: Position): Boolean = ranges.exists(_.containsPosition(position))

  def containsPositionInKey(pos: Int): Boolean =
    yamlSources
      .exists(x => positionsMapper.exists(pm => YamlLocation(x, pm).inKey(pos)))

  def containsPositionInKey(position: Position): Boolean =
    yamlSources
      .exists(x => positionsMapper.exists(pm => YamlLocation(x, pm).inKey(position)))

  def isInitialized: Boolean

  def isEmpty: Boolean

  def content: Option[String]

  def referingUnit: Option[IASTUnit]

  def positionsMapper: Option[IPositionsMapper]

  def includePathLabel: Option[String]

  def externalLocationPath: Option[String]

  def isYAML: Boolean
}
