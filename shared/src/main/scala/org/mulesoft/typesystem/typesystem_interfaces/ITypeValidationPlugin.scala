package org.mulesoft.typesystem.typesystem_interfaces

trait ITypeValidationPlugin {
    def process(t: IParsedType, reg: ITypeRegistry): Seq[PluginValidationIssue]

    def id: String
}
