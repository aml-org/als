package org.mulesoft.als.suggestions.test.raml10

import amf.core.remote.Context
import org.mulesoft.als.suggestions.PlatformBasedExtendedFSProvider
import org.mulesoft.als.suggestions.interfaces.IExtendedFSProvider

import scala.concurrent.Future

class IncludeTestWithCustomProvider extends RAML10Test {

  test("Test not duplicate end bar") {
    this.runTest("includes/subdirs/include-sub-dir.raml", Set("fragments/"))
  }

  test("Test not duplicate begin bar") {
    this.runTest("includes/subdirs/include-sub2-dir.raml", Set("another/", "testFragment.raml"))
  }

  class BadPlatformBaseForSubDirs() extends PlatformBasedExtendedFSProvider(platform) {

    override def resolve(absBasePath: String, path: String): Option[String] = {
      val p = Context(platform, absBasePath).resolve(path)
      if (isDirectory(p) && !p.endsWith("/")) Some(p + "/")
      else Some(p)
    }

    override def readDirAsync(path: String): Future[Seq[String]] = super.readDirAsync(path).map(s => s.map("/" + _))
  }
}
