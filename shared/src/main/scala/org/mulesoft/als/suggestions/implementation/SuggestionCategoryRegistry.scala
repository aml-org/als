package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.resources.{Oas20Categories, Raml08Categories, Raml10Categories}
import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition
import amf.core.remote.{Oas, Oas20, Raml08, Raml10, Vendor}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.YDocument
import org.yaml.parser.YamlParser

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

private class LangageCategories private {

    private val map:mutable.Map[String,ListBuffer[SuggestionCategoryEntry]] = mutable.Map()

    def category(text:String, owner:Option[ITypeDefinition], range:Option[ITypeDefinition]):Option[String] = {
        var result = map.get(text).flatMap(list=>{
            var cNameOpt:Option[String] = None
            if(range.isDefined) {
                cNameOpt = list.find(x => x.is.exists(n => range.exists(_.isAssignableFrom(n)))).map(_.categoryName)
            }
            if(cNameOpt.isEmpty && owner.isDefined){
                cNameOpt = list.find(x => x.parentIs.exists(n => owner.exists(_.isAssignableFrom(n)))).map(_.categoryName)
            }
            cNameOpt
        })
        result
    }

    def this(categories:Seq[SuggestionCategoryEntry]){
        this()
        categories.foreach(cat=>{
            var key = cat.text
            var list = map.get(key)
            if(list.isEmpty){
                list = Some(ListBuffer[SuggestionCategoryEntry]())
                map.put(key,list.get)
            }
            list.get += cat
        })
    }
}

private class SuggestionCategoryEntry(val text:String, val parentIs:Seq[String], val is:Seq[String], val categoryName:String) {}


object SuggestionCategoryRegistry {

    private val map:mutable.Map[String,LangageCategories] = mutable.Map()

    def init():Future[Unit] = {

        var raml10Content = Raml10Categories.value
        var raml08Content = Raml08Categories.value
        var oas20Content  = Oas20Categories.value

        map.put(Raml10.toString,new LangageCategories(constructEntries(raml10Content)))
        map.put(Raml08.toString,new LangageCategories(constructEntries(raml08Content)))
        val oasCategories = new LangageCategories(constructEntries(oas20Content))
        map.put(Oas20.toString,oasCategories)
        map.put(Oas.toString,oasCategories)
        Future.successful()
    }

    def getCategory(vendor:Vendor,text:String, owner:Option[ITypeDefinition], range:Option[ITypeDefinition]):Option[String] = map.get(vendor.toString).flatMap(_.category(text,owner,range))

    private def constructEntries(content:String):Seq[SuggestionCategoryEntry] = {
        var result:ListBuffer[SuggestionCategoryEntry] = ListBuffer()
        buildYamlDocument(content).foreach(yDoc =>
            YJSONWrapper(yDoc.node).properties.foreach(cat => {
                var cName = cat.name
                cat.value.properties.foreach(prop => {
                    var pName = prop.name
                    var parentIs:Seq[String] = prop.value.propertyValue("parentIs",ARRAY).map(_.map(_.value(STRING).orNull)).getOrElse(Seq()).filter(x=>Option(x).isDefined)
                    var is:Seq[String] = prop.value.propertyValue("is",ARRAY).map(_.map(_.value(STRING).orNull)).getOrElse(Seq()).filter(x=>Option(x).isDefined)
                    var sce = new SuggestionCategoryEntry(pName,parentIs,is,cName)
                    result += sce
                })
        }))
        result
    }

    private def buildYamlDocument(content:String):Option[YDocument] = {
        val parser = YamlParser(content).withIncludeTag("!include")
        val parts  = parser.parse(true)
        parts.find(_.isInstanceOf[YDocument]).asInstanceOf[Option[YDocument]]
    }
}