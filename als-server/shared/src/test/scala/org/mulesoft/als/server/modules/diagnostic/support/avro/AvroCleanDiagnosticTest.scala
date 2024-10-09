package org.mulesoft.als.server.modules.diagnostic.support.avro

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.server.modules.diagnostic.BasicCleanDiagnosticTest
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity


class AvroCleanDiagnosticTest extends BasicCleanDiagnosticTest {

  test("Async importing valid Avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/async26-imports-valid-avro.yaml"))
      } yield {
        server.shutdown()
        d.foreach(filediag => assert(filediag.diagnostics.isEmpty))
        assert(d.size == 2)
        assert(d.head.profile == ProfileNames.ASYNC26)
      }
    }
  }

  test("Async importing invalid Avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/async26-imports-invalid-avro.yaml"))
      } yield {
        server.shutdown()
        assert(d.size == 2)
        val avroFileDiagnostics = d.find(d => d.uri.contains("avro/async26-imports-invalid-avro.yaml"))
        assert(avroFileDiagnostics.isDefined)
        assert(avroFileDiagnostics.get.diagnostics.length == 1)
        val errorMessage = avroFileDiagnostics.get.diagnostics.head.message.toLowerCase()
        assert(errorMessage.contains("duplicate") && errorMessage.contains("address"))
        assert(avroFileDiagnostics.get.diagnostics.head.severity.get == DiagnosticSeverity.Error)
        assert(avroFileDiagnostics.get.profile == ProfileNames.ASYNC26)
      }
    }
  }


  test("valid complex avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/schemas/avro-user/avrotoavro.avsc"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.isEmpty)
        assert(d.head.profile == ProfileNames.AVROSCHEMA)
      }
    }
  }

  test("valid avro with unions") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/schemas/union-type-payload-error.avsc"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.isEmpty)
        assert(d.head.profile == ProfileNames.AVROSCHEMA)
      }
    }
  }
}
