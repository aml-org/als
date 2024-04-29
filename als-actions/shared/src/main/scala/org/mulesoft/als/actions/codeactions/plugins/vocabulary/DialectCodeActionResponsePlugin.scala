package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}

abstract class DialectCodeActionResponsePlugin extends CodeActionResponsePlugin {
  protected val params: CodeActionRequestParams
  val dialect: Option[Dialect] = params.bu match {
    case d: Dialect => Some(d)
    case _          => None
  }
}
