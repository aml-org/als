package org.mulesoft.als.actions.common.dialect

import amf.core.annotations.{LexicalInformation, SourceAST, SourceLocation}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfObject, AmfScalar}
import amf.core.parser
import amf.core.parser.FieldEntry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.metamodel.domain.PropertyMappingModel
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common._
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * For the moment this just looks for declaration in NodeMapping Ranges
  */
object DialectDefinitions {
  def getDefinition(uri: String,
                    position: Position,
                    fbu: Future[BaseUnit],
                    platform: Platform): Future[Seq[LocationLink]] =
    for {
      bu <- fbu
    } yield {
      findByPosition(uri, bu, position)
        .map(toLocationLink)
        .sortWith(sortInner)
    }

  private def getLinkForPosition(obj: AmfObject, position: Position, entry: YMapEntry): Option[String] = {
    if (entry.key.asScalar.map(_.text).contains("range")) {
      obj.fields
        .fields()
        .find(_.field.value.iri() == PropertyMappingModel.ObjectRange.value.iri())
        .flatMap { entry =>
          entry.value.value match {
            case array: AmfArray =>
              array.values.headOption.flatMap {
                case s: AmfScalar =>
                  Some(s.value.toString)
                case _ => None
              }
            case _ => None
          }
        }

    } else None
  }

  private def getPositionForLink(d: Dialect, link: String): Option[Location] = {
    d.declares
      .find(_.id == link)
      .flatMap(e => {
        e.annotations
          .find(classOf[LexicalInformation])
          .flatMap(li => e.annotations.find(classOf[SourceLocation]).map(sl => (li, sl)))
      })
      .map(t => Location(t._2.location, LspRangeConverter.toLspRange(PositionRange(t._1.range))))
  }

  private def findByPosition(uri: String, bu: BaseUnit, position: Position): Seq[(Location, Location)] = {
    bu match {
      case d: Dialect =>
        val tree: ObjectInTree = ObjectInTreeBuilder.fromUnit(d, position.toAmfPosition)

        // todo: add Lexical info to fields in NodeMapping, so that we can find the specific one
        // val fieldEntry = tree.getFieldEntry(position.toAmfPosition, FieldEntryOrdering)
        val yPartBranch: Option[YPartBranch] =
          tree.obj.annotations
            .find(classOf[SourceAST])
            .map(ast => NodeBranchBuilder.build(ast.ast, position.toAmfPosition, isJson = false))

        val target: Option[Location] =
          yPartBranch
            .flatMap(_.parentEntry)
            .flatMap(entry => getLinkForPosition(tree.obj, position, entry))
            .flatMap(getPositionForLink(d, _))
        target
          .map { t =>
            val origin = getYBranch(position, d).node.location
            Seq((ActionTools.sourceLocationToLocation(origin), t))
          }
          .getOrElse(Nil)
      case _ => Nil
    }
  }

  private def sortInner(l1: LocationLink, l2: LocationLink): Boolean =
    l1.originSelectionRange
      .flatMap { l1pr =>
        l2.originSelectionRange.map { l2pr =>
          val pr1 = PositionRange(LspRangeConverter.toPosition(l1pr.start), LspRangeConverter.toPosition(l1pr.end))
          val pr2 = PositionRange(LspRangeConverter.toPosition(l2pr.start), LspRangeConverter.toPosition(l2pr.end))
          pr1.intersection(pr2).contains(pr1)
        }
      }
      .getOrElse(false)

  private def toLocationLink(s: (Location, Location)) =
    LocationLink(s._2.uri, s._2.range, s._2.range, Some(s._1.range))

  private def getYBranch(position: Position, bu: BaseUnit) =
    NodeBranchBuilder.build(bu, position.toAmfPosition, YamlUtils.isJson(bu))

  object FieldEntryOrdering extends Ordering[FieldEntry] { // TODO: unify with suggestions
    override def compare(x: FieldEntry, y: FieldEntry): Int = {
      val tuple: Option[(parser.Range, parser.Range)] = for {
        xRange <- x.value.annotations.find(classOf[LexicalInformation]).map(_.range)
        yRange <- y.value.annotations.find(classOf[LexicalInformation]).map(_.range)
      } yield (xRange, yRange)

      tuple match {
        case Some((xRange, yRange)) =>
          val start = xRange.start.compareTo(yRange.start)
          if (start == 0) xRange.end.compareTo(yRange.end)
          else start
        case _ => 0
      }
    }
  }
}
