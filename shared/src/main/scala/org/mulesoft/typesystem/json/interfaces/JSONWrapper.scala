package org.mulesoft.typesystem.json.interfaces

import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._

trait JSONWrapper {

    def key:Option[String]

    def kind: JSONWrapperKind[_ <: Any]

    /** *
      *
      * @return JSONWrapper, JSONWrapper[], Int, String, Boolean, Null
      */
    def value: Any

    def value[T](kind: JSONWrapperKind[T]): Option[T] = kind.cast(value)

    def properties: Seq[JSONProperty]

    def hasProperty(name: String): Boolean = getProperty(name).isDefined

    def propertyNames: Seq[String] = properties.map(x => x.name)

    def property(name: String): Option[JSONProperty] = if (kind == OBJECT) getProperty(name) else None

    protected def getProperty(name: String): Option[JSONProperty]

    def propertyValue(name: String): Option[JSONWrapper] = property(name) match {
        case Some(p) => Some(p.value)
        case _ => None
    }

    def propertyValue[T](name: String, kind: JSONWrapperKind[T]): Option[T] = property(name) match {
        case Some(p) => p.value.value[T](kind)
        case _ => None
    }

    def numberValue(): Option[Number] = value(NUMBER)

    def items: Option[Seq[JSONWrapper]] = value(ARRAY)

    def stringValue: Option[String] = value(STRING)

    def booleanValue: Option[Boolean] = value(BOOLEAN)

    def isNull: Boolean = kind == NULL

    def isObject: Boolean = kind == OBJECT

    def isNumber: Boolean = kind == NUMBER

    def isBoolean: Boolean = kind == BOOLEAN

    def isString: Boolean = kind == STRING

    def asObject: Option[JSONWrapper] = kind match {
        case OBJECT => Some(this)
        case _ => None
    }

    def numberPropertyValue(name: String): Option[Number] = propertyValue(name, NUMBER)

    def arrayPropertyValue(name: String): Option[Seq[JSONWrapper]] = propertyValue(name, ARRAY)

    def stringPropertyValue(name: String): Option[String] = propertyValue(name, STRING)

    def booleanPropertyValue(name: String): Option[Boolean] = propertyValue(name, BOOLEAN)

    def isNullProperty(name: String): Boolean = kind == NULL

    def objectPropertyValue(name: String): Option[JSONWrapper] = propertyValue(name) match {
        case Some(w) => w.asObject
        case _ => None
    }

    def range: NodeRange

    override def toString: String = kind match {

        case STRING => "\"" + value(STRING).get.replace("\"", "\\\"") + "\""
        case NUMBER => "" + value(NUMBER).get
        case BOOLEAN => "" + value(BOOLEAN).get
        case NULL => "null"
        case OBJECT => "{" + properties.map(p=>s"${p.name}:${p.value}").mkString(",") + "}"
        case ARRAY => "[" + value(ARRAY).get.mkString(",") + "]"
    }
}

