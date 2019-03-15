package org.mulesoft.language.outline.test.aml

import amf.core.remote.Aml

trait DialectTest {

    def rootPath:String = "AML"

    def format:String = Aml.toString
}
