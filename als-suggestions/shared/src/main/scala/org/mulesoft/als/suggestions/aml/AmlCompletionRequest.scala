package org.mulesoft.als.suggestions.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import amf.core.client.scala.model.document.{BaseUnit, EncodesModel}
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.als.common.YPartASTWrapper.AlsYPart
import org.mulesoft.als.common.ASTElementWrapper._
import org.mulesoft.als.common._
import org.mulesoft.als.common.dtoTypes.{PositionRange, TextHelper, Position => DtoPosition}
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.styler.{SuggestionRender, SuggestionStylerBuilder}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.yaml.lexer.YamlToken
import org.yaml.model.YNode.MutRef
import org.yaml.model._

class AmlCompletionRequest(
    val baseUnit: BaseUnit,
    val position: DtoPosition,
    val actualDialect: Dialect,
    val directoryResolver: DirectoryResolver,
    val styler: SuggestionRender,
    val astPartBranch: ASTPartBranch,
    val configurationReader: AlsConfigurationReader,
    private val objectInTree: ObjectInTree,
    val inheritedProvider: Option[DeclarationProvider] = None,
    val rootUri: Option[String],
    val completionsPluginHandler: CompletionsPluginHandler,
    val alsConfigurationState: ALSConfigurationState
) {

  lazy val branchStack: Seq[AmfObject] = objectInTree.stack

  lazy val amfObject: AmfObject = objectInTree.obj

  val nodeDialect: Dialect =
    objectInTree
      .objSpec(alsConfigurationState.findSemanticByName)
      .flatMap(alsConfigurationState.definitionFor)
      .getOrElse(actualDialect)

  val currentNode: Option[NodeMapping] = DialectNodeFinder.find(objectInTree.obj, None, nodeDialect)

  private def entryAndMapping: Option[(FieldEntry, Boolean)] =
    objectInTree.fieldValue
      .map(fe => (fe, false))
      .orElse({
        FieldEntrySearcher(objectInTree.obj, currentNode, astPartBranch, actualDialect)
          .search(objectInTree.stack.headOption)
      })

  lazy val (fieldEntry: Option[FieldEntry], isKeyMapping: Boolean) = entryAndMapping match {
    case Some(value) => (Some(value._1), value._2)
    case None        => (None, false)
  }

  def prefix: String = styler.params.prefix

  val propertyMapping: List[PropertyMapping] = {
    val mappings: List[PropertyMapping] = currentNode match {
      case Some(nm: NodeMapping) =>
        PropertyMappingFilter(objectInTree, nodeDialect, nm).filter().toList
      case _ => Nil
    }

    fieldEntry match {
      case Some(e) =>
        val maybeMappings = mappings
          .find(pm =>
            pm.fields
              .fields()
              .exists(f => f.value.toString == e.field.value.iri())
          )
          .map(List(_))
        maybeMappings
          .getOrElse(mappings)
      case _ => mappings
    }
  }

  lazy val declarationProvider: DeclarationProvider =
    inheritedProvider.getOrElse(DeclarationProvider(baseUnit, Some(actualDialect)))
}

// todo: make instance
object AmlCompletionRequestBuilder {

  def build(
      baseUnit: BaseUnit,
      position: AmfPosition,
      dialect: Dialect,
      directoryResolver: DirectoryResolver,
      snippetSupport: Boolean,
      rootLocation: Option[String],
      configuration: AlsConfigurationReader,
      completionsPluginHandler: CompletionsPluginHandler,
      alsConfigurationState: ALSConfigurationState
  ): AmlCompletionRequest = {
    val partBranch: ASTPartBranch = {
      NodeBranchBuilder
        .build(baseUnit, position, strict = false)
    }

    val dtoPosition = DtoPosition(position)
    val styler = SuggestionStylerBuilder.build(
      !YamlUtils.isJson(baseUnit),
      prefix(partBranch, dtoPosition, baseUnit.raw.getOrElse("")),
      dtoPosition,
      partBranch,
      configuration,
      snippetSupport,
      baseUnit
        .location()
        .flatMap(alsConfigurationState.platform.extension)
        .flatMap(
          alsConfigurationState.platform.mimeFromExtension
        ), // should we use `yaml` as default? maybe check header for RAML?
      baseUnit.indentation(dtoPosition)
    )

    new AmlCompletionRequest(
      baseUnit,
      dtoPosition,
      dialect,
      directoryResolver,
      styler,
      partBranch,
      configuration,
      objInTree(baseUnit, dialect, partBranch),
      rootUri = rootLocation,
      completionsPluginHandler = completionsPluginHandler,
      alsConfigurationState = alsConfigurationState
    )
  }
  /*
      objInTree knowledge could be used in other features, if we start keeping track of every branch in a Unit it could
      be a nice idea to have general cache for a `(BaseUnit, position) -> lazy objectInTree branch` (an ObjectInTreeManager)
   */
  private def objInTree(baseUnit: BaseUnit, definedBy: Dialect, astPartBranch: ASTPartBranch): ObjectInTree = {
    val objectInTree = ObjectInTreeBuilder.fromUnit(baseUnit, baseUnit.identifier, definedBy, astPartBranch)
    objectInTree.obj match {
      case d: EncodesModel if d.fields.exists(DocumentModel.Encodes) =>
        ObjectInTree(d.encodes, Seq(objectInTree.obj) ++ objectInTree.stack, None, astPartBranch)
      case _ => objectInTree
    }
  }

