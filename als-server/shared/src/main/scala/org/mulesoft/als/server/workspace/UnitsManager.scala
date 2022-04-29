package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.ast.{AccessUnits, AstListener}
import org.mulesoft.amfintegration.UnitWithNextReference
import org.mulesoft.lsp.Initializable

import scala.concurrent.Future

/** UnitsManager provides the functionality for Listeners to subscribe, and receive a notification with each new
  * available Unit
  */
trait UnitsManager[UnitType <: UnitWithNextReference, ListenerType <: AstListener[_]]
    extends UnitAccessor[UnitType]
    with Initializable {

  def subscribers: List[ListenerType]

  val dependencies: List[AccessUnits[UnitType]]

  override def initialize(): Future[Unit] =
    Future.successful(dependencies.foreach(d => d.withUnitAccessor(this)))
}
