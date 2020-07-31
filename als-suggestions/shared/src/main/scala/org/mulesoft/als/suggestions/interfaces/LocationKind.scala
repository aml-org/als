package org.mulesoft.als.suggestions.interfaces

sealed class LocationKind {}

object LocationKind {

  object VALUE_COMPLETION extends LocationKind

  object KEY_COMPLETION extends LocationKind

  object PATH_COMPLETION extends LocationKind

  object DIRECTIVE_COMPLETION extends LocationKind

  object VERSION_COMPLETION extends LocationKind

  object ANNOTATION_COMPLETION extends LocationKind

  object SEQUENCE_KEY_COPLETION extends LocationKind

  object INCOMMENT extends LocationKind
}
