package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions

case object TypeDefinitionConfigType
    extends ConfigType[TypeDefinitionClientCapabilities, Either[Boolean, StaticRegistrationOptions]]
