package org.mulesoft.als.server

import amf.apicontract.internal.convert.ApiBaseClientConverter
import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.ClientInternalMatcher
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.configuration.{ClientDirectoryResolver, DirectoryResolverAdapter, ResourceLoaderConverter}
import org.mulesoft.als.logger.Logger

trait ALSConverters extends ApiBaseClientConverter with ClientLoggerConverter {

  type ClientAMFValidator
  type ClientAMFPlugin

  implicit object DirectoryResolverConverter
      extends ClientInternalMatcher[ClientDirectoryResolver, DirectoryResolver] {
    override def asInternal(from: ClientDirectoryResolver): DirectoryResolver =
      DirectoryResolverAdapter.convert(from)
  }

  implicit object ClientResourceLoaderConverter extends ClientInternalMatcher[ClientResourceLoader, ResourceLoader] {
    override def asInternal(from: ClientResourceLoader): ResourceLoader =
      ResourceLoaderConverter.internalResourceLoader(from)
  }

  def toInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin

  implicit object ClientAMFPluginConverter
      extends ClientInternalMatcher[ClientAMFPlugin, AMFShapePayloadValidationPlugin] {
    override def asInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin = toInternal(from)
  }

}

trait ClientLoggerConverter {
  type ClientLogger
  def asInternal(from: ClientLogger): Logger
}

object ALSConverters extends ALSClientConverter with ALSConverters
