//package org.mulesoft.language.server.common.utils
//
//import scala.language.experimental.macros
//import scala.reflect.macros._
//
///**
//  * Utility for obtaining type name
//  */
//object TypeName {
//
//  /**
//    * Gets type name on compile time
//    * @tparam T
//    * @return
//    */
//  def get[T]: String = macro typeName_[T]
//
//  def typeName_[T](c: Context)(implicit tag: c.WeakTypeTag[T]): c.Expr[String] = {
//
//    import c.universe._
//
//    val name = showRaw(tag.tpe.typeSymbol.fullName)
//    reify {
//      c.Expr[String] { Literal(Constant(name)) }.splice
//    }
//  }
//}
