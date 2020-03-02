package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.annotations.Aliases
import amf.core.metamodel.domain.ModelVocabularies
import amf.core.vocabulary.Namespace
import amf.dialects.OasBaseDialect
import amf.dialects.oas.nodes._
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, DocumentsModel, External, PublicNodeMapping}

object AsyncApi20Dialect extends OasBaseDialect {

  override def DialectLocation: String = "file://vocabularies/dialects/asyncapi20.yaml"

  lazy val dialect: Dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName("asyncapi")
      .withVersion("2.0.0") // 3.0.1? 3.0.2?
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(Seq(
        ))
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
                    .withId(DialectLocation + "#/documents/securitySchemes")
                    .withName("securitySchemes")
                    .withMappedNode(AsyncApi20SecuritySchemeObject.id)
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
