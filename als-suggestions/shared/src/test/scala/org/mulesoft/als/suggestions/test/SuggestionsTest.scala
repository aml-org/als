package org.mulesoft.als.suggestions.test

import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

trait SuggestionsTest extends AsyncFunSuite with BaseSuggestionsForTest {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def matchCategory(suggestion: CompletionItem): Boolean = {
    suggestion.label.toLowerCase() match {
      case t if t == "securityschemes" =>
        suggestion.detail.map(_.toLowerCase()).contains("security")
      case t if t == "baseuri" =>
        suggestion.detail.map(_.toLowerCase()).contains("root")
      case t if t == "protocols" =>
        suggestion.detail.map(_.toLowerCase()).contains("root")
      case t if t == "uses" =>
        suggestion.detail.map(_.toLowerCase()).contains("unknown")
      case _ => true
    }
  }

  def assertCategory(path: String, suggestions: Set[CompletionItem]): Assertion = {
    if (suggestions.forall(matchCategory))
      succeed
    else fail(s"Difference in categories for $path")
  }

  def assert(path: String, actualSet: Set[String], golden: Set[String]): Assertion = {
    def replaceEOL(s: String): String = {
      s.replace("\r\n", "\n")
    }

    val diff1 = actualSet
      .map(replaceEOL)
      .diff(golden.map(replaceEOL))

    val diff2 = golden
      .map(replaceEOL)
      .diff(actualSet.map(replaceEOL))

    diff1.foreach(println)
    diff2.foreach(println)
    if (diff1.isEmpty && diff2.isEmpty) succeed
    else
      fail(s"Difference for $path: got [${actualSet
        .mkString(", ")}] while expecting [${golden.mkString(", ")}]")
  }

  /**
    * @param path   URI for the API resource
    * @param label  Pointer placeholder
    * @param cut    if true, cuts text after label
    * @param labels set of every label in the file (needed for cleaning API)
    */
  def runTestCategory(path: String,
                      label: String = "*",
                      cut: Boolean = false,
                      labels: Array[String] = Array("*")): Future[Assertion] =
    this
      .suggest(path, label)
      .map(r => assertCategory(path, r.toSet))

  /**
    * @param path                URI for the API resource
    * @param originalSuggestions Expected result set
    * @param label               Pointer placeholder
    * @param cut                 if true, cuts text after label
    * @param labels              set of every label in the file (needed for cleaning API)
    */
  def runSuggestionTest(path: String,
                        originalSuggestions: Set[String],
                        label: String = "*",
                        cut: Boolean = false,
                        labels: Array[String] = Array("*"),
                        dialect: Option[String] = None): Future[Assertion] =
    this
      .suggest(path, label, dialect)
      .map(
        r =>
          assert(path,
                 r.flatMap(s => s.textEdit.map(_.left.get.newText).orElse(s.insertText)).toSet,
                 originalSuggestions))

  def withDialect(path: String, originalSuggestions: Set[String], dialectPath: String): Future[Assertion] = {
    runSuggestionTest(path, originalSuggestions, dialect = Some(filePath(dialectPath)))
  }

  def rootPath: String

  override def suggest(path: String, label: String, dialect: Option[String] = None): Future[Seq[CompletionItem]] = {
    dialect match {
      case Some(d) => platform.resolve(d).flatMap(c => super.suggest(filePath(path), label, Some(c.stream.toString)))
      case _       => super.suggest(filePath(path), label, None)
    }

  }

  case class ModelResult(u: BaseUnit, url: String, position: Int, originalContent: Option[String])

  def parseAMF(path: String, env: Environment = Environment()): Future[BaseUnit] =
    AmfInstance.default.modelBuilder().parse(path, env, None).map(_.baseUnit)

  def filePath(path: String): String = {
    var result =
      s"file://als-suggestions/shared/src/test/resources/test/$rootPath/$path"
        .replace('\\', '/')
    result = result.replace("/null", "")
    result
  }

}
