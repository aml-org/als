package org.mulesoft.als.nodeclient

import org.mulesoft.als.server.wasm.AmfWasmOpaValidator

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("@aml-org/amf-custom-validator", JSImport.Namespace)
object AmfCustomValidatorNode extends AmfWasmOpaValidator
