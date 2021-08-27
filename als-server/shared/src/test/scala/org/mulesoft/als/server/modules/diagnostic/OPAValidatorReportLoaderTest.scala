package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.position.Position
import org.mulesoft.als.server.modules.diagnostic.custom.OPAValidatorReportLoader
import org.scalatest.FunSuite

import concurrent.ExecutionContext.Implicits.global
class OPAValidatorReportLoaderTest extends FunSuite {

  val example =
    """{
      |  "@context": {
      |    "actual": {
      |      "@id": "http://a.ml/vocabularies/validation#actual"
      |    },
      |    "argument": {
      |      "@id": "http://a.ml/vocabularies/validation#argument"
      |    },
      |    "column": {
      |      "@id": "http://a.ml/vocabularies/lexical#column"
      |    },
      |    "component": {
      |      "@id": "http://a.ml/vocabularies/validation#component"
      |    },
      |    "condition": {
      |      "@id": "http://a.ml/vocabularies/validation#condition"
      |    },
      |    "conforms": {
      |      "@id": "http://www.w3.org/ns/shacl#conforms"
      |    },
      |    "end": {
      |      "@id": "http://a.ml/vocabularies/lexical#end"
      |    },
      |    "expected": {
      |      "@id": "http://a.ml/vocabularies/validation#expected"
      |    },
      |    "focusNode": {
      |      "@id": "http://www.w3.org/ns/shacl#focusNode"
      |    },
      |    "lexical": "http://a.ml/vocabularies/lexical#",
      |    "line": {
      |      "@id": "http://a.ml/vocabularies/lexical#line"
      |    },
      |    "location": {
      |      "@id": "http://a.ml/vocabularies/validation#location"
      |    },
      |    "negated": {
      |      "@id": "http://a.ml/vocabularies/validation#negated"
      |    },
      |    "range": {
      |      "@id": "http://a.ml/vocabularies/lexical#range"
      |    },
      |    "result": {
      |      "@id": "http://www.w3.org/ns/shacl#result"
      |    },
      |    "resultMessage": {
      |      "@id": "http://www.w3.org/ns/shacl#resultMessage"
      |    },
      |    "resultPath": {
      |      "@id": "http://www.w3.org/ns/shacl#resultPath"
      |    },
      |    "resultSeverity": {
      |      "@id": "http://www.w3.org/ns/shacl#resultSeverity"
      |    },
      |    "shacl": "http://www.w3.org/ns/shacl#",
      |    "sourceShapeName": {
      |      "@id": "http://a.ml/vocabularies/validation#sourceShapeName"
      |    },
      |    "start": {
      |      "@id": "http://a.ml/vocabularies/lexical#start"
      |    },
      |    "subResult": {
      |      "@id": "http://a.ml/vocabularies/validation#subResult"
      |    },
      |    "trace": {
      |      "@id": "http://a.ml/vocabularies/validation#trace"
      |    },
      |    "traceValue": {
      |      "@id": "http://www.w3.org/ns/shacl#traceValue"
      |    },
      |    "uri": {
      |      "@id": "http://a.ml/vocabularies/lexical#uri"
      |    },
      |    "validation": "http://a.ml/vocabularies/validation#"
      |  },
      |  "@type": "shacl:ValidationReport",
      |  "conforms": false,
      |  "result": [
      |    {
      |      "@type": [
      |        "shacl:ValidationResult"
      |      ],
      |      "focusNode": {
      |        "@id": "amf://id#4"
      |      },
      |      "resultMessage": "Scalars in parameters must have minLength defined",
      |      "resultSeverity": {
      |        "@id": "http://www.w3.org/ns/shacl#Violation"
      |      },
      |      "sourceShapeName": "validation1",
      |      "trace": [
      |        {
      |          "@type": [
      |            "validation:TraceMessage"
      |          ],
      |          "component": "nested",
      |          "resultPath": "apiContract.parameter / shapes.schema",
      |          "traceValue": {
      |            "actual": 1,
      |            "expected": 0,
      |            "negated": false,
      |            "subResult": [
      |              {
      |                "@type": [
      |                  "shacl:ValidationResult"
      |                ],
      |                "focusNode": {
      |                  "@id": "amf://id#6"
      |                },
      |                "resultMessage": "error in nested nodes under apiContract.parameter / shapes.schema",
      |                "sourceShapeName": "nested",
      |                "trace": [
      |                  {
      |                    "@type": [
      |                      "validation:TraceMessage"
      |                    ],
      |                    "component": "minCount",
      |                    "location": {
      |                      "@type": [
      |                        "lexical:Location"
      |                      ],
      |                      "range": {
      |                        "@type": [
      |                          "lexical:Range"
      |                        ],
      |                        "end": {
      |                          "@type": [
      |                            "lexical:Position"
      |                          ],
      |                          "column": 19,
      |                          "line": 7
      |                        },
      |                        "start": {
      |                          "@type": [
      |                            "lexical:Position"
      |                          ],
      |                          "column": 6,
      |                          "line": 6
      |                        }
      |                      },
      |                      "uri": ""
      |                    },
      |                    "resultPath": "shacl.minLength",
      |                    "traceValue": {
      |                      "actual": 0,
      |                      "condition": ">=",
      |                      "expected": 1,
      |                      "negated": false
      |                    }
      |                  }
      |                ]
      |              }
      |            ]
      |          }
      |        }
      |      ]
      |    }
      |  ]
      |}""".stripMargin
  test("Simple OPA Validation report") {
    val report =
      for {
        r <- new OPAValidatorReportLoader().load(example)
      } yield {
        assert(r.results.head.targetNode == "amf://id#4")
        assert(r.results.head.position.isDefined)
        assert(r.results.head.message == "Scalars in parameters must have minLength defined")
        assert(r.results.head.position.get.range.start == Position(5, 6))
        assert(r.results.head.position.get.range.end == Position(6, 19))
      }
  }

