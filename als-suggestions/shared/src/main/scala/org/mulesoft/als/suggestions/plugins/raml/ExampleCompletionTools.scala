package org.mulesoft.als.suggestions.plugins.raml

import org.mulesoft.als.suggestions.interfaces.ICompletionRequest

trait ExampleCompletionTools {
  protected def isExample(request: ICompletionRequest): Boolean = {
    request.astNode match {
      case Some(node) =>
        if (node.isElement)
          node.asElement.get.definition.nameId.contains("ExampleSpec") ||
          node.asElement.get.definition.nameId.contains("ExampleSpecFragment")
        else
          node.parent.exists(p =>
            p.definition.nameId.contains("ExampleSpec") || p.definition.nameId.contains("ExampleSpecFragment"))
      case _ => false
    }
  }
}
