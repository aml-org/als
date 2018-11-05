package org.mulesoft.typesystem.dialects

import org.mulesoft.typesystem.nominal_interfaces.{IDialectUniverse, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.Universe
import org.mulesoft.typesystem.project.ITypeCollection

import scala.collection.mutable.Map

class DialectUniverse(_name:String,_parent:Option[IUniverse] = None,_uversion:String) extends Universe(_name, _parent, _uversion) with IDialectUniverse {

    private var _root:Option[ITypeDefinition] = None

    private var _library:Option[ITypeDefinition] = None

    private var _frgaments:Map[String,ITypeDefinition] = Map()

    override def root: Option[ITypeDefinition] = _root

    override def library: Option[ITypeDefinition] = _library

    override def fragments: collection.Map[String, ITypeDefinition] = _frgaments.toMap

    def withRoot(t:ITypeDefinition):DialectUniverse = {
        this._root = Option(t)
        this
    }

    def withLibrary(t:ITypeDefinition):DialectUniverse = {
        this._library = Option(t)
        this
    }

    def withFragment(t:ITypeDefinition):DialectUniverse = {
        Option(t).foreach(x=>_frgaments.put(x.nameId.get,x))
        this
    }
}
