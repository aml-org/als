package org.mulesoft.amfintegration

import amf.core.internal.remote.Spec

case object Specs {
  val all: Seq[Spec] = Seq(Spec.RAML08,
                           Spec.RAML10,
                           Spec.OAS30,
                           Spec.OAS20,
                           Spec.ASYNC20,
                           Spec.AML,
                           Spec.AMF,
                           Spec.PAYLOAD,
                           Spec.JSONSCHEMA)
}
