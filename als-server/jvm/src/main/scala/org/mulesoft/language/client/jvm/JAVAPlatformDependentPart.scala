// $COVERAGE-OFF$
package org.mulesoft.language.client.jvm;

import amf.client.remote.Content;
import org.mulesoft.language.server.core.platform.PlatformDependentPart;

import scala.concurrent.Future;

object JAVAPlatformDependentPart extends PlatformDependentPart {
	def fetchHttp(url: String): Future[Content] = Future.failed(new Throwable("Unsupported method"));
	
	override def encodeURI(url: String): String = url;
	
	override def encodeURIComponent(url: String): String = url;
	
	override def decodeURI(url: String): String = url;
	
	override def decodeURIComponent(url: String): String = url;
	
	override def normalizeURL(url: String): String = url;
	
	override def normalizePath(url: String): String = url;
	
	override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] = stream.toString.find(p);
	
	override def operativeSystem(): String = System.getProperty("os.name");
}
// $COVERAGE-ON$