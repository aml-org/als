package org.mulesoft.amfintegration.visitors.aliases

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfElement
import amf.core.internal.annotations.Aliases
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.relationships.AliasInfo
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.lsp.feature.common.{Location, Position, Range}
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}

class AliasesVisitor extends AliasesVisitorType {
  private def parserToDtoRange(core: AmfPositionRange): Range =
    Range(Position(core.start.line - 1, core.start.column), Position(core.end.line - 1, core.end.column))

  override protected def innerVisit(element: AmfElement): Seq[AliasInfo] = {
    element match {
      case bu: BaseUnit =>
        bu.annotations
          .find(classOf[Aliases])
          .map { aliases =>
            val targets = bu.annotations.targets()
            aliases.aliases.flatMap { alias =>
              targets.get(alias._2.fullUrl).map { ranges =>
                ranges.map(r => {
                  AliasInfo(alias._1, Location(bu.location().getOrElse(bu.id), parserToDtoRange(r)), alias._2.fullUrl)
                })

              }
            }
          }
          .map(_.toSeq.flatten)
          .getOrElse(Nil)
      case _ => Nil
    }
  }
}

object AliasesVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[AliasesVisitor] = Some(new AliasesVisitor())
}
