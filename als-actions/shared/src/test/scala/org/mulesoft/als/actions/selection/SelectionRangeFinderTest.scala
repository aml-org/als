package org.mulesoft.als.actions.selection

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.common.{Range, Position => LspPosition}
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.{ExecutionContext, Future}

class SelectionRangeFinderTest extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  it should "select the range on YAML map" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(1, 8))
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |title: test
          |description: description test
          |""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 3, 0)
          .andThen(1, 0, 2, 0)
          .andThen(1, 7, 1, 11)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the range on JSON map" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(6, 18))
    val files: Map[String, String] = Map(
      testUri ->
        """{
          |    "swagger": "2.0",
          |    "info": {
          |      "version": "1.0.0",
          |      "title": "OpenApi Petstore",
          |      "license": {
          |        "name": "MIT"
          |      }
          |    }
          |}""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 9, 1)
          .andThen(2, 4, 8, 5)
          .andThen(5, 6, 7, 7)
          .andThen(6, 8, 6, 21)
          .andThen(6, 16, 6, 21)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the key on YAML map" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(1, 3))
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |title: test
          |description: description test
          |""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 3, 0)
          .andThen(1, 0, 2, 0)
          .andThen(1, 0, 1, 5)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the key on JSON map" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(6, 12))
    val files: Map[String, String] = Map(
      testUri ->
        """{
          |    "swagger": "2.0",
          |    "info": {
          |      "version": "1.0.0",
          |      "title": "OpenApi Petstore",
          |      "license": {
          |        "name": "MIT"
          |      }
          |    }
          |}""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 9, 1)
          .andThen(2, 4, 8, 5)
          .andThen(5, 6, 7, 7)
          .andThen(6, 8, 6, 21)
          .andThen(6, 8, 6, 14)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the range on a YAML sequence" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(17, 5))
    val files: Map[String, String] = Map(
      testUri ->
        """swagger: "2.0"
          |info:
          |  version: 1.0.0
          |  title: OpenApi Petstore
          |  description: A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification
          |  termsOfService: http://openapi.io/terms/
          |  contact:
          |    name: OpenApi API Team
          |    email: apiteam@openapi.io
          |    url: http://openapi.io
          |  license:
          |    name: Apache 2.0
          |    url: https://www.apache.org/licenses/LICENSE-2.0.html
          |host: petstore.openapi.io
          |basePath: /api
          |schemes:
          |  - http
          |  - ws
          |  - wss
          |consumes:
          |  - application/json
          |""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(15, 0, 19, 0)
          .andThen(16, 0, 19, 0)
          .andThen(17, 4, 17, 6)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the range on a JSON sequence" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(13, 8))
    val files: Map[String, String] = Map(
      testUri ->
        """{
          |    "swagger": "2.0",
          |    "info": {
          |      "version": "1.0.0",
          |      "title": "OpenApi Petstore",
          |      "license": {
          |        "name": "MIT"
          |      }
          |    },
          |    "host": "petstore.openapi.io",
          |    "basePath": "/v1",
          |    "schemes": [
          |      "http",
          |      "ws",
          |      "wss"
          |    ],
          |    "consumes": [
          |      "application/json"
          |    ]
          |  }""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 19, 3)
          .andThen(11, 4, 15, 5)
          .andThen(11, 15, 15, 5)
          .andThen(13, 6, 13, 10)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the ranges on YAML with multicursor" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(3, 6), Position(17, 5))
    val files: Map[String, String] = Map(
      testUri ->
        """swagger: "2.0"
          |info:
          |  version: 1.0.0
          |  title: OpenApi Petstore
          |  description: A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification
          |  termsOfService: http://openapi.io/terms/
          |  contact:
          |    name: OpenApi API Team
          |    email: apiteam@openapi.io
          |    url: http://openapi.io
          |  license:
          |    name: Apache 2.0
          |    url: https://www.apache.org/licenses/LICENSE-2.0.html
          |host: petstore.openapi.io
          |basePath: /api
          |schemes:
          |  - http
          |  - ws
          |  - wss
          |consumes:
          |  - application/json
          |""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(1, 0, 13, 0)
          .andThen(3, 2, 4, 0)
          .andThen(3, 2, 3, 7)
          .build(),
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(15, 0, 19, 0)
          .andThen(16, 0, 19, 0)
          .andThen(17, 4, 17, 6)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the ranges on JSON with multicursor" in {
    val testUri                  = "file://test.yaml"
    val positions: Seq[Position] = Seq(Position(6, 10), Position(13, 8))
    val files: Map[String, String] = Map(
      testUri ->
        """{
          |    "swagger": "2.0",
          |    "info": {
          |      "version": "1.0.0",
          |      "title": "OpenApi Petstore",
          |      "license": {
          |        "name": "MIT"
          |      }
          |    },
          |    "host": "petstore.openapi.io",
          |    "basePath": "/v1",
          |    "schemes": [
          |      "http",
          |      "ws",
          |      "wss"
          |    ],
          |    "consumes": [
          |      "application/json"
          |    ]
          |  }""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 19, 3)
          .andThen(2, 4, 8, 5)
          .andThen(5, 6, 7, 7)
          .andThen(6, 8, 6, 21)
          .andThen(6, 8, 6, 14)
          .build(),
        SelectionRangeBuilder(0, 0, 19, 3)
          .andThen(11, 4, 15, 5)
          .andThen(11, 15, 15, 5)
          .andThen(13, 6, 13, 10)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  it should "select the range on an import" in {
    val testUri                  = "file://test.raml"
    val positions: Seq[Position] = Seq(Position(10, 25), Position(10, 35))
    val files: Map[String, String] = Map(
      testUri ->
        """#%RAML 1.0
          |/download/driver/{driverID}:
          |  displayName: driver ID download
          |  get:
          |    is: [client-id-required]
          |    description: get mobile ordering master by driver ID
          |    responses:
          |      200:
          |        body:
          |          application/json:
          |            schema: !include combinedCustomerDownloadSchema.json
          |            example: !include combinedCustomerDownloadExample.json""".stripMargin)
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 11, 66)
          .andThen(1, 0, 11, 66)
          .andThen(3, 2, 11, 66)
          .andThen(6, 4, 11, 66)
          .andThen(7, 6, 11, 66)
          .andThen(8, 8, 11, 66)
          .andThen(9, 10, 11, 66)
          .andThen(10, 12, 11, 0)
          .andThen(10, 20, 10, 64)
          .build(),
        SelectionRangeBuilder(1, 0, 11, 66)
          .andThen(1, 0, 11, 66)
          .andThen(3, 2, 11, 66)
          .andThen(6, 4, 11, 66)
          .andThen(7, 6, 11, 66)
          .andThen(8, 8, 11, 66)
          .andThen(9, 10, 11, 66)
          .andThen(10, 12, 11, 0)
          .andThen(10, 20, 10, 64)
          .andThen(10, 29, 10, 64)
          .build()
      )
    runTest(testUri, files, expected, positions)
  }

  case class SelectionRangeBuilder(fromLine: Int, fromCol: Int, toLine: Int, toCol: Int) {
    private var internal: Option[SelectionRangeBuilder] = None
    def build(): SelectionRange = {
      SelectionRange(Range(LspPosition(fromLine, fromCol), LspPosition(toLine, toCol)), internal.map(_.build()))
    }

    protected def setParent(selectionRangeBuilder: SelectionRangeBuilder): Unit = {
      internal = Some(selectionRangeBuilder)
    }

    def andThen(fromLine: Int, fromCol: Int, toLine: Int, toCol: Int): SelectionRangeBuilder = {
      val builder = SelectionRangeBuilder(fromLine, fromCol, toLine, toCol)
      builder.setParent(this)
      builder
    }

  }

  private def runTest(testUri: String,
                      files: Map[String, String],
                      expected: Seq[SelectionRange],
                      positions: Seq[Position]) = {
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
        .flatMap(ast => {
          Future {
            ast.map(yPart => SelectionRangeFinder.findSelectionRange(yPart, positions)).getOrElse(Seq.empty)
          }
        })
    } yield {
      result should be(expected)
    }
  }
}
