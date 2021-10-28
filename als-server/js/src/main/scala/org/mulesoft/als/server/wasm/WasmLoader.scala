package org.mulesoft.als.server.wasm

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSGlobal, JSImport}

@js.native
@JSImport("@aml-org/amf-custom-validator-web", JSImport.Namespace)
object AmfCustomValidatorWeb extends AmfWasmOpaValidator

trait AmfWasmOpaValidator extends js.Any {
  def validate(profile: String,
               data: String,
               debug: Boolean,
               callback: js.Function2[UndefOr[String], UndefOr[JsError], Unit]): Unit = js.native

  def exit(): Unit = js.native
}

class Callback[T] {
  private val finished: Promise[T] = Promise()
  def callback: (UndefOr[T], UndefOr[JsError]) => Unit = (result: UndefOr[T], error: UndefOr[JsError]) => {
    if (result.isDefined) result.foreach(finished.success)
    else
      finished.failure(new JsCustomValidatorError(error.map(_.message).getOrElse("Js Wasm failed without message?")))
  }
  def future: Future[T] = finished.future
}

class JsCustomValidatorError(message: String) extends Exception(message)

@js.native
@JSGlobal
class JsError extends js.Object {
  val name: String           = js.native
  val message: String        = js.native
  val stack: UndefOr[String] = js.native
}
