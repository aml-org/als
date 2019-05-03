package org.mulesoft.high.level.typesystem

import amf.core.annotations.{Aliases, DeclaredElement}
import amf.core.metamodel.document.{BaseUnitModel, DocumentModel}
import amf.core.model.document.{BaseUnit, Document, Fragment, Module}
import amf.core.model.domain.{AmfArray, AmfScalar, ScalarNode, Shape}
import amf.core.model.document.ExternalFragment
import amf.core.model.domain.extensions.{CustomDomainProperty, PropertyShape}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.models.{ArrayShape, MatrixShape, NodeShape, UnionShape}
import org.mulesoft.high.level.builder.{IASTFactory, NodeBuilder, UniverseProvider}
import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUnionType, IUniverse}
import org.mulesoft.typesystem.nominal_interfaces.extras.{TopLevelExtra, UserDefinedExtra}
import org.mulesoft.typesystem.nominal_types.{
  AbstractType,
  Array,
  EmptyUniverse,
  ExternalType,
  NilType,
  Property,
  StructuredType,
  Union,
  Universe,
  ValueType
}
import org.mulesoft.typesystem.project._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.collection.Map

object TypeBuilder {

  val BUILTIN = true

  def buildTypes(units: Map[String, BaseUnit], factory: IASTFactory): TypeCollectionBundle = {

    var parentUniverseOpt = UniverseProvider.universe(factory.format)
    if (parentUniverseOpt.isDefined) {
      var parentOpt = parentUniverseOpt.get.parent
      if (parentOpt.isDefined) {
        parentUniverseOpt = parentOpt
      }
    }
    var bundle: TypeCollectionBundle = initTypeCollectionBundle(units, parentUniverseOpt)
    var ctx                          = new Context(units, bundle, parentUniverseOpt.getOrElse(EmptyUniverse), factory)
    buildClasses(ctx)
    bundle
  }

  private def buildClasses(ctx: Context): Unit = {

    ctx.bundle.typeCollections.values.foreach(tc => {
      var unit = ctx.units(tc.id)
      initTypeCollection(tc, unit, ctx)
    })
    ctx.bundle.typeCollections.values.foreach(tc => {
      tc.types.ownTypes.foreach(fillType(_, ctx))
      tc.annotationTypes.ownTypes.foreach(fillType(_, ctx))
    })
  }

  def getOrCreate(shape: Shape,
                  universe: IUniverse,
                  bundle: TypeCollectionBundle,
                  factory: IASTFactory): AbstractType = {
    var ctx = new Context(Map(), bundle, universe.parent.getOrElse(EmptyUniverse), factory)
    getOrCreate(shape, universe, ctx)
  }

  private def getOrCreate(shape: Shape, universe: IUniverse, ctx: Context): AbstractType = {
    Option(shape.name).flatMap(name => ctx.bundle.getType(shape.id, name.value())) match {
      case Some(t) => t
      case _ if shape.annotations.contains(classOf[DeclaredElement]) =>
        ctx.cached(shape) match {
          case Some(a) => a
          case _ =>
            val inlineType = initType(shape, universe, ctx)
            ctx.add(shape, inlineType)
            fillType(inlineType, ctx)
            inlineType
        }
      case _ =>
        val inlineType = initType(shape, universe, ctx)
        fillType(inlineType, ctx)
        inlineType
    }
  }

  private def createProperty(pShape: PropertyShape, universe: IUniverse, ctx: Context): Property = {
    var t = getOrCreate(pShape.range, universe, ctx)
    new Property(pShape.name.value()).withRequired(pShape.minCount.value() > 0).withRange(t)
  }

