package org.mulesoft.language.client.js.serverConnection

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("fs", JSImport.Namespace, "fs")
object FSFacade extends js.Object {
	def exists(path: String, handler: js.Function1[Boolean, Unit]): Unit = js.native
	
	def readDir(path: String, handler: js.Function2[js.Error, js.Array[String], Unit]): Unit = js.native
	
	def isDirectory(path: String, handler: js.Function2[js.Error, Boolean, Unit]): Unit = js.native
	
	def readFile(path: String, handler: js.Function2[js.Error, js.Object, Unit]): Unit = js.native
}

object FS {
	def exists(path: String): Future[Boolean] = {
		var promise = Promise[Boolean]();
		
		FSFacade.exists(this.removeProtocol(path), (result) => {
			promise.success(result);
		});
		
		promise.future;
	}
	
	def readDir(path: String): Future[Seq[String]] = {
		var promise = Promise[Seq[String]]();
		
		FSFacade.readDir(this.removeProtocol(path), (error, result) => {
			promise.success(result);
		});
		
		promise.future;
	}
	
	def isDirectory(path: String): Future[Boolean] = {
		var promise = Promise[Boolean]();
		
		FSFacade.isDirectory(this.removeProtocol(path), (error, result) => {
			promise.success(result);
		});
		
		promise.future;
	}
	
	def content(path: String): Future[String] = {
		var promise = Promise[String]();
		
		FSFacade.readFile(this.removeProtocol(path), (error, result) => {
			if(js.isUndefined(error) || error == null) {
				promise.success(result.toString());
			} else {
				promise.failure(new Throwable(error.message))
			}
		});
		
		promise.future;
	}
	
	private def removeProtocol(path: String): String = {
		if(path.indexOf("file:///") == 0) {
			return path.replace("file:///", "/");
		}
		
		return path;
	}
}
