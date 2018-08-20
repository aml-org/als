package org.mulesoft.language.client.jvm;

import amf.client.remote.Content;
import org.mulesoft.language.server.core.platform.PlatformDependentPart;

import scala.concurrent.Future;

object JAVAPlatformDependentPart extends PlatformDependentPart {
	def fetchHttp(url: String): Future[Content] = Future.failed(new Throwable("Unsupported method"));
	
	override def encodeURI(url: String): String = "";
	
	override def encodeURIComponent(url: String): String = "";
	
	override def decodeURI(url: String): String = "";
	
	override def decodeURIComponent(url: String): String = "";
	
	override def normalizeURL(url: String): String = "";
	
	override def normalizePath(url: String): String = "";
	
	override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] = None;
	
	override def operativeSystem(): String = System.getProperty("os.name");
}