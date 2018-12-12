package org.mulesoft.typesystem.typesystem_interfaces

trait IHasExtra {
    def getExtra[T](kind:Extra[T]): Option[T]

    def putExtra[T](kind:Extra[T],value:T): Unit

    def putExtra[T](kind:Extra[T]): Unit = putExtra(kind,kind.default.get)
}

trait Extra[T] {

    def name: String

    def clazz:Class[T]

    def default:Option[T]
}