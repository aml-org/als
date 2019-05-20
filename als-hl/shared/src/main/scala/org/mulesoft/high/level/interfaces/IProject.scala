package org.mulesoft.high.level.interfaces

import amf.core.remote.Vendor
import org.mulesoft.high.level.implementation.ASTUnit
import org.mulesoft.typesystem.project.ITypeCollectionBundle

import scala.collection.Map

trait IProject {

  def rootASTUnit: IASTUnit

  def rootPath: String

  def units: Map[String, ASTUnit]

  def types: ITypeCollectionBundle

  def language: Vendor

  def resolve(absBasePath: String, path: String): Option[IASTUnit]

  def resolvePath(path: String, p: String): Option[String]
}
