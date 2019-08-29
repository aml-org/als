package org.mulesoft.typesystem.definition.system

import org.mulesoft.typesystem.nominal_interfaces.IUniverse
import org.mulesoft.typesystem.nominal_interfaces.extras.DescriptionExtra
import org.mulesoft.typesystem.nominal_types.{
  AbstractType,
  Array,
  Property,
  StructuredType,
  Universe,
  ValueType
}
import org.yaml.parser.YamlParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.nominal_interfaces.extras.{
  BuiltInExtra,
  PropertySyntaxExtra
}
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model._

import scala.collection.mutable.ListBuffer

object RamlUniverseProvider {

  def raml08Universe(): Future[IUniverse] = {
    Future {
      buildUniverse("RAML", "08", RAML08Universe.value)
    }
  }

  private def buildUniverse(vendor: String,
                            version: String,
                            content: String): IUniverse = {

    var builtins: Universe = new Universe(vendor, None, version)
    var result: Universe = new Universe(vendor, Some(builtins), version)
    var universeModel = new UniverseModel(result, builtins)

    var moduleNodes: ListBuffer[JSONWrapper] = ListBuffer()
    buildYamlDocument(content).foreach(
      yDoc =>
        YJSONWrapper(yDoc.node)
          .value(ARRAY)
          .foreach(_.foreach(moduleNodes += _)))

    moduleNodes.foreach(addModule(_, universeModel))
    universeModel.modules.values.foreach(processHierarchy)
    universeModel.modules.values.foreach(_.initClassesFromModels())
    universeModel.modules.values.foreach(fillClasses)
    universeModel.lockClasses()

    result
  }

  def processHierarchy(m: Module): Unit = {
    m.node
      .propertyValue("classes", ARRAY)
      .foreach(_.foreach(classNode => {
        classNode
          .propertyValue("name", STRING)
          .foreach(
            m.getClassModel(_)
              .foreach(cl => {

                classNode
                  .propertyValue("extends", ARRAY)
                  .foreach(
                    _.foreach(_.propertyValue("typeName", STRING).foreach(m
                      .getClassModel(_)
                      .foreach(cl.addSuperType))))

                classNode
                  .propertyValue("annotations", ARRAY)
                  .foreach(_.foreach(aNode =>
                    aNode.propertyValue("name", STRING) match {
                      case Some("MetaModel.superclasses") =>
                        aNode.propertyValue("arguments", ARRAY) match {
                          case Some(args1) =>
                            args1.foreach(_.value(ARRAY) match {
                              case Some(args2) =>
                                args2
                                  .flatMap(_.value(STRING))
                                  .foreach(
                                    m.getClassModel(_).foreach(cl.addSuperType))
                              case _ =>
                            })
                          case _ =>
                        }
                      case _ =>
                  }))
              }))
      }))
  }

  def fillClasses(m: Module): Unit = {
    m.node
      .propertyValue("classes", ARRAY)
      .foreach(_.foreach(classNode => {
        val classNameOpt = classNode.propertyValue("name", STRING)
        classNameOpt.foreach(
          m.getClass(_)
            .foreach(cl => {

              var clModel = m.getClassModel(cl.nameId.get).get
              clModel.superTypes.foreach(stModel => {
                m.getClass(stModel.name).foreach(cl.addSuperType)
              })

              cl match {
                case strType: StructuredType =>
                  classNode
                    .propertyValue("fields", ARRAY)
                    .foreach(_.foreach(pNode =>
                      pNode
                        .propertyValue("name", STRING)
                        .foreach(name => {
                          pNode
                            .propertyValue("type", OBJECT)
                            .foreach(tNode => {
                              var kind = tNode
                                .propertyValue("typeKind", NUMBER)
                                .getOrElse(0)
                              var rangeNameOpt: Option[String] = None
                              var prop: Option[Property] = None
                              kind match {
                                case 1 =>
                                  tNode
                                    .propertyValue("base", OBJECT)
                                    .foreach(_.propertyValue("typeName", STRING)
                                      .foreach(m
                                        .getClass(_)
                                        .foreach(range => {
                                          var arr = new Array(name)
                                          arr.setComponent(range)
                                          prop = Some(strType
                                            .addProperty(name, arr)
                                            .withMultiValue())
                                        })))
                                case _ =>
                                  tNode
                                    .propertyValue("typeName", STRING)
                                    .foreach(m
                                      .getClass(_)
                                      .foreach(r =>
                                        prop =
                                          Some(strType.addProperty(name, r))))
                              }
                              prop.foreach(processPropertyAnnotations(_, pNode))
                              pNode.propertyValue("optional", BOOLEAN) match {
                                case Some(false) =>
                                  prop.foreach(_.withRequired(true))
                                case _ =>
                              }
                            })
                        })))
                case _ =>
              }
            }))
      }))
  }

