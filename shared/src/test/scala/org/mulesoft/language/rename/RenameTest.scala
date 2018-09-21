package org.mulesoft.language.rename

import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.language.common.dtoTypes.{IOpenedDocument, ITextEdit}
import org.mulesoft.language.test.LanguageServerTest
import org.mulesoft.language.test.dtoTypes.TextEdit
import org.scalatest.Assertion

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

abstract class RenameTest extends LanguageServerTest {

    def runTest(path:String, newName:String):Future[Assertion] = {
        init().flatMap(_=>{
            val resultPath = path.replace(".","-renamed.")
            var resolved = filePath(path)
            var resolvedResultPath = filePath(resultPath)
            var renamedContent:Option[String] = None
            var content:Option[String] = None
            Future.sequence(List(this.platform.resolve(resolved),this.platform.resolve(resolvedResultPath))).flatMap(contents => {

                val fileContentsStr = contents.head.stream.toString
                val renamedFileContentsStr = contents.last.stream.toString
                renamedContent = Option(renamedFileContentsStr.trim)
                val markerInfo = this.findMarker(fileContentsStr)
                content = Option(markerInfo.rawContent)
                val position = markerInfo.position
                getClient.flatMap(client=>{
                    val filePath = s"file:///$path"
                    client.documentOpened(IOpenedDocument(filePath,0,markerInfo.rawContent))
                    client.rename(filePath,position,newName).map(changedDocs=>{
                        client.documentClosed(filePath)
                        changedDocs
                    })
                })
            }).map(changedDocs=>{
                var edits:ListBuffer[ITextEdit] = ListBuffer()
                changedDocs.foreach(x => x.textEdits.map(edits ++= _))
                var newText = content.get
                edits.sortBy(x=>(-1) * x.range.start).foreach(te => {
                   newText = newText.substring(0, te.range.start) + te.text + newText.substring(te.range.end)
                })
                val result = renamedContent.contains(newText.trim)

                if (result) succeed
                else fail(s"Difference for $path: got [${ newText}] while expecting [${renamedContent.get}]")
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