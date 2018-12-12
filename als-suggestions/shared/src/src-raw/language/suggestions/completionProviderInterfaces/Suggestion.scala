package org.mulesoft.als.suggestions.completionProviderInterfaces

import org.mulesoft.als.suggestions.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.completionProviderInterfaces.IEditorStateProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IFSProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.Suggestion;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolver;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolverExt;

trait Suggestion {
  var text: String
  var description: String
  var displayText: String
  var prefix: String
  var category: String
}
