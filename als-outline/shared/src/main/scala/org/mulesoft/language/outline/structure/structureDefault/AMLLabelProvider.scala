package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra

class AMLLabelProvider extends DefaultLabelProvider {

  override def getLabelText(node: IParseResult): String = {

    var result: Option[String] = None
    node.asElement.foreach(el => {
      val keyPropOpt = el.children.find(_.property.flatMap(_.getExtra(PropertySyntaxExtra)).exists(_.isKey))
      keyPropOpt.foreach(prop => {
        if (prop.isAttr) {
          result = prop.asAttr.get.value.map(_.toString)
        }
      })
    })

    if (result.nonEmpty) {
      result.get
    }
    else {
      super.getLabelText(node)
    }
  }
}
