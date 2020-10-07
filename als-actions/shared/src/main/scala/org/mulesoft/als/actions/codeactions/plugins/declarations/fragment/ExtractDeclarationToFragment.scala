package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import amf.core.annotations.ExternalFragmentRef
import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.Fragment
import amf.core.model.domain.{DomainElement, Linkable}
import amf.core.remote.{Mimes, Vendor}
import amf.plugins.document.webapi.annotations.ForceEntry
import amf.plugins.document.webapi.parser.spec.common.emitters.WebApiDomainElementEmitter
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.BaseElementDeclarableExtractors
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml.FragmentBundle
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.lsp.edit.{CreateFile, TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_TO_FRAGMENT_ACTION,
  END_EXTRACT_TO_FRAGMENT_ACTION,
  MessageTypes
}
import org.yaml.model.YNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ExtractDeclarationToFragment extends CodeActionResponsePlugin with BaseElementDeclarableExtractors {
  protected val kindTitle: CodeActionKindTitle
  protected def fragmentBundle: Option[FragmentBundle]
  private val plainName = "newFile"

  private def finalName(c: Option[Int] = None): Future[String] = {
    val maybeName = s"${fragmentBundle.map(_.name).getOrElse(plainName)}${c.getOrElse("")}"
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

  protected def externalFragment(de: DomainElement): Future[Fragment] =
    wholeUri.map(fragmentBundle.get.fragment.withEncodes(de).withLocation(_)) // if fragmentBundle is not defined, it shouldnt have reach this code

  protected def externalFragmentRendered(ef: Fragment): Future[String] =
    params.amfInstance
      .modelBuilder()
      .serialize(params.bu.sourceVendor.map(_.name).getOrElse(Vendor.AML.name), getSyntax, ef)

  private def getSyntax: String =
    if (yPartBranch.exists(_.isJson))
      Mimes.`APPLICATION/JSON`
    else Mimes.`APPLICATION/YAML`

  private def externalFragmentTextEdit(ef: Fragment): Future[(String, TextEdit)] =
    for {
      r   <- externalFragmentRendered(ef)
      uri <- wholeUri
    } yield (uri, TextEdit(Range(Position(0, 0), Position(0, 0)), r))

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    linkEntry.flatMap { mle =>
      (mle, amfObject) match {
        case (Some(le), Some(de: DomainElement)) =>
          for {
            externalFragment <- externalFragment(de)
            (uri, textEdit)  <- externalFragmentTextEdit(externalFragment)
          } yield buildEdit(params.uri, le, uri, textEdit)
        case _ => Future.successful(Seq.empty)
      }
    }

  private def buildEdit(editUri: String, editTextEdit: TextEdit, newUri: String, newTextEdit: TextEdit) = {
    Seq(
      kindTitle.baseCodeAction(
        AbstractWorkspaceEdit(
          Seq(
            Right(CreateFile(newUri, None)),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(editUri, None), Seq(editTextEdit))),
            Left(TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(newTextEdit)))
          )
        )
      ))
  }

  override protected val renderLink: Future[Option[YNode]] = // todo: move just for inlined type/example?
    finalName().map { name =>
      amfObject
        .collect {
          case l: Linkable =>
            val asRaml                = s"$name.raml"
            val linkDe: DomainElement = l.link(asRaml)
            linkDe.annotations += ExternalFragmentRef(asRaml)
            linkDe.annotations += ForceEntry() // raml explicit types
            WebApiDomainElementEmitter
              .emit(linkDe, vendor, UnhandledErrorHandler)
        }
    }

  override protected def code(params: CodeActionRequestParams): String =
    "extract declared element to fragment code action"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_EXTRACT_TO_FRAGMENT_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_EXTRACT_TO_FRAGMENT_ACTION

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract element to fragment : \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri
}
