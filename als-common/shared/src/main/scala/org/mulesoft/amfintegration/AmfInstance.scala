package org.mulesoft.amfintegration

import amf.client.convert.CoreRegister
import amf.client.plugins.AMFPlugin
import amf.client.remod.AMFGraphConfiguration
import amf.core.AMF
import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.BaseUnit
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.WebApi
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.domain.VocabulariesRegister
import amf.plugins.features.AMFValidation
import org.mulesoft.als.CompilerEnvironment
import org.mulesoft.amfintegration.vocabularies._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// todo: move to another module
class AmfInstance(plugins: Seq[AMFPlugin], platform: Platform, environment: Environment)
    extends CompilerEnvironment[BaseUnit, ErrorCollector, Dialect, Environment] {

  def parse(uri: String): Future[AmfParseResult] =
    modelBuilder().parse(uri, environment)

  val alsAmlPlugin: ALSAMLPlugin = ALSAMLPlugin()

  private var initialization: Option[Future[Unit]] = None
  // TODO: create a configuration based on profiles registered, dialects and resource loader(environment)
  val amfConfiguration = AMFPluginsRegistry.obtainStaticConfig()

  override def init(profile: InitOptions = InitOptions.AllProfiles): Future[Unit] =
    synchronized {
      initialization match {
        case Some(f) => f
        case _ =>
          amf.Core.registerPlugin(AlsSyamlSyntaxPluginHacked)
          WebApi.register(platform.defaultExecutionEnvironment)
          if (AMFPluginsRegistry.documentPluginForID(alsAmlPlugin.ID).isDefined) // hack for static amf registry
            AMFPluginsRegistry.unregisterDocumentPlugin(alsAmlPlugin)
          amf.Core.registerPlugin(alsAmlPlugin) // todo: two servers in same jvm would break this
          VocabulariesRegister.register(platform)
          AMFValidation.register()
          amf.Core.registerPlugin(PayloadValidatorPlugin)
          CoreRegister.register(platform)
          plugins.foreach(amf.core.AMF.registerPlugin)
          WebApi.register()
          alsAmlPlugin.vocabularyRegistry.index(AlsDeclarationKeysVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(SchemaOrgVocabulary()) // parametrize voc initializations
          alsAmlPlugin.vocabularyRegistry.index(ShaclVocabulary())     // parametrize voc initializations
          alsAmlPlugin.vocabularyRegistry.index(AmlApiContractVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(AmlDataModelVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(AmlDataShapesVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(AmlCoreVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(AmlDocumentVocabulary())
          alsAmlPlugin.vocabularyRegistry.index(AlsPatchedVocabulary())
          val f = AMF.init().andThen {
            case _ =>
              profile.vendors.foreach { v =>
                alsAmlPlugin.registerWebApiDialect(v)
              }
          }
          initialization = Some(f)
          f
      }
    }

  override def modelBuilder(): ParserHelper = new ParserHelper(platform, this)
}

object AmfInstance extends PlatformSecrets {
  def apply(environment: Environment): AmfInstance =
    apply(platform, environment)
  def apply(platform: Platform, environment: Environment): AmfInstance =
    new AmfInstance(Nil, platform, environment)
  def default: AmfInstance = new AmfInstance(Nil, platform, Environment())
}
