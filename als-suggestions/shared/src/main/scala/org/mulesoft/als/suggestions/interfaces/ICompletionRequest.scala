package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.positioning.YamlLocation

trait ICompletionRequest {
    def kind: LocationKind;
	
    def prefix: String;
	
    def position: Int;
	
    def config: ICompletionConfig;
	
    def astNode: Option[IParseResult];
	
    def yamlLocation: Option[YamlLocation];
	
    def actualYamlLocation: Option[YamlLocation];
	
    def currentIndent: String;
	
	def indentCount: Int;
}
