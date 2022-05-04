package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{ClassTerm, NodeMapping, PropertyMapping, PropertyTerm}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.annotations.Aliases
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionRequestParams
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.common.ASTElementWrapper.AlsPositionRange
import org.mulesoft.als.common.dtoTypes.{PositionRange, Position => DtoPosition}
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, DialectImplicits}
import org.mulesoft.common.client.lexical.ASTElement
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}
import org.yaml.render.YamlRender

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SynthesizeVocabularyAction(dialect: Dialect, override val params: CodeActionRequestParams)
    extends DialectActionsHelper {

  var knownClassTerms: Map[String, String] = Map()
  var knownPropertyTerms: Seq[String]      = Seq()
  var localTextEdits: Seq[TextEdit]        = Seq()

  val aliases: Set[String] = (dialect.externals.map(_.alias.value()) ++
    dialect.annotations.find(classOf[Aliases]).map(_.aliases.map(_._1)).getOrElse(Seq.empty)).toSet

  val alias: String = ExtractorCommon.nameNotInList("voc", aliases)

  def knownNames: Set[String] = (knownClassTerms.values ++ knownPropertyTerms).toSet

  def synthesize(): Future[Seq[AbstractCodeAction]] = {
    val nodeMappings: Seq[NodeMapping] = collectNodeMappings(dialect, nm => nm.nodetypeMapping.isNullOrEmpty)

    val propertyMappings: Seq[PropertyMapping] =
      collectPropertyMappings(dialect, p => missingPropertyTerm(p.nodePropertyMapping().value(), p.name().value()))

    val classTerms: Seq[ClassTerm]       = createClassTerms(nodeMappings)
    val propertyTerms: Seq[PropertyTerm] = createPropertyTerms(propertyMappings)

    buildEdits(classTerms, propertyTerms).map(edits => {
      Seq(SynthesizeVocabularyCodeAction.baseCodeAction(edits))
    })
  }

  protected def createClassTerms(mappings: Seq[NodeMapping]): Seq[ClassTerm] = {
    mappings.flatMap(m => {
      val displayName = m.name.option()
      val name        = createName(m.name.option(), knownNames, "classTerm", capitalize = true)
      val classTerm   = createClassTerm(displayName, name)
      val textEdit    = createTextEdit("classTerm", name, m.annotations.astElement())
      if (textEdit.isDefined) { // if we were able to create the edit
        knownClassTerms = knownClassTerms + (m.id -> name)
        localTextEdits = localTextEdits :+ textEdit.get
        Some(classTerm)
      } else None
    })
  }

  protected def createPropertyTerms(propertyMappings: Seq[PropertyMapping]): Seq[PropertyTerm] = {
    propertyMappings.flatMap(p => {
      val displayName                = p.name().option()
      val name                       = createName(displayName, knownNames, "propertyTerm", capitalize = false)
      val propertyTerm: PropertyTerm = createPropertyTerm(p, displayName, name, knownClassTerms)
      val textEdit                   = createTextEdit("propertyTerm", name, p.annotations.astElement())
      if (textEdit.isDefined) { // if we were able to create the edit
        knownPropertyTerms = knownPropertyTerms :+ name
        localTextEdits = localTextEdits :+ textEdit.get
        Some(propertyTerm.withName(name))
      } else None
    })
  }

  def createName(
      maybeString: Option[String],
      knownNames: Set[String],
      defaultName: String,
      capitalize: Boolean
  ): String = {
    val c    = maybeString.getOrElse(defaultName).toCharArray
    val name = (if (capitalize) c.head.toUpper else c.head.toLower) + c.tail.mkString
    ExtractorCommon.nameNotInList(name, knownNames)
  }

  def missingPropertyTerm(nodePropertyTerm: String, name: String): Boolean =
    nodePropertyTerm == (Namespace.Data + name).iri()

  def createTextEdit(key: String, name: String, astElement: Option[ASTElement]): Option[TextEdit] = {
    val firstEntryPosition = findFirstEntryPosition(astElement)
    firstEntryPosition.map(position => {
      val indentation = indent(dialect.indentation(position))
      val entry: String = YamlRender
        .render(
          Seq(YMapEntry(YNode(key, params.uri), YNode(alias + "." + name, params.uri))),
          renderOptions
        ) + "\n" + indentation
      val range = LspRangeConverter.toLspRange(PositionRange(position, position))
      TextEdit(range, entry)
    })
  }

  @tailrec
  private def findFirstEntryPosition(astElement: Option[ASTElement]): Option[DtoPosition] =
    astElement match {
      case Some(yMapEntry: YMapEntry) => findFirstEntryPosition(Some(yMapEntry.value.value))
      case Some(yNode: YNode)         => findFirstEntryPosition(Some(yNode.value))
      case Some(map: YMap)            => map.entries.headOption.map(_.range.toPositionRange.start)
      case _                          => None
    }

  def buildEdits(classTerms: Seq[ClassTerm], propertyTerms: Seq[PropertyTerm]): Future[AbstractWorkspaceEdit] = {
    val fileUri = createFileUri("vocabulary")
    fileUri.map(newUri => {
      val vocabularyFileName = newUri.substring(newUri.lastIndexOf('/') + 1)
      val vocabulary =
        buildVocabulary("http://a.ml/vocabulary/#", vocabularyFileName.replace(".yaml", ""), classTerms, propertyTerms)
      val referenceToVocabulary = createReferenceToNewVocabulary(vocabularyFileName, alias)
      val localEdits =
        TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), referenceToVocabulary +: localTextEdits)

      AbstractWorkspaceEdit(createVocabularyFile(newUri, vocabulary) :+ Left(localEdits))
    })
  }

  def indent(n: Int): String =
    (if (formattingOptions.insertSpaces) " " else "\t") * n

  override protected val extension: String = "yaml"
}
