package org.mulesoft.als.server.custom

import java.lang.reflect.Type
import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import com.google.gson.{Gson, JsonElement}
import com.google.gson.reflect.TypeToken
import org.mulesoft.lsp.textsync.DidFocusParams

trait DidFocusCommand {
  val DID_FOCUS_CHANGE_COMMAND: String = "didFocusChange"

  def onDidFocus(args: util.List[AnyRef], callback: DidFocusParams => Unit): CompletableFuture[AnyRef] = {
    def parseParamsDidFocus(getArguments: util.List[AnyRef]): Option[DidFocusParams] = {
      try {
        if (getArguments.size() != 1) throw new Exception(s"Invalid argument ${getArguments.size()}")
        getArguments.get(0) match {
          case dfp: DidFocusParams => Some(dfp)
          case json: JsonElement =>
            val t: Type = new TypeToken[DidFocusParams]() {}.getType()
            Option(new Gson().fromJson(json, t))
          case _ => throw new Exception(s"Invalid argument ${getArguments}")
        }
      } catch {
        case _: Exception => None
      }
    }

    parseParamsDidFocus(args) match {
      case Some(p) =>
        callback(p)
        completedFuture("ok")
      case _ => completedFuture("wrong parameters")
    }
  }

}
