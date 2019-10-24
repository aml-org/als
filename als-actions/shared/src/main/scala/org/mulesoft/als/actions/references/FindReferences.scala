package org.mulesoft.als.actions.references

import org.mulesoft.lsp.common.Location

trait FindReferences {
  def getReferences: Seq[Location] = ???
}
