package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.positioning.YamlLocation
import org.yaml.model.YPart

class CompletionRequest(
                       _kind:CompletionRequestKind,
                       _prefix:String,
                       _position:Int,
                       _config:ICompletionConfig,
                       private var _astNode:Option[IParseResult],
                       ) extends ICompletionRequest{

    var _yamlLocation:Option[YamlLocation] = None

    var _actualYamlLocation:Option[YamlLocation] = None

    override def kind: CompletionRequestKind = _kind

    override def prefix: String = _prefix

    override def position: Int = _position

    override def config: ICompletionConfig = _config

    override def astNode: Option[IParseResult] = _astNode

    override def yamlLocation: Option[YamlLocation] = _yamlLocation

    override def actualYamlLocation: Option[YamlLocation] = _actualYamlLocation

    def withAstNode(n:IParseResult):CompletionRequest = withAstNode(Option(n))

    def withAstNode(opt:Option[IParseResult]):CompletionRequest = {
        _astNode = opt
        this
    }

    def withYamlLocation(n:YamlLocation):CompletionRequest = withYamlLocation(Option(n))

    def withYamlLocation(opt:Option[YamlLocation]):CompletionRequest = {
        _yamlLocation = opt
        this
    }

    def withActualYamlLocation(n:YamlLocation):CompletionRequest = withActualYamlLocation(Option(n))

    def withActualYamlLocation(opt:Option[YamlLocation]):CompletionRequest = {
        _actualYamlLocation = opt
        this
    }
}

object CompletionRequest {

    def apply(
                 _kind:CompletionRequestKind,
                 _prefix:String,
                 _position:Int,
                 _config:ICompletionConfig,
                 _astNode:Option[IParseResult] = None
             ):CompletionRequest = new CompletionRequest(_kind,_prefix,_position,_config,_astNode)
}
