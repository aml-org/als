package org.mulesoft.als.server.wasm

object Extra {
  val profile: String =
    """#%Validation Profile 1.0
      |
      |profile: Test1
      |
      |violation:
      |  - scalar-parameters
      |
      |validations:
      |  scalar-parameters:
      |    targetClass: apiContract.Parameter
      |    message: Scalars in parameters must have minLength defined
      |    propertyConstraints:
      |      shapes.schema / shacl.minLength:
      |        minCount: 1
      |""".stripMargin
  val data: String =
    """
      |[
      |  {
      |    "@id": "file://test/data/integration/profile1/negative.data.raml",
      |    "@type": [
      |      "http://a.ml/vocabularies/document#Document",
      |      "http://a.ml/vocabularies/document#Fragment",
      |      "http://a.ml/vocabularies/document#Module",
      |      "http://a.ml/vocabularies/document#Unit"
      |    ],
      |    "http://a.ml/vocabularies/document#encodes": [
      |      {
      |        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api",
      |        "@type": [
      |          "http://a.ml/vocabularies/apiContract#WebAPI",
      |          "http://a.ml/vocabularies/apiContract#API",
      |          "http://a.ml/vocabularies/document#RootDomainElement",
      |          "http://a.ml/vocabularies/document#DomainElement"
      |        ],
      |        "http://a.ml/vocabularies/core#name": [
      |          {
      |            "@value": "Test API"
      |          }
      |        ],
      |        "http://a.ml/vocabularies/apiContract#endpoint": [
      |          {
      |            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1",
      |            "@type": [
      |              "http://a.ml/vocabularies/apiContract#EndPoint",
      |              "http://a.ml/vocabularies/document#DomainElement"
      |            ],
      |            "http://a.ml/vocabularies/apiContract#path": [
      |              {
      |                "@value": "/endpoint1"
      |              }
      |            ],
      |            "http://a.ml/vocabularies/apiContract#supportedOperation": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get",
      |                "@type": [
      |                  "http://a.ml/vocabularies/apiContract#Operation",
      |                  "http://a.ml/vocabularies/document#DomainElement"
      |                ],
      |                "http://a.ml/vocabularies/apiContract#method": [
      |                  {
      |                    "@value": "get"
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/apiContract#expects": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request",
      |                    "@type": [
      |                      "http://a.ml/vocabularies/apiContract#Request",
      |                      "http://a.ml/vocabularies/apiContract#Message",
      |                      "http://a.ml/vocabularies/document#DomainElement"
      |                    ],
      |                    "http://a.ml/vocabularies/apiContract#parameter": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/apiContract#Parameter",
      |                          "http://a.ml/vocabularies/document#DomainElement"
      |                        ],
      |                        "http://a.ml/vocabularies/core#name": [
      |                          {
      |                            "@value": "a"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#paramName": [
      |                          {
      |                            "@value": "a"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#required": [
      |                          {
      |                            "@value": true
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#binding": [
      |                          {
      |                            "@value": "query"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/shapes#schema": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/shapes#ScalarShape",
      |                              "http://a.ml/vocabularies/shapes#AnyShape",
      |                              "http://www.w3.org/ns/shacl#Shape",
      |                              "http://a.ml/vocabularies/shapes#Shape",
      |                              "http://a.ml/vocabularies/document#DomainElement"
      |                            ],
      |                            "http://www.w3.org/ns/shacl#datatype": [
      |                              {
      |                                "@id": "http://www.w3.org/2001/XMLSchema#string"
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#name": [
      |                              {
      |                                "@value": "schema"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#sources": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema/source-map",
      |                                "@type": [
      |                                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#synthesized-field": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema/source-map/synthesized-field/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "true"
      |                                      }
      |                                    ]
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema/source-map/lexical/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(8,6)-(9,0)]"
      |                                      }
      |                                    ]
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#sources": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/source-map",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/source-map/lexical/element_1",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(8,6)-(9,0)]"
      |                                  }
      |                                ]
      |                              },
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/parameter/query/a/source-map/lexical/element_0",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "http://a.ml/vocabularies/shapes#schema"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(8,6)-(9,0)]"
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/apiContract#payload": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/apiContract#Payload",
      |                          "http://a.ml/vocabularies/document#DomainElement"
      |                        ],
      |                        "http://a.ml/vocabularies/core#mediaType": [
      |                          {
      |                            "@value": "application/json"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/shapes#schema": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema",
      |                            "@type": [
      |                              "http://www.w3.org/ns/shacl#NodeShape",
      |                              "http://a.ml/vocabularies/shapes#AnyShape",
      |                              "http://www.w3.org/ns/shacl#Shape",
      |                              "http://a.ml/vocabularies/shapes#Shape",
      |                              "http://a.ml/vocabularies/document#DomainElement"
      |                            ],
      |                            "http://www.w3.org/ns/shacl#closed": [
      |                              {
      |                                "@value": false
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#property": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b",
      |                                "@type": [
      |                                  "http://www.w3.org/ns/shacl#PropertyShape",
      |                                  "http://www.w3.org/ns/shacl#Shape",
      |                                  "http://a.ml/vocabularies/shapes#Shape",
      |                                  "http://a.ml/vocabularies/document#DomainElement"
      |                                ],
      |                                "http://www.w3.org/ns/shacl#path": [
      |                                  {
      |                                    "@id": "http://a.ml/vocabularies/data#b"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/shapes#range": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b",
      |                                    "@type": [
      |                                      "http://a.ml/vocabularies/shapes#ScalarShape",
      |                                      "http://a.ml/vocabularies/shapes#AnyShape",
      |                                      "http://www.w3.org/ns/shacl#Shape",
      |                                      "http://a.ml/vocabularies/shapes#Shape",
      |                                      "http://a.ml/vocabularies/document#DomainElement"
      |                                    ],
      |                                    "http://www.w3.org/ns/shacl#datatype": [
      |                                      {
      |                                        "@id": "http://www.w3.org/2001/XMLSchema#string"
      |                                      }
      |                                    ],
      |                                    "http://www.w3.org/ns/shacl#name": [
      |                                      {
      |                                        "@value": "b"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#sources": [
      |                                      {
      |                                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b/source-map",
      |                                        "@type": [
      |                                          "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                        ],
      |                                        "http://a.ml/vocabularies/document-source-maps#synthesized-field": [
      |                                          {
      |                                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b/source-map/synthesized-field/element_0",
      |                                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                                              {
      |                                                "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b"
      |                                              }
      |                                            ],
      |                                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                                              {
      |                                                "@value": "true"
      |                                              }
      |                                            ]
      |                                          }
      |                                        ],
      |                                        "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                          {
      |                                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b/source-map/lexical/element_0",
      |                                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                                              {
      |                                                "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/scalar/b"
      |                                              }
      |                                            ],
      |                                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                                              {
      |                                                "@value": "[(12,10)-(13,0)]"
      |                                              }
      |                                            ]
      |                                          }
      |                                        ]
      |                                      }
      |                                    ]
      |                                  }
      |                                ],
      |                                "http://www.w3.org/ns/shacl#minCount": [
      |                                  {
      |                                    "@value": 1
      |                                  }
      |                                ],
      |                                "http://www.w3.org/ns/shacl#name": [
      |                                  {
      |                                    "@value": "b"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#sources": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/source-map",
      |                                    "@type": [
      |                                      "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                      {
      |                                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b/source-map/lexical/element_0",
      |                                        "http://a.ml/vocabularies/document-source-maps#element": [
      |                                          {
      |                                            "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/property/b"
      |                                          }
      |                                        ],
      |                                        "http://a.ml/vocabularies/document-source-maps#value": [
      |                                          {
      |                                            "@value": "[(12,10)-(13,0)]"
      |                                          }
      |                                        ]
      |                                      }
      |                                    ]
      |                                  }
      |                                ]
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#name": [
      |                              {
      |                                "@value": "schema"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#sources": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/source-map",
      |                                "@type": [
      |                                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#auto-generated-name": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/source-map/auto-generated-name/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": ""
      |                                      }
      |                                    ]
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/source-map/lexical/element_1",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(10,6)-(13,0)]"
      |                                      }
      |                                    ]
      |                                  },
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/schema/source-map/lexical/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "http://www.w3.org/ns/shacl#property"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(11,8)-(13,0)]"
      |                                      }
      |                                    ]
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#sources": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/source-map",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson/source-map/lexical/element_0",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/application%2Fjson"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(10,6)-(13,0)]"
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#sources": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/source-map",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/source-map/lexical/element_2",
      |                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                              {
      |                                "@value": "http://a.ml/vocabularies/apiContract#parameter"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                              {
      |                                "@value": "[(7,4)-(9,0)]"
      |                              }
      |                            ]
      |                          },
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/source-map/lexical/element_0",
      |                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                              {
      |                                "@value": "http://a.ml/vocabularies/apiContract#payload"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                              {
      |                                "@value": "[(9,4)-(13,0)]"
      |                              }
      |                            ]
      |                          },
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request/source-map/lexical/element_1",
      |                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                              {
      |                                "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/request"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                              {
      |                                "@value": "[(7,0)-(13,0)]"
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      }
      |                    ]
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#sources": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/source-map",
      |                    "@type": [
      |                      "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#synthesized-field": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/source-map/synthesized-field/element_0",
      |                        "http://a.ml/vocabularies/document-source-maps#element": [
      |                          {
      |                            "@value": "http://a.ml/vocabularies/apiContract#expects"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#value": [
      |                          {
      |                            "@value": "true"
      |                          }
      |                        ]
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get/source-map/lexical/element_0",
      |                        "http://a.ml/vocabularies/document-source-maps#element": [
      |                          {
      |                            "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/get"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#value": [
      |                          {
      |                            "@value": "[(6,2)-(13,0)]"
      |                          }
      |                        ]
      |                      }
      |                    ]
      |                  }
      |                ]
      |              }
      |            ],
      |            "http://a.ml/vocabularies/document-source-maps#sources": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/source-map",
      |                "@type": [
      |                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1/source-map/lexical/element_0",
      |                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                      {
      |                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint1"
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                      {
      |                        "@value": "[(5,0)-(13,0)]"
      |                      }
      |                    ]
      |                  }
      |                ]
      |              }
      |            ]
      |          },
      |          {
      |            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2",
      |            "@type": [
      |              "http://a.ml/vocabularies/apiContract#EndPoint",
      |              "http://a.ml/vocabularies/document#DomainElement"
      |            ],
      |            "http://a.ml/vocabularies/apiContract#path": [
      |              {
      |                "@value": "/endpoint2"
      |              }
      |            ],
      |            "http://a.ml/vocabularies/apiContract#supportedOperation": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get",
      |                "@type": [
      |                  "http://a.ml/vocabularies/apiContract#Operation",
      |                  "http://a.ml/vocabularies/document#DomainElement"
      |                ],
      |                "http://a.ml/vocabularies/apiContract#method": [
      |                  {
      |                    "@value": "get"
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/apiContract#expects": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request",
      |                    "@type": [
      |                      "http://a.ml/vocabularies/apiContract#Request",
      |                      "http://a.ml/vocabularies/apiContract#Message",
      |                      "http://a.ml/vocabularies/document#DomainElement"
      |                    ],
      |                    "http://a.ml/vocabularies/apiContract#parameter": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/apiContract#Parameter",
      |                          "http://a.ml/vocabularies/document#DomainElement"
      |                        ],
      |                        "http://a.ml/vocabularies/core#name": [
      |                          {
      |                            "@value": "c"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#paramName": [
      |                          {
      |                            "@value": "c"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#required": [
      |                          {
      |                            "@value": true
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#binding": [
      |                          {
      |                            "@value": "query"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/shapes#schema": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/shapes#ScalarShape",
      |                              "http://a.ml/vocabularies/shapes#AnyShape",
      |                              "http://www.w3.org/ns/shacl#Shape",
      |                              "http://a.ml/vocabularies/shapes#Shape",
      |                              "http://a.ml/vocabularies/document#DomainElement"
      |                            ],
      |                            "http://www.w3.org/ns/shacl#datatype": [
      |                              {
      |                                "@id": "http://www.w3.org/2001/XMLSchema#string"
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#minLength": [
      |                              {
      |                                "@value": 10
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#name": [
      |                              {
      |                                "@value": "schema"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#sources": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema/source-map",
      |                                "@type": [
      |                                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#default-node": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema/source-map/default-node/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": ""
      |                                      }
      |                                    ]
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema/source-map/lexical/element_1",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(16,6)-(18,0)]"
      |                                      }
      |                                    ]
      |                                  },
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/scalar/schema/source-map/lexical/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "http://www.w3.org/ns/shacl#minLength"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(17,8)-(18,0)]"
      |                                      }
      |                                    ]
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#sources": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/source-map",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/source-map/lexical/element_1",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(16,6)-(18,0)]"
      |                                  }
      |                                ]
      |                              },
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/c/source-map/lexical/element_0",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "http://a.ml/vocabularies/shapes#schema"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(16,6)-(18,0)]"
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      },
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/apiContract#Parameter",
      |                          "http://a.ml/vocabularies/document#DomainElement"
      |                        ],
      |                        "http://a.ml/vocabularies/core#name": [
      |                          {
      |                            "@value": "d"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#paramName": [
      |                          {
      |                            "@value": "d"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#required": [
      |                          {
      |                            "@value": true
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/apiContract#binding": [
      |                          {
      |                            "@value": "query"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/shapes#schema": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/shapes#ScalarShape",
      |                              "http://a.ml/vocabularies/shapes#AnyShape",
      |                              "http://www.w3.org/ns/shacl#Shape",
      |                              "http://a.ml/vocabularies/shapes#Shape",
      |                              "http://a.ml/vocabularies/document#DomainElement"
      |                            ],
      |                            "http://www.w3.org/ns/shacl#datatype": [
      |                              {
      |                                "@id": "http://www.w3.org/2001/XMLSchema#string"
      |                              }
      |                            ],
      |                            "http://www.w3.org/ns/shacl#name": [
      |                              {
      |                                "@value": "schema"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#sources": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema/source-map",
      |                                "@type": [
      |                                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#synthesized-field": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema/source-map/synthesized-field/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "true"
      |                                      }
      |                                    ]
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                                  {
      |                                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema/source-map/lexical/element_0",
      |                                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                                      {
      |                                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/scalar/schema"
      |                                      }
      |                                    ],
      |                                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                                      {
      |                                        "@value": "[(18,6)-(18,8)]"
      |                                      }
      |                                    ]
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#sources": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/source-map",
      |                            "@type": [
      |                              "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/source-map/lexical/element_1",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(18,6)-(18,8)]"
      |                                  }
      |                                ]
      |                              },
      |                              {
      |                                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/parameter/query/d/source-map/lexical/element_0",
      |                                "http://a.ml/vocabularies/document-source-maps#element": [
      |                                  {
      |                                    "@value": "http://a.ml/vocabularies/shapes#schema"
      |                                  }
      |                                ],
      |                                "http://a.ml/vocabularies/document-source-maps#value": [
      |                                  {
      |                                    "@value": "[(18,6)-(18,8)]"
      |                                  }
      |                                ]
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#sources": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/source-map",
      |                        "@type": [
      |                          "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/source-map/lexical/element_1",
      |                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                              {
      |                                "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                              {
      |                                "@value": "[(15,0)-(18,8)]"
      |                              }
      |                            ]
      |                          },
      |                          {
      |                            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/request/source-map/lexical/element_0",
      |                            "http://a.ml/vocabularies/document-source-maps#element": [
      |                              {
      |                                "@value": "http://a.ml/vocabularies/apiContract#parameter"
      |                              }
      |                            ],
      |                            "http://a.ml/vocabularies/document-source-maps#value": [
      |                              {
      |                                "@value": "[(15,4)-(18,8)]"
      |                              }
      |                            ]
      |                          }
      |                        ]
      |                      }
      |                    ]
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#sources": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/source-map",
      |                    "@type": [
      |                      "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#synthesized-field": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/source-map/synthesized-field/element_0",
      |                        "http://a.ml/vocabularies/document-source-maps#element": [
      |                          {
      |                            "@value": "http://a.ml/vocabularies/apiContract#expects"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#value": [
      |                          {
      |                            "@value": "true"
      |                          }
      |                        ]
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                      {
      |                        "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get/source-map/lexical/element_0",
      |                        "http://a.ml/vocabularies/document-source-maps#element": [
      |                          {
      |                            "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/get"
      |                          }
      |                        ],
      |                        "http://a.ml/vocabularies/document-source-maps#value": [
      |                          {
      |                            "@value": "[(14,2)-(18,8)]"
      |                          }
      |                        ]
      |                      }
      |                    ]
      |                  }
      |                ]
      |              }
      |            ],
      |            "http://a.ml/vocabularies/document-source-maps#sources": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/source-map",
      |                "@type": [
      |                  "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#lexical": [
      |                  {
      |                    "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2/source-map/lexical/element_0",
      |                    "http://a.ml/vocabularies/document-source-maps#element": [
      |                      {
      |                        "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api/end-points/%2Fendpoint2"
      |                      }
      |                    ],
      |                    "http://a.ml/vocabularies/document-source-maps#value": [
      |                      {
      |                        "@value": "[(13,0)-(18,8)]"
      |                      }
      |                    ]
      |                  }
      |                ]
      |              }
      |            ]
      |          }
      |        ],
      |        "http://a.ml/vocabularies/document-source-maps#sources": [
      |          {
      |            "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/source-map",
      |            "@type": [
      |              "http://a.ml/vocabularies/document-source-maps#SourceMap"
      |            ],
      |            "http://a.ml/vocabularies/document-source-maps#source-vendor": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/source-map/source-vendor/element_0",
      |                "http://a.ml/vocabularies/document-source-maps#element": [
      |                  {
      |                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api"
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#value": [
      |                  {
      |                    "@value": "RAML 1.0"
      |                  }
      |                ]
      |              }
      |            ],
      |            "http://a.ml/vocabularies/document-source-maps#lexical": [
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/source-map/lexical/element_1",
      |                "http://a.ml/vocabularies/document-source-maps#element": [
      |                  {
      |                    "@value": "file://test/data/integration/profile1/negative.data.raml#/web-api"
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#value": [
      |                  {
      |                    "@value": "[(3,0)-(18,8)]"
      |                  }
      |                ]
      |              },
      |              {
      |                "@id": "file://test/data/integration/profile1/negative.data.raml#/web-api/source-map/lexical/element_0",
      |                "http://a.ml/vocabularies/document-source-maps#element": [
      |                  {
      |                    "@value": "http://a.ml/vocabularies/core#name"
      |                  }
      |                ],
      |                "http://a.ml/vocabularies/document-source-maps#value": [
      |                  {
      |                    "@value": "[(3,0)-(5,0)]"
      |                  }
      |                ]
      |              }
      |            ]
      |          }
      |        ]
      |      }
      |    ],
      |    "http://a.ml/vocabularies/document#version": [
      |      {
      |        "@value": "3.0.0"
      |      }
      |    ],
      |    "http://a.ml/vocabularies/document#root": [
      |      {
      |        "@value": true
      |      }
      |    ]
      |  }
      |]
      |""".stripMargin
}