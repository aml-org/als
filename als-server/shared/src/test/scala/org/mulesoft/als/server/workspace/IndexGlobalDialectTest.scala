package org.mulesoft.als.server.workspace

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.client.scala.model.document.ExternalFragment
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.{Flaky, MockDiagnosticClientNotifier, ServerIndexGlobalDialectCommand}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.{TraceKind, WorkspaceFolder}

import scala.concurrent.ExecutionContext

trait IndexGlobalDialectTest extends ServerIndexGlobalDialectCommand {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "config-provider"

  val dialect: String = """#%Dialect 1.0
                  |dialect: Test
                  |version: 2
                  |external:
                  |  myExternal: http://voc.com/#
                  |documents:
                  |  root:
                  |    encodes: A
                  |nodeMappings:
                  |  A:
                  |    classTerm: myExternal.ExternalClass
                  |    mapping:
                  |      prop1:
                  |        range: string
                  |        propertyTerm: myExternal.stringName
                  |      prop2:
                  |        required: true
                  |        range: integer
                  |        propertyTerm: myExternal.mixedScalar""".stripMargin
  val instance1Path: String = filePath("ws1/instance.yaml")
  val instance1: String =
    """#%Test 2
      |prop1: name
      |prop2: this""".stripMargin

  val instance2Path: String = filePath("ws2/instance.yaml")
  val instance2: String =
    """#%Test 2
      |prop1: name
      |prop2: 2""".stripMargin

  private val dialectPath = filePath("ws1/dialect.yaml")

