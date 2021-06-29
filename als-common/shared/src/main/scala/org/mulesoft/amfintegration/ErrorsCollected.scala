package org.mulesoft.amfintegration

import amf.core.client.scala.validation.AMFValidationResult

case class ErrorsCollected(errors: List[AMFValidationResult])
