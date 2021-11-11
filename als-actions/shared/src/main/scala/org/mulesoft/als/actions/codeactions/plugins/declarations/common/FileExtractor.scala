package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.client.scala.model.domain.{DomainElement, Linkable}
import amf.core.internal.annotations.ExternalFragmentRef
import amf.core.internal.parser.domain.Annotations
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.amfintegration.amfconfiguration.ProfileMatcher
import org.mulesoft.lsp.edit.{CreateFile, TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier
import org.yaml.model.YNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FileExtractor extends BaseElementDeclarableExtractors {
  protected def fallbackName: String
  protected val fileName: Option[String] = None
  protected val extension: String
  protected val additionalAnnotations: Annotations

  protected def finalName(c: Option[Int] = None): Future[String] = {
    val maybeName = s"${fileName.getOrElse(fallbackName)}${c.getOrElse("")}"
    params.directoryResolver
      .exists(completeUri(maybeName))
      .flatMap {
        case false => Future.successful(maybeName)
        case true  => finalName(Some(c.getOrElse(0) + 1))
      }
  }

  protected val relativeUri: String       = params.uri.substring(0, params.uri.lastIndexOf('/') + 1)
  protected def wholeUri: Future[String]  = finalName().map(completeUri)
  protected def completeUri(name: String) = s"$relativeUri$name.$extension"

  override protected lazy val renderLink: Future[Option[YNode]] =
    finalName().map { name =>
      amfObject
        .collect {
          case l: Linkable =>
            val fileName              = s"$name.$extension"
            val linkDe: DomainElement = l.link(fileName)
            linkDe.annotations += ExternalFragmentRef(fileName)
            linkDe.annotations ++= additionalAnnotations
            params.alsConfigurationState.configForSpec(spec).emit(linkDe)
        }
    }

  protected def buildFileEdit(editUri: String,
                              editTextEdit: TextEdit,
                              newUri: String,
                              newTextEdit: TextEdit): Seq[AbstractWorkspaceEdit] = {
    Seq(
      AbstractWorkspaceEdit(
        Seq(
          Right(CreateFile(newUri, None)),
          Left(TextDocumentEdit(VersionedTextDocumentIdentifier(editUri, None), Seq(editTextEdit))),
          Left(TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(newTextEdit)))
        )
      ))
  }

}
