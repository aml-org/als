package org.mulesoft.als.actions.codeactions.plugins.declarations.library

import amf.core.annotations.DeclaredElement
import amf.core.model.document.{BaseUnit, Document, Module}
import amf.core.model.domain.DomainElement
import amf.core.remote.{Mimes, Vendor}
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.common.YamlWrapper.{YMapEntryOps, YNodeImplicits}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.lsp.edit.{CreateFile, TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_TO_LIBRARY_ACTION,
  END_EXTRACT_TO_LIBRARY_ACTION,
  MessageTypes
}
import org.yaml.model.{YMap, YNode, YPart}
import org.yaml.render.{YamlRender, YamlRenderOptions}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ExtractDeclarationsToLibrary extends CodeActionResponsePlugin {
  protected val kindTitle: CodeActionKindTitle
  protected val params: CodeActionRequestParams

  private val plainName = "library"
  private val aliasName = ExtractorCommon.nameNotInList("lib", params.bu.definedAliases)

  private def finalName(c: Option[Int] = None): Future[String] = {
    val maybeName = s"$plainName${c.getOrElse("")}"
    params.directoryResolver
      .exists(completeUri(maybeName))
      .flatMap {
        case false => Future.successful(maybeName)
        case true  => finalName(Some(c.getOrElse(0) + 1))
      }
  }

  private val relativeUri: String       = params.uri.substring(0, params.uri.lastIndexOf('/') + 1)
  private def wholeUri: Future[String]  = finalName().map(completeUri)
  private def completeUri(name: String) = s"$relativeUri$name.raml"

  protected def module(lde: List[DomainElement]): Future[Module] = {
    val targetModule = Module()
    lde
      .foreach(targetModule.withDeclaredElement)
    wholeUri.map(targetModule.withLocation)
  }

  protected def moduleRendered(ef: Module): Future[String] =
    params.amfInstance
      .modelBuilder()
      .serialize(params.bu.sourceVendor.map(_.name).getOrElse(Vendor.AML.name), syntax, ef)

  private val syntax: String = Mimes.`APPLICATION/YAML` // if it finds declared in a RAML, I can't be JSON

  private def moduleTextEdit(ef: Module): Future[(String, TextEdit)] =
    for {
      r   <- moduleRendered(ef)
      uri <- wholeUri
    } yield (uri, TextEdit(Range(Position(0, 0), Position(0, 0)), r))

  /**
    * Entry for the import of the new library
    * If uses is already defined, insert inside, else create root level uses on top
    * @return
    */
  private def linkEntry(ast: YPart): Future[Option[TextEdit]] = {
    ast match {
      case m: YMap if m.entries.flatMap(e => e.key.asScalar).map(_.text).contains("uses") =>
        m.entries
          .find(_.key.asScalar.map(_.text).contains("uses"))
          .map { uses =>
            finalName()
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
    finalName()
      .map(name => YNode(s"$name.raml").asEntry(aliasName).inMap.withKey("uses"))
      .map(n => YamlRender.render(n, 0, options))
      .map(t => Some(TextEdit(Range(common.Position(1, 0), common.Position(1, 0)), s"$t\n")))
  }

  private def declaredInRange(range: PositionRange, bu: BaseUnit): Seq[DomainElement] = bu match {
    case d: Document =>
      d.declares.filter(domainElementWithinRange(_, range))
    case _ => Seq.empty
  }

  /**
    * @param de
    * @param range
    * @return true if it somehow intersects with the range
    */
  private def domainElementWithinRange(de: DomainElement, range: PositionRange): Boolean =
    de.annotations.ast().map(_.range).map(PositionRange(_)).flatMap(range.intersection).isDefined

  protected val selectedElements: Seq[DomainElement] = declaredInRange(params.range, params.bu)

  /**
    * Add ALIAS to all references for extracted elements
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

  private def getLinks(element: DomainElement,
                       links: Seq[RelationshipLink],
                       extractedRanges: Seq[PositionRange]): Seq[PositionRange] =
    links
      .filter(l => element.annotations.ast().contains(l.targetEntry))
      .map(l => PositionRange(l.sourceEntry.range))
      .filterNot(pr => extractedRanges.exists(epr => epr.intersection(pr).isDefined)) // filter out the ones being extracted

  private val yamlOptions: YamlRenderOptions = YamlRenderOptions().withIndentationSize(
    params.configuration
      .getFormatOptionForMime(Mimes.`APPLICATION/YAML`)
      .indentationSize
  )

  /**
    *
    * @param lde
    * @return Text edits with empty text over extracted declarations
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
        params.dialect
      )
      // as it is just for RAML (ergo YAML), we will take the whole line as the entry to be erased
      .map(range => Range(Position(range.start.line, 0), LspRangeConverter.toLspPosition(range.`end`)))
      .map(TextEdit(_, ""))

  override protected def task(params: CodeActionRequestParams): Future[Seq[CodeAction]] =
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
        WorkspaceEdit(
          Map(editUri -> allEdited, newUri -> Seq(newTextEdit)),
          Seq(
            Right(CreateFile(newUri, None)),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(editUri, None), allEdited)),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(newTextEdit)))
          )
        )
      ))

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
