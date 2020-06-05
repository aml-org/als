package org.mulesoft.als.actions.definition

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.actions.common.dialect.DialectDefinitions
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.common.LocationLink
import org.scalatest.{AsyncFlatSpec, Matchers}
import org.mulesoft.lsp.feature.common.{Range => LspRange}
import org.mulesoft.lsp.feature.common.{Position => LspPosition}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class DefinitionFileTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "Find Definition"

  it should "Find definitions on Dialect ranges" in {
    val files: Map[String, String] = Map(
      "file://dialect.yaml" ->
        """#%Dialect 1.0
        |
        |dialect: Movie
        |version: 1.0
        |
        |external:
        |  schema: https://schema.org/
        |
        |documents:
        |  root:
        |    encodes: MovieNode
        |    declares:
        |      actor: ActorNode
        |nodeMappings:
        |    ActorNode:
        |      classTerm: schema.Actor
        |      mapping:
        |        name:
        |          propertyTerm: schema.name
        |          range: string
        |          mandatory: true
        |    MovieNode:
        |      classTerm: schema.Movie
        |      mapping:
        |        title:
        |          propertyTerm: schema.name
        |          range: string
        |          mandatory: true
        |        image:
        |          propertyTerm: schema.image
        |          range: anyUri
        |        classification:
        |          propertyTerm: schema.contentRating
        |          range: string
        |          enum:
        |            - PG 13
        |            - PG 17
        |            - R
        |          mandatory: true
        |        datePublished:
        |          propertyTerm: schema.datePublished
        |          range: date
        |          mandatory: true
        |        cast:
        |          propertyTerm: schema.cast
        |          range: ActorNode
        |          allowMultiple: true
        |
        |""".stripMargin)

    val resourceLoader: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        Future.successful(files.getOrElse(resource, "")).map { c =>
          new Content(c, resource)
        }
      override def accepts(resource: String): Boolean =
        files.keySet.contains(resource)
    }

    val env: Environment = Environment().add(resourceLoader)

    val instance = new AmfInstance(Nil, platform, env)
    val amfInit  = instance.init()
    for {
      _ <- amfInit
      result <- DialectDefinitions.getDefinition(files.keySet.head,
                                                 Position(45, 22),
                                                 instance
                                                   .parse(files.keySet.head)
                                                   .map(_.baseUnit),
                                                 this.platform)
    } yield {
      result.headOption should be(
        Some(LocationLink(
          "file://dialect.yaml",
          LspRange(LspPosition(15, 0), LspPosition(21, 0)),
          LspRange(LspPosition(15, 0), LspPosition(21, 0)),
          Some(LspRange(LspPosition(45, 17), LspPosition(45, 26)))
        )))

    }

  }
}
