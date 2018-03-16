package org.mulesoft.positioning

import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.model._

object YamlSearch {

    def getLocation(position:Int, yPart:YPart, mapper:IPositionsMapper):YamlLocation = {

        val range = YRange(yPart)
        val offset = mapper.offset(position)
        mapper.initRange(range)
        if(!range.containsPosition(position)){
            YamlLocation.empty
        }
        else if(offset<mapper.offset(range.start.position)){
            YamlLocation.empty
        }
        else {

            yPart match {
                case mapEntry: YMapEntry =>
                    val thisLocation = YamlLocation(mapEntry, mapper)
                    var keyLocation = getLocation(position,mapEntry.key,mapper)
                    if(keyLocation.nonEmpty){
                        thisLocation
                    }
                    else if(offset<=mapper.offset(YRange(mapEntry.key).start.position)){
                        YamlLocation.empty
                    }
                    else {
                        var valLocation = getLocation(position,mapEntry.value,mapper)
                        if(valLocation.isEmpty){
                            thisLocation
                        }
                        else if (thisLocation.hasSameNode(valLocation)) {
                            thisLocation
                        }
                        else {
                            valLocation
                        }
                    }

                case node:YNode =>
                    var thisLocation = YamlLocation(node,mapper)
                    var valLocation = getLocation(position,node.value,mapper)
                    if(valLocation.isEmpty){
                        thisLocation
                    }
                    else if (thisLocation.hasSameValue(valLocation)){
                        thisLocation
                    }
                    else {
                        valLocation
                    }

                case map:YMap =>
                    var entryOpt = map.entries.find(me=>{
                        var r = YRange(me)
                        mapper.initRange(r)
                        r.containsPosition(position)
                    })
                    val thisLocation = YamlLocation (map, mapper)
                    entryOpt match {
                        case Some(me) =>
                            var entryLocation = getLocation(position, me, mapper)
                            if (entryLocation.nonEmpty) {
                                entryLocation
                            }
                            else {
                                thisLocation
                            }
                        case None =>
                            thisLocation
                    }

                case sequence:YSequence =>
                    val thisLocation = YamlLocation (sequence, mapper)
                    var componentOpt = sequence.nodes.find(n=>{
                        var r = YRange(n)
                        mapper.initRange(r)
                        r.containsPosition(position)
                    })
                    componentOpt match {
                        case Some(me) =>
                            var componentLocation = getLocation(position, me, mapper)
                            if (componentLocation.nonEmpty) {
                                componentLocation
                            }
                            else {
                                thisLocation
                            }
                        case None => YamlLocation(sequence,mapper)
                    }

                case scalar:YScalar => YamlLocation(scalar,mapper)

                case _ => YamlLocation(yPart,mapper)
            }
        }
    }

}
