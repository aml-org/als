package org.mulesoft.language.outline.test

import upickle.default.{macroRW, write, ReadWriter => RW, read}
import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.JvmPlatform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.{IParseResult, IProject}
import org.mulesoft.language.outline.structure.structureImpl.{ConfigFactory, StructureBuilder}
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.scalatest.{Assertion, AsyncFunSuite}
import org.scalatest.{Assertion, Succeeded}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

object File {
    val FILE_PROTOCOL = "file://"

    def unapply(url: String): Option[String] = {
        url match {
            case s if s.startsWith(FILE_PROTOCOL) =>
                val path = s.stripPrefix(FILE_PROTOCOL)
                Some(path)
            case _ => None
        }
    }
}

trait OutlineTest[T] extends AsyncFunSuite with PlatformSecrets {

    implicit override def executionContext:ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global

    def readDataFromAST(project:IProject,position:Int):T

    def readDataFromString(dataString:String):T

    def emptyData():T

    def compare(obj1:T, obj2:T, prefix1:String, prefix2:String):Seq[Diff]

    def runTest(path:String,jsonPath:String):Future[Assertion] = {

        val fullFilePath = filePath(path)
        val fullJsonPath = filePath(jsonPath)

        org.mulesoft.high.level.Core.init()

        this.getActualOutline(fullFilePath).flatMap(actualOutline=>{

            this.getExpectedOutline(fullJsonPath).flatMap(expectedOutlineStr=>{

              val expectedOutline:T = readDataFromString(expectedOutlineStr)
                val diffs = compare(actualOutline,expectedOutline,"actual","expected")
                //var actualJSON = write(ao1,2)
                //platform.write(fullJsonPath,actualJSON)
                if(diffs.isEmpty) {
                    succeed
                }
                else {
                    var message = diffs.mkString("\n")
                    fail(message)
                }
            })
        })
    }

    def format:String
    def rootPath:String

