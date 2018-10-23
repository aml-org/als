package org.mulesoft.language.server.server.modules.astManager

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Cache, Context, Platform}
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.internal.environment.Environment
import amf.plugins.features.validation.PlatformValidator
import org.mulesoft.language.server.common.utils.PathRefine

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Promise}

class ParserHelper(val platform:Platform) extends CommandHelper{

    protected def parseInput(config: ParserConfig, env:Environment): Future[BaseUnit] = {
        var inputFile   = ensureUrl(config.input.get)
        val inputFormat = config.inputFormat.get
        RuntimeCompiler(
            inputFile,
            Option(effectiveMediaType(config.inputMediaType, config.inputFormat)),
            config.inputFormat,
            Context(platform),
            UnspecifiedReference,
            Cache(),
            None,
            env
        )
    }
    override def ensureUrl(inputFile: String): String = ParserHelper.ensureUrl(inputFile, platform)
//
//    def parseResult(config: ParserConfig,env:Environment): Future[ParseResult] = {
//
//        val validatorInstance = PlatformValidator.instance;
//
//        for {
//            unit <- parse(config, env)
//            report <- report(unit,config)
//        } yield {
//            ParseResult(unit,report)
//        }
//    }

    def parse(config: ParserConfig,env:Environment): Future[BaseUnit] = {
        var promise = Promise[BaseUnit]();

        Future {
            AMFInit().andThen {
                case Success(initResult) => processDialects(config).andThen {
                    case Success(dialectsResult) => parseInput(config, env).andThen {
                        case Success(parseResult) => promiseSuccess(promise, parseResult, "parseInput");

                        case Failure(throwable) => promiseFailure(promise, throwable, "parseInput");
                    }

                    case Failure(throwable) => promiseFailure(promise, throwable, "processDialects");
                }
        
                case Failure(throwable) => promiseFailure(promise, throwable, "AMFInit eval");
            }
        } andThen {
            case Failure(throwable) => promiseFailure(promise, throwable, "AMFInit call");
        }
        
        promise.future;
    }
    
    def promiseSuccess[T](promise: Promise[T], success: T, operation: String): Unit = {
        //println(operation + " success");
        
        promise.success(success);
    }
    
    def promiseFailure[T](promise: Promise[T], throwable: Throwable, operation: String): Unit = {
        //println(operation + " failure");
    
        //throwable.printStackTrace();
        
        promise.failure(throwable);
    }

//    def report(model: BaseUnit, config: ParserConfig):Future[AMFValidationReport] = {
//        val customProfileLoaded = if (config.customProfile.isDefined) {
//            RuntimeValidator.loadValidationProfile(config.customProfile.get) map { profileName =>
//                profileName
//            }
//        } else {
//            Future {
//                config.profile
//            }
//        }
//        customProfileLoaded.flatMap(profile => {
//                val result = RuntimeValidator(model, profile)
//                //RuntimeValidator.reset()
//                result
//            }
//        )
//    }

    def printModel(model:BaseUnit, config: ParserConfig): Future[Unit] = {
        generateOutput(config,model)
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)

    def ensureUrl(inputFile: String, platform: Platform): String =
        PathRefine.refinePath(inputFile, platform)
}