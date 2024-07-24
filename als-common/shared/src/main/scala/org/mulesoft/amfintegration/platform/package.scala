package org.mulesoft.amfintegration

package object platform {
  val filePrefix        = "file:"
  val windowsSMB        = "\\\\" // just in case because AMF sometimes tries to re-fetch a URI decoding it
  val windowsSMBEncoded = "%5c%5c"
  val unixStyle         = "//"
}
