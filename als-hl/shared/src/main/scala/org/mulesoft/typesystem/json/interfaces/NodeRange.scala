package org.mulesoft.typesystem.json.interfaces

trait NodeRange {

    def start: Point

    def end: Point

    def containsPosition(pos: Int): Boolean = start.position <= pos && end.position > pos
}
