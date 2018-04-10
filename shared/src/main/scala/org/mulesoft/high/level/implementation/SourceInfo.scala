package org.mulesoft.high.level.implementation

import org.mulesoft.high.level.interfaces.{IASTUnit, IProject, ISourceInfo}
import org.mulesoft.positioning.{IPositionsMapper, YamlLocation}
import org.mulesoft.typesystem.json.interfaces.NodeRange
import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.lexer.YamlToken._
import org.yaml.model.YNode.MutRef
import org.yaml.model._

class SourceInfo private extends ISourceInfo {

    var _ranges:Seq[NodeRange] = Seq()
    var _yamlSources: Seq[YPart] = Seq()
    var _positionsMapper:Option[IPositionsMapper] = None
    var _content: Option[String] = None
    var _isInitialized:Boolean = false
    var _referingUnit: Option[IASTUnit] = None
    var _includePathLabel: Option[String] = None
    var _externalLocationPath: Option[String] = None

    override def content: Option[String] = _content

    override def yamlSources: Seq[YPart] = _yamlSources

    override def offset: Option[Int] = {
        if(isEmpty){
            None
        }
        else if(_ranges.lengthCompare(1)==0){
            var start = _ranges.head.start.position
            Some(_positionsMapper.get.offset(start))
        }
        else {
            None
        }
    }

    override def valueOffset: Option[Int] = {
        if(isEmpty){
            None
        }
        else if(_ranges.lengthCompare(1)==0){
            var yPartOPt:Option[YPart] = _yamlSources.head match {
                case me:YMapEntry => Some(me.value.value)
                case n:YNode => Some(n.value)
                case s:YValue => Some(s)
            }
            yPartOPt match {
                case Some(yPart) =>
                    var result:Option[Int] = None
                    var yChildren = yPart.children
                    if(yChildren.nonEmpty && yChildren.head.isInstanceOf[YNonContent]){
                        yChildren.head.asInstanceOf[YNonContent].tokens.find(x=>{
                            if(x.tokenType==Indent){
                                result = Some(x.text.length)
                                true
                            }
                            else false
                        })
                    }
                    if(result.isEmpty) {
                        yPart match {
                            case scalar: YScalar =>
                                var v = scalar.value
                                if (Option(v).isEmpty || v.toString.isEmpty) {
                                    result = offset.map(_ + SourceInfo.YAML_INDENT)
                                }
                            case _ =>
                        }
                        if (result.isEmpty) {
                            var r = YRange(yPart)
                            _positionsMapper.get.initRange(r)
                            var start = r.start.position
                            result = Some(_positionsMapper.get.offset(start))
                        }
                    }
                    result
                case None => None
            }

        }
        else {
            None
        }
    }

    def withSources(sources:Seq[YPart]):SourceInfo = {
        _yamlSources = sources
        this
    }

    def init(project:IProject,referingUnit:Option[IASTUnit],inExternalFile:Option[String] = None): Unit ={

        if(_yamlSources.isEmpty){
            _ranges = List(YRange.empty)
        }
        else{
            _ranges = _yamlSources.map(YRange(_))
        }
        _positionsMapper = referingUnit.map(_.positionsMapper)
        referingUnit.foreach(withReferingUnit)

        if(_yamlSources.lengthCompare(1)==0){
            var yl = YamlLocation(_yamlSources.head,_positionsMapper)
            yl.node.flatMap(_.yPart match {
                case mr:MutRef => Some(mr)
                case _ => None
            }).flatMap(_.origValue match {
                case scalar:YScalar => Option(scalar.value)
                case _ => None
            }).foreach(includePathObj => {
                var includePath = includePathObj.toString
                withIncludePathLabel(includePath)
                var ownPath = referingUnit match {
                    case Some(u) => Some(u.path)
                    case _ => inExternalFile
                }
                ownPath.flatMap(x=>project.resolvePath(x,includePath)).foreach(withExternalLocationPath)
            })
        }
        _positionsMapper.foreach(pm=>_ranges.foreach(pm.initRange))
        _isInitialized = true
    }

    override def ranges: Seq[NodeRange] = _ranges

    override def isInitialized: Boolean = _isInitialized

    override def isEmpty: Boolean = !_isInitialized

    override def referingUnit: Option[IASTUnit] = _referingUnit

    override def includePathLabel: Option[String] = _includePathLabel

    override def externalLocationPath: Option[String] = _externalLocationPath

    def withReferingUnit(unit:IASTUnit):SourceInfo = {
        _referingUnit = Option(unit)
        this
    }

    def withIncludePathLabel(ip:String):SourceInfo = {
        _includePathLabel = Option(ip)
        this
    }

    def withExternalLocationPath(ip:String):SourceInfo = {
        _externalLocationPath = Option(ip)
        this
    }

    override def isYAML: Boolean = content.isDefined && !content.get.trim.startsWith("{")
}

object SourceInfo {

    val YAML_INDENT:Int = 2

    def apply():SourceInfo = new SourceInfo()
}
