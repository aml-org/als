package org.mulesoft.language.common.dtoTypes

case class ISuggestion (
    /**
      * Full text to insert, including the index.
      */
    text: String,

    /**
      * Description of the suggestion.
      */
    description: Option[String],

    /**
      * Text to display.
      */
    displayText: Option[String],
    /**
      * Detected suggestion prefix.
      */
    prefix: Option[String],

    /**
      * Suggestion category.
      */
    category: Option[String]
)
{

}
