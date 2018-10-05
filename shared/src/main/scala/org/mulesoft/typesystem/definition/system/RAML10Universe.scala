package org.mulesoft.typesystem.definition.system

object RAML10Universe {
val value1 =
"""[
  {
    "classes": [
      {
        "name": "Library",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
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
                  "contains description of why library exist"
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
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Namespace which the library is imported under"
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
            "typeName": "LibraryBase",
            "nameSpace": "",
            "basicName": "LibraryBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "LibraryBase",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "schemas",
            "type": {
              "base": {
                "typeName": "TypeDeclaration",
                "nameSpace": "",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "Alias for the equivalent \"types\" property, for compatibility with RAML 0.8. Deprecated - API definitions should use the \"types\" property, as the \"schemas\" alias for that property name may be removed in a future RAML version. The \"types\" property allows for XML and JSON schemas."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "types",
            "type": {
              "base": {
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "locationKind",
                  "DataModel.LocationKind.MODELS"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declarations of (data) types for use within this API"
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "Declarations of (data) types for use within this API."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose properties map type names to type declarations; or an array of such objects"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declarations of traits for use within this API"
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "Declarations of traits for use within this API."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose properties map trait names to trait declarations; or an array of such objects"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declarations of resource types for use within this API"
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "Declarations of resource types for use within this API."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose properties map resource type names to resource type declarations; or an array of such objects"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "annotationTypes",
            "type": {
              "base": {
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "decls",
                  "true"
                ]
              },
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declarations of annotation types for use by annotations"
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "Declarations of annotation types for use by annotations."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose properties map annotation type names to annotation type declarations; or an array of such objects"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "Security schemas declarations"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Declarations of security schemes for use within this API."
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "Declarations of security schemes for use within this API."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose properties map security scheme names to security scheme declarations; or an array of such objects"
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
            "name": "MetaModel.internalClass",
            "arguments": []
          }
        ],
        "extends": [
          {
            "typeName": "FragmentDeclaration",
            "nameSpace": "",
            "basicName": "FragmentDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
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
                  "Short plain-text label for the API"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A longer, human-friendly description of the API"
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
                  "The version of the API, e.g. 'v1'"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A URI that's to be used as the base of all the resources' URIs. Often used as the base of the URL of each resource, containing the location of the API. Can be a template URI."
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
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "DataModel.ModelLocation.BURI"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "DataModel.LocationKind.APISTRUCTURE"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Named parameters used in the baseUri (template)"
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
                  "The protocols supported by the API"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Array of strings, with each being \"HTTP\" or \"HTTPS\", case-insensitive"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "mediaType",
            "type": {
              "base": {
                "typeName": "Bodies.MimeType",
                "nameSpace": "Bodies",
                "basicName": "MimeType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The default media type to use for request and response bodies (payloads), e.g. \"application/json\""
                ]
              },
              {
                "name": "MetaModel.inherited",
                "arguments": []
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Media type string"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "The security schemes that apply to every resource and method in the API"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.documentationTableLabel",
                "arguments": [
                  "/&lt;relativeUri&gt;"
                ]
              },
              {
                "name": "MetaModel.newInstanceName",
                "arguments": [
                  "New Resource"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The resources of the API, identified as relative URIs that begin with a slash (/). Every property whose key begins with a slash (/), and is either at the root of the API definition or is the child property of a resource property, is a resource property, e.g.: /users, /{groupId}, etc"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
                  "Additional overall documentation for the API"
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
            "typeName": "LibraryBase",
            "nameSpace": "",
            "basicName": "LibraryBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "annotations": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Annotations to be applied to this API. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
              ]
            }
          ]
        }
      },
      {
        "name": "Overlay",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
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
                  "contains description of why overlay exist"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "extends",
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
                  "Location of a valid RAML API definition (or overlay or extension), the overlay is applied to."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Short plain-text label for the API"
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
            "typeName": "Api",
            "nameSpace": "",
            "basicName": "Api",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "Extension",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
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
                  "contains description of why extension exist"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "extends",
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
                  "Location of a valid RAML API definition (or overlay or extension), the extension is applied to"
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Short plain-text label for the API"
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
            "typeName": "Api",
            "nameSpace": "",
            "basicName": "Api",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "UsesDeclaration",
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
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Name prefix (without dot) used to refer imported declarations"
                ]
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
                "name": "MetaModel.description",
                "arguments": [
                  "Content of the schema"
                ]
              },
              {
                "name": "MetaModel.canBeValue",
                "arguments": []
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
        "annotations": [],
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value2 =
"""{
        "name": "FragmentDeclaration",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "uses",
            "type": {
              "base": {
                "typeName": "UsesDeclaration",
                "nameSpace": "",
                "basicName": "UsesDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
          }
        ],
        "moduleName": null,
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
                  "Title of documentation section"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
        "annotations": [
          {
            "name": "MetaModel.possibleInterfaces",
            "arguments": [
              [
                "FragmentDeclaration"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "Methods": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts",
      "Resources": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts",
      "Decls": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts",
      "Params": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\parameters.ts",
      "Common": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts",
      "Bodies": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Security": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\api.ts"
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
        "methods": [
          {
            "returnType": {
              "typeName": "string",
              "nameSpace": "",
              "basicName": "string",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": null
            },
            "name": "value",
            "start": 170,
            "end": 210,
            "text": "\n\n  value():string {\n    return null\n  }",
            "arguments": []
          }
        ],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "NilType",
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
              "nil"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "nil"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TimeOnlyType",
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
              "time-only"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "time-only"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateOnlyType",
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
              "date-only"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "date-only"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateTimeOnlyType",
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
              "datetime-only"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "datetime-only"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateTimeType",
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
              "datetime"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "datetime"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "FileType",
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
              "file"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "file"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "FixedUriString",
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ContentType",
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
              "[GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown/)"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
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
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Common": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts"
  },
  {
    "classes": [
      {
        "name": "ExampleSpec",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
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
                "name": "MetaModel.example",
                "arguments": []
              },
              {
                "name": "MetaModel.selfNode",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "String representation of example"
                ]
              },
              {
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "* Valid value for this type<br>* String representing the serialized version of a valid value"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "strict",
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
                  "By default, examples are validated against any type declaration. Set this to false to allow examples that need not validate."
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
                "name": "MetaModel.key",
                "arguments": []
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Example identifier, if specified"
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
                  "An alternate, human-friendly name for the example"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A longer, human-friendly description of the example"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "markdown string"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          }
        ],
"""
val value3 =
"""
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.customHandling",
            "arguments": []
          },
          {
            "name": "MetaModel.possibleInterfaces",
            "arguments": [
              [
                "FragmentDeclaration"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "annotations": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Annotations to be applied to this example. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
              ]
            }
          ]
        }
      },
      {
        "name": "TypeDeclaration",
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
                  "Type name for top level types. For properties and parameters -- property o parameter name, respectively. For bodies -- media type."
                ]
              },
              {
                "name": "MetaModel.extraMetaKey",
                "arguments": [
                  "headers"
                ]
              },
              {
                "name": "MetaModel.hide",
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
                  "The displayName attribute specifies the type display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "facets",
            "type": {
              "base": {
                "typeName": "TypeDeclaration",
                "nameSpace": "",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.declaringFields",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "When extending from a type you can define new facets (which can then be set to concrete values by subtypes)."
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "fixedFacets",
            "type": {
              "base": {
                "typeName": "TypeExtension",
                "nameSpace": "",
                "basicName": "TypeExtension",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns facets fixed by the type. Value is an object with properties named after facets fixed. Value of each property is a value of the corresponding facet."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "schema",
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
                "name": "MetaModel.typeExpression",
                "arguments": []
              },
              {
                "name": "MetaModel.allowMultiple",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Alias for the equivalent \"type\" property, for compatibility with RAML 0.8. Deprecated - API definitions should use the \"type\" property, as the \"schema\" alias for that property name may be removed in a future RAML version. The \"type\" property allows for XML and JSON schemas."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Single string denoting the base type or type expression"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "type",
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
                "name": "MetaModel.typeExpression",
                "arguments": []
              },
              {
                "name": "MetaModel.allowMultiple",
                "arguments": []
              },
              {
                "name": "MetaModel.canBeValue",
                "arguments": []
              },
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
                  "A base type which the current type extends, or more generally a type expression."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "string denoting the base type or type expression"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "location",
            "type": {
              "typeName": "ModelLocation",
              "nameSpace": "",
              "basicName": "ModelLocation",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.system",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Location of the parameter (can not be edited by user)"
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "locationKind",
            "type": {
              "typeName": "LocationKind",
              "nameSpace": "",
              "basicName": "LocationKind",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.system",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Kind of location"
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
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
                  "Provides default value for a property"
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "example",
            "type": {
              "typeName": "ExampleSpec",
              "nameSpace": "",
              "basicName": "ExampleSpec",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.example",
                "arguments": []
              },
              {
                "name": "MetaModel.selfNode",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An example of this type instance represented as string or yaml map/sequence. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the examples property is present."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "* Valid value for this type<br>* String representing the serialized version of a valid value"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "examples",
            "type": {
              "base": {
                "typeName": "ExampleSpec",
                "nameSpace": "",
                "basicName": "ExampleSpec",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.embeddedInMaps",
                "arguments": []
              },
              {
                "name": "MetaModel.example",
                "arguments": []
              },
              {
                "name": "MetaModel.selfNode",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "An example of this type instance represented as string. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the example property is present."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "* Valid value for this type<br>* String representing the serialized version of a valid value"
                ]
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
                "name": "MetaModel.requireValue",
                "arguments": [
                  "fieldOrParam",
                  true
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "For property or parameter states if it is required."
                ]
              },
              {
                "name": "MetaModel.describesAnnotation",
                "arguments": [
                  "required"
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              },
              {
                "name": "MetaModel.defaultBooleanValue",
                "arguments": [
                  true
                ]
              },
              {
                "name": "MetaModel.parentPropertiesRestriction",
                "arguments": [
                  [
                    "properties",
                    "queryParameters",
                    "headers"
                  ]
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A longer, human-friendly description of the type"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "markdown string"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "xml",
            "type": {
              "typeName": "XMLFacetInfo",
              "nameSpace": "",
              "basicName": "XMLFacetInfo",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "allowedTargets",
            "type": {
              "base": {
                "typeName": "AnnotationTarget",
                "nameSpace": "",
                "basicName": "AnnotationTarget",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "API",
                    "DocumentationItem",
                    "Resource",
                    "Method",
                    "Response",
                    "RequestBody",
                    "ResponseBody",
                    "TypeDeclaration",
                    "NamedExample",
                    "ResourceType",
                    "Trait",
                    "SecurityScheme",
                    "SecuritySchemeSettings",
                    "AnnotationTypeDeclaration",
                    "Library",
                    "Overlay",
                    "Extension",
                    "Scalar"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Restrictions on where annotations of this type can be applied. If this property is specified, annotations of this type may only be applied on a property corresponding to one of the target names specified as the value of this property."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An array, or single, of names allowed target nodes."
                ]
              },
              {
                "name": "MetaModel.parentPropertiesRestriction",
                "arguments": [
                  [
                    "annotationTypes"
                  ]
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.convertsToGlobalOfType",
            "arguments": [
              "SchemaString"
            ]
          },
          {
            "name": "MetaModel.canInherit",
            "arguments": [
              "mediaType"
            ]
          },
          {
            "name": "MetaModel.possibleInterfaces",
            "arguments": [
              [
                "FragmentDeclaration"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "annotations": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Annotations to be applied to this type. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
              ]
            }
          ]
        }
      },
      {
        "name": "TypeExtension",
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
        "name": "XMLFacetInfo",
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ArrayTypeDeclaration",
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
              "value": "array"
            },
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
            "annotations": [
              {
                "name": "MetaModel.facetId",
                "arguments": [
                  "uniqueItems"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Should items in array be unique"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "items",
            "type": {
              "typeName": "TypeDeclaration",
              "nameSpace": "",
              "basicName": "TypeDeclaration",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.typeExpression",
                "arguments": []
              },
              {
                "name": "MetaModel.allowMultiple",
                "arguments": []
              },
              {
                "name": "MetaModel.canBeValue",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Array component type."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Inline type declaration or type name."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.facetId",
                "arguments": [
                  "minItems"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Minimum amount of items in array"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "integer ( >= 0 ). Defaults to 0"
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.facetId",
                "arguments": [
                  "maxItems"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Maximum amount of items in array"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "integer ( >= 0 ). Defaults to undefined."
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.convertsToGlobalOfType",
            "arguments": [
              "SchemaString"
            ]
          },
          {
            "name": "MetaModel.alias",
            "arguments": [
              "array"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "UnionTypeDeclaration",
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
              "value": "union"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.convertsToGlobalOfType",
            "arguments": [
              "SchemaString"
            ]
          },
          {
            "name": "MetaModel.requireValue",
            "arguments": [
              "locationKind",
              "LocationKind.MODELS"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "ObjectTypeDeclaration",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "type",
            "type": null,
            "annotations": [
              {
                "name": "MetaModel.hide",
                "arguments": []
              }
            ],
            "valueConstraint": {
              "isCallConstraint": false,
              "value": "object"
            },
            "optional": false
          },
          {
            "name": "properties",
            "type": {
              "base": {
                "typeName": "TypeDeclaration",
                "nameSpace": "",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
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
                "name": "MetaModel.description",
                "arguments": [
                  "The properties that instances of this type may or must have."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose keys are the properties' names and whose values are property declarations."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "minProperties",
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "minProperties"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The minimum number of properties allowed for instances of this type."
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "maxProperties",
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "maxProperties"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The maximum number of properties allowed for instances of this type."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A Boolean that indicates if an object instance has additional properties."
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Type property name to be used as discriminator, or boolean"
                ]
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "discriminatorValue",
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
                  "The value of discriminator for the type."
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
                "typeName": "any",
                "nameSpace": "",
                "basicName": "any",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.definingPropertyIsEnough",
            "arguments": [
              "properties"
            ]
          },
          {
            "name": "MetaModel.setsContextValue",
            "arguments": [
              "field",
              "true"
            ]
          },
          {
            "name": "MetaModel.convertsToGlobalOfType",
            "arguments": [
              "SchemaString"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value4 =
"""
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "pattern"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Regular expression that this string should path"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "regexp"
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "minLength"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Minimum length of the string"
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "maxLength"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Maximum length of the string"
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "enum"
                ]
              },
              {
                "name": "MetaModel.describesAnnotation",
                "arguments": [
                  "oneOf"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The enum attribute provides an enumeration of the parameter's valid values. This MUST be an array. If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. If there is no matching value, the clients and servers MUST treat this as an error."
                ]
              },
              {
                "name": "MetaModel.hide",
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value must be a string"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
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
          },
          {
            "name": "enum",
            "type": {
              "base": {
                "typeName": "boolean",
                "nameSpace": "",
                "basicName": "boolean",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value must be a boolean"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "minimum"
                ]
              },
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
                "name": "MetaModel.facetId",
                "arguments": [
                  "maximum"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type number or integer) The maximum attribute specifies the parameter's maximum value."
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
                "typeName": "number",
                "nameSpace": "",
                "basicName": "number",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": null
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.facetId",
                "arguments": [
                  "enum"
                ]
              },
              {
                "name": "MetaModel.describesAnnotation",
                "arguments": [
                  "oneOf"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "(Optional, applicable only for parameters of type string) The enum attribute provides an enumeration of the parameter's valid values. This MUST be an array. If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. If there is no matching value, the clients and servers MUST treat this as an error."
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
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
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "int32",
                    "int64",
                    "int",
                    "long",
                    "float",
                    "double",
                    "int16",
                    "int8"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Value format"
                ]
              }
            ],
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
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A numeric instance is valid against \"multipleOf\" if the result of the division of the instance by this keyword's value is an integer."
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value MUST be a number. Indicate floating point numbers as defined by YAML."
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
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
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "int32",
                    "int64",
                    "int",
                    "long",
                    "int16",
                    "int8"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Value format"
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "Value MUST be a integer."
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateOnlyTypeDeclaration",
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
              "value": "date-only"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "the \"full-date\" notation of RFC3339, namely yyyy-mm-dd (no implications about time or timezone-offset)"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "TimeOnlyTypeDeclaration",
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
              "value": "time-only"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "the \"partial-time\" notation of RFC3339, namely hh:mm:ss[.ff...] (no implications about date or timezone-offset)"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateTimeOnlyTypeDeclaration",
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
              "value": "datetime-only"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "combined date-only and time-only with a separator of \"T\", namely yyyy-mm-ddThh:mm:ss[.ff...] (no implications about timezone-offset)"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "DateTimeTypeDeclaration",
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
              "value": "datetime"
            },
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
            "annotations": [
              {
                "name": "MetaModel.oneOf",
                "arguments": [
                  [
                    "rfc3339",
                    "rfc2616"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Format used for this date time rfc3339 or rfc2616"
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
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "a timestamp, either in the \"date-time\" notation of RFC3339, if format is omitted or is set to rfc3339, or in the format defined in RFC2616, if format is set to rfc2616."
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "NilTypeDeclaration",
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
              "value": "datetime-only"
            },
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [
          {
            "name": "MetaModel.availableToUser",
            "arguments": []
          },
          {
            "name": "MetaModel.description",
            "arguments": [
              "The type has single instance: null"
            ]
          },
          {
            "name": "MetaModel.declaresSubTypeOf",
            "arguments": [
              "TypeDeclaration"
            ]
          }
        ],
        "extends": [
          {
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      }
    ],
    "aliases": [],
    "enumDeclarations": [
      {
        "name": "ModelLocation",
        "members": [
          "QUERY",
          "HEADERS",
          "URI",
          "FORM",
          "BURI",
          "ANNOTATION",
          "MODEL",
          "SECURITYSCHEMATYPE"
        ]
      },
      {
        "name": "LocationKind",
        "members": [
          "APISTRUCTURE",
          "DECLARATIONS",
          "MODELS"
        ]
      }
    ],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "Bodies": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts",
      "Common": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts",
      "Declarations": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
  },
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
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
              "typeName": "Sys.StatusCodeString",
              "nameSpace": "Sys",
              "basicName": "StatusCodeString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
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
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              }
            ],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "headers",
            "type": {
              "base": {
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
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
                  "DataModel.ModelLocation.HEADERS"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "DataModel.LocationKind.APISTRUCTURE"
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
                  "Detailed information about any response headers returned by this method"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Object whose property names are the response header names and whose values describe the values."
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
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
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
                  "The body of the response: a body declaration"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Object whose properties are either<br>* Media types and whose values are type objects describing the request body for that media type, or<br>* a type object describing the request body for the default media type specified in the root mediaType property."
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A longer, human-friendly description of the response"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Markdown string"
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "displayName": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "An alternate, human-friendly name for the response"
              ]
            }
          ],
          "annotations": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Annotations to be applied to this response. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
              ]
            }
          ]
        }
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Common": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts"
  },
  {
    "classes": [
      {
        "name": "Annotable",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "annotations",
            "type": {
              "base": {
                "typeName": "Decls.AnnotationRef",
                "nameSpace": "Decls",
                "basicName": "AnnotationRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.noDirectParse",
                "arguments": []
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "datamodel.LocationKind.APISTRUCTURE"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "location",
                  "datamodel.ModelLocation.ANNOTATION"
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Most of RAML model elements may have attached annotations decribing additional meta data about this element"
                ]
              },
              {
                "name": "MetaModel.documentationTableLabel",
                "arguments": [
                  "(&lt;annotationName&gt;)"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "A value corresponding to the declared type of this annotation."
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
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "Decls": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts"
  },
  {
    "classes": [
      {
        "name": "AnnotationRef",
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
            "name": "annotation",
            "type": {
              "typeName": "DataModel.TypeDeclaration",
              "nameSpace": "DataModel",
              "basicName": "TypeDeclaration",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.customHandling",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Returns referenced annotation"
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
              "Annotations allow you to attach information to your API"
            ]
          },
          {
            "name": "MetaModel.tags",
            "arguments": [
              [
                "annotations"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "Reference",
            "nameSpace": "",
            "basicName": "Reference",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "AnnotationTarget",
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
              "Elements to which this Annotation can be applied (enum)"
            ]
          },
          {
            "name": "MetaModel.tags",
            "arguments": [
              [
                "annotations"
              ]
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
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
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Common": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\common.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\declarations.ts"
  },
  {
    "classes": [
      {
        "name": "TemplateParameter",
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
        "name": "TemplateRef",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "parameters",
            "type": {
              "base": {
                "typeName": "TemplateParameter",
                "nameSpace": "",
                "basicName": "TemplateParameter",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
            "typeName": "Reference",
            "nameSpace": "",
            "basicName": "Reference",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
            "typeName": "TemplateRef",
            "nameSpace": "",
            "basicName": "TemplateRef",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
          },
          {
            "name": "MetaModel.possibleInterfaces",
            "arguments": [
              [
                "FragmentDeclaration"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "MethodBase",
            "nameSpace": "",
            "basicName": "MethodBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "displayName": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "The displayName attribute specifies the trait display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
              ]
            }
          ]
        }
      },
      {
        "name": "MethodBase",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "body",
            "type": {
              "base": {
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
                  "A method can override the protocols specified in the resource or at the API root, by employing this property."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "array of strings of value HTTP or HTTPS, or a single string of such kind, case-insensitive"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
            "name": "securedBy",
            "type": {
              "base": {
                "typeName": "Security.SecuritySchemeRef",
                "nameSpace": "Security",
                "basicName": "SecuritySchemeRef",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
            "name": "description",
            "type": {
              "typeName": "Sys.MarkdownString",
              "nameSpace": "Sys",
              "basicName": "MarkdownString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
            },
            "annotations": [],
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
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          }
        ],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "Operation",
            "nameSpace": "",
            "basicName": "Operation",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
"""
val value5 =
"""
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
                    "options",
                    "head",
                    "patch",
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
              },
              {
                "name": "MetaModel.hide",
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
            "typeName": "MethodBase",
            "nameSpace": "",
            "basicName": "MethodBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "displayName": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "The displayName attribute specifies the method display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
              ]
            }
          ]
        }
      },
      {
        "name": "Operation",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [
          {
            "name": "queryParameters",
            "type": {
              "base": {
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
                  "DataModel.ModelLocation.QUERY"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "DataModel.LocationKind.APISTRUCTURE"
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
                  "An APIs resources MAY be filtered (to return a subset of results) or altered (such as transforming  a response body from JSON to XML format) by the use of query strings. If the resource or its method supports a query string, the query string MUST be defined by the queryParameters property"
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
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
                  "DataModel.ModelLocation.HEADERS"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "DataModel.LocationKind.APISTRUCTURE"
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
            "name": "queryString",
            "type": {
              "typeName": "DataModel.TypeDeclaration",
              "nameSpace": "DataModel",
              "basicName": "TypeDeclaration",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Specifies the query string needed by this method. Mutually exclusive with queryParameters."
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
                "typeName": "Bodies.Response",
                "nameSpace": "Bodies",
                "basicName": "Response",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
                  "response",
                  "true"
                ]
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
                  "Information about the expected responses to a request"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "An object whose keys are the HTTP status codes of the responses and whose values describe the responses."
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
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
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "Bodies": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\bodies.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Security": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
  },
  {
    "classes": [
      {
        "name": "SecuritySchemePart",
        "methods": [],
        "typeParameters": [],
        "typeParameterConstraint": [],
        "implements": [],
        "fields": [],
        "isInterface": false,
        "annotations": [],
        "extends": [
          {
            "typeName": "Operation",
            "nameSpace": "",
            "basicName": "Operation",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "annotations": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "Annotations to be applied to this security scheme part. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
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
        "extends": [
          {
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "typeName": "Sys.FixedUriString",
              "nameSpace": "Sys",
              "basicName": "FixedUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "typeName": "Sys.FixedUriString",
              "nameSpace": "Sys",
              "basicName": "FixedUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "typeName": "Sys.FixedUriString",
              "nameSpace": "Sys",
              "basicName": "FixedUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
          },
          {
            "name": "signatures",
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
                    "HMAC-SHA1",
                    "RSA-SHA1",
                    "PLAINTEXT"
                  ]
                ]
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "List of the signature methods used by the server. Available methods: HMAC-SHA1, RSA-SHA1, PLAINTEXT"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "typeName": "Sys.FixedUriString",
              "nameSpace": "Sys",
              "basicName": "FixedUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "typeName": "Sys.FixedUriString",
              "nameSpace": "Sys",
              "basicName": "FixedUriString",
              "typeKind": 0,
              "typeArguments": [],
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
            },
            "annotations": [
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
                "name": "MetaModel.required",
                "arguments": []
              },
              {
                "name": "MetaModel.oftenKeys",
                "arguments": [
                  [
                    "authorization_code",
                    "password",
                    "client_credentials",
                    "implicit"
                  ]
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of: authorization_code, password, client_credentials, implicit, or any absolute url."
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of:<br>* authorization_code<br>* password<br>* client_credentials<br>* implicit <br>*  or any absolute url."
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "AbstractSecurityScheme",
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
                    "Pass Through",
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
                  "string<br><br>The value MUST be one of<br>* OAuth 1.0,<br>* OAuth 2.0,<br>* BasicSecurityScheme Authentication<br>* DigestSecurityScheme Authentication<br>* Pass Through<br>* x-&lt;other&gt;"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description attribute MAY be used to describe a security schemes property."
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The description MAY be used to describe a securityScheme."
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
                  "The displayName attribute specifies the security scheme display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
          }
        ],
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },
      {
        "name": "PassThroughSecurityScheme",
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
              "value": "Pass Through"
            },
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {}
      },"""
    val value6 =
    """{
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
              "Declares globally referable security scheme definition"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
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
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "Methods": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts"
  },
  {
    "classes": [
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
            "typeName": "TemplateRef",
            "nameSpace": "",
            "basicName": "TemplateRef",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
        "annotations": [
          {
            "name": "MetaModel.inlinedTemplates",
            "arguments": []
          },
          {
            "name": "MetaModel.allowQuestion",
            "arguments": []
          },
          {
            "name": "MetaModel.possibleInterfaces",
            "arguments": [
              [
                "FragmentDeclaration"
              ]
            ]
          }
        ],
        "extends": [
          {
            "typeName": "ResourceBase",
            "nameSpace": "",
            "basicName": "ResourceBase",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "displayName": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "The displayName attribute specifies the resource type display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
              ]
            }
          ]
        }
      },
      {
        "name": "ResourceBase",
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
            "annotations": [],
            "valueConstraint": null,
            "optional": false
          },
          {
            "name": "methods",
            "type": {
              "base": {
                "typeName": "Methods.Method",
                "nameSpace": "Methods",
                "basicName": "Method",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Methods that are part of this resource type definition"
                ]
              },
              {
                "name": "MetaModel.markdownDescription",
                "arguments": [
                  "The methods available on this resource."
                ]
              },
              {
                "name": "MetaModel.documentationTableLabel",
                "arguments": [
                  "get?<br>patch?<br>put?<br>post?<br>delete?<br>options?<br>head?"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "Object describing the method"
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
                  "A list of the traits to apply to all methods declared (implicitly or explicitly) for this resource. Individual methods may override this declaration"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "array, which can contain each of the following elements:<br>* name of unparametrized trait <br>* a key-value pair with trait name as key and a map of trait parameters as value<br>* inline trait declaration <br><br>(or a single element of any above kind)"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "The resource type which this resource inherits."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "one of the following elements:<br>* name of unparametrized resource type<br>* a key-value pair with resource type name as key and a map of its parameters as value<br>* inline resource type declaration"
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
            },
            "annotations": [],
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
                  "The security schemes that apply to all methods declared (implicitly or explicitly) for this resource."
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "array of security scheme names or a single security scheme name"
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
                "typeName": "DataModel.TypeDeclaration",
                "nameSpace": "DataModel",
                "basicName": "TypeDeclaration",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
                  "DataModel.ModelLocation.URI"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "locationKind",
                  "DataModel.LocationKind.APISTRUCTURE"
                ]
              },
              {
                "name": "MetaModel.setsContextValue",
                "arguments": [
                  "fieldOrParam",
                  true
                ]
              },
              {
                "name": "MetaModel.description",
                "arguments": [
                  "Detailed information about any URI parameters of this resource"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "object whose property names are the URI parameter names and whose values describe the values"
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
            "typeName": "Annotable",
            "nameSpace": "",
            "basicName": "Annotable",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
          }
        ],
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
              "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
              },
              {
                "name": "MetaModel.hide",
                "arguments": []
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
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
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
                  "A nested resource is identified as any property whose name begins with a slash (\"/\") and is therefore treated as a relative URI."
                ]
              },
              {
                "name": "MetaModel.documentationTableLabel",
                "arguments": [
                  "/&lt;relativeUri&gt;"
                ]
              },
              {
                "name": "MetaModel.valueDescription",
                "arguments": [
                  "object describing the nested resource"
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
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "displayName": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "The displayName attribute specifies the resource display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself)."
              ]
            }
          ],
          "description": [
            {
              "name": "MetaModel.description",
              "arguments": [
                "A longer, human-friendly description of the resource."
              ]
            },
            {
              "name": "MetaModel.valueDescription",
              "arguments": [
                "Markdown string"
              ]
            }
          ],
          "annotations": [
            {
              "name": "MetaModel.markdownDescription",
              "arguments": [
                "Annotations to be applied to this resource. Annotations are any property whose key begins with \"(\" and ends with \")\" and whose name (the part between the beginning and ending parentheses) is a declared annotation name."
              ]
            }
          ]
        }
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts",
      "Security": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\security.ts",
      "Methods": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\methods.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\resources.ts"
  },
  {
    "classes": [
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
          },
          {
            "name": "fileTypes",
            "type": {
              "base": {
                "typeName": "Sys.ContentType",
                "nameSpace": "Sys",
                "basicName": "ContentType",
                "typeKind": 0,
                "typeArguments": [],
                "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\parameters.ts"
              },
              "typeKind": 1
            },
            "annotations": [
              {
                "name": "MetaModel.description",
                "arguments": [
                  "A list of valid content-type strings for the file. The file type */* should be a valid value."
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
                  "The minLength attribute specifies the parameter value's minimum number of bytes."
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
                  "The maxLength attribute specifies the parameter value's maximum number of bytes."
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
            "name": "MetaModel.availableToUser",
            "arguments": []
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
            "typeName": "TypeDeclaration",
            "nameSpace": "",
            "basicName": "TypeDeclaration",
            "typeKind": 0,
            "typeArguments": [],
            "modulePath": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\parameters.ts"
          }
        ],
        "moduleName": null,
        "annotationOverridings": {
          "avaliableToUser": []
        }
      }
    ],
    "aliases": [],
    "enumDeclarations": [],
    "imports": {
      "MetaModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\metamodel.ts",
      "Sys": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\systemTypes.ts",
      "DataModel": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\datamodel.ts"
    },
    "name": "C:\\GIT-repos\\AMF\\raml-definition-system\\raml-definition\\spec-1.0\\parameters.ts"
  }
]
"""
val value = value1 + value2 + value3 + value4 + value5 + value6

}
