package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.Document
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.remote.{Mimes, Spec}
import amf.core.internal.utils.InflectorBase.Inflector
import org.mulesoft.als.actions.codeactions.TreeKnowledge
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.als.common.YamlUtils.isJson
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp, DialectImplicits}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.Range
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseElementDeclarableExtractors extends TreeKnowledge {

  private lazy val baseName: String =
    amfObject
      .flatMap(_.declarableKey(params.dialect))
      .map(_.singularize)
      .map(t => s"new$t")
      .getOrElse("newDeclaration")

  /**
    * Placeholder for the new name (key and reference)
    */
  protected def newName: String = ExtractorCommon.nameNotInList(baseName, params.bu.declaredNames.toSet)

  protected lazy val amfObject: Option[AmfObject] =
    extractAmfObject(maybeTree, params.dialect)

  /**
    * Selected object if there is a clean match in the range and it is a declarable, or the parents range
    */
  protected final def extractAmfObject(maybeTree: Option[ObjectInTree], dialect: Dialect): Option[AmfObject] =
    extractable(maybeTree.map(_.obj), dialect) orElse
      extractable(maybeTree.flatMap(_.stack.headOption), dialect)

  protected def extractable(maybeObject: Option[AmfObject], dialect: Dialect): Option[AmfObject] =
    maybeObject
      .filterNot(_.isInstanceOf[Document])
      .find(o => o.declarableKey(dialect).isDefined)

  /**
    * The original node with lexical info for the declared node
    */
  protected lazy val entryAst: Option[YPart] =
    amfObject.flatMap(_.annotations.ast()) match {
      case Some(entry: YMapEntry) => Some(entry.value)
      case c                      => c
    }

  /**
    * The original range info for the declared node
    */
  protected lazy val entryRange: Option[Range] =
    entryAst
      .map(_.range)
      .map(PositionRange(_))
      .map(LspRangeConverter.toLspRange)

  /**
    * The indentation for the existing node, as we already ensured it is a key, the first position gives de current indentation
    */
  protected lazy val entryIndentation: Int =
    yPartBranch.flatMap(_.parentEntry).map(_.range.columnFrom).getOrElse(0)

  protected def positionIsExtracted: Boolean =
    entryRange
      .map(n => PositionRange(n))
      .exists(r => position.exists(r.contains))

  protected lazy val sourceName: String =
    entryAst.map(_.sourceName).getOrElse(params.uri)

  /**
    * Fallback entry, should not be necessary as the link should be rendered
    */
  protected lazy val jsonRefEntry: YNode =
    YNode(
      YMap(
        IndexedSeq(YMapEntry(YNode("$ref"), YNode(s"$newName"))),
        sourceName
      ))

  /**
    * Render of the link generated by the new object
    */
  protected lazy val renderLink: Future[Option[YNode]] = Future.successful(None)

  protected lazy val spec: Spec =
    maybeTree.flatMap(_.objSpec) getOrElse params.bu.sourceSpec.getOrElse(Spec.AML)

  /**
    * The entry which holds the reference for the new declaration (`{"$ref": "declaration/$1"}`)
    */
  protected lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map { rl =>
      if (isJson(params.bu))
        entryRange.map(
          TextEdit(
            _,
            JsonRender.render(rl.getOrElse(jsonRefEntry), entryIndentation, jsonOptions)
          ))
      else if (params.dialect.isJsonStyle)
        entryRange.map(
          TextEdit(
            _,
            s"\n${YamlRender.render(rl.getOrElse(jsonRefEntry), entryIndentation, yamlOptions)}\n"
          ))
      else // default as raml style if none defined
        entryRange.map(TextEdit(_, s" ${rl.map(YamlRender.render(_, 0, yamlOptions).trim).getOrElse(newName)}\n"))
    }

  protected val jsonOptions: JsonRenderOptions = JsonRenderOptions().withIndentationSize(
    params.configuration
      .getFormatOptionForMime(Mimes.`application/json`)
      .tabSize
  )

  protected val yamlOptions: YamlRenderOptions = YamlRenderOptions().withIndentationSize(
    params.configuration
      .getFormatOptionForMime(Mimes.`application/yaml`)
      .tabSize
  )
}
