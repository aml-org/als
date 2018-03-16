package org.mulesoft.als.suggestions.completionProviderInterfaces

import org.mulesoft.als.suggestions.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.completionProviderInterfaces.IEditorStateProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IFSProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.Suggestion;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolver;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolverExt;

trait FSResolverExt extends FSResolver {
  def list(fullPath: String): Array[String]
  def exists(fullPath: String): Boolean
  def dirname(fullPath: String): String
  def resolve(contextPath: String, relativePath: String): String
  def extname(fullPath: String): String
  def isDirectory(fullPath: String): Boolean
  def isDirectoryAsync(path: String): Promise[Boolean]
  def existsAsync(path: String): Promise[Boolean]
  def listAsync(path: String): Promise[Array[String]]
}
