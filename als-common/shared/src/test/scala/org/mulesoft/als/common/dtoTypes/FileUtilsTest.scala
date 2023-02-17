package org.mulesoft.als.common.dtoTypes

import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.common.URIImplicits._
import org.scalatest.funsuite.AnyFunSuite

class FileUtilsTest extends AnyFunSuite with PlatformSecrets {

  private val path = "root/drive/space in folder/file%3A"
  private val uri  = platform.encodeURI(path) // "root/drive/space%20in%20folder/file%253A"

  private def absUri(uri: String) = FileUtils.FILE_PROTOCOL + "/" + uri

  private def relUri(uri: String) = FileUtils.FILE_PROTOCOL + uri

  test("getPath from absolute path") {
    assert(FileUtils.getPath("/" + path, platform) == "/" + path)
  }

  test("getPath from relative path") {
    assert(FileUtils.getPath(path, platform) == path)
  }

  test("getPath from absolute URI") {
    assert(FileUtils.getPath(absUri(uri), platform) == "/" + path)
  }

  test("getPath from relative URI") {
    assert(FileUtils.getPath(relUri(uri), platform) == path)
  }

  test("getEncodedUri from absolute path") {
    assert(FileUtils.getEncodedUri("/" + path, platform) == absUri(uri))
  }

  test("getEncodedUri from relative path") {
    assert(FileUtils.getEncodedUri(path, platform) == relUri(uri))
  }

  test("getEncodedUri from absolute URI") {
    assert(FileUtils.getEncodedUri(absUri(uri), platform) == absUri(uri))
  }

  test("getEncodedUri from relative URI") {
    assert(FileUtils.getEncodedUri(relUri(uri), platform) == relUri(uri))
  }

  test("getDecodedUri from absolute path") {
    assert(FileUtils.getDecodedUri("/" + path, platform) == absUri(path))
  }

  test("getDecodedUri from relative path") {
    assert(FileUtils.getDecodedUri(path, platform) == relUri(path))
  }

  test("getDecodedUri from absolute URI") {
    assert(FileUtils.getDecodedUri(absUri(uri), platform) == absUri(path))
  }

  test("getDecodedUri from relative URI") {
    assert(FileUtils.getDecodedUri(relUri(uri), platform) == relUri(path))
  }

  test("getDecodedUri from relative decoded URI") {
    assert(FileUtils.getDecodedUri(relUri(path), platform) == relUri(path))
  }

  test("getPath from relative decoded URI") {
    assert(FileUtils.getPath(relUri(path), platform) == path)
  }

  ignore("getEncodedUri from relative decoded URI") { // TODO: this cant be done as we can't tell if the original was or not encoded
    assert(FileUtils.getEncodedUri(relUri(path), platform) == relUri(path))
  }

  test("URIImplicits") {
    val wrongEncode                 = "file:///test-sugg%20%281%29/folder2/api%20root.raml"
    val goodEncode                  = "file:///test-sugg%20(1)/folder2/api%20root.raml"
    val path                        = "/test-sugg (1)/folder2/api root.raml"
    val noEncode                    = "file:///test-sugg (1)/folder2/api root.raml"
    implicit val platform: Platform = this.platform
    assert(wrongEncode.toAmfUri == goodEncode)
    assert(goodEncode.toAmfUri == goodEncode)
    assert(noEncode.toAmfUri == goodEncode)
    assert(path.toAmfUri == goodEncode)
    assert(wrongEncode.toPath == path)
  }
}
