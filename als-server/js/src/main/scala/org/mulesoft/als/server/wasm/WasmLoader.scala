package org.mulesoft.als.server.wasm

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSGlobal, JSImport}

@js.native
@JSImport("@aml-org/amf-custom-validator-web", JSImport.Namespace)
object AmfCustomValidatorWeb extends AmfWasmOpaValidator

@js.native
trait AmfWasmOpaValidator extends js.Any {
  def initialize(callback: js.Function1[UndefOr[JsError], Unit]): Unit = js.native

  def validate(profile: String,
               data: String,
               debug: Boolean,
               callback: js.Function2[UndefOr[String], UndefOr[JsError], Unit]): Unit = js.native

  def exit(): Unit = js.native
}

trait Callback[T] {
  protected val finished: Promise[T] = Promise()
  def future: Future[T]              = finished.future

}

class Callback2[T] extends Callback[T] {
  def callback: (UndefOr[T], UndefOr[JsError]) => Unit =
    (result: UndefOr[T], error: UndefOr[JsError]) =>
      if (error.exists(_ != null))
        finished.failure(new JsCustomValidatorError(error.map(_.message).getOrElse("Js Wasm failed without message?")))
      else
        result.foreach(finished.success)
}

class Callback1 extends Callback[Unit] {
  def callback: UndefOr[JsError] => Unit =
    (err: UndefOr[JsError]) =>
      if (err.exists(_ == null)) { // returns null when successful
        finished.success()
      } else
        finished.failure(new JsCustomValidatorError(err.map(_.message).getOrElse("Js Wasm failed without message?")))
}

class JsCustomValidatorError(message: String) extends Exception(message)

@js.native
@JSGlobal
class JsError extends js.Object {
  val name: String           = js.native
  val message: String        = js.native
  val stack: UndefOr[String] = js.native
}
