package org.mulesoft.exceptions

object PathTweaks {

  private var pathTweaker: PathTweaker = DummyPathTweaker

  def setPathTweaker(pt: PathTweaker): Unit = pathTweaker = pt

  /** anypoint-als hack: used to go from a DF path to a Cache path (file:///exchange_modules/ to file:///cache/
    */
  def apply(s: String): String = pathTweaker.tweak(s)

  /** anypoint-als hack: used to go from a Cache path to a DF path (file:///cache/ to file:///exchange_modules/
    */
  def unapply(s: String): String = pathTweaker.untweak(s)
}

trait PathTweaker {
  def tweak(s: String): String
  def untweak(s: String): String
}

object DummyPathTweaker extends PathTweaker {
  override def tweak(s: String): String = s

  override def untweak(s: String): String = s
}
