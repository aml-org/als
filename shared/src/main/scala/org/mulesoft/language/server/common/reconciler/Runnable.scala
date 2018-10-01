// $COVERAGE-OFF$
package org.mulesoft.language.server.common.reconciler;

import amf.core.model.document.BaseUnit

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success};

import scala.concurrent.ExecutionContext.Implicits.global

trait Runnable[ResultType] {
	def run(): Promise[ResultType];
	
	def conflicts(other: Runnable[Any]): Boolean;
	
	def cancel(): Unit;
	
	def isCanceled(): Boolean;
}

class TestRunnable(var message: String, var kind: String) extends Runnable[String] {
	private var canceled = false;
	
	def run(): Promise[String] = Promise().success(message);
	
	def conflicts(other: Runnable[Any]): Boolean = other.asInstanceOf[TestRunnable].kind == kind;
	
	def cancel() {
		canceled = true;
	}
	
	def isCanceled(): Boolean = canceled;
}

// $COVERAGE-ON$