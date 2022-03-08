package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.internal.remote.Mimes
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.YamlUtils.isJson
import org.mulesoft.als.common.ASTWrapper.YNodeImplicits
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.declarations.DeclarationCreator
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp}
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

object ExtractorCommon extends DeclarationCreator {

  def existAnyOtherDeclaration(objs: Seq[AmfObject], bu: BaseUnit): Boolean =
    !bu.declarations.forall(objs.contains)

  def existAnyDeclaration(
      objs: Seq[AmfObject],
      yPartBranch: Option[YPartBranch],
      bu: BaseUnit,
      dialect: Dialect
  ): Seq[PositionRange] =
    if (!existAnyOtherDeclaration(objs, bu))
      deleteAll(objs, yPartBranch, bu, dialect)
    else deleteDeclarationGroup(objs, bu, dialect)

  private def deleteAll(objs: Seq[AmfObject], yPartBranch: Option[YPartBranch], bu: BaseUnit, dialect: Dialect) =
    declarationPathForDialect(dialect) match {
      case Some(d) =>
        val flatten: Option[YMapEntry] = yPartBranch
          .map(_.stack)
          .getOrElse(Nil)
          .reverse
          .collectFirst({ case m: YMap => m.entries.find(_.key.asScalar.exists(_.text == d)) })
          .flatten
        flatten.map(_.range).map(PositionRange(_)).toSeq
      case _ => deleteDeclarationGroup(objs, bu, dialect)
    }

  private def deleteDeclarationGroup(objs: Seq[AmfObject], bu: BaseUnit, dialect: Dialect): Seq[PositionRange] = {
    val objsByKey: Map[Option[String], Seq[AmfObject]] = objs.groupBy(_.declarableKey(dialect))
    val allRanges =
      if (!objsByKey.keySet.contains(None))
        objsByKey.iterator.flatMap { t =>
          val objsInDk = t._2
          t._1
            .flatMap(dk => bu.declarationKeys.find(_.entry.key.asScalar.exists(_.text == dk)))
            .flatMap { dk =>
              if (dk.entry.value.as[YMap].entries.size <= objsInDk.size) Some(Seq(dk.entry.range))
              else None
            }
            .getOrElse(objsInDk.flatMap(_.annotations.ast().map(_.range)))
        }
      else objs.flatMap(_.annotations.ast().map(_.range))

    allRanges.map(PositionRange(_)).toSeq
  }

  /** Emit declared element
    * @param amfObject
    * @return
    *   Element as YNode
    */
  def declaredElementNode(
      amfObject: Option[AmfObject],
      dialect: Dialect,
      alsConfigurationState: ALSConfigurationState
  ): Option[YNode] =
    amfObject
      .collect { case e: DomainElement =>
        alsConfigurationState.configForDialect(dialect).emit(e)
      }

  /** The complete node and the entry where it belongs, contemplating the path for the declaration and existing AST
    */
  def wrappedDeclaredEntry(
      amfObject: Option[AmfObject],
      dialect: Dialect,
      bu: BaseUnit,
      uri: String,
      newName: String,
      alsConfigurationState: ALSConfigurationState
  ): Option[(YNode, Option[YMapEntry])] =
    (declaredElementNode(amfObject, dialect, alsConfigurationState), amfObject, dialect) match {
      case (Some(den), Some(fdp), dialect) =>
        var fullPath                = den.withKey(newName)
        val keyPath                 = declarationPath(fdp, dialect)
        val entries: Seq[YMapEntry] = findExistingKeyPart(bu, uri, keyPath)
        keyPath
          .dropRight(entries.size)
          .foreach(k => fullPath = fullPath.withKey(k))
        Some(fullPath, entries.lastOption)
      case _ => None
    }

  /** Render for the new declaration, and the top entry on which it should be nested
    */
  def declaredEntry(
      amfObject: Option[AmfObject],
      dialect: Dialect,
      bu: BaseUnit,
      uri: String,
      newName: String,
      configurationReader: AlsConfigurationReader,
      jsonOptions: JsonRenderOptions,
      yamlOptions: YamlRenderOptions,
      alsConfigurationState: ALSConfigurationState
  ): Option[(String, Option[YMapEntry])] = {
    val wrapped = wrappedDeclaredEntry(amfObject, dialect, bu, uri, newName, alsConfigurationState)
    val maybeParent: Option[YMapEntry] = wrapped.flatMap(_._2)
    wrapped
      .map(_._1)
      .map { node =>
        renderNode(node, maybeParent, bu, configurationReader, jsonOptions, yamlOptions)
      }
  }

  def renderNode(
      node: YNode,
      maybeParent: Option[YMapEntry],
      bu: BaseUnit,
      configurationReader: AlsConfigurationReader,
      jsonOptions: JsonRenderOptions,
      yamlOptions: YamlRenderOptions
  ): (String, Option[YMapEntry]) = {
    if (isJson(bu)) {
      renderJson(configurationReader, jsonOptions, maybeParent, node)
    } else {
      val rendered = YamlRender
        .render(node, getIndentation(Mimes.`application/yaml`, maybeParent, configurationReader), yamlOptions)
      (rendered, maybeParent)
    }
  }

  private def renderJson(
      configurationReader: AlsConfigurationReader,
      jsonOptions: JsonRenderOptions,
      maybeParent: Option[YMapEntry],
      node: YNode
  ) = {
    val toRender = node.value match {
      case m: YMap => m.entries.headOption.getOrElse(node)
      case _       => node
    }
    val rendered = JsonRender
      .render(toRender, getIndentation(Mimes.`application/json`, maybeParent, configurationReader), jsonOptions)
    val renderedChild = maybeParent
      .map(_.value.value)
      .collect {
        case m: YMap if m.entries.nonEmpty =>
          s",$rendered" // if I'm not the only son, I put a comma to separate from siblings
        case m: YMap if m.entries.isEmpty => rendered // single child, no comma needed
      }
      .getOrElse(s"$rendered,") // if I can't ensure I will be the last son, then I fallback with a trailing comma
    (renderedChild, maybeParent)
  }

  /** Current indentation
    *
    * @param mime
    * @param maybeParent
    * @return
    */
  private def getIndentation(mime: String, maybeParent: Option[YMapEntry], configuration: AlsConfigurationReader): Int =
    maybeParent
      .map(
        _.key.range.columnFrom + configuration
          .getFormatOptionForMime(mime)
          .tabSize
      )
      .getOrElse(0)
}
