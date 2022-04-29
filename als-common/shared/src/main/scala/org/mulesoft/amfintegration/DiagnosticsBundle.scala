package org.mulesoft.amfintegration

import org.mulesoft.als.common.dtoTypes.ReferenceStack

case class DiagnosticsBundle(isExternal: Boolean, references: Set[ReferenceStack]) {
  def and(stack: ReferenceStack): DiagnosticsBundle = DiagnosticsBundle(isExternal, references + stack)
}
