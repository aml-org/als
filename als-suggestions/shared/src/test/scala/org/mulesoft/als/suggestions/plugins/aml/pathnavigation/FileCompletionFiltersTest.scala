package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FileCompletionFiltersTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach with AlsPlatformSecrets {

  override def beforeEach(): Unit = {
    super.beforeEach()
    FileCompletionFilters.clearFilters()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    FileCompletionFilters.clearFilters()
  }

  behavior of "default FileCompletionFilters"

  it should "filter the current directory" in {
    val result = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/file.raml", "/full/Uri", FileWithType("file.raml", isDirectory = false), platform))
    result shouldBe true
  }

  it should "filter by file extension - supported" in {
    val result = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/full/Uri", FileWithType("file.raml", isDirectory = false), platform))
    result shouldBe true
  }

  it should "filter by file extension - exception" in {
    val result = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/full/Uri", FileWithType("file.xml", isDirectory = false), platform))
    result shouldBe true
  }

  it should "filter by file extension - not supported" in {
    val result = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/full/Uri", FileWithType("file.other", isDirectory = false), platform))
    result shouldBe false
  }


  behavior of "custom FileCompletionFilters"

  it should "validate a file name" in {
    FileCompletionFilters.addFilter({
      case PredicateParams(_, _, FileWithType("descriptor.json", false), _) => false
      case _ => true
    })
    val notExcluded = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/full/Uri", FileWithType("random.json", isDirectory = false), platform))
    notExcluded shouldBe true
    val excluded = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/full/Uri", FileWithType("descriptor.json", isDirectory = false), platform))
    excluded shouldBe false
  }

  it should "validate a directory" in {
    FileCompletionFilters.addFilter({
      case PredicateParams(_, "/exclude-dir", FileWithType("exclude-subdir", true), _) => false
      case _ => true
    })
    val notExcludedDir = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType("other-subdir", isDirectory = true), platform))
    notExcludedDir shouldBe true
    val notExcludedFile = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType("file.json", isDirectory = false), platform))
    notExcludedFile shouldBe true
    val result = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType("exclude-subdir", isDirectory = true), platform))
    result shouldBe false
  }

  it should "validate a name pattern" in {
    FileCompletionFilters.addFilter({
      case PredicateParams(_, _, FileWithType(name, _), _) =>
        "^[.].*".r.findFirstMatchIn(name).isEmpty
      case _ => true
    })
    val notExcludedDir = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType("normal", isDirectory = true), platform))
    notExcludedDir shouldBe true
    val notExcludedFile = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType("normal.json", isDirectory = false), platform))
    notExcludedFile shouldBe true
    val resultDir = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType(".hidden", isDirectory = true), platform))
    resultDir shouldBe false
    val resultFile = FileCompletionFilters.filter(PredicateParams("file:///full/Uri/main.raml", "/exclude-dir", FileWithType(".hidden", isDirectory = true), platform))
    resultFile shouldBe false
  }

}
