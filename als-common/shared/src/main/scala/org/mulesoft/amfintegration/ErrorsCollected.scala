package org.mulesoft.amfintegration

import amf.core.validation.AMFValidationResult

case class ErrorsCollected(errors: List[AMFValidationResult])
