package org.mulesoft.typesystem.project

class DependencyEntry[T](val path:String,val tc:T) {

    def isModule = false

    def isFragment = false
}

class FragmentDependencyEntry[T](path:String, tc:T) extends DependencyEntry[T](path,tc) {

    override def isFragment = true
}

class ModuleDependencyEntry[T] (
                                path:String,
                                tc:T,
                                val namespace:String,
                                val label:String) extends DependencyEntry[T](path,tc) {
    override def isModule = true
}
