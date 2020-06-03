package org.mulesoft.als.actions.fileusage

import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object FindFileUsages {

  def getUsages(uri: String, allLinks: Future[Map[String, Seq[DocumentLink]]]): Future[Seq[Location]] =
    for {
      links <- allLinks
    } yield {
      val locations: mutable.ListBuffer[Location] = mutable.ListBuffer()
      for { (u, documentLinks) <- links } {
        documentLinks
          .filter(_.target == uri)
          .map(dl => Location(u, dl.range))
          .foreach(locations +=)
      }
      locations.toList
    }
}
