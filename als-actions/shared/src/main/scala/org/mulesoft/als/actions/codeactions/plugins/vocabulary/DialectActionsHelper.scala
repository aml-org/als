package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.aml.client.scala.model.document.{Dialect, Vocabulary, kind}
import amf.aml.client.scala.model.domain.{
  ClassTerm,
  DatatypePropertyTerm,
  NodeMapping,
  ObjectPropertyTerm,
  PropertyMapping,
  PropertyTerm
}
import amf.aml.internal.render.emitters.vocabularies.VocabularyEmitter
import amf.aml.internal.render.plugin.SyntaxDocument
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.remote.Mimes
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.CreatesFileCodeAction
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.common.dtoTypes.{PositionRange, Position => DtoPosition}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.lsp.configuration.FormatOptions
import org.mulesoft.lsp.edit._
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.yaml.model.{YMap, YMapEntry, YNode}
import org.yaml.render.{YamlRender, YamlRenderOptions}

trait DialectActionsHelper extends CreatesFileCodeAction {

  val formattingOptions: FormatOptions = params.configuration.getFormatOptionForMime(Mimes.`application/yaml`)
  val renderOptions: YamlRenderOptions = YamlRenderOptions(formattingOptions.tabSize, applyFormatting = true)

  protected def collectNodeMappings(dialect: Dialect, condition: NodeMapping => Boolean): Seq[NodeMapping] =
    dialect.declares.collect({
      case nm: NodeMapping if condition(nm) => nm
    })

  protected def collectPropertyMappings(dialect: Dialect,
                                        condition: PropertyMapping => Boolean): Seq[PropertyMapping] =
    dialect.declares
      .collect({
        case nm: NodeMapping =>
          nm.propertiesMapping()
            .filter(condition)
      })
      .flatten

  protected def createClassTerm(displayName: Option[String], name: String): ClassTerm = {
    ClassTerm().withId(name).withDisplayName(displayName.getOrElse(name)).withName(name)
  }

  protected def createPropertyTerm(p: PropertyMapping,
                                   displayName: Option[String],
                                   name: String,
                                   classTerms: Map[String, String]): PropertyTerm = {
    val propertyTerm = if (p.objectRange().isEmpty) {
      createDatatypePropertyTerm(p)
    } else {
      createObjectRangePropertyTerm(p, classTerms)
    }
    propertyTerm.withId(name).withDisplayName(displayName.getOrElse(name))
    propertyTerm
  }

  private def createDatatypePropertyTerm(p: PropertyMapping): PropertyTerm = {
    val propertyTerm = DatatypePropertyTerm()
    propertyTerm.withRange(p.literalRange().value().replace(Namespace.Xsd.base, ""))
    propertyTerm
  }

  private def createObjectRangePropertyTerm(p: PropertyMapping, classTerms: Map[String, String]): PropertyTerm = {
    val propertyTerm = ObjectPropertyTerm()
    if (p.objectRange().size == 1) {
      classTerms.get(p.objectRange().head.value()).foreach(propertyTerm.withRange)
    }
    propertyTerm
  }

  protected def buildVocabulary(base: String,
                                name: String,
                                classTerms: Seq[ClassTerm],
                                propertyTerms: Seq[PropertyTerm]): Vocabulary =
    Vocabulary()
      .withBase(base)
      .withName(name)
      .withDeclares(classTerms ++ propertyTerms)

  protected def createVocabularyFile(newUri: String,
                                     vocabulary: Vocabulary): Seq[Either[TextDocumentEdit, ResourceOperation]] = {
    val doc = SyntaxDocument.getFor(Mimes.`application/yaml`, kind.Vocabulary)
    val vocabularyEdits = TextEdit(Range(Position(0, 0), Position(0, 0)),
                                   YamlRender.render(VocabularyEmitter(vocabulary, doc).emitVocabulary()))
    val vocabularyContent = TextDocumentEdit(VersionedTextDocumentIdentifier(newUri, None), Seq(vocabularyEdits))
    val newFileOperation  = CreateFile(newUri, Some(NewFileOptions(Some(false), Some(false))))
    Seq(Right(newFileOperation), Left(vocabularyContent))
  }

  def createReferenceToNewVocabulary(vocabularyName: String, alias: String): TextEdit = {
    val newEntry                         = YMapEntry(YNode(alias), YNode(vocabularyName))
    val usesYMapEntry: Option[YMapEntry] = findRootKey("uses")

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

  protected def findRootKey(key: String): Option[YMapEntry] =
    params.bu.objWithAST
      .flatMap(_.annotations.ast())
      .collectFirst({
        case yMap: YMap => yMap.entries.find(_.key.asScalar.exists(_.text == key))
      })
      .flatten

}
