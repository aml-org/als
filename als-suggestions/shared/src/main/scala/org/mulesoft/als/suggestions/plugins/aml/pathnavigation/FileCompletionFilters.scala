package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.suggestions.plugins.aml.pathnavigation.FileCompletionFilters.FileFilterPredicate

object FileCompletionFilters {
  type FileFilterPredicate = PredicateParams => Boolean

  def addFilter(filter: FileFilterPredicate): Unit = filters = filters :+ filter

  def clearFilters(): Unit = filters = defaultFilters

  def filter: FileFilterPredicate = predicate => filters.forall(_(predicate))

  private val defaultFilters: Seq[FileFilterPredicate] = Seq(
    FilterSupportedTypes(),
    FilterCurrentFile()
  )
  private var filters: Seq[FileFilterPredicate] = defaultFilters
}

private object FilterSupportedTypes extends PathCompletion {
  def apply(): FileFilterPredicate = {
    case PredicateParams(_, _, fileWithType, platform) =>
      fileWithType.isDirectory || supportedExtension(fileWithType.file, platform)
    case _ => false
  }
}

private object FilterCurrentFile {
  def apply(): FileFilterPredicate = {
    case PredicateParams(fullUri, actual, fileWithType, platform) =>
      s"${fullUri.toPath(platform)}${fileWithType.file}" != actual
    case _ => false
  }
}

case class PredicateParams(fullUri: String, actual: String, fileWithType: FileWithType, platform: Platform)