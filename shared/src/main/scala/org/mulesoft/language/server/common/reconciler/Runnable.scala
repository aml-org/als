//package org.mulesoft.language.server.common.reconciler
//
//import org.mulesoft.language.server.common.logger.ILogger;
//import org.mulesoft.language.server.common.promise_polyfill.PromisePolyfillIndex
//import org.mulesoft.language.server.common.reconciler.Runnable;
//import org.mulesoft.language.server.common.reconciler.Reconciler;
//
//trait Runnable[ResultType] {
//  def run(): Promise[ResultType]
//  def conflicts(other: Runnable[Any]): Boolean
//  def cancel(): Unit
//  def isCanceled(): Boolean
//}
