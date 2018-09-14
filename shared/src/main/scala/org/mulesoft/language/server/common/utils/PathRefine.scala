package org.mulesoft.language.server.common.utils

import amf.core.remote.Platform

object PathRefine {

    def refinePath(uri:String, platform:Platform):String = {

        val isWindows = platform.operativeSystem().toLowerCase().indexOf("win") == 0
       // println(s"Platform is: ${platform.operativeSystem()}, windows detected: ${isWindows}")
        var result = uri
        if(isWindows) {
            if (Option(uri).isDefined) {
                result = platform.decodeURIComponent(uri)
            }
        }
        result
    }

}