  test("Global dialect will apply to all workspaces", Flaky) {
    val notifier     = new MockDiagnosticClientNotifier(3000)
    val (server, wm) = buildServer(notifier)
    withServer(
      server,
      AlsInitializeParams(
        Some(AlsClientCapabilities()),
        Some(TraceKind.Off),
        workspaceFolders = Some(Seq(WorkspaceFolder(filePath("ws1")), WorkspaceFolder(filePath("ws2"))))
      )
    )(s => {
      for {
        _  <- openFile(s)(instance1Path, instance1)
        _  <- openFile(s)(instance2Path, instance2)
        _  <- notifier.nextCall
        _  <- notifier.nextCall
        u1 <- wm.getUnit(instance1Path, "Instance1")
        u2 <- wm.getUnit(instance2Path, "Instance2")
        _  <- indexGlobalDialect(s, dialectPath, dialect)
        _  <- focusNotification(s)(instance1Path, 1)
        _  <- focusNotification(s)(instance2Path, 1)
        _  <- notifier.nextCall
        _  <- notifier.nextCall
        i1 <- wm.getUnit(instance1Path, "Instance1")
        i2 <- wm.getUnit(instance2Path, "Instance2")
      } yield {
        assert(u1.unit.isInstanceOf[ExternalFragment])
        assert(u2.unit.isInstanceOf[ExternalFragment])
        assert(i1.unit.isInstanceOf[DialectInstance])
        assert(i1.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
        assert(i2.unit.isInstanceOf[DialectInstance])
        assert(i2.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
      }
    })
  }

  test("Global dialect is immutable", Flaky) {
    val notifier     = new MockDiagnosticClientNotifier(3000)
    val (server, wm) = buildServer(notifier)
    withServer(
      server,
      AlsInitializeParams(
        Some(AlsClientCapabilities()),
        Some(TraceKind.Off),
        workspaceFolders = Some(Seq(WorkspaceFolder(filePath("ws1"))))
      )
    )(s => {
      for {
        _  <- openFile(s)(dialectPath, dialect)
        _  <- indexGlobalDialect(s, dialectPath, dialect)
        _  <- openFile(s)(instance1Path, instance1)
        _  <- notifier.nextCall
        _  <- notifier.nextCall
        i1 <- wm.getUnit(instance1Path, "Instance1")
        _  <- changeFile(s)(dialectPath, dialect.replace("required: true", ""), 2)
        _  <- notifier.nextCall
        _  <- focusNotification(s)(instance1Path, 1)
        i2 <- wm.getUnit(instance1Path, "Instance2")
      } yield {
        assert(i1.unit.isInstanceOf[DialectInstance])
        assert(i1.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
        assert(i2.unit.isInstanceOf[DialectInstance])
        assert(i2.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
        assert(i1.unit == i2.unit)
      }
    })
  }

  test("Index global dialect will update when registering again", Flaky) {
    val notifier     = new MockDiagnosticClientNotifier(3000)
    val (server, wm) = buildServer(notifier)
    withServer(
      server,
      AlsInitializeParams(
        Some(AlsClientCapabilities()),
        Some(TraceKind.Off),
        workspaceFolders = Some(Seq(WorkspaceFolder(filePath("ws1"))))
      )
    )(s => {
      for {
        _  <- indexGlobalDialect(s, dialectPath, dialect)
        _  <- openFile(s)(instance1Path, instance1)
        d1 <- notifier.nextCall
        i1 <- wm.getUnit(instance1Path, "Instance1")
        _  <- indexGlobalDialect(s, dialectPath, dialect.replace("integer", "string"))
        _  <- focusNotification(s)(instance1Path, 1)
        d2 <- notifier.nextCall
        i2 <- wm.getUnit(instance1Path, "Instance2")
      } yield {
        assert(d1.uri == d2.uri && d1.uri == instance1Path)
        assert(d1.diagnostics.nonEmpty)
        assert(d2.diagnostics.isEmpty)
        assert(i1.unit.isInstanceOf[DialectInstance])
        assert(i1.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
        assert(i2.unit.isInstanceOf[DialectInstance])
        assert(i2.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
        assert(i1.unit != i2.unit)
      }
    })
  }

  test("Index global dialect will read from fs") {
    val notifier     = new MockDiagnosticClientNotifier(3000)
    val (server, wm) = buildServer(notifier)
    withServer(
      server,
      AlsInitializeParams(
        Some(AlsClientCapabilities()),
        Some(TraceKind.Off),
        workspaceFolders = Some(Seq(WorkspaceFolder(filePath("ws1"))))
      )
    )(s => {
      for {
        _  <- indexGlobalDialect(s, dialectPath)
        _  <- openFile(s)(instance1Path, instance1)
        d1 <- notifier.nextCall
        i1 <- wm.getUnit(instance1Path, "Instance1")
      } yield {
        d1.diagnostics.find(_.message.contains("Property: 'prop2' not supported")) should not be empty
        assert(i1.unit.isInstanceOf[DialectInstance])
        assert(i1.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
      }
    })
  }

  test("Index global dialect will read from environment provider", Flaky) {
    val notifier     = new MockDiagnosticClientNotifier(3000)
    val (server, wm) = buildServer(notifier)
    withServer(
      server,
      AlsInitializeParams(
        Some(AlsClientCapabilities()),
        Some(TraceKind.Off),
        workspaceFolders = Some(Seq(WorkspaceFolder(filePath("ws1"))))
      )
    )(s => {
      for {
        _  <- indexGlobalDialect(s, dialectPath)
        _  <- openFile(s)(instance1Path, instance1)
        d1 <- notifier.nextCall
        _  <- openFile(s)(dialectPath, dialect)
        _  <- notifier.nextCall
        _  <- indexGlobalDialect(s, dialectPath)
        _  <- focusNotification(s)(instance1Path, 1)
        d2 <- notifier.nextCall
        i1 <- wm.getUnit(instance1Path, "Instance1")
      } yield {
        d1.uri should equal(instance1Path)
        d1.uri should equal(d2.uri)
        d1.diagnostics.find(_.message.contains("Property: 'prop2' not supported")) should not be empty
        d2.diagnostics.find(_.message.contains("Property: 'prop2' not supported")) should be(empty)
        assert(i1.unit.isInstanceOf[DialectInstance])
        assert(i1.unit.asInstanceOf[DialectInstance].processingData.definedBy().value() == dialectPath)
      }
    })
  }

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): (LanguageServer, WorkspaceManager) = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, EditorConfiguration())
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    dm.foreach(m => b.addInitializableModule(m))
    (b.build(), factory.workspaceManager)
  }

}