  def processPropertyAnnotations(prop: Property, node: JSONWrapper): Unit = {

    var syntaxExtra = PropertySyntaxExtra()
    node.propertyValue("annotations", ARRAY) match {
      case Some(arr) =>
        arr.foreach(a =>
          a.propertyValue("name", STRING) match {
            case Some("MetaModel.embeddedInMaps") =>
              syntaxExtra.setIsEmbeddedInMaps()
            case Some("MetaModel.embeddedInArray") =>
              syntaxExtra.setIsEmbeddedInArray()
            case Some("MetaModel.key")        => syntaxExtra.setIsKey()
            case Some("MetaModel.value")      => syntaxExtra.setIsValue()
            case Some("MetaModel.example")    => syntaxExtra.setIsExample()
            case Some("MetaModel.hideFromUI") => syntaxExtra.setIsHiddenFromUI()
            case Some("MetaModel.oneOf") =>
              var innerArray: Option[Seq[JSONWrapper]] = a
                .propertyValue("arguments", ARRAY)
                .flatMap(_.headOption)
                .flatMap(_.value(ARRAY))
              innerArray.foreach(arr => syntaxExtra.setEnum(arr.map(_.value)))
            case Some("MetaModel.oftenKeys") =>
              var innerArray: Option[Seq[JSONWrapper]] = a
                .propertyValue("arguments", ARRAY)
                .flatMap(_.headOption)
                .flatMap(_.value(ARRAY))
              innerArray.foreach(arr =>
                syntaxExtra.setOftenValues(arr.map(_.value)))
            case Some("MetaModel.parentPropertiesRestriction") =>
              var innerArray: Option[Seq[JSONWrapper]] = a
                .propertyValue("arguments", ARRAY)
                .flatMap(_.headOption)
                .flatMap(_.value(ARRAY))
              innerArray.foreach(arr =>
                syntaxExtra
                  .setParentPropertiesRestriction(arr.flatMap(_.value(STRING))))
            case Some("MetaModel.description") =>
              val textOpt: Option[String] = a
                .propertyValue("arguments", ARRAY)
                .flatMap(_.headOption)
                .flatMap(_.value(STRING))
              textOpt.foreach(text => {
                prop.putExtra(DescriptionExtra, DescriptionExtra(text))
              })
            case _ =>
        })
      case _ =>
    }
    if (syntaxExtra.isSufficient) {
      prop.putExtra(PropertySyntaxExtra, syntaxExtra)
    }
  }

  def addModule(node: JSONWrapper, u: UniverseModel): Unit =
    node
      .propertyValue("name")
      .foreach(_.value(STRING)
        .foreach(u
          .newModule(_, node)
          .foreach(m => {
            node
              .propertyValue("classes")
              .foreach(_.value(ARRAY)
                .foreach(_.foreach(registerClassModel(_, m))))

            node
              .propertyValue("imports")
              .foreach(iNode =>
                iNode.propertyNames.foreach(ns =>
                  iNode.propertyValue(ns, STRING).foreach(m.addImport(ns, _))))
          })))

  def registerClassModel(node: JSONWrapper, m: Module): Unit =
    node
      .propertyValue("name")
      .foreach(
        _.value(STRING)
          .foreach(name => {
            var aliases: ListBuffer[String] = ListBuffer()
            var isAvailableToUser = false
            node
              .propertyValue("annotations", ARRAY)
              .foreach(_.foreach(aNode =>
                aNode.propertyValue("name", STRING) match {
                  case Some("MetaModel.alias") =>
                    aNode
                      .propertyValue("arguments", ARRAY)
                      .foreach(_.foreach(_.value(STRING).foreach(aliases += _)))
                  case Some("MetaModel.availableToUser") =>
                    isAvailableToUser = true
                  case _ =>
              }))
            var cl = m.createClassModel(name, isAvailableToUser)
            aliases.foreach(m.registerModelAlias(_, cl))
          }))

  private def buildYamlDocument(content: String): Option[YDocument] = {
    val parser = YamlParser(content).withIncludeTag("!include")
    parser.documents().headOption
    val parts = parser.parse(true)
    parts.find(_.isInstanceOf[YDocument]).asInstanceOf[Option[YDocument]]
  }

