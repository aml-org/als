package org.mulesoft.lsp.feature.command

/** Represents a reference to a command. Provides a title which will be used to represent a command in the UI. Commands
  * are identified by a string identifier. The recommended way to handle commands is to implement their execution on the
  * server side if the client and server provides the corresponding capabilities. Alternatively the tool extension code
  * could handle the command. The protocol currently doesn't specify a set of well-known commands.
  *
  * @param title
  *   Title of the command, like `save`.
  * @param command
  *   The identifier of the actual command handler.
  * @param arguments
  *   Arguments that the command handler should be invoked with.
  */
case class Command(title: String, command: String, arguments: Option[Seq[Any]])
