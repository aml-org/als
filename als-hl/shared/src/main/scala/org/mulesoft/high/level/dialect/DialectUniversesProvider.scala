package org.mulesoft.high.level.dialect

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse

import scala.collection.mutable.Map

object DialectUniversesProvider {

    private val map: Map[String,Map[String,IDialectUniverse]] = Map()

    def getUniverse(d:Dialect):IDialectUniverse = {

        val name = d.name().value()
        val version = d.version().value()

        var m = map.get(name)
        if(m.isEmpty){
            m = Some(Map[String,IDialectUniverse]())
            map.put(name,m.get)
        }
        var resultOpt = m.get.get(version)
        if(resultOpt.isEmpty){
            val u = DialectUniverseBuilder.buildUniverse(d)
            m.get.put(version,u)
            resultOpt = Some(u)
        }
        resultOpt.get
    }
}
