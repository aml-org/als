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

class CompletionRequest {
  var content: IEditorStateProvider = _
  var prefixValue: String = _
  var async: Boolean = false
  var promises: Array[Promise[Array[Any]]] = _
  def this(content: IEditorStateProvider) = {
 (this.content=content)
 
}
  def prefix(): String = {
 if ((typeof(this.prefixValue)!=="undefined")) {
 return this.prefixValue
 
}
return getPrefix( this )
 
}
  def setPrefix(value: String): Unit = {
 (this.prefixValue=value)
 
}
  def valuePrefix(): String = {
 var offset = this.content.getOffset()
var text = this.content.getText()
{
var i = (offset-1)
while( (i>=0)) {
 {
 var c = text.charAt( i )
if (((((((((c==="\r")||(c==="\n"))||(c===" "))||(c==="\t"))||(c==="\""))||(c==="'"))||(c===":"))||(c==="("))) {
 return text.substring( (i+1), offset )
 
}
 
}
 (i-= 1)
}
}
return ""
 
}
}
