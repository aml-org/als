package org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml

import amf.core.model.domain.AmfObject
import amf.core.remote.Mimes
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.Range
import org.yaml.model.{YMap, YMapEntry, YNode, YPart, YType}
import org.yaml.render.{YamlRender, YamlRenderOptions}

import scala.annotation.tailrec

object RamlTypeExtractor {

  /**
    *
    * @param entryRange replace range (the whole declarable node)
    * @param renderLink node which contains the rendered inclusion
    * @param entryAst the whole entry (from the declarable amfObject)
    * @param yPartBranch the most specific branch for the selected range
    * @param amfObject the selected declarable amfObject
    * @param configurationReader
    * @param newName
    * @param yamlOptions
    * @return
    */
  def linkEntry(entryRange: Option[Range],
                renderLink: Option[YNode],
                entryAst: Option[YPart],
                yPartBranch: Option[YPartBranch],
                amfObject: Option[AmfObject],
                configurationReader: AlsConfigurationReader,
                newName: String,
                yamlOptions: YamlRenderOptions): Option[TextEdit] =
    entryRange.map(TextEdit(
      _,
      s"\n${renderLink
        .map(YamlRender.render(_, entryIndentation(entryAst, yPartBranch, amfObject, configurationReader), yamlOptions))
        .getOrElse(newName)}\n"
    ))

  protected def entryIndentation(entryAst: Option[YPart],
                                 yPartBranch: Option[YPartBranch],
                                 amfObject: Option[AmfObject],
                                 configurationReader: AlsConfigurationReader): Int =
    getActualIndentation(entryAst, yPartBranch) + indentIfNecessary(amfObject, configurationReader)

  private def indentationSize(configurationReader: AlsConfigurationReader): Int =
    configurationReader.getFormatOptionForMime(Mimes.`APPLICATION/YAML`).tabSize

  /**
    * cases for inlined types
    * @return
    */
  private def indentIfNecessary(amfObject: Option[AmfObject], configurationReader: AlsConfigurationReader): Int =
    amfObject
      .map(_.annotations.ast() match {
        case Some(e: YMapEntry) if e.value.tagType != YType.Map => indentationSize(configurationReader)
        case _                                                  => 0
      })
      .getOrElse(0)

  /**
    * If its an entry check the start position for key, else check if I can get a close entry, else check my parent entry
    */
  @tailrec
  private final def getActualIndentation(p: Option[YPart], yPartBranch: Option[YPartBranch]): Int =
    p match {
      case Some(e: YMapEntry) => e.key.range.columnFrom
      case Some(n: YNode)     => getActualIndentation(Some(n.value), yPartBranch)
      case Some(m: YMap)      => getActualIndentation(m.entries.headOption, yPartBranch)
      case Some(_)            => getActualIndentation(yPartBranch.flatMap(_.parentEntry), yPartBranch)
      case _                  => 0
    }
}
