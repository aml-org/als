package org.mulesoft.amfintegration

import amf.core.annotations._
import amf.core.metamodel.Field
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement}
import amf.core.parser
import amf.core.parser.{Annotations, FieldEntry, Value, Position => AmfPosition}
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, NodeMapping, PropertyTerm}
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.document.webapi.annotations.{DeclarationKey, DeclarationKeys, ExternalJsonSchemaShape}
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.webapi.metamodel.AbstractModel
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model.{YNode, YPart, YSequence, YType}
import org.yaml.model.YPart
import org.yaml.model.{YMapEntry, YPart}

import scala.collection.mutable

object AmfImplicits {

  implicit class AlsLexicalInformation(li: LexicalInformation) {

    def contains(pos: AmfPosition): Boolean =
      PositionRange(Position(li.range.start), Position(li.range.end))
        .contains(Position(pos))

    def atEnd(pos: AmfPosition) = {
      li.range.end.line > pos.line || (li.range.end.line == pos.line && li.range.end.column < pos.column)
    }

    def containsAtField(pos: AmfPosition): Boolean =
      containsCompletely(pos) || isAtEmptyScalar(pos)

    def isAtEmptyScalar(pos: AmfPosition): Boolean =
      Range(li.range.start.line, li.range.end.line + 1)
        .contains(pos.line) && !isLastLine(pos) && li.range.start == li.range.end
    def isLastLine(pos: AmfPosition): Boolean =
      li.range.end.column == 0 && pos.line == li.range.end.line

    def containsCompletely(pos: AmfPosition): Boolean =
      PositionRange(Position(li.range.start), Position(li.range.end))
        .containsNotEndObj(Position(pos)) && !isLastLine(pos)

    def containsField(pos: AmfPosition): Boolean =
      PositionRange(Position(li.range.start), Position(li.range.end))
        .containsNotEndField(Position(pos))
  }

  implicit class AmfAnnotationsImp(ann: Annotations) {
    def lexicalInformation(): Option[LexicalInformation] = ann.find(classOf[LexicalInformation])

    def location(): Option[String] = ann.find(classOf[SourceLocation]).map(_.location)

    def range(): Option[parser.Range] = ann.lexicalInformation().map(_.range)

    def ast(): Option[YPart] = ann.find(classOf[SourceAST]).map(_.ast)

    def isSynthesized: Boolean = ann.contains(classOf[SynthesizedField])

    def targets(): Map[String, parser.Range] = ann.find(classOf[ReferenceTargets]).map(_.targets).getOrElse(Map.empty)

    def isRamlTypeExpression: Boolean = ann.find(classOf[ParsedFromTypeExpression]).isDefined

    def ramlExpression(): Option[String] = ann.find(classOf[ParsedFromTypeExpression]).map(_.expression)

    def sourceNodeText(): Option[String] =
      ann
        .find(classOf[SourceNode])
        .flatMap(_.node.asScalar)
        .map(_.text)

    def externalJsonSchemaShape: Option[YMapEntry] = ann.find(classOf[ExternalJsonSchemaShape]).map(_.original)

    def declarationKeys(): List[DeclarationKey] = ann.find(classOf[DeclarationKeys]).map(_.keys).getOrElse(List.empty)
  }

  implicit class FieldEntryImplicit(f: FieldEntry) {

    def fieldContains(position: AmfPosition): Boolean = {
      f.value.annotations.lexicalInfo.orElse(f.value.value.annotations.lexicalInfo).exists(_.contains(position))
    }

    def isArrayIncluded(position: AmfPosition): Boolean = {
      f.value.annotations.ast().orElse(f.value.value.annotations.ast()) match {
        case Some(n: YNode) if n.tagType == YType.Seq =>
          PositionRange(n.value.range).contains(Position(position)) && (isEndChar(position, n.value.range) || n
            .as[YSequence]
            .nodes
            .lastOption
            .exists(isEmptyNodeLine(_, position)))
        case Some(arr: YSequence) =>
          PositionRange(arr.range).contains(Position(position)) && isEndChar(position, arr.range)
        case Some(other) => PositionRange(other.range).contains(Position(position))
        case _           => false
      }
    }

    def isEndChar(position: AmfPosition, range: InputRange) = {
      position.line < range.lineTo || (position.line == range.lineTo && position.column > range.columnTo) || range.lineFrom == range.lineTo
    }

    def isEmptyNodeLine(n: YNode, position: AmfPosition): Boolean = {
      n.isNull && n.range.lineFrom == n.range.lineTo && n.range.lineFrom == position.line
    }
  }

