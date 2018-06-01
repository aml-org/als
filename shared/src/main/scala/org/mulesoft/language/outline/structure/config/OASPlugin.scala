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

    val contentProvider = new DefaultContentProvider(
      visibilityFilter, labelProvider, keyProvider, decorators)


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

        if(node.isElement && node.asElement.get.definition.nameId.isDefined){
          val defName = node.asElement.get.definition.nameId.get

          defName == OASDefinitionKeys.PathItemObject ||
            defName == OASDefinitionKeys.ParameterObject ||
            defName == OASDefinitionKeys.ParameterDefinitionObject ||
            defName == OASDefinitionKeys.Response ||
            defName == OASDefinitionKeys.ResponseDefinitionObject
        }

        false

      }
    }
    result("Resources") = resourcesCategoryFilter

    val schemasCategoryFilter = new CategoryFilter {
      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {

        if (node.isElement && node.asElement.get.property.isDefined &&
          node.asElement.get.property.get.nameId.isDefined) {

          val propertyName = node.asElement.get.property.get.nameId.get

          propertyName == "schemas" ||
            propertyName == "types"
        } else {

          false
        }
      }
    }
    result("Schemas & Types") = schemasCategoryFilter

    result("Resource Types & Traits") = new CategoryFilter {
      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {
        false
      }
    }

    result("Other") = new CategoryFilter {
      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {

        !resourcesCategoryFilter.apply(node) && !schemasCategoryFilter.apply(node)
      }
    }

    result

  }

  def buildDecorator(): Decorator = {
    val result = new DefaultOASDecorator()

    result.addDecoration(OASNodeTypes.ATTRIBUTE, Decoration(
      Icons.ARROW_SMALL_LEFT,
      TextStyles.NORMAL
    ))

    result.addDecoration(OASNodeTypes.PATH_ITEM, Decoration(
      Icons.PRIMITIVE_SQUARE,
      TextStyles.HIGHLIGHT
    ))

    result.addDecoration(OASNodeTypes.OPERATION_OBJECT, Decoration(
      Icons.PRIMITIVE_DOT,
      TextStyles.WARNING
    ))

    result.addDecoration(OASNodeTypes.DEFINITION_OBJECT, Decoration(
      Icons.FILE_SUBMODULE,
      TextStyles.NORMAL
    ))

    result.addDecoration(OASNodeTypes.SCHEMA_OBJECT, Decoration(
      Icons.FILE_BINARY,
      TextStyles.SUCCESS
    ))

    result.addDecoration(OASNodeTypes.ITEMS_OBJECT, Decoration(
      Icons.BOOK,
      TextStyles.NORMAL
    ))

    result
  }
}
