package org.mulesoft.lsp.feature.reference

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object ReferenceConfigType
    extends ConfigType[ReferenceClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