  private def fillType(t: AbstractType, ctx: Context): Unit = {
    t.getExtra(Extras.SOURCE_SHAPE)
      .foreach(shape => {
        var inherits = shape.inherits
        if (inherits.nonEmpty) {
          inherits.foreach(superShape => {
            var st = getOrCreate(superShape, t.universe, ctx)
            t.addSuperType(st)
          })
        } else if (shape.isLink) {
          val superShape = shape.effectiveLinkTarget().asInstanceOf[Shape]
          t.addSuperType(getOrCreate(superShape, t.universe, ctx))
        } else {
          var st = ctx.factory.builtinSuperType(shape)
          st.foreach(t.addSuperType)
        }
        shape.meta match {
          case UnionShapeModel =>
            var anyOf   = shape.asInstanceOf[UnionShape].anyOf
            var options = anyOf.map(getOrCreate(_, t.universe, ctx))
            t.asInstanceOf[Union].setOptions(options)
          case ArrayShapeModel | MatrixShapeModel =>
            Option(shape.fields.get(ArrayShapeModel.Items))
              .filter(_.isInstanceOf[Shape])
              .map(_.asInstanceOf[Shape])
              .foreach(items => {
                var componentType = getOrCreate(items, t.universe, ctx)
                t.asInstanceOf[Array].setComponent(componentType)
              })
          case NodeShapeModel =>
            var structuredType = t.asInstanceOf[StructuredType]
            val nodeShape      = shape.asInstanceOf[NodeShape]
            var props          = nodeShape.properties
            var properties     = props.map(createProperty(_, t.universe, ctx))
            properties.foreach(structuredType.registerProperty)
          case _ =>
        }
        var customShapePropertyDefinitions = shape.customShapePropertyDefinitions
        var facets                         = customShapePropertyDefinitions.map(createProperty(_, t.universe, ctx))
        facets.foreach(t.addFacet)

        builtinDefinitionsFamily(t, ctx)
          .flatMap(_.allProperties)
          .foreach(prop => {
            var pName = prop.nameId.get
            ctx.factory.builtInFacetValue(pName, shape).foreach(t.fixFacet(pName, _, BUILTIN))
          })

        shape.customShapeProperties.foreach(ext => {
          var fName = ext.definedBy.name
          val value = ext.extension
          var fValue: Any = value match {
            case sn: ScalarNode => sn.value
            case _              => value
          }
          t.fixFacet(fName.value(), fValue)
        })
      })
  }

  private def initTypeCollection(tc: TypeCollection, unit: BaseUnit, ctx: Context): Unit = {
    Option(unit.fields.getValue(DocumentModel.Declares))
      .flatMap(_.value match {
        case arr: AmfArray => Some(arr.values)
        case _             => None
      })
      .foreach(_.foreach({
        case cdp: CustomDomainProperty => tc.annotationTypes.register(initAnnotationType(cdp, tc.annotationTypes, ctx))
        case shape: Shape              => tc.types.register(initType(shape, tc.types, ctx))
        case _                         =>
      }))
    tc.types.ownTypes.foreach(_.putExtra(TopLevelExtra))
    tc.annotationTypes.ownTypes.foreach(_.putExtra(TopLevelExtra))
  }

  private def initTypeCommon(shape: Shape, universe: Universe, topLevel: Boolean = false): AbstractType = {
    null
  }

  private def initType(_shape: Shape, universe: IUniverse, ctx: Context): AbstractType = {

    var shape = _shape
    shape.linkTarget
      .filter(x => x.isInstanceOf[ArrayShape] | x.isInstanceOf[MatrixShape])
      .foreach(x => shape = x.asInstanceOf[Shape])

    val name = shape.name
    val id   = shape.id
    var result = shape.meta match {
      case UnionShapeModel                    => new Union(name.value(), universe, id)
      case ArrayShapeModel | MatrixShapeModel => new Array(name.value(), universe, id)
      case ScalarShapeModel                   => new ValueType(name.value(), universe, id)
      case NilShapeModel                      => NilType
      case _                                  => new StructuredType(name.value(), universe, id)
    }
    if (result != NilType) {
      result.putExtra(Extras.SOURCE_SHAPE, shape)
      result.putExtra(UserDefinedExtra)
    }
    result
  }

