//package org.mulesoft.language.server.common.utils
//
//import org.mulesoft.language.server.common.path.PathIndex
//import org.mulesoft.language.server.common.urijs.UrijsIndex
//import org.mulesoft.language.server.common.typeInterfaces.ILoggerSettings;
//import org.mulesoft.language.server.common.typeInterfaces.MessageSeverity;
//import org.mulesoft.language.server.common.utils.LogMessage;
//
//class UtilsIndex{
//
//def pathFromURI(uri: String) = {
// return (new URI( uri )).path()
//
//}
//def isHTTPUri(uri: String) = {
// val protocol = (new URI( uri )).protocol()
//return (("http"===protocol)||("HTTP"===protocol))
//
//}
//def isFILEUri(uri: String) = {
// val protocol = (new URI( uri )).protocol()
//return (("file"===protocol)||("FILE"===protocol))
//
//}
//def extName(uri: String) = {
// return path.extname( pathFromURI( uri ) )
//
//}
//def resolve(path1: String, path2: String): String = {
// return path.resolve( path1, path2 ).replace( java.util.regex.Pattern.compile(raw"""\\""", "g"), "/" )
//
//}
//def basename(path1: String) = {
// return path.basename( path1 )
//
//}
//def dirname(path1: String) = {
// return path.dirname( path1 )
//
//}
//def transformUriToOriginalFormat(originalUri: String, toTransform: String) = {
// if (((isFILEUri( originalUri )&&(!isFILEUri( toTransform )))&&(!isHTTPUri( toTransform )))) {
// return (new URI( toTransform )).protocol( "file" ).`toString`()
//
//}
//return toTransform
//
//}
//def filterLogMessage(message: LogMessage, settings: ILoggerSettings): LogMessage = {
// if ((!settings)) {
// return Map( "message" -> message.message,
//"severity" -> message.severity,
//"component" -> message.component,
//"subcomponent" -> message.subcomponent )
//
//}
//if (settings.disabled) {
// return null
//
//}
//if ((message.component&&settings.allowedComponents)) {
// if ((settings.allowedComponents.indexOf( message.component )===(-1))) {
// return null
//
//}
//
//}
//if ((message.component&&settings.deniedComponents)) {
// if ((settings.allowedComponents.indexOf( message.component )!==(-1))) {
// return null
//
//}
//
//}
//if (((settings.maxSeverity!=null)&&(message.severity!=null))) {
// if ((message.severity<settings.maxSeverity)) {
// return null
//
//}
//
//}
//var text = message.message
//if (((settings.maxMessageLength&&text)&&(text.length>settings.maxMessageLength))) {
// (text=text.substring( 0, (settings.maxMessageLength-1) ))
//
//}
//return Map( "message" -> text,
//"severity" -> message.severity,
//"component" -> message.component,
//"subcomponent" -> message.subcomponent )
//
//}
//
//
//}
