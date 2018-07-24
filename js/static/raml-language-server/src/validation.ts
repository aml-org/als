// /// <reference path="../typings/main.d.ts" />
// import parser = require("raml-1-parser");
// import path = require("path");
//
// import utils = parser.utils;
//
// import projectApi = parser.project;
//
// setInterval(() => null, 10);
//
// var dummyProject: any = parser.project.createProject('');
//
// process.on('message', (data: any) => {
//     try {
//         ast(data.ramlPath, data.content).then((hlNode:parser.hl.IHighLevelNode) => {
//             if(!(hlNode && hlNode.lowLevel && hlNode.lowLevel())) {
//                 process.send({
//                     taskId: data.taskId,
//                     clientId: data.clientId,
//                     error: "no data",
//                     errors: []
//                 });
//
//                 return;
//             }
//
//             var errors = gatherValidationErrors(hlNode, data.ramlPath);
//
//             process.send({
//                 taskId: data.taskId,
//                 clientId: data.clientId,
//                 errors: errors || []
//             });
//         }).catch(error => {
//             process.send({
//                 taskId: data.taskId,
//                 clientId: data.clientId,
//                 error: error.message,
//                 errors: []
//             });
//         })
//     } catch(exception) {
//         process.send({
//             taskId: data.taskId,
//             clientId: data.clientId,
//             error: exception.message,
//             errors: []
//         });
//     }
// });
//
// function ast(ramlPath: string, content: string): Promise<parser.IHighLevelNode> {
//     return parser.parseRAML(content, {
//         filePath: ramlPath,
//         fsResolver: dummyProject.resolver,
//         httpResolver: dummyProject._httpResolver
//     }).then((api: parser.hl.BasicNode) => api.highLevel());
// }
//
// function gatherValidationErrors(astNode: parser.IHighLevelNode, ramlPath: string) {
//     if (astNode) {
//         var acceptor = new Acceptor(ramlPath, [], astNode.root());
//
//         astNode.validate(acceptor);
//     }
//
//     return acceptor.getErrors();
// }
//
// class Acceptor extends utils.PointOfViewValidationAcceptorImpl {
//     constructor(private ramlPath: string, errors:any[], primaryUnit : parser.hl.IParseResult) {
//         super(errors, primaryUnit)
//     }
//
//     buffers:{[path:string]:any} = {}
//
//     getErrors(): any[] {
//         return this.errors;
//     }
//
//     accept(issue: parser.hl.ValidationIssue) {
//         if(!issue){
//             return;
//         }
//
//         this.transformIssue(issue);
//
//         var issueType = issue.isWarning ? "Warning" :"Error";
//
//         var issuesArray: parser.hl.ValidationIssue[] = [];
//
//         while(issue) {
//             issuesArray.push(issue);
//
//             if(issue.extras && issue.extras.length>0){
//                 issue = issue.extras[0];
//             } else {
//                 issue = null;
//             }
//         }
//
//         var issues = issuesArray.reverse().map(x=>{
//             var result = this.convertParserIssue(x,issueType);
//
//             issueType = "Trace";
//
//             return result;
//         });
//
//         for(var i = 0 ; i < issues.length - 1; i++){
//             issues[0].trace.push(issues[i + 1]);
//         }
//
//         var message = issues[0];
//
//         this.errors.push(message);
//     }
//
//     private convertParserIssue(x: parser.hl.ValidationIssue, iType:string): any {
//         var t = x.message;
//
//         var ps = x.path;
//
//         if(x.unit) {
//             ps = x.unit.absolutePath();
//         }
//
//         var trace = {
//             type: iType,
//             filePath: x.path ? ps : null,
//             text: t,
//             range: [x.start, x.end],
//             trace: [],
//         };
//
//         return trace;
//     }
//
//     acceptUnique(issue: parser.hl.ValidationIssue){
//         this.accept(issue);
//     }
//
//     end() {
//
//     }
// }
