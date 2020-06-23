package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.metamodel.Field
import amf.core.model.domain.AmfObject
import amf.core.parser
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.ObjectInTreeBuilder
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.hover._
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HoverManager(wm: WorkspaceManager, amfInstance: AmfInstance)
    extends RequestModule[HoverClientCapabilities, Boolean] {
  private var active = true
  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[HoverParams, Hover] {
      override def `type`: RequestType[HoverParams, Hover] = HoverRequestType

      override def apply(params: HoverParams): Future[Hover] = {
        if (active) hover(params) else Future.successful(Hover.empty)
      }
    }
  )

  override val `type`: ConfigType[HoverClientCapabilities, Boolean] =
    HoverConfigType

  private def hover(params: HoverParams): Future[Hover] = {
    val uuid = UUID.randomUUID().toString
    wm.getLastUnit(params.textDocument.uri, uuid).map { cu =>
      val dtoPosition = LspRangeConverter.toPosition(params.position)
      val tree        = ObjectInTreeBuilder.fromUnit(cu.unit, dtoPosition.toAmfPosition)
      val semantic: Option[(Seq[String], Option[amf.core.parser.Range])] =
        tree.fieldEntry.flatMap(f => fieldEntry(f, cu)).orElse(classTerm(tree.obj, cu))
      semantic
        .map(s => Hover(s._1, s._2.map(r => LspRangeConverter.toLspRange(PositionRange(r))))) // if sequence, we could show all the semantic hierarchy?
        .getOrElse(Hover.empty)
    }
  }

  private def fieldEntry(f: FieldEntry, cu: CompilableUnit): Option[(Seq[String], Option[parser.Range])] = {
    propertyTerm(f.field, cu).map(s => (Seq(s), f.value.annotations.range().orElse(f.value.value.annotations.range())))
  }
  private def propertyTerm(field: Field, cu: CompilableUnit): Option[String] = {
    if (field.doc.description.nonEmpty)
      Some(field.doc.description)
    else
      amfInstance.alsAmlPlugin.getSemanticDescription(field.value) // TODO: inherits from another???
  }

  private def classTerm(obj: AmfObject, cu: CompilableUnit): Option[(Seq[String], Option[amf.core.parser.Range])] = {
    val objectSemantics =
      if (obj.meta.doc.description.nonEmpty) Seq(obj.meta.doc.description)
      else {
        obj.meta.`type`.flatMap { v =>
          amfInstance.alsAmlPlugin.getSemanticDescription(v)
        }
      }

    if (objectSemantics.nonEmpty) Some((objectSemantics, obj.annotations.range()))
    else None
  }

  override def applyConfig(config: Option[HoverClientCapabilities]): Boolean = {
    // should check mark up?
    active = config.exists(_.contentFormat.contains(MarkupKind.Markdown)) || config.isEmpty
    true
  }

  override def initialize(): Future[Unit] = Future.successful()
}
