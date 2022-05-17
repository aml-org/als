package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.Position

trait MarkerFinderTest {
  def findMarker(str: String, label: String = "[*]", cut: Boolean = true): MarkerInfo = {
    findMarkers(str, label, cut).headOption
      .getOrElse(MarkerInfo(str, str, Position(str.length, str), 0))
  }

  def findMarkers(str: String, label: String = "[*]", cut: Boolean = true): Seq[MarkerInfo] = {
    var markers    = Seq[Int]()
    var offset     = 0
    var rawContent = str

    do {
      offset = rawContent.indexOf(label, offset)
      if (offset >= 0) {
        markers = offset +: markers
        // I have no idea why we wouldn't want to cut
        if (cut) rawContent = rawContent.substring(0, offset) + rawContent.substring(offset + label.length)
      }
    } while (offset >= 0)

    markers.reverse.map(off => {
      MarkerInfo(rawContent, str, Position(off, rawContent), off)
    })
  }
}

case class MarkerInfo(content: String, originalContent: String, position: Position, offset: Int)
