package org.mulesoft.als.server.workspace.fileusage

import org.scalatest.Succeeded

import scala.concurrent.Future

class FileContentsTest extends ServerFileUsageTest with TestEntries {
  test("Test File Contents") {
    Future
      .sequence {
        testSets.map { test =>
          runFileContentsTest(test.root, test.mainFile, test.ws, test.searchedUri, test.ws)
        }
      }
      .map(r => assert(r.forall(_ == Succeeded)))
  }
}
