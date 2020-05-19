package org.mulesoft.amfintegration.dialect

import amf.core.annotations.Aliases
import amf.core.metamodel.domain.ModelVocabularies
import amf.core.model.domain.DomainElement
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, DocumentsModel, External, PublicNodeMapping}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait BaseDialect {

  def toNode(obj: DialectNode): DomainElement = obj.Obj

  def DialectLocation: String
  protected val name: String
  protected val version: String
  protected def emptyDocument: DocumentsModel

  protected def encodes: DialectNode
  val declares: Seq[DialectNode]

  protected def declaredNodes: Map[String, DialectNode]

  lazy val dialect: Dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName(name)
      .withVersion(version)
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(declares.map(toNode))
      .withDocuments(
        emptyDocument
          .withRoot(
            DocumentMapping()
              .withId(DialectLocation + "#/documents/root")
              .withEncoded(encodes.id)
              .withDeclaredNodes(declaredNodes
                .map({
                  case (k, v) =>
                    PublicNodeMapping()
                      .withId(DialectLocation + s"#/documents/$k")
                      .withName(k)
                      .withMappedNode(v.id)
                })
                .toList)
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
