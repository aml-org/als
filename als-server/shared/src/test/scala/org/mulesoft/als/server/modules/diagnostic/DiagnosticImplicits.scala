package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation, PublishDiagnosticsParams}
import org.mulesoft.lsp.feature.common.Position
import org.yaml.model.YDocument
import org.yaml.render.YamlRender

object DiagnosticImplicits {

  implicit class PublishDiagnosticsParamsWriter(p: PublishDiagnosticsParams) {
    def write: String =
      YamlRender.render(yDocument)

    def yDocument: YDocument =
      YDocument.objFromBuilder(e => {
        e.entry("uri", v => {
          v += p.uri
        })
        e.entry("diagnostics", e => {
          e.list(b => {
            p.diagnostics.sortBy(_.range.start)(new PositionComparator).foreach(_.entry(b))
          })
        })
      })
  }

  class PositionComparator extends Ordering[Position] {
    override def compare(x: Position, y: Position): Int = {
      if (x.line != y.line) {
        x.line - y.line
      } else {
        x.character - y.character
      }
    }
  }

  implicit class DiagnosticWriter(d: Diagnostic) {
    def entry(b: YDocument.PartBuilder): Unit = {
      b.obj(obj => {
        obj.entry("message", d.message)
        obj.entry("range", d.range.toString)
        d.severity.foreach(c => obj.entry("severity", c.toString))
        d.code.foreach(c => obj.entry("code", c))
        d.codeDescription.foreach(c => obj.entry("codeDescription", c))
        d.source.foreach(c => obj.entry("source", c))
        obj.entry("relatedInformation", e => {
          e.list(b => {
            d.relatedInformation.foreach(_.entry(b))
          })
        })
      })
    }
  }

  implicit class RelatedInformationWriter(r: DiagnosticRelatedInformation) {
    def entry(b: YDocument.PartBuilder): Unit = {
      b.obj(d => {
        d.entry("message", r.message)
        d.entry("location", r.location.toString)
      })
    }
  }
}
