package org.mulesoft.amfmanager

import amf.client.commands.CommandHelper
import amf.client.parse.DefaultParserErrorHandler
import amf.client.remote.Content
import amf.core.annotations.SourceVendor
import amf.core.client.ParserConfig
import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.parser.UnspecifiedReference
import amf.core.remote._
import amf.core.resolution.pipelines.ResolutionPipeline
import amf.core.services.{RuntimeCompiler, RuntimeResolver, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.core.{AMFCompilerRunCount, CompilerContextBuilder}
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.{ProfileName, ProfileNames}

import scala.concurrent.Future

class ParserHelper(val platform: Platform) extends CommandHelper {

  private def parseInput(url: String, env: Environment, plat: Option[Platform]): Future[BaseUnit] = {
    val eh        = new DefaultParserErrorHandler(AMFCompilerRunCount.nextRun())
    val inputFile = ensureUrl(url)

    RuntimeCompiler.forContext(
      new CompilerContextBuilder(inputFile, plat.getOrElse(platform), eh).withEnvironment(env).build(),
      None,
      None,
      UnspecifiedReference
    )
  }

  def parseResult(url: String): Future[ParseResult] = {
    for {
      unit   <- parse(url)
      report <- ParserHelper.report(unit)
    } yield ParseResult(unit, report)
  }

  def parse(url: String, env: Environment = Environment()): Future[BaseUnit] = {
    for {
      _     <- AmfInitializationHandler.init()
      model <- parseInput(url, env, None)
    } yield model
  }

  def indexDialect(url: String, content: Option[String]): Future[Unit] = {
    val env = content.fold(Environment())(c => {
      Environment().add(new ResourceLoader {

        /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
        override def fetch(resource: String): Future[Content] = Future(new Content(c, resource))

        /** Accepts specified resource. */
        override def accepts(resource: String): Boolean = resource == url
      })
    })

    for {
      _     <- AmfInitializationHandler.init()
      model <- AMLPlugin.registry.registerDialect(url, env)
    } yield { Unit }
  }

  def printModel(model: BaseUnit, config: ParserConfig): Future[Unit] = {
    generateOutput(config, model)
  }

}

object ParserHelper {
  def apply(platform: Platform) = new ParserHelper(platform)

  def report(model: BaseUnit): Future[AMFValidationReport] = RuntimeValidator(model, profile(model))

  def reportResolved(model: BaseUnit): Future[AMFValidationReport] =
    RuntimeValidator(model, profile(model), resolved = true)

  private def vendor(model: BaseUnit): Option[Vendor] = {
    val ann = model match {
      case d: EncodesModel => d.encodes.annotations.find(classOf[SourceVendor])
      case _               => model.annotations.find(classOf[SourceVendor])
    }
    ann.map(_.vendor)
  }

  private def profile(model: BaseUnit): ProfileName = {
    vendor(model) match {
      case Some(Raml10) => ProfileNames.RAML10
      case Some(Raml08) => ProfileNames.RAML08
      case Some(Raml)   => ProfileNames.RAML
      case Some(Oas20)  => ProfileNames.OAS20
      case Some(Oas30)  => ProfileNames.OAS30
      case Some(Oas)    => ProfileNames.OAS
      case Some(Aml)    => ProfileNames.AML
      case _            => ProfileNames.AMF
    }
  }

  def resolve(model: BaseUnit): BaseUnit = {
    RuntimeResolver.resolve(vendor(model).getOrElse(Amf).name,
                            model,
                            ResolutionPipeline.CACHE_PIPELINE,
                            UnhandledErrorHandler)
  }
}
