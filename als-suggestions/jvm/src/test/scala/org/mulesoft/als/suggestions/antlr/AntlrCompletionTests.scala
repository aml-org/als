package org.mulesoft.als.suggestions.antlr

import org.scalatest.flatspec.AsyncFlatSpec
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.suggestions.antlr.plugins.AntlrStructureCompletionPlugin
import org.mulesoft.antlrast.platform.{PlatformGraphQLParser, PlatformProtobuf3Parser}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper


class AntlrCompletionTests extends AsyncFlatSpec {

  behavior of "Basic ANTLR GRPC Structure suggestions"
  it should "suggest the first explicit token" in {
    val plugin = new AntlrStructureCompletionPlugin("", "test.proto", DtoPosition(0, 0), new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.size shouldBe 1
      items.head.label shouldBe "syntax"
    })
  }

  it should "suggest the second explicit token" in {
    val plugin = new AntlrStructureCompletionPlugin("syntax", "test.proto", DtoPosition(0, 0), new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.size shouldBe 1
      items.head.label shouldBe "="
    })
  }

  it should "suggest opening bracket" in {
    val content = """syntax = "proto3";
                    |package test;
                    |extend google.protobuf.MessageOptions google.protobuf.FileOptions
                    |
                    |""".stripMargin

    val position = DtoPosition(2,66)

    val trimmedContent = content.substring(0, position.offset(content))

    val plugin = new AntlrStructureCompletionPlugin(trimmedContent, "test.proto", position, new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.head.label shouldBe "{"
    })
  }

  it should "suggest extend enum" in {
    val content = """syntax = "proto3";
                    |package test;
                    |extend
                    |
                    |""".stripMargin

    val position = DtoPosition(2,7)

    val trimmedContent = content.substring(0, position.offset(content))

    val plugin = new AntlrStructureCompletionPlugin(trimmedContent, "test.proto", position, new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.map(_.label).contains("google.protobuf.FileOptions") shouldBe true
    })
  }

  it should "suggest extend enum even when not at the end of a file" in {
    val content = """syntax = "proto3";
                    |package test;
                    |extend google.protobuf.MessageOptions google.protobuf.FileOptions
                    |
                    |""".stripMargin

    val position = DtoPosition(2,7)

    val trimmedContent = content // .substring(0, position.offset(content))

    val plugin = new AntlrStructureCompletionPlugin(trimmedContent, "test.proto", position, new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.size shouldBe 9
      items.map(_.label).contains("google.protobuf.FileOptions") shouldBe true
    })
  }

  it should "suggest nothing when a name is due" in {
    val content = """"syntax = "proto3";
                    |
                    |package greeter;
                    |
                    |enum """".stripMargin

    val position = DtoPosition(4,5)

    val trimmedContent = content // .substring(0, position.offset(content))

    val plugin = new AntlrStructureCompletionPlugin(trimmedContent, "test.proto", position, new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.size shouldBe 0
    })
  }

  it should "suggest brackets for an enum" in {
    val content = """"syntax = "proto3";
                    |
                    |package greeter;
                    |
                    |enum name """".stripMargin

    val position = DtoPosition(4,10)

    val trimmedContent = content // .substring(0, position.offset(content))

    val plugin = new AntlrStructureCompletionPlugin(trimmedContent, "test.proto", position, new PlatformProtobuf3Parser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.map(_.label).contains("{") shouldBe true
    })
  }


  behavior of "Basic ANTLR GraphQL Structure suggestions"

  it should "suggest the first explicit token" in {
    val plugin = new AntlrStructureCompletionPlugin("", "test.graphql", DtoPosition(0, 0), new PlatformGraphQLParser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.size shouldBe 14
      items.map(_.label).contains("type") shouldBe true
    })
  }

  it should "suggest schema type token" in {
    val plugin = new AntlrStructureCompletionPlugin("""type ActivityLikeNotification {
                                                      |  id: Int!
                                                      |  """.stripMargin,
      "test.graphql", DtoPosition(2, 0), new PlatformGraphQLParser())
    val eventualItems = plugin.suggest()
    eventualItems.map(items => {
      items.map(_.label).contains("}") shouldBe true
    })
  }


}
