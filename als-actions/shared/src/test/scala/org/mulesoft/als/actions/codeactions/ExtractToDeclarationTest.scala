package org.mulesoft.als.actions.codeactions

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.{ExtractElementCodeAction, ExtractRAMLTypeCodeAction}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.common.{Range, VersionedTextDocumentIdentifier, Position => LspPosition}

class ExtractToDeclarationTest extends BaseCodeActionTests {
  behavior of "Extract element to declaration"

  it should "extract a schema from open api 3 parameter" in {
    val elementUri                       = "extract-element/schema-from-oas/schema.yaml"
    val range                            = PositionRange(Position(10, 15), Position(10, 16))
    val dialect: Option[Dialect]         = Some(OAS30Dialect.dialect)
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    val changes: Map[String, Seq[TextEdit]] = Map(
      "file://als-actions/shared/src/test/resources/codeactions/extract-element/schema-from-oas/schema.yaml" ->
        Seq(
          TextEdit(Range(LspPosition(9, 17), LspPosition(11, 30)),
                   """
            |              $ref: $1""".stripMargin),
          TextEdit(
            Range(LspPosition(5, 0), LspPosition(5, 0)),
            """
              |  schemas:
              |    $1:
              |      type: string
              |      example: textplain
              |""".stripMargin
          )
        ))
    val expected: Seq[CodeAction] = createResponse(pluginFactory, changes)

    runTest(elementUri, range, dialect, pluginFactory, expected)
  }

  it should "extract a schema from oas 3 json" in {
    val elementUri                       = "extract-element/schema-from-oas/schema.json"
    val range                            = PositionRange(Position(13, 19), Position(13, 20))
    val dialect: Option[Dialect]         = Some(OAS30Dialect.dialect)
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    val changes: Map[String, Seq[TextEdit]] = Map(
      "file://als-actions/shared/src/test/resources/codeactions/extract-element/schema-from-oas/schema.json" ->
        Seq(
          TextEdit(Range(LspPosition(12, 22), LspPosition(15, 13)),
                   """{
              |                "$ref": "$1"
              |              }""".stripMargin),
          TextEdit(
            Range(LspPosition(6, 3), LspPosition(6, 3)),
            """
              |{
              |      "schemas": {
              |        "$1": {
              |          "type": "string",
              |          "example": "textplain"
              |        }
              |      }
              |    }
              |""".stripMargin
          )
        ))
    val expected: Seq[CodeAction] = createResponse(pluginFactory, changes)

    runTest(elementUri, range, dialect, pluginFactory, expected)
  }

  it should "extract a type from RAML 1.0 payload" in {
    val elementUri                       = "extract-element/raml-type/raml-type.raml"
    val range                            = PositionRange(Position(15, 27), Position(16, 26))
    val dialect: Option[Dialect]         = Some(Raml10TypesDialect.dialect)
    val pluginFactory: CodeActionFactory = ExtractRAMLTypeCodeAction

    val changes: Map[String, Seq[TextEdit]] = Map(
      "file://als-actions/shared/src/test/resources/codeactions/extract-element/raml-type/raml-type.raml" ->
        Seq(
          TextEdit(Range(LspPosition(14, 22), LspPosition(17, 36)),
                   """
              |                          type: $1
              |""".stripMargin),
          TextEdit(
            Range(LspPosition(8, 0), LspPosition(8, 0)),
            """
              |  $1:
              |    type: object
              |    properties:
              |      B: other
              |""".stripMargin
          )
        ))
    val expected: Seq[CodeAction] = createResponse(pluginFactory, changes)

    runTest(elementUri, range, dialect, pluginFactory, expected)
  }

  private def createResponse(pluginFactory: CodeActionFactory, changes: Map[String, Seq[TextEdit]]): Seq[CodeAction] =
    Seq(
      pluginFactory.baseCodeAction(
        WorkspaceEdit(
          changes,
          changes.map(t => Left(TextDocumentEdit(VersionedTextDocumentIdentifier(t._1, None), t._2))).toList)
      ))
}
