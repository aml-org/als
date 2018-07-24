// /// <reference path="../typings/main.d.ts" />
// import childProcess = require("child_process");
//
// import path = require("path");
//
// var shortid = require('shortid');
//
// var validationProcess = (<any>childProcess).fork(path.resolve(__dirname, './validation.js'), [], {
//     silent: true
// });
//
// class TaskQueue {
//     private tasks: any = {};
//
//     registerTask(task: Task): void {
//         var clientId: string = task.clientId;
//
//         var taskId: string = this.generateId(clientId);
//
//         task.id = taskId;
//
//         this.tasks[clientId][taskId] = task;
//     }
//
//     unregisterTask(task: Task): void {
//         var clientId: string = task.clientId;
//
//         var taskId: string = task.id;
//
//         if(!this.tasks[clientId]) {
//             return;
//         }
//
//         delete this.tasks[clientId][taskId];
//
//         if(Object.keys(this.tasks[clientId]).length === 0) {
//             delete this.tasks[clientId];
//         }
//     }
//
//     getTask(clientId: string, taskId: string): Task {
//         if(!this.tasks[clientId]) {
//             return null;
//         }
//
//         if(!this.tasks[clientId][taskId]) {
//             return null;
//         }
//
//         return <Task>(this.tasks[clientId][taskId]);
//     }
//
//     getClientTasks(clientId: string): Task[] {
//         if(!this.tasks[clientId]) {
//             return [];
//         }
//
//         var tasks = this.tasks[clientId];
//
//         return Object.keys(tasks).map(key => {
//             return tasks[key];
//         });
//     }
//
//     private generateId(clientId: string): string {
//         if(!this.tasks[clientId]) {
//             this.tasks[clientId] = {};
//         }
//
//         var taskId = shortid.generate();
//
//         while(this.tasks[clientId][taskId]) {
//             taskId = shortid.generate();
//         }
//
//         return taskId;
//     }
// }
//
// var taskQueue: any = new TaskQueue();
//
// export abstract class Task {
//     aborted: boolean = false;
//
//     id: string = undefined;
//
//     constructor(public clientId: string) {
//
//     }
//
//     run(wait: number = 0): void {
//         taskQueue.registerTask(this);
//
//         this.innerRun(wait);
//     }
//
//     abort() {
//         taskQueue.unregisterTask(this);
//
//         this.aborted = true;
//     }
//
//     performDone(data: any): void {
//         if(this.aborted) {
//             return;
//         }
//
//         taskQueue.unregisterTask(this);
//
//         this.innerDone(data);
//     }
//
//     abstract isIdenticalTask(task: Task): boolean;
//
//     abstract innerRun(wait: number): void;
//
//     abstract innerDone(data: any): void;
// }
//
// export class ValidationTask extends Task {
//     constructor(private ramlPath: string, private content: string,
//                 private done: (errors: any[]) => void, cliendId: string) {
//         super(cliendId);
//     }
//
//     innerRun(wait: number = 0) {
//         if(wait === 0) {
//             if(this.aborted) {
//                 return;
//             }
//
//             validationProcess.send({
//                 clientId: this.clientId,
//                 taskId: this.id,
//                 ramlPath: this.ramlPath,
//                 content: this.content
//             });
//
//             return;
//         }
//
//         setTimeout(() => {
//             if(this.aborted) {
//                 return;
//             }
//
//             validationProcess.send({
//                 clientId: this.clientId,
//                 taskId: this.id,
//                 ramlPath: this.ramlPath,
//                 content: this.content
//             });
//         }, wait);
//     }
//
//     innerDone(data: any) {
//         this.done(data.errors);
//     }
//
//     isIdenticalTask(task: Task): boolean {
//         var anotherTask: ValidationTask = <any>task;
//
//         if(this.ramlPath !== anotherTask.ramlPath) {
//             return false;
//         }
//
//         if(this.content !== anotherTask.content) {
//             return false;
//         }
//
//         if(this.clientId !== anotherTask.clientId) {
//             return false;
//         }
//
//         return true;
//     }
// }
//
// validationProcess.on('message', (response: any) => {
//     var task = taskQueue.getTask(response.clientId, response.taskId);
//
//     if(!task) {
//         return;
//     }
//
//     if(response.error) {
//         console.error("Vaidation process throwed: " + response.error);
//     }
//
//     task.performDone(response);
// });
//
// validationProcess.stdout.on('data', data => {
//     console.log("Validation process stdout: " + data.toString());
// });
//
// validationProcess.stderr.on('data', data => {
//     console.log("Validation process stderr: " + data.toString());
// });
//
// validationProcess.on('close', function (code) {
//     console.log('Validation process exited with code ' + code);
// });
//
// export function sheduleTask(task: Task) {
//     var clientId = task.clientId;
//
//     var clientTasks = taskQueue.getClientTasks(clientId);
//
//     clientTasks.forEach((clientTask: Task) => {
//         clientTask.abort();
//     });
//
//     task.run(200);
// }
