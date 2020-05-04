package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory

case class AmlBuilderFactory(override val dialect: Dialect) extends BuilderFactory