package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.core.annotations.Aliases
import amf.core.remote.Mimes
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.emitters.vocabularies.VocabularyEmitter
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain._
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionRequestParams
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{CreatesFileCodeAction, ExtractorCommon}
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.common.dtoTypes.{PositionRange, Position => DtoPosition}
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.lsp.configuration.FormatOptions
import org.mulesoft.lsp.edit.{CreateFile, NewFileOptions, TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}
import org.yaml.render.{YamlRender, YamlRenderOptions}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SynthesizeVocabularyAction(dialect: Dialect, override val params: CodeActionRequestParams)
    extends CreatesFileCodeAction {
  val formattingOptions: FormatOptions     = params.configuration.getFormatOptionForMime(Mimes.`APPLICATION/YAML`)
  val renderOptions: YamlRenderOptions     = YamlRenderOptions(formattingOptions.indentationSize, applyFormatting = true)
  var knownClassTerms: Map[String, String] = Map()
  var knownPropertyTerms: Seq[String]      = Seq()
  var localTextEdits: Seq[TextEdit]        = Seq()

  val aliases: Set[String] = (dialect.externals.map(_.alias.value()) ++
    dialect.annotations.find(classOf[Aliases]).map(_.aliases.map(_._1)).getOrElse(Seq.empty)).toSet

  val alias: String = ExtractorCommon.nameNotInList("voc", aliases)

  def knownNames: Set[String] = (knownClassTerms.values ++ knownPropertyTerms).toSet

  def synthesize(): Future[Seq[AbstractCodeAction]] = {
    val nodeMappings: Seq[NodeMapping] = dialect.declares.collect({
      case nm: NodeMapping if nm.nodetypeMapping.isNullOrEmpty => nm
    })

    val propertyMappings: Seq[PropertyMapping] = dialect.declares
      .collect({
        case nm: NodeMapping =>
          nm.propertiesMapping()
            .filter(p => {
              missingPropertyTerm(p.nodePropertyMapping().value(), p.name().value())
            })
      })
      .flatten

    val classTerms: Seq[ClassTerm]       = createClassTerms(nodeMappings)
    val propertyTerms: Seq[PropertyTerm] = createPropertyTerms(propertyMappings)

    createVocabulary(classTerms, propertyTerms).map(edits => {
      Seq(SynthesizeVocabularyCodeAction.baseCodeAction(edits))
    })
  }

  private def createClassTerms(mappings: Seq[NodeMapping]) = {
    mappings.flatMap(m => {
      val displayName = m.name.option()
      val name        = createName(m.name.option(), knownNames, "classTerm", capitalize = true)
      val classTerm   = ClassTerm().withId(name).withDisplayName(displayName.getOrElse(name))
      val textEdit    = createTextEdit("classTerm", name, m.annotations.ast())
      if (textEdit.isDefined) { // if we were able to create the edit
        knownClassTerms = knownClassTerms + (m.id -> name)
        localTextEdits = localTextEdits :+ textEdit.get
        Some(classTerm.withName(name))
      } else None
    })
  }

  private def createPropertyTerms(propertyMappings: Seq[PropertyMapping]): Seq[PropertyTerm] = {
    propertyMappings.flatMap(p => {
      val displayName = p.name().option()
      val name        = createName(displayName, knownNames, "propertyTerm", capitalize = false)
      val propertyTerm = if (p.objectRange().isEmpty) {
        datatypePropertyTerm(p)
      } else {
        objectRangePropertyTerm(p)
      }
      propertyTerm.withId(name).withDisplayName(displayName.getOrElse(name))
      val textEdit = createTextEdit("propertyTerm", name, p.annotations.ast())
      if (textEdit.isDefined) { // if we were able to create the edit
        knownPropertyTerms = knownPropertyTerms :+ name
        localTextEdits = localTextEdits :+ textEdit.get
        Some(propertyTerm.withName(name))
      } else None
    })
  }

  def datatypePropertyTerm(p: PropertyMapping): PropertyTerm = {
    val propertyTerm = DatatypePropertyTerm()
    propertyTerm.withRange(p.literalRange().value().replace(Namespace.Xsd.base, ""))
    propertyTerm
  }

  def objectRangePropertyTerm(p: PropertyMapping): PropertyTerm = {
    val propertyTerm = ObjectPropertyTerm()
    if (p.objectRange().size == 1) {
      knownClassTerms.get(p.objectRange().head.value()).foreach(propertyTerm.withRange)
    }
    propertyTerm
  }

  def createName(maybeString: Option[String],
                 knownNames: Set[String],
                 defaultName: String,
                 capitalize: Boolean): String = {
    val c    = maybeString.getOrElse(defaultName).toCharArray
    val name = (if (capitalize) c.head.toUpper else c.head.toLower) + c.tail.mkString
    ExtractorCommon.nameNotInList(name, knownNames)
  }

  def missingPropertyTerm(nodePropertyTerm: String, name: String): Boolean =
    nodePropertyTerm == (Namespace.Data + name).iri()

  def createTextEdit(key: String, name: String, ast: Option[YPart]): Option[TextEdit] = {
    val firstEntryPosition = findFirstEntryPosition(ast)
    firstEntryPosition.map(position => {
      val indentation = indent(dialect.indentation(position))
      val entry: String = YamlRender
        .render(Seq(YMapEntry(YNode(key, params.uri), YNode(alias + "." + name, params.uri))), renderOptions) + "\n" + indentation
      val range = LspRangeConverter.toLspRange(PositionRange(position, position))
      TextEdit(range, entry)
    })
  }

  @tailrec
  private def findFirstEntryPosition(ast: Option[YPart]): Option[DtoPosition] =
    ast match {
      case Some(yMapEntry: YMapEntry) => findFirstEntryPosition(Some(yMapEntry.value.value))
      case Some(yNode: YNode)         => findFirstEntryPosition(Some(yNode.value))
      case Some(map: YMap)            => map.entries.headOption.map(_.range.toPositionRange.start)
      case _                          => None
    }

  def createVocabulary(classTerms: Seq[ClassTerm], propertyTerms: Seq[PropertyTerm]): Future[AbstractWorkspaceEdit] = {
    val vocabulary = Vocabulary()
    vocabulary.withBase("http://a.ml/vocabulary/#")
    vocabulary.withDeclares(classTerms ++ propertyTerms)
    val fileUri = createFileUri("vocabulary")
    fileUri.map(newUri => {
      val vocabularyName = newUri.substring(newUri.lastIndexOf('/') + 1)
      vocabulary.withName(vocabularyName.replace(".yaml", ""))

      val vocabularyEdits = TextEdit(Range(Position(0, 0), Position(0, 0)),
                                     YamlRender.render(VocabularyEmitter(vocabulary).emitVocabulary()))
      val vocabularyContent     = TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(vocabularyEdits))
      val referenceToVocabulary = createReferenceToNewVocabulary(vocabularyName)
      val localEdits =
        TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), referenceToVocabulary +: localTextEdits)
      val newFileOperation = CreateFile(newUri, Some(NewFileOptions(Some(false), Some(false))))

      AbstractWorkspaceEdit(Seq(Right(newFileOperation), Left(vocabularyContent), Left(localEdits)))
    })
  }

  def createReferenceToNewVocabulary(vocabularyName: String): TextEdit = {
    val newEntry = YMapEntry(YNode(alias), YNode(vocabularyName))
    val usesYMapEntry: Option[YMapEntry] = params.bu.objWithAST
      .flatMap(_.annotations.ast())
      .collectFirst({
        case yMap: YMap => yMap.entries.find(_.key.asScalar.exists(_.text == "uses"))
      })
      .flatten

    val rendered: String = usesYMapEntry.map(_.value.value).getOrElse(YMap.empty) match {
      case map: YMap =>
        val yMapToRender = YMap(map.location, IndexedSeq(YMapEntry(YNode("uses"), YMap(newEntry +: map.entries, ""))))
        YamlRender.render(Seq(yMapToRender), renderOptions)
    }
    TextEdit(
      LspRangeConverter.toLspRange(
        usesYMapEntry.map(_.range.toPositionRange).getOrElse(PositionRange(DtoPosition(1, 0), DtoPosition(1, 0)))),
      rendered)
  }

  def indent(n: Int): String =
    (if (formattingOptions.insertSpaces) " " else "\t") * n

  override protected val extension: String = "yaml"
}
