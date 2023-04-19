package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.apicontract.client.scala.model.document.ComponentModule
import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.client.scala.model.document.DeclaresModel
import amf.core.client.scala.model.domain.{AmfObject, NamedDomainElement}
import amf.core.internal.parser.domain.Fields
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import amf.shapes.internal.annotations.DocumentDeclarationKey
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect

import scala.concurrent.Future

object DeclarablePathSuggestor {
  def apply(
      declared: DeclaresModel,
      prefix: String,
      targetClass: Option[String],
      flagFromComponent: Boolean = false
  ): DeclarablePathSuggestor = declared match {
    case json: JsonSchemaDocument   => JsonSchemaSuggestor(json, prefix, flagFromComponent)
    case component: ComponentModule => ComponentSuggestor(component, prefix, targetClass, flagFromComponent)
    case _                          => new DeclarablePathSuggestor(declared, prefix, flagFromComponent)
  }

}

sealed class DeclarablePathSuggestor(
    declarations: DeclaresModel,
    prefix: String,
    flagFromExternalFragment: Boolean = false
) extends PathSuggestor {
  override def suggest(): Future[Seq[RawSuggestion]] = {
    val names = declarations.declares.flatMap {
      case named: NamedDomainElement => named.name.option()
      case _                         => None
    }
    Future.successful(buildSuggestions(names.map(buildText), prefix, flagFromExternalFragment))
  }

  override protected def prevFromPrefix(prefix: String): String =
    if (prefix.contains("#")) prefix.substring(0, prefix.lastIndexOf("#") + 1)
    else super.prevFromPrefix(prefix)

  def buildText(name: String): String = name
}

sealed case class JsonSchemaSuggestor(
    schema: JsonSchemaDocument,
    prefix: String,
    flagFromExternalFragment: Boolean = false
) extends DeclarablePathSuggestor(schema, prefix) {
  override def buildText(name: String): String = {
    val defKey = schema.annotations.find(classOf[DocumentDeclarationKey]).map(_.value).getOrElse("definitions")
    s"/$defKey/$name"
  }
}

sealed case class ComponentSuggestor(
    component: ComponentModule,
    prefix: String,
    targetClass: Option[String],
    flagFromExternalFragment: Boolean = false
) extends DeclarablePathSuggestor(component, prefix) {

  override def suggest(): Future[Seq[RawSuggestion]] = {
    val names = component.declares.flatMap {
      case param: Parameter
          if targetClass.forall(tc => param.metaURIs.contains(tc)) &&
            isSynthetizedBinding(param) => // is not inside parameters
        for {
          componentsKey <- OAS30Dialect.dialect.documents().declarationsPath().option()
          name          <- param.name.option()
        } yield {
          s"/$componentsKey/headers/$name"
        }
      case named: NamedDomainElement if targetClass.forall(tc => named.metaURIs.contains(tc)) =>
        for {
          componentsKey <- OAS30Dialect.dialect.documents().declarationsPath().option()
          declaredKey   <- named.declarableKey(OAS30Dialect.dialect)
          name          <- named.name.option()
        } yield {
          s"/$componentsKey/$declaredKey/$name"
        }
      case _ => None
    }
    Future.successful(buildSuggestions(names.map(buildText), prefix, flagFromExternalFragment))
  }

  private def isSynthetizedBinding(param: Parameter) =
    Option(param.fields.getValue(ParameterModel.Binding))
//    param.fields.fields().find(_.field.value.iri() == ParameterModel.Binding.value.iri())
      .exists(_.annotations.isSynthesized)
}
