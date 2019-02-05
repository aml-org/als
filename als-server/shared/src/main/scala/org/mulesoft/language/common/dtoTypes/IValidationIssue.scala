package org.mulesoft.language.common.dtoTypes

/**
  * Validation issue: error or warning
  */
case class IValidationIssue(
    /**
      * Error code
      */
    var code: String,
    /**
      * Error type.
      */
    var `type`: String,
    /**
      * Document uri. Legacy: to be renamed to uri.
      */
    var filePath: String,
    /**
      * Issue human-readable text.
      */
    var text: String,
    /**
      * Range producing the issue.
      */
    var range: IRange,
    /**
      * Subsequent validation issues
      */
    var trace: Seq[IValidationIssue]
) {}
