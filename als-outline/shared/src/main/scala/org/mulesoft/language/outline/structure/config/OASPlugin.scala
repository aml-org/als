package org.mulesoft.language.outline.structure.config

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, Decorator, IASTProvider}
import org.mulesoft.language.outline.structure.structureDefault._
import org.mulesoft.language.outline.structure.structureDefaultInterfaces.Decoration

import scala.collection.mutable

class OASPlugin extends IStructurePlugin {

  override def accepts(provider: IASTProvider): Boolean = {
    provider.language.contains("OAS") || provider.language.contains("oas")
  }

  override def buildConfig(provider: IASTProvider): LanguageDependendStructureConfig = {

    val labelProvider = new DefaultLabelProvider()

    val categories = buildCategories().toMap

    val decorators = Seq(buildDecorator())

    val keyProvider = new DefaultKeyProvider()

    val visibilityFilter = new DefaultVisibilityFilter()

    val contentProvider = new DefaultContentProvider(visibilityFilter, labelProvider, keyProvider, decorators)

    LanguageDependendStructureConfig(
      labelProvider,
      contentProvider,
      categories,
      decorators,
      visibilityFilter
    )
  }

  def buildCategories(): mutable.Map[String, CategoryFilter] = {
    val result = new mutable.HashMap[String, CategoryFilter]()

    val resourcesCategoryFilter = new CategoryFilter {

      override def apply(node: IParseResult): Boolean = {

        val definition = node.asElement.get.definition
        if (node.isElement && definition.nameId.isDefined) {
          val defName = definition.nameId.get

          val result = defName == OASDefinitionKeys.PathItemObject ||
            defName == OASDefinitionKeys.PathsObject ||
            defName == OASDefinitionKeys.ParameterObject ||
            definition.isAssignableFrom(OASDefinitionKeys.ParameterDefinitionObject) ||
            defName == OASDefinitionKeys.Response ||
            defName == OASDefinitionKeys.ResponseDefinitionObject

          result
        } else {
          false
        }

      }
    }
    result("ResourcesCategory") = resourcesCategoryFilter

    val schemasCategoryFilter = new CategoryFilter {

      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {

        if (node.isElement && node.asElement.get.definition.nameId.isDefined) {
          val defName = node.asElement.get.definition.nameId.get

          defName == OASDefinitionKeys.SchemaObject ||
          defName == OASDefinitionKeys.ItemsObject ||
          defName == OASDefinitionKeys.DefinitionObject
        } else {
          false
        }

      }
    }
    result("SchemasAndTypesCategory") = schemasCategoryFilter

    result("ResourceTypesAndTraitsCategory") = (_: IParseResult) => false

    result("OtherCategory") = (node: IParseResult) => {

      !resourcesCategoryFilter.apply(node) && !schemasCategoryFilter.apply(node)
    }

    result

  }

  def buildDecorator(): Decorator = {
    val result = new DefaultOASDecorator()

    result.addDecoration(OASNodeTypes.ATTRIBUTE,
                         Decoration(
                           Icons.ARROW_SMALL_LEFT,
                           TextStyles.NORMAL
                         ))

    result.addDecoration(OASNodeTypes.PATH_ITEM,
                         Decoration(
                           Icons.PRIMITIVE_SQUARE,
                           TextStyles.HIGHLIGHT
                         ))

    result.addDecoration(OASNodeTypes.OPERATION_OBJECT,
                         Decoration(
                           Icons.PRIMITIVE_DOT,
                           TextStyles.WARNING
                         ))

    result.addDecoration(OASNodeTypes.DEFINITION_OBJECT,
                         Decoration(
                           Icons.FILE_SUBMODULE,
                           TextStyles.NORMAL
                         ))

    result.addDecoration(OASNodeTypes.SCHEMA_OBJECT,
                         Decoration(
                           Icons.FILE_BINARY,
                           TextStyles.SUCCESS
                         ))

    result.addDecoration(OASNodeTypes.ITEMS_OBJECT,
                         Decoration(
                           Icons.BOOK,
                           TextStyles.NORMAL
                         ))

    result
  }
}
