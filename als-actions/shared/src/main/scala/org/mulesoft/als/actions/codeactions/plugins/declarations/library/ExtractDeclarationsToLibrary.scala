package org.mulesoft.als.actions.codeactions.plugins.declarations.library

import amf.core.client.scala.model.document.Module
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.remote.{Mimes, Spec}
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{
  CreatesFileCodeAction,
  DeclaredElementKnowledge,
  ExtractorCommon
}
import org.mulesoft.als.common.YamlWrapper.{YMapEntryOps, YNodeImplicits}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.lsp.edit.{CreateFile, TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_TO_LIBRARY_ACTION,
  END_EXTRACT_TO_LIBRARY_ACTION,
  MessageTypes
}
import org.yaml.model.{YMap, YNode, YPart}
import org.yaml.render.{YamlRender, YamlRenderOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ExtractDeclarationsToLibrary extends CodeActionResponsePlugin with CreatesFileCodeAction {
  protected val kindTitle: CodeActionKindTitle
  protected val params: CodeActionRequestParams

  private val plainName                    = "library"
  override protected val extension: String = "raml"
  private val aliasName                    = ExtractorCommon.nameNotInList("lib", params.bu.definedAliases)

  private def wholeUri: Future[String] = createFileUri(plainName)

  protected def module(lde: List[DomainElement]): Future[Module] = {
    val targetModule = Module()
    lde
      .foreach(targetModule.withDeclaredElement)
    wholeUri.map(targetModule.withLocation)
  }

  protected def moduleRendered(ef: Module): String =
    params.alsConfigurationState
      .configForSpec(params.bu.sourceSpec.getOrElse(Spec.AML))
      .serialize(syntax, ef)

  private val syntax: String =
    Mimes.`application/yaml` // Mimes.`APPLICATION/YAML` // if it finds declared in a RAML, I can't be JSON

  private def moduleTextEdit(ef: Module): Future[(String, TextEdit)] =
    wholeUri.map(uri => (uri, TextEdit(Range(Position(0, 0), Position(0, 0)), moduleRendered(ef))))

  /** Entry for the import of the new library If uses is already defined, insert inside, else create root level uses on
    * top
    * @return
    */
  private def linkEntry(ast: YPart): Future[Option[TextEdit]] = {
    ast match {
      case m: YMap if m.entries.flatMap(e => e.key.asScalar).map(_.text).contains("uses") =>
        m.entries
          .find(_.key.asScalar.map(_.text).contains("uses"))
          .map { uses =>
            finalName(plainName)
              .map(name => YNode(s"$name.raml").withKey(aliasName))
              .map(YamlRender.render(_, yamlOptions.indentationSize, yamlOptions))
              .map { n =>
                val range = LspRangeConverter.toLspRange(PositionRange(uses.value.range))
                Some(TextEdit(Range(range.`end`, range.`end`), s"$n\n"))
              }
          }
          .getOrElse(plainUses(yamlOptions))
      case _ =>
        plainUses(yamlOptions)
    }
  }

  private def plainUses(options: YamlRenderOptions): Future[Option[TextEdit]] = {
    finalName(plainName)
      .map(name => YNode(s"$name.raml").asEntry(aliasName).inMap.withKey("uses"))
      .map(n => YamlRender.render(n, 0, options))
      .map(t => Some(TextEdit(Range(common.Position(1, 0), common.Position(1, 0)), s"$t\n")))
  }

  protected val selectedElements: Seq[DomainElement] =
    DeclaredElementKnowledge.declaredInRange(params.range, params.bu)

  /** Add ALIAS to all references for extracted elements
    *
    * @param lde
    * @param allRelationships
    * @return
    */
  private def modifiedLinks(lde: List[DomainElement], allRelationships: Seq[RelationshipLink]): Seq[TextEdit] =
    lde
      .flatMap(getLinks(_, allRelationships, lde.flatMap(de => de.annotations.ast()).map(a => PositionRange(a.range))))
      .map(LspRangeConverter.toLspRange)
      .map(_.start)
      .map { position =>
        TextEdit(Range(position, position), s"$aliasName.")
      } ++ removeDeclarations(lde)

  private def getLinks(
      element: DomainElement,
      links: Seq[RelationshipLink],
      extractedRanges: Seq[PositionRange]
  ): Seq[PositionRange] =
    links
      .filter(l => element.annotations.ast().contains(l.targetEntry))
      .map(l => PositionRange(l.sourceEntry.range))
      .filterNot(pr =>
        extractedRanges.exists(epr => epr.intersection(pr).isDefined)
      ) // filter out the ones being extracted

  private val yamlOptions: YamlRenderOptions = YamlRenderOptions().withIndentationSize(
    params.configuration
      .getFormatOptionForMime(Mimes.`application/yaml`)
      .tabSize
  )

  /** @param lde
    * @return
    *   Text edits with empty text over extracted declarations
    */
  private def removeDeclarations(lde: List[DomainElement]): Seq[TextEdit] =
    ExtractorCommon
      .existAnyDeclaration(
        lde,
        lde.headOption
          .flatMap(_.annotations.ast())
          .map(a => PositionRange(a.range))
          .map(_.start)
          .map(params.yPartBranch.getCachedOrNew(_, params.uri)),
        params.bu,
        params.definedBy
      )
      // as it is just for RAML (ergo YAML), we will take the whole line as the entry to be erased
      .map(range => Range(Position(range.start.line, 0), LspRangeConverter.toLspPosition(range.`end`)))
      .map(TextEdit(_, ""))

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    params.bu.ast
      .map {
        linkEntry(_)
          .flatMap { le =>
            (le, selectedElements) match {
              case (Some(le), lde: List[DomainElement]) =>
                for {
                  externalFragment <- module(lde)
                  (uri, textEdit)  <- moduleTextEdit(externalFragment)
                } yield {
                  // text edit for each reference to lde in relationships
                  val allEdited: Seq[TextEdit] = modifiedLinks(lde, params.allRelationships) :+ le
                  buildEdit(params.uri, allEdited, uri, textEdit)
                }
              case _ => Future.successful(Seq.empty)
            }
          }
      }
      .getOrElse(Future.successful(Seq.empty))

  private def buildEdit(editUri: String, allEdited: Seq[TextEdit], newUri: String, newTextEdit: TextEdit) =
    Seq(
      kindTitle.baseCodeAction(
        AbstractWorkspaceEdit(
          Seq(
            Right(CreateFile(newUri, None)),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(editUri, None), allEdited)),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(newTextEdit)))
          )
        )
      )
    )

  override protected def code(params: CodeActionRequestParams): String =
    "extract declared element to library code action"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_EXTRACT_TO_LIBRARY_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_EXTRACT_TO_LIBRARY_ACTION

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract element to library : \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri
}
