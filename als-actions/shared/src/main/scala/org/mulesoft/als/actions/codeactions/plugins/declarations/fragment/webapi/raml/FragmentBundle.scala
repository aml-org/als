package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml

import amf.apicontract.client.scala.model.document.{
  AnnotationTypeDeclarationFragment,
  DataTypeFragment,
  DocumentationItemFragment,
  NamedExampleFragment,
  ResourceTypeFragment,
  SecuritySchemeFragment,
  TraitFragment
}
import amf.apicontract.internal.metamodel.domain.security.SecuritySchemeModel
import amf.apicontract.internal.metamodel.domain.templates.{ResourceTypeModel, TraitModel}
import amf.core.client.scala.model.document.Fragment
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.shapes.internal.domain.metamodel.AnyShapeModel

trait FragmentBundle {
  val name: String
  val fragment: Fragment
  def applies(element: AmfObject): Boolean
}

case class IriFragmentBundle(typeIri: String, fragment: Fragment, name: String) extends FragmentBundle {
  override def applies(element: AmfObject): Boolean =
    element.meta.`type`.headOption.map(_.iri()).contains(typeIri)
}

object IriFragmentBundle {
  def apply(model: DomainElementModel, fragment: Fragment): FragmentBundle =
    new IriFragmentBundle(model.`type`.head.iri(), fragment, model.doc.displayName)
}

object FragmentBundles {
  val DataTypeFragmentBundle: FragmentBundle =
    IriFragmentBundle(AnyShapeModel.`type`.head.iri(), DataTypeFragment(), "DataType") // displayName not intuitive
  val NamedExampleFragmentBundle: FragmentBundle =
    IriFragmentBundle("", NamedExampleFragment(), "NamedExample")
  val DocumentationItemFragmentBundle: FragmentBundle =
    IriFragmentBundle("//creative common model?", DocumentationItemFragment(), "DocumentationItem")
  val AnnotationTypeDeclarationFragmentBundle: FragmentBundle =
    IriFragmentBundle(CustomDomainPropertyModel, AnnotationTypeDeclarationFragment())
  val SecuritySchemeFragmentBundle: FragmentBundle =
    IriFragmentBundle(SecuritySchemeModel, SecuritySchemeFragment())
  val TraitFragmentBundle: FragmentBundle =
    IriFragmentBundle(TraitModel, TraitFragment())
  val ResourceTypeFragmentBundle: FragmentBundle =
    IriFragmentBundle(ResourceTypeModel, ResourceTypeFragment())

  val all = Seq(
    DataTypeFragmentBundle,
    NamedExampleFragmentBundle,
    DocumentationItemFragmentBundle,
    AnnotationTypeDeclarationFragmentBundle,
    SecuritySchemeFragmentBundle,
    TraitFragmentBundle,
    ResourceTypeFragmentBundle
  )
}

object RamlFragmentMatcher {
  def fragmentFor(element: AmfObject): Option[FragmentBundle] =
    FragmentBundles.all.find(_.applies(element))
}
