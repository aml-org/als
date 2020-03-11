package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.core.annotations.Aliases
import amf.core.metamodel.domain.ModelVocabularies
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}

object JsonSchemaDraft7Dialect extends JsonSchemaDraft7DialectNodes {

  override val DialectLocation = "file://vocabularies/dialects/jsonSchemaDraft7.yaml"

  // Dialect
  val dialect: Dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName("Json Schema")
      .withVersion("Draft 7")
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(
        Seq(
          AnyShapeNode,
          ShapeNode,
          ArrayShapeNode,
          NilShapeNode,
          NodeShapeNode,
          NumberShapeNode,
          StringShapeNode
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
