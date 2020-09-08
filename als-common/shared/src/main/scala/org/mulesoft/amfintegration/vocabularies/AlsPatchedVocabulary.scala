package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2.{AsyncApi20ComponentsKeyTerm, AsyncApi20IdKeyTerm, AsyncApi20InfoKeyTerm}
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas2.{Oas2BasePathKeyTerm, Oas2HostKeyTerm, Oas2InfoKeyTerm, Oas2PathsKeyTerm}
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas3.{Oas3ComponentsKeyTerm, Oas3InfoKeyTerm, Oas3PathsKeyTerm}
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08.{Raml08BaseUriParametersKeyTerm, Raml08SecuredByKeyTerm}
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml10.Raml10UsesKeyTerm

object AlsPatchedVocabulary extends VocabularyObject {
  override protected def base: String = "http://als.patched/#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    oas2terms ++ oas3terms ++ asyncApi2terms ++ raml08terms ++ raml10terms

  val oas2terms = Seq(
    Oas2InfoKeyTerm,
    Oas2PathsKeyTerm,
    Oas2BasePathKeyTerm,
    Oas2HostKeyTerm,
  )

  val oas3terms = Seq(
    Oas3InfoKeyTerm,
    Oas3ComponentsKeyTerm,
    Oas3PathsKeyTerm
  )

  val asyncApi2terms = Seq(
    AsyncApi20ComponentsKeyTerm,
    AsyncApi20IdKeyTerm,
    AsyncApi20InfoKeyTerm
  )

  val raml08terms = Seq(
    Raml08SecuredByKeyTerm,
    Raml08BaseUriParametersKeyTerm
  )

  val raml10terms = Seq(
    Raml10UsesKeyTerm
  )
}
