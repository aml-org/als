package org.mulesoft.amfintegration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.{Dialect, DialectInstance, Vocabulary}
import amf.aml.client.scala.model.domain._
import amf.aml.internal.parse.common.{DeclarationKey, DeclarationKeys}
import amf.antlr.client.scala.parse.syntax.SourceASTElement
import amf.apicontract.client.scala.AMFConfiguration
import amf.apicontract.internal.metamodel.domain.AbstractModel
import amf.core.client.common.position.{Range, Position => AmfPosition}
import amf.core.client.scala.model.document._
import amf.core.client.scala.model.domain.{AmfObject, AmfScalar, DomainElement, NamedDomainElement}
import amf.core.internal.annotations._
import amf.core.internal.metamodel.Type.ArrayLike
import amf.core.internal.metamodel.document.ModuleModel
import amf.core.internal.metamodel.{Field, Obj}
import amf.core.internal.parser.domain.{Annotations, FieldEntry, Value}
import amf.core.internal.remote.Spec
import amf.custom.validation.internal.report.loaders.ProfileDialectLoader
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.shapes.internal.annotations.{
  BaseVirtualNode,
  ExternalJsonSchemaShape,
  ParsedFromTypeExpression,
  ParsedJSONSchema,
  SchemaIsJsonSchema
}
import org.mulesoft.als.common.ASTWrapper._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{ASTPartBranch, ASTWrapper, YPartBranch}
import org.mulesoft.amfintegration.dialect.dialects.validations.RawValidationProfileDialect
import org.mulesoft.antlrast.ast.ASTElement
import org.mulesoft.lexer.InputRange
import org.yaml.model._

import scala.collection.mutable

object AmfImplicits {

  implicit class ASTElementImplicits(ast: ASTElement) {
    def contains(position: AmfPosition): Boolean = {
      ast.start.line <= position.line && ast.start.column <= position.column && ast.end.line >= position.line
    }

    def toPositionRange: PositionRange =
      PositionRange(Position(AmfPosition(ast.start.line, ast.start.column)),
                    Position(AmfPosition(ast.end.line, ast.end.column)))
  }

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
      scala
        .Range(li.range.start.line, li.range.end.line + 1)
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

    def trueLocation(): Option[String] =
      ann.find(classOf[SourceLocation]).map(_.location) orElse ypart().map(_.location.sourceName)

    def range(): Option[Range] = ann.lexicalInformation().map(_.range)

    def ypart(): Option[YPart] = pureYpart() orElse baseVirtualNode()

    def pureYpart(): Option[YPart] = ann.find(classOf[SourceYPart]).map(_.ast)

    def astElement(): Option[ASTElement] = ann.find(classOf[SourceASTElement]).map(_.ast)

    private def baseVirtualNode(): Option[YPart] = ann.find(classOf[BaseVirtualNode]).map(_.ast)

    def jsonSchema(): Option[ParsedJSONSchema] = ann.find(classOf[ParsedJSONSchema])

    def isSynthesized: Boolean = ann.contains(classOf[SynthesizedField])

    def isVirtual: Boolean  = ann.contains(classOf[VirtualElement])
    def isInferred: Boolean = ann.contains(classOf[Inferred])
    def isDeclared: Boolean = ann.contains(classOf[DeclaredElement])

    def targets(): Map[String, Seq[Range]] =
      ann.find(classOf[ReferenceTargets]).map(_.targets).getOrElse(Map.empty)

    def containsAstBranch(astBranch: ASTPartBranch[_])
    def containsYPart(yPartBranch: YPartBranch): Option[Boolean] =
      this
        .ypart()
        .map(y => {
          yPartBranch.node.sameContentAndLocation(y) ||
          yPartBranch.stack.contains(y) ||
          (yPartBranch.node match {
            case node: YNode => node.value.sameContentAndLocation(y)
            case _           => false
          })
        })

    def containsJsonSchemaPosition(yPartBranch: YPartBranch): Option[Boolean] =
      this
        .jsonSchema()
        .map(j => {
          yPartBranch.node match {
            case node: YNode if node.value.isInstanceOf[YScalar] =>
              node.value.asInstanceOf[YScalar].text == j.value
            case _ => false
          }
        })

    def containsPosition(amfPosition: AmfPosition): Boolean =
      this
        .ypart()
        .exists(_.contains(amfPosition)) || (this.ypart().isEmpty && this.astElement().exists(_.contains(amfPosition)))

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

