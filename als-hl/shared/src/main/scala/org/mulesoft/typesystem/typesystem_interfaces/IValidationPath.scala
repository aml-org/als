package org.mulesoft.typesystem.typesystem_interfaces

trait IValidationPath {

    def name: Option[String]

    def index: Option[Int]

    def child: Option[IValidationPath]
}
