package org.mulesoft.typesystem.typesystem_interfaces

trait IConstraint extends ITypeFacet {
    def composeWith(r: IConstraint): Option[IConstraint]
}
