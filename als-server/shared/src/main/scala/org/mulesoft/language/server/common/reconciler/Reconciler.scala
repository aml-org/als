//package org.mulesoft.language.server.common.reconciler
//
//import org.mulesoft.language.server.common.logger.ILogger;
//import org.mulesoft.language.server.common.promise_polyfill.PromisePolyfillIndex
//import org.mulesoft.language.server.common.reconciler.Runnable;
//import org.mulesoft.language.server.common.reconciler.Reconciler;
//
//class Reconciler {
//  var waitingList: Array[Runnable[Any]] = Array()
//  var runningList: Array[Runnable[Any]] = Array()
//  def this(logger: ILogger, timeout: Int) = {
//}
//  def schedule[ResultType](runnable: Runnable[ResultType]): Promise[ResultType] = {
// this.addToWaitingList( runnable )
//return new Promise( (( resolve: ((ResultType) => Unit), reject: ((Any) => Unit) ) =>  {
// setTimeout( (() =>  {
// this.logger.debugDetail( ("Time came to execute "+runnable.`toString`()), "Reconciler", "schedule" )
// if (runnable.isCanceled()) {
// this.logger.debugDetail( (("Runnable "+runnable.`toString`())+" is cancelled, doing nothing"), "Reconciler", "schedule" )
//this.removeFromWaitingList( runnable )
//return
//
//}
// val currentlyRunning = this.findConflictingInRunningList( runnable )
// if (currentlyRunning) {
// this.logger.debugDetail( (((("Conflicting to "+runnable.`toString`())+" is found in the running list: ")+currentlyRunning.`toString`())+" rescheduling current one."), "Reconciler", "schedule" )
//this.schedule( runnable )
//return
//
//}
// this.removeFromWaitingList( runnable )
// this.addToRunningList( runnable )
// this.logger.debugDetail( ("Executing "+runnable.`toString`()), "Reconciler", "schedule" )
// this.run( runnable ).then( (result =>  {
// resolve( result )
//
//}), (error =>  {
// reject( error )
//
//}) )
//
//}), this.timeout )
//
//}) )
//
//}
//  def run[ResultType](runnable: Runnable[ResultType]): Promise[ResultType] = {
// return runnable.run().then( (result =>  {
// this.removeFromRunningList( runnable )
// return result
//
//}), (error =>  {
// this.removeFromRunningList( runnable )
// throw error
//}) )
//
//}
//  def addToWaitingList[ResultType](runnable: Runnable[ResultType]) = {
// this.logger.debugDetail( (("Adding runnable "+runnable.`toString`())+" to waiting list"), "Reconciler", "addToWaitingList" )
//(this.waitingList=this.waitingList.filter( (current =>  {
// this.logger.debugDetail( ((("Comparing existing runnable "+current.`toString`())+" to the new ")+runnable.`toString`()), "Reconciler", "addToWaitingList" )
// val conflicts = runnable.conflicts( current )
// if (conflicts) {
// this.logger.debugDetail( "Runnables are conflicting, canceling existing one", "Reconciler", "addToWaitingList" )
//current.cancel()
//
//}
// return (!conflicts)
//
//}) ))
//this.waitingList.push( runnable )
//
//}
//  def removeFromWaitingList[ResultType](runnable: Runnable[ResultType]) = {
// this.logger.debugDetail( (("Removing "+runnable.`toString`())+" from waiting list"), "Reconciler", "removeFromWaitingList" )
//val index = this.waitingList.indexOf( runnable )
//if ((index!==(-1))) {
// this.waitingList.splice( index, 1 )
//
//}
//
//}
//  def addToRunningList[ResultType](runnable: Runnable[ResultType]) = {
// this.logger.debugDetail( (("Adding "+runnable.`toString`())+" to running list"), "Reconciler", "removeFromWaitingList" )
//this.runningList.push( runnable )
//
//}
//  def removeFromRunningList[ResultType](runnable: Runnable[ResultType]) = {
// this.logger.debugDetail( (("Removing "+runnable.`toString`())+" from running list"), "Reconciler", "removeFromWaitingList" )
//val index = this.runningList.indexOf( runnable )
//if ((index!==(-1))) {
// this.runningList.splice( index, 1 )
//
//}
//
//}
//  def findConflictingInRunningList[ResultType](runnable: Runnable[ResultType]): Runnable[ResultType] = {
// (this.runningList).foreach { fresh1 =>
//val current = zeroOfMyType
// = fresh1
// {
// if (runnable.conflicts( current )) {
// return current
//
//}
//
//}
//}
//return null
//
//}
//}
