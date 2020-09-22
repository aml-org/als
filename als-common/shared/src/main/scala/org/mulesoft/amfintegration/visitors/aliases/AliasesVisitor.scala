package org.mulesoft.amfintegration.visitors.aliases

import amf.core.annotations.Aliases
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfElement
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.relationships.AliasInfo
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.lsp.feature.common.{Location, Position, Range}
class AliasesVisitor extends AliasesVisitorType {
  private def parserToDtoRange(core: amf.core.parser.Range): Range =
    Range(Position(core.start.line - 1, core.start.column), Position(core.end.line - 1, core.end.column))

  override protected def innerVisit(element: AmfElement): Seq[AliasInfo] = {
    element match {
      case bu: BaseUnit =>
        bu.annotations
          .find(classOf[Aliases])
          .map { aliases =>
            val targets = bu.annotations.targets()
            aliases.aliases.flatMap { alias =>
              targets.get(alias._2._1).map { range =>
                AliasInfo(alias._1,
                                 Location(bu.location().getOrElse(bu.id), parserToDtoRange(range)),
                                 alias._2._1)
              }
            }
          }
          .map(_.toSeq)
          .getOrElse(Nil)
      case _ => Nil
    }
  }
}

object AliasesVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[AliasesVisitor] = Some(new AliasesVisitor())
}