  private class Module(val universeModel: UniverseModel,
                       val path: String,
                       val node: JSONWrapper) {

    val imports: scala.collection.mutable.Map[String, String] =
      scala.collection.mutable.Map[String, String]()

    val classes: scala.collection.mutable.Map[String, AbstractType] =
      scala.collection.mutable.Map[String, AbstractType]()

    val classModels: scala.collection.mutable.Map[String, TypeModel] =
      scala.collection.mutable.Map[String, TypeModel]()

    def lockClasses(): Unit =
      classes.values.foreach(cl => {
        cl.lock()
        cl.putExtra(BuiltInExtra)
      })

    def addClass(cl: AbstractType): Unit =
      cl.nameId.foreach(name => {
        classes.put(name, cl)
        universeModel.universe.register(cl)
      })

    def registerAlias(name: String, cl: AbstractType): Unit = {
      classes.put(name, cl)
      universeModel.universe.registerAlias(name, cl)
    }

    def registerModelAlias(name: String, cl: TypeModel): Unit = {
      if (!cl.aliases.contains(name)) {
        cl.aliases += name
      }
      classModels.put(name, cl)
    }

    def addImport(namespace: String, path: String): Unit =
      imports.put(namespace, path)

    def createClass(classModel: TypeModel): AbstractType = {
      val name = classModel.name
      if (classes.contains(name)) {
        throw new Error(s"Class $name is duplicated in $path")
      }
      var targetUniverse = universeModel.universe
      if (classModel.isAvailableToUser) {
        targetUniverse = universeModel.parentUniverse
      }
      val isValueType = classModel.isAssigNamleForm("ValueType")
      val result =
        if (isValueType) new ValueType(name, targetUniverse)
        else new StructuredType(name, targetUniverse)
      targetUniverse.register(result)
      classModel.aliases.foreach(registerAlias(_, result))
      classes.put(name, result)
      result
    }

    def createClassModel(name: String,
                         isAvailableToUser: Boolean): TypeModel = {
      if (classModels.contains(name)) {
        throw new Error(s"Class $name is duplicated in $path")
      }
      var result = new TypeModel(name, isAvailableToUser)
      classModels.put(name, result)
      result
    }

    def initClassesFromModels(): Unit = {
      var _classModels = scala.collection.mutable
        .Map[String, TypeModel]() ++= classModels
      classModels.values.foreach(_.aliases.foreach(_classModels.remove(_)))
      _classModels.values.foreach(createClass)
    }

    def getClass(name: String): Option[AbstractType] = {
      var ind = name.indexOf('.')
      if (ind >= 0) {
        var actualName = name.substring(ind + 1)
        var namespace = name.substring(0, ind)
        imports.get(namespace) match {
          case Some(iPath) =>
            universeModel.module(iPath) match {
              case Some(m) => m.getClass(actualName)
              case _       => throw new Error(s"Unknown module path: $iPath")
            }
          case _ =>
            throw new Error(s"Unknown namespace used in $path: $namespace")
        }
      } else {
        classes.get(name) match {
          case Some(cl) => Some(cl)
          case None     => universeModel.getClass(name)
        }
      }
    }

    def getClassModel(name: String): Option[TypeModel] = {
      var ind = name.indexOf('.')
      if (ind >= 0) {
        var actualName = name.substring(ind + 1)
        var namespace = name.substring(0, ind)
        imports.get(namespace) match {
          case Some(iPath) =>
            universeModel.module(iPath) match {
              case Some(m) => m.getClassModel(actualName)
              case _       => throw new Error(s"Unknown module path: $iPath")
            }
          case _ =>
            throw new Error(s"Unknown namespace used in $path: $namespace")
        }
      } else {
        classModels.get(name) match {
          case Some(cl) => Some(cl)
          case None     => universeModel.getClassModel(name)
        }
      }
    }
  }

  private class UniverseModel(val universe: Universe,
                              val parentUniverse: Universe) {
    val modules: scala.collection.mutable.Map[String, Module] =
      scala.collection.mutable.Map[String, Module]()

    def module(path: String): Option[Module] = modules.get(path)

    def newModule(path: String, node: JSONWrapper): Option[Module] = {
      val module = new Module(this, path, node)
      modules.put(path, module)
      Some(module)
    }

    def getClass(name: String): Option[AbstractType] = {
      var result: Option[AbstractType] = None
      modules.values.find(m => {
        m.classes.get(name) match {
          case Some(cl) =>
            result = Some(cl)
            true
          case None => false
        }
      })
      result
    }

    def getClassModel(name: String): Option[TypeModel] = {
      var result: Option[TypeModel] = None
      modules.values.find(m => {
        m.classModels.get(name) match {
          case Some(cl) =>
            result = Some(cl)
            true
          case None => false
        }
      })
      result
    }

    def lockClasses(): Unit = modules.values.foreach(_.lockClasses())
  }

  private class TypeModel(val name: String, val isAvailableToUser: Boolean) {

    val superTypes: ListBuffer[TypeModel] = ListBuffer()

    val aliases: ListBuffer[String] = ListBuffer()

    def addSuperType(st: TypeModel): Unit = superTypes += st

    def isAssigNamleForm(n: String): Boolean =
      name == n || superTypes.exists(_.isAssigNamleForm(n))
  }

}
