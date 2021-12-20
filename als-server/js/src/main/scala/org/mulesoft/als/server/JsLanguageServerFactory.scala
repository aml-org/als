package org.mulesoft.als.server

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.platform.validation.payload.{AMFPayloadValidationPluginConverter, JsAMFPayloadValidationPlugin}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.CoreClientConverters.ClientList
import amf.core.internal.convert.PayloadValidationPluginConverter.PayloadValidationPluginMatcher.asInternal
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.configuration._
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidatorBuilder
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, JsCustomValidator}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.wasm.AmfWasmOpaValidator
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("JsLanguageServerFactory")
class JsLanguageServerFactory(override val clientNotifier: ClientNotifier) extends JsPlatformLanguageServerFactory {

  override def convertResourceLoaders(rl: Seq[ClientResourceLoader]): Seq[ResourceLoader] =
    rl.map(ResourceLoaderConverter.internalResourceLoader)

  override def convertLogger(logger: JsClientLogger): Logger =
    ClientLoggerAdapter(logger)

  override def convertDirectoryResolver(directoryResolver: ClientDirectoryResolver): DirectoryResolver =
    DirectoryResolverAdapter.convert(directoryResolver)

  override def convertPlugins(plugins: Seq[JsAMFPayloadValidationPlugin]): Seq[AMFShapePayloadValidationPlugin] =
    plugins
      .map(AMFPayloadValidationPluginConverter.toAMF)
      .map(asInternal)

  override def convertValidator(validator: AmfWasmOpaValidator): AMFOpaValidatorBuilder =
    (logger: Logger) => JsCustomValidator(logger, validator)
}

trait JsPlatformLanguageServerFactory
    extends ClientLanguageServerFactory[js.Any, JsClientLogger, JsAMFPayloadValidationPlugin, AmfWasmOpaValidator] {

  def withLogger(logger: js.UndefOr[JsClientLogger]): JsPlatformLanguageServerFactory =
    logger.map(withClientLogger).getOrElse(withLogger(PrintLnLogger))

  def withResourceLoaders(rls: ClientList[ClientResourceLoader]): this.type =
    withResourceLoaders(rls.toSeq)

  def withAmfPlugins(plugins: ClientList[JsAMFPayloadValidationPlugin]): this.type =
    withAmfPlugins(convertPlugins(plugins.toSeq))

}

@JSExportAll
@JSExportTopLevel("JsSerializationProps")
case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(prettyPrint: Boolean): DocBuilder[js.Any] =
    JsOutputBuilder() // TODO: JsOutputBuilder with prettyPrint
}