    def bulbLoaders(path: String, content:String): Seq[ResourceLoader] = {
        var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
            override def accepts(resource: String): Boolean = resource == path

            override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
        })
        loaders ++= platform.loaders()
        loaders
    }

    def getExpectedOutline(url: String): Future[String]
        = this.platform.resolve(url).map(_.stream.toString)

    def getActualOutline(url: String): Future[T] = {

        val config = this.buildParserConfig(format, url)

        var position = 0;

        var contentOpt:Option[String] = None
        this.platform.resolve(url).map(content => {

            val fileContentsStr = content.stream.toString
            val markerInfo = this.findMarker(fileContentsStr)

            position = markerInfo.position
            contentOpt = Some(markerInfo.content)
            var env = this.buildEnvironment(url, markerInfo.content, position, content.mime)
            env
        }).flatMap(env=>{

            this.amfParse(config,env)

        }).flatMap(x => x match {
            case amfUnit: BaseUnit => this.buildHighLevel(amfUnit).map(project => {
                readDataFromAST(project,position)
            })
            case _ =>
                Future.successful(emptyData())
        }) recoverWith {
            case e:Throwable =>
                println(e)
                Future.successful(emptyData())
            case _ => Future.successful(emptyData())
        }
    }

    def buildParserConfig(language: String, url: String): ParserConfig = {

        new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(url),
            Some(language),
            Some("application/yaml"),
            None,
            Some("AMF Graph"),
            Some("application/ld+json")
        )
    }

    def amfParse(config: ParserConfig,env:Environment=Environment()): Future[BaseUnit] = {

        val helper = ParserHelper(this.platform)
        helper.parse(config,env)
    }

    def buildEnvironment(fileUrl: String, content: String, position: Int, mime: Option[String]): Environment = {

        var loaders:Seq[ResourceLoader] = List(new ResourceLoader {override def accepts(resource: String): Boolean = resource == fileUrl

            override def fetch(resource: String): Future[Content] = Future.successful(new Content(content,fileUrl))
        })
        loaders ++= platform.loaders()
        var env:Environment = Environment(loaders)
        env
    }
    //  def cacheUnit(fileUrl: String, content: String, position: Int, mime: Option[String]): Unit = {
    //
    //    File.unapply(fileUrl).foreach(x=>this.platform.cacheResourceText(
    //      x, content, mime))
    //  }

    def buildHighLevel(model:BaseUnit):Future[IProject] = {

        Core.init().flatMap(_=>org.mulesoft.high.level.Core.buildModel(model,platform))
    }

    def filePath(path:String):String = {
        var rootDir = System.getProperty("user.dir")
        s"file://$rootDir/shared/src/test/resources/$rootPath/$path".replace('\\','/').replace("null/", "")
    }

    def findMarker(str:String,label:String="*", cut: Boolean = true): MarkerInfo = {

        var position = str.indexOf(label);

        if(position<0){
            new MarkerInfo(str,str.length)
        }
        else {
            var rawContent = str.substring(0, position) + str.substring(position + 1)
            new MarkerInfo(rawContent, position)
        }

    }

    def getStructureFromAST(ast: IParseResult, language: String, position: Int): Map[String, StructureNodeJSON] = {

        val config = ConfigFactory.getConfig(new ASTProvider(ast, position, language))

        if (config.isDefined) {

            val categories = new StructureBuilder(config.get).getStructureForAllCategories

            val result = new mutable.HashMap[String, StructureNodeJSON]()
            categories.keySet.foreach(categoryName=>{
                result(categoryName) = categories(categoryName).toJSON
            })

            result.toMap
        } else {

            Map.empty
        }
    }

    def compareStructureNodeMaps(map1:Map[String,StructureNode],map2:Map[String,StructureNode], prefix1:String,prefix2:String,path:String="/",result:ListBuffer[Diff]=ListBuffer(),noBounds:Boolean=false):Seq[Diff] = {
        compareMapsKeySets(map1, map2, prefix1, prefix2, result, path)
        compareMapsKeySets(map2, map1, prefix2, prefix1, result, path)
        map1.keys filter map2.contains foreach(key => {
            var p = s"$path[$key]"
            compareStructureNodes(map1(key),map2(key),prefix1,prefix2,p,result,noBounds)
        })
        result
    }

    private def compareMapsKeySets(map1: Map[String,_], map2: Map[String,_],prefix1: String, prefix2: String, result: ListBuffer[Diff], path:String):Unit = {
        map1.keys.filter(x => {
            !map2.contains(x)
        }).foreach(key => {
            var message = s"$prefix1 value has '$key' key while $prefix2 value does not"
            result += Diff("map_keys", message, path)
        })
    }

    protected def compareStructureNodes(n1:StructureNode,n2:StructureNode,prefix1:String,prefix2:String,path:String="",result:ListBuffer[Diff]=ListBuffer(),noBounds:Boolean=false):Seq[Diff] = {

        comparePrimitiveValue("text",n1.text,n2.text,prefix1,prefix2,path,result)
        comparePrimitiveValue("typeText",n1.typeText,n2.typeText,prefix1,prefix2,path,result)
        //comparePrimitiveValue("icon",n1.icon,n2.icon,prefix1,prefix2,path,result)
        //comparePrimitiveValue("textStyle",n1.textStyle,n2.textStyle,prefix1,prefix2,path,result)
        comparePrimitiveValue("key",n1.key,n2.key,prefix1,prefix2,path,result)
        if(!noBounds) {
            comparePrimitiveValue("start", n1.start, n2.start, prefix1, prefix2, path, result)
            comparePrimitiveValue("end", n1.end, n2.end, prefix1, prefix2, path, result)
        }
        //comparePrimitiveValue("selected",n1.selected,n2.selected,prefix1,prefix2,path,result)
        comparePrimitiveValue("category",n1.category,n2.category,prefix1,prefix2,path,result)

        if(n1.children.lengthCompare(n2.children.length) != 0){
            var message = s"array lengths mismatch. $prefix1: ${n1.children.length}, $prefix2: ${n2.children.length}"
            var p = s"$path/children"
            result += Diff("array_length", message,p)
        }
        var chMap1:mutable.Map[String,StructureNode] = mutable.Map()
        n1.children.foreach(x=>chMap1.put(x.text,x))
        var chMap2:mutable.Map[String,StructureNode] = mutable.Map()
        n2.children.foreach(x=>chMap2.put(x.text,x))
        compareStructureNodeMaps(chMap1.toMap, chMap2.toMap, prefix1, prefix2, s"$path/children", result)
        result
    }

    private def comparePrimitiveValue(fieldName:String,val1:Any,val2:Any,prefix1:String,prefix2:String,path:String,diffs:ListBuffer[Diff]):Unit = {
        if(val1!= null && val1.isInstanceOf[String] && val1.asInstanceOf[String].isEmpty && val2 ==null){
            return
        }
        if(val2!= null && val2.isInstanceOf[String] && val2.asInstanceOf[String].isEmpty && val1 ==null){
            return
        }
        if(val1 != val2){
            var message = s"values mismatch: $prefix1: $val1, $prefix2: $val2"
            var p = path + "/" + fieldName
            diffs += Diff(fieldName,message,p)
        }
    }
}

class MarkerInfo(val content:String, val position:Int) {}

case class StructureNode(
    /**
     * Node label text to be displayed.
    */
    text: String = null,

    /**
      * Node type label, if any.
    */
    typeText: String = null,

    /**
      * Node icon. Structure module is not setting up, how icons are represented in the client
      * system, or what icons exist,
      * instead the client is responsible to configure the mapping from nodes to icon identifiers.
      */
    icon: String = null,

    /**
      * Text style of the node. Structure module is not setting up, how text styles are represented in the client
      * system, or what text styles exist,
      * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
      */
    textStyle: String = null,

    /**
      * Unique node identifier.
      */
    key: String = null,

    /**
      * Node start position from the beginning of the document.
      */
    start: Int = -1,

    /**
      * Node end position from the beginning of the document.
      */
    end: Int = -1,

    /**
      * Whether the node is selected.
      */
    selected: Boolean = false,

    /**
      * Node children.
      */
    children: Seq[StructureNode] = Seq(),

    /**
      * Node category, if determined by a category filter.
      */
    category: String  = null) {

}

object StructureNode {
    implicit def rw: RW[StructureNode] = macroRW


    implicit def sharedToTransport(
                                      from: StructureNodeJSON): StructureNode = {

        val result = StructureNode(
            from.text,
            from.typeText.orNull,
            from.icon,
            from.textStyle,
            from.key,
            from.start,
            from.end,
            from.selected,
            from.children.map(child=>StructureNode.sharedToTransport(child)),
            from.category
        )
        result
    }
}

class Diff (name:String, message:String, path:String){
    override def toString = s"$name. $path: $message"
}

object Diff {
    def apply(name:String,message: String, path: String): Diff = new Diff(name, message, path)
}