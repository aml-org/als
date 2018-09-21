package org.mulesoft.high.level.implementation

object NodePrinter {

    def printElement(node:ASTNodeImpl, indent:String = ""):String = {
        val definition = node.definition
        val property = node.property
        val children = node.children
        val sourceInfo = node.sourceInfo
        var classname = definition.nameId.getOrElse("")
        var definitionClassName:String = printDefinitionClassName(node)
        var parentPropertyName = property.flatMap(_.nameId).getOrElse("")
        var result:StringBuilder = StringBuilder.newBuilder
        result.append(s"$indent$classname[$definitionClassName] <--- $parentPropertyName\n")
        children.foreach(x=>result.append(x.printDetails(indent+"  ")))
        result.append(s"$indent  #range: ${sourceInfo.ranges.head}\n")
        result.toString()
    }

    def printProperty(node: ASTPropImpl, indent: String = ""): String = {
        val definition = node.definition
        val name = node.name
        val value = node.value
        val sourceInfo = node.sourceInfo
        var className = if (definition.isDefined) definition.get.nameId.getOrElse("") else ""
        var definitionClassName: String = printDefinitionClassName(node)
        var result = s"$indent$name: $className[$definitionClassName]  =  ${value.getOrElse("$None")}"
        var ranges = sourceInfo.ranges
        if (ranges.lengthCompare(1) == 0) {
            result += s"; #range: ${ranges.head}"
        }
        else {
            result += (s"\n$indent #ranges:" + ranges.map(r => s"\n$indent  " + r).mkString(""))
        }
        result += "\n"
        result
    }

    def printDefinitionClassName(node:BasicASTNode):String = {
        val property = node.property
        (for {
            prop <- property
            range <- prop.range
            n <- range.nameId
        } yield {
            var result:String = range.nameId.getOrElse("")
            if(range.isArray){
                for {
                    ct <- range.array.get.componentType
                    ctName <- ct.nameId
                }
                    yield {
                        result = result + s"(array[$ctName])"
                    }
            }
            result
        }).getOrElse("")
    }

    def printNode(node:BasicASTNode,indent: String): String = s"${indent}Unkown\n"

}
