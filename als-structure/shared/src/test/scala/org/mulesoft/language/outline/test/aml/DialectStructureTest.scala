package org.mulesoft.language.outline.test.aml

import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

trait DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath     = filePath(dialectPath)
    val editorConfiguration = EditorConfiguration().withDialect(fullDialectPath)
    for {
      alsConfiguration <- editorConfiguration.getState.map(state =>
        ALSConfigurationState(state, EmptyProjectConfigurationState, None)
      )
      result <- super.runTest(path, jsonPath, Some(alsConfiguration))
    } yield result
  }

}
