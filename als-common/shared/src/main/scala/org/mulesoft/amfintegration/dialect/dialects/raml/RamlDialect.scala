package org.mulesoft.amfintegration.dialect.dialects.raml

import amf.core.annotations.Aliases
import amf.core.metamodel.domain.ModelVocabularies
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.parser.Annotations
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain._
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.domain.shapes.metamodel.{AnyShapeModel, CreativeWorkModel, ExampleModel}
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10DialectNodes

trait RamlDialect {

  // Base location for all information the dialect
  val dialectLocation: String
  // Marking syntactic fields in the AST that are not directly mapped to properties in the model
  final val ImplicitField: String = (Namespace.Meta + "implicit").iri()

  // Dialect
  protected val version: String
  protected val dialectDeclares: Seq[NodeMapping]
  protected val rootId: String
  final lazy val dialect: Dialect = {
    val d = Dialect()
      .withId(dialectLocation)
      .withName("RAML")
      .withVersion(version)
      .withLocation(dialectLocation)
      .withId(dialectLocation)
      .withDeclares(dialectDeclares)
      .withDocuments(
        DocumentsModel()
          .withId(dialectLocation + "#/documents")
          .withReferenceStyle(ReferenceStyles.RAML)
          .withRoot(
            DocumentMapping()
              .withId(dialectLocation + "#/documents/root")
              .withEncoded(rootId)
          )
          .withFragments(Seq(
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/datatype")
              .withDocumentName("DataType")
              .withEncoded(AnyShapeModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/securityScheme")
              .withDocumentName("SecurityScheme")
              .withEncoded(SecuritySchemeModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/namedExample")
              .withDocumentName("NamedExample")
              .withEncoded(ExampleModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/documentationItem")
              .withDocumentName("DocumentationItem")
              .withEncoded(CreativeWorkModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/annotationTypeDeclaration")
              .withDocumentName("AnnotationTypeDeclaration")
              .withEncoded(CustomDomainPropertyModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/trait")
              .withDocumentName("Trait")
              .withEncoded(TraitModel.`type`.head.iri()),
            DocumentMapping()
              .withId(dialectLocation + "#/documents/fragments/resourceType")
              .withDocumentName("ResourceType")
              .withEncoded(ResourceTypeModel.`type`.head.iri())
          )))

    d.withExternals(
      Seq(
        External()
          .withId(dialectLocation + "#/externals/core")
          .withAlias("core")
          .withBase(Namespace.Core.base),
        External()
          .withId(dialectLocation + "#/externals/shacl")
          .withAlias("shacl")
          .withBase(Namespace.Shacl.base),
        External()
          .withId(dialectLocation + "#/externals/apiContract")
          .withAlias("apiContract")
          .withBase(Namespace.ApiContract.base),
        External()
          .withId(dialectLocation + "#/externals/meta")
          .withAlias("meta")
          .withBase(Namespace.Meta.base),
        External()
          .withId(dialectLocation + "#/externals/owl")
          .withAlias("owl")
          .withBase(Namespace.Owl.base)
      ))

    val vocabularies = Seq(
      ModelVocabularies.AmlDoc,
      ModelVocabularies.ApiContract,
      ModelVocabularies.Core,
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
