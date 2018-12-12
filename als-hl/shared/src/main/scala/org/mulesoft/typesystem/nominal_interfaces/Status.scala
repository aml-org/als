package org.mulesoft.typesystem.nominal_interfaces

trait Status {
    def getValidationPathAsString: String

    def getSeverity: Int

    def isError: Boolean

    def getMessage: String
}
