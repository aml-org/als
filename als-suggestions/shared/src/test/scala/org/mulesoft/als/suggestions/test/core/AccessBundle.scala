package org.mulesoft.als.suggestions.test.core

import org.mulesoft.als.suggestions.client.UnitBundle
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AccessBundle {

  def accessBundle(alsConfigurationState: ALSConfigurationState)(uri: String): Future[UnitBundle] =
    alsConfigurationState.parse(uri).map(r => UnitBundle(r.result.baseUnit, r.documentDefinition, r.context))
}
