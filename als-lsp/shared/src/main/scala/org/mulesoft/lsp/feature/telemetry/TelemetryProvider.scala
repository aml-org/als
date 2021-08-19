package org.mulesoft.lsp.feature.telemetry

import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MessageTypes extends Enumeration {
  type MessageTypes = String
  val BEGIN_PARSE                               = "BEGIN_PARSE"
  val END_PARSE                                 = "END_PARSE"
  val BEGIN_PARSE_PATCHED                       = "BEGIN_PARSE_PATCHED"
  val END_PARSE_PATCHED                         = "END_PARSE_PATCHED"
  val BEGIN_REPORT                              = "BEGIN_REPORT"
  val END_REPORT                                = "END_REPORT"
  val BEGIN_COMPLETION                          = "BEGIN_COMPLETION"
  val END_COMPLETION                            = "END_COMPLETION"
  val BEGIN_STRUCTURE                           = "BEGIN_STRUCTURE"
  val END_STRUCTURE                             = "END_STRUCTURE"
  val BEGIN_DIAGNOSTIC_PARSE                    = "BEGIN_DIAGNOSTIC_PARSE"
  val END_DIAGNOSTIC_PARSE                      = "END_DIAGNOSTIC_PARSE"
  val BEGIN_DIAGNOSTIC_RESOLVED                 = "BEGIN_DIAGNOSTIC_RESOLVED"
  val END_DIAGNOSTIC_RESOLVED                   = "END_DIAGNOSTIC_RESOLVED"
  val INDEX_DIALECT                             = "INDEX_DIALECT"
  val BEGIN_RESOLUTION                          = "BEGIN_RESOLUTION"
  val END_RESOLUTION                            = "END_RESOLUTION"
  val BEGIN_DOCUMENT_LINK                       = "BEGIN_DOCUMENT_LINK"
  val END_DOCUMENT_LINK                         = "END_DOCUMENT_LINK"
  val BEGIN_DOCUMENT_HIGHLIGHT                  = "BEGIN_DOCUMENT_HIGHLIGHT"
  val END_DOCUMENT_HIGHLIGHT                    = "END_DOCUMENT_HIGHLIGHT"
  val BEGIN_GOTO_DEF                            = "BEGIN_GOTO_DEF"
  val END_GOTO_DEF                              = "END_GOTO_DEF"
  val BEGIN_GOTO_IMPL                           = "BEGIN_GOTO_IMPL"
  val END_GOTO_IMPL                             = "END_GOTO_IMPL"
  val BEGIN_GOTO_T_DEF                          = "BEGIN_GOTO_T_DEF"
  val END_GOTO_T_DEF                            = "END_GOTO_T_DEF"
  val BEGIN_FIND_REF                            = "BEGIN_FIND_REF"
  val END_FIND_REF                              = "END_FIND_REF"
  val BEGIN_PREP_RENAME                         = "BEGIN_PREP_RENAME"
  val END_PREP_RENAME                           = "END_PREP_RENAME"
  val BEGIN_RENAME                              = "BEGIN_RENAME"
  val END_RENAME                                = "END_RENAME"
  val BEGIN_SERIALIZATION                       = "BEGIN_SERIALIZATION"
  val END_SERIALIZATION                         = "END_SERIALIZATION"
  val BEGIN_CLEAN_VALIDATION                    = "BEGIN_CLEAN_VALIDATION"
  val END_CLEAN_VALIDATION                      = "END_CLEAN_VALIDATION"
  val BEGIN_CONVERSION                          = "BEGIN_CONVERSION"
  val END_CONVERSION                            = "END_CONVERSION"
  val BEGIN_QUICK_FIX                           = "BEGIN_QUICK_FIX"
  val END_QUICK_FIX                             = "END_QUICK_FIX"
  val BEGIN_FILE_USAGE                          = "BEGIN_FILE_USAGE"
  val END_FILE_USAGE                            = "END_FILE_USAGE"
  val BEGIN_HOVER                               = "BEGIN_HOVER"
  val END_HOVER                                 = "END_HOVER"
  val BEGIN_FOLDING                             = "BEGIN_FOLDING"
  val END_FOLDING                               = "END_FOLDING"
  val BEGIN_SELECTION_RANGE                     = "BEGIN_SELECTION_RANGE"
  val END_SELECTION_RANGE                       = "END_SELECTION_RANGE"
  val BEGIN_RENAME_FILE_ACTION                  = "BEGIN_RENAME_FILE_ACTION"
  val END_RENAME_FILE_ACTION                    = "END_RENAME_FILE_ACTION"
  val BEGIN_CODE_ACTION                         = "BEGIN_CODE_ACTION"
  val END_CODE_ACTION                           = "END_CODE_ACTION"
  val BEGIN_TEST_ACTION                         = "BEGIN_TEST_ACTION"
  val END_TEST_ACTION                           = "END_TEST_ACTION"
  val BEGIN_EXTRACT_ELEMENT_ACTION              = "BEGIN_EXTRACT_ELEMENT_ACTION"
  val END_EXTRACT_ELEMENT_ACTION                = "END_EXTRACT_ELEMENT_ACTION"
  val BEGIN_EXTRACT_TO_FRAGMENT_ACTION          = "BEGIN_EXTRACT_TO_FRAGMENT_ACTION"
  val END_EXTRACT_TO_FRAGMENT_ACTION            = "END_EXTRACT_TO_FRAGMENT_ACTION"
  val BEGIN_EXTRACT_TO_LIBRARY_ACTION           = "BEGIN_EXTRACT_TO_LIBRARY_ACTION"
  val END_EXTRACT_TO_LIBRARY_ACTION             = "END_EXTRACT_TO_LIBRARY_ACTION"
  val BEGIN_DELETE_NODE_ACTION                  = "BEGIN_DELETE_NODE_ACTION"
  val END_DELETE_NODE_ACTION                    = "END_DELETE_NODE_ACTION"
  val BEGIN_DOCUMENT_FORMATTING                 = "BEGIN_DOCUMENT_FORMATTING"
  val END_DOCUMENT_FORMATTING                   = "END_DOCUMENT_FORMATTING"
  val BEGIN_DOCUMENT_RANGE_FORMATTING           = "BEGIN_DOCUMENT_RANGE_FORMATTING"
  val END_DOCUMENT_RANGE_FORMATTING             = "END_DOCUMENT_RANGE_FORMATTING"
  val BEGIN_TYPE_TO_JSON_SCHEMA_ACTION          = "BEGIN_TYPE_TO_JSON_SCHEMA_ACTION"
  val END_TYPE_TO_JSON_SCHEMA_ACTION            = "END_TYPE_TO_JSON_SCHEMA_ACTION"
  val BEGIN_JSON_SCHEMA_TO_TYPE_ACTION          = "BEGIN_JSON_SCHEMA_TO_TYPE_ACTION"
  val END_JSON_SCHEMA_TO_TYPE_ACTION            = "END_JSON_SCHEMA_TO_TYPE_ACTION"
  val BEGIN_EXTERNAL_JSON_SCHEMA_TO_TYPE_ACTION = "BEGIN_EXTERNAL_JSON_SCHEMA_TO_TYPE_ACTION"
  val END_EXTERNAL_JSON_SCHEMA_TO_TYPE_ACTION   = "END_EXTERNAL_JSON_SCHEMA_TO_TYPE_ACTION"
  val BEGIN_SYNTHESIZE_VOCABULARY               = "BEGIN_SYNTHESIZE_VOCABULARY"
  val END_SYNTHESIZE_VOCABULARY                 = "END_SYNTHESIZE_VOCABULARY"
  val BEGIN_EXTERNAL_VOCABULARY_TO_LOCAL        = "BEGIN_EXTERNAL_VOCABULARY_TO_LOCAL"
  val END_EXTERNAL_VOCABULARY_TO_LOCAL          = "END_EXTERNAL_VOCABULARY_TO_LOCAL"
  val ERROR_MESSAGE                             = "ERROR_MESSAGE"
  val BEGIN_CUSTOM_DIAGNOSTIC                   = "BEGIN_CUSTOM_DIAGNOSTIC"
  val END_CUSTOM_DIAGNOSTIC                     = "END_CUSTOM_DIAGNOSTIC"
  val BEGIN_GET_WORKSPACE_CONFIGURATION         = "BEGIN_GET_WORKSPACE_CONFIGURATION"
  val END_GET_WORKSPACE_CONFIGURATION           = "END_GET_WORKSPACE_CONFIGURATION"
}

trait TelemetryProvider {

  protected def addTimedMessage(code: String, messageType: MessageTypes, msg: String, uri: String, uuid: String): Unit
  def addErrorMessage(code: String, msg: String, uri: String, uuid: String): Unit

  final def timeProcess[T](code: String,
                           beginType: MessageTypes,
                           endType: MessageTypes,
                           msg: String,
                           uri: String,
                           fn: () => Future[T],
                           uuid: String = UUID.randomUUID().toString): Future[T] = {
    val time = System.currentTimeMillis()
    addTimedMessage(code, beginType, msg, uri, uuid)
    fn()
      .andThen {
        case _ =>
          addTimedMessage(code, endType, s"$msg \n\ttook ${System.currentTimeMillis() - time} millis", uri, uuid)
      }
  }
}
