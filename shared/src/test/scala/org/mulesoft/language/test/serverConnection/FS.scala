package org.mulesoft.language.test.serverConnection

import amf.core.unsafe.PlatformSecrets

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


object FS extends PlatformSecrets {
	def exists(path: String): Future[Boolean] = {
		platform.fs.asyncFile(path).exists
	}

	def readDir(path: String): Future[Seq[String]] = {
        platform.fs.asyncFile(path).list.map(x=>x)
	}

	def isDirectory(path: String): Future[Boolean] = {
        platform.fs.asyncFile(path).isDirectory
	}

	def content(path: String): Future[String] = {
		platform.resolve(path).map(_.stream.toString)
	}

	private def removeProtocol(path: String): String = {
		if(path.indexOf("file:///") == 0) {
			return path.replace("file:///", "/");
		}

		return path;
	}
}

