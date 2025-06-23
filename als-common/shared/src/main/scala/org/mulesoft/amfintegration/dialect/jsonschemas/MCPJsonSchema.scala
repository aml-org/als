package org.mulesoft.amfintegration.dialect.jsonschemas

object MCPJsonSchema extends InMemoryJsonSchema {
  override val name: String = "mcp-schema"

  override val fileContent: String = // todo: use MCPSchema.schema (when updating AMF Snapshot)
    """{
      |  "$schema": "http://json-schema.org/draft/2019-09/schema#",
      |  "type": "object",
      |  "properties": {
      |    "apiVersion": {
      |      "type": "string"
      |    },
      |    "kind": {
      |      "type": "string",
      |      "enum": ["ApiInstance"]
      |    },
      |    "metadata": {
      |      "type": "object",
      |      "properties": {
      |        "name": {
      |          "type": "string"
      |        },
      |        "labels": {
      |          "type": "object",
      |          "properties": {
      |            "flex.mulesoft.com/api-group-id": {
      |              "type": "string"
      |            },
      |            "flex.mulesoft.com/api-asset-id": {
      |              "type": "string"
      |            },
      |            "flex.mulesoft.com/api-version": {
      |              "type": "string"
      |            }
      |          }
      |        }
      |      }
      |    },
      |    "spec": {
      |      "type": "object",
      |      "properties": {
      |        "address": {
      |          "type": "string"
      |        },
      |        "deprecated": {
      |          "type": "boolean"
      |        },
      |        "deployment": {
      |          "type": "object",
      |          "properties": {
      |            "target": {
      |              "type": "string"
      |            },
      |            "runtimeVersion": {
      |              "type": "string"
      |            },
      |            "technology": {
      |              "type": "string",
      |              "enum": ["FlexGateway"]
      |            }
      |          }
      |        },
      |        "policies": {
      |          "type": "array",
      |          "items": {
      |            "type": "object",
      |            "properties": {
      |              "policyRef": {
      |                "type": "object",
      |                "properties": {
      |                  "name": {
      |                    "type": "string"
      |                  }
      |                }
      |              }
      |            }
      |          }
      |        }
      |      }
      |    }
      |  }
      |}""".stripMargin
}