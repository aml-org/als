package org.mulesoft.als.suggestions.plugins.oas

class ParameterReferencePlugin extends ReferencePlugin {

    override def id: String = ParameterReferencePlugin.ID

    def definitionClass:String = "ParameterDefinitionObject"

    def targetClass:String = "ParameterObject"
}

object ParameterReferencePlugin {
    val ID = "parameter.reference.completion.plugin";

    def apply(): ParameterReferencePlugin = new ParameterReferencePlugin();
}


