package org.mulesoft.amfintegration

import amf.core.annotations.{LexicalInformation, ReferenceTargets, SourceAST, SourceNode, SynthesizedField, _}
import amf.core.metamodel.Field
import amf.core.metamodel.document.ModuleModel
import amf.core.model.document._
import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement, NamedDomainElement}
import amf.core.parser
import amf.core.parser.{Annotations, FieldEntry, Value, Position => AmfPosition}
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain._
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.document.webapi.annotations._
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.webapi.metamodel.AbstractModel
import org.mulesoft.als.common.YamlWrapper
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model.{YMapEntry, YNode, YPart, YScalar, YSequence, YType, _}

import scala.collection.mutable

object AmfImplicits {

  implicit class AlsLexicalInformation(li: LexicalInformation) {

    def contains(pos: AmfPosition): Boolean =
      PositionRange(Position(li.range.start), Position(li.range.end))
        .contains(Position(pos))

    def atEnd(pos: AmfPosition): Boolean = {
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

    def isVirtual: Boolean  = ann.contains(classOf[VirtualObject])
    def isInferred: Boolean = ann.contains(classOf[Inferred])
    def isDeclared: Boolean = ann.contains(classOf[DeclaredElement])

    def targets(): Map[String, Seq[parser.Range]] =
      ann.find(classOf[ReferenceTargets]).map(_.targets).getOrElse(Map.empty)

    def containsPosition(amfPosition: AmfPosition): Option[Boolean] =
      this.ast() map {
        case ast: YMapEntry =>
          YMapEntryOps(ast).contains(amfPosition)
        case ast: YMap =>
          AlsYMapOps(ast).contains(amfPosition)
        case ast: YNode if ast.isNull =>
          true
        case ast: YNode if ast.tagType == YType.Str =>
          ast.contains(amfPosition) || ast.asScalar.exists(_.contains(amfPosition))
        case ast: YScalar =>
          AlsYScalarOps(ast).contains(amfPosition)
        case other =>
          other.contains(amfPosition)
      }
    def isRamlTypeExpression: Boolean = ann.find(classOf[ParsedFromTypeExpression]).isDefined

    def ramlExpression(): Option[String] = ann.find(classOf[ParsedFromTypeExpression]).map(_.expression)

    def sourceNodeText(): Option[String] =
      ann
        .find(classOf[SourceNode])
        .flatMap(_.node.asScalar)
        .map(_.text)

    def externalJsonSchemaShape: Option[YMapEntry] = ann.find(classOf[ExternalJsonSchemaShape]).map(_.original)

    def declarationKeys(): List[DeclarationKey] = ann.find(classOf[DeclarationKeys]).map(_.keys).getOrElse(List.empty)

    def schemeIsJsonSchema: Boolean = ann.contains(classOf[SchemaIsJsonSchema])
  }

  implicit class FieldEntryImplicit(f: FieldEntry) {

    // B.containsLexically(A)
    // true=> solo cuando B.ann y A.ann y A esta dentro de B
    def containsLexically(other: FieldEntry): Boolean = {
      val otherRange = other.value.annotations.ast().map(a => a.range.toPositionRange)
      val localRange = f.value.annotations.ast().map(a => a.range.toPositionRange)
      (localRange, otherRange) match {
        case (Some(b), Some(a)) => b.contains(a)
        case _                  => false
      }
    }

    def fieldContains(position: AmfPosition): Boolean = {
      f.value.annotations
        .lexicalInformation()
        .orElse(f.value.value.annotations.lexicalInformation())
        .exists(_.contains(position))
    }

    def isInferred: Boolean = f.value.annotations.isInferred

    def isArrayIncluded(amfPosition: AmfPosition): Boolean =
      f.value.annotations
        .ast()
        .orElse(f.value.value.annotations.ast()) match {
        case Some(n: YNode) if n.tagType == YType.Seq =>
          n.value.contains(amfPosition) || n
            .as[YSequence]
            .nodes
            .lastOption
            .exists(isEmptyNodeLine(_, amfPosition))
        case Some(arr: YSequence) =>
          PositionRange(arr.range).contains(Position(amfPosition)) && isEndChar(amfPosition, arr.range)
        case Some(e: YMapEntry) => e.contains(amfPosition)
        case Some(n: YNode) if n.tagType == YType.Map =>
          n.contains(amfPosition) &&
            AlsYMapOps(n.value.asInstanceOf[YMap]).contains(amfPosition)
        case Some(other) => other.contains(amfPosition)
        case _           => false
      }

    def astValueArray(): Boolean = {
      f.value.annotations.ast() match {
        case Some(e: YMapEntry) => e.value.tagType == YType.Seq
        case Some(n: YNode)     => n.tagType == YType.Seq
        case _                  => false
      }
    }

    def isEndChar(position: AmfPosition, range: InputRange): Boolean =
      position.line < range.lineTo || (position.line == range.lineTo && position.column > range.columnTo) || range.lineFrom == range.lineTo

    def isEmptyNodeLine(n: YNode, position: AmfPosition): Boolean =
      n.isNull && n.range.lineFrom == n.range.lineTo && n.range.lineFrom == position.line

    def isSemanticName: Boolean =
      f.field.value.name.toLowerCase == "name" || f.field.value.name.toLowerCase() == "declarationname"
  }

  implicit class AmfObjectImp(amfObject: AmfObject) {
    def declarableKey(dialect: Dialect): Option[String] =
      amfObject.metaURIs
        .flatMap(dialect.declarationsMapTerms.get(_))
        .headOption

    def metaURIs: List[String] = amfObject.meta.`type` match {
      case head :: tail if isAbstract =>
        (head.iri() + "Abstract") +: tail.map(_.iri())
      case l => l.map(_.iri())
    }

    lazy val isAbstract: Boolean = amfObject.fields
      .getValueAsOption(AbstractModel.IsAbstract)
      .collect({
        case Value(scalar: AmfScalar, _) => scalar
      })
      .exists(_.toBool)

    def containsPosition(amfPosition: AmfPosition): Boolean =
      amfObject.annotations.containsPosition(amfPosition).getOrElse(false)
  }

  implicit class DomainElementImp(d: DomainElement) extends AmfObjectImp(d) {
    def getLiteralProperty(f: Field): Option[Any] =
      d.fields
        .getValueAsOption(f)
        .collect({ case Value(v: AmfScalar, _) => v.value })
  }

  implicit class BaseUnitImp(bu: BaseUnit) extends AmfObjectImp(bu) {
    def objWithAST: Option[AmfObject] =
      bu.annotations
        .ast()
        .map(_ => bu)
        .orElse(
          bu match {
            case e: EncodesModel if e.encodes.annotations.ast().isDefined =>
              Some(e.encodes)
            case _ => None
          }
        )

    lazy val isFragment: Boolean = bu.isInstanceOf[Fragment]

    def ast: Option[YPart] =
      bu match {
        case e: Document if e.encodes.annotations.ast().isDefined         => e.encodes.annotations.ast()
        case e: ExternalFragment if e.encodes.annotations.ast().isDefined => e.encodes.annotations.ast()
        case _                                                            => bu.annotations.ast()
      }

    def declaredNames: Seq[String] =
      bu.declarations.flatMap {
        case n: NamedDomainElement => Some(n.name.value())
        case _                     => None
      }

    def definedAliases: Set[String] =
      bu.annotations
        .find(classOf[Aliases])
        .map(a => a.aliases.map(_._1))
        .getOrElse(Set.empty)

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

    val declarationKeys: List[DeclarationKey] =
      bu match {
        case d: DeclaresModel =>
          d.fields
            .fields()
            .find(t => t.field == ModuleModel.Declares)
            .map(_.value.annotations.declarationKeys())
            .getOrElse(List.empty)
        case _ => List.empty
      }

    val declarations: Seq[AmfObject] =
      bu match {
        case d: DeclaresModel => d.declares
        case _                => Nil
      }

    def indentation(position: Position): Int =
      bu.raw
        .map(text => {
          YamlWrapper.getIndentation(text, position)
        })
        .getOrElse(0)

    def documentMapping(dialect: Dialect): Option[DocumentMapping] = bu match {
      case fragment: Fragment            => documentForFragment(fragment, dialect)
      case d: Document if d.root.value() => Some(dialect.documents().root())
      case _                             => None
    }

    private def documentForFragment(fragment: Fragment, dialect: Dialect): Option[DocumentMapping] =
      dialect.documents().fragments().find(doc => fragment.encodes.metaURIs.exists(_.equals(doc.encoded().value())))

  }

  implicit class DialectImplicits(d: Dialect) extends BaseUnitImp(d) {
    def referenceStyle: Option[String] =
      Option(d.documents()).flatMap(_.referenceStyle().option())

    def isRamlStyle: Boolean = referenceStyle.contains(ReferenceStyles.RAML)
    def isJsonStyle: Boolean = referenceStyle.contains(ReferenceStyles.JSONSCHEMA)

    private val declaredTerms = d.declares
      .collect({ case nm: NodeMapping => nm.nodetypeMapping.value() -> nm })
      .toMap

    // IdNodeMapping -> TermNodeMapping(amf object meta)
    def termsForId: Map[String, String] =
      d.declares
        .collect({ case nm: NodeMapping => nm })
        .map(nm => nm.id -> nm.nodetypeMapping.value())
        .toMap

    def findNodeMappingByTerm(term: String): Option[NodeMapping] =
      declaredTerms.get(term)

    def declarationsMapTerms: Map[String, String] = {
      d.documents()
        .root()
        .declaredNodes()
        .flatMap { pnm =>
          declaredTerms.values
            .find(_.id == pnm.mappedNode().value())
            .map { declared =>
              declared.nodetypeMapping.value() -> pnm.name().value()
            }
        }
        .toMap
    }

    def vocabulary(base: String): Option[Vocabulary] = {
      d.references.collectFirst {
        case v: Vocabulary if v.base.option().contains(base) => v
      }
    }
  }

  implicit class VocabularyImplicit(v: Vocabulary) extends BaseUnitImp(v) {
    val properties: Seq[PropertyTerm] = v.declares.collect {
      case p: PropertyTerm => p
    }
    val classes: Seq[ClassTerm] = v.declares.collect { case c: ClassTerm => c }

    def getPropertyTerm(n: String): Option[PropertyTerm] =
      v.properties.find(p => p.name.option().contains(n))

    def getClassTerm(n: String): Option[ClassTerm] =
      v.classes.find(p => p.name.option().contains(n))
  }

  // we have another in suggestions.aml oriented to suggestions. Could be unified?
  implicit class NodeMappingImplicit(nodeMapping: NodeMapping) {

    def findPropertyByTerm(term: String): Option[PropertyMapping] = {
      nodeMapping.propertiesMapping().find(_.nodePropertyMapping().value() == term)
    }
  }

}
