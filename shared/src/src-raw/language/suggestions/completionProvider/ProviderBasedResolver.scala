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

class ProviderBasedResolver extends FSResolverExt {
  def this(provider: IFSProvider) = {
}
  def content(fullPath: String): String = {
 return this.provider.content( fullPath )
 
}
  def contentAsync(fullPath: String): Promise[String] = {
 return this.provider.contentAsync( fullPath )
 
}
  def list(fullPath: String): Array[String] = {
 return this.provider.readDir( fullPath )
 
}
  def exists(fullPath: String): Boolean = {
 return this.provider.exists( fullPath )
 
}
  def dirname(fullPath: String): String = {
 return this.provider.dirName( fullPath )
 
}
  def resolve(contextPath: String, relativePath: String): String = {
 return this.provider.resolve( contextPath, relativePath )
 
}
  def extname(fullPath: String): String = {
 var lastDotIndex = fullPath.lastIndexOf( "." )
if (((lastDotIndex===(-1))||(lastDotIndex===(fullPath.length-1)))) {
 return null
 
}
return fullPath.substring( (lastDotIndex+1) )
 
}
  def isDirectory(fullPath: String): Boolean = {
 return this.provider.isDirectory( fullPath )
 
}
  def isDirectoryAsync(path: String): Promise[Boolean] = {
 return this.provider.isDirectoryAsync( path )
 
}
  def existsAsync(path: String): Promise[Boolean] = {
 return this.provider.existsAsync( path )
 
}
  def listAsync(path: String): Promise[Array[String]] = {
 return this.provider.readDirAsync( path )
 
}
}
