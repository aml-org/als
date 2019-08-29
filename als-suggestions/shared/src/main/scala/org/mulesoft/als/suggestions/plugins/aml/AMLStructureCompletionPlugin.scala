package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.{Field, Obj}
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
import org.mulesoft.als.common.AmfSonElementFinder._
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(propertyMapping: Seq[PropertyMapping],
                                    indentation: String,
                                    yPartBranch: YPartBranch,
                                    amfObject: AmfObject) {

  private def extractText(mapping: PropertyMapping): (String, String) = {
    val cleanText = mapping.name().value()
    val whiteSpaces =
      if (mapping.literalRange().isNullOrEmpty) indentation
      else ""
    (cleanText, whiteSpaces)
  }

  private def startsWithLetter(string: String) = { // TODO: move to single object responsible for presentation
    val validSet: Set[Char] =
      (('a' to 'z') ++ ('A' to 'Z') ++ "\"" ++ "\'").toSet
    if (string.headOption.exists(validSet.contains)) true
    else false
  }

  /**
    *
    * @param completeMappings Properties that may be contained in this Field
    * @param amfObject Selected Field
    * @return Check if amfObject has an AST node which contains a property that I am suggesting (which could mean there is an incomplete object)
    */
  private def alreadyWriting(completeMappings: Seq[PropertyMapping], amfObject: AmfObject): Boolean = {
    val writingProperty: Option[String] = // Drop the "{k:}"
      yPartBranch.stack.drop(3).headOption match {
        case Some(ye: YMapEntry) => ye.key.asScalar.map(_.text)
        case _                   => None
      }
    writingProperty.exists(wp => completeMappings.exists(cm => cm.name().value() == wp))
  }

  private def getSuggestions: Seq[(String, String)] = {
    if (alreadyWriting(propertyMapping, amfObject))
      Nil
    else propertyMapping.map(extractText)
  }

  def resolve(classTerm: String): Seq[RawSuggestion] =
    getSuggestions
      .map(
        s =>
          RawSuggestion(if (startsWithLetter(s._1)) s._1
                        else s""""${s._1}"""",
                        s._1,
                        s._1,
                        Seq(),
                        isKey = true,
                        s._2,
                        CategoryRegistry(classTerm, s._1)))
}

object AMLStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingProperty(params.yPartBranch)) {
        if (!isInFieldValue(params)) {
          val isEncoded = isEncodes(params.amfObject, params.actualDialect) && params.fieldEntry.isEmpty
          if ((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded) {
            new AMLStructureCompletionsPlugin(params.propertyMapping,
                                              params.indentation,
                                              params.yPartBranch,
                                              params.amfObject)
              .resolve(params.amfObject.meta.`type`.head.iri())
          } else Nil
        } else resolveObjInArray(params)
      } else Nil
    }
  }

  private def isWritingProperty(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKey || (yPartBranch.isJson && (yPartBranch.isInArray && yPartBranch.stringValue == "x"))

  protected def objInArray(params: AmlCompletionRequest): Option[DomainElementModel] = {
    params.fieldEntry match {
      case Some(FieldEntry(Field(t: ArrayLike, _, _, _), value))
          if t.element
            .isInstanceOf[DomainElementModel] && params.yPartBranch.isInArray =>
        Some(t.element.asInstanceOf[DomainElementModel])
      case _ => None
    }
  }

  protected def resolveObjInArray(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    objInArray(params) match {
      case Some(meta) =>
        val props = params.actualDialect.declares
          .collect({ case nm: NodeMapping => nm })
          .find(_.nodetypeMapping.value() == meta.`type`.head.iri())
          .map(_.propertiesMapping())
          .getOrElse(Nil)

        new AMLStructureCompletionsPlugin(props, params.indentation, params.yPartBranch, params.amfObject)
          .resolve(meta.`type`.head.iri())
      case _ => Nil
    }
  }
}
