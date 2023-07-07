package org.mulesoft.als.server.workspace

import amf.core.client.scala.model.document.Document
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.LanguageServerBaseTest
import org.mulesoft.als.server.modules.workspace.{DefaultProjectConfigurationProvider, MainFileTreeBuilder}
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.visitors.AmfElementVisitors
import org.mulesoft.amfintegration.AmfImplicits._
import scala.concurrent.{ExecutionContext, Future}

class DefaultProjectConfigurationProviderTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "config-provider"

  val ws1: String       = filePath("ws1")
  val ws2: String       = filePath("ws2")
  val api: String       = filePath("ws1/api.raml")
  val extension: String = filePath("ws1/extension.yaml")
  val profile: String   = filePath("ws1/profile.yaml")
  val library: String   = filePath("ws1/library.raml")
  val dialect: String   = filePath("ws1/dialect.yaml")

  def buildConfigurationProvider(): ProjectConfigurationProvider = {
    val container                  = TextDocumentContainer()
    val editorConfiguration        = EditorConfiguration()
    val disableValidationAllTraces = false
    new DefaultProjectConfigurationProvider(container, editorConfiguration, logger, disableValidationAllTraces)
  }

  test("Project configuration provider will hold multiple configurations") {
    val provider = buildConfigurationProvider()
    for {
      c1 <- provider.newProjectConfiguration(
        ProjectConfiguration(ws1, Some("api.raml"), extensionDependency = Set(extension))
      )
      c2 <- provider.newProjectConfiguration(
        ProjectConfiguration(ws2, Some("instance.yaml"), metadataDependency = Set(filePath("ws2/dialect.yaml")))
      )
      p1 <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
      p2 <- provider.getProjectInfo(ws2).getOrElse(Future(EmptyProjectConfigurationState))
    } yield {
      assert(c1 == p1)
      assert(c1.config.mainFile.contains("api.raml"))
      assert(c1.extensions.exists(_.extensions().nonEmpty))
      assert(c2 == p2)
      assert(c2.config.mainFile.contains("instance.yaml"))
      assert(c2.extensions.exists(d => d.nameAndVersion() == "Test 2"))
    }
  }

  test("Project configuration provider will override existing configurations") {
    val provider = buildConfigurationProvider()
    for {
      c1 <- provider.newProjectConfiguration(ProjectConfiguration(ws1, Some("api.raml")))
      c2 <- provider.newProjectConfiguration(
        ProjectConfiguration(ws1, Some("library.raml"), metadataDependency = Set(filePath("ws2/dialect.yaml")))
      )
    } yield {
      assert(c1 != c2)
      c1.config.mainFile should contain("api.raml")
      c1.extensions should be(empty)
      c2.config.mainFile should contain("library.raml")
      c2.extensions should not be (empty)
    }
  }

  test("Project configuration state should be immutable") {
    val provider = buildConfigurationProvider()
    for {
      c1 <- provider.newProjectConfiguration(ProjectConfiguration(ws1, Some("api.raml")))
      p1 <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
      c2 <- provider.newProjectConfiguration(
        ProjectConfiguration(
          ws1,
          Some("library.raml"),
          designDependency = Set(api),
          validationDependency = Set(profile),
          extensionDependency = Set(extension),
          metadataDependency = Set(dialect)
        )
      )
      p2 <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
    } yield {
      assert(c1 == p1)
      assert(c2 == p2)
      assert(c1 != c2)
      assert(p1 != p2)
      p1.config.mainFile should contain("api.raml")
      p1.config.metadataDependency should be(empty)
      p1.config.extensionDependency should be(empty)
      p1.config.validationDependency should be(empty)
      p1.extensions should be(empty)
      p1.profiles should be(empty)

      p2.config.mainFile should contain("library.raml")
      p2.extensions should not be empty
      p2.extensions.size should be(2)
      p2.config.metadataDependency should not be empty
      p2.config.extensionDependency should not be empty
      p2.config.validationDependency should not be empty
      p2.extensions should not be empty
      p2.profiles should not be empty
    }
  }

  test("Project configuration state should contain parsed instances") {
    val provider = buildConfigurationProvider()
    for {
      c1 <- provider.newProjectConfiguration(
        ProjectConfiguration(
          ws1,
          Some("api.raml"),
          designDependency = Set(library),
          validationDependency = Set(profile),
          extensionDependency = Set(extension),
          metadataDependency = Set(dialect)
        )
      )
      p1 <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
    } yield {
      assert(c1 == p1)
      p1.config.mainFile should contain("api.raml")
      p1.config.metadataDependency should contain(dialect)
      p1.config.extensionDependency should contain(extension)
      p1.config.validationDependency should contain(profile)
      p1.extensions.find(d => d.nameAndVersion() == "Test 2") should not be empty
      p1.extensions.find(d => d.nameAndVersion() == "Annotation mappings 1.0") should not be empty
      p1.profiles.find(p => p.path.contains("profile.yaml")) should not be empty
      p1.profiles.find(p =>
        p.model.encodes.graph
          .scalarByProperty("http://a.ml/vocabularies/amf/core#name")
          .contains("ValidateGet")
      ) should not be empty
    }
  }

  test("Default project configuration will cache units") {
    val provider = buildConfigurationProvider()
    for {
      c1 <- provider.newProjectConfiguration(
        ProjectConfiguration(
          ws1,
          Some("api.raml"),
          validationDependency = Set(profile),
          extensionDependency = Set(extension),
          metadataDependency = Set(dialect)
        )
      )
      p1           <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
      editorConfig <- EditorConfiguration().getState
      parseResult  <- ALSConfigurationState(editorConfig, p1, None).parse(api)
      tree <- MainFileTreeBuilder.build(
        parseResult,
        new AmfElementVisitors(Seq.empty),
        logger,
        disableValidationAllTraces = false
      )
      _ <- provider.afterNewTree(ws1, tree)
      c2 <- provider.newProjectConfiguration(
        ProjectConfiguration(
          ws1,
          Some("api.raml"),
          designDependency = Set(library),
          validationDependency = Set(profile),
          extensionDependency = Set(extension),
          metadataDependency = Set(dialect)
        )
      )
      p2 <- provider.getProjectInfo(ws1).getOrElse(Future(EmptyProjectConfigurationState))
      _  <- provider.afterNewTree(ws1, tree)
      assert1 <- Future {
        assert(c1 == p1)
        assert(c1 != c2)
      }
      libC1 <- c1.cache
        .fetch(library)
        .map(_.content)
        .recoverWith({ case _ => Future(Document().withId("nonCacheC1")) })
      libC2 <- c2.cache.fetch(library).map(_.content)
      assertp1 <- p1.cache
        .fetch(library)
        .map(_.content)
        .recoverWith({ case _ => Future(Document().withId("nonCacheP1")) })
      assertp2 <- p2.cache
        .fetch(library)
        .map(_.content)
        .recoverWith({ case _ => Future(Document().withId("nonCacheP2")) })
    } yield {
      libC1.id shouldBe "nonCacheC1"
      libC2.identifier shouldBe library
      assertp1.id shouldBe "nonCacheP1"
      assertp2.identifier shouldBe library
      assert1
    }
  }
}
