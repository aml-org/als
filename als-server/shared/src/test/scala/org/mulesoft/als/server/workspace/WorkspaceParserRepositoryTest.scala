package org.mulesoft.als.server.workspace

import amf.client.parse.DefaultErrorHandler
import amf.client.remote.Content
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.modules.workspace.{ParsedUnit, WorkspaceParserRepository}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.{AmfInstance, AmfParseResult}
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceParserRepositoryTest extends AsyncFunSuite with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val amfConfig: AmfInstance = AmfInstance.default
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
      assert(apiPU.bu.id == api.uri)
      assert(getLocationOrFail(apiPU.bu) == api.uri)
      assert(cacheablePU.bu.id == cachable.uri)
      assert(getLocationOrFail(cacheablePU.bu) == cachable.uri)
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
      val moddedBU          = apiPU.bu.cloneUnit()
      moddedBU.withLocation("file://newLocation/api.raml")
      r.updateUnit(new AmfParseResult(moddedBU, new DefaultErrorHandler, ExternalFragmentDialect(), None))
      val moddedPU = getParsedUnitOrFail(r, "file://newLocation/api.raml")
      assert(moddedPU.bu.id == apiPU.bu.id) // Same id, but different location
      assert(moddedPU.bu.location() != apiPU.bu.location())
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

  def parse(url: String, env: Environment): Future[AmfParseResult] = amfConfig.modelBuilder().parse(url, env, None)

  def buildEnvironment(files: Set[MockFile]): Environment =
    Environment().withLoaders(files.map(f => buildResourceLoaderForFile(f)).toSeq)

  def makeRepository(files: Set[MockFile], cacheables: Set[String] = Set.empty): Future[WorkspaceParserRepository] = {
    val repository: WorkspaceParserRepository = new WorkspaceParserRepository(EmptyLogger)
    repository.setCachables(cacheables)
    val env: Environment = buildEnvironment(files)
    val futures: Set[Future[Unit]] = files.map(f => {
      parse(f.uri, env).map(bu => repository.updateUnit(bu))
    })
    Future.sequence(futures).map(_ => repository)
  }

  def makeRepositoryTree(files: Set[MockFile],
                         mainFile: MockFile,
                         cacheables: Set[String] = Set.empty): Future[WorkspaceParserRepository] = {
    val repository: WorkspaceParserRepository = new WorkspaceParserRepository(EmptyLogger)
    repository.setCachables(cacheables)
    val env: Environment = buildEnvironment(files)
    val future = parse(mainFile.uri, env).flatMap(bu => {
      repository.newTree(bu)
    })
    future.flatMap(_ => Future(repository))
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
