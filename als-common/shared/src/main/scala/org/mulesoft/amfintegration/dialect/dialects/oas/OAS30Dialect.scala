package org.mulesoft.amfintegration.dialect.dialects.oas

import amf.aml.client.scala.model.document.{Dialect, Vocabulary}
import amf.aml.client.scala.model.domain.{DocumentMapping, DocumentsModel, External, PublicNodeMapping}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.annotations.{Aliases, ReferencedInfo}
import amf.core.internal.metamodel.domain.ModelVocabularies
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas3.JsonSchemas
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes._

object OAS30Dialect extends OasBaseDialect {

  override def DialectLocation: String = "file://vocabularies/dialects/oas30.yaml"

  lazy val dialect: Dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName("openapi")
      .withVersion("3.0.0") // 3.0.1? 3.0.2?
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(
        Seq(
          Oas30ServerObject,
          Oas30PathItemObject,
          AMLLinkObject,
          Oas30ApiKeySecuritySchemeObject,
          Oas30ApiKeySecurityObject,
          AMLRequestBodyObject,
          Oas20ScopeObject,
          AMLEncodingObject,
          Oas30WebApiNode,
          Oas30ResponseObject,
          Oas30OAuth20SecurityObject,
          Oas30SecuritySchemeObject,
          Oas30SecuritySettingsObject,
          AMLTagObject,
          XmlObject,
          Oas30FlowObject,
          Oas30OperationObject,
          AMLContentObject,
          AMLExternalDocumentationObject,
          Oauth2SecuritySchemeObject,
          Oas30OpenIdConnectUrl,
          AMLContactObject,
          Oas30ExampleObject,
          AMLInfoObject,
          Oas30VariableObject,
          Oas30ParamObject,
          AMLIriTemplateMappingObject,
          AMLCallbackObject,
          AMLLicenseObject,
          Oas30HttpSecurityObject,
          JsonSchemas.SchemaObject,
          JsonSchemas.AnySchemaObject,
          JsonSchemas.ArraySchemaObject,
          JsonSchemas.IntegerSchemaObject,
          JsonSchemas.NodeShapeObject,
          JsonSchemas.NumberSchemaObject,
          JsonSchemas.StringSchemaObject
        )
      )
      .withDocuments(
        DocumentsModel()
          .withId(DialectLocation + "#/documents")
          .withKeyProperty(true)
          .withReferenceStyle(ReferenceStyles.JSONSCHEMA)
          .withDeclarationsPath("components")
          .withRoot(
            DocumentMapping()
              .withId(DialectLocation + "#/documents/root")
              .withEncoded(Oas30WebApiNode.id)
              .withDeclaredNodes(
                Seq(
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/schemas")
                    .withName("schemas")
                    .withMappedNode(Oas30SchemaObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/responses")
                    .withName("responses")
                    .withMappedNode(Oas30ResponseObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/parameters")
                    .withName("parameters")
                    .withMappedNode(Oas30ParamObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/examples")
                    .withName("examples")
                    .withMappedNode(Oas30ExampleObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/requestBodies")
                    .withName("requestBodies")
                    .withMappedNode(AMLRequestBodyObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/headers")
                    .withName("headers")
                    .withMappedNode(Oas30AMLHeaderObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/securitySchemes")
                    .withName("securitySchemes")
                    .withMappedNode(Oas30SecuritySchemeObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/links")
                    .withName("links")
                    .withMappedNode(AMLLinkObject.id),
                  PublicNodeMapping()
                    .withId(DialectLocation + "#/documents/callbacks")
                    .withName("callbacks")
                    .withMappedNode(AMLCallbackObject.id)
                )
              )
          )
      )

    d.withExternals(
      Seq(
        External()
          .withId(DialectLocation + "#/externals/core")
          .withAlias("core")
          .withBase(Namespace.Core.base),
        External()
          .withId(DialectLocation + "#/externals/shacl")
          .withAlias("shacl")
          .withBase(Namespace.Shacl.base),
        External()
          .withId(DialectLocation + "#/externals/meta")
          .withAlias("meta")
          .withBase(Namespace.Meta.base),
        External()
          .withId(DialectLocation + "#/externals/owl")
          .withAlias("owl")
          .withBase(Namespace.Owl.base)
      )
    )

    val vocabularies = Seq(
      ModelVocabularies.AmlDoc,
      ModelVocabularies.ApiContract,
      ModelVocabularies.Shapes,
      ModelVocabularies.Meta,
      ModelVocabularies.Security
    )
    d.annotations += Aliases(vocabularies.map { vocab =>
      (vocab.alias, ReferencedInfo(s"fake://id/${vocab.alias}", vocab.base, vocab.filename))
    }.toSet)

    d.withReferences(vocabularies.map { vocab =>
      Vocabulary()
        .withLocation(vocab.filename)
        .withId(vocab.filename)
        .withBase(vocab.base)
    })

    d
  }

  def apply(): Dialect = dialect

}
