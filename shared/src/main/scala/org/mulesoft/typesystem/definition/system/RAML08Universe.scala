package org.mulesoft.typesystem.definition.system

object RAML08Universe {
val value1 =
"""[
  {
    "classes": [
      {
        "name": "GlobalSchema",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [
          {
            "typeName": "Referencable",
            "nameSpace": "",
            "basicName": "Referencable",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "Sys.SchemaString",
                "nameSpace": "Sys",
                "basicName": "SchemaString",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
          }
        ],
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Name of the global schema, used to refer on schema content"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "value",
            "type": {
              "typeName": "Sys.SchemaString",
              "nameSpace": "Sys",
              "basicName": "SchemaString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Content of the schema"
                ]
              },
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
        "annotations": [
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "value"
            ]
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Content of the schema"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "RAMLSimpleElement",
            "nameSpace": "",
            "basicName": "RAMLSimpleElement",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
          }
        ],
        "moduleName": "RAMLSpec",
        "annotationOverridings": {}
      },
      {
        "name": "Api",
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
                  "The title property is a short plain text description of the RESTful API. The value SHOULD be suitable for use as a title for the contained user documentation."
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
                  "If the RAML API definition is targeted to a specific API version, the API definition MUST contain a version property. The version property is OPTIONAL and should not be used if: The API itself is not versioned. The API definition does not change between versions. The API architect can decide whether a change to user documentation elements, but no change to the API's resources, constitutes a version change. The API architect MAY use any versioning scheme so long as version numbers retain the same format. For example, 'v3', 'v3.0', and 'V3' are all allowed, but are not considered to be equal."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "baseUri",
            "type": {
              "typeName": "Sys.FullUriTemplateString",
              "nameSpace": "Sys",
              "basicName": "FullUriTemplateString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional during development; Required after implementation) A RESTful API's resources are defined relative to the API's base URI. The use of the baseUri field is OPTIONAL to allow describing APIs that have not yet been implemented. After the API is implemented (even a mock implementation) and can be accessed at a service endpoint, the API definition MUST contain a baseUri property. The baseUri property's value MUST conform to the URI specification RFC2396 or a Level 1 Template URI as defined in RFC6570. The baseUri property SHOULD only be used as a reference value."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "baseUriParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.BURI"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Base uri parameters are named parameters which described template parameters in the base uri"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "uriParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.BURI"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "URI parameters can be further defined by using the uriParameters property. The use of uriParameters is OPTIONAL. The uriParameters property MUST be a map in which each key MUST be the name of the URI parameter as defined in the baseUri property. The uriParameters CANNOT contain a key named version because it is a reserved URI parameter name. The value of the uriParameters property is itself a map that specifies  the property's attributes as named parameters"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "protocols",
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
                    "HTTP",
                    "HTTPS"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A RESTful API can be reached HTTP, HTTPS, or both. The protocols property MAY be used to specify the protocols that an API supports. If the protocols property is not specified, the protocol specified at the baseUri property is used. The protocols property MUST be an array of strings, of values `HTTP` and/or `HTTPS`."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "mediaType",
            "type": {
              "typeName": "Bodies.MimeType",
              "nameSpace": "Bodies",
              "basicName": "MimeType",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.oftenKeys",
                "arguments": [
                  [
                    "application/json",
                    "application/xml",
                    "application/x-www-form-urlencoded",
                    "multipart/formdata"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional) The media types returned by API responses, and expected from API requests that accept a body, MAY be defaulted by specifying the mediaType property. This property is specified at the root level of the API definition. The property's value MAY be a single string with a valid media type described in the specification."
                ]
              },
              {
                "name": "MetaModel.inherited",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schemas",
            "type": {
              "base": {
                "typeName": "GlobalSchema",
                "nameSpace": "",
                "basicName": "GlobalSchema",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
                  "To better achieve consistency and simplicity, the API definition SHOULD include an OPTIONAL schemas property in the root section. The schemas property specifies collections of schemas that could be used anywhere in the API definition. The value of the schemas property is an array of maps; in each map, the keys are the schema name, and the values are schema definitions. The schema definitions MAY be included inline or by using the RAML !include user-defined data type."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "traits",
            "type": {
              "base": {
                "typeName": "Methods.Trait",
                "nameSpace": "Methods",
                "basicName": "Trait",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
                  "Declarations of traits used in this API"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "securedBy",
            "type": {
              "base": {
                "typeName": "Security.SecuritySchemeRef",
                "nameSpace": "Security",
                "basicName": "SecuritySchemeRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInArray",
                "arguments": []
              },
              {
                "name": "MetaModel.allowNull",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of the security schemes to apply to all methods, these must be defined in the securitySchemes declaration."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "securitySchemes",
            "type": {
              "base": {
                "typeName": "Security.AbstractSecurityScheme",
                "nameSpace": "Security",
                "basicName": "AbstractSecurityScheme",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
                  "Security schemes that can be applied using securedBy"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "resourceTypes",
            "type": {
              "base": {
                "typeName": "Resources.ResourceType",
                "nameSpace": "Resources",
                "basicName": "ResourceType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
                  "Declaration of resource types used in this API"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "resources",
            "type": {
              "base": {
                "typeName": "Resources.Resource",
                "nameSpace": "Resources",
                "basicName": "Resource",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Resource"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Resources are identified by their relative URI, which MUST begin with a slash (/). A resource defined as a root-level property is called a top-level resource. Its property's key is the resource's URI relative to the baseUri. A resource defined as a child property of another resource is called a nested resource, and its property's key is its URI relative to its parent resource's URI. Every property whose key begins with a slash (/), and is either at the root of the API definition or is the child property of a resource property, is a resource property. The key of a resource, i.e. its relative URI, MAY consist of multiple URI path fragments separated by slashes; e.g. `/bom/items` may indicate the collection of items in a bill of materials as a single resource. However, if the individual URI path fragments are themselves resources, the API definition SHOULD use nested resources to describe this structure; e.g. if `/bom` is itself a resource then `/items` should be a nested resource of `/bom`, while `/bom/items` should not be used."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "documentation",
            "type": {
              "base": {
                "typeName": "DocumentationItem",
                "nameSpace": "",
                "basicName": "DocumentationItem",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
                  "The API definition can include a variety of documents that serve as a user guides and reference documentation for the API. Such documents can clarify how the API works or provide business context. Documentation-generators MUST include all the sections in an API definition's documentation property in the documentation output, and they MUST preserve the order in which the documentation is declared. To add user documentation to the API, include the documentation property at the root of the API definition. The documentation property MUST be an array of documents. Each document MUST contain title and content attributes, both of which are REQUIRED. If the documentation property is specified, it MUST include at least one document. Documentation-generators MUST process the content field as if it was defined using Markdown."
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
        "moduleName": "RAMLSpec",
        "annotationOverridings": {}
      },
      {
        "name": "DocumentationItem",
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
                "name": "MetaModel.description",
                "arguments": [
                  "title of documentation section"
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
            "name": "content",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Content of documentation section"
                ]
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
            "typeName": "RAMLSimpleElement",
            "nameSpace": "",
            "basicName": "RAMLSimpleElement",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
          }
        ],
        "moduleName": "RAMLSpec",
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Params": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts",
      "Bodies": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts",
      "Resources": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts",
      "Methods": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts",
      "Security": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\api.ts"
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
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "IntegerType",
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
              "integer"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "integer"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
        "extends": [],
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
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value2 =
"""{
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
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
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
  },
  {
    "classes": [
      {
        "name": "RAMLSimpleElement",
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
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts"
  },
  {
    "classes": [
      {
        "name": "Parameter",
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "name of the parameter"
                ]
              },
              {
                "name": "MetaModel.extraMetaKey",
                "arguments": [
                  "headers"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "displayName",
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
                  "An alternate, human-friendly name for the parameter"
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
                "name": "MetaModel.defaultValue",
                "arguments": [
                  "string"
                ]
              },
              {
                "name": "MetaModel.descriminatingProperty",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The type attribute specifies the primitive type of the parameter's resolved value. API clients MUST return/throw an error if the parameter's resolved value does not match the specified type. If type is not specified, it defaults to string."
                ]
              },
              {
                "name": "MetaModel.canBeDuplicator",
                "arguments": []
              }
            ],
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
                  "Set to true if parameter is required"
                ]
              },
              {
                "name": "MetaModel.defaultBooleanValue",
                "arguments": [
                  true
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The default attribute specifies the default value to use for the property if the property is omitted or its value is not specified. This SHOULD NOT be interpreted as a requirement for the client to send the default attribute's value if there is no other value to send. Instead, the default attribute's value is the value the server uses if the client does not send a value."
                ]
              }
            ],
            "valueConstraint": null,
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
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional) The example attribute shows an example value for the property. This can be used, e.g., by documentation generators to generate sample values for the property."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "repeat",
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
                  "The repeat attribute specifies that the parameter can be repeated. If the parameter can be used multiple times, the repeat parameter value MUST be set to 'true'. Otherwise, the default value is 'false' and the parameter may not be repeated."
                ]
              },
              {
                "name": "MetaModel.defaultBooleanValue",
                "arguments": [
                  false
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description attribute describes the intended use or meaning of the $self. This value MAY be formatted using Markdown."
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
        "name": "StringTypeDeclaration",
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
              "value": "string"
            },
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The pattern attribute is a regular expression that a parameter of type string MUST match. Regular expressions MUST follow the regular expression specification from ECMA 262/Perl 5. The pattern MAY be enclosed in double quotes for readability and clarity."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The enum attribute provides an enumeration of the parameter's valid values. This MUST be an array. If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. If there is no matching value, the clients and servers MUST treat this as an error."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The minLength attribute specifies the parameter value's minimum number of characters."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The maxLength attribute specifies the parameter value's maximum number of characters."
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
            "name": "MetaModel.description",
            "arguments": [
              "Value must be a string"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Parameter",
            "nameSpace": "",
            "basicName": "Parameter",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BooleanTypeDeclaration",
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
              "value": "boolean"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value must be a boolean"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Parameter",
            "nameSpace": "",
            "basicName": "Parameter",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "NumberTypeDeclaration",
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
              "value": "number"
            },
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type number or integer) The minimum attribute specifies the parameter's minimum value."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type number or integer) The maximum attribute specifies the parameter's maximum value."
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
            "name": "MetaModel.description",
            "arguments": [
              "Value MUST be a number. Indicate floating point numbers as defined by YAML."
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Parameter",
            "nameSpace": "",
            "basicName": "Parameter",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "IntegerTypeDeclaration",
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
              "value": "integer"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value MUST be a integer."
            ]
          }
        ],
        "extends": [
          {
            "typeName": "NumberTypeDeclaration",
            "nameSpace": "",
            "basicName": "NumberTypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateTypeDeclaration",
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
              "value": "date"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value MUST be a string representation of a date as defined in RFC2616 Section 3.3. "
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Parameter",
            "nameSpace": "",
            "basicName": "Parameter",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "FileTypeDeclaration",
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
              "value": "file"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.requireValue",
            "arguments": [
              "location",
              "ParameterLocation.FORM"
            ]
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "(Applicable only to Form properties) Value is a file. Client generators SHOULD use this type to handle file uploads correctly."
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Parameter",
            "nameSpace": "",
            "basicName": "Parameter",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [
      {
        "name": "ParameterLocation",
        "members": [
          "QUERY",
          "HEADERS",
          "URI",
          "FORM",
          "BURI"
        ]
      }
    ],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts"
  },
"""
val value3 =
"""
  {
    "classes": [
      {
        "name": "MimeType",
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
              "This sub type of the string represents mime types"
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
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BodyLike",
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Mime type of the request or response body"
                ]
              },
              {
                "name": "MetaModel.canInherit",
                "arguments": [
                  "mediaType"
                ]
              },
              {
                "name": "MetaModel.oftenKeys",
                "arguments": [
                  [
                    "application/json",
                    "application/xml",
                    "application/x-www-form-urlencoded",
                    "multipart/form-data"
                  ]
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schema",
            "type": {
              "typeName": "Sys.SchemaString",
              "nameSpace": "Sys",
              "basicName": "SchemaString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.requireValue",
                "arguments": [
                  "this.name.isForm()",
                  "false"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The structure of a request or response body MAY be further specified by the schema property under the appropriate media type. The schema key CANNOT be specified if a body's media type is application/x-www-form-urlencoded or multipart/form-data. All parsers of RAML MUST be able to interpret JSON Schema and XML Schema. Schema MAY be declared inline or in an external file. However, if the schema is sufficiently large so as to make it difficult for a person to read the API definition, or the schema is reused across multiple APIs or across multiple miles in the same API, the !include user-defined data type SHOULD be used instead of including the content inline. Alternatively, the value of the schema field MAY be the name of a schema specified in the root-level schemas property, or it MAY be declared in an external file and included by using the by using the RAML !include user-defined data type."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "example",
            "type": {
              "typeName": "Sys.ExampleString",
              "nameSpace": "Sys",
              "basicName": "ExampleString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Documentation generators MUST use body properties' example attributes to generate example invocations."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "formParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.FORM"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Web forms REQUIRE special encoding and custom declaration. If the API's media type is either application/x-www-form-urlencoded or multipart/form-data, the formParameters property MUST specify the name-value pairs that the API is expecting. The formParameters property is a map in which the key is the name of the web form parameter, and the value is itself a map the specifies the web form parameter's attributes."
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Human readable description of the body"
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
            "name": "MetaModel.canInherit",
            "arguments": [
              "mediaType"
            ]
          }
        ],
        "extends": [],
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
              "typeName": "Sys.StatusCodeString",
              "nameSpace": "Sys",
              "basicName": "StatusCodeString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.extraMetaKey",
                "arguments": [
                  "statusCodes"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Responses MUST be a map of one or more HTTP status codes, where each status code itself is a map that describes that status code."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "headers",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.HEADERS"
                ]
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Header"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An API's methods may support custom header values in responses. The custom, non-standard HTTP headers MUST be specified by the headers property. API's may include the the placeholder token {?} in a header name to indicate that any number of headers that conform to the specified format can be sent in responses. This is particularly useful for APIs that allow HTTP headers that conform to some naming convention to send arbitrary, custom data."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "body",
            "type": {
              "base": {
                "typeName": "BodyLike",
                "nameSpace": "",
                "basicName": "BodyLike",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Body"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Each response MAY contain a body property, which conforms to the same structure as request body properties (see Body). Responses that can return more than one response code MAY therefore have multiple bodies defined. For APIs without a priori knowledge of the response types for their responses, `*/*` MAY be used to indicate that responses that do not matching other defined data types MUST be accepted. Processing applications MUST match the most descriptive media type first if `*/*` is used."
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description attribute describes the intended use or meaning of the $self. This value MAY be formatted using Markdown."
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
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Params": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts"
  },
  {
    "classes": [
      {
        "name": "ResourceBase",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [
          {
            "typeName": "DeclaresDynamicType",
            "nameSpace": "",
            "basicName": "DeclaresDynamicType",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "ResourceType",
                "nameSpace": "",
                "basicName": "ResourceType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
          }
        ],
        "fields": [
          {
            "name": "methods",
            "type": {
              "base": {
                "typeName": "Methods.Method",
                "nameSpace": "Methods",
                "basicName": "Method",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Methods that are part of this resource type definition"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "is",
            "type": {
              "base": {
                "typeName": "Methods.TraitRef",
                "nameSpace": "Methods",
                "basicName": "TraitRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
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
                  "Instantiation of applyed traits"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
            "type": {
              "typeName": "ResourceTypeRef",
              "nameSpace": "",
              "basicName": "ResourceTypeRef",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Instantiation of applyed resource type"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "securedBy",
            "type": {
              "base": {
                "typeName": "Security.SecuritySchemeRef",
                "nameSpace": "Security",
                "basicName": "SecuritySchemeRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInArray",
                "arguments": []
              },
              {
                "name": "MetaModel.allowNull",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "securityScheme may also be applied to a resource by using the securedBy key, which is equivalent to applying the securityScheme to all methods that may be declared, explicitly or implicitly, by defining the resourceTypes or traits property for that resource. To indicate that the method may be called without applying any securityScheme, the method may be annotated with the null securityScheme."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "uriParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.URI"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Uri parameters of this resource"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "displayName",
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
                  "An alternate, human-friendly name for the resource type"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "baseUriParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "fieldOrParam",
                  true
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.BURI"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A resource or a method can override a base URI template's values. This is useful to restrict or change the default or parameter selection in the base URI. The baseUriParameters property MAY be used to override any or all parameters defined at the root level baseUriParameters property, as well as base URI parameters not specified at the root level."
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description attribute describes the intended use or meaning of the $self. This value MAY be formatted using Markdown."
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
        "name": "Resource",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "relativeUri",
            "type": {
              "typeName": "Sys.RelativeUriString",
              "nameSpace": "Sys",
              "basicName": "RelativeUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
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
                  "Relative URL of this resource from the parent resource"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "resources",
            "type": {
              "base": {
                "typeName": "Resource",
                "nameSpace": "",
                "basicName": "Resource",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Resource"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Children resources"
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
            "typeName": "ResourceBase",
            "nameSpace": "",
            "basicName": "ResourceBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ResourceTypeRef",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "resourceType",
            "type": {
              "typeName": "ResourceType",
              "nameSpace": "",
              "basicName": "ResourceType",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns referenced resource type"
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
            "typeName": "Reference",
            "nameSpace": "",
            "basicName": "Reference",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "ResourceType",
                "nameSpace": "",
                "basicName": "ResourceType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ResourceType",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [
          {
            "typeName": "DeclaresDynamicType",
            "nameSpace": "",
            "basicName": "DeclaresDynamicType",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "ResourceType",
                "nameSpace": "",
                "basicName": "ResourceType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
          }
        ],
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Name of the resource type"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "usage",
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
                  "Instructions on how and when the resource type should be used."
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
                "name": "MetaModel.hideFromUI",
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
            "typeName": "ResourceBase",
            "nameSpace": "",
            "basicName": "ResourceBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Params": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts",
      "Bodies": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts",
      "Methods": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts",
      "Security": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\resources.ts"
  },
  {
    "classes": [
      {
        "name": "MethodBase",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "responses",
            "type": {
              "base": {
                "typeName": "Bodies.Response",
                "nameSpace": "Bodies",
                "basicName": "Response",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Response"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Resource methods MAY have one or more responses. Responses MAY be described using the description property, and MAY include example attributes or schema properties."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "body",
            "type": {
              "base": {
                "typeName": "Bodies.BodyLike",
                "nameSpace": "Bodies",
                "basicName": "BodyLike",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Body"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Some method verbs expect the resource to be sent as a request body. For example, to create a resource, the request must include the details of the resource to create. Resources CAN have alternate representations. For example, an API might support both JSON and XML representations. A method's body is defined in the body property as a hashmap, in which the key MUST be a valid media type."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "protocols",
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
                    "HTTP",
                    "HTTPS"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A method can override an API's protocols value for that single method by setting a different value for the fields."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "securedBy",
            "type": {
              "base": {
                "typeName": "SecuritySchemeRef",
                "nameSpace": "",
                "basicName": "SecuritySchemeRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInArray",
                "arguments": []
              },
              {
                "name": "MetaModel.allowNull",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of the security schemas to apply, these must be defined in the securitySchemes declaration. To indicate that the method may be called without applying any securityScheme, the method may be annotated with the null securityScheme. Security schemas may also be applied to a resource with securedBy, which is equivalent to applying the security schemas to all methods that may be declared, explicitly or implicitly, by defining the resourceTypes or traits property for that resource."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "baseUriParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "fieldOrParam",
                  true
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "Params.ParameterLocation.BURI"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A resource or a method can override a base URI template's values. This is useful to restrict or change the default or parameter selection in the base URI. The baseUriParameters property MAY be used to override any or all parameters defined at the root level baseUriParameters property, as well as base URI parameters not specified at the root level."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
"""
val value4 =
"""
          {
            "name": "queryParameters",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "ParameterLocation.QUERY"
                ]
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New query parameter"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An APIs resources MAY be filtered (to return a subset of results) or altered (such as transforming a response body from JSON to XML format) by the use of query strings. If the resource or its method supports a query string, the query string MUST be defined by the queryParameters property"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "headers",
            "type": {
              "base": {
                "typeName": "Params.Parameter",
                "nameSpace": "Params",
                "basicName": "Parameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "ParameterLocation.HEADERS"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Headers that allowed at this position"
                ]
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Header"
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Method object allows description of http methods"
            ]
          }
        ],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Method",
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
                "name": "MetaModel.extraMetaKey",
                "arguments": [
                  "methods"
                ]
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "get",
                    "put",
                    "post",
                    "delete",
                    "patch",
                    "options",
                    "head",
                    "trace",
                    "connect"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Method that can be called"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "is",
            "type": {
              "base": {
                "typeName": "TraitRef",
                "nameSpace": "",
                "basicName": "TraitRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
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
                  "Instantiation of applyed traits"
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
            "typeName": "MethodBase",
            "nameSpace": "",
            "basicName": "MethodBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Trait",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [
          {
            "typeName": "DeclaresDynamicType",
            "nameSpace": "",
            "basicName": "DeclaresDynamicType",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "Trait",
                "nameSpace": "",
                "basicName": "Trait",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
          }
        ],
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Name of the trait"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "usage",
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
                  "Instructions on how and when the trait should be used."
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
                "name": "MetaModel.hideFromUI",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "displayName",
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
                  "An alternate, human-friendly name for the trait"
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
            "name": "MetaModel.inlinedTemplates",
            "arguments": []
          },
          {
            "name": "MetaModel.allowQuestion",
            "arguments": []
          }
        ],
        "extends": [
          {
            "typeName": "MethodBase",
            "nameSpace": "",
            "basicName": "MethodBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TraitRef",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "trait",
            "type": {
              "typeName": "Trait",
              "nameSpace": "",
              "basicName": "Trait",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns referenced trait"
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
            "typeName": "Reference",
            "nameSpace": "",
            "basicName": "Reference",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "Trait",
                "nameSpace": "",
                "basicName": "Trait",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Params": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts",
      "Bodies": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts",
      "Security": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
  },
  {
    "classes": [
      {
        "name": "SecuritySchemePart",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "displayName",
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
                  "An alternate, human-friendly name for the security scheme part"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "is",
            "type": {
              "base": {
                "typeName": "TraitRef",
                "nameSpace": "",
                "basicName": "TraitRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
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
                  "Instantiation of applyed traits"
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
            "typeName": "MethodBase",
            "nameSpace": "",
            "basicName": "MethodBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "headers": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Optional array of headers, documenting the possible headers that could be accepted."
              ]
            },
            {
              "name": "MetaModel.valueDescription",
              "arguments": [
                "Object whose property names are the request header names and whose values describe the values."
              ]
            }
          ],
          "queryParameters": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Query parameters, used by the schema in order to authorize the request. Mutually exclusive with queryString."
              ]
            },
            {
              "name": "MetaModel.valueDescription",
              "arguments": [
                "Object whose property names are the query parameter names and whose values describe the values."
              ]
            }
          ],
          "queryString": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "Specifies the query string, used by the schema in order to authorize the request. Mutually exclusive with queryParameters."
              ]
            },
            {
              "name": "MetaModel.valueDescription",
              "arguments": [
                "Type name or type declaration"
              ]
            }
          ],
          "responses": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "Optional array of responses, describing the possible responses that could be sent."
              ]
            }
          ],
          "description": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "A longer, human-friendly description of the security scheme part"
              ]
            },
            {
              "name": "MetaModel.valueDescription",
              "arguments": [
                "Markdown string"
              ]
            }
          ]
        }
      },
      {
        "name": "SecuritySchemeSettings",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.allowAny",
            "arguments": []
          }
        ],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "AbstractSecurityScheme",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [
          {
            "typeName": "Referencable",
            "nameSpace": "",
            "basicName": "Referencable",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "AbstractSecurityScheme",
                "nameSpace": "",
                "basicName": "AbstractSecurityScheme",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
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
              },
              {
                "name": "MetaModel.startFrom",
                "arguments": [
                  ""
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Name of the security scheme"
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
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "OAuth 1.0",
                    "OAuth 2.0",
                    "Basic Authentication",
                    "Digest Authentication",
                    "x-{other}"
                  ]
                ]
              },
              {
                "name": "MetaModel.descriminatingProperty",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The securitySchemes property MUST be used to specify an API's security mechanisms, including the required settings and the authentication methods that the API supports. one authentication method is allowed if the API supports them."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "string<br><br>The value MUST be one of<br>* OAuth 1.0,<br>* OAuth 2.0, <br>* BasicSecurityScheme Authentication<br>* DigestSecurityScheme Authentication<br>* x-&lt;other&gt;"
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
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description attribute MAY be used to describe a security schemes property."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "describedBy",
            "type": {
              "typeName": "SecuritySchemePart",
              "nameSpace": "",
              "basicName": "SecuritySchemePart",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A description of the request components related to Security that are determined by the scheme: the headers, query parameters or responses. As a best practice, even for standard security schemes, API designers SHOULD describe these properties of security schemes. Including the security scheme description completes an API documentation."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "settings",
            "type": {
              "typeName": "SecuritySchemeSettings",
              "nameSpace": "",
              "basicName": "SecuritySchemeSettings",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The settings attribute MAY be used to provide security scheme-specific information. The required attributes vary depending on the type of security scheme is being declared. It describes the minimum set of properties which any processing application MUST provide and validate if it chooses to implement the security scheme. Processing applications MAY choose to recognize other properties for things such as token lifetime, preferred cryptographic algorithms, and more."
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
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "SecuritySchemeRef",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "securityScheme",
            "type": {
              "typeName": "AbstractSecurityScheme",
              "nameSpace": "",
              "basicName": "AbstractSecurityScheme",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns AST node of security scheme, this reference refers to, or null."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "settings",
            "type": {
              "typeName": "SecuritySchemeSettings",
              "nameSpace": "",
              "basicName": "SecuritySchemeSettings",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
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
            "typeName": "Reference",
            "nameSpace": "",
            "basicName": "Reference",
            "typeKind": 0,
            "typeArguments": [
              {
                "typeName": "AbstractSecurityScheme",
                "nameSpace": "",
                "basicName": "AbstractSecurityScheme",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
              }
            ],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "OAuth1SecuritySchemeSettings",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "requestTokenUri",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URI of the Temporary Credential Request endpoint as defined in RFC5849 Section 2.1"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "FixedUriString"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "authorizationUri",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URI of the Resource Owner Authorization endpoint as defined in RFC5849 Section 2.2"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "FixedUriString"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "tokenCredentialsUri",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URI of the Token Request endpoint as defined in RFC5849 Section 2.3"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "FixedUriString"
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
            "name": "MetaModel.allowAny",
            "arguments": []
          },
          {
            "name": "MetaModel.functionalDescriminator",
            "arguments": [
              "$parent.type=='OAuth 1.0'"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "SecuritySchemeSettings",
            "nameSpace": "",
            "basicName": "SecuritySchemeSettings",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "OAuth2SecuritySchemeSettings",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "accessTokenUri",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URI of the Token Endpoint as defined in RFC6749 Section 3.2. Not required forby implicit grant type."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "FixedUriString"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "authorizationUri",
            "type": {
              "typeName": "Sys.FixedUri",
              "nameSpace": "Sys",
              "basicName": "FixedUri",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The URI of the Authorization Endpoint as defined in RFC6749 Section 3.1. Required forby authorization_code and implicit grant types."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "FixedUriString"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "authorizationGrants",
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
                  "A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of: authorization_code, password, client_credentials, implicit, or refresh_token."
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of:<br>* authorization_code<br>* password<br>* client_credentials <br>* implicit<br>* refresh_token."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "scopes",
            "type": {
              "base": {
                "typeName": "Scope",
                "nameSpace": "",
                "basicName": "Scope",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of scopes supported by the security scheme as defined in RFC6749 Section 3.3"
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
            "name": "MetaModel.allowAny",
            "arguments": []
          }
        ],
        "extends": [
          {
            "typeName": "SecuritySchemeSettings",
            "nameSpace": "",
            "basicName": "SecuritySchemeSettings",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Scope",
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
            "annotations": [],
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
        "annotationOverridings": {}
      },
      {
        "name": "OAuth2SecurityScheme",
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
              "value": "OAuth 2.0"
            },
            "optional": false
          },
          {
            "name": "settings",
            "type": {
              "typeName": "OAuth2SecuritySchemeSettings",
              "nameSpace": "",
              "basicName": "OAuth2SecuritySchemeSettings",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "AbstractSecurityScheme",
            "nameSpace": "",
            "basicName": "AbstractSecurityScheme",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "OAuth1SecurityScheme",
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
              "value": "OAuth 1.0"
            },
            "optional": false
          },
          {
            "name": "settings",
            "type": {
              "typeName": "OAuth1SecuritySchemeSettings",
              "nameSpace": "",
              "basicName": "OAuth1SecuritySchemeSettings",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "AbstractSecurityScheme",
            "nameSpace": "",
            "basicName": "AbstractSecurityScheme",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "BasicSecurityScheme",
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
              "value": "Basic Authentication"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "AbstractSecurityScheme",
            "nameSpace": "",
            "basicName": "AbstractSecurityScheme",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DigestSecurityScheme",
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
              "value": "Digest Authentication"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "AbstractSecurityScheme",
            "nameSpace": "",
            "basicName": "AbstractSecurityScheme",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "CustomSecurityScheme",
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
              "value": "x-{other}"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.description",
            "arguments": [
              "Declares globally referable security schema definition"
            ]
          },
          {
            "name": "MetaModel.actuallyExports",
            "arguments": [
              "$self"
            ]
          },
          {
            "name": "MetaModel.referenceIs",
            "arguments": [
              "settings"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "AbstractSecurityScheme",
            "nameSpace": "",
            "basicName": "AbstractSecurityScheme",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\systemTypes.ts",
      "Params": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\parameters.ts",
      "Bodies": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\bodies.ts",
      "Common": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\common.ts",
      "Methods": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\methods.ts"
    },
    "name": "C:\\GIT-repos\\raml-org\\raml-definition-system\\raml-definition\\spec-0.8\\security.ts"
  }
]"""

val value = value1 + value2 + value3 + value4
}
