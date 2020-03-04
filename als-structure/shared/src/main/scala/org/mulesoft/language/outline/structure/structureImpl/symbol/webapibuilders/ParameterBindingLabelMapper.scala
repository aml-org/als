package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

object ParameterBindingLabelMapper {

  def toLabel(in: String): String = {
    in match {
      case "header"   => "Header"
      case "query"    => "Query Parameters"
      case "body"     => "Body Parameters"
      case "formData" => "Form Data Parameters"
      case "path"     => "Path/Uri Parameters"
      case "cookie"   => "Cookie Parameters"
      case _          => "Parameters"
    }
  }
}
