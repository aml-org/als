package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.actions.common.AliasInfo
import org.mulesoft.als.server.modules.workspace.references.visitors.{AmfElementDefaultVisitors, AmfElementVisitors}
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

private[workspace] object Relationships {
  def apply(repository: Repository, fcu: () => Option[Future[CompilableUnit]]): Relationships =
    new Relationships(repository, fcu)
}

class Relationships private (private val repository: Repository, fcu: () => Option[Future[CompilableUnit]]) {

  private def getVisitorResult[T](uri: String)(fromTree: () => Seq[T],
                                               fallBack: AmfElementVisitors => Seq[T]): Future[Seq[T]] = {
    if (repository.inTree(uri))
      Future.successful(fromTree())
    else
      fcu()
        .map {
          _.map { cu => // todo: optimize in cases in which I want all references from the same BU?
            val visitors = AmfElementDefaultVisitors.build()
            visitors.applyAmfVisitors(List(cu.unit))
            fallBack(visitors)
          }
        }
        .getOrElse(Future.successful(Nil))
  }

  def getDocumentLinks(uri: String): Future[Seq[DocumentLink]] =
    getVisitorResult(uri)(() => repository.getDocumentLinks().getOrElse(uri, Nil),
                          visitors => visitors.getDocumentLinksFromVisitors.getOrElse(uri, Nil))

  def getAliases(uri: String): Future[Seq[AliasInfo]] =
    getVisitorResult(uri)(repository.getAliases, visitors => visitors.getAliasesFromVisitors)

  def getRelationships(uri: String): Future[Seq[(Location, Location)]] =
    getVisitorResult(uri)(repository.getRelationships, visitors => visitors.getRelationshipsFromVisitors)
}
