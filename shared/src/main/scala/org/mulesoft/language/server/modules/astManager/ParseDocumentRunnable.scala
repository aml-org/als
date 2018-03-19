//package org.mulesoft.language.server.server.modules.astManager
//
//
//class ParseDocumentRunnable extends Runnable[IHighLevelNode] {
//  var TYPE_CONST = "astManager.ParseDocumentRunnable"
//
//  def isInstance(runnable: Runnable[Any]): Boolean = {
//    return (((runnable.asInstanceOf[Any]).getTypeConst && (typeof(((runnable.asInstanceOf[Any]).getTypeConst)) === "function")) && (ParseDocumentRunnable.TYPE_CONST === (runnable.asInstanceOf[Any]).getTypeConst()))
//
//  }
//
//  var canceled = false
//
//  def this(uri: String, version: Int, editorManager: IEditorManagerModule, connection: IServerConnection, logger: ILogger) = {
//  }
//
//  def getTypeConst(): String = {
//    return ParseDocumentRunnable.TYPE_CONST
//
//  }
//
//  def `toString`(): String = {
//    return (((("[Runnable " + this.uri) + ":") + this.version) + "]")
//
//  }
//
//  def run(): Promise[IHighLevelNode] = {
//    val options = this.prepareParserOptions()
//    return this.parseAsynchronously(options)
//
//  }
//
//  def conflicts(other: Runnable[Any]): Boolean = {
//    if (ParseDocumentRunnable.isInstance(other)) {
//      return (other.getURI() === this.getURI())
//
//    }
//    return false
//
//  }
//
//  def cancel(): Unit = {
//    (this.canceled = true)
//
//  }
//
//  def isCanceled(): Boolean = {
//    return this.canceled
//
//  }
//
//  def getURI() = {
//    return this.uri
//
//  }
//
//  def prepareParserOptions(): Options = {
//    this.logger.debug("Running the parsing", "ParseDocumentRunnable", "prepareParserOptions")
//    val dummyProject: Any = parser.project.createProject(pathModule.dirname(this.uri))
//    val connection = this.connection
//    val logger = this.logger
//    val fsResolver = Map(
//
//    def content(path: Nothing) = {
//      logger.debug(("Request for path " + path), "ParseDocumentRunnable", "fsResolver#content")
//      logger.error("Should never be called", "ParseDocumentRunnable", "fsResolver#content")
//      return null
//
//    }
//
//    ,
//    def contentAsync(path: Nothing) = {
//      logger.debug(("Request for path " + path), "ParseDocumentRunnable", "fsResolver#contentAsync")
//      if ((path.indexOf("file://") === 0)) {
//        (path = path.substring(7))
//        logger.debugDetail(("Path changed to: " + path), "ParseDocumentRunnable", "fsResolver#contentAsync")
//
//      }
//      return connection.content(path)
//
//    }
//
//    )
//    var documentUri = this.uri
//    this.logger.debugDetail(("Parsing uri " + documentUri), "ParseDocumentRunnable", "prepareParserOptions")
//    if ((documentUri.indexOf("file://") === 0)) {
//      (documentUri = documentUri.substring(7))
//      this.logger.debugDetail(("Parsing uri changed to: " + documentUri), "ParseDocumentRunnable", "prepareParserOptions")
//
//    }
//    return Map("filePath" -> documentUri,
//      "fsResolver" -> fsResolver,
//      "httpResolver" -> dummyProject._httpResolver,
//      "rejectOnErrors" -> false)
//
//  }
//
//  def parseAsynchronously(parserOptions: Any): Promise[IHighLevelNode] = {
//    val editor = this.editorManager.getEditor(this.uri)
//    this.logger.debugDetail(("Got editor: " + ((editor != null))), "ParseDocumentRunnable", "parseAsynchronously")
//    if ((!editor)) {
//      return parser.loadRAML(parserOptions.filePath, Array(), parserOptions).then(((api: BasicNode) => {
//        this.logger.debug(("Parsing finished, api: " + ((api != null))), "ParseDocumentRunnable", "parseAsynchronously")
//        return api.highLevel()
//
//      }), (error => {
//        this.logger.debug(("Parsing finished, ERROR: " + error), "ParseDocumentRunnable", "parseAsynchronously")
//        throw error
//      }))
//
//    }
//    else {
//      this.logger.debugDetail(("EDITOR text:\n" + editor.getText()), "ParseDocumentRunnable", "parseAsynchronously")
//      return parser.parseRAML(editor.getText(), parserOptions).then(((api: BasicNode) => {
//        this.logger.debug(("Parsing finished, api: " + ((api != null))), "ParseDocumentRunnable", "parseAsynchronously")
//        return api.highLevel()
//
//      }), (error => {
//        this.logger.debug(("Parsing finished, ERROR: " + error), "ParseDocumentRunnable", "parseAsynchronously")
//        throw error
//      }))
//
//    }
//
//  }
//}
