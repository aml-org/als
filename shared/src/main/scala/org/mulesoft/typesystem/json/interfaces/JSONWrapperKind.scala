package org.mulesoft.typesystem.json.interfaces

import org.mulesoft.common.time.SimpleDateTime

sealed trait JSONWrapperKind[T] {
    def cast(v: Any): Option[T]
}

object JSONWrapperKind {

    object OBJECT extends JSONWrapperKind[JSONWrapper] {
        def cast(v: Any): Option[JSONWrapper] = {
            v match {
                case x:JSONWrapper => Some(x)
                case _ => None
            }
        }
    }

    object ARRAY extends JSONWrapperKind[Seq[JSONWrapper]] {
        def cast(v: Any): Option[Seq[JSONWrapper]] = {
            v match {
                case x:Seq[_] => Some(x).asInstanceOf[Option[Seq[JSONWrapper]]]
                case _ => None
            }
        }
    }

    object NUMBER extends JSONWrapperKind[Number] {
        def cast(v: Any): Option[Number] = {
            v match {
                case x:Number => Some(x)
                case _ => None
            }
        }
    }

    object STRING extends JSONWrapperKind[String] {
        def cast(v: Any): Option[String] = {
            v match {
                case x:String => Some(x)
                case _ => None
            }
        }
    }

    object BOOLEAN extends JSONWrapperKind[Boolean] {
        def cast(v: Any): Option[Boolean] = {
            v match {
                case x:Boolean => Some(x)
                case _ => None
            }
        }
    }

    object DATE extends JSONWrapperKind[SimpleDateTime] {
        def cast(v: Any): Option[SimpleDateTime] = {
            v match {
                case x:SimpleDateTime => Some(x)
                case _ => None
            }
        }
    }

    object NULL extends JSONWrapperKind[Null] {
        def cast(v: Any): Option[Null] = {
            v match {
                case x:JSONWrapper => None
                case _ => Some(v).asInstanceOf[Option[Null]]
            }
        }
    }

    def wrapperKind(value:Any):JSONWrapperKind[_] = {

        value match {
            case x:JSONWrapper => OBJECT
            case x:Seq[_] => ARRAY
            case x:Number => NUMBER
            case x:String => STRING
            case x:Boolean => BOOLEAN
            case x:SimpleDateTime => DATE
            case _ =>
                Option(value) match {
                    case Some(x) => throw new Error("Unsupported JSON value: " + value)
                    case None => NULL
                }
        }
    }
}
