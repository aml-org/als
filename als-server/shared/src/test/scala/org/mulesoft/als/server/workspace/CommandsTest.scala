package org.mulesoft.als.server.workspace

import amf.client.remote.Content
import amf.core.CompilerContextBuilder
import amf.core.model.document.Document
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Amf, Mimes}
import amf.core.services.RuntimeCompiler
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}

import scala.concurrent.{ExecutionContext, Future}

class CommandsTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def buildServer(): LanguageServer = {
    val factory = ManagersFactory(MockDiagnosticClientNotifier, logger, withDiagnostics = false)
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .build()
  }

  override def rootPath: String = ""

  test("Parse and model errors at full validation") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |description: missing title
          |a
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)
      compile(server)(api).map { s =>
        s.length should be(1)
        s.head.uri should be(api)
        s.head.diagnostics.length should be(2)
      }
    }
  }

  test("Serialized editing resolved model") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |title: a title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)
      for {
        s <- serialize(server)(api)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(api, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        val api1 = parsed.asInstanceOf[Document].encodes.asInstanceOf[WebApi]
        api1.id should be("amf://id#/web-api")
      }
    }
  }
}
