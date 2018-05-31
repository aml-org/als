package org.mulesoft.als.suggestions.interfaces

import amf.core.remote.Vendor
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ICompletionPlugin {

    def id:String

    def languages:Seq[Vendor]

    def suggest(request:ICompletionRequest):Future[ICompletionResponse]

    def isApplicable(request:ICompletionRequest):Boolean

}