  implicit class AmfObjectImp(amfObject: AmfObject) {
    def declarableKey(dialect: Dialect): Option[String] =
      amfObject.meta.`type`
        .map(_.iri())
        .flatMap(dialect.declarationsMapTerms.get(_))
        .headOption

    def metaURIs: List[String] = amfObject.meta.`type` match {
      case head :: tail if isAbstract => (head.iri() + "Abstract") +: (tail.map(_.iri()))
      case l                          => l.map(_.iri())
    }

    lazy val isAbstract: Boolean = amfObject.fields
      .getValueAsOption(AbstractModel.IsAbstract)
      .collect({
        case Value(scalar: AmfScalar, _) => scalar
      })
      .exists(_.toBool)

    def containsPosition(position: AmfPosition): Boolean = amfObject.position().exists(_.contains(position))
  }

  implicit class DomainElementImp(d: DomainElement) extends AmfObjectImp(d) {
    def getLiteralProperty(f: Field): Option[Any] =
      d.fields.getValueAsOption(f).collect({ case Value(v: AmfScalar, _) => v.value })
  }

  implicit class BaseUnitImp(bu: BaseUnit) extends AmfObjectImp(bu) {
    def objWithAST: Option[AmfObject] =
      bu.annotations
        .ast()
        .map(_ => bu)
        .orElse(
          bu match {
            case e: EncodesModel if e.encodes.annotations.ast().isDefined => Some(e.encodes)
            case _                                                        => None
          }
        )

    def ast: Option[YPart] = {
      bu match {
        case e: EncodesModel if e.encodes.annotations.ast().isDefined => e.encodes.annotations.ast()
        case _                                                        => bu.annotations.ast()
      }
    }

    def flatRefs: Seq[BaseUnit] = {
      val set: mutable.Set[BaseUnit] = mutable.Set.empty

      def innerRefs(refs: Seq[BaseUnit]): Unit =
        refs.foreach { bu =>
          if (set.add(bu)) innerRefs(bu.references)
        }

      innerRefs(bu.references)
      set.toSeq
    }

    def identifier: String = bu.location().getOrElse(bu.id)
  }

  implicit class DialectImplicits(d: Dialect) extends BaseUnitImp(d) {
    def referenceStyle: Option[String] =
      Option(d.documents()).flatMap(_.referenceStyle().option())

    def isRamlStyle: Boolean = referenceStyle.contains(ReferenceStyles.RAML)
    def isJsonStyle: Boolean = referenceStyle.contains(ReferenceStyles.JSONSCHEMA)


    // IdNodeMapping -> TermNodeMapping(amf object meta)
    def termsForId: Map[String, String] =
      d.declares.collect({ case nm: NodeMapping => nm }).map(nm => nm.id -> nm.nodetypeMapping.value()).toMap

    def declarationsMapTerms: Map[String, String] = {
      d.documents()
        .root()
        .declaredNodes()
        .flatMap { pnm =>
          d.declares
            .find(_.id == pnm.mappedNode().value())
            .collect({ case nm: NodeMapping => nm })
            .map { declared =>
              declared.nodetypeMapping.value() -> pnm.name().value()
            }
        }
        .toMap
    }
    def vocabulary(base: String): Option[Vocabulary] =
      d.references.collectFirst { case v: Vocabulary if v.base.option().contains(base) => v }
  }

  implicit class VocabularyImplicit(v: Vocabulary) extends BaseUnitImp(v) {
    val properties: Seq[PropertyTerm] = v.declares.collect { case p: PropertyTerm => p }
    val classes: Seq[ClassTerm]       = v.declares.collect { case c: ClassTerm    => c }

    def getPropertyTerm(n: String): Option[PropertyTerm] = v.properties.find(p => p.name.option().contains(n))

    def getClassTerm(n: String): Option[ClassTerm] = v.classes.find(p => p.name.option().contains(n))
  }
}
