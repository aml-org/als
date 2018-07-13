package org.mulesoft.language.server.server.modules.editorManager

import org.mulesoft.language.common.logger.ILogger
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint, IRange}

import scala.collection.mutable.ArrayBuffer


class TextBufferInfo (uri: String, logger: ILogger) extends IEditorTextBuffer {

  var text: String = ""

  var lineLengths: ArrayBuffer[Int] = ArrayBuffer()

  def characterIndexForPosition(position: IPoint): Int = {
    var lineStartOffset = 0

    for {i <- 0 until position.row} {
      lineStartOffset += this.lineLengths(i);
    }

    val result = lineStartOffset + position.column

    this.logger.debugDetail(
      "characterIndexForPosition:" + ": [" + position.row + ":" + position.column + "] = " + result,
      "EditorManager", "TextBufferInfo#characterIndexForPosition")

    result
  }

  def positionForCharacterIndex(offset: Int): IPoint = {
    var pos = offset

    var found: Option[IPoint] = None

    for {i <- 0 to this.lineLengths.length if found.isEmpty} {

      val lineLength = this.lineLengths(i);

      if (pos < lineLength) {

        this.logger.debugDetail(
          "positionForCharacterIndex:" + offset +
            ": [" + i + ":" + pos + "]",
          "EditorManager", "TextBufferInfo#positionForCharacterIndex")

        found = Option(IPoint (
          i,
          pos
        ))
      }

      pos -= lineLength;
    }

    if (found.isDefined) {

      found.get
    } else {

      if (pos == 0) {

        val resultRow = this.lineLengths.length - 1
        val resultColumn = this.lineLengths(this.lineLengths.length-1)

        this.logger.debugDetail("positionForCharacterIndex:" + offset + ": [" + resultRow + ":" + resultColumn + "]",
          "EditorManager", "TextBufferInfo#positionForCharacterIndex")

        IPoint (
          resultRow,
          resultColumn
        )
      } else {

        val errorMessage = s"""Character position exceeds text length: ${ offset} > + ${ this.text.length}"""
        this.logger.error( errorMessage, "EditorManager", "TextBufferInfo#positionForCharacterIndex" )
        throw new Error( errorMessage )
      }
    }
  }


  def rangeForRow(rowParam: Int, includeNewline: Boolean): IRange = {

    this.logger.debugDetail("rangeForRow start:" + rowParam,
      "EditorManager", "TextBufferInfo#rangeForRow" )

    var lineStartOffset = 0

    for {i <- 0 until rowParam} {
      lineStartOffset += this.lineLengths(i);
    }

    val lineLength = this.lineLengths(rowParam)

    val startPoint: IPoint = IPoint (
      rowParam,
      0
    )

    val endPoint = IPoint (
      rowParam,
      lineLength
    )

    this.logger.debugDetail("rangeForRow return:" + rowParam + ": [" + startPoint.row + ":" + startPoint.column + "]" +
      ",[" + endPoint.row + ":" + endPoint.column + "]",
      "EditorManager", "TextBufferInfo#rangeForRow" )

    new IRange {
      var start: IPoint = startPoint
      var end: IPoint = endPoint
    }

  }

  def getTextInRange(range: IRange): String = {

    val startOffset = this.characterIndexForPosition(range.start)
    val endOffset = this.characterIndexForPosition( range.end )

    val result = this.text.substring( startOffset, endOffset )

    this.logger.debugDetail(
      "Text in range: [" + range.start.row + ":" + range.start.column + "]" +
        ",[" + range.end.row + ":" + range.end.column + "]:\n" + result,
      "EditorManager", "TextBufferInfo#getTextInRange" )

    result
  }

  def setTextInRange(range: IRange, text: String, normalizeLineEndings: Boolean = false): Unit = {

    this.logger.debug(
      "Setting text in range: [" + range.start.row + ":" + range.start.column + "] ," +
        "[" + range.end.row + ":" + range.end.column + "]\n" + text,
      "EditorManager", "TextBufferInfo#setTextInRange"
    )

    val startOffset = this.characterIndexForPosition(range.start)

    val endOffset = this.characterIndexForPosition(range.end)

    this.logger.debugDetail(
      "Found range in absolute coords: [" + startOffset + ":" + endOffset + "]",
      "EditorManager", "TextBufferInfo#setTextInRange"
    )

    val startText = if (startOffset > 0) this.text.substring( 0, startOffset) else ""

    val endText = if (endOffset < this.text.length) this.text.substring(endOffset) else ""

    this.logger.debugDetail("Start text is:\n" + startText, "EditorManager", "TextBufferInfo#setTextInRange")
    this.logger.debugDetail("End text is:\n" + endText, "EditorManager", "TextBufferInfo#setTextInRange")

    this.setText(startText + text + endText)

    this.logger.debugDetail("Final text is:\n" + this.text, "EditorManager", "TextBufferInfo#setTextInRange")

  }

  def getText(): String = {

    this.text
  }

  def getEndPosition(): IPoint = {

    this.positionForCharacterIndex(this.text.length-1)
  }

  def setText(text: String): Unit = {

    this.text = text
    this.initMapping()

//    if ((this.editorManager&&this.editorManager.getDocumentChangeExecutor())) {
//     this.editorManager.getDocumentChangeExecutor().changeDocument( Map( "uri" -> this.uri,
//    "text" -> this.text ) )
//
//    }
//    else {
//     this.logger.error( "Can not report document change to the client as there is no executor", "EditorManager", "TextBufferInfo#setText" )
//
//    }
  }

  def initMapping() : Unit = {

    this.lineLengths = ArrayBuffer[Int]()

    var ind = 0
    val l = this.text.length

    var ignoreNext = false

    for {i <- 0 until l} {

      if (ignoreNext) {
        ignoreNext = false
      } else {

        ignoreNext = false

        if (this.text.charAt(i) == '\r') {
          if (i < l - 1 && this.text.charAt(i + 1) == '\n') {

            this.lineLengths += (i - ind + 2);

            ind = i + 2;

            ignoreNext = true
          } else {

            this.lineLengths += (i - ind + 1);
            ind = i + 1;
          }
        } else if (this.text.charAt(i) == '\n') {

          this.lineLengths += (i - ind + 1)
          ind = i + 1;
        }
      }

    }

    this.lineLengths += (l - ind);
  }
}
