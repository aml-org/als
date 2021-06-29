package org.mulesoft.amfintegration.dialect.dialects.oas

import amf.aml.client.scala.model.document.{Dialect, Vocabulary}
import amf.aml.client.scala.model.domain.{DocumentMapping, DocumentsModel, External, PublicNodeMapping}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.annotations.Aliases
import amf.core.internal.metamodel.domain.ModelVocabularies
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas2.JsonSchemas
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes._

object OAS20Dialect extends OasBaseDialect {

  override def DialectLocation: String = "file://vocabularies/dialects/oas20.yaml"

  // Dialect
  lazy val dialect: Dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName("swagger")
      .withVersion("2.0")
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(Seq(
        Oas20WebApiNode,
        AMLInfoObject,
        Oas20PathItemObject,
        Oas20BodyParameterObject,
        Oas20SecuritySchemeObject,
        Oas20SecuritySettingsNode,
        AMLLicenseObject,
        Oas20ParamObject,
        Oas20ResponseObject,
        Oas20ParamObject,
        Oauth2SecuritySchemeObject,
        Oas2Oauth2FlowSchemeObject,
        Oas20ScopeObject,
        Oas20ApiKeySecuritySchemeObject,
        AMLContactObject,
        AMLExampleObject,
        AMLExternalDocumentationObject,
        Oas20AMLOperationObject,
        AMLTagObject,
        XmlObject,
        Oas20AMLHeaderObject,
        JsonSchemas.SchemaObject,
        JsonSchemas.AnySchemaObject,
        JsonSchemas.ArraySchemaObject,
        JsonSchemas.IntegerSchemaObject,
        JsonSchemas.NodeShapeObject,
        JsonSchemas.NumberSchemaObject,
        JsonSchemas.StringSchemaObject,
      ))
      .withDocuments(
        DocumentsModel()
          .withId(DialectLocation + "#/documents")
          .withKeyProperty(true)
          .withReferenceStyle(ReferenceStyles.JSONSCHEMA)
          .withRoot(
            DocumentMapping()
              .withId(DialectLocation + "#/documents/root")
              .withEncoded(Oas20WebApiNode.id)
              .withDeclaredNodes(Seq(
                PublicNodeMapping()
                  .withId(DialectLocation + "#/documents/definitions")
                  .withName("definitions")
                  .withMappedNode(Oas20SchemaObject.id),
                PublicNodeMapping()
                  .withId(DialectLocation + "#/documents/parameters")
                  .withName("parameters")
                  .withMappedNode(Oas20ParamObject.id),
                PublicNodeMapping()
                  .withId(DialectLocation + "#/documents/responses")
                  .withName("responses")
                  .withMappedNode(Oas20ResponseObject.id),
                PublicNodeMapping()
                  .withId(DialectLocation + "#/documents/securityDefinitions")
                  .withName("securityDefinitions")
                  .withMappedNode(Oas20SecuritySchemeObject.id)
              ))
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
      ))

    val vocabularies = Seq(
      ModelVocabularies.AmlDoc,
      ModelVocabularies.ApiContract,
      ModelVocabularies.Shapes,
      ModelVocabularies.Meta,
      ModelVocabularies.Security
    )
    d.annotations += Aliases(vocabularies.map { vocab =>
      (vocab.alias, (vocab.base, vocab.filename))
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
