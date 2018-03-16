package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{IAnnotation, IAnnotationType}

class Annotation(`type`: IAnnotationType, parameters: scala.collection.Map[String,Any]) extends Described(`type`.nameId.getOrElse("")) with IAnnotation {

    def parameterNames:Seq[String] = parameters.keys.to[collection.immutable.Seq]

    def parameter(name: String): Any = parameters.get(name)

    def getType:IAnnotationType = `type`
}
