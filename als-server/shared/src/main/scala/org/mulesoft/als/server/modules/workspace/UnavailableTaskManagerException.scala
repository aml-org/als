package org.mulesoft.als.server.modules.workspace

import org.mulesoft.exceptions.AlsException

class UnavailableTaskManagerException(uri: String = "", uuid: String = "")
    extends AlsException("TaskManager is not available.", uri, uuid)
