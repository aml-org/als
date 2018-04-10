package org.mulesoft.als.suggestions.plugins.oas

import amf.core.remote.{Oas, Vendor}

class ResponseReferencePlugin extends ReferencePlugin {

    override def id: String = ResponseReferencePlugin.ID

    def definitionClass:String = "ResponseDefinitionObject"

    def targetClass:String = "ResponseObject"
}

object ResponseReferencePlugin {
    val ID = "response.reference.completion.plugin";

    def apply(): ResponseReferencePlugin = new ResponseReferencePlugin();
}
