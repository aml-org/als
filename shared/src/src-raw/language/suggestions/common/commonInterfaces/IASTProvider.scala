package org.mulesoft.als.suggestions.common.commonInterfaces

import org.mulesoft.als.suggestions.common.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.common.commonInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.common.commonInterfaces.Decorator;
import org.mulesoft.als.suggestions.common.commonInterfaces.LabelProvider;
import org.mulesoft.als.suggestions.common.commonInterfaces.VisibilityFilter;
import org.mulesoft.als.suggestions.common.commonInterfaces.CategoryFilter;
import org.mulesoft.als.suggestions.common.commonInterfaces.KeyProvider;
import org.mulesoft.als.suggestions.common.commonInterfaces.IPoint;
import org.mulesoft.als.suggestions.common.commonInterfaces.IRange;
import org.mulesoft.als.suggestions.common.commonInterfaces.IEditorTextBuffer;
import org.mulesoft.als.suggestions.common.commonInterfaces.IAbstractTextEditor;
import org.mulesoft.als.suggestions.common.commonInterfaces.IEditorProvider;
import org.mulesoft.als.suggestions.common.commonInterfaces.ITextEdit;
import org.mulesoft.als.suggestions.common.commonInterfaces.IChangedDocument;

trait IASTProvider {
  def getASTRoot(): IHighLevelNode
  def getSelectedNode(): IParseResult
}
