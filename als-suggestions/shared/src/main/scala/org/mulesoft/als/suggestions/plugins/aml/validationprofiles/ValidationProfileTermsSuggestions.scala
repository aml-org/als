package org.mulesoft.als.suggestions.plugins.aml.validationprofiles

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{DialectDomainElement, NodeMapping}
import amf.core.client.scala.parse.AMFParser
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.amfintegration.dialect.dialects.CanonicalApiDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ValidationProfileTermsSuggestions extends ResolveIfApplies {

  lazy val classMap: Future[Map[String, Seq[String]]] = {
    val dialect: Future[Dialect] =
      AMFParser.parseContent(CanonicalApiDialect.fileContent, AMLConfiguration.predefined()).map { r =>
        r.baseUnit.asInstanceOf[Dialect]
      }

    dialect.map { d =>
      val mappings = d.declares.collect { case np: NodeMapping => np }
      mappings.flatMap { np =>
        np.nodetypeMapping.option().map { term =>
          term -> np.propertiesMapping().map(_.nodePropertyMapping().value())
        }
      }.toMap
    }

  }

  def suggestClasses(): Future[Seq[RawSuggestion]] =
    classMap.map(_.keys.map(k => RawSuggestion(k, isAKey = false)).toSeq)

  def expand(reduced: String): String = {
    val strings = reduced.split("\\.")
    val base = strings.head match {
      case "shacl"       => "http://www.w3.org/ns/shacl#"
      case "rdfs"        => "http://www.w3.org/2000/01/rdf-schema#"
      case "raml-shapes" => "http://vocabularies/data_shapes.yaml#"
      case "security"    => "http://vocabularies/security.yaml#"
      case "apiBinding"  => "http://vocabularies/api_binding.yaml#"
      case "data"        => "http://vocabularies/data_model.yaml#"
      case "doc"         => "http://vocabularies/aml_doc.yaml#"
      case "apiContract" => "http://vocabularies/api_contract.yaml#"
      case "core"        => "http://vocabularies/core.yaml#"
      case "meta"        => "http://vocabularies/aml_meta.yaml#"
    }
    base + strings.last
  }
  def suggestProperties(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val obj =
      if (request.amfObject.meta.`type`.head.iri() == "http://a.ml/vocabularies/amf-validation#ShapeValidation")
        request.amfObject
      else request.branchStack.head
    obj match {
      case d: DialectDomainElement =>
        val head = d.getScalarByProperty("http://a.ml/vocabularies/amf-validation#ramlClassId").map(_.toString).head
        classMap.map { cm =>
          cm.getOrElse(expand(head), Nil).map(RawSuggestion.forKey(_, mandatory = false))
        }
      case _ => Future(Nil)
    }
  }

  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (request.actualDocumentDefinition.name().getOrElse("") == "Validation Profile") {
      if (request.astPartBranch.parentEntryIs("targetClass")) Some(suggestClasses())
      else if (request.astPartBranch.isKeyDescendantOf("propertyConstraints")) Some(suggestProperties(request))
      else notApply
    } else notApply
  }
}
