package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.aml.DialectLevelSuggestionsTest

class AsyncAPILevelSuggestionsTest extends DialectLevelSuggestionsTest {

  val fixture: Seq[TestCase] = Seq(
    TestCase(
      filePath("test2Lv.yaml"),
      Seq(
        TestCaseLabel("[*0]", Some("InfoObject"), 2),
        TestCaseLabel("[*1]", Some("ServerObject"), 2),
        TestCaseLabel("[*2]", Some("ExternalDocumentationObject"), 2)
      )
    ),
    TestCase(filePath("testRoot.yaml"),
             Seq(
               TestCaseLabel("[*0]", None, 1)
             )),
    TestCase(
      filePath("test3Lv.yaml"),
      Seq(
        TestCaseLabel("[*0]", Some("LicenseObject"), 3),
        TestCaseLabel("[*1]", Some("ContactObject"), 3),
//        TestCaseLabel("[*2]", Some("MessageObject"), 3),
//        TestCaseLabel("[*3]", Some("SchemaObject"), 3),
//        TestCaseLabel("[*4]", Some("TopicItemObject"), 3),
//        TestCaseLabel("[*5]", Some("SecuritySchemeObject"), 3),
      )
    )
  )

  fixture.foreach { f =>
    test(s"test ${f.path}") {
      for {
        (content, cases) <- adaptContent(f.path, f.labels)
        bu <- parse(content)
        results <- assertCases(bu, cases, content)
      } yield
        assert {
          results
            .filter(!_.succeed)
            .map { r =>
              r.message.reduce { (m1, m2) =>
                s"$m1\n- Failed test for ${f.path} - ${r.dialectClass}:\n\t$m2"
              }
            }
            .mkString("\n\n") isEmpty
        }
    }
  }

  // add different suit for oas with dialects??
  override def format: String = Aml.toString

  override def rootPath: String = "AML/AsyncAPI/full"

}
