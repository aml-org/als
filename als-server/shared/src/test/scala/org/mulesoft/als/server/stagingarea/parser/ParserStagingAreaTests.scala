package org.mulesoft.als.server.stagingarea.parser

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.logger.MessageSeverity.MessageSeverity
import org.mulesoft.als.server.modules.ast.{CHANGE_FILE, CLOSE_FILE, NotificationKind, OPEN_FILE}
import org.mulesoft.als.server.modules.workspace.ParserStagingArea
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocument}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class ParserStagingAreaTests extends FlatSpec with Matchers {
  private val dummyEnvironmentProvider = new EnvironmentProvider{

    override val amfConfiguration: AmfConfigurationWrapper = AmfConfigurationWrapper()

    override def openedFiles: Seq[String] = Seq.empty

    override def amfConfigurationSnapshot(): AmfConfigurationWrapper = amfConfiguration.branch

    override def initialize(): Future[Unit] = {Future.successful()}

    override def branch: EnvironmentProvider = ???

    override def filesInMemory: Map[String, TextDocument] = ???
  }

  behavior of "ParserStagingArea simple file operation"

  private val uritest = "file://uritest.yaml"
  it should "enqueue a new notification" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    psa.hasPending should be(false)
    psa.enqueue(uritest, OPEN_FILE)
    psa.contains(uritest) should be(true)
  }

  it should "enqueue many new notifications" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val input = Set(
      (uritest, OPEN_FILE),
      ("file://uritest1.yaml", CLOSE_FILE),
      ("file://uritest2.yaml", OPEN_FILE),
      ("file://uritest3.yaml", CHANGE_FILE)
    )
    psa.enqueue(input.toList)
    val resultList = ListBuffer[(String, NotificationKind)]()
    while(psa.hasPending)
      resultList += psa.dequeue()
    resultList.toSet should be(input)
  }

  it should "dequeue a notification" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val input = ("file://test.yaml", OPEN_FILE)
    psa.enqueue(input._1, input._2)
    psa.hasPending should be(true)
    psa.dequeue() should be(input)
  }

  behavior of "ParserStagingArea snapshot generation"

  it should "dequeue all" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val input = Set(
      (uritest, OPEN_FILE),
      ("file://uritest1.yaml", CLOSE_FILE),
      ("file://uritest2.yaml", OPEN_FILE),
      ("file://uritest3.yaml", CHANGE_FILE),
    )
    psa.enqueue(input.toList)
    val snapshot = psa.snapshot()
    psa.hasPending should be(false)
  }

  it should "have the same elements that were on the queue" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val input = Set(
      (uritest, OPEN_FILE),
      ("file://uritest1.yaml", CLOSE_FILE),
      ("file://uritest2.yaml", OPEN_FILE),
      ("file://uritest3.yaml", CHANGE_FILE),
    )
    psa.enqueue(input.toList)
    val snapshot = psa.snapshot()
    snapshot.files.toSet should be(input)
  }

  it should "remove all notifications for a URI but the last if it is CloseNotification" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val lastTuple = (uritest, CLOSE_FILE)
    val otherTuple = ("file://uritest1.yaml", OPEN_FILE)
    val input = List(
      (uritest, OPEN_FILE),
      otherTuple,
      lastTuple,
      (uritest, OPEN_FILE),
      (uritest, CHANGE_FILE),
      lastTuple,
    )
    psa.enqueue(input)
    val snapshot = psa.snapshot()
    snapshot.files.contains(lastTuple) should be(true)
    snapshot.files.contains(otherTuple) should be(true)
  }

  it should "remove all notifications for a URI but the last if it is ChangeNotification" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val lastTuple = (uritest, CHANGE_FILE)
    val otherTuple = ("file://uritest1.yaml", OPEN_FILE)
    val input = List(
      (uritest, OPEN_FILE),
      otherTuple,
      lastTuple,
      (uritest, OPEN_FILE),
      (uritest, CHANGE_FILE),
      lastTuple,
    )
    psa.enqueue(input)
    val snapshot = psa.snapshot()
    snapshot.files.contains(lastTuple) should be(true)
    snapshot.files.contains(otherTuple) should be(true)
  }

  it should "merge a CloseNotification with the OpenNotification as a ChangeNotification" in {
    val psa = new ParserStagingArea(dummyEnvironmentProvider, new TestLogger)
    val input = List(
      (uritest, CLOSE_FILE),
        (uritest, OPEN_FILE)
    )
    psa.enqueue(input)
    val snapshot = psa.snapshot()
    snapshot.files.contains( (uritest, CHANGE_FILE)) should be(true)
    snapshot.files.size should be(1)
  }

  it should "log a warning if MergeNotification is followed by OpenNotification, but keep the last one" in {
    val logger = new TestLogger
    val psa = new ParserStagingArea(dummyEnvironmentProvider, logger)
    val lastTuple = (uritest, OPEN_FILE)
    val input = List(
      (uritest, CHANGE_FILE),
      lastTuple
    )
    psa.enqueue(input)
    val snapshot = psa.snapshot()
    snapshot.files.contains(lastTuple) should be(true)
    logger.logs.contains(("warning", s"file opened without closing ${lastTuple._1}")) should be(true)
  }

  it should "log a warning if CloseNotification is followed by ChangeNotification, but keep the last one" in {
    val logger = new TestLogger
    val psa = new ParserStagingArea(dummyEnvironmentProvider, logger)
    val lastTuple = (uritest, CHANGE_FILE)
    val input = List(
      (uritest, CLOSE_FILE),
      lastTuple
    )
    psa.enqueue(input)
    val snapshot = psa.snapshot()
    snapshot.files.contains(lastTuple) should be(true)
    logger.logs.contains(("warning", s"file changed after closing ${lastTuple._1}")) should be(true)
  }

  class TestLogger extends Logger {
    private val list = ListBuffer[(String, String)]()
    def logs: Seq[(String, String)] = list
    override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit = ???
    override def debug(message: String, component: String, subComponent: String): Unit =
      list.append(("debug", message))
    override def warning(message: String, component: String, subComponent: String): Unit =
      list.append(("warning", message))
    override def error(message: String, component: String, subComponent: String): Unit = ???
  }
}
