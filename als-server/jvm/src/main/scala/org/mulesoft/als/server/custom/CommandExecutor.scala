package org.mulesoft.als.server.custom

import scala.reflect.ClassTag
import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import com.google.gson.{Gson, JsonElement}

trait CommandExecutor[P] {

  def matcher: AnyRef => Option[P]

  def parseJson(json: JsonElement)(implicit ctag: ClassTag[P]): Option[P] =
    Option(new Gson().fromJson(json, ctag.runtimeClass.asInstanceOf[Class[P]]))

  def apply(args: util.List[AnyRef], callback: P => Unit): CompletableFuture[AnyRef] = {
    def parseParamsDidFocus(getArguments: util.List[AnyRef]): Option[P] = {
      if (getArguments.size() != 1) None
      matcher(getArguments.get(0))
    }

    parseParamsDidFocus(args) match {
      case Some(p) =>
        callback(p)
        completedFuture("ok")
      case _ => completedFuture("wrong parameters")
    }
  }
}
