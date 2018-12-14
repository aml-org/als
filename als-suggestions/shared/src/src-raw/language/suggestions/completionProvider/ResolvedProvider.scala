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

class ResolvedProvider extends IFSProvider {
  var fsResolver: FSResolverExt = _
  def this(resolver: FSResolverExt) = {
 (this.fsResolver=resolver)
 
}
  def content(fullPath: String): String = {
 return this.resolver.content( fullPath )
 
}
  def contentAsync(fullPath: String): Promise[String] = {
 return this.resolver.contentAsync( fullPath )
 
}
  def contentDirName(content: IEditorStateProvider): String = {
 return this.resolver.dirname( content.getPath() )
 
}
  def dirName(path: String): String = {
 return this.resolver.dirname( path )
 
}
  def exists(path: String): Boolean = {
 return this.resolver.exists( path )
 
}
  def resolve(contextPath: String, relativePath: String): String = {
 return this.resolver.resolve( contextPath, relativePath )
 
}
  def isDirectory(path: String): Boolean = {
 return this.resolver.isDirectory( path )
 
}
  def isDirectoryAsync(path: String): Promise[Boolean] = {
 return this.resolver.isDirectoryAsync( path )
 
}
  def readDir(path: String): Array[String] = {
 return this.resolver.list( path )
 
}
  def existsAsync(path: String): Promise[Boolean] = {
 return this.resolver.existsAsync( path )
 
}
  def readDirAsync(path: String): Promise[Array[String]] = {
 return this.resolver.listAsync( path )
 
}
}
