package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.ISuggestion

class Suggestion(_text:String,_description:String,_displayText:String, _prefix:String) extends ISuggestion{

    private var categoryOpt:Option[String] = None

    override def text: String = _text

    override def description: String = _description

    override def displayText: String = _displayText

    override def prefix: String = _prefix

    override def category: String = categoryOpt.getOrElse("unknown")

    def withCategory(cat:String):Suggestion = {
        categoryOpt = Option(cat)
        this
    }

    override def toString:String = text
}

object Suggestion {
    def apply(_text:String,_description:String,_displayText:String, _prefix:String):Suggestion
            = new Suggestion(_text,_description,_displayText,_prefix)
}
