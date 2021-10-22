package org.mulesoft.amfintegration

/**
  * @param initializedDialects a list of session-wide dialects set on server initialization
  */
//class AlsDialectsRegistry(initializedDialects: List[DialectWithMediaType]) {
//
//  /*
//    def registerWebApiDialect(vendor: Vendor, d: Dialect): Unit = registry.addWebApiDialect(d, vendor)
//
//  def registerWebApiDialect(vendor: Vendor): Unit = vendor match {
//    case Raml10 => registerWebApiDialect(Raml10, Raml10TypesDialect()) // no se registran en AMF configuration, se mantienen en un wrapper propio
//    case Raml08 => registerWebApiDialect(Raml08, Raml08TypesDialect())
//    case Oas30 =>
//      registerWebApiDialect(Oas30, OAS30Dialect())
//    case Oas20 =>
//      registerWebApiDialect(Oas20, OAS20Dialect())
//    case AsyncApi20 =>
//      registerWebApiDialect(AsyncApi20, AsyncApi20Dialect())
//    case _ => // ignore
//  }
//
//   */
//
//  private def allDialects: Seq[DialectWithMediaType] = alsDialects.toSeq
//
//  private def dialectFor(unit: DialectInstanceUnit): Option[Dialect] = None
//
//  private val alsDialects: mutable.Set[DialectWithMediaType] = mutable.Set.empty
//
//  def addWebApiDialect(d: DialectWithMediaType): Unit = alsDialects.add(d)
//
//  def amlAndWebApiDialects: Iterable[DialectWithMediaType] = alsDialects ++ allDialects
//
//  private def dialectForMediaType(mt: String): Option[Dialect] = alsDialects.find(_.mediaType == mt).map(_.dialect)
//
//  def dialectForUnit(bu: BaseUnit): Option[Dialect] = bu match {
//    case _: Dialect               => Some(MetaDialect.dialect)
//    case _: Vocabulary            => Some(VocabularyDialect.dialect)
//    case diu: DialectInstanceUnit => dialectFor(diu)
//    case _                        => dialectForMediaType(bu.sourceMediaType)
//  }
//
//
//  def registerDialect(content: String, configuration: AMLConfiguration): Future[Dialect] = {
//    val loader = StringResourceLoader(nextDialectUri(), content)
//    registerDialect(loader.url, configuration.withResourceLoader(loader))
//  }
//
//  private val counter = new IdCounter()
//
//  private def nextUri = s"file://${counter.genId("temp-dialect")}.yaml"
//
//  private def nextDialectUri() = {
//    var next = nextUri
//    while (allDialects.map(_.id).contains(next)) next = nextUri
//    next
//  }
//}
