package org.mulesoft.als.server.lsp4j

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.mulesoft.als.server.lsp4j.extension.AlsInitializeParamsTypeAdapter
import org.scalatest.FunSuite

import java.io.{ByteArrayInputStream, InputStreamReader}

class GsonTypeAdapters extends FunSuite {

  private val gson = new Gson()

  test("ALS InitializationParams") {
    val serializationCapability = """{
                                      |    "capabilities": {
                                      |       "serialization": {
                                      |         "acceptsNotification": true
                                      |       }
                                      |     }
                                      |}""".stripMargin

    val typeAdapter: AlsInitializeParamsTypeAdapter = new AlsInitializeParamsTypeAdapter(gson)
    val in: JsonReader =
      new JsonReader(new InputStreamReader(new ByteArrayInputStream(serializationCapability.getBytes()), "UTF-8"))
    val params = typeAdapter.read(in)
    assert(params.getCapabilities.getSerialization.getAcceptsNotification.booleanValue())
  }
}