  val example2 =
    """{
      |  "@context": {
      |    "actual": {
      |      "@id": "http://a.ml/vocabularies/validation#actual"
      |    },
      |    "argument": {
      |      "@id": "http://a.ml/vocabularies/validation#argument"
      |    },
      |    "column": {
      |      "@id": "http://a.ml/vocabularies/lexical#column"
      |    },
      |    "component": {
      |      "@id": "http://a.ml/vocabularies/validation#component"
      |    },
      |    "condition": {
      |      "@id": "http://a.ml/vocabularies/validation#condition"
      |    },
      |    "conforms": {
      |      "@id": "http://www.w3.org/ns/shacl#conforms"
      |    },
      |    "end": {
      |      "@id": "http://a.ml/vocabularies/lexical#end"
      |    },
      |    "expected": {
      |      "@id": "http://a.ml/vocabularies/validation#expected"
      |    },
      |    "focusNode": {
      |      "@id": "http://www.w3.org/ns/shacl#focusNode"
      |    },
      |    "lexical": "http://a.ml/vocabularies/lexical#",
      |    "line": {
      |      "@id": "http://a.ml/vocabularies/lexical#line"
      |    },
      |    "location": {
      |      "@id": "http://a.ml/vocabularies/validation#location"
      |    },
      |    "negated": {
      |      "@id": "http://a.ml/vocabularies/validation#negated"
      |    },
      |    "range": {
      |      "@id": "http://a.ml/vocabularies/lexical#range"
      |    },
      |    "result": {
      |      "@id": "http://www.w3.org/ns/shacl#result"
      |    },
      |    "resultMessage": {
      |      "@id": "http://www.w3.org/ns/shacl#resultMessage"
      |    },
      |    "resultPath": {
      |      "@id": "http://www.w3.org/ns/shacl#resultPath"
      |    },
      |    "resultSeverity": {
      |      "@id": "http://www.w3.org/ns/shacl#resultSeverity"
      |    },
      |    "shacl": "http://www.w3.org/ns/shacl#",
      |    "sourceShapeName": {
      |      "@id": "http://a.ml/vocabularies/validation#sourceShapeName"
      |    },
      |    "start": {
      |      "@id": "http://a.ml/vocabularies/lexical#start"
      |    },
      |    "subResult": {
      |      "@id": "http://a.ml/vocabularies/validation#subResult"
      |    },
      |    "trace": {
      |      "@id": "http://a.ml/vocabularies/validation#trace"
      |    },
      |    "traceValue": {
      |      "@id": "http://www.w3.org/ns/shacl#traceValue"
      |    },
      |    "uri": {
      |      "@id": "http://a.ml/vocabularies/lexical#uri"
      |    },
      |    "validation": "http://a.ml/vocabularies/validation#"
      |  },
      |  "@type": "shacl:ValidationReport",
      |  "conforms": false,
      |  "result": [
      |    {
      |      "@type": [
      |        "shacl:ValidationResult"
      |      ],
      |      "focusNode": {
      |        "@id": "amf://id#4"
      |      },
      |      "resultMessage": "Scalars in parameters must have minLength defined",
      |      "resultSeverity": {
      |        "@id": "http://www.w3.org/ns/shacl#Violation"
      |      },
      |      "sourceShapeName": "scalar-parameters",
      |      "trace": [
      |        {
      |          "@type": [
      |            "validation:TraceMessage"
      |          ],
      |          "component": "nested",
      |          "resultPath": "apiContract.parameter",
      |          "traceValue": {
      |            "actual": 1,
      |            "expected": 0,
      |            "negated": false,
      |            "subResult": [
      |              {
      |                "@type": [
      |                  "shacl:ValidationResult"
      |                ],
      |                "focusNode": {
      |                  "@id": "amf://id#5"
      |                },
      |                "resultMessage": "error in nested nodes under apiContract.parameter",
      |                "sourceShapeName": "nested",
      |                "trace": [
      |                  {
      |                    "@type": [
      |                      "validation:TraceMessage"
      |                    ],
      |                    "component": "nested",
      |                    "location": {
      |                      "@type": [
      |                        "lexical:Location"
      |                      ],
      |                      "range": {
      |                        "@type": [
      |                          "lexical:Range"
      |                        ],
      |                        "end": {
      |                          "@type": [
      |                            "lexical:Position"
      |                          ],
      |                          "column": 0,
      |                          "line": 9
      |                        },
      |                        "start": {
      |                          "@type": [
      |                            "lexical:Position"
      |                          ],
      |                          "column": 6,
      |                          "line": 8
      |                        }
      |                      },
      |                      "uri": ""
      |                    },
      |                    "resultPath": "shapes.schema",
      |                    "traceValue": {
      |                      "actual": 1,
      |                      "expected": 0,
      |                      "negated": false,
      |                      "subResult": [
      |                        {
      |                          "@type": [
      |                            "shacl:ValidationResult"
      |                          ],
      |                          "focusNode": {
      |                            "@id": "amf://id#6"
      |                          },
      |                          "resultMessage": "error in nested nodes under shapes.schema",
      |                          "sourceShapeName": "nested",
      |                          "trace": [
      |                            {
      |                              "@type": [
      |                                "validation:TraceMessage"
      |                              ],
      |                              "component": "minCount",
      |                              "location": {
      |                                "@type": [
      |                                  "lexical:Location"
      |                                ],
      |                                "range": {
      |                                  "@type": [
      |                                    "lexical:Range"
      |                                  ],
      |                                  "end": {
      |                                    "@type": [
      |                                      "lexical:Position"
      |                                    ],
      |                                    "column": 0,
      |                                    "line": 9
      |                                  },
      |                                  "start": {
      |                                    "@type": [
      |                                      "lexical:Position"
      |                                    ],
      |                                    "column": 6,
      |                                    "line": 8
      |                                  }
      |                                },
      |                                "uri": ""
      |                              },
      |                              "resultPath": "shacl.minLength",
      |                              "traceValue": {
      |                                "actual": 0,
      |                                "condition": ">=",
      |                                "expected": 1,
      |                                "negated": false
      |                              }
      |                            }
      |                          ]
      |                        }
      |                      ]
      |                    }
      |                  }
      |                ]
      |              }
      |            ]
      |          }
      |        }
      |      ]
      |    }
      |  ]
      |}
      |""".stripMargin

  test("Get lexical from nested validation report") {
    val report =
      for {
        r <- new OPAValidatorReportLoader().load(example2)
      } yield {
        assert(r.results.head.targetNode == "amf://id#4")
        assert(r.results.head.position.isDefined)
        assert(r.results.head.message == "Scalars in parameters must have minLength defined")
        assert(r.results.head.position.get.range.start == Position(7, 6))
        assert(r.results.head.position.get.range.end == Position(8, 0))
      }
  }
}
