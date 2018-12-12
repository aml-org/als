package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.IAnnotation
import scala.collection.mutable.ListBuffer

class Described(var _name: String, var _description: String = "") extends Adaptable {

    def nameId: Option[String] = Option(_name)

    def description: String = _description

    var _tags: ListBuffer[String] = ListBuffer[String]()

    var _version: String = _

    var _annotations: ListBuffer[IAnnotation] = ListBuffer[IAnnotation]()

    def addAnnotation(a: IAnnotation):Unit = _annotations += a

    def removeAnnotation(a: IAnnotation):Unit = _annotations = _annotations.filter(_!=a)

    def annotations():Seq[IAnnotation] = _annotations

    def tags:Seq[String] = _tags

    def withDescription(d: String):Described = {
        _description = d
        this
    }

    def setName(name: String):Unit = _name = name
}
