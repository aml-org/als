package org.mulesoft.typesystem.nominal_interfaces

class IPrintDetailsSettings(var hideProperties: Boolean,
var hideSuperTypeProperties: Boolean,
var printStandardSuperclasses: Boolean)
{

}

object IPrintDetailsSettings {

    def apply(a1: Boolean,a2: Boolean,a3: Boolean):IPrintDetailsSettings = new IPrintDetailsSettings(a1,a2,a3)

    def apply(a1: Boolean,a2: Boolean):IPrintDetailsSettings = apply(a1,a2,false)

    def apply(a1: Boolean):IPrintDetailsSettings = apply(a1,false)

    def apply():IPrintDetailsSettings = apply(false)
}
