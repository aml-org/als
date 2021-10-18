package org.mulesoft.als.server.workspace

import amf.core.client.common.remote.Content
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.modules.workspace.{ParsedUnit, WorkspaceParserRepository}
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceParserRepositoryTest extends AsyncFunSuite with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val amfConfig: Future[AmfConfigurationWrapper] = AmfConfigurationWrapper()
  test("Basic repository test") {

    val cachable: MockFile = MockFile("file://fakeURI/ws/cachable.raml",
                                      """#%RAML 1.0 Library
      |types:
      |  A: string
      """.stripMargin)

    val api: MockFile = MockFile(
      "file://fakeURI/ws/api.raml",
      """#%RAML 1.0
      |title: test
      |uses:
      |  lib: cachable.raml
      |types:
      |  B: lib.A
      """.stripMargin
    )

    val repository = makeRepository(Set(api, cachable), Set(cachable.uri))
    repository.map(r => {
      val apiPU       = getParsedUnitOrFail(r, api.uri)
      val cacheablePU = getParsedUnitOrFail(r, cachable.uri)
      assert(apiPU.parsedResult.result.baseUnit.id == api.uri)
      assert(getLocationOrFail(apiPU.parsedResult.result.baseUnit) == api.uri)
      assert(cacheablePU.parsedResult.result.baseUnit.id == cachable.uri)
      assert(getLocationOrFail(cacheablePU.parsedResult.result.baseUnit) == cachable.uri)
    })
  }

  def getParsedUnitOrFail(respository: WorkspaceParserRepository, uri: String): ParsedUnit = {
    respository.getUnit(uri).getOrElse(fail(s"$uri not parsed"))
  }

  def getLocationOrFail(bu: BaseUnit): String = {
    bu.location().getOrElse(fail(s"Couldn't get location for ${bu.id}"))
  }

  test("Prioritize location over id") {
    val api: MockFile = MockFile(
      "file://fakeURI/ws/api.raml",
      """
      |#%RAML 1.0
      |title: api
      |traits
      |   trait1: !import trait.raml
      |/endpoint:
      |   is: trait1
      |""".stripMargin
    )

    val traitMF: MockFile = MockFile(
      "file://fakeURI/ws/trait.raml",
      """
      |#%RAML 1.0 Trait
      |properties:
      |   body:
      |       application/json:
      |           type: integer
      |""".stripMargin
    )

    val repository: Future[WorkspaceParserRepository] = makeRepository(Set(api, traitMF))
    repository.map(r => {
      val apiPU: ParsedUnit = getParsedUnitOrFail(r, api.uri)
      val moddedBU: AMFResult =
        AMFResult(apiPU.parsedResult.result.baseUnit.cloneUnit().withLocation("file://newLocation/api.raml"),
                  apiPU.parsedResult.result.results) // this `result.result.results` looks hideous, i know

      r.updateUnit(new AmfParseResult(moddedBU, ExternalFragmentDialect(), r.amfConfiguration))
      val moddedPU = getParsedUnitOrFail(r, "file://newLocation/api.raml")
      assert(moddedPU.parsedResult.result.baseUnit.id == apiPU.parsedResult.result.baseUnit.id) // Same id, but different location
      assert(moddedPU.parsedResult.result.baseUnit.location() != apiPU.parsedResult.result.baseUnit.location())
    })
  }

  test("Cache working") {
    val cachable: MockFile = MockFile("file://fakeURI/ws/cachable.raml",
                                      """#%RAML 1.0 Library
      |types:
      |  A: string
      """.stripMargin)

    val api: MockFile = MockFile(
      "file://fakeURI/ws/api.raml",
      """#%RAML 1.0
      |title: test
      |uses:
      |  lib: cachable.raml
      |types:
      |  B: lib.A
      """.stripMargin
    )

    val repository: Future[WorkspaceParserRepository] = makeRepositoryTree(Set(api, cachable), api, Set(cachable.uri))
    repository.flatMap(r => {
      r.resolverCache
        .fetch(cachable.uri)
        .map(_.content.raw.getOrElse("err"))
        .map(cached => { assert(cached == cachable.content) })
    })
  }

  test("Correct tree keys") {
    val cachable: MockFile = MockFile("file://fakeURI/ws/cachable.raml",
                                      """#%RAML 1.0 Library
      |types:
      |  A: string
      """.stripMargin)

    val api: MockFile = MockFile(
      "file://fakeURI/ws/api.raml",
      """#%RAML 1.0
      |title: test
      |uses:
      |  lib: cachable.raml
      |types:
      |  B: lib.A
      """.stripMargin
    )

    val repository: Future[WorkspaceParserRepository] = makeRepositoryTree(Set(api, cachable), api, Set(cachable.uri))
    repository.flatMap(r => {
      assert(r.inTree(api.uri))
      assert(r.inTree(cachable.uri))
    })
  }

  test("Dependencies") {
    val cachable: MockFile = MockFile("file://fakeURI/ws/cachable.raml",
                                      """#%RAML 1.0 Library
      |types:
      |  A: string
      """.stripMargin)

    val api: MockFile = MockFile(
      "file://fakeURI/ws/api.raml",
      """#%RAML 1.0
      |title: test
      |uses:
      |  lib: cachable.raml
      |types:
      |  B: lib.A
      """.stripMargin
    )

    val repository: Future[WorkspaceParserRepository] = makeRepositoryTree(Set(api, cachable), api, Set(cachable.uri))
    repository.flatMap(r => {
      r.references.get(cachable.uri) match {
        case Some(result) =>
          assert(result.references.size == 1)
          assert(result.references.head.stack.size == 1)
          assert(result.references.head.stack.head.originUri == api.uri)
        case None => fail(s"No references for ${cachable.uri}")
      }

      r.references.get(api.uri) match {
        case Some(result) => assert(result.references.head.stack.isEmpty)
        case None         => fail(s"No references for ${api.uri}")
      }
    })
  }

  case class MockFile(uri: String, content: String)

  def makeRepository(files: Set[MockFile], cacheables: Set[String] = Set.empty): Future[WorkspaceParserRepository] = {
    for {
      branchAmfConfig <- configWithRL(files)
      repository <- Future {
        val r = new WorkspaceParserRepository(branchAmfConfig, EmptyLogger)
        r.setCachables(cacheables)
        r
      }
      r <- Future
        .sequence(files.map(f => {
          branchAmfConfig.parse(f.uri).map(bu => repository.updateUnit(bu))
        }))
        .map(_ => repository)
    } yield r
  }

  private def configWithRL(files: Set[MockFile]): Future[AmfConfigurationWrapper] = {
    for {
      branchAmfConfig <- amfConfig.map(_.branch)
      _               <- Future { files.foreach(f => branchAmfConfig.withResourceLoader(buildResourceLoaderForFile(f))) }
    } yield branchAmfConfig
  }

  def makeRepositoryTree(files: Set[MockFile],
                         mainFile: MockFile,
                         cacheables: Set[String] = Set.empty): Future[WorkspaceParserRepository] = {
    for {
      branchAmfConfig <- configWithRL(files)
      repository <- Future {
        val r = new WorkspaceParserRepository(branchAmfConfig, EmptyLogger)
        r.setCachables(cacheables)
        r
      }
      _ <- branchAmfConfig
        .parse(mainFile.uri)
        .flatMap(bu => repository.newTree(bu))
    } yield repository

  }

  def buildResourceLoaderForFile(mockFile: MockFile): ResourceLoader = {
    val fileUrl: String = mockFile.uri
    val content: String = mockFile.content
    new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    }
  }

}