  private def initAnnotationType(cdp: CustomDomainProperty, universe: Universe, ctx: Context): AbstractType = {
    var result = initType(cdp.schema, universe, ctx)
    result.putExtra(Extras.SOURCE_CUSTOM_DOMAIN_PROPERTY, cdp)
    val allowedTargets = cdp.domain
    if (allowedTargets.nonEmpty) {
      result.fixFacet("allowedTargets", allowedTargets, BUILTIN)
    }
    result
  }

  private def initTypeCollectionBundle(units: Map[String, BaseUnit], parentUniverseOpt: Option[IUniverse]) = {
    var bundle = TypeCollectionBundle()
    for (unit <- units.values) {
      var id             = normalizedPath(unit)
      var tUniverse      = new Universe(id, parentUniverseOpt, "")
      var aUniverse      = new Universe(id, Some(tUniverse), "")
      var typeCollection = TypeCollection(id, tUniverse, aUniverse)
      bundle.registerTypeCollection(typeCollection)
    }
    for (unit <- units.values) {
      var tc = bundle.typeCollections(normalizedPath(unit))
      getReferences(unit).foreach(u => {
        val ref       = u.reference
        var referedTc = bundle.typeCollections(ref)
        var refUnit   = units(ref)
        refUnit match {
          case m: Module =>
            var aliases = m.annotations
              .find(classOf[Aliases])
              .map(_.aliases)
              .getOrElse((null, (null, null)) :: Nil)
            //TODO aliases validity filter needed
            for (usesEntry <- aliases) {
              val namespace          = usesEntry._1
              val referingModulePath = usesEntry._2._1
              val libPath            = usesEntry._2._2
              bundle.typeCollections
                .get(referingModulePath)
                .foreach(referingTc => {
                  var dep = new ModuleDependencyEntry(ref, referedTc, namespace, libPath)
                  referingTc.registerDependency(dep)
                })
            }
          case ef: ExternalFragment =>
            var dep = new DependencyEntry(ref, referedTc)
            tc.registerDependency(dep)
          case f: Fragment =>
            var dep = new FragmentDependencyEntry(ref, referedTc)
            tc.registerDependency(dep)
          case _ =>
        }
      })
    }
    bundle
  }

  def getReferences(unit: BaseUnit): Seq[BaseUnitReference] = {
    unit.references.map(unit => {
      var ref: String = normalizedPath(unit)
      new BaseUnitReference(ref, unit)
    })
  }

  def normalizedPath(unit: BaseUnit): String = unitPath(unit.location().getOrElse(unit.id))

  def unitPath(str: String): String = {
    var ref = str
    val ind = ref.lastIndexOf("#")
    if (ind >= 0) {
      ref = ref.substring(0, ind)
    }
    ref
  }

  def builtinDefinitionsFamily(t: ITypeDefinition, ctx: Context): Seq[ITypeDefinition] = {

    val tArray: ListBuffer[ITypeDefinition] = ListBuffer()
    tArray += t
    tArray ++= t.allSuperTypes
    var result = tArray
      .flatMap(_.getExtra(Extras.SOURCE_SHAPE))
      .flatMap(shape => {
        ctx.factory.discriminateShape(shape, ctx.parentUniverse)
      })
      .distinct
    result
  }
}

class Context(val units: Map[String, BaseUnit],
              val bundle: TypeCollectionBundle,
              val parentUniverse: IUniverse,
              val factory: IASTFactory) {
  def cached(shape: Shape): Option[AbstractType] = cache.get(shape)

  def add(shape: Shape, t: AbstractType): AbstractType = {
    cache += shape -> t
    t
  }

  val cache: mutable.Map[Shape, AbstractType] = mutable.Map()
}

class BaseUnitReference(val reference: String, val unit: BaseUnit) {}
