package org.mulesoft.als.suggestions.interfaces

import amf.core.remote.Vendor
import org.mulesoft.high.level.interfaces.ISourceInfo

import scala.concurrent.Future

trait ICompletionPlugin {

  def id: String

  def languages: Seq[Vendor]

  def suggest(request: ICompletionRequest): Future[ICompletionResponse]

  def isApplicable(request: ICompletionRequest): Boolean

  protected def trailingSpaceForKey(sourceInfo: ISourceInfo): String = {
    val off = sourceInfo.valueOffset.getOrElse(0) + 2
    "\n" + " " * off
  }
}
