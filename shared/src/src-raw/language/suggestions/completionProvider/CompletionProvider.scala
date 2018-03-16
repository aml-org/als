package org.mulesoft.als.suggestions.completionProvider

import org.mulesoft.als.suggestions.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.completionProviderInterfaces.IFSProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IEditorStateProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolverExt;
import org.mulesoft.als.suggestions.completionProviderInterfaces.Suggestion;
import org.mulesoft.als.suggestions.logger.LoggerIndex
import org.mulesoft.als.suggestions.completionProvider.CompletionRequest;
import org.mulesoft.als.suggestions.completionProvider.CompletionProvider;
import org.mulesoft.als.suggestions.completionProvider.ResolvedProvider;
import org.mulesoft.als.suggestions.completionProvider.ProviderBasedResolver;

class CompletionProvider {
  var contentProvider: IFSProvider = _
  var currentRequest: CompletionRequest = null
  var astProvider: IASTProvider = null
  var level: Int = 0
  def this(contentProvider: IFSProvider, astProvider: IASTProvider = null) = {
 (this.contentProvider=contentProvider)
 
}
  def suggest(request: CompletionRequest, doPostProcess: Boolean = false): Array[Any] = {
 var suggestions: Array[Any] = doSuggest( request, this )
return (if (doPostProcess) postProcess( suggestions, request ) else suggestions)
 
}
  def suggestAsync(request: CompletionRequest, doPostProcess: Boolean = false): Promise[Array[Any]] = {
 return doSuggestAsync( request, this ).then( (suggestions =>  (if (doPostProcess) postProcess( suggestions, request ) else suggestions)), (error =>  error) )
 
}
}
