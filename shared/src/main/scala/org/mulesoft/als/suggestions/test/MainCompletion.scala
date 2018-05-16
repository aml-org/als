package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.client.{ExitCodes, ParserConfig}
import amf.core.model.document.{BaseUnit, Document}
import amf.core.remote._
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.suggestions.{CompletionProvider, Core}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.implementation.{CompletionConfig, DummyASTProvider, DummyEditorStateProvider}
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MainCompletion extends PlatformSecrets{

    def main(args: Array[String]):Unit = {


        //val platform = TrunkPlatform(content)

        //var url = "file:///Users/munch/work/alsamf/als-suggestions/shared/src/test/resources/test/oas/test01.yaml"
        //var url = "file://C:\\GIT-repos\\raml-org\\examples\\!!!AMFTest\\Completion\\typeRef.raml".replace("\\","/")

        //var url = "file://C:/GIT-repos/raml-org/examples/!!!AMFTest/!!ramlTest.raml"
        //var url = "file://C:/GIT-repos/raml-org/examples/!!!AMFTest/!!oasTest.yml"
        var url = "file://C:/GIT-repos/raml-org/examples/!!!AMFTest/Completion/SwaggerObject.json"
        var cfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(url),
            //Some("RAML 1.0"),
            Some("OAS 2.0"),
            Some("application/yaml"),
            None,
            Some("AMF Graph"),
            Some("application/ld+json")
        )

        var position = -1

        var result = platform.resolve(url).map(c => {
            var ci = getPositions(c.stream.toString, "*", false);

            position = ci.position
            var loaders:Seq[ResourceLoader] = List(new ResourceLoader {override def accepts(resource: String): Boolean = resource == url

                override def fetch(resource: String): Future[Content] = Future.successful(new Content(ci.content,url))
            })
            loaders ++= platform.loaders()
            var env:Environment = Environment(loaders)
            env
        }).flatMap(env=>{
            val helper = ParserHelper(platform)
            helper.parse(cfg,env)
        }).flatMap(x=>{
            operate(x)

        }).map(x=>{
            val rootNode = x.rootASTUnit.rootNode
            var astProvider = new DummyASTProvider(x,position)
            val baseName = url.substring(url.lastIndexOf('/') + 1)
            var editorStateProvider = new DummyEditorStateProvider(x.rootASTUnit.text,url,baseName,position)
            var cfg = new CompletionConfig().withAstProvider(astProvider).withEditorStateProvider(editorStateProvider)
            CompletionProvider().withConfig(cfg)
        }).flatMap(_.suggest).map(suggestions=>{
            println(suggestions.mkString("\n"))
            println("\n\n-- done --")
        })
        Await.result(result,1 day)
        System.exit(ExitCodes.Success)
    }

    def operate(model:BaseUnit):Future[IProject] = {
        Core.init().flatMap(_=>org.mulesoft.high.level.Core.buildModel(model,platform))
    }

    def getPositions(str:String,label:String="*", cut: Boolean = true):CompletionInput = {
        var position = str.indexOf(label);

        if(position<0){
            new CompletionInput(str,str.length)
        }
        else {
            var rawContent = str.substring(0, position) + str.substring(position + 1)
            var preparedContent = Core.prepareText(rawContent, position, YAML)
            new CompletionInput(preparedContent, position)
        }
//        if(position<0){
//            position = str.length;
//        }
//
//        if(!cut) {
//            var content = str.substring(0, position);
//
//            if(position < str.length - 1) {
//                content += str.substring(position + 1);
//            }
//
//            new CompletionInput(content, position)
//        } else {
//            var content = str.substring(0, position);
//
//            var ind = str.indexOf("\n", position);
//
//            if (ind > 0) {
//                if (str.charAt(ind - 1) == '\r') {
//                    ind -= 1
//                }
//
//                var ind1 = str.lastIndexOf('\n', position)
//
//                if(ind1 < 0) {
//                    ind1 = 0
//                }
//
//                val line = str.substring(ind1, position).trim
//
//                if(line.indexOf(':') < 0) {
//                    if(line.trim.nonEmpty && line.charAt(0) != '-') {
//                        content += ":"
//                    }
//                }
//
//                content += str.substring(ind)
//            }
//
//            new CompletionInput(content, position)
//        }
    }
}

class CompletionInput(val content:String, val position:Int) {}
