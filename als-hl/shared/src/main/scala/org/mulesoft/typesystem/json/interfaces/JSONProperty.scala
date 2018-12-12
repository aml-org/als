package org.mulesoft.typesystem.json.interfaces
// $COVERAGE-OFF$
trait JSONProperty {

    def name: String

    def value: JSONWrapper

    def valueKind: JSONWrapperKind[_ <: Any] = value.kind
}
// $COVERAGE-ON$