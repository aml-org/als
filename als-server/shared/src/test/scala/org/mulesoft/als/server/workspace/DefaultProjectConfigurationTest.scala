package org.mulesoft.als.server.workspace

import amf.apicontract.client.scala.RAMLConfiguration
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.EmptyLogger
import org.scalatest.{AsyncFunSuite, Matchers}

class DefaultProjectConfigurationTest
    extends AsyncFunSuite
    with Matchers
    with PlatformSecrets
    with MockResourceLoader {
//todo: test project configuration adapter
  test("delete me") {
    assert(true)
  }
//  test("Cache working - from tree") {
//    val cacheableUri = "file://fakeURI/ws/cachable.raml"
//    val cachable: MockFile = MockFile(cacheableUri,
//                                      """#%RAML 1.0 Library
//        |types:
//        |  A: string
//      """.stripMargin)
//    val mainUri = "file://fakeURI/ws/api.raml"
//    val api: MockFile = MockFile(
//      mainUri,
//      """#%RAML 1.0
//        |title: test
//        |uses:
//        |  lib: cachable.raml
//        |types:
//        |  B: lib.A
//      """.stripMargin
//    )
//
//    val config = DefaultProjectConfiguration(
//      "",
//      Seq.empty,
//      Seq.empty,
//      ProjectConfiguration("", Some(mainUri), Set(cacheableUri), Set.empty, Set.empty, Set.empty),
//      EmptyLogger // todo: logger
//    )
//
//    val amfConfig = config
//      .configure(RAMLConfiguration.RAML10())
//      .withResourceLoader(buildResourceLoaderForFile(api))
//      .withResourceLoader(buildResourceLoaderForFile(cachable))
//      .baseUnitClient()
//    //todo: implement
////    amfConfig.parse(mainUri).map(result => {
////      val amf = AmfParseResult(result.)
////      val visitors = AmfElementDefaultVisitors.build(result.baseUnit)
////      val tree = MainFileTreeBuilder.build(result, visitors, logger)
////      config.cacheBuilder.updateCache(mainUri, )
////    })
//    assert(true)
//  }

}
