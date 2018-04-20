//package org.mulesoft.language.server.server.modules.validationManager
//
//
//
//class Acceptor extends utils.PointOfViewValidationAcceptorImpl {
//  var buffers: {def apply(path: String): Any
//    /* def update() -- if you need it */
//  } = Map(
//  )
//  var foundIssues: Array[IValidationIssue] = Array()
//
//  def this(ramlPath: String, primaryUnit: IParseResult, logger: ILogger) = {
//    super (Array(), primaryUnit)
//
//  }
//
//  def getErrors(): Array[IValidationIssue] = {
//    return this.foundIssues
//
//  }
//
//  def accept(issue: ValidationIssue) = {
//    if ((!issue)) {
//      return
//
//    }
//    this.logger.debugDetail(("Accepting issue: " + issue.message), "ValidationManager", "accept")
//    this.transformIssue(issue)
//    var issueType = (if (issue.isWarning) "Warning" else "Error")
//    val issuesArray: Array[ValidationIssue] = Array()
//    while (issue) {
//      {
//        issuesArray.push(issue)
//        if ((issue.extras && (issue.extras.length > 0))) {
//          (issue = issue.extras(0))
//
//        }
//        else {
//          (issue = null)
//
//        }
//
//      }
//    }
//    val issues = issuesArray.reverse().map((x => {
//      val result = this.convertParserIssue(x, issueType)
//      (issueType = "Trace")
//      return result
//
//    })) {
//      var i = 0
//      while ((i < (issues.length - 1))) {
//        {
//          issues(0).trace.push(issues((i + 1)))
//
//        }
//        (i += 1)
//      }
//    }
//    val message = issues(0)
//    this.foundIssues.push(message)
//
//  }
//
//  def acceptUnique(issue: ValidationIssue) = {
//    this.accept(issue)
//
//  }
//
//  def end() = {
//  }
//
//  def convertParserIssue(originalIssue: ValidationIssue, issueType: String): IValidationIssue = {
//    val t = originalIssue.message
//    var ps = originalIssue.path
//    if (originalIssue.unit) {
//      (ps = originalIssue.unit.absolutePath())
//
//    }
//    val trace = Map("code" -> originalIssue.code,
//      "type" -> issueType,
//      "filePath" -> (if (originalIssue.path) ps else null),
//      "text" -> t,
//      "range" -> Map("start" -> originalIssue.start,
//        "end" -> originalIssue.end),
//      "trace" -> Array())
//    return trace
//
//  }
//}
