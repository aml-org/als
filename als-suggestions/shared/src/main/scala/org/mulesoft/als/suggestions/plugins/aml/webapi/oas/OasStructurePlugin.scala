//package org.mulesoft.als.suggestions.plugins.aml.webapi.oas
//
//import amf.core.annotations.DeclaredElement
//import amf.core.model.domain.Shape
//import amf.dialects.oas.nodes.{AMLInfoObject, Oas20ResponseObject}
//import amf.plugins.domain.webapi.models._
//import org.mulesoft.als.suggestions.RawSuggestion
//import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
//import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
//import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionPlugin, _}
//import org.mulesoft.als.suggestions.plugins.aml._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//object OasStructurePlugin extends AMLCompletionPlugin {
//  override def id: String = "AMLStructureCompletionPlugin"
//
//  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
//    request.amfObject match {
//      case _: Parameter | _: Shape => emptySuggestion
//      case r: Response if r.annotations.contains(classOf[DeclaredElement]) && request.fieldEntry.isEmpty =>
//        declaredResponse(request)
//      case _: Tag if request.branchStack.headOption.exists(_.isInstanceOf[Operation]) =>
//        emptySuggestion
//      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
//        infoSuggestions()
//      case _ =>
//        AMLStructureCompletionPlugin.resolve(request)
//    }
//  }
//
//  private def declaredResponse(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
//    new AMLStructureCompletionsPlugin(
//      request.propertyMapping.filter(_.id != Oas20ResponseObject.statusCodeProperty.id))
//      .resolve(request.amfObject.meta.`type`.head.iri())
//  }
//
//  def infoSuggestions(): Future[Seq[RawSuggestion]] =
//    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))
//}
