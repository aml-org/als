package org.mulesoft.typesystem.typesystem_interfaces

trait ElementSourceInfo extends SourceInfo {
    var scalarsSources: scala.collection.Map[String, Seq[SourceInfo]]
}
