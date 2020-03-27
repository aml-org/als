//
//package org.mulesoft.als.suggestions.plugins.aml.webapi.async
//
//import amf.core.model.domain.{AmfObject, Shape}
//import amf.dialects.oas.nodes.AMLInfoObject
//import amf.plugins.domain.shapes.models.{AnyShape, ScalarShape}
//import amf.plugins.domain.webapi.metamodel.{MessageModel, ParameterModel}
//import amf.plugins.domain.webapi.models.{Payload, Response, Server, WebApi}
//import org.mulesoft.als.suggestions.RawSuggestion
//import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
//import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
//import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionPlugin
//import org.mulesoft.als.suggestions.plugins.aml._
//import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApiVariableObject
//
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//import org.mulesoft.als.suggestions.plugins.aml._
//import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
//object Async20StructureCompletionPlugin extends AMLCompletionPlugin {
//  override def id: String = "AMLStructureCompletionPlugin"
//
//  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
//    request.amfObject match {
//      case r: Response if request.fieldEntry.map(_.field).contains(MessageModel.Headers) =>
//        Future.successful(
//          Async20TypeFacetsCompletionPlugin
//            .resolveShape(AnyShape(r.fields, r.annotations), Nil))
//      case _: Response if !MessageKnowledge.isRootMessageBlock(request) =>
//        emptySuggestion
//      case _: ScalarShape if request.branchStack.exists(_.isInstanceOf[Server]) && request.fieldEntry.isEmpty =>
//        Future(AsyncApiVariableObject.Obj.propertiesRaw(None))
//      case _: Shape =>
//        emptySuggestion
//      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
//        infoSuggestions()
//      case _ =>
//        AMLStructureCompletionPlugin.resolve(request)
//    }
//
//  def infoSuggestions(): Future[Seq[RawSuggestion]] =
//    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))
//}