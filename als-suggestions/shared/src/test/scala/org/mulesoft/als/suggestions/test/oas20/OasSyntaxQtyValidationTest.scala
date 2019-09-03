package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.common.io.{Fs, SyncFile}
import org.scalatest.FunSuite

class OasSyntaxQtyValidationTest extends FunSuite {

  test("test that yaml has same files than json resources") {
    val jsonPath = "als-suggestions/shared/src/test/resources/test/oas20/by-directory/json"
    val yamlPath = "als-suggestions/shared/src/test/resources/test/oas20/by-directory/yaml"

    val jsonDir = Fs.syncFile(jsonPath)
    val yamlDir = Fs.syncFile(yamlPath)

    iterateDir(jsonDir, yamlDir)

    succeed
  }

  private def iterateFiles(jsonDir: String,
                           yamlDir: String,
                           jsonFiles: Array[String],
                           yamlFiles: Array[String]): Unit = {
    jsonFiles.zipWithIndex.foreach {
      case (jsonPath, index) =>
        val yamlPath = yamlFiles(index)
        if (jsonPath.split('.').head != yamlPath.split('.').head)
          fail(s"Different files found, json: $jsonPath and \n$yamlPath")

        val jsonFile = Fs.syncFile(jsonDir + "/" + jsonPath)
        val yamlFile = Fs.syncFile(yamlDir + "/" + yamlPath)
        if (!jsonFile.exists)
          fail(s"Json file/dir does not exists ${jsonFile.path}")

        if (!yamlFile.exists)
          fail(s"Yaml file/dir does not exists ${yamlFile.path}")
        if (jsonFile.isDirectory && yamlFile.isDirectory)
          iterateDir(jsonFile, yamlFile)
        else if (jsonFile.isDirectory != yamlFile.isDirectory)
          fail(s"One of the following is dir while other is file: 1-${jsonFile.path} \n2-${yamlFile.path}")

    }
  }

  private def iterateDir(jsonDir: SyncFile, yamlDir: SyncFile): Unit = {
    val yamlFiles = yamlDir.list
    val jsonFiles = jsonDir.list

    if (yamlFiles.size != jsonFiles.size)
      fail(
        s"Yaml dir: ${yamlDir.path} has ${yamlFiles.size} files while equivalent jsonDir: ${jsonDir.path} has ${jsonFiles.size} files")

    iterateFiles(jsonDir.path, yamlDir.path, jsonFiles.sorted, yamlFiles.sorted)

  }

}
