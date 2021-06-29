package org.mulesoft.als.server.modules.configurationfiles

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.diff.ListAssertions
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.workspace.extract.ExchangeConfigReader
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext

class ExchangeConfigReaderTest extends AsyncFlatSpec with PlatformSecrets with ListAssertions with Matchers {

  override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val base = "als-server/shared/src/test/resources/config-reader/exchange/"

  case class TestConfig(folder: String, main: String, dependencies: List[String])

  val fixture: List[TestConfig] = List(
    TestConfig("simple", "api.raml", Nil),
    TestConfig("one-dependency", "api.raml", List(base + "one-dependency/dependency/lib.raml")),
    TestConfig("one-dependency-two", "api.raml", List(base + "one-dependency-two/dependency/lib.raml")),
    TestConfig("dependency-sublevel",
               "api.raml",
               List(base + "dependency-sublevel/subdir/dependency/subdir/lib.raml")),
    TestConfig("son-dependency", "api.raml", List(base + "son-dependency/dependency/lib.raml")),
    TestConfig("two-dependencies",
               "api.raml",
               List(base + "two-dependencies/dependency/lib.raml", base + "two-dependencies/dependency2/lib2.raml")),
    TestConfig("for-encode", "api%20with%20space.raml", Nil)
  )

  fixture.foreach { testCase =>
    s"Folder ${testCase.folder}" should s"should have dependencies: ${testCase.dependencies.length}" in {

      ExchangeConfigReader
        .readRoot("file://" + base + testCase.folder, AmfConfigurationWrapper(), EmptyLogger)
        .map { maybeConf =>
          maybeConf.isDefined should be(true)
          val config = maybeConf.get
          config.mainFile should be(testCase.main)
          assert(config.cachables.toList.sorted, testCase.dependencies)
        }(executionContext)
    }
  }

}
