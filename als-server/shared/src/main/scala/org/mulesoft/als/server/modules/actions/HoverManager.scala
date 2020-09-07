package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.metamodel.{Field, Obj}
import amf.core.model.domain.{AmfObject, DataNode}
import amf.core.parser
import amf.core.parser.{FieldEntry, Range, Position => AmfPosition}
import amf.core.vocabulary.ValueType
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.hover.PatchedHover
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, DialectImplicits}
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.hover._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestType, TelemeteredRequestHandler}
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HoverManager(wm: WorkspaceManager, amfInstance: AmfInstance, telemetryProvider: TelemetryProvider)
    extends RequestModule[HoverClientCapabilities, Boolean] {

  private lazy val patchedHover = new PatchedHover(amfInstance.alsAmlPlugin)
  private var active            = true

  override val `type`: ConfigType[HoverClientCapabilities, Boolean] =
    HoverConfigType

  override def applyConfig(config: Option[HoverClientCapabilities]): Boolean = {
    // should check mark up?
    active = config.exists(_.contentFormat.contains(MarkupKind.Markdown)) || config.isEmpty
    true
  }

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(new HoverTelemeteredRequestHandler())

  class HoverTelemeteredRequestHandler() extends TelemeteredRequestHandler[HoverParams, Hover] {
    override def `type`: RequestType[HoverParams, Hover] = HoverRequestType

    override protected def telemetry: TelemetryProvider = telemetryProvider

    override protected def task(params: HoverParams): Future[Hover] = hover(params)

    override protected def code(params: HoverParams): String = "HoverManager"

    override protected def beginType(params: HoverParams): MessageTypes = MessageTypes.BEGIN_HOVER

    override protected def endType(params: HoverParams): MessageTypes = MessageTypes.END_HOVER

    override protected def msg(params: HoverParams): String =
      s"request for hover on ${params.textDocument.uri} and position ${params.position.toString}"

    override protected def uri(params: HoverParams): String = params.textDocument.uri

    private def hover(params: HoverParams): Future[Hover] = {
      val uuid = UUID.randomUUID().toString
      wm.getLastUnit(params.textDocument.uri, uuid).map { cu =>
        val dtoPosition: Position = LspRangeConverter.toPosition(params.position)

        getSemantic(cu, dtoPosition, params.textDocument.uri)
          .map(s => Hover(s._1, s._2.map(r => LspRangeConverter.toLspRange(PositionRange(r))))) // if sequence, we could show all the semantic hierarchy?
          .getOrElse(Hover.empty)
      }
    }

    def isInDeclarationKey(cu: CompilableUnit, amfPosition: AmfPosition): Boolean =
      cu.unit.annotations.declarationKeys().exists(k => k.entry.key.range.contains(amfPosition))

    private def buildDeclarationKeyUri(name: String): ValueType =
      ValueType(s"http://als.declarationKeys/#${name}DeclarationKey")

    def fromDeclarationKey(cu: CompilableUnit, amfPosition: AmfPosition): Option[(Seq[String], Option[parser.Range])] =
      cu.unit.annotations
        .declarationKeys()
        .find(k => k.entry.key.range.contains(amfPosition))
        .map(key => {
          val valueType = getDeclarationValueType(cu, key.entry)
          val description = valueType
            .map(
              v =>
                amfInstance.alsAmlPlugin
                  .getSemanticDescription(buildDeclarationKeyUri(v.name))
                  .getOrElse(s"Contains declarations of reusable ${v.name} objects"))
            .getOrElse({
              s"Contains declarations for ${key.entry.key.value.toString}"
            })

          (Seq(description), Some(Range(key.entry.range)))
        })

    def getPatchedHover(tree: ObjectInTree,
                        branch: YPartBranch,
                        dialect: Option[Dialect]): Option[(Seq[String], Option[parser.Range])] =
      dialect.flatMap(patchedHover.getHover(tree.obj, branch, _))

    private def getSemantic(cu: CompilableUnit,
                            dtoPosition: Position,
                            location: String): Option[(Seq[String], Option[parser.Range])] = {
      val tree: ObjectInTree = cu.tree.getCachedOrNew((dtoPosition, location))
      val branch             = cu.yPartBranch.getCachedOrNew(dtoPosition, location)
      if (tree.obj.isInstanceOf[DataNode]) hackFromNonDynamic(tree, cu)
      else if (isInDeclarationKey(cu, dtoPosition.toAmfPosition)) fromDeclarationKey(cu, dtoPosition.toAmfPosition)
      else getPatchedHover(tree, branch, cu.definedBy).orElse(fromTree(tree, cu))
    }

    private def hackFromNonDynamic(tree: ObjectInTree,
                                   cu: CompilableUnit): Option[(Seq[String], Option[parser.Range])] =
      tree.stack.collectFirst({ case obj if !obj.isInstanceOf[DataNode] => obj }).flatMap(classTerm(_, cu))

    private def fromTree(tree: ObjectInTree, cu: CompilableUnit): Option[(Seq[String], Option[parser.Range])] =
      tree.fieldEntry.orElse(tree.fieldValue).flatMap(f => fieldEntry(f, cu)).orElse(classTerm(tree.obj, cu))

    private def fieldEntry(f: FieldEntry, cu: CompilableUnit): Option[(Seq[String], Option[parser.Range])] = {
      propertyTerm(f.field, cu).map(s =>
        (Seq(s), f.value.annotations.range().orElse(f.value.value.annotations.range())))
    }
    private def propertyTerm(field: Field, cu: CompilableUnit): Option[String] = {
      amfInstance.alsAmlPlugin
        .getSemanticDescription(field.value)
        .orElse({
          if (field.doc.description.nonEmpty) Some(field.doc.description) else None
        })
      // TODO: inherits from another???
    }

    private def getSemanticForMeta(meta: Obj): Seq[String] = {
      val classSemantic = meta.`type`.flatMap { v =>
        amfInstance.alsAmlPlugin.getSemanticDescription(v)
      }
      if (classSemantic.isEmpty && meta.doc.description.nonEmpty) Seq(meta.doc.description)
      else classSemantic
    }

    private def classTerm(obj: AmfObject, cu: CompilableUnit): Option[(Seq[String], Option[amf.core.parser.Range])] = {
      val finalSemantics = getSemanticForMeta(obj.meta)
      if (finalSemantics.nonEmpty) Some((finalSemantics, obj.annotations.range()))
      else None
    }

  }

  private def getDeclarationValueType(cu: CompilableUnit, entry: YMapEntry): Option[ValueType] = {
    cu.definedBy
      .flatMap(
        _.declarationsMapTerms
          .find(_._2 == entry.key.value.toString)
          .map(a => {
            ValueType(a._1)
          }))
  }
}
