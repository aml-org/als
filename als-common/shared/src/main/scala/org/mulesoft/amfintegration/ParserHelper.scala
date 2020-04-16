package org.mulesoft.amfmanager

import amf.client.commands.CommandHelper
import amf.client.parse.DefaultParserErrorHandler
import amf.client.remote.Content
import amf.core.annotations.SourceVendor
import amf.core.client.ParserConfig
import amf.core.emitter.RenderOptions
import amf.core.errorhandling.{ErrorCollector, UnhandledErrorHandler}
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.parser.UnspecifiedReference
import amf.core.remote._
import amf.core.resolution.pipelines.ResolutionPipeline
import amf.core.services.{RuntimeCompiler, RuntimeResolver, RuntimeValidator}
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.{AMFSerializer, CompilerContextBuilder}
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectLibrary}
import amf.{ProfileName, ProfileNames}
import org.mulesoft.als.ModelBuilder
import org.mulesoft.amfmanager.BaseUnitImplicits._
import org.yaml.builder.DocBuilder

import scala.concurrent.{ExecutionContext, Future}

class AmfParseResult(val baseUnit: BaseUnit, val eh: ErrorCollector) {

  val location: String = baseUnit.location().getOrElse(baseUnit.id)

  def groupedErrors: Map[String, List[AMFValidationResult]] =
    eh.getErrors.groupBy(e => e.location.getOrElse(location))

  lazy val tree: Set[String] = baseUnit.flatRefs
    .map(bu => bu.location().getOrElse(bu.id))
    .toSet + baseUnit.location().getOrElse(baseUnit.id)
}

class ParserHelper(val platform: Platform, amfInit: Future[Unit])
    extends CommandHelper
    with ModelBuilder[AmfParseResult, Environment] {

  private def parseInput(url: String, env: Environment, plat: Option[Platform]): Future[AmfParseResult] = {
    val eh        = DefaultParserErrorHandler()
    val inputFile = ensureUrl(url)

    RuntimeCompiler
      .forContext(
        new CompilerContextBuilder(inputFile, plat.getOrElse(platform), eh)
          .withEnvironment(env)
          .build(),
        None,
        None,
        UnspecifiedReference
      )
      .map(m => new AmfParseResult(m, eh))
  }

  def parse(url: String, env: Environment): Future[AmfParseResult] = {
    for {
      _     <- amfInit
      model <- parseInput(url, env, None)
    } yield model
  }

  def indexDialect(url: String, content: Option[String]): Future[Unit] = {
    val env = content.fold(Environment())(c => {
      Environment().add(new ResourceLoader {

        /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
        override def fetch(resource: String): Future[Content] =
          Future(new Content(c, resource))

        /** Accepts specified resource. */
        override def accepts(resource: String): Boolean = resource == url
      })
    })

    for {
      _     <- amfInit
      model <- AMLPlugin.registry.registerDialect(url, env)
    } yield { Unit }
  }

  def editingResolve(model: BaseUnit, eh: ErrorCollector): BaseUnit = {
    RuntimeResolver.resolve(ParserHelper.vendor(model).map(_.name).getOrElse(Amf.name),
                            model,
                            ResolutionPipeline.EDITING_PIPELINE,
                            eh)
  }

  def compatibilityResolve(model: BaseUnit, target: String): BaseUnit = {
    RuntimeResolver.resolve(target, model, ResolutionPipeline.COMPATIBILITY_PIPELINE)
  }

  private def syntaxFor(profile: ProfileName) = {
    profile match {
      case ProfileNames.OAS20 => Mimes.`APPLICATION/JSON`
      case _                  => Mimes.`APPLICATION/YAML`
    }
  }

  private def mediaType(syntax: String) = {
    if (syntax.toUpperCase.contains("JSON")) Mimes.`APPLICATION/JSON`
    else Mimes.`APPLICATION/YAML`
  }

  def convertTo(model: BaseUnit, target: String, syntax: Option[String]): Future[String] = {
    val unit = compatibilityResolve(model, target)
    val name = ProfileName(target)
    new AMFSerializer(unit, syntax.map(mediaType).getOrElse(syntaxFor(name)), target, RenderOptions())
      .renderToString(ExecutionContext.Implicits.global)
  }

  def printModel(model: BaseUnit, config: ParserConfig): Future[Unit] = {
    generateOutput(config, model)
  }

  override def parse(uri: String): Future[AmfParseResult] =
    parse(uri, Environment())
}

object ParserHelper {
  def toJsonLD(resolved: BaseUnit, builder: DocBuilder[_]): Future[Unit] = {
    new AMFSerializer(resolved,
                      Mimes.`APPLICATION/LD+JSONLD`,
                      Amf.name,
                      RenderOptions().withCompactUris.withoutSourceMaps)
      .renderToBuilder(builder)(ExecutionContext.Implicits.global)
  }
  def report(model: BaseUnit): Future[AMFValidationReport] =
    RuntimeValidator(model, profile(model))

  def reportResolved(model: BaseUnit): Future[AMFValidationReport] =
    RuntimeValidator(model, profile(model), resolved = true)

  def vendor(model: BaseUnit): Option[Vendor] = {
    val ann = model match {
      case _ @(_: Dialect | _: DialectLibrary) => Some(SourceVendor(Aml))
      case d: EncodesModel                     => d.encodes.annotations.find(classOf[SourceVendor])
      case _                                   => model.annotations.find(classOf[SourceVendor])
    }
    ann.map(_.vendor)
  }

  def profile(model: BaseUnit): ProfileName = {
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
