package org.mulesoft.als.suggestions.aml

import amf.core.annotations.{LexicalInformation, SourceAST, SynthesizedField}
import amf.core.model.document.{BaseUnit, Document}
import amf.core.model.domain.{AmfArray, AmfObject, DomainElement}
import amf.core.parser
import amf.core.parser.FieldEntry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common._
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.model.{YDocument, YNode, YType}
class AmlCompletionRequest(val baseUnit: BaseUnit,
                           val position: Position,
                           val actualDialect: Dialect,
                           val platform: Platform,
                           val directoryResolver: DirectoryResolver,
                           val styler: Boolean => Seq[Suggestion] => Seq[Suggestion],
                           val yPartBranch: YPartBranch,
                           private val objectInTree: ObjectInTree,
                           val inheritedProvider: Option[DeclarationProvider] = None) {

  lazy val branchStack: Seq[AmfObject] = objectInTree.stack

  lazy val amfObject: AmfObject = objectInTree.obj

  val prefix: String = {
    yPartBranch.node match {
      case node: YNode =>
        node.tagType match {
          case YType.Str =>
            val lines: Iterator[String] = node
              .as[String]
              .lines
              .drop(position.line - node.range.lineFrom)
            if (lines.hasNext)
              lines
                .next()
                .substring(0, position.column - node.range.columnFrom - {
                  if (node.asScalar.exists(_.mark.plain)) 0 else 1 // if there is a quotation mark, adjust the range according
                })
            else ""
          case YType.Include =>
            node match {
              case mr: YNode.MutRef if mr.origTag.tagType == YType.Include => mr.origValue.toString
              case _                                                       => ""
            }
          case _ => ""
        }
      case _ => ""
    }
  }

  lazy val fieldEntry: Option[FieldEntry] = { // todo: maybe this should be a seq and not an option
    objectInTree.obj.fields
      .fields()
      .filter(f =>
        f.value.value match {
          case _: AmfArray =>
            f.value.annotations
              .find(classOf[LexicalInformation])
              .exists(_.containsCompletely(position))
          case v =>
            v.position()
              .exists(_.contains(position)) && (f.value.annotations
              .find(classOf[LexicalInformation])
              .forall(_.containsCompletely(position)) && !f.value.value.annotations
              .contains(classOf[SynthesizedField]))
      })
      .toList
      .sorted(FieldEntryOrdering)
      .lastOption
  }

  private def parentTermKey(): Seq[PropertyMapping] =
    objectInTree.stack.headOption
      .flatMap(getDialectNode(_, None))
      .collectFirst({ case n: NodeMapping => n })
      .map(_.propertiesMapping())
      .getOrElse(Nil)
      .filter(p => p.mapTermKeyProperty().option().isDefined)

  val propertyMapping: List[PropertyMapping] = {
    val parentMappins = parentTermKey()

    val mappings = getDialectNode(objectInTree.obj, fieldEntry) match {
      case Some(nm: NodeMapping) =>
        val terms = parentMappins
          .find(pr => pr.objectRange().exists(or => or.value() == nm.id))
          .map(p => Seq(p.mapTermKeyProperty().option(), p.mapTermValueProperty().option()).flatten)
          .getOrElse(Nil)

        (if (terms.nonEmpty)
           nm.propertiesMapping().filter(p => !p.nodePropertyMapping().option().exists(terms.contains))
         else nm.propertiesMapping()).toList
      case _ => Nil
    }
    fieldEntry match {
      case Some(e) =>
        if (e.value.value
              .position()
              .exists(li => li.contains(position))) {
          val maybeMappings = mappings
            .find(
              pm =>
                pm.fields
                  .fields()
                  .exists(f => f.value.toString == e.field.value.iri()))
            .map(List(_))
          maybeMappings
            .getOrElse(mappings)
        } else mappings
      case _ => mappings
    }
  }

  private def getDialectNode(amfObject: AmfObject, fieldEntry: Option[FieldEntry]): Option[DomainElement] =
    amfObject.meta.`type`.flatMap { v =>
      actualDialect.declares.find {
        case s: NodeMapping =>
          s.nodetypeMapping.value() == v.iri() &&
            fieldEntry.forall(f => {
              s.propertiesMapping()
                .find(
                  pm =>
                    pm.fields
                      .fields()
                      .exists(_.value.toString == f.field.value.iri()))
                .exists(_.mapTermKeyProperty().isNullOrEmpty)
            })
        case _ => false
      }
    }.headOption

  lazy val declarationProvider: DeclarationProvider = {
    inheritedProvider.getOrElse(DeclarationProvider(baseUnit, Some(actualDialect)))
  }

  lazy val indentation: String =
    (if (yPartBranch.isKey) "\n" else "") + baseUnit.raw
      .flatMap(text => {
        val pos  = position.moveLine(-1)
        val left = text.substring(0, pos.offset(text))
        val line = if (left.contains("\n")) left.substring(left.lastIndexOf("\n")).stripPrefix("\n") else left
        val first = line.headOption match {
          case Some(c) if c == ' ' || c == '\t' => Some(c)
          case _                                => None
        }
        first.map(f => {
          val spaces = line.substring(0, line.takeWhile(_ == f).length)
          if (f == '\t') s"$spaces\t"
          else s"$spaces  "
        })
      })
      .getOrElse("  ")
}

object AmlCompletionRequestBuilder {

  def build(baseUnit: BaseUnit,
            position: Position,
            dialect: Dialect,
            platform: Platform,
            directoryResolver: DirectoryResolver,
            styler: Boolean => Seq[Suggestion] => Seq[Suggestion]): AmlCompletionRequest = {
    val yPartBranch: YPartBranch = {
      val ast = baseUnit match {
        case d: Document =>
          d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
        case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
      }

      NodeBranchBuilder.build(ast.getOrElse(YDocument(IndexedSeq.empty, "")), position)
    }

    val objectInTree = ObjectInTreeBuilder.fromUnit(baseUnit, position)
    new AmlCompletionRequest(baseUnit,
                             position,
                             dialect,
                             platform,
                             directoryResolver,
                             styler,
                             yPartBranch,
                             objectInTree)
  }

  def forElement(element: DomainElement,
                 filterProvider: DeclarationProvider,
                 parent: AmlCompletionRequest): AmlCompletionRequest = {

    val objectInTree = ObjectInTreeBuilder.fromSubTree(
      element,
      parent.position,
      parent.branchStack.splitAt(parent.branchStack.indexOf(element))._2)
    new AmlCompletionRequest(
      parent.baseUnit,
      parent.position,
      parent.actualDialect,
      parent.platform,
      parent.directoryResolver,
      parent.styler,
      parent.yPartBranch,
      objectInTree,
      Some(filterProvider)
    )
  }
}
