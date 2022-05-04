package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.aml.client.scala.model.document.{Dialect, Vocabulary}
import amf.aml.client.scala.model.domain.{ClassTerm, External, NodeMapping, PropertyMapping, PropertyTerm}
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionRequestParams
import org.mulesoft.als.common.ASTElementWrapper.AlsPositionRange
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExternalVocabularyToLocalAction(
    dialect: Dialect,
    external: External,
    override val params: CodeActionRequestParams
) extends DialectActionsHelper {

  val base: String                         = external.base.value()
  val alias: String                        = external.alias.value()
  var knownClassTerms: Map[String, String] = Map()

  def task(): Future[Seq[AbstractCodeAction]] =
    for {
      vocabulary <- transformExternalToLocal()
      edits      <- createAbstractWorkspaceEdit(vocabulary)
    } yield Seq(ExternalVocabularyToLocalCodeAction.baseCodeAction(edits))

  def transformExternalToLocal(): Future[Vocabulary] = Future {
    val nodeMappings: Map[String, Seq[NodeMapping]] =
      collectNodeMappings(dialect, nm => nm.nodetypeMapping.option().exists(_.inVocabulary))
        .map(nm => {
          (nm.nodetypeMapping.value().toAlias -> nm)
        })
        .joinMap

    val classTerms = createClassTerms(nodeMappings)

    val propertyMappings: Map[String, Seq[PropertyMapping]] =
      collectPropertyMappings(dialect, p => p.nodePropertyMapping().option().exists(_.inVocabulary))
        .map(p => {
          (p.nodePropertyMapping().value().toAlias -> p)
        })
        .joinMap

    val propertyTerms = createPropertyTerms(propertyMappings)

    buildVocabulary(base, alias, classTerms, propertyTerms)
  }

  def createAbstractWorkspaceEdit(vocabulary: Vocabulary): Future[AbstractWorkspaceEdit] =
    createFileUri("vocabulary").map(newUri => {
      val vocabularyFileName = newUri.substring(newUri.lastIndexOf('/') + 1)
      val vocabularyEdits    = createVocabularyFile(newUri, vocabulary)
      val referenceEdit      = createReferenceToNewVocabulary(vocabularyFileName, alias)
      val deleteOldReference = deleteOldReferenceEdit()
      val localEdits =
        TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), Seq(referenceEdit, deleteOldReference))
      AbstractWorkspaceEdit(vocabularyEdits :+ Left(localEdits))
    })

  def createClassTerms(nodeMappings: Map[String, Seq[NodeMapping]]): Seq[ClassTerm] =
    nodeMappings
      .map({ case (k, v) =>
        val classTerm = createClassTerm(v.head.name.option(), k)
        val properties: Seq[String] = v.flatMap(nm => {
          knownClassTerms = knownClassTerms + (nm.id -> k)
          nm.propertiesMapping()
            .flatMap(
              _.nodePropertyMapping()
                .option()
                .flatMap(prop => {
                  if (prop.inVocabulary) {
                    Some(prop.toAlias)
                  } else {
                    None
                  }
                })
            )
        })
        classTerm.withProperties(properties.distinct)
      })
      .toSeq

  def createPropertyTerms(propertyMappings: Map[String, Seq[PropertyMapping]]): Seq[PropertyTerm] =
    propertyMappings
      .map({ case (k, v) =>
        if (v.size == 1 || sameRange(v)) {
          createPropertyTerm(v.head, v.head.name().option(), k, knownClassTerms)
        } else {
          createPropertyTerm(v.head, v.head.name().option(), k, knownClassTerms)
            .withRange(null)
        }
      })
      .toSeq

  def sameRange(properties: Seq[PropertyMapping]): Boolean = {
    val head = properties.head
    properties.forall(p => p.literalRange() == head.literalRange() || p.objectRange() == head.objectRange())
  }

  def deleteOldReferenceEdit(): TextEdit = {
    val range: PositionRange = findRootKey("external")
      .flatMap(_.value.value match {
        case yMap: YMap if yMap.entries.size == 1 => Some(yMap.range.toPositionRange)
        case _                                    => None
      })
      .getOrElse(external.annotations.astElement().map(_.location.range.toPositionRange).get)
    TextEdit(LspRangeConverter.toLspRange(range), "")
  }

  override protected val extension: String = "yaml"

  implicit class BaseCleaner(str: String) {
    def toAlias: String       = str.replace(base, "")
    def inVocabulary: Boolean = str.startsWith(base)
  }

  implicit class Mapper[A](seq: Seq[(String, A)]) {
    def joinMap: Map[String, Seq[A]] = seq.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }
  }
}
