package org.mulesoft.typesystem.typesystem_interfaces

trait HasSource {
    def sourceMap: Option[ElementSourceInfo]
}
