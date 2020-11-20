package org.mulesoft.als.suggestions.test.aml

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.model.document.BaseUnit
import amf.core.model.domain.DomainElement
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstance}
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, NodeMapping, PropertyMapping}
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.patcher.PatchedContent
import org.mulesoft.als.suggestions.test.SuggestionsTest

import scala.concurrent.Future

trait DialectLevelSuggestionsTest extends SuggestionsTest {

  protected case class TestCaseLabel(label: String, dialectClass: Option[String], level: Int)

  protected case class TestCase(path: String, labels: Seq[TestCaseLabel])

  protected case class Result(succeed: Boolean, dialectClass: String, message: Option[String] = None)

  protected case class PositionResult(position: Int, dialectClass: Option[String], level: Int, content: String)

  private def getPropertiesByPath(d: Dialect, nodeName: String): Seq[PropertyMapping] = {
    val de: Option[DomainElement] = d.declares.find(de => de.id endsWith s"/$nodeName")
    de match {
      case Some(n: NodeMapping) => n.propertiesMapping()
      case _                    => Nil
    }
  }

  private def addPropTrailingSpaces(propertyMapping: PropertyMapping, level: Int): String = {
    if (propertyMapping.literalRange().option().isEmpty) {
      propertyMapping.name().value() + ":\n" + (" " * (level * 2))
    } else propertyMapping.name().value() + ": "
  }

  private def getGoldenDialect(nodeName: Option[String], level: Int = 1, bu: BaseUnit): Set[String] = {
    bu match {
      case d: DialectInstance =>
        AMLPlugin().registry.dialectFor(d) match {
          case Some(d: Dialect) =>
            nodeName match {
              case Some(n) => getPropertiesByPath(d, n).map(addPropTrailingSpaces(_, level)).toSet
              case None    => getRootProperties(d)
            }
          case _ => fail(s"Cannot find dialect for ${d.definedBy().value()}")
        }
      case other =>
        fail(s"Dialect Instance expected but ${other.id} found")
    }
  }

  private def getRootProperties(d: Dialect): Set[String] = {
    // all root nodes
    val mapping: DocumentMapping = d.documents().root()
    d.declares
      .find(_.id == mapping.encoded().value())
      .collectFirst({ case n: NodeMapping => n })
      .map(e => e.propertiesMapping().map(addPropTrailingSpaces(_, 1)).toSet)
      .getOrElse(Set.empty) ++ mapping
      .declaredNodes()
      .map(_.name().value() + ":\n" + (" " * 2)) ++ // declared cannot be scalars??
      Seq("uses" + ":\n" + (" " * 2))
  }

  protected def adaptContent(path: String, cases: Seq[TestCaseLabel]): Future[(Content, Seq[PositionResult])] = {
    this.platform.resolve(path).map { content =>
      val (finalContent, finalCases) = buildPositions(content.stream.toString, cases)
      (content.copy(stream = new CharSequenceStream(finalContent)), finalCases)
    }
  }

  private def buildPositions(content: String, cases: Seq[TestCaseLabel]): (String, Seq[PositionResult]) = {
    var partialContent = content
    val results = cases.map { c =>
      val info = this.findMarker(partialContent, c.label)
      partialContent = info.content
      PositionResult(info.offset, c.dialectClass, c.level, info.content)
    }
    (partialContent, results)
  }

  protected def assert(actualSet: Set[String], golden: Set[String]): (Boolean, Option[String]) = {
    val diff1 = actualSet.diff(golden)
    val diff2 = golden.diff(actualSet)

    diff1.foreach(println)
    diff2.foreach(println)

    if (diff1.isEmpty && diff2.isEmpty) (true, None)
    else
      (false, Some(s"Difference: got [${actualSet.mkString(", ")}] while expecting [${golden.mkString(", ")}]"))
  }

}
