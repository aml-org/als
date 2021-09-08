package org.mulesoft.als.logger

/**
  * Logger configuration / settings
  */
trait LoggerSettings {

  /**
    * If true, disables all logging.
    */
  var disabled: Option[Boolean]

  /**
    * List of components, which are allowed to appear in log.
    * If empty or absent, all components are allowed (except those excplicitly denied).
    */
  var allowedComponents: Option[Seq[String]]

  /**
    * Components, which never appear in the log
    */
  var deniedComponents: Option[Seq[String]]

  /**
    * Messages with lower severity will not appear in log.
    */
  var maxSeverity: Option[MessageSeverity.Value]

  /**
    * Messages having more length will be cut off to this number.
    */
  var maxMessageLength: Option[Int]
}
