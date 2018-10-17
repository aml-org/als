package org.mulesoft.language.test.serverConnection

import java.io.File

import amf.core.unsafe.PlatformSecrets

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


object FS extends PlatformSecrets {
	def exists(path: String): Future[Boolean] = {
		Future.successful(new File(path).exists())
	}

	def readDir(path: String): Future[Seq[String]] = {
        val arr = new File(path).list()
        Future.successful(arr.toSeq)
	}

	def isDirectory(path: String): Future[Boolean] = {
        Future.successful(new File(path).isDirectory())
	}

	def content(path: String): Future[String] = {
		throw new Error("not implemented")
	}

	private def removeProtocol(path: String): String = {
		if(path.indexOf("file:///") == 0) {
			return path.replace("file:///", "/");
		}

		return path;
	}
}

