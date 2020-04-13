package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.annotations.ErrorDeclaration
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.ElementNameExtractor._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.{AMLCompletionPlugin, CompletionPlugin}
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}
import amf.core.parser._
import scala.concurrent.Future
import org.mulesoft.amfmanager.AmfImplicits._
class AMLRamlStyleDeclarationsReferences(nodeTypeMappings: Seq[String],
                                         prefix: String,
                                         provider: DeclarationProvider,
                                         actualName: Option[String]) {

  def resolve(): Seq[RawSuggestion] = {
    val values =
      if (prefix.contains(".")) resolveAliased(prefix.split('.').head)
      else resolveLocal(actualName)

    values.map(RawSuggestion.apply(_, isAKey = false))
  }

  private def resolveAliased(alias: String) =
    nodeTypeMappings
      .flatMap(provider.forNodeType(_, alias))
      .map(n => alias + "." + n)

  private def resolveLocal(actualName: Option[String]) = {
    val names = nodeTypeMappings.flatMap(provider.forNodeType)
    actualName.fold(names)(n => names.filter(_ != n))
  }
}

object AMLRamlStyleDeclarationsReferences extends AMLDeclarationReferences {
  override def id: String = "AMLRamlStyleDeclarationsReferences"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful({
      if (params.yPartBranch.isValue && styleOrEmpty(params.actualDialect)) {
        val actualName = params.amfObject.elementIdentifier()
        new AMLRamlStyleDeclarationsReferences(getObjectRangeIds(params),
                                               params.prefix,
                                               params.declarationProvider,
                                               actualName).resolve()
      } else Seq.empty
    })
  }

  private def styleOrEmpty(dialect: Dialect) = {
    dialect.documents().referenceStyle().option().forall(_ == ReferenceStyles.RAML)
  }

}

trait AMLDeclarationReferences extends AMLCompletionPlugin {

  protected def getObjectRangeIds(params: AmlCompletionRequest): Seq[String] = {
    val candidates = getFieldIri(params.fieldEntry, params.propertyMapping)
      .orElse(declaredFromKey(params.yPartBranch.parent, params.propertyMapping))
      .map(_.objectRange().flatMap(_.option())) match {
      case Some(seq) => seq
      case _ =>
        val obj = if (params.amfObject.isInstanceOf[ErrorDeclaration]) params.branchStack.head else params.amfObject
        obj.metaURIs.headOption.toSeq
    }
    candidates.filter(_ != DomainElementModel.`type`.head.iri())
  }

  private def getFieldIri(fieldEntry: Option[FieldEntry],
                          propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    fieldEntry.flatMap(fe => propertyMapping.find(_.nodePropertyMapping().value() == fe.field.value.iri()))

//  private def referenceFromDeclared(amfObject: AmfObject): Option[String] = {
//    amfObject.fields.fields() match {
//      case head :: Nil if amfObject.elementIdentifier().nonEmpty =>
//        amfObject.meta.`type`.headOption.map(_.iri())
//      case others if others.nonEmpty => // hack for inferred fields like data type
//        amfObject.annotations.find(classOf[SourceAST]).map(_.ast) match {
//          case Some(p) if refEntryOrMap(p) =>
//            amfObject.meta.`type`.headOption.map(_.iri())
//          case _ => None
//        }
//      case _ => amfObject.meta.`type`.headOption.map(_.iri())
//    }
//  }
//
//  private def refEntryOrMap(ast:YPart)  :Boolean =
//    ast match {
//      case e:YMapEntry => isRefKey(e)
//      case m:YMap => m.entries.headOption.exists(isRefKey)
//      case n:YNode => n.toOption[YMap].flatMap(_.entries.headOption).exists(isRefKey)
//      case _ => false
//    }
//
//  private def isRefKey(e:YMapEntry):Boolean = e.key.asScalar.exists(_.text == "$ref")

  private def declaredFromKey(parent: Option[YPart], propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    parent
      .collect({ case entry: YMapEntry => entry.key.toString })
      .flatMap(k => propertyMapping.find(p => p.name().option().contains(k)))
}
