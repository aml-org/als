package org.mulesoft.als.server.modules.workspace.resolution

import org.mulesoft.als.server.modules.workspace.Repository
import org.mulesoft.amfintegration.AmfResolvedUnit

class ResolutionRepository extends Repository[AmfResolvedUnit] {
  override def updateUnit(key: String, unit: AmfResolvedUnit): Unit =
    if (!units.get(key).exists(u => u.originalUnit.eq(unit.originalUnit)))
      super.updateUnit(key, unit)
}
