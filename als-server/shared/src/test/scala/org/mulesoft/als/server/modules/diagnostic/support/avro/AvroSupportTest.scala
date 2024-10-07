package org.mulesoft.als.server.modules.diagnostic.support.avro

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.server.modules.diagnostic.BasicCleanDiagnosticTest
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity


class AvroSupportTest extends BasicCleanDiagnosticTest {

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
        assert(d.head.diagnostics.length == 1)
        assert(d.head.diagnostics.head.message == "Exception thrown in validation: Duplicate field zipcode in record Address: zipcode type:BOOLEAN pos:1 and zipcode type:INT pos:0.")
        assert(d.head.diagnostics.head.severity.get == DiagnosticSeverity.Error)
        assert(d.head.profile == ProfileNames.ASYNC26)
      }
    }
  }


  test("avro containing inline avro") {
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

  test("Avro with no errors") {
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
