package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.workspace.WorkspaceConfigurationClientCapabilities
import org.mulesoft.als.server.feature.diagnostic.{
  CleanDiagnosticTreeClientCapabilities,
  CustomValidationClientCapabilities
}
import org.mulesoft.als.server.feature.fileusage.FileUsageClientCapabilities
import org.mulesoft.als.server.feature.renamefile.RenameFileActionClientCapabilities
import org.mulesoft.als.server.feature.serialization.{ConversionClientCapabilities, SerializationClientCapabilities}
import org.mulesoft.lsp.configuration.{TextDocumentClientCapabilities, WorkspaceClientCapabilities}

/** ClientCapabilities now define capabilities for dynamic registration, workspace and text document features the client
  * supports. The experimental can be used to pass experimental capabilities under development. For future compatibility
  * a ClientCapabilities object literal can have more properties set than currently defined. Servers receiving a
  * ClientCapabilities object literal with unknown properties should ignore these properties. A missing property should
  * be interpreted as an absence of the capability. If a missing property normally defines sub properties, all missing
  * sub properties should be interpreted as an absence of the corresponding capability.
  *
  * @param workspace
  *   Workspace specific client capabilities.
  * @param textDocument
  *   Text document specific client capabilities.
  * @param experimental
  *   Experimental client capabilities.
  * @param serialization
  *   If the client supports serialization notifications
  * @param cleanDiagnosticTree
  *   If the client wantst to enable request of clean validations
  */
case class AlsClientCapabilities(
    workspace: Option[WorkspaceClientCapabilities] = None,
    textDocument: Option[TextDocumentClientCapabilities] = None,
    experimental: Option[AnyRef] = None,
    serialization: Option[SerializationClientCapabilities] = None,
    cleanDiagnosticTree: Option[CleanDiagnosticTreeClientCapabilities] = None,
    fileUsage: Option[FileUsageClientCapabilities] = None,
    conversion: Option[ConversionClientCapabilities] = None,
    renameFileAction: Option[RenameFileActionClientCapabilities] = None,
    workspaceConfiguration: Option[WorkspaceConfigurationClientCapabilities] =
      None, // fixme: maybe it should be inside WorkspaceClientCapabilities? @deprecated
    customValidations: Option[CustomValidationClientCapabilities] = None
)
