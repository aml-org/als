package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.typesystem_interfaces.{Extra, IHasExtra}

import scala.collection.mutable

trait HasExtra extends IHasExtra{

    var _extras: mutable.Map[String,Any] = mutable.Map()


    def putExtra[T](kind:Extra[T],value:T): Unit = _extras(kind.name) = value

    def getExtra[T](kind:Extra[T]): Option[T] = {
        _extras.get(kind.name).flatMap(x=>{
            if(kind.clazz.isInstance(x)){
                Some(x.asInstanceOf[T])
            }
            else None
        })
    }

}
