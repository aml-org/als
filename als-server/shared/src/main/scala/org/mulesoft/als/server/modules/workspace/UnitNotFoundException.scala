package org.mulesoft.als.server.modules.workspace

import org.mulesoft.exceptions.AlsException

case class UnitNotFoundException(uri: String, uuid: String)
    extends AlsException(s"Unit not found at repository for uri: $uri", uri, uuid)
