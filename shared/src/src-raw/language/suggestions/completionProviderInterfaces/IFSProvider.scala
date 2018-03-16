package org.mulesoft.als.suggestions.completionProviderInterfaces

import org.mulesoft.als.suggestions.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.completionProviderInterfaces.IEditorStateProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IFSProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.Suggestion;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolver;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolverExt;

trait IFSProvider {
  def content(fullPath: String): String
  def contentAsync(fullPath: String): Promise[String]
  def contentDirName(content: IEditorStateProvider): String
  def dirName(fullPath: String): String
  def exists(fullPath: String): Boolean
  def existsAsync(path: String): Promise[Boolean]
  def resolve(contextPath: String, relativePath: String): String
  def isDirectory(fullPath: String): Boolean
  def readDir(fullPath: String): Array[String]
  def readDirAsync(path: String): Promise[Array[String]]
  def isDirectoryAsync(path: String): Promise[Boolean]
}
