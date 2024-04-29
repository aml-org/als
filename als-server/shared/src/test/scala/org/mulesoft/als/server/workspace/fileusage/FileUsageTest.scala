package org.mulesoft.als.server.workspace.fileusage

import org.mulesoft.als.actions.fileusage.FindFileUsages
import org.mulesoft.lsp.feature.common.{Location, Position, Range}
import org.scalatest.Succeeded

import scala.concurrent.{ExecutionContext, Future}

class FileUsageTest extends ServerFileUsageTest with TestEntries {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("No handler") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (_, wsManager) <- buildServer(test.root, test.ws, test.mainFile)
            links <- FindFileUsages.getUsages(test.searchedUri, wsManager.getAllDocumentLinks(test.searchedUri, ""))
          } yield {
            (links, test.result)
          }
        }
      }
    } yield {
      assert(results.forall(t => t._1.toSet == t._2))
    }
  }

  test("Through handler") {
    Future
      .sequence {
        testSets.map { test =>
          runFileUsageTest(test.root, test.mainFile, test.ws, test.searchedUri, test.result)
        }
      }
      .map(r => assert(r.forall(_ == Succeeded)))
  }
}
