package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.structure

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.domain.AmfObject
import amf.shapes.client.scala.model.domain.ScalarShape
import amf.shapes.internal.annotations.AVROSchemaType
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.dialect.dialects.avro.{AvroEnumNode, AvroFixedNode, AvroMapNode}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode
import org.mulesoft.common.client.lexical.ASTElement
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ResolveBySchemaType[T <: AmfObject] extends ResolveIfApplies {

  protected val schemaType: String

  protected val node: DialectNode
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case obj: T if isSchemaType(obj) && isSameLevel(obj.annotations.astElement(), request.astPartBranch) =>
        applies(mapNodeSuggestions(request.actualDialect))
      case _ =>
        notApply
    }
  }

  private def isSameLevel(maybeElement: Option[ASTElement], astPartBranch: ASTPartBranch): Boolean =
    (maybeElement, astPartBranch) match {
      case (Some(ast: YMap), ypb: YPartBranch) =>
        ypb.node == ast
      case _ => false
    }

  private def isSchemaType(obj: T) = obj.annotations.avroSchemaType() match {
    case Some(value) =>
      value match {
        case AVROSchemaType(t) => t == schemaType
        case _                 => false
      }
    case _ => false
  }

  private def mapNodeSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(node.Obj.propertiesRaw(Some("schemas"), d))
}

object ResolveFixed extends ResolveBySchemaType[ScalarShape] {
  override protected val schemaType: String = "fixed"
  override protected val node: DialectNode  = AvroFixedNode
}

object ResolveMap extends ResolveBySchemaType[ScalarShape] {
  override protected val schemaType: String = "map"
  override protected val node: DialectNode  = AvroMapNode
}
object ResolveEnum extends ResolveBySchemaType[ScalarShape] {
  override protected val schemaType: String = "enum"
  override protected val node: DialectNode  = AvroEnumNode
}
