package org.mulesoft.als.actions.folding

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.folding.FoldingRange
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class FoldingRangesTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val fragmentUri = "file://fragment.json"
  val fragment: String =
    """{
      |	"Person": {
      |		"type": "object",
      |		"properties": {
      |			"a": {
      |				"type": "string"
      |			}
      |		}
      |	}
      |}""".stripMargin

  behavior of "Folding Ranges"

  it should "Extract range of a YAML Map" in {
    val testUri = "file://test.yaml"
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |title: test
          |description: description test
          |""".stripMargin)
    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(1, Some(0), 2, Some(29), None)
      )
    runTest(testUri, files, expected)
  }

  it should "Extract range of a YAML Sequence" in {
    val testUri = "file://test.yaml"
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |title: test
          |description: description test
          |protocols:
          | - HTTP
          | - HTTPS
          |""".stripMargin)
    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(1, Some(0), 5, Some(8), None),
        FoldingRange(3, Some(10), 5, Some(8), None)
      )
    runTest(testUri, files, expected)
  }

  it should "Extract range of a YAML Sequence inlined" in {
    val testUri = "file://test.yaml"
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |title: test
          |description: description test
          |protocols: [HTTP, HTTPS]
          |""".stripMargin)
    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(1, Some(0), 3, Some(23), None),
        FoldingRange(3, Some(11), 3, Some(23), None)
      )
    runTest(testUri, files, expected)
  }

  it should "Extract ranges of YAML with References" in {
    val testUri = "file://test.yaml"
    val files: Map[String, String] = Map(
      testUri ->
        s"""openapi: "3.0.0"
          |info:
          |  version: 1.0.0
          |  title: api-2
          |paths: {}
          |
          |externalDocs:
          |  - url: url.com
          |    description: desc dummy
          |  - url: fake.com
          |    description: desc fake
          |
          |components:
          |  schemas:
          |    Author:
          |      description: The author of an article.
          |      allOf:
          |        - $$ref: ${fragmentUri}#Person
          |        - $$ref: "#/components/schemas/Author"""".stripMargin,
      fragmentUri -> fragment
    )
    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(0, Some(0), 18, Some(45), None),
        FoldingRange(1, Some(5), 3, Some(14), None),
        FoldingRange(4, Some(7), 4, Some(9), None),
        FoldingRange(6, Some(13), 10, Some(26), None),
        FoldingRange(7, Some(4), 8, Some(27), None),
        FoldingRange(9, Some(4), 10, Some(26), None),
        FoldingRange(12, Some(11), 18, Some(45), None),
        FoldingRange(13, Some(10), 18, Some(45), None),
        FoldingRange(14, Some(11), 18, Some(45), None),
        FoldingRange(16, Some(12), 18, Some(45), None),
        FoldingRange(17, Some(10), 17, Some(43), None),
        FoldingRange(18, Some(10), 18, Some(45), None)
      )
    runTest(testUri, files, expected)
  }

  it should "Extract ranges of complex YAML" in {
    val testUri = "file://dialect.yaml"
    val files: Map[String, String] = Map(
      testUri ->
        """#%Dialect 1.0
          |
          |dialect: Movie
          |version: 1.0
          |
          |external:
          |  schema: https://schema.org/
          |
          |documents:
          |  root:
          |    encodes: MovieNode
          |
          |nodeMappings:
          |    MovieNode:
          |      classTerm: schema.Movie
          |      mapping:
          |        title:
          |          propertyTerm: schema.name
          |          range: string
          |          mandatory: true
          |        image:
          |          propertyTerm: schema.image
          |          range: anyUri
          |        classification:
          |          propertyTerm: schema.contentRating
          |          range: string
          |          enum:
          |            - PG 13
          |            - PG 17
          |            - R
          |          mandatory: true
          |        datePublished:
          |          propertyTerm: schema.datePublished
          |          range: date
          |          mandatory: true""".stripMargin)
    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(2, Some(0), 34, Some(25), None),
        FoldingRange(5, Some(9), 6, Some(29), None),
        FoldingRange(8, Some(10), 10, Some(22), None),
        FoldingRange(9, Some(7), 10, Some(22), None),
        FoldingRange(12, Some(13), 34, Some(25), None),
        FoldingRange(13, Some(14), 34, Some(25), None),
        FoldingRange(15, Some(14), 34, Some(25), None),
        FoldingRange(16, Some(14), 19, Some(25), None),
        FoldingRange(20, Some(14), 22, Some(23), None),
        FoldingRange(23, Some(23), 30, Some(25), None),
        FoldingRange(26, Some(15), 29, Some(15), None),
        FoldingRange(31, Some(22), 34, Some(25), None)
      )
    runTest(testUri, files, expected)
  }

  it should "Extract ranges of JSON with References" in {
    val testUri = "file://test.json"
    val files: Map[String, String] = Map(
      testUri ->
        s"""{
          |  "openapi": "3.0.0",
          |  "info": {
          |    "version": "1.0.0",
          |    "title": "api-2"
          |  },
          |  "paths": {},
          |  "components": {
          |    "schemas": {
          |      "Author": {
          |        "description": "The author of an article.",
          |        "allOf": [
          |          {
          |            "$$ref": "${fragmentUri}#Person"
          |          },
          |          {
          |            "$$ref": "#/components/schemas/Author"
          |          }
          |        ]
          |      }
          |    }
          |  }
          |}"""".stripMargin,
      fragmentUri -> fragment
    )

    val expected: Seq[FoldingRange] =
      Seq(
        FoldingRange(0, Some(0), 16, Some(49), None),
        FoldingRange(2, Some(10), 4, Some(20), None),
        FoldingRange(6, Some(11), 6, Some(13), None),
        FoldingRange(7, Some(16), 16, Some(49), None),
        FoldingRange(8, Some(15), 16, Some(49), None),
        FoldingRange(9, Some(16), 16, Some(49), None),
        FoldingRange(11, Some(17), 16, Some(49), None),
        FoldingRange(12, Some(10), 13, Some(49), None),
        FoldingRange(15, Some(10), 16, Some(49), None)
      )
    runTest(testUri, files, expected)
  }

  private def runTest(testUri: String, files: Map[String, String], expected: Seq[FoldingRange]) = {
    val resourceLoader: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        Future.successful(files.getOrElse(resource, "")).map { c =>
          new Content(c, resource)
        }

      override def accepts(resource: String): Boolean =
        files.keySet.contains(resource)
    }

    val amfConfiguration = AmfConfigurationWrapper(Seq(resourceLoader))
    for {
      result <- amfConfiguration
        .parse(testUri)
        .map(_.result.baseUnit)
        .map(_.objWithAST.flatMap(_.annotations.ast()))
        .map(_.map(FileRanges.ranges)
          .getOrElse(Seq.empty))
    } yield {
      result should be(expected)
    }
  }
}
