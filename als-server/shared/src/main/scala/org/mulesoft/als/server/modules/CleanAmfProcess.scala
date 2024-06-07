package org.mulesoft.als.server.modules

import amf.core.client.scala.AMFResult
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AMLSpecificConfiguration,
  AmfResult => AmfResultWrap
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CleanAmfProcess {
  def parseAndResolve(
      refinedUri: String,
      alsConfigurationState: ALSConfigurationState
  ): Future[(AmfResultWrap, AMFResult, ALSConfigurationState)] =
    for {
      pr <- AMLSpecificConfiguration(alsConfigurationState.getAmfConfig(refinedUri, asMain = true))
        .parse(refinedUri)
        .map(new AmfResultWrap(_))
      helper <- Future(alsConfigurationState.configForUnit(pr.result.baseUnit))
      resolved <- Future({
        Logger.debug(s"About to report: $refinedUri", "CleanAmfProcess", "resolve")
        helper.fullResolution(pr.result.baseUnit)
      })
    } yield (pr, resolved, alsConfigurationState)
}
