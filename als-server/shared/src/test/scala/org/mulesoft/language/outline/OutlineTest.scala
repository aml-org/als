package org.mulesoft.language.outline

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.language.common.dtoTypes.IOpenedDocument
import org.mulesoft.language.test.LanguageServerTest
import org.mulesoft.language.test.dtoTypes.StructureNode
import org.scalatest.Assertion

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

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

trait OutlineTest[SharedType, TransportType] extends LanguageServerTest {

  def readDataFromString(dataString: String): TransportType

  def compare(obj1: SharedType, obj2: TransportType, prefix1: String, prefix2: String): Seq[Diff]

  def serialize(obj: SharedType): String

  def runTest(path: String, jsonPath: String): Future[Assertion] = {

    val fullFilePath = filePath(path)
    val fullJsonPath = filePath(jsonPath)

    for {
      _ <- org.mulesoft.high.level.Core.init()
      actualOutline <- this.getActualOutline(fullFilePath, path)
      expectedOutlineStr <- this.getExpectedOutline(fullJsonPath)
    } yield {
      val expectedOutline: TransportType = readDataFromString(expectedOutlineStr)
      val diffs = compare(actualOutline, expectedOutline, "actual", "expected")
      if (diffs.isEmpty) {
        succeed
      } else {
        // var actualJSON = serialize(actualOutline)
        // platform.write(fullJsonPath,actualJSON)
        val message = diffs.mkString("\n")
        fail(message)
      }
    }
  }

  def bulbLoaders(path: String, content: String): Seq[ResourceLoader] = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    loaders ++= platform.loaders()
    loaders
  }

  def getExpectedOutline(url: String): Future[String] = this.platform.resolve(url).map(_.stream.toString)

  def getActualOutline(url: String, shortUrl: String): Future[SharedType] = {

    var position = 0

    var contentOpt: Option[String] = None
    this.platform
      .resolve(url)
      .flatMap(content => {

        val doc = IOpenedDocument(shortUrl, 0, content.stream.toString)
        getClient.flatMap(client => {
          client.documentOpened(doc)
          client
            .getStructure(shortUrl)
            .map(result => {
              client.documentClosed(shortUrl)
              result
            })
        })
      })
      .map(x => x.asInstanceOf[SharedType])
  }

  def buildEnvironment(fileUrl: String, content: String, position: Int, mime: Option[String]): Environment = {

    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    })
    loaders ++= platform.loaders()
    val env: Environment = Environment(loaders)
    env
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    val position = str.indexOf(label)

    if (position < 0) {
      new MarkerInfo(str, str.length)
    } else {
      val rawContent = str.substring(0, position) + str.substring(position + 1)
      new MarkerInfo(rawContent, position)
    }

  }

  def compareStructureNodeMaps(map1: Map[String, StructureNode],
                               map2: Map[String, StructureNode],
                               prefix1: String,
                               prefix2: String,
                               path: String = "/",
                               result: ListBuffer[Diff] = ListBuffer(),
                               noBounds: Boolean = false): Seq[Diff] = {
    compareMapsKeySets(map1, map2, prefix1, prefix2, result, path)
    compareMapsKeySets(map2, map1, prefix2, prefix1, result, path)
    map1.keys filter map2.contains foreach (key => {
      var p = s"$path[$key]"
      compareStructureNodes(map1(key), map2(key), prefix1, prefix2, p, result, noBounds)
    })
    result
  }

  private def compareMapsKeySets(map1: Map[String, _],
                                 map2: Map[String, _],
                                 prefix1: String,
                                 prefix2: String,
                                 result: ListBuffer[Diff],
                                 path: String): Unit = {
    map1.keys
      .filter(x => {
        !map2.contains(x)
      })
      .foreach(key => {
        var message = s"$prefix1 value has '$key' key while $prefix2 value does not"
        result += Diff("map_keys", message, path)
      })
  }

  protected def compareStructureNodes(n1: StructureNode,
                                      n2: StructureNode,
                                      prefix1: String,
                                      prefix2: String,
                                      path: String = "",
                                      result: ListBuffer[Diff] = ListBuffer(),
                                      noBounds: Boolean = false): Seq[Diff] = {

    comparePrimitiveValue("text", n1.text, n2.text, prefix1, prefix2, path, result)
    comparePrimitiveValue("typeText", n1.typeText, n2.typeText, prefix1, prefix2, path, result)
    //comparePrimitiveValue("icon",n1.icon,n2.icon,prefix1,prefix2,path,result)
    //comparePrimitiveValue("textStyle",n1.textStyle,n2.textStyle,prefix1,prefix2,path,result)
    comparePrimitiveValue("key", n1.key, n2.key, prefix1, prefix2, path, result)
    if (!noBounds && n2.start > 0 && n1.end > 0) {
      comparePrimitiveValue("start", n1.start, n2.start, prefix1, prefix2, path, result)
      comparePrimitiveValue("end", n1.end, n2.end, prefix1, prefix2, path, result)
    }
    //comparePrimitiveValue("selected",n1.selected,n2.selected,prefix1,prefix2,path,result)
    comparePrimitiveValue("category", n1.category, n2.category, prefix1, prefix2, path, result)

    if (n1.children.lengthCompare(n2.children.length) != 0) {
      var message = s"array lengths mismatch. $prefix1: ${n1.children.length}, $prefix2: ${n2.children.length}"
      var p = s"$path/children"
      result += Diff("array_length", message, p)
    }
    var chMap1: mutable.Map[String, StructureNode] = mutable.Map()
    n1.children.foreach(x => chMap1.put(x.text, x))
    var chMap2: mutable.Map[String, StructureNode] = mutable.Map()
    n2.children.foreach(x => chMap2.put(x.text, x))
    compareStructureNodeMaps(chMap1.toMap, chMap2.toMap, prefix1, prefix2, s"$path/children", result)
    result
  }

  private def comparePrimitiveValue(fieldName: String,
                                    val1: Any,
                                    val2: Any,
                                    prefix1: String,
                                    prefix2: String,
                                    path: String,
                                    diffs: ListBuffer[Diff]): Unit = {
    if (val1 != null && val1.isInstanceOf[String] && val1.asInstanceOf[String].isEmpty && val2 == null) {
      return
    }
    if (val2 != null && val2.isInstanceOf[String] && val2.asInstanceOf[String].isEmpty && val1 == null) {
      return
    }
    if (val1 == null && val2 != null && val2.isInstanceOf[Option[_]]) {
      val opt = val2.asInstanceOf[Option[_]]
      if (opt.isEmpty) {
        return
      }
      if (opt.contains("")) {
        return
      }
    }
    if (val2 == null && val1 != null && val1.isInstanceOf[Option[_]]) {
      val opt = val1.asInstanceOf[Option[_]]
      if (opt.isEmpty) {
        return
      }
      if (opt.contains("")) {
        return
      }
    }
    if (val1 != val2) {
      val message = s"values mismatch: $prefix1: $val1, $prefix2: $val2"
      val p = path + "/" + fieldName
      diffs += Diff(fieldName, message, p)
    }
  }
}

class MarkerInfo(val content: String, val position: Int) {}

class Diff(name: String, message: String, path: String) {
  override def toString = s"$name. $path: $message"
}

object Diff {
  def apply(name: String, message: String, path: String): Diff = new Diff(name, message, path)
}
