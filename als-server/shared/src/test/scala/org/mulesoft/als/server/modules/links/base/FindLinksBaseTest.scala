package org.mulesoft.als.server.modules.links.base

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.links.FindLinksTest
import org.mulesoft.lsp.feature.link.DocumentLink

class FindLinksBaseTest extends FindLinksTest {

  test("simple-include") {
    runTest(
      "files/simple-include/simple.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(1, 16), Position(1, 32))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-include/title%20file.yaml",
          None
        )
      )
    )
  }

  test("simple-ref") {
    runTest(
      "files/simple-ref/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 9), Position(2, 28))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/simple%20contact.json",
          None
        )
      )
    )
  }

  test("simple-uses") {
    runTest(
      "files/simple-uses/simple.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 7), Position(2, 26))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-uses/simple%20library.raml",
          None
        )
      )
    )
  }

  test("simple-uri") {
    runTest(
      "files/simple-uri/simple.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(3, 7), Position(3, 90))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-uri/lib.raml",
          None
        )
      )
    )
  }

  test("Uri with path navigation") {
    runTest(
      "files/path-nav/path-nav-include.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(14, 23), Position(14, 31))),
          "file://als-server/shared/src/test/resources/actions/links/files/path-nav/ref.yaml",
          None
        )
      )
    )
  }

  test("file included two times") {
    runTest(
      "files/double-include/root.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(8, 22), Position(8, 34))),
          "file://als-server/shared/src/test/resources/actions/links/files/double-include/example.json",
          None
        ),
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(20, 30), Position(20, 42))),
          "file://als-server/shared/src/test/resources/actions/links/files/double-include/example.json",
          None
        )
      )
    )
  }

  test("smb link simple") {
    runTest(
      "files/smb-leak/simple.raml",
      Set.empty
    )
  }

  test("smb link simple-relative") {
    runTest(
      "files/smb-leak/simple-relative.raml",
      Set.empty
    )
  }

  // there are hacks in AMF (amf.core.internal.remote.Context) which result in this case working as if the file was
  // "absolute" (relative to the root file), and fixes the double slash resulting in
  // "file://als-server/shared/src/test/resources/actions/links/files/smb-leak/lib.raml" as a link
  // this is probably not correct, but should not affect the SMB Leak as it is not a `file:////` link
  ignore("smb link unix-path") {
    runTest(
      "files/smb-leak/unix-path.raml",
      Set.empty
    )
  }

  test("smb link windows-path") {
    runTest(
      "files/smb-leak/windows-path.raml",
      Set.empty
    )
  }

  test("smb link windows-uri-combined") {
    runTest(
      "files/smb-leak/windows-uri-combined.raml",
      Set.empty
    )
  }
}
