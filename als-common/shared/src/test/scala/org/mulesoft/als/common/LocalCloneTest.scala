package org.mulesoft.als.common

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.config.{CachedReference, UnitCache}
import org.scalatest.funsuite.AsyncFunSuite
import amf.core.client.scala.model.document.{Document, Module}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.metamodel.document.DocumentModel
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfigurationState,
  ProjectConfigurationState
}
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
class LocalCloneTest extends AsyncFunSuite with Matchers {

  val currentLocation = "als://current-document"
  val moduleLocation  = "als://module-id/dependency"
  val module          = Module().withId(moduleLocation).withLocation(moduleLocation)
  val configurationState = new ProjectConfigurationState {
    override def cache: UnitCache = new UnitCache {

      /** Fetch specified reference and return associated cached reference if exists. */
      override def fetch(url: String): Future[CachedReference] = Future.failed(new Exception())
    }

    override val extensions: Seq[Dialect]                = Nil
    override val profiles: Seq[ValidationProfile]        = Nil
    override val config: ProjectConfiguration            = ProjectConfiguration("", "", Set(module.id))
    override val results: Seq[AMFParseResult]            = Nil
    override val resourceLoaders: Seq[ResourceLoader]    = Nil
    override val projectErrors: Seq[AMFValidationResult] = Nil
  }
  val state = ALSConfigurationState(EditorConfigurationState.empty, configurationState, None)

  test("Test localClone with cached library") {

    val document = Document()
      .withId(currentLocation)
      .withLocation(currentLocation)
      .setArrayWithoutId(DocumentModel.References, Seq(module))

    val clone = state.getLocalClone(document)
    clone shouldNot be(document)
    clone.id should be(document.id)
    clone.references.head shouldBe document.references.head

  }

  test("Test localClone with two libraries, one cached") {

    val secondModule = Module().withId("als://local-module").withLocation("als://local-module")
    val document = Document()
      .withId(currentLocation)
      .withLocation(currentLocation)
      .setArrayWithoutId(DocumentModel.References, Seq(module, secondModule))

    val clone = state.getLocalClone(document)
    clone shouldNot be(document)
    clone.id should be(document.id)
    clone.references.head shouldBe document.references.head
    clone.references.last shouldNot be(document.references.last)
    clone.references.last.id shouldBe document.references.last.id

  }

  test("Test localClone with transitive cached") {

    val secondModule = Module()
      .withId("als://local-module")
      .withLocation("als://local-module")
      .setArrayWithoutId(DocumentModel.References, Seq(module))
    val document = Document()
      .withId(currentLocation)
      .withLocation(currentLocation)
      .setArrayWithoutId(DocumentModel.References, Seq(secondModule))

    val clone = state.getLocalClone(document)
    clone shouldNot be(document)
    clone.id should be(document.id)
    val firstCloneReference = clone.references.head
    val firstReference      = document.references.head
    firstCloneReference shouldNot be(firstReference.references.head)

    val transitiveClonedReference = firstCloneReference.references.head
    val transitiveReference       = firstReference.references.head
    transitiveClonedReference shouldBe transitiveReference
    transitiveClonedReference.id shouldBe transitiveReference.id

  }

}
