package org.mulesoft.typesystem.typesystem_interfaces

import MetaInformationKind._

trait ITypeFacet {

    def facetName: String

    def requiredType: Option[IParsedType]

    def owner: Option[IParsedType]

    def isInheritable: Boolean

    def validateSelf(registry: ITypeRegistry): IStatus

    def value: Any

    def kind: MetaInformationKind

    def annotations: Seq[IAnnotation]

    def isConstraint: Boolean

}
