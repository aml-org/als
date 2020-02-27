package org.mulesoft.als.server.modules.workspace.references.visitors.aliases

import amf.core.annotations.{Aliases, ReferenceTargets}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfElement
import org.mulesoft.als.actions.common.AliasInfo
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.lsp.feature.common.{Location, Position, Range}

class AliasesVisitor extends AliasesVisitorType {
  private def parserToDtoRange(core: amf.core.parser.Range): Range =
    Range(Position(core.start.line - 1, core.start.column), Position(core.end.line - 1, core.end.column))

  override protected def innerVisit(element: AmfElement): Seq[Result] = {
    element match {
      case bu: BaseUnit =>
        bu.annotations
          .find(classOf[Aliases])
          .map { aliases =>
            val targets = bu.annotations.collect {
              case rt: ReferenceTargets => rt
            }
            aliases.aliases.flatMap { alias =>
              targets.find(t => t.targetLocation == alias._2._1).map { tl =>
                AliasInfo(alias._1,
                          Location(bu.location().getOrElse(bu.id), parserToDtoRange(tl.originRange)),
                          tl.targetLocation)
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
  override def apply(): AliasesVisitor = new AliasesVisitor()
}