  private def extractFromSeq(seq: YSequence, position: DtoPosition, yPartBranch: YPartBranch): String = {
    def findInner(yPart: YPart): YPart =
      yPart.children.find(p => p.contains(position.toAmfPosition, yPartBranch.strict)).getOrElse(yPart)
    val p = findInner(seq)
    p match {
      case non: YNonContent =>
        non.tokens
          .filterNot(_.tokenType == YamlToken.Indicator) // ignore `[]`
          .find(t => t.location.range.contains(position.toAmfPosition))
          .map { t =>
            val diff = position.column - t.location.columnFrom
            t.text.substring(0, Math.max(diff, 0)).trim
          }
          .getOrElse("")
      case _ => p.toString
    }
  }

  private def prefix(astBranch: ASTPartBranch, position: DtoPosition, content: String): String =
    astBranch.node match {
      case node: MutRef =>
        node.origValue.toString.substring(
          0,
          (0 max (position.column - node.origValue.range.columnFrom - {
            if (node.asScalar.exists(_.mark.plain)) 0 else 1 // if there is a quotation mark, adjust the range according
          })) min node.origValue.toString.length
        )
      case node: YNode =>
        if (PositionRange(node.tag.range).contains(position))
          node.tag.text
        else
          node.tagType match {
            case YType.Str =>
              val lines: Iterator[String] = node
                .as[String]
                .linesIterator
                .drop(position.line - node.range.lineFrom)
              if (lines.hasNext) {
                val next = lines.next()
                val diff = position.column - node.range.columnFrom - {
                  if (node.asScalar.exists(_.mark.plain)) 0 else 1
                } // if there is a quotation mark, adjust the range according
                if (diff > next.length) {
                  if (position.column - 1 == node.range.columnTo) next
                  else ""
                } // todo: should not be necessary, but `contains` with scalar values inside flow arrays are faulty
                else next.substring(0, Math.max(diff, 0))
              } else ""
            case YType.Bool =>
              val line = node.asScalar.map(_.text).getOrElse("")
              val diff = position.column - node.range.columnFrom
              line.substring(0, Math.max(diff, 0))
            case _ => ""
          }
      case s: YSequence =>
        extractFromSeq(s, position, astBranch.asInstanceOf[YPartBranch])
      case _ =>
        val line        = TextHelper.linesWithSeparators(content)(position.line)
        val textContent = line.substring(0, position.toAmfPosition.column)
        // add trailing space to avoid checking if it ends with `{, [, `, "`
        s"$textContent ".split(Array('{', '[', ''', '"')).lastOption.getOrElse("").trim
    }

  def forElement(
      element: DomainElement,
      current: DomainElement,
      filterProvider: DeclarationProvider,
      parent: AmlCompletionRequest,
      ignoredPlugins: Set[AMLCompletionPlugin]
  ): AmlCompletionRequest = {
    val currentIndex = parent.branchStack.indexOf(current) + 1
    val newStack: Seq[AmfObject] =
      if (currentIndex < parent.branchStack.length) parent.branchStack.splitAt(currentIndex)._2 else parent.branchStack
    val objectInTree =
      ObjectInTreeBuilder.fromSubTree(
        element,
        parent.baseUnit.identifier,
        newStack,
        parent.actualDialect,
        parent.astPartBranch
      )
    new AmlCompletionRequest(
      parent.baseUnit,
      parent.position,
      parent.actualDialect,
      parent.directoryResolver,
      parent.styler,
      parent.astPartBranch,
      parent.configurationReader,
      objectInTree,
      Some(filterProvider),
      parent.rootUri,
      parent.completionsPluginHandler.filter(ignoredPlugins),
      parent.alsConfigurationState
    )
  }
}
