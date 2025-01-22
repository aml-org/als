package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.{AllCodeActions, CustomCodeActions}
import org.mulesoft.als.actions.codeactions.plugins.declarations.delete.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.testaction.TestCodeAction
import org.mulesoft.als.common.WorkspaceEditSerializer
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfiguration, EmptyProjectConfigurationState}
import org.scalatest.compatible.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.yaml.model.YDocument
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.render.YamlRender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BaseActionsPluginTest extends AnyFlatSpec {
  behavior of "AllCodeActions"
  it should "add a custom code action to the registry" in {
    CustomCodeActions.addCustom(TestCodeAction)
    assert(AllCodeActions.all.contains(TestCodeAction))
    CustomCodeActions.clear()
    assert(!AllCodeActions.all.contains(TestCodeAction))
  }
}