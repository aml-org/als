package org.mulesoft.high.level.dialect

import amf.core.remote.Vendor._
import amf.core.annotations.{Aliases, SourceAST}
import amf.core.metamodel.document.DocumentModel
import amf.core.model.document.{BaseUnit, Module}
import amf.core.model.domain.AmfArray
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.metamodel.document.DialectInstanceModel
import amf.plugins.document.vocabularies.model.document.{
  Dialect,
  DialectInstance,
  DialectInstanceFragment,
  DialectInstanceLibrary
}
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement, PropertyMapping}
import org.mulesoft.high.level.implementation._
import org.mulesoft.high.level.interfaces._
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.dialects.extras.SourcePropertyMapping
import org.mulesoft.typesystem.dialects.{BuiltinUniverse, DialectUniverse}
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse
import org.mulesoft.typesystem.project.{ModuleDependencyEntry, TypeCollection, TypeCollectionBundle}
import org.yaml.model.{YNode, YPart, YSequence}

import scala.collection.Map
import scala.collection.mutable
import scala.collection.IndexedSeq
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class DialectProjectBuilder {

  def buildProject(rootUnit: BaseUnit, platform: AlsPlatform): IProject = {

    val u: IDialectUniverse = getUniverse(rootUnit.fields(DialectInstanceModel.DefinedBy).toString)
    val unitPath            = TypeBuilder.normalizedPath(rootUnit)
    val bundle              = new TypeCollectionBundle()
    val project             = new Project(bundle, AML, platform)
    val rootASTUnit         = buildUnit(rootUnit, project, u)
    project.setRootUnit(rootASTUnit)
    initASTUnits(project.units)
    project
  }

  def buildUnit(rootUnit: BaseUnit, project: Project, u: IDialectUniverse): ASTUnit = {

    val unitPath       = TypeBuilder.normalizedPath(rootUnit)
    val typeCollection = new TypeCollection(unitPath, u.asInstanceOf[DialectUniverse], null)
    val bundle         = project.types
    bundle.asInstanceOf[TypeCollectionBundle].registerTypeCollection(typeCollection)
    val unit = new ASTUnit(rootUnit, typeCollection, project)

    rootUnit match {
      case di: DialectInstance          => buildDialectInstance(di, unit, u)
      case dil: DialectInstanceLibrary  => buildDialectInstanceLibrary(dil, unit, u)
      case dif: DialectInstanceFragment => buildDialectInstanceFragment(dif, unit, u)
      case _                            =>
    }
    processDependencies(unit, project, u)
    unit
  }

  def buildDialectInstanceLibrary(rootUnit: DialectInstanceLibrary, unit: ASTUnit, u: IDialectUniverse): Unit = {

    if (u.library.isEmpty) {
      throw new Error(s"No library type is registered for dialect: ${rootUnit.definedBy().value()}")
    }
    val rootType = u.library.get
    val rootNode = new ASTNodeImpl(rootUnit, rootUnit, None, rootType, None)
    rootNode.setASTUnit(unit)

    unit.setRootNode(rootNode)

    rootUnit.declares.foreach(de => {
      val dde = de.asInstanceOf[DialectDomainElement]
      declarationPropertyNameFromId(dde.id).foreach(propName => {
        val srcOpt = dde.annotations.find(classOf[SourceAST]).map(_.ast)
        addObjectProperty(rootNode, dde, propName, srcOpt)
      })
    })
    rootNode.initSources(Some(unit), None)
  }

  def buildDialectInstanceFragment(rootUnit: DialectInstanceFragment, unit: ASTUnit, u: IDialectUniverse): Unit = {

    val fragmentTypeNameOpt = fragmentType(unit.text)
    if (fragmentTypeNameOpt.isEmpty) {
      throw new RuntimeException("Unable to detect fragment type")
    }

    val rootTypeOpt = u.fragments.get(fragmentTypeNameOpt.get)
    if (rootTypeOpt.isEmpty) {
      throw new RuntimeException(s"Unable to detect type for fragment name: ${fragmentTypeNameOpt.get}")
    }
    val rootNode = new ASTNodeImpl(rootUnit.encodes, rootUnit, None, rootTypeOpt.get, None)
    rootNode.setASTUnit(unit)

    unit.setRootNode(rootNode)
    Option(rootUnit.encodes).foreach(enc => {
      val dde = enc.asInstanceOf[DialectDomainElement]
      fillProperties(dde, rootNode, None)
    })
    rootNode.initSources(Some(unit), None)
  }

  def fragmentType(content: String): Option[String] = {
    val ind1 = content.indexOf("#%")
    val ind2 = content.indexOf("/")
    if (ind1 < 0 || ind2 < 0 || ind1 > ind2) {
      None
    } else {
      val tName = content.substring(ind1 + "#%".length, ind2).trim
      Some(tName)
    }
  }

  def getUniverse(dialectId: String): IDialectUniverse = {
    val dialectOpt = AMLPlugin.registry.allDialects().find(_.id == dialectId)
    val universe: IDialectUniverse = dialectOpt match {
      case Some(d) => DialectUniversesProvider.getUniverse(d)
      case _       => new DialectUniverse(dialectId, None, "")
    }
    universe
  }

  def buildDialectInstance(rootUnit: DialectInstance, unit: ASTUnit, u: IDialectUniverse): Unit = {

    if (u.root.isEmpty) {
      throw new Error(s"No root type is registered for dialect: ${rootUnit.definedBy().value()}")
    }

    val rootType = u.root.get
    val rootNode = new ASTNodeImpl(rootUnit.encodes, rootUnit, None, rootType, None)
    rootNode.setASTUnit(unit)

    unit.setRootNode(rootNode)
    Option(rootUnit.encodes).foreach(enc => {
      val dde = enc.asInstanceOf[DialectDomainElement]
      fillProperties(dde, rootNode, None)
    })

    rootUnit.declares.foreach(de => {
      val dde = de.asInstanceOf[DialectDomainElement]
      declarationPropertyNameFromId(dde.id).foreach(propName => {
        val srcOpt = dde.annotations.find(classOf[SourceAST]).map(_.ast)
        addObjectProperty(rootNode, dde, propName, srcOpt)
      })
    })

    rootNode.initSources(Some(unit), None)
  }

  private def processDependencies(unit: ASTUnit, project: Project, u: IDialectUniverse): Unit = {

    unit.baseUnit.annotations.find(classOf[Aliases])

    Option(unit.baseUnit.references)
      .getOrElse(Seq())
      .foreach(bu => {
        val newUnit = buildUnit(bu, project, u)
        project.addUnit(newUnit)
      })
  }

  private def fillProperties(_dde: DialectDomainElement, parent: ASTNodeImpl, pm: Option[PropertyMapping]): Unit = {
    var dde                                        = _dde
    val unit                                       = parent.astUnit
    val rootUnit                                   = unit.baseUnit
    val sourcesMap: mutable.Map[String, SourceAST] = mutable.Map()
    dde.propertyAnnotations
      .map(e => (e._1, e._2.find(classOf[SourceAST])))
      .foreach(e => e._2.foreach(src => sourcesMap.put(e._1, src)))

    val definition = parent.definition
    dde.literalProperties.foreach(p => {
      val propId    = p._1
      val propValue = p._2
      propertyNameFromId(propId).foreach(propName => {
        val srcOpt     = sourcesMap.get(propId).map(_.ast)
        val sourceInfo = SourceInfo().withReferingUnit(unit)
        srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))
        val propOpt                           = definition.property(propName)
        var rangeOpt                          = propOpt.flatMap(_.range)
        val children: ListBuffer[ASTPropImpl] = ListBuffer()
        propValue match {
          case seq: Seq[Any] =>
            rangeOpt = rangeOpt.flatMap(x => {
              if (x.isArray) {
                x.array.flatMap(_.componentType)
              } else {
                Some(x)
              }
            })
            val arr = ArrayBuffer[Any]() ++= seq
            val srcSeq: IndexedSeq[YPart] = srcOpt match {
              case Some(x) =>
                x match {
                  case s: YSequence => s.nodes
                  case n: YNode =>
                    n.value match {
                      case s: YSequence => s.nodes
                      case _            => IndexedSeq(x)
                    }
                  case _ => IndexedSeq(x)
                }
              case _ => IndexedSeq()
            }
            for (i <- 0 until arr.length) {
              val buf  = new LiteralArrayPropertyValueBuffer(dde, propId, arr, srcSeq, i)
              val prop = new ASTPropImpl(dde, rootUnit, Some(parent), rangeOpt.orNull, propOpt, buf)
              prop.setASTUnit(unit)
              children += prop
            }
          case _ =>
            val buf = new LiteralPropertyValueBuffer(dde, propId, propValue, srcOpt)

            val prop = new ASTPropImpl(dde, rootUnit, Some(parent), rangeOpt.orNull, propOpt, buf)
            prop.setASTUnit(unit)
            children += prop
        }
        children.foreach(parent.addChild)
      })
    })
    dde.objectProperties.foreach(p => {
      val propId    = p._1
      val childNode = p._2
      propertyNameFromId(propId).foreach(propName => {
        val srcOpt = sourcesMap.get(propId).map(_.ast)
        addObjectProperty(parent, childNode, propName, srcOpt)
      })
    })
    dde.objectCollectionProperties.foreach(p => {
      val propId                            = p._1
      val values: Seq[DialectDomainElement] = p._2
      propertyNameFromId(propId).foreach(propName => {
        values.foreach(x => {
          addObjectProperty(parent, x, propName, None)
        })
      })
    })
    dde.mapKeyProperties.foreach(p => {
      val propId    = p._1
      val propValue = p._2
      DialectUniverseBuilder
        .findPropertyByRdfId(propId, definition)
        .foreach(p => {
          val srcOpt     = sourcesMap.get(propId).map(_.ast)
          val sourceInfo = SourceInfo().withReferingUnit(unit)
          srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))

          val buf = new LiteralPropertyValueBuffer(dde, propId, propValue, srcOpt)

          val prop = new ASTPropImpl(dde, rootUnit, Some(parent), definition, Some(p), buf)
          prop.setASTUnit(unit)
          parent.addChild(prop)
        })
    })
    dde.linkProperties.foreach(p => {})
  }

  private def addObjectProperty(node: ASTNodeImpl,
                                propValue: DialectDomainElement,
                                propName: String,
                                _srcOpt: Option[YPart] = None): Unit = {

    val srcOpt: Option[YPart] = _srcOpt.orElse(propValue.annotations.find(classOf[SourceAST]).map(_.ast))

    val baseUnit   = node.astUnit.baseUnit
    val definition = node.definition

    val sourceInfo = SourceInfo().withReferingUnit(node.astUnit)
    srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))

    val propOpt = definition.property(propName)
    val range = propOpt
      .flatMap(_.range)
      .map(r => {
        if (r.isArray) {
          r.array.get.componentType.getOrElse(r)
        } else {
          r
        }
      })
      .getOrElse(BuiltinUniverse.any)
    val ch = new ASTNodeImpl(propValue, baseUnit, Some(node), range, propOpt)
    node.addChild(ch)
    ch.setASTUnit(node.astUnit)
    fillProperties(propValue, ch, propOpt.flatMap(x => x.getExtra(SourcePropertyMapping)))
  }

  private def propertyNameFromId(propId: String): Option[String] = {
    var ind = propId.lastIndexOf("/property/")
    if (ind < 0) {
      None
    } else {
      val pName = propId.substring(ind + "/property/".length)
      Some(pName)
    }
  }

  private def declarationPropertyNameFromId(propId: String): Option[String] = {
    val ind1 = propId.lastIndexOf("#/")
    val ind2 = propId.lastIndexOf("/")
    if (ind1 < 0 || ind2 < 0 || ind2 <= ind1) {
      None
    } else {
      val pName = propId.substring(ind1 + "#/".length, ind2)
      Some(pName)
    }
  }

  private def initASTUnits(astUnits: Map[String, ASTUnit]): Unit = {

    for (astUnit <- astUnits.values) {
      var aliases = astUnit.baseUnit.annotations
        .find(classOf[Aliases])
        .map(_.aliases)
        .getOrElse((null, (null, null)) :: Nil)
      for (usesEntry <- aliases) {
        val namespace         = usesEntry._1
        val referedModulePath = usesEntry._2._1
        val libPath           = usesEntry._2._2
        astUnits
          .get(referedModulePath)
          .foreach(referedAstUnit => {
            var dep = new ModuleDependencyEntry(referedModulePath, referedAstUnit, namespace, libPath)
            astUnit.registerDependency(dep)
            var reverseDep = new ModuleDependencyEntry(astUnit.path, astUnit, namespace, libPath)
            referedAstUnit.registerReverseDependency(reverseDep)
          })
      }
    }
  }

}

object DialectProjectBuilder {

  private var instance: Option[DialectProjectBuilder] = None

  def getInstance: DialectProjectBuilder = {

    instance match {
      case Some(x) => x
      case _ => {
        val x = new DialectProjectBuilder()
        instance = Some(x)
        x
      }
    }

  }

}
