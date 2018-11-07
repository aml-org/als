package org.mulesoft.language.outline.structure.config

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, Decorator, IASTProvider}
import org.mulesoft.language.outline.structure.structureDefault._
import org.mulesoft.language.outline.structure.structureDefaultInterfaces.Decoration
import org.mulesoft.typesystem.dialects.extras.{Declaration, RootType}
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse

import scala.collection.mutable

class AMLPlugin extends IStructurePlugin {

  override def accepts(provider: IASTProvider): Boolean = {
    provider.language.toLowerCase.contains("aml")
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

    val declaresCategoryFilter = new CategoryFilter {

        override def apply(node: IParseResult): Boolean = {

            if (node.isElement) {
                if(node.parent.isDefined){
                    if(node.parent.get.parent.isEmpty) {
                        node.property.flatMap(_.getExtra(Declaration)).isDefined
                    }
                    else {
                        false//apply(node.parent.get)
                    }
                }
                else {
                    false
                }
            } else {
                false
            }
        }
    }
      result("DeclaresCategory") = declaresCategoryFilter

      val encodesCategoryFilter = new CategoryFilter {

          override def apply(node: IParseResult): Boolean = {

              if (node.isElement) {
                  if (node.parent.isDefined) {
                      if(node.parent.get.parent.isEmpty) {
                          if (node.property.isEmpty) {
                              false
                          }
                          val parentDef = node.parent.get.definition
                          if (parentDef.getExtra(RootType).isDefined) {
                              val domOpt = node.property.get.domain
                              if (domOpt.isEmpty) {
                                  false
                              }
                              else {
                                  !domOpt.contains(parentDef)
                              }
                          }
                          else {
                              true
                          }
                      }
                      else {
                          false //apply(node.parent.get)
                      }
                  }
                  else {
                      false
                  }

              } else {
                  false
              }
          }
      }
      result("EncodesCategory") = encodesCategoryFilter

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
