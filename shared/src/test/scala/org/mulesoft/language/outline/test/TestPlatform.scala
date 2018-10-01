package org.mulesoft.language.outline.test

import amf.client.resource.{BaseFileResourceLoader, FileResourceLoader, HttpResourceLoader}
import amf.core.remote.{EcmaEncoder, FileNotFound, JvmPlatform, Platform}
import amf.internal.resource.{ResourceLoader, ResourceLoaderAdapter}
import java.io.{File, FileNotFoundException}
import java.net.URI
import java.util.concurrent.CompletableFuture

import amf.client.remote.Content
import amf.core.lexer.FileStream
import amf.core.remote.FutureConverter._
import amf.core.remote.FileMediaType._
import amf.core.unsafe.PlatformBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import amf.core.utils.Strings
import org.mulesoft.common.io.{FileSystem, Fs}

import scala.concurrent.Future

class TestPlatform extends JvmPlatform {

    override def name = "test"

    override def loaders(): Seq[ResourceLoader] = Seq(
        ResourceLoaderAdapter(new UTF8FileResourceLoader())
    )

}

object TestPlatform {
    private var instance: Option[TestPlatform] = None

    def getInstance(): TestPlatform = instance match {
        case Some(p) => p
        case None =>

            instance = Some(new TestPlatform())
            instance.get
    }
}

class UTF8FileResourceLoader() extends BaseFileResourceLoader {
    def fetchFile(resource: String): CompletableFuture[Content] = {
        //println("fetching utf-8")
        Future {
            try {
                //println(resource + ":" + new FileStream(new File(resource), "utf-8").data.toString)
                Content(new FileStream(new File(resource), "utf-8"),
                    ensureFileAuthority(resource),
                    extension(resource).flatMap(mimeFromExtension))
            } catch {
                case e: FileNotFoundException =>
                    // exception for local file system where we accept spaces [] and other chars in files names
                    val decoded = resource.urlDecoded
                    try {
                        Content(new FileStream(new File(resource), "utf-8"),
                            ensureFileAuthority(resource),
                            extension(resource).flatMap(mimeFromExtension))
                    } catch {
                        case e: FileNotFoundException => throw FileNotFound(e)
                    }
            }
        }.asJava
    }

    def ensureFileAuthority(str: String): String = if (str.startsWith("file:")) str else s"file://$str"
}