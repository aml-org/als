package org.mulesoft.typesystem.syaml.to.json

class YJSONProperty(private val _name:String, private val _value:YJSONWrapper) extends org.mulesoft.typesystem.json.interfaces.JSONProperty {

    override def name:String = _name

    override def value:YJSONWrapper = _value
}

object YJSONProperty {
    def apply(name:String,value:YJSONWrapper):YJSONProperty = new YJSONProperty(name,value)
}

