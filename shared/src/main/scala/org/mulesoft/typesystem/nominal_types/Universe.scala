package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUniverse}

import scala.collection.mutable.ListBuffer

class Universe(_name:String,_parent:Option[IUniverse] = None,_uversion:String) extends IUniverse {


    private var _classes: ListBuffer[AbstractType] = ListBuffer[AbstractType]()

    private var aMap:scala.collection.mutable.Map[String,AbstractType] = scala.collection.mutable.Map[String,AbstractType]()

    def version:String = _uversion

    def name:Option[String] = Option(_name)

    def types: Seq[ITypeDefinition] = {
        var result = ListBuffer[ITypeDefinition]() ++= _classes
        if (this._parent.isDefined) {
            result ++= _parent.get.types
        }
        result
    }

    def ownTypes: Seq[AbstractType] = ListBuffer[AbstractType]() ++= _classes

    def `type`(name: String): Option[ITypeDefinition] = {
        if (this.aMap.contains(name)) {
            this.aMap.get(name)
        }
        else {
            var tp:Option[ITypeDefinition]
                = _classes.find(x => x.nameId.isDefined && x.nameId.get == name)
            if (tp.isEmpty && _parent.isDefined){
                tp = _parent.get.`type`(name)
            }
            tp
        }
    }

    def ownType(name: String): Option[AbstractType] = {
        if (this.aMap.contains(name)) {
            this.aMap.get(name)
        }
        else {
            _classes.find(x => x.nameId.isDefined && x.nameId.get == name)
        }
    }

    def parent:Option[IUniverse] = _parent

    def register(t:AbstractType):Unit = _classes += t

    def registerAlias(a: String, t: AbstractType):Unit = aMap.put(a,t)

}