package org.mulesoft.test

import amf.core.client.{ExitCodes, ParserConfig}
import amf.core.metamodel.domain.ShapeModel
import amf.core.model.document.{BaseUnit, Document}
import amf.core.model.domain.AmfScalar
import amf.core.unsafe.PlatformSecrets
import org.mulesoft.high.level.builder.{NodeBuilder, ProjectBuilder}
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IProject}
import org.mulesoft.high.level.{Core, Search}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends PlatformSecrets{

    def main(args: Array[String]):Unit = {


        //val platform = TrunkPlatform(content)

        var cfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            //Some("https://rawgit.com/KonstantinSviridov/stuff/master/raml/fragments/typeFragment.raml"),//Some("api.raml"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/template-refs.raml"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/typesInDifferentPlaces.raml"),
            //Some("file://C:/GIT-repos/raml-tck/tests/oas/general/annotation_Ov2-0.json"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/templates.raml"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/!!ramlTest.raml"),
            //Some("file://C:/GIT-repos/AMF/als-hl/shared/src/test/resources/OAS20/find/references/parameters/test001/spec.yml"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/!!oasTest.yml"),
            //Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/Completion/SwaggerObject.json"),
            Some("C:\\GIT-repos\\als\\als-outline\\shared\\src\\test\\resources\\OAS20\\structure\\test006\\api.yml".replace("\\","/")),
            //Some("OAS 2.0"),
            Some("RAML 1.0"),
            Some("application/yaml"),
            None,
            Some("RAML 1.0"),
            Some("application/yaml")
        )

//        var cfg2 = new ParserConfig(
//            Some(ParserConfig.PARSE),
//            Some("file://C:/GIT-repos/raml-org/examples/!!!AMFTest/!!ramlTest.raml"),
//            Some("OAS 2.0"),
//            Some("application/yaml"),
//            None,
//            Some("AMF Graph"),
//            Some("application/ld+json"),
//            true
//        )

        val helper = ParserHelper(platform)
        var result = helper.parse(cfg).flatMap(x=>{
            //helper.printModel(x,cfg)
//            x.asInstanceOf[Document].declares.find(x=>{
//                x.isInstanceOf[Parameter] && x.asInstanceOf[Parameter].name == "skipParam"
//            }).foreach(p=>{
//                val paramSchema = p.asInstanceOf[Parameter].schema.asInstanceOf[ScalarShape]
//                try {
//                    val minimum = paramSchema.minimum
//                    println(s"minimum1: $minimum, type: ${minimum.getClass.getCanonicalName}")
//                }
//                catch {
//                    case t:Throwable => println("Exception: " + t)
//                    case _ =>
//                }
//                val minimum = paramSchema.fields.getValue(ScalarShapeModel.Minimum).value.asInstanceOf[AmfScalar].value
//                println(s"minimum2: $minimum, type: ${minimum.getClass.getCanonicalName}")
//            })
            operate(x)
        }).map(x=>{
            val rootNode = x.rootASTUnit.rootNode
            println(x.rootASTUnit.rootNode.printDetails)
        })
        Await.result(result,1 day)
        System.exit(ExitCodes.Success)
    }

    def operate(model:BaseUnit):Future[IProject] = {
        Core.init().flatMap(_=>Core.buildModel(model,platform))
    }

//    class ExprParser extends RegexParsers {
//        val ident = "[^/{}]+".r
//        val condition = "\{[^/{}]\}".r
//        def segment: Parser[Any] = ident | ident ~ condition
//        def path: Parser[Any] = segment ~ opt("/" ~ path)
////        val number = "[0-9]+".r
////        def expr: Parser[Any] = term ~ opt(("+" | "-") ~ expr)
////        def term: Parser[Any] = factor ~ rep("*" ~ factor)
////        def factor: Parser[Any] = number | "(" ~ expr ~ ")"
//    }

}
