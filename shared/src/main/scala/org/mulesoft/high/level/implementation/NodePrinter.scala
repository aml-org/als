// $COVERAGE-OFF$
package org.mulesoft.high.level.implementation

import org.mulesoft.typesystem.nominal_interfaces.IPrintDetailsSettings
import org.mulesoft.typesystem.nominal_types.{AbstractType, Array, Described}

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

    def printType(
                 t:AbstractType,
                 indent: String,
                 settings:IPrintDetailsSettings): String = {

        var standardIndent = "  "
        var result:String = ""
        var className = getTypeClassName(t)
        var nameIdValue = t.nameId.getOrElse("")
        result = result + s"$indent$nameIdValue[$className]\n"
        if(t.isArray){
            t.asInstanceOf[Array].componentType.foreach(
                ct=>result += s"$indent${standardIndent}Component type: ${ct.nameId.getOrElse("")}\n")
        }
        if (t.properties.nonEmpty && !settings.hideProperties) {
            result = result + s"$indent${standardIndent}Properties:\n"
            t.properties.foreach(property => {
                var propertyRangeOpt = property.range
                if(propertyRangeOpt.isDefined) {
                    var propertyType = ""
                    var componentType= ""
                    var propertyRange = propertyRangeOpt.get
                    if (propertyRange.isInstanceOf[Described]) {
                        propertyType = propertyType + propertyRange.asInstanceOf[Described].nameId.getOrElse("")
                    }
                    if (propertyRange.isInstanceOf[AbstractType]) {
                        propertyType = propertyType + s"[${getTypeClassName(propertyRange.asInstanceOf[AbstractType])}]"
                    }
                    if(propertyRange.isArray){
                        propertyRange.asInstanceOf[Array].componentType.foreach(
                            ct => componentType = s"$indent${standardIndent*3}Component type: ${ct.nameId.getOrElse("")}\n")
                    }
                    result = result + s"$indent$standardIndent$standardIndent${property.nameId.getOrElse("")}: $propertyType\n$componentType"
                }
            })
        }
        var stArr = t.superTypes
        var filteredSuperTypes = stArr
        if (stArr.nonEmpty && !settings.printStandardSuperclasses) {
            filteredSuperTypes = stArr.filter(st => {
                var name = ""
                var `type` = ""
                st match {
                    case at:AbstractType =>
                        name = if (at.nameId.isDefined) at.nameId.get else ""
                        `type` = getTypeClassName(t)
                    case d:Described => name = if (d.nameId.isDefined) d.nameId.get else ""
                    case _ =>
                }
                !isStandardSuperclass(name, `type`)
            })

        }
        if (filteredSuperTypes.nonEmpty) {
            result = result + s"$indent${standardIndent}Super types:\n"
            filteredSuperTypes.foreach(superType => {
                result = result + superType.printDetails(indent + standardIndent + standardIndent, settings)
            })
        }
        result.toString
    }


    def getTypeClassName(t:Any): String = {
        t.getClass.getName
    }

    def isStandardSuperclass(nId: String, className: String):Boolean = {
        if ((nId == "TypeDeclaration") && (className == "NodeClass"))
            true
        if ((nId == "ObjectTypeDeclaration") && (className == "NodeClass"))
            true
        if ((nId == "RAMLLanguageElement") && (className == "NodeClass"))
            true
        false
    }

}
// $COVERAGE-ON$