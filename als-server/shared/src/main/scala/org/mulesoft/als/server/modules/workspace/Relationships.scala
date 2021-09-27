package org.mulesoft.als.server.modules.workspace

import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.amfintegration.visitors.{AmfElementDefaultVisitors, AmfElementVisitors}
import org.mulesoft.lsp.feature.link.DocumentLink

private[workspace] object Relationships {
  def apply(repository: WorkspaceParserRepository, cu: CompilableUnit): Relationships =
    new Relationships(repository, cu)
}

class Relationships private (private val repository: WorkspaceParserRepository, cu: CompilableUnit) {

  private def getVisitorResult[T](uri: String)(fromTree: () => Seq[T],
                                               fallBack: AmfElementVisitors => Seq[T]): Seq[T] =
    if (repository.inTree(uri))
      fromTree()
    else {
      val visitors = AmfElementDefaultVisitors.build(cu.unit)
      visitors.applyAmfVisitors(cu.unit, cu.amfConfiguration)
      fallBack(visitors)
    }

  def getDocumentLinks(uri: String): Seq[DocumentLink] =
    getVisitorResult(uri)(() => repository.documentLinks().getOrElse(uri, Nil),
                          visitors => visitors.getDocumentLinksFromVisitors.getOrElse(uri, Nil))

  /**
    * Provides Project links for all files
    * @return
    */
  def getAllDocumentLinks: Map[String, Seq[DocumentLink]] =
    repository.documentLinks()

  def getAliases(uri: String): Seq[AliasInfo] =
    getVisitorResult(uri)(repository.aliases, visitors => visitors.getAliasesFromVisitors)

  def getRelationships(uri: String): Seq[RelationshipLink] =
    getVisitorResult(uri)(repository.relationships, visitors => visitors.getRelationshipsFromVisitors)
}
