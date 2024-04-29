package org.mulesoft.als.server.modules.actions

import amf.core.internal.validation.CoreValidations
import org.mulesoft.amfintegration.ErrorsCollected

trait FormattingManager {
  private val syntaxValidationId: String = CoreValidations.SyamlError.id
  def getSyntaxErrors(ec: ErrorsCollected, uri: String): ErrorsCollected =
    ErrorsCollected(
      ec.errors.filter(v => v.location.exists(_.equals(uri)) && v.validationId.equals(syntaxValidationId))
    )
}
