package org.mulesoft.language.common.typeInterfaces

/**
  * Validation report.
  */
case class IValidationReport (

  /**
    * This is the "point of view" uri, actual reported unit paths are located
    * in the particular issues.
    */
  var pointOfViewUri: String,

  /**
    * Optional document version of the point of view.
    */
  var version: Int,

  /**
    * Validation issues.
    */
  var issues: Seq[IValidationIssue]
)
{

}
