# Outline module

## Interface

Here is a sample code for invoking scala outline.
```
val config = ConfigFactory.getConfig(new ASTProvider(ast, position, language))

    if (config.isDefined) {

      val categories = new StructureBuilder(config.get).getStructureForAllCategories

      val result = new mutable.HashMap[String, StructureNodeJSON]()
      categories.keySet.foreach(categoryName=>{
        result(categoryName) = categories(categoryName).toJSON
      })

      result.toMap
    }
```

First we need to create configuration, the main argument is AST provider:
```

/**
  * Provides AST, this module operates upon
  */
trait IASTProvider {

  /**
    * Returns the root of AST
    * @return
    */
  def getASTRoot: Option[IHighLevelNode]

  /**
    * Returns selected node.
    * @return
    */
  def getSelectedNode: Option[IParseResult]

  def language: String
}
```
AST provider should be able to provide AST and selected node for the document.

After that structure builder is being constructed from config and called to get the structure categories.
The result is a map from category identifier to `StructureNode`.

`StrcutureNode` may contain redundant information for pure structure use, its easier to just convert it to JSON obbjects by calling `toJSON`, getting `StructureNodeJSON` as a result.
 
```
trait StructureNodeJSON {
   /**
     * Node label text to be displayed.
     */
   def text: String
   /**
     * Node type label, if any.
     */
   def typeText: Option[String]
   /**
     * Node icon. Structure module is not setting up, how icons are represented in the client
     * system, or what icons exist,
     * instead the client is responsible to configure the mapping from nodes to icon identifiers.
     */
   def icon: String
   /**
     * Text style of the node. Structure module is not setting up, how text styles are represented in the client
     * system, or what text styles exist,
     * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
     */
   def textStyle: String
   /**
     * Unique node identifier.
     */
   def key: String
   /**
     * Node start position from the beginning of the document.
     */
   def start: Int
   /**
     * Node end position from the beginning of the document.
     */
   def end: Int
   /**
     * Whether the node is selected.
     */
   def selected: Boolean
   /**
     * Node children.
     */
   def children: Seq[StructureNodeJSON]
   /**
     * Node category, if determined by a category filter.
     */
   def category: String
}
```

`text` and `typeText` contain structure node text and structure node type.
`icon` and `textStyle` contain icon and text style identifiers (as provided by the respective language plug-in).
`start` and `end` set up the offset of the node in text counting from 0.
`key` contains unique node key, and `category` points to the node category identifier provided by the category classifier set up by the language plug-in. 


## Developing new plugins / supporting new languages

TODO
