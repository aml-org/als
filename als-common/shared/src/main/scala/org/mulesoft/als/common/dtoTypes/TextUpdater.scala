package org.mulesoft.als.common.dtoTypes

import java.lang.Math.max

object TextUpdater {
  type ReplaceParams = (Int, Int, String)

  def replace(range: PositionRange, text: String, fragment: String): String =
    replace(range.start.offset(text), range.end.offset(text), text, fragment)

  def replace(position: Position, length: Int, text: String, fragment: String): String = {
    val startOffset = position.offset(text)
    replace(startOffset, startOffset + length, text, fragment)
  }

  def replace(from: Int, to: Int, text: String, fragment: String): String =
    text.take(from) + fragment + text.drop(to)

  def replaceParams(range: PositionRange, text: String, fragment: String): ReplaceParams =
    (range.start.offset(text), range.end.offset(text), fragment)

  def replaceParams(position: Position, length: Int, text: String, fragment: String): ReplaceParams = {
    val startOffset = position.offset(text)
    (startOffset, startOffset + length, fragment)
  }

  def replaceAll(text: String, replaceParamsSeq: Seq[ReplaceParams]): String = {
    def innerReplaceAll(seq: Seq[ReplaceParams], acc: String, rest: String, offset: Int): String = seq match {
      case Seq() => acc + rest
      case Seq(current, tail @ _*) =>
        val (from, to, fragment) = current
        val newAcc               = acc + rest.take(max(0, from - offset)) + fragment
        val newRest              = rest.drop(max(0, to - offset))
        innerReplaceAll(tail, newAcc, newRest, to)
    }

    val sorted = replaceParamsSeq.sortWith((first, second) => first._1 < second._2)
    innerReplaceAll(sorted, "", text, 0)
  }

}
