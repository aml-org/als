package org.mulesoft.als.server.custom

import scala.reflect.ClassTag
import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import com.google.gson.{Gson, JsonElement}

trait CommandParamDeserializer[P] {

  def matcher: AnyRef => Option[P]

  def parseJson(json: JsonElement)(implicit ctag: ClassTag[P]): Option[P] =
    Option(new Gson().fromJson(json, ctag.runtimeClass.asInstanceOf[Class[P]]))

  def apply(args: util.List[AnyRef]): Option[P] = {
    if (args.size() != 1) None
    else matcher(args.get(0))
  }
}
