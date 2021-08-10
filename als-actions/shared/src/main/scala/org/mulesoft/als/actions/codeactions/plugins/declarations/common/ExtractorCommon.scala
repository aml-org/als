package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.remote.{Mimes, Vendor}
import amf.plugins.document.vocabularies.emitters.instances.AmlDomainElementEmitter
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.webapi.parser.spec.common.emitters.WebApiDomainElementEmitter
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.YamlUtils.isJson
import org.mulesoft.als.common.YamlWrapper.YNodeImplicits
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp}
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

import scala.annotation.tailrec

object ExtractorCommon {

  def existAnyOtherDeclaration(objs: Seq[AmfObject], bu: BaseUnit): Boolean =
    !bu.declarations.forall(objs.contains)

  def existAnyDeclaration(objs: Seq[AmfObject],
                          yPartBranch: Option[YPartBranch],
                          bu: BaseUnit,
                          dialect: Dialect): Seq[PositionRange] =
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
        } else objs.flatMap(_.annotations.ast().map(_.range))

    allRanges.map(PositionRange(_)).toSeq
  }

  /**
    * Emit a new domain element
    *
    * @param e DomainElement to be emitted
    * @return
    */
  def emitElement(e: DomainElement, vendor: Vendor, dialect: Dialect): YNode =
    if (vendor == Vendor.AML)
      AmlDomainElementEmitter
        .emit(e, dialect, UnhandledErrorHandler)
    else
      WebApiDomainElementEmitter
        .emit(e, vendor, UnhandledErrorHandler)

  /**
    * Emit declared element
    * @param amfObject
    * @return Element as YNode
    */
  def declaredElementNode(amfObject: Option[AmfObject], vendor: Vendor, dialect: Dialect): Option[YNode] =
    amfObject
      .collect {
        case e: DomainElement => emitElement(e, vendor, dialect)
      }

  /**
    * The complete node and the entry where it belongs, contemplating the path for the declaration and existing AST
    */
  def wrappedDeclaredEntry(amfObject: Option[AmfObject],
                           vendor: Vendor,
                           dialect: Dialect,
                           bu: BaseUnit,
                           uri: String,
                           newName: String): Option[(YNode, Option[YMapEntry])] =
    (declaredElementNode(amfObject, vendor, dialect), amfObject, dialect) match {
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

  def findExistingKeyPart(bu: BaseUnit, uri: String, keyPath: Seq[String]): Seq[YMapEntry] = {
    val maybePart = bu.references
      .find(_.location().contains(uri))
      .getOrElse(bu)
      .objWithAST
      .flatMap(_.annotations.ast())
    val entries = getExistingParts(maybePart, keyPath)
    entries
  }

  def declarationPath(fdp: AmfObject, dialect: Dialect): Seq[String] =
    Seq(fdp.declarableKey(dialect), declarationPathForDialect(dialect)).flatten

  def declarationPathForDialect(dialect: Dialect): Option[String] =
    dialect.documents().declarationsPath().option()

  /**
    * Secuential list for each node in the AST that already exists for the destiny
    *
    * @param maybePart
    * @param keys
    * @return
    */
  private def getExistingParts(maybePart: Option[YPart], keys: Seq[String]): Seq[YMapEntry] =
    maybePart match {
      case Some(n: YMap) => getExistingParts(YNode(n), keys.reverse, Seq.empty)
      case Some(d: YDocument) =>
        getExistingParts(d.node, keys.reverse, Seq.empty)
      case _ => Seq.empty
    }

  @tailrec
  private def getExistingParts(node: YNode, keys: Seq[String], acc: Seq[YMapEntry] = Seq.empty): Seq[YMapEntry] =
    keys match {
      case head :: _ =>
        node.value match {
          case m: YMap =>
            val maybeEntry = m.entries
              .find(_.key.asScalar.exists(_.text == head))
            maybeEntry match { // with match instead of map for tailrec optimization
              case Some(v) => getExistingParts(v.value, keys.tail, acc :+ v)
              case None    => acc
            }
          case _ => acc
        }
      case _ => acc
    }

  /**
    * Render for the new declaration, and the top entry on which it should be nested
    */
  def declaredEntry(amfObject: Option[AmfObject],
                    vendor: Vendor,
                    dialect: Dialect,
                    bu: BaseUnit,
                    uri: String,
                    newName: String,
                    configurationReader: AlsConfigurationReader,
                    jsonOptions: JsonRenderOptions,
                    yamlOptions: YamlRenderOptions): Option[(String, Option[YMapEntry])] = {
    val wrapped                        = wrappedDeclaredEntry(amfObject, vendor, dialect, bu, uri, newName)
    val maybeParent: Option[YMapEntry] = wrapped.flatMap(_._2)
    wrapped
      .map(_._1)
      .map { node =>
        renderNode(node, maybeParent, bu, configurationReader, jsonOptions, yamlOptions)
      }
  }

  def renderNode(node: YNode,
                 maybeParent: Option[YMapEntry],
                 bu: BaseUnit,
                 configurationReader: AlsConfigurationReader,
                 jsonOptions: JsonRenderOptions,
                 yamlOptions: YamlRenderOptions): (String, Option[YMapEntry]) = {
    if (isJson(bu)) {
      renderJson(configurationReader, jsonOptions, maybeParent, node)
    } else {
      val rendered = YamlRender
        .render(node, getIndentation(Mimes.`APPLICATION/YAML`, maybeParent, configurationReader), yamlOptions)
      (rendered, maybeParent)
    }
  }

  private def renderJson(configurationReader: AlsConfigurationReader,
                         jsonOptions: JsonRenderOptions,
                         maybeParent: Option[YMapEntry],
                         node: YNode) = {
    val toRender = node.value match {
      case m: YMap => m.entries.headOption.getOrElse(node)
      case _       => node
    }
    val rendered = JsonRender
      .render(toRender, getIndentation(Mimes.`APPLICATION/JSON`, maybeParent, configurationReader), jsonOptions)
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

  /**
    * Current indentation
    *
    * @param mime
    * @param maybeParent
    * @return
    */
  private def getIndentation(mime: String,
                             maybeParent: Option[YMapEntry],
                             configuration: AlsConfigurationReader): Int =
    maybeParent
      .map(
        _.key.range.columnFrom + configuration
          .getFormatOptionForMime(mime)
          .tabSize)
      .getOrElse(0)

  @tailrec
  def nameNotInList(baseName: String, existing: Set[String], c: Option[Int] = None): String = {
    val maybeName = s"$baseName${c.getOrElse("")}"
    if (existing.contains(maybeName))
      nameNotInList(baseName, existing, Some(c.getOrElse(0) + 1))
    else maybeName
  }
}
