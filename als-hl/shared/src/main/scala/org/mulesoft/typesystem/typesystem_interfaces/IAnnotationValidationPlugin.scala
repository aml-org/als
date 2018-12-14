package org.mulesoft.typesystem.typesystem_interfaces

trait IAnnotationValidationPlugin {
    def process(entry: IAnnotatedElement): Seq[PluginValidationIssue]

    def id: String
}
