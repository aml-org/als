package org.mulesoft.lsp.feature.implementation

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions

case object ImplementationConfigType
    extends ConfigType[ImplementationClientCapabilities, Either[Boolean, StaticRegistrationOptions]]
