package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation

class AlsValidationResult(val result: AMFValidationResult, val stack: Seq[DiagnosticRelatedInformation] = Seq())
