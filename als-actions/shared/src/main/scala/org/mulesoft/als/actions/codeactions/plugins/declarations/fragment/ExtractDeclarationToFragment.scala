package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import amf.core.model.document.Fragment
import amf.core.model.domain.DomainElement
import amf.core.parser.Annotations
import amf.core.remote.{Mimes, Vendor}
import amf.plugins.document.webapi.annotations.ForceEntry
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.conversions.ShapeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.FileExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml.FragmentBundle
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_TO_FRAGMENT_ACTION,
  END_EXTRACT_TO_FRAGMENT_ACTION,
  MessageTypes
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ExtractDeclarationToFragment extends CodeActionResponsePlugin with FileExtractor with ShapeExtractor {
  protected val kindTitle: CodeActionKindTitle
  protected def fragmentBundle: Option[FragmentBundle]

  override protected val fileName: Option[String]           = fragmentBundle.map(_.name)
  override protected val fallbackName: String               = "newFile"
  override protected val extension: String                  = "raml"
  override protected val additionalAnnotations: Annotations = Annotations() += ForceEntry()

  protected def externalFragment(de: DomainElement): Future[Fragment] =
    wholeUri.map(fragmentBundle.get.fragment.withEncodes(de).withLocation(_)) // if fragmentBundle is not defined, it shouldnt have reach this code

  protected def externalFragmentRendered(ef: Fragment): Future[String] =
    params.amfInstance
      .modelBuilder()
      .serialize(vendor.name, getSyntax, ef)

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
          } yield buildFileEdit(params.uri, le, uri, textEdit).map(kindTitle.baseCodeAction)
        case _ => Future.successful(Seq.empty)
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
