package org.mulesoft.als.suggestions.plugins.oas

import amf.core.remote.{Oas, Vendor}

class ResponseReferencePlugin extends ReferencePlugin {

    override def id: String = ResponseReferencePlugin.ID

    def definitionClass:String = "ResponseDefinitionObject"

    def targetClass:String = "Response"

    def restrictedClasses:Seq[String] = List("ParameterDefinitionObject")

    def alwaysSequence:Boolean = false
}

object ResponseReferencePlugin {
    val ID = "response.reference.completion.plugin";

    def apply(): ResponseReferencePlugin = new ResponseReferencePlugin();
}
