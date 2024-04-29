package org.mulesoft.language.outline.test.aml

import amf.core.internal.remote.{Aml, Spec}

trait DialectTest {

  def rootPath: String = "AML"

  def format: String = Spec.AML.toString
}
