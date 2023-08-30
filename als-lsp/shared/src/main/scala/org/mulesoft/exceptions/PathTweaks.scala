package org.mulesoft.exceptions

object PathTweaks {

  /** anypoint-als hack: used to go from a DF path to a Cache path (file:///exchange_modules/ to file:///cache/
    */
  def apply(s: String): String = s

  /** anypoint-als hack: used to go from a Cache path to a DF path (file:///cache/ to file:///exchange_modules/
    */
  def unapply(s: String): String = s
}
