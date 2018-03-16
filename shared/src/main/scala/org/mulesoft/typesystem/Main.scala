package org.mulesoft.typesystem

import amf.core.client.ExitCodes
import org.mulesoft.typesystem.definition.system.RamlUniverseProvider
import org.mulesoft.typesystem.nominal_interfaces.IUniverse

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

    def main(args:Array[String]): Unit ={

        var result = RamlUniverseProvider.oas20Universe().map(printUniverse2)
        Await.ready(result,1 day)
        System.exit(ExitCodes.Success)
    }

    def printUniverse(u:IUniverse):Unit = u.types.foreach(x=>println(x.printDetails))

    def printUniverse2(u:IUniverse):Unit = {
        var count = 0
        var result:String =
            """#%RAML 1.0 Library
              |
              |annotationTypes:
              |  amfPath:
              |  amfClass:
              |
              |types:
              |""".stripMargin
        u.types.foreach(x=>{
            result += s"  ${x.nameId.get}:\n"
            if(x.superTypes.lengthCompare(1)==0){
                result += s"    type: ${x.superTypes.head.nameId.get}\n"
            }
            else if(x.superTypes.lengthCompare(1) > 0){
                result += s"    type: [${x.superTypes.map(_.nameId.get).mkString(", ")}]\n"
            }
            result += "    (amfClass):\n"
            if(x.properties.nonEmpty){
                result += "    properties:\n"
                x.properties.foreach(y=> {
                    var r = y.range.get
                    var rangeStr = if(r.isArray)r.array.flatMap(_.componentType.map(z=>z.nameId.get+"[]"))else r.nameId
                    result += s"      ${y.nameId.get}:\n"
                    result += s"        type: ${rangeStr.get}\n"
                    result +=  "        (amfPath):\n"
                    count+=1
                })
            }
            result += "\n"
        })
        println(result)
        println(s"Total properties count: $count")
    }
}
