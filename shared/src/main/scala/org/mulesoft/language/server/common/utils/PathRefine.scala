package org.mulesoft.language.server.common.utils

import amf.core.remote.Platform

object PathRefine {

    def refinePath(uri:String, platform:Platform):String = {
        val isWindows = platform.operativeSystem().toLowerCase().indexOf("win") >= 0
        var result = uri
        if(isWindows) {
            if (Option(uri).isDefined) {
                result = platform.decodeURIComponent(uri)
            }
            if (uri.startsWith("file:///")) {
                result = result.replace("file:///", "file://")
            }
        }
        result
    }

}
