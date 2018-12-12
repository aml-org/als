package org.mulesoft.typesystem.typesystem_interfaces

trait IStatus extends IHasExtra {
    def isOk: Boolean

    def isWarning: Boolean

    def isError: Boolean

    def isInfo: Boolean

    def getMessage: String

    def setMessage(m: String): Unit

    def getSubStatuses: Seq[IStatus]

    def getSource: Any

    def getErrors: Seq[IStatus]

    def getValidationPath: Option[IValidationPath]

    def setValidationPath(p: IValidationPath): Unit

    def getValidationPathAsString: String

    def getCode: String

    def setCode(c: String): Unit

    def getSeverity: Int

    def getInternalRange: RangeObject

    def getInternalPath: IValidationPath

    def getFilePath: String
}
