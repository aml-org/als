package org.mulesoft.language.suggestions

import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.language.common.dtoTypes.IOpenedDocument
import org.mulesoft.language.test.LanguageServerTest
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class SuggestionsTest extends LanguageServerTest {

    def runTest(path:String, expectedSuggestions:Set[String]):Future[Assertion] = {
        init().flatMap(_=>{
            var resolved = filePath(path)
            this.platform.resolve(resolved).flatMap(content => {

                val fileContentsStr = content.stream.toString
                val markerInfo = this.findMarker(fileContentsStr)
                val position = markerInfo.position
                getClient.flatMap(client=>{
                    val filePath = s"file:///$path"
                    client.documentOpened(IOpenedDocument(filePath,0,markerInfo.rawContent))
                    client.getSuggestions(filePath,position).map(suggestions=>{
                        client.documentClosed(filePath)
                        suggestions
                    })
                })
            }).map(suggestions=>{
                val resultSet = suggestions.map(_.text).toSet
                val diff1 = resultSet.diff(expectedSuggestions)
                val diff2 = expectedSuggestions.diff(resultSet)

                if (diff1.isEmpty && diff2.isEmpty) succeed
                else fail(s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]")
                succeed
            })
        })
    }
    def findMarker(str:String,label:String="*", cut: Boolean = true): MarkerInfo = {

        var position = str.indexOf(label);

        if(position<0){
            new MarkerInfo(str,str.length, str)
        }
        else {
            var rawContent = str.substring(0, position) + str.substring(position + 1)
            var preparedContent =
                org.mulesoft.als.suggestions.Core.prepareText(rawContent, position, YAML)
            new MarkerInfo(preparedContent, position, rawContent)
        }

    }
}

class MarkerInfo(val content:String, val position:Int, val rawContent:String) {}