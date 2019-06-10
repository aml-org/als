// $COVERAGE-OFF$
package org.mulesoft.positioning

import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.model._

object YamlSearch {
  private def nodeStartOffset(yPart: YPart, range: YRange, mapper: IPositionsMapper): Int = yPart match {
    case node: YNode =>
      if (node.children.isEmpty || node.children.length < 3) {
        mapper.offset(range.start.position)
      } else {
        var nodeContent = node.children(1)

        val map   = node.children.find(_.isInstanceOf[YMap]).map(_.asInstanceOf[YMap])
        val entry = map.flatMap(_.entries.headOption)
        val range = YRange(entry.getOrElse(node), Option(mapper))
        mapper.offset(range.start.position)
      }

    case _ => mapper.offset(range.start.position)
  }

  def getLocation(position: Int,
                  yPart: YPart,
                  mapper: IPositionsMapper,
                  parentStack: List[YamlLocation] = List(),
                  ignoreIndents: Boolean = false): YamlLocation = {
    val range  = YRange(yPart, Option(mapper))
    val offset = mapper.offset(position)

    if (!range.containsPosition(position)) {
      YamlLocation.empty
    } else if (!ignoreIndents && offset < nodeStartOffset(yPart, range, mapper)) {
      YamlLocation.empty
    } else
      yPart match {
        case mapEntry: YMapEntry =>
          val thisLocation = YamlLocation(mapEntry, mapper, parentStack)
          val keyLocation  = getLocation(position, mapEntry.key, mapper, thisLocation :: parentStack, ignoreIndents)
          if (keyLocation.nonEmpty) {
            thisLocation
          } else if (offset <= mapper.offset(YRange(mapEntry.key, Option(mapper)).start.position)) {
            YamlLocation.empty
          } else {
            val valLocation = getLocation(position, mapEntry.value, mapper, thisLocation :: parentStack, ignoreIndents)
            if (valLocation.isEmpty) {
              thisLocation
            } else if (thisLocation.hasSameNode(valLocation)) {
              thisLocation
            } else {
              valLocation
            }
          }

        case node: YNode =>
          val thisLocation = YamlLocation(node, mapper, parentStack)
          val valLocation  = getLocation(position, node.value, mapper, thisLocation :: parentStack, ignoreIndents)
          if (valLocation.isEmpty) {
            thisLocation
          } else if (thisLocation.hasSameValue(valLocation)) {
            thisLocation
          } else {
            valLocation
          }

        case map: YMap =>
          val entryOpt = map.entries.find(me => {
            val r = YRange(me, Option(mapper))
            mapper.initRange(r)
            r.containsPosition(position)
          })
          val thisLocation = YamlLocation(map, mapper, parentStack)
          entryOpt match {
            case Some(me) =>
              val entryLocation = getLocation(position, me, mapper, thisLocation :: parentStack, ignoreIndents)
              if (entryLocation.nonEmpty) {
                entryLocation
              } else {
                thisLocation
              }
            case None =>
              thisLocation
          }

        case sequence: YSequence =>
          val thisLocation = YamlLocation(sequence, mapper, parentStack)
          val componentOpt = sequence.nodes.find(n => {
            val r = YRange(n, Option(mapper))
            mapper.initRange(r)
            r.containsPosition(position)
          })
          componentOpt match {
            case Some(me) =>
              val componentLocation = getLocation(position, me, mapper, thisLocation :: parentStack, ignoreIndents)
              if (componentLocation.nonEmpty) {
                componentLocation
              } else {
                thisLocation
              }
            case None => YamlLocation(sequence, mapper, parentStack)
          }

        case scalar: YScalar => YamlLocation(scalar, mapper, parentStack)

        case doc: YDocument => getLocation(position, doc.node, mapper, parentStack, ignoreIndents)

        case _ => YamlLocation(yPart, mapper, parentStack)
      }
  }

}

// $COVERAGE-ON$
