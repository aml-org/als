package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition}

class Property(_name: String) extends Described(_name) with IProperty  with HasExtra{

    var _ownerClass: Option[StructuredType] = None
    var _nodeRange: Option[ITypeDefinition] = None
    var _groupName: Option[String] = None
    var _keyShouldStartFrom: Option[String] = None
    var _enumOptions: Option[Seq[String]] = None
    var _isRequired: Boolean = false
    var _isMultiValue: Boolean = false
    var _defaultValue: Option[Any] = None
    var _descriminates: Boolean = false
    var _defaultBooleanValue: Option[Boolean] = None
    var _defaultIntegerValue: Option[Int] = None
    var _keyRegexp: Option[String] = None

    def withMultiValue(v: Boolean = true): Property = {
        _isMultiValue = v
        this
    }

    def withDescriminating(b: Boolean): Property = {
        _descriminates = b
        this
    }

    def withRequired(req: Boolean): Property = {
        _isRequired = req
        this
    }

    def isRequired: Boolean = _isRequired

    def withKeyRestriction(keyShouldStartFrom: String): Property = {
        _keyShouldStartFrom = Option(keyShouldStartFrom)
        this
    }

    def withDomain(d: StructuredType, custom: Boolean = false): Property = {
        _ownerClass = Option(d)
        if (custom) {
            d.registerCustomProperty(this)
        }
        else {
            d.registerProperty(this)
        }
        this
    }

    def setDefaultValue(s: Any): Property = {
        _defaultValue = Option(s)
        this
    }

    def setDefaultBooleanVal(s: Any): Property = {
        s match {
            case b: Boolean => _defaultBooleanValue = Option(b)
            case _ =>
        }
        this
    }

    def setDefaultIntegerVal(s: Any): Property = {
        s match {
            case b: Int => _defaultIntegerValue = Option(b)
            case _ =>
        }
        this
    }

    def defaultValue: Option[Any] = {
        _defaultValue match {
            case Some(x) => x
            case None =>
                _defaultBooleanValue match {
                    case Some(x) => x
                    case None =>
                        _defaultIntegerValue match {
                            case Some(x) => x
                            case None =>
                        }
                }
        }
        None
    }

    def isPrimitive = false

    def withRange(t: ITypeDefinition): Property = {
        this._nodeRange = Option(t)
        this
    }

    def isValueProperty:Boolean = _nodeRange.isDefined && _nodeRange.get.hasValueTypeInHierarchy

    def enumOptions:Option[Seq[String]] = _enumOptions

    def keyPrefix: Option[String] = _keyShouldStartFrom

    def withEnumOptions(op: Seq[String]): Property = {
        this._enumOptions = Option(op)
        this
    }


    def withKeyRegexp(regexp: String): Property = {
        this._keyRegexp = Option(regexp)
        this
    }

    def getKeyRegexp: Option[String] = _keyRegexp

    def matchKey(k: String): Boolean = {

        _groupName match {
            case Some(x) => x == k
            case None => {
                if (_keyShouldStartFrom.isDefined && k.startsWith(_keyShouldStartFrom.get)) {
                    true
                }
                else if (_enumOptions.isDefined && _enumOptions.contains(k)) {
                    true
                }
                //                else if (_keyRegexp.isDefined && _keyRegexp.get.test(k)) {
                //                    true
                //                }
                false
            }
        }
    }

    def domain: Option[StructuredType] = _ownerClass

    def range: Option[ITypeDefinition] = _nodeRange

    def isMultiValue: Boolean = range match {
        case Some(r) => r.hasArrayInHierarchy
        case None => _isMultiValue
    }


    def isDescriminator: Boolean = _descriminates
}