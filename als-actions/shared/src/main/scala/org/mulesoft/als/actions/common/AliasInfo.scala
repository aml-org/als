package org.mulesoft.als.actions.common

import org.mulesoft.lsp.feature.common.Location

case class AliasInfo(tag: String, declaration: Location, target: String)
