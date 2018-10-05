package org.mulesoft.typesystem.nominal_interfaces.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class DescriptionExtra extends Extra[DescriptionExtra] {

    private var _text:String = ""

    def text:String = _text

    protected def withText(txt:String):DescriptionExtra = {
        _text = txt
        this
    }

    override def name: String = "Description"

    override def clazz: Class[DescriptionExtra] = classOf[DescriptionExtra]

    override def default: Option[DescriptionExtra] = None
}

object DescriptionExtra extends DescriptionExtra {
    def apply(_text: String): DescriptionExtra = new DescriptionExtra().withText(_text)
}


