package org.mulesoft.high.level.interfaces

import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, NodeRange}
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.YPart

trait ISourceInfo {

    def yamlSources:Seq[YPart]

    def ranges:Seq[NodeRange]

    def jsonNodes:Seq[JSONWrapper] = yamlSources.flatMap(YJSONWrapper(_))

    def offset: Option[Int]

    def valueOffset: Option[Int]

    def containsPosition(pos:Int):Boolean = ranges.exists(_.containsPosition(pos))

    def isInitialized:Boolean

    def isEmpty:Boolean

    def content: Option[String]

    def referingUnit:Option[IASTUnit]

    def includePathLabel:Option[String]

    def externalLocationPath:Option[String]

    def isYAML:Boolean
}
