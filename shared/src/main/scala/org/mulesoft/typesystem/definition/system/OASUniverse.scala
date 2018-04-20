package org.mulesoft.typesystem.definition.system

object OASUniverse {
val value1 =
"""[
  {
    "classes": [
      {
        "name": "InfoObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "title",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The title of the application."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A short description of the application. GFM syntax can be used for rich text representation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "termsOfService",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The Terms of Service for the API."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "version",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Provides the version of the application API (not to be confused with the specification version)"
                ]
              },
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "contact",
            "type": {
              "typeName": "ContactObject",
              "nameSpace": "",
              "basicName": "ContactObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Contact information for the exposed API"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "license",
            "type": {
              "typeName": "LicenseObject",
              "nameSpace": "",
              "basicName": "LicenseObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The license information for the exposed API."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ContactObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The identifying name of the contact person/organization."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "url",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URL pointing to the contact information. MUST be in the format of a URL."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "email",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URL pointing to the contact information. MUST be in the format of a URL."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "LicenseObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The identifying name of license"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "url",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A URL to the license used for the API. MUST be in the format of a URL."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SecurityRequirementObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.value",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TagObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "externalDocs",
            "type": {
              "typeName": "core.ExternalDocumentationObject",
              "nameSpace": "core",
              "basicName": "ExternalDocumentationObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SwaggerObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "info",
            "type": {
              "typeName": "InfoObject",
              "nameSpace": "",
              "basicName": "InfoObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "host",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The host (name or ip) serving the API. This MUST be the host only and does not include the scheme nor sub-paths. It MAY include a port. If the host is not included, the host serving the documentation is to be used (including the port). The host does not support path templating."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "basePath",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The base path on which the API is served, which is relative to the host. If it is not included, the API is served directly under the host. The value MUST start with a leading slash (/). The basePath does not support path templating."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schemes",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "http",
                    "https",
                    "ws",
                    "wss"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The transfer protocol of the API. Values MUST be from the list: \"http\", \"https\", \"ws\", \"wss\". If the schemes is not included, the default scheme to be used is the one used to access the Swagger definition itself."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "consumes",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of MIME types the APIs can consume. This is global to all APIs but can be overridden on specific API calls. Value MUST be as described under Mime Types."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "produces",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of MIME types the APIs can consume. This is global to all APIs but can be overridden on specific API calls. Value MUST be as described under Mime Types."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "paths",
            "type": {
              "typeName": "paths.PathsObject",
              "nameSpace": "paths",
              "basicName": "PathsObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The available paths and operations for the API."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "definitions",
            "type": {
              "base": {
                "typeName": "defs.DefinitionObject",
                "nameSpace": "defs",
                "basicName": "DefinitionObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An object to hold data types produced and consumed by operations."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "parameters",
            "type": {
              "base": {
                "typeName": "defs.ParameterDefinitionObject",
                "nameSpace": "defs",
                "basicName": "ParameterDefinitionObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An object to hold parameters that can be used across operations. This property does not define global parameters for all operations."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "responses",
            "type": {
              "base": {
                "typeName": "defs.ResponseDefinitionObject",
                "nameSpace": "defs",
                "basicName": "ResponseDefinitionObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An object to hold responses that can be used across operations. This property does not define global responses for all operations."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "securityDefinitions",
            "type": {
              "base": {
                "typeName": "defs.SecurityDefinitionObject",
                "nameSpace": "defs",
                "basicName": "SecurityDefinitionObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Security scheme definitions that can be used across the specification."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "security",
            "type": {
              "base": {
                "typeName": "SecurityRequirementObject",
                "nameSpace": "",
                "basicName": "SecurityRequirementObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInArray",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A declaration of which security schemes are applied for the API as a whole. The list of values describes alternative security schemes that can be used (that is, there is a logical OR between the security requirements). Individual operations can override this definition."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "tags",
            "type": {
              "base": {
                "typeName": "TagObject",
                "nameSpace": "",
                "basicName": "TagObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of tags used by the specification with additional metadata. The order of the tags can be used to reflect on their order by the parsing tools. Not all tags that are used by the Operation Object must be declared. The tags that are not declared may be organized randomly or based on the tools' logic. Each tag name in the list MUST be unique."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "externalDocs",
            "type": {
              "typeName": "core.ExternalDocumentationObject",
              "nameSpace": "core",
              "basicName": "ExternalDocumentationObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Additional external documentation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts",
      "paths": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts",
      "defs": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts",
      "extensions": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts",
      "core": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\core.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts"
  },
  {
    "classes": [
      {
        "name": "SpecPartMetaData",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": true,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {},
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts"
  },
  {
    "classes": [
      {
        "name": "ValueType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "StringType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.nameAtRuntime",
            "arguments": [
              "string"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "string"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "AnyType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.nameAtRuntime",
            "arguments": [
              "any"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "any"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value2 =
"""{
        "name": "NumberType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.nameAtRuntime",
            "arguments": [
              "number"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "number"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BooleanType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.nameAtRuntime",
            "arguments": [
              "boolean"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "boolean"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Referencable",
        "methods": [],
        "typeParameters": [
          "T"
        ],
        "typeParameterConstraint": [
          null
        ],
        "implements": [],
        "fields": [],
        "isInterface": true,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Reference",
        "methods": [],
        "typeParameters": [
          "T"
        ],
        "typeParameterConstraint": [
          null
        ],
        "implements": [],
        "fields": [
          {
            "name": "structuredValue",
            "type": {
              "typeName": "TypeInstance",
              "nameSpace": "",
              "basicName": "TypeInstance",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns a structured object if the reference point to one."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns name of referenced object"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DeclaresDynamicType",
        "methods": [],
        "typeParameters": [
          "T"
        ],
        "typeParameterConstraint": [
          null
        ],
        "implements": [],
        "fields": [],
        "isInterface": true,
        "annotations": [],
        "extends": [
          {
            "typeName": "Referencable",
            "nameSpace": "",
            "basicName": "Referencable",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "T",
                "nameSpace": "",
                "basicName": "T",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "UriTemplate",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "This type currently serves both for absolute and relative urls"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "RelativeUriString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "This  type describes relative uri templates"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "UriTemplate",
            "nameSpace": "",
            "basicName": "UriTemplate",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "FullUriTemplateString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "This  type describes absolute uri templates"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "UriTemplate",
            "nameSpace": "",
            "basicName": "UriTemplate",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "FixedUri",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "This  type describes fixed uris"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "MarkdownString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.innerType",
            "arguments": [
              "markdown"
            ]
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Mardown string is a string which can contain markdown as an extension this markdown should support links with RAML Pointers since 1.0"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SchemaString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Schema at this moment only two subtypes are supported (json schema and xsd)"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "JSonSchemaString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.functionalDescriminator",
            "arguments": [
              "this.mediaType&&this.mediaType.isJSON()"
            ]
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "JSON schema"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "SchemaString",
            "nameSpace": "",
            "basicName": "SchemaString",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "XMLSchemaString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.innerType",
            "arguments": [
              "xml"
            ]
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "XSD schema"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "SchemaString",
            "nameSpace": "",
            "basicName": "SchemaString",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ExampleString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "StatusCodeString",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "StringType",
            "nameSpace": "",
            "basicName": "StringType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "JSONExample",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.functionalDescriminator",
            "arguments": [
              "this.mediaType.isJSON()"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ExampleString",
            "nameSpace": "",
            "basicName": "ExampleString",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "XMLExample",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.functionalDescriminator",
            "arguments": [
              "this.mediaType.isXML()"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ExampleString",
            "nameSpace": "",
            "basicName": "ExampleString",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TypeInstance",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "properties",
            "type": {
              "base": {
                "typeName": "TypeInstanceProperty",
                "nameSpace": "",
                "basicName": "TypeInstanceProperty",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Array of instance properties"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "isScalar",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Whether the type is scalar"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "typeName": "any",
              "nameSpace": "",
              "basicName": "any",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "For instances of scalar types returns scalar value"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.customHandling",
            "arguments": []
          }
        ],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TypeInstanceProperty",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Property name"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "typeName": "TypeInstance",
              "nameSpace": "",
              "basicName": "TypeInstance",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Property value"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "values",
            "type": {
              "base": {
                "typeName": "TypeInstance",
                "nameSpace": "",
                "basicName": "TypeInstance",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Array of values if property value is array"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "isArray",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Whether property has array as value"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.customHandling",
            "arguments": []
          }
        ],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts"
  },
  {
    "classes": [
      {
        "name": "PathsObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "paths",
            "type": {
              "base": {
                "typeName": "PathItemObject",
                "nameSpace": "",
                "basicName": "PathItemObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "PathItemObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "path",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.startFrom",
                "arguments": [
                  "/"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A relative path to an individual endpoint. The field name MUST begin with a slash. The path is appended to the basePath in order to construct the full URL. Path templating is allowed."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "operations",
            "type": {
              "base": {
                "typeName": "OperationObject",
                "nameSpace": "",
                "basicName": "OperationObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "parameters",
            "type": {
              "base": {
                "typeName": "ParameterObject",
                "nameSpace": "",
                "basicName": "ParameterObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of parameters that are applicable for all the operations described under this path. These parameters can be overridden at the operation level, but cannot be removed there. The list MUST NOT include duplicated parameters. A unique parameter is defined by a combination of a name and location. The list can use the Reference Object to link to parameters that are defined at the Swagger Object's parameters. There can be one 'body' parameter at most."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ItemsObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "type",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.descriminatingProperty",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "string",
                    "number",
                    "integer",
                    "boolean",
                    "array",
                    "file",
                    "object"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Required. The type of the parameter. Since the parameter is not located at the request body, it is limited to simple types (that is, not an object). The value MUST be one of \"string\", \"number\", \"integer\", \"boolean\", \"array\" or \"file\". If type is \"file\", the consumes MUST be either \"multipart/form-data\" or \" application/x-www-form-urlencoded\" and the parameter MUST be in \"formData\"."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "format",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "items",
            "type": {
              "typeName": "ItemsObject",
              "nameSpace": "",
              "basicName": "ItemsObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "collectionFormat",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "csv",
                    "ssv",
                    "tsv",
                    "pipes",
                    "multi"
                  ]
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "default",
            "type": {
              "typeName": "any",
              "nameSpace": "",
              "basicName": "any",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "maximum",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "exclusiveMaximum",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "minimum",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "exclusiveMinimum",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "maxLength",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "minLength",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "pattern",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "maxItems",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "minItems",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "uniqueItems",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "enum",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "multipleOf",
            "type": {
              "typeName": "number",
              "nameSpace": "",
              "basicName": "number",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value3 =
"""{
        "name": "ParameterObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "$ref",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A brief description. This could contain examples of use. GFM syntax can be used for rich text representation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Required. The name of the parameter. Parameter names are case sensitive.\nIf in is \"path\", the name field MUST correspond to the associated path segment from the path field in the Paths Object. See Path Templating for further information.\nFor all other cases, the name corresponds to the parameter name used based on the in property."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "in",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "required",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Determines whether this parameter is mandatory. If the parameter is in 'path', this property is required and its value MUST be true. Otherwise, the property MAY be included and its default value is false."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BodyParameterObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "schema",
            "type": {
              "typeName": "SchemaObject",
              "nameSpace": "",
              "basicName": "SchemaObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "in",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "body"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The location of the parameter. Possible values are \"query\", \"header\", \"path\", \"formData\" or \"body\"."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.descriminatingProperty",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "string",
                    "number",
                    "integer",
                    "boolean",
                    "array",
                    "file",
                    "object"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Required. The type of the parameter. Since the parameter is not located at the request body, it is limited to simple types (that is, not an object). The value MUST be one of \"string\", \"number\", \"integer\", \"boolean\", \"array\" or \"file\". If type is \"file\", the consumes MUST be either \"multipart/form-data\" or \" application/x-www-form-urlencoded\" and the parameter MUST be in \"formData\"."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ParameterObject",
            "nameSpace": "",
            "basicName": "ParameterObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "CommonParameterObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "allowEmptyValue",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "example",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.example",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.superclasses",
            "arguments": [
              [
                "ItemsObject"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ParameterObject",
            "nameSpace": "",
            "basicName": "ParameterObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "in": [
            {
              "name": "MetaModel.required",
              "arguments": []
            },
            {
              "name": "MetaModel.oneOf",
              "arguments": [
                [
                  "query",
                  "header",
                  "path",
                  "formData"
                ]
              ]
            },
            {
              "name": "MetaModel.description",
              "arguments": [
                "The location of the parameter. Possible values are \"query\", \"header\", \"path\", \"formData\" or \"body\"."
              ]
            }
          ]
        }
      },
      {
        "name": "pointer",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ValueType",
            "nameSpace": "",
            "basicName": "ValueType",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SchemaObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "$ref",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "title",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "required",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "properties",
            "type": {
              "base": {
                "typeName": "defs.DefinitionObject",
                "nameSpace": "defs",
                "basicName": "DefinitionObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "allOf",
            "type": {
              "base": {
                "typeName": "SchemaObject",
                "nameSpace": "",
                "basicName": "SchemaObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "additionalProperties",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "additionalPropertiesSchema",
            "type": {
              "typeName": "SchemaObject",
              "nameSpace": "",
              "basicName": "SchemaObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.hideFromUI",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "items",
            "type": {
              "typeName": "SchemaObject",
              "nameSpace": "",
              "basicName": "SchemaObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "discriminator",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "readOnly",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "xml",
            "type": {
              "typeName": "XMLObject",
              "nameSpace": "",
              "basicName": "XMLObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "externalDocs",
            "type": {
              "base": {
                "typeName": "core.ExternalDocumentationObject",
                "nameSpace": "core",
                "basicName": "ExternalDocumentationObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": null,
            "annotations": [],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "object"
            },
            "optional": false
          },
          {
            "name": "example",
            "type": {
              "typeName": "any",
              "nameSpace": "",
              "basicName": "any",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.example",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.definingPropertyIsEnough",
            "arguments": [
              "properties"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ItemsObject",
            "nameSpace": "",
            "basicName": "ItemsObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ArrayObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "items",
            "type": {
              "typeName": "ItemsObject",
              "nameSpace": "",
              "basicName": "ItemsObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": null,
            "annotations": [],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "array"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ItemsObject",
            "nameSpace": "",
            "basicName": "ItemsObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "HeaderObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ItemsObject",
            "nameSpace": "",
            "basicName": "ItemsObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Example",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "mimeType",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "typeName": "any",
              "nameSpace": "",
              "basicName": "any",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ResponsesObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "default",
            "type": {
              "typeName": "ResponseObject",
              "nameSpace": "",
              "basicName": "ResponseObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "responses",
            "type": {
              "base": {
                "typeName": "Response",
                "nameSpace": "",
                "basicName": "Response",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ResponseObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "$ref",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schema",
            "type": {
              "typeName": "SchemaObject",
              "nameSpace": "",
              "basicName": "SchemaObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "headers",
            "type": {
              "base": {
                "typeName": "HeaderObject",
                "nameSpace": "",
                "basicName": "HeaderObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "example",
            "type": {
              "base": {
                "typeName": "Example",
                "nameSpace": "",
                "basicName": "Example",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Response",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "code",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ResponseObject",
            "nameSpace": "",
            "basicName": "ResponseObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "OperationObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "method",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "get",
                    "put",
                    "delete",
                    "post",
                    "options",
                    "head",
                    "patch"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "key of the operation"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "parameters",
            "type": {
              "base": {
                "typeName": "ParameterObject",
                "nameSpace": "",
                "basicName": "ParameterObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of parameters that are applicable for all the operations described under this path. These parameters can be overridden at the operation level, but cannot be removed there. The list MUST NOT include duplicated parameters. A unique parameter is defined by a combination of a name and location. The list can use the Reference Object to link to parameters that are defined at the Swagger Object's parameters. There can be one 'body' parameter at most."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "tags",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "summary",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A verbose explanation of the operation behavior. GFM syntax can be used for rich text representation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "externalDocs",
            "type": {
              "typeName": "core.ExternalDocumentationObject",
              "nameSpace": "core",
              "basicName": "ExternalDocumentationObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "operationId",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Unique string used to identify the operation. The id MUST be unique among all operations described in the API. Tools and libraries MAY use the operationId to uniquely identify an operation, therefore, it is recommended to follow common programming naming conventions."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
"""
val value4 =
"""{
            "name": "consumes",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of MIME types the operation can consume. This overrides the consumes definition at the Swagger Object. An empty value MAY be used to clear the global definition. Value MUST be as described under Mime Types."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "produces",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of MIME types the operation can produce. This overrides the produces definition at the Swagger Object. An empty value MAY be used to clear the global definition. Value MUST be as described under Mime Types."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "responses",
            "type": {
              "typeName": "ResponsesObject",
              "nameSpace": "",
              "basicName": "ResponsesObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The list of possible responses as they are returned from executing this operation."
                ]
              },
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schemes",
            "type": {
              "base": {
                "typeName": "string",
                "nameSpace": "",
                "basicName": "string",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "http",
                    "https",
                    "ws",
                    "wss"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The transfer protocol for the operation. Values MUST be from the list: \"http\", \"https\", \"ws\", \"wss\". The value overrides the Swagger Object schemes definition."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "deprecated",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declares this operation to be deprecated. Usage of the declared operation should be refrained. Default value is false."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "security",
            "type": {
              "base": {
                "typeName": "swagger.SecurityRequirementObject",
                "nameSpace": "swagger",
                "basicName": "SecurityRequirementObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInArray",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "XMLObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "attribute",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "If attribute is set to true, a type instance should be serialized as an XML attribute. It can only be true for scalar types."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "wrapped",
            "type": {
              "typeName": "boolean",
              "nameSpace": "",
              "basicName": "boolean",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "If wrapped is set to true, a type instance should be wrapped in its own XML element. It can not be true for scalar types and it can not be true at the same moment when attribute is true."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Allows to override the name of the XML element or XML attribute in it's XML representation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "namespace",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Allows to configure the name of the XML namespace."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "prefix",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Allows to configure the prefix which will be used during serialization to XML."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts",
      "core": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\core.ts",
      "swagger": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\swagger.ts",
      "extensions": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts",
      "defs": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts"
  },
  {
    "classes": [
      {
        "name": "ExternalDocumentationObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "description",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A short description of the target documentation. GFM syntax can be used for rich text representation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "url",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URL for the target documentation. Value MUST be in the format of a URL."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\core.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts",
      "extensions": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\core.ts"
  },
  {
    "classes": [
      {
        "name": "WithSpecificationExtensions",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "specificationExtensions",
            "type": {
              "base": {
                "typeName": "SpecificationExtension",
                "nameSpace": "",
                "basicName": "SpecificationExtension",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SpecificationExtension",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.value",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts"
  },
  {
    "classes": [
      {
        "name": "DefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "SchemaObject",
            "nameSpace": "",
            "basicName": "SchemaObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ParameterDefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "key",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ParameterObject",
            "nameSpace": "",
            "basicName": "ParameterObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "CommonParameterDefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.superclasses",
            "arguments": [
              [
                "ParameterDefinitionObject"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "CommonParameterObject",
            "nameSpace": "",
            "basicName": "CommonParameterObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BodyParameterDefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.superclasses",
            "arguments": [
              [
                "ParameterDefinitionObject"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "BodyParameterObject",
            "nameSpace": "",
            "basicName": "BodyParameterObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ResponseDefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "key",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "ResponseObject",
            "nameSpace": "",
            "basicName": "ResponseObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SecurityDefinitionObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "basic",
                    "apiKey",
                    "oauth2"
                  ]
                ]
              },
              {
                "name": "MetaModel.descriminatingProperty",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ApiKey",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "in",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "query",
                    "header"
                  ]
                ]
              },
              {
                "name": "MetaModel.required",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": null,
            "annotations": [],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "apiKey"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "SecurityDefinitionObject",
            "nameSpace": "",
            "basicName": "SecurityDefinitionObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "OAuth2",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "flow",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "implicit",
                    "password",
                    "application",
                    "accessCode"
                  ]
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "authorizationUrl",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "tokenUrl",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "scopes",
            "type": {
              "typeName": "ScopesObject",
              "nameSpace": "",
              "basicName": "ScopesObject",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": null,
            "annotations": [],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "oauth2"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "SecurityDefinitionObject",
            "nameSpace": "",
            "basicName": "SecurityDefinitionObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Basic",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "type",
            "type": null,
            "annotations": [],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "basic"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "SecurityDefinitionObject",
            "nameSpace": "",
            "basicName": "SecurityDefinitionObject",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ScopesObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "scopes",
            "type": {
              "base": {
                "typeName": "ScopeObject",
                "nameSpace": "",
                "basicName": "ScopeObject",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "WithSpecificationExtensions",
            "nameSpace": "",
            "basicName": "WithSpecificationExtensions",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ScopeObject",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "name",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "description",
            "type": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {
          "value": [
            {
              "name": "MetaModel.value",
              "arguments": []
            }
          ]
        }
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\systemTypes.ts",
      "paths": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\paths.ts",
      "extensions": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\extensions.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\oas-definition\\oas-2.0\\definitions.ts"
  }
]"""

val value = value1 + value2 + value3 + value4
}
