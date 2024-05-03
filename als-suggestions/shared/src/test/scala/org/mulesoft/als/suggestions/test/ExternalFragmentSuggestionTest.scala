package org.mulesoft.als.suggestions.test

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.{MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.{Suggestions, UnitBundle}
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.Future

class ExternalFragmentSuggestionTest extends AsyncFunSuite with PlatformSecrets with MarkerFinderTest {

  protected val dr = new PlatformDirectoryResolver(platform)

  private val baseUri   = "file://als-suggestions/shared/src/test/resources/test/external-fragment"
  private val mainUri   = s"$baseUri/api.raml"
  private val schemaUri = s"$baseUri/schema.json"

  test("should suggest for external fragment if dialect is defined") {
    for {
      suggestionsDependency <- EditorConfiguration().getState
        .map(ALSConfigurationState(_, EmptyProjectConfigurationState, None))
        .flatMap(state => {
          new Suggestions(AlsConfiguration(), dr, accessBundleDependency(state))
            .initialized()
            .suggest(schemaUri, 77, snippetsSupport = true, None)
        })
      suggestionsMain <- EditorConfiguration().getState
        .map(ALSConfigurationState(_, EmptyProjectConfigurationState, None))
        .flatMap(state => {
          new Suggestions(AlsConfiguration(), dr, accessBundleMain(state))
            .initialized()
            .suggest(schemaUri, 77, snippetsSupport = true, None)
        })
    } yield {
      assert(suggestionsDependency.toSet.equals(suggestionsMain.toSet))
    }

  }
  private def accessBundleDependency(alsConfigurationState: ALSConfigurationState)(uri: String): Future[UnitBundle] =
    alsConfigurationState
      .parse(schemaUri)
      .map(r => UnitBundle(r.result.baseUnit, JsonSchemaDraft4Dialect.dialect, r.context))

  private def accessBundleMain(alsConfigurationState: ALSConfigurationState)(uri: String): Future[UnitBundle] =
    alsConfigurationState
      .parse(mainUri)
      .map(r => UnitBundle(r.result.baseUnit.references.head, JsonSchemaDraft4Dialect.dialect, r.context))
}
