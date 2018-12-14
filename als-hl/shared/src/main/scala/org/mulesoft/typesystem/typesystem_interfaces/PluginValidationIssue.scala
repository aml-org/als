package org.mulesoft.typesystem.typesystem_interfaces

trait PluginValidationIssue {
    var issueCode: String
    var message: String
    var isWarning: Boolean
    var path: IValidationPath
}
