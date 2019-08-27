package org.mulesoft.high.level.builder

import amf.core.annotations.SourceAST
import amf.core.metamodel.document.{BaseUnitModel, DocumentModel, ExtensionLikeModel}
import amf.core.metamodel.domain.{DomainElementModel, ExternalSourceElementModel, ShapeModel}
import amf.core.metamodel.domain.extensions.{
  CustomDomainPropertyModel,
  DomainExtensionModel,
  PropertyShapeModel,
  ShapeExtensionModel
}
import amf.core.model.document.{BaseUnit, Document, Fragment, Module}
import amf.core.model.domain._
import amf.core.parser.{Annotations, Fields}
import amf.core.remote.{Raml10, Vendor}
import amf.plugins.document.webapi.metamodel.FragmentsTypesModels._
import amf.plugins.document.webapi.metamodel.{ExtensionModel, OverlayModel}
import amf.plugins.document.webapi.model.{AnnotationTypeDeclarationFragment, DataTypeFragment}
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.metamodel.security._
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.metamodel._
import org.mulesoft.high.level.implementation.{BasicValueBuffer, JSONValueBuffer}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.nominal_interfaces.extras.UserDefinedExtra
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.{AbstractType, StructuredType}
import org.mulesoft.typesystem.project.{ITypeCollectionBundle, TypeCollectionBundle}
import org.yaml.model.{YMap, YScalar}

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequiredPropertyValueBuffer(element: AmfObject, hlNode: IHighLevelNode)
    extends BasicValueBuffer(element, PropertyShapeModel.MinCount) {

  override def getValue: Option[Any] = {
    getMinCount match {
      case Some(mc) => Some(mc > 0)
      case _        => None
    }
  }

  private def getMinCount = {
    super.getValue match {
      case Some(value) =>
        value match {
          case minCount: Int => Some(minCount)
          case _             => None
        }
      case _ => None
    }
  }

  override def setValue(value: Any): Unit = {
    value match {
      case required: Boolean =>
        val newMinCount = if (required) 1 else 0
        getMinCount match {
          case Some(mc) =>
            if ((mc > 0) != required) {
              super.setValue(newMinCount)
            }
          case _ => super.setValue(newMinCount)
        }
      case _ =>
    }
  }
}

object RequiredPropertyValueBuffer {
  def apply(element: AmfObject, hlNode: IHighLevelNode): RequiredPropertyValueBuffer =
    new RequiredPropertyValueBuffer(element, hlNode)
}

class AdditionalPropertiesValueBuffer(element: AmfObject, hlNode: IHighLevelNode)
    extends BasicValueBuffer(element, NodeShapeModel.Closed) {

  override def getValue: Option[Boolean] = super.getValue match {
    case Some(value) =>
      value match {
        case b: Boolean => Some(!b)
        case _          => None
      }
    case _ => None
  }

  override def setValue(value: Any): Unit = {
    value match {
      case b: Boolean => super.setValue(!b)
      case _          =>
    }
  }
}

object AdditionalPropertiesValueBuffer {
  def apply(element: AmfObject, hlNode: IHighLevelNode): AdditionalPropertiesValueBuffer =
    new AdditionalPropertiesValueBuffer(element, hlNode)
}