    def toPositionRange(): Option[PositionRange] =
      this.ypart().map(a => a.range.toPositionRange).orElse(this.astElement().map(a => a.toPositionRange))
  }

  implicit class FieldEntryImplicit(f: FieldEntry) {

    def objectSon: Boolean = f.field.`type` match {
      case _: Obj         => true
      case arr: ArrayLike => arr.element.isInstanceOf[Obj]
      case _              => false
    }

    /** @param other
      *   the other FieldEntry to compare
      * @return
      *   B.containsLexically(A) returns true when and only when B.ann and A.ann are defined and A is included inside B
      */
    def containsLexically(other: FieldEntry): Boolean = {
      val otherRange = other.value.annotations.toPositionRange()
      val localRange = f.value.annotations.toPositionRange()
      (localRange, otherRange) match {
        case (Some(b), Some(a)) => b.contains(a)
        case _                  => false
      }
    }

    def fieldContains(position: AmfPosition): Boolean =
      f.value.annotations
        .lexicalInformation()
        .orElse(f.value.value.annotations.lexicalInformation())
        .exists(_.contains(position))

    def isInferred: Boolean = f.value.annotations.isInferred

    def isArrayIncluded(amfPosition: AmfPosition): Boolean =
      f.value.annotations
        .ypart()
        .orElse(f.value.value.annotations.ypart()) match {
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

    def astValueArray(): Boolean =
      f.value.annotations.ypart() match {
        case Some(e: YMapEntry) => e.value.tagType == YType.Seq
        case Some(n: YNode)     => n.tagType == YType.Seq
        case _                  => false
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
        (head.iri() + "Abstract") +: (tail.map(_.iri()))
      case l => l.map(_.iri())
    }

    lazy val isAbstract: Boolean = amfObject.fields
      .getValueAsOption(AbstractModel.IsAbstract)
      .collect({ case Value(scalar: AmfScalar, _) =>
        scalar
      })
      .exists(_.toBool)

    def range: Option[Range] = amfObject.position().map(_.range)
  }

  implicit class DomainElementImp(d: DomainElement) extends AmfObjectImp(d) {
    def getLiteralProperty(f: Field): Option[Any] =
      d.fields
        .getValueAsOption(f)
        .collect({ case Value(v: AmfScalar, _) => v.value })
  }

  implicit class BaseUnitImp(bu: BaseUnit) extends AmfObjectImp(bu) {

    def vendor: Option[Spec] = bu.sourceSpec

    def objWithAST: Option[AmfObject] =
      bu.annotations
        .ypart()
        .map(_ => bu)
        .orElse(
          bu match {
            case e: EncodesModel if e.encodes.annotations.ypart().isDefined =>
              Some(e.encodes)
            case _ => None
          }
        )

    lazy val isFragment: Boolean = bu.isInstanceOf[Fragment]

    def ast: Option[YPart] =
      bu match {
        case e: Document if e.encodes.annotations.ypart().isDefined         => e.encodes.annotations.ypart()
        case e: ExternalFragment if e.encodes.annotations.ypart().isDefined => e.encodes.annotations.ypart()
        case _                                                              => bu.annotations.ypart()
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

    def aliasedModules: Map[String, Module] = {
      def lookModule(tuple: (String, ReferencedInfo)) =
        bu.references.collectFirst({ case m: Module if m.id == tuple._2.id => (tuple._1, m) })

      bu.annotations.find(classOf[Aliases]).map(_.aliases.flatMap(t => lookModule(t))).getOrElse(Set.empty).toMap
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
          ASTWrapper.getIndentation(text, position)
        })
        .getOrElse(0)

    def documentMapping(dialect: Dialect): Option[DocumentMapping] = bu match {
      case fragment: Fragment            => documentForFragment(fragment, dialect)
      case d: Document if d.root.value() => Some(dialect.documents().root())
      case _                             => None
    }

    private def documentForFragment(fragment: Fragment, dialect: Dialect): Option[DocumentMapping] =
      dialect.documents().fragments().find(doc => fragment.encodes.metaURIs.exists(_.equals(doc.encoded().value())))

    def isValidationProfile: Boolean =
      bu match {
        case instance: DialectInstance =>
          instance.isValidationProfile
        case _ => false
      }
  }

  implicit class DialectInstanceImp(instance: DialectInstance) {
    def isValidationProfile: Boolean =
      instance.processingData.definedBy().option().contains(ProfileDialectLoader.PROFILE_DIALECT_ID)
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

    def declarationsMapTerms: Map[String, String] =
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

    def vocabulary(base: String): Option[Vocabulary] =
      d.references.collectFirst {
        case v: Vocabulary if v.base.option().contains(base) => v
      }
  }

  implicit class VocabularyImplicit(v: Vocabulary) extends BaseUnitImp(v) {
    val properties: Seq[PropertyTerm] = v.declares.collect { case p: PropertyTerm =>
      p
    }
    val classes: Seq[ClassTerm] = v.declares.collect { case c: ClassTerm => c }

    def getPropertyTerm(n: String): Option[PropertyTerm] =
      v.properties.find(p => p.name.option().contains(n))

    def getClassTerm(n: String): Option[ClassTerm] =
      v.classes.find(p => p.name.option().contains(n))
  }

  // we have another in suggestions.aml oriented to suggestions. Could be unified?
  implicit class NodeMappingImplicit(nodeMapping: NodeMapping) {

    def findPropertyByTerm(term: String): Option[PropertyMapping] =
      nodeMapping.propertiesMapping().find(_.nodePropertyMapping().value() == term)
  }

  implicit class AmlConfigurationImplicit(config: AMLConfiguration) {
    def fullResolution(unit: BaseUnit) = {
      config match {
        case amf: AMFConfiguration =>
        case aml                   =>
      }
    }
  }

}
