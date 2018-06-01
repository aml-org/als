package org.mulesoft.language.outline.structure.config
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, Decorator, IASTProvider}
import org.mulesoft.language.outline.structure.structureDefault._

import scala.collection.mutable
import org.mulesoft.language.outline.structure.structureDefaultInterfaces.Decoration

class RAMLPlugin extends IStructurePlugin {

  override def accepts(provider: IASTProvider): Boolean = {
    provider.language.contains("RAML") || provider.language.contains("raml")
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

    result("Resources") = new CategoryFilter {

      override def apply(node: IParseResult): Boolean = {

        node.isElement && node.asElement.get.definition.nameId.get == RamlDefinitionKeys.RESOURCE
      }
    }

    result("Schemas & Types") = new CategoryFilter {
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

    result("Resource Types & Traits") = new CategoryFilter {
      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {
        node.isElement && (
          node.asElement.get.definition.nameId.get == RamlDefinitionKeys.RESOURCE_TYPE ||
            node.asElement.get.definition.nameId.get == RamlDefinitionKeys.TRAIT)
      }
    }

    result("Other") = new CategoryFilter {
      /**
        * Checks whether current node is applicable to a category
        */
      override def apply(node: IParseResult): Boolean = {
        if (node.isElement && node.asElement.get.property.isDefined &&
          node.asElement.get.property.get.nameId.isDefined) {

          val propertyName = node.asElement.get.property.get.nameId.get

          propertyName != "resources" &&
            propertyName != "schemas" &&
            propertyName != "types" &&
            propertyName != "resourceTypes" &&
            propertyName != "traits"
        } else {

          false
        }

      }
    }

    result

  }

  def buildDecorator(): Decorator = {
    val result = new DefaultRAMLDecorator()

    result.addDecoration(RamlNodeTypes.ATTRIBUTE, Decoration(
      Icons.ARROW_SMALL_LEFT,
      TextStyles.NORMAL
    ))

    result.addDecoration(RamlNodeTypes.RESOURCE, Decoration(
      Icons.PRIMITIVE_SQUARE,
      TextStyles.HIGHLIGHT
    ))

    result.addDecoration(RamlNodeTypes.METHOD, Decoration(
      Icons.PRIMITIVE_DOT,
      TextStyles.WARNING
    ))

    result.addDecoration(RamlNodeTypes.SECURITY_SCHEME, Decoration(
      Icons.FILE_SUBMODULE,
      TextStyles.NORMAL
    ))

    result.addDecoration(RamlNodeTypes.ANNOTATION_DECLARATION, Decoration(
      Icons.TAG,
      TextStyles.HIGHLIGHT
    ))

    result.addDecoration(RamlNodeTypes.TYPE_DECLARATION, Decoration(
      Icons.FILE_BINARY,
      TextStyles.SUCCESS
    ))

    result.addDecoration(RamlNodeTypes.DOCUMENTATION_ITEM, Decoration(
      Icons.BOOK,
      TextStyles.NORMAL
    ))

    result
  }
}
