package org.mulesoft.als.server

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.convert.CoreClientConverters.ClientList
import org.mulesoft.als.configuration.{DefaultJsServerSystemConf, JsServerSystemConf, ResourceLoaderConverter}
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.client.platform.{AlsClientNotifier, AlsLanguageServerFactory}
import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.als.server.workspace.WorkspaceManager

import scala.concurrent.ExecutionContextExecutor
import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichFutureNonThenable, JSRichGenTraversableOnce}
import scala.scalajs.js.Promise

class JsLanguageServerTest extends AMFValidatorTest {
  override implicit val executionContext: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  override def rootPath: String = ""

  val clientConnection: MockDiagnosticClientNotifier with AlsClientNotifier[js.Any] = new MockDiagnosticClientNotifier(3000) with AlsClientNotifier[js.Any] {
    override def notifyProjectFiles(params: FilesInProjectParams): Unit = ???

    override def notifySerialization(params: SerializationResult[js.Any]): Unit = ???
  }

  val systemConfig: JsServerSystemConf = DefaultJsServerSystemConf
  val serializationProps: JsSerializationProps = JsSerializationProps(clientConnection)

  //todo: reimplement
  ignore("Test custom validators plugged from client") {
    var flag = false
    val server = new AlsLanguageServerFactory(clientConnection)
      .withSerializationProps(serializationProps)
      .withResourceLoaders(systemConfig.clientLoaders)
      .withDirectoryResolver(systemConfig.clientDirResolver)
//      .withAmfPlugins(js.Array(testValidator(() => flag = true).asInstanceOf[ALSConverters.ClientAMFPlugin]))
      .build()
    val content =
      """#%RAML 1.0
        |title: Example of request bodies
        |mediaType: application/json
        |
        |
        |/groups:
        |  post:
        |    body:
        |      application/xml:
        |        type: Person
        |        example: !include person.xml
        |
        |types:
        |  Person:
        |    properties:
        |      age: integer
        |""".stripMargin

    val payload: String = """<Person>
                            |  <age>false</age>
                            |</Person>""".stripMargin
    withServer(server){ s =>
      for {
        _ <- openFile(s)("file:///person.xml", payload)
        _ <- openFile(s)("file:///uri.raml", content)
        _ <- clientConnection.nextCall
        _ <- clientConnection.nextCall
        _ <- clientConnection.nextCall
      } yield {
        assert(flag)
      }
    }
  }

  object JsTestLogger {
    def apply(): JsClientLogger =
      js.Dynamic
        .literal(
          error = (message: String) => logger.error(message,"", ""),
          warn = (message: String) => logger.warning(message, "", ""),
          info = (message: String) => logger.debug(message, "", ""),
          log = (message: String) => logger.debug(message, "",""),
        )
        .asInstanceOf[JsClientLogger]
  }

  val fakeRL: ClientResourceLoader = js.Dynamic
    .literal(
      accepts = new js.Function1[String, Boolean] {
        override def apply(arg1: String): Boolean = false
      },
      fetch = new js.Function1[String, js.Promise[_]] {
        override def apply(arg1: String): Promise[_] = js.Promise.resolve[String]("")
      }
    )
    .asInstanceOf[ClientResourceLoader]

  val platfromLoaders: List[ResourceLoader] = platform.loaders().toList

  def getServerResourceLoaders(resourceLoaders: js.Array[ClientResourceLoader]): Seq[ResourceLoader] = {
    (new AlsLanguageServerFactory(clientConnection)
      .withSerializationProps(serializationProps)
      .withResourceLoaders(resourceLoaders)
      .build()
      .workspaceService match {
      case ws: WorkspaceManager => ws
      case _ => fail("Workspace Manager not found?")
    }).editorConfiguration.resourceLoaders
  }

  test("Should have platform loaders as default") {
    val loaders = getServerResourceLoaders(js.Array())
    loaders.size should be(2)
    loaders should contain allElementsOf platfromLoaders
  }

  test("Should not have default platform loaders when given list isn't empty"){
    val loaders = getServerResourceLoaders(js.Array(fakeRL))
    loaders.size should be(1)
    loaders should contain noElementsOf platfromLoaders
  }

  test("Should keep platform loaders when added"){
    val loaders = getServerResourceLoaders((platform.loaders().map(_.toClient) :+ fakeRL).toJSArray)
    loaders.size should be(3)
  }

  implicit class nativeRLWrapper(rl: ResourceLoader) {
    def toClient: ClientResourceLoader =
      js.Dynamic
        .literal(
          accepts = new js.Function1[String, Boolean] {
            override def apply(arg1: String): Boolean = rl.accepts(arg1)
          },
          fetch = new js.Function1[String, js.Promise[_]] {
            override def apply(arg1: String): Promise[_] = rl.fetch(arg1).toJSPromise
          }
        )
        .asInstanceOf[ClientResourceLoader]
  }
}