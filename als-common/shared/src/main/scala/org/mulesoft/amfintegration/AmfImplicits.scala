package org.mulesoft.amfintegration

import amf.core.annotations._
import amf.core.metamodel.Field
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement}
import amf.core.parser
import amf.core.parser.{Annotations, Value}
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, NodeMapping, PropertyTerm}
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.document.webapi.annotations.{DeclarationKey, DeclarationKeys, ExternalJsonSchemaShape}
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.webapi.metamodel.AbstractModel
import org.yaml.model.{YMapEntry, YPart}

import scala.collection.mutable

object AmfImplicits {
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
