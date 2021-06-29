package org.mulesoft.amfintegration.vocabularies

import amf.aml.client.scala.model.domain.{ClassTerm, ObjectPropertyTerm, PropertyTerm}

trait TermObjectNode {
  val name: String
  val displayName: Option[String] = None
  val description: String
}

trait PropertyTermObjectNode extends TermObjectNode {

  lazy val obj: PropertyTerm = ObjectPropertyTerm()
    .withName(name)
    .withDisplayName(displayName.getOrElse(name))
    .withDescription(description)
}

trait ClassTermObjectNode extends TermObjectNode {
  lazy val obj: ClassTerm = ClassTerm()
    .withName(name)
    .withDisplayName(name)
    .withDescription(description)
}
