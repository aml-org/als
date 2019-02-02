package org.mulesoft.high.level.implementation

import amf.core.remote.Vendor
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.typesystem.project.ITypeCollectionBundle

import scala.collection.mutable

class Project(bundle: ITypeCollectionBundle, _lang: Vendor, _platform: AlsPlatform) extends IProject {

  private var _rootASTUnit: Option[ASTUnit] = None

  private var _units: mutable.Map[String, ASTUnit] = mutable.Map()

  override def rootASTUnit: ASTUnit = _rootASTUnit.get

  override def rootPath: String = _rootASTUnit.get.path

  override def units: collection.Map[String, ASTUnit] = _units

  override def types: ITypeCollectionBundle = bundle

  override def platform: AlsPlatform = _platform

  def setRootUnit(u: ASTUnit): Unit = {
    _rootASTUnit = Some(u)
    addUnit(u)
  }

  def addUnit(u: ASTUnit): Unit = _units.put(u.path, u)

  override def language: Vendor = _lang

  override def resolve(absBasePath: String, path: String): Option[ASTUnit] = {

    var resolvedPath = resolvePath(absBasePath, path)
    resolvedPath match {
      case Some(x) => _units.get(x)
      case None    => None
    }
  }

  override def resolvePath(absBasePath: String, path: String): Option[String] =
    _platform.resolvePath(absBasePath, path)
}

object Project {

  def apply(bundle: ITypeCollectionBundle, lang: Vendor, platform: AlsPlatform): Project =
    new Project(bundle, lang, platform)
}
