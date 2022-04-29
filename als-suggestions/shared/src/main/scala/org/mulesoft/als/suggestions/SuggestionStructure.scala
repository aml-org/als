package org.mulesoft.als.suggestions

case class SuggestionStructure(
    rangeKind: RangeKind = StringScalarRange,
    isKey: Boolean = false,
    keyRange: ScalarRange = StringScalarRange,
    isMandatory: Boolean = false,
    isTopLevel: Boolean = false,
    nonPlain: Boolean = false
) {

  def scalarProperty: Boolean = rangeKind.isInstanceOf[ScalarRange]

  def isArray: Boolean = rangeKind == ArrayRange

  def isObject: Boolean = rangeKind == ObjectRange
}

trait RangeKind

object ObjectRange extends RangeKind
object ArrayRange  extends RangeKind
trait ScalarRange  extends RangeKind

object StringScalarRange extends ScalarRange
object NumberScalarRange extends ScalarRange
object BoolScalarRange   extends ScalarRange
object PlainText         extends RangeKind
