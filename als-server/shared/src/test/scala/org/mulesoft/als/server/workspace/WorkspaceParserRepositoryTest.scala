package org.mulesoft.als.server.workspace

import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.modules.workspace.{ParsedUnit, WorkspaceParserRepository}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AmfParseResult,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceParserRepositoryTest extends AsyncFunSuite with Matchers with PlatformSecrets with MockResourceLoader {
  override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

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

    val repository = makeRepository(Set(api, cachable))
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

      r.updateUnit(new AmfParseResult(moddedBU, ExternalFragmentDialect(), apiPU.parsedResult.context, api.uri))
      val moddedPU = getParsedUnitOrFail(r, "file://newLocation/api.raml")
      assert(moddedPU.parsedResult.result.baseUnit.id == apiPU.parsedResult.result.baseUnit.id) // Same id, but different location
      assert(moddedPU.parsedResult.result.baseUnit.location() != apiPU.parsedResult.result.baseUnit.location())
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

    val repository: Future[WorkspaceParserRepository] = makeRepositoryTree(Set(api, cachable), api)
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

    val repository: Future[WorkspaceParserRepository] = makeRepositoryTree(Set(api, cachable), api)
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

  def makeRepository(files: Set[MockFile]): Future[WorkspaceParserRepository] = {
    for {
      aLSConfigurationState <- configWithRL(files)
      repository <- Future {
        val r = new WorkspaceParserRepository(EmptyLogger)
        r
      }
      r <- Future
        .sequence(files.map(f => {
          aLSConfigurationState.parse(f.uri).map(bu => repository.updateUnit(bu))
        }))
        .map(_ => repository)
    } yield r
  }

  private def configWithRL(files: Set[MockFile]): Future[ALSConfigurationState] = {
    val rls    = files.map(f => buildResourceLoaderForFile(f))
    val global = EditorConfiguration.withPlatformLoaders(rls.toSeq)
    global.getState.map(ALSConfigurationState(_, EmptyProjectConfigurationState, None))(executionContext)
  }

  def makeRepositoryTree(files: Set[MockFile], mainFile: MockFile): Future[WorkspaceParserRepository] = {
    for {
      globalConfiguration <- configWithRL(files)
      repository          <- Future { new WorkspaceParserRepository(EmptyLogger) }
      _ <- globalConfiguration
        .parse(mainFile.uri)
        .flatMap(bu => repository.newTree(bu))
    } yield repository

  }

}
