package org.mulesoft.als.suggestions.interfaces

import amf.core.remote.Vendor

trait ICompletionPlugin {

    def id:String

    def languages:Seq[Vendor]

    def suggest(request:ICompletionRequest):Seq[ISuggestion]

    def isApplicable(request:ICompletionRequest):Boolean

}
