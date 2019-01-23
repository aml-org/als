package org.mulesoft.als.suggestions.plugins.raml

import amf.core.model.domain.AmfObject
import amf.core.remote.{Oas20, Raml08, Raml10, Vendor}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{
  ICompletionPlugin,
  ICompletionRequest,
  ICompletionResponse,
  LocationKind
}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra

import scala.concurrent.Future

class CommonHeadersNamesCompletionPlugin extends ICompletionPlugin {

  override def id: String = CommonHeadersNamesCompletionPlugin.ID

  override def languages: Seq[Vendor] = CommonHeadersNamesCompletionPlugin.supportedLanguages

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    val node: IHighLevelNode = elementForHeader(request).get
    val trailingSpaces       = trailingSpaceForKey(request.astNode.get.sourceInfo)
    Future.successful(
      CompletionResponse(
        CommonHeaderNames.names.map(n =>
          Suggestion(n, "New Header", n, request.prefix).withTrailingWhitespace(trailingSpaces)),
        LocationKind.KEY_COMPLETION,
        request
      ))
  }

  override def isApplicable(request: ICompletionRequest): Boolean = {
    request.astNode
      .filter(att => att.asAttr.exists(_.property.exists(_.getExtra(PropertySyntaxExtra).exists(_.isKey))))
      .flatMap(_.parent.flatMap(_.asElement))
      .exists { p =>
        isHeader(p.amfNode)
      }
  }

  private def isHeader(amfObject: AmfObject) = {
    amfObject match {
      case para: Parameter => para.isHeader
      case _               => false
    }
  }

  private def elementForHeader(request: ICompletionRequest): Option[IHighLevelNode] =
    request.astNode.flatMap(_.asElement).orElse(request.astNode.flatMap(_.parent.flatMap(_.asElement)))
}

object CommonHeadersNamesCompletionPlugin {
  val ID = "commonHeadersNames.completion"

  val supportedLanguages: Seq[Vendor] = Seq(Raml10, Raml08, Oas20)

  def apply(): CommonHeadersNamesCompletionPlugin = new CommonHeadersNamesCompletionPlugin()
}

object CommonHeaderNames {

  val names: Seq[String] = Seq(
    "Content-Encoding",
    "Content-Language",
    "Content-Location",
    "Content-Type",
    "Content-Length",
    "Content-Range",
    "Transfer-Encoding",
    "Cache-Control",
    "Expect",
    "Host",
    "HTTP2-Settings",
    "Max-Forwards",
    "Pragma",
    "Range",
    "TE",
    "If-Match",
    "If-Modified-Since",
    "If-None-Match",
    "If-Range",
    "If-Unmodified-Since",
    "Accept",
    "Accept-Charset",
    "Accept-Encoding",
    "Accept-Language",
    "Authorization",
    "Proxy-Authorization",
    "DNT",
    "From",
    "Referer",
    "User-Agent",
    "Age",
    "Expires",
    "Date",
    "Location",
    "Retry-After",
    "Tk",
    "Vary",
    "Warning",
    "ETag",
    "Last-Modified",
    "WWW-Authenticate",
    "Proxy-Authenticate",
    "Accept-Ranges",
    "Allow",
    "Server",
    "Accept-Patch",
    "Accept-Post",
    "Access-Control-Allow-Credentials",
    "Access-Control-Allow-Headers",
    "Access-Control-Allow-Methods",
    "Access-Control-Allow-Origin",
    "Access-Control-Expose-Headers",
    "Access-Control-Max-Age",
    "Access-Control-Request-Headers",
    "Access-Control-Request-Method",
    "Content-Disposition",
    "Content-Security-Policy",
    "Content-Security-Policy-Report-Only",
    "Cookie",
    "Forwarded",
    "Link",
    "Origin",
    "Prefer",
    "Preference-Applied",
    "Set-Cookie",
    "Strict-Transport-Security",
    "Via",
    "A-IM",
    "Accept-CH",
    "Accept-Features",
    "ALPN",
    "Alt-Svc",
    "Alternates",
    "Apply-To-Redirect-Ref",
    "CH",
    "Content-Base",
    "Content-DPR",
    "Cookie2",
    "DASL",
    "DAV",
    "Delta-Base",
    "Depth",
    "Destination",
    "DPR",
    "Encryption",
    "Encryption-Key",
    "IM",
    "If",
    "If-Schedule-Tag-Match",
    "Key",
    "Last-Event-ID",
    "Link-Template",
    "Lock-Token",
    "MD",
    "Negotiate",
    "Nice",
    "Overwrite",
    "POE",
    "POE-Links",
    "Redirect-Ref",
    "RW",
    "Schedule-Reply",
    "Schedule-Tag",
    "Sec-WebSocket-Accept",
    "Sec-WebSocket-Extensions",
    "Sec-WebSocket-Key",
    "Sec-WebSocket-Protocol",
    "Sec-WebSocket-Version",
    "Set-Cookie2",
    "SLUG",
    "Status-URI",
    "Sunset",
    "Surrogate-Capability",
    "Surrogate-Control",
    "TCN",
    "Timeout",
    "Variant-Vary",
    "X-Frame-Options"
  )
}
