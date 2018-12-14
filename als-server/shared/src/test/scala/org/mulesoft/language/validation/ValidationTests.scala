//package org.mulesoft.language.validation
//
//import org.mulesoft.language.server.test.parsertests.util.UtilIndex
//
//object ValidationTests {
//
//  var describe = zeroOfMyType
//  var it = zeroOfMyType
//  var require = zeroOfMyType
//  beforeEach((() => {
//    util.sleep(100)
//
//  }))
//  describe("Parser integration tests", (() => {
//    this.timeout(30000)
//    it("Instagram", (done => {
//      util.testErrors(done, util.data("../example-ramls/Instagram/api.raml"), Array("Content is not valid according to schema: Expected type string but found type null", "Content is not valid according to schema: Expected type string but found type null", "Content is not valid according to schema: Expected type object but found type null", "Content is not valid according to schema: Expected type object but found type null"))
//
//    }))
//    it("Omni", (done => {
//      util.testErrors(done, util.data("../example-ramls/omni/api.raml"))
//
//    }))
//    it("Omni 0.8", (done => {
//      util.testErrors(done, util.data("../example-ramls/omni08/api.raml"))
//
//    }))
//    it("Cosmetics Overlay", (done => {
//      util.testErrors(done, util.data("../example-ramls/cosmetics/hypermedia.raml"))
//
//    }))
//    it("Cosmetics Extension", (done => {
//      util.testErrors(done, util.data("../example-ramls/cosmetics/hypermedia1.raml"))
//
//    }))
//    it("Exchange", (done => {
//      util.testErrors(done, util.data("../example-ramls/exchange/api.raml"))
//
//    }))
//    it("core services", (done => {
//      util.testErrors(done, util.data("../example-ramls/core-services/api.raml"), Array("Can not parse JSON example: Unexpected token '{'", "Can not parse JSON example: Cannot tokenize symbol 'O'", "Can not parse JSON example: Cannot tokenize symbol 'O'"))
//
//    }))
//    it("cloudhub new logging", (done => {
//      util.testErrors(done, util.data("../example-ramls/cloudhub-new-logging/api.raml"), Array("Can not parse JSON example: Unexpected token '}'"))
//
//    }))
//    it("audit logging", (done => {
//      util.testErrors(done, util.data("../example-ramls/audit-logging-query/api.raml"))
//
//    }))
//    it("application monitoring", (done => {
//      util.testErrors(done, util.data("../example-ramls/application-monitoring/api.raml"), Array("Unrecognized schema: 'appmonitor'"))
//
//    }))
//    it("lib1", (done => {
//      util.testErrors(done, util.data("../example-ramls/blog-users1/blog-users.raml"))
//
//    }))
//    it("lib2", (done => {
//      util.testErrorsByNumber(done, util.data("../example-ramls/blog-users2/blog-users.raml"), 2, 1)
//
//    }))
//    it("lib3", (done => {
//      util.testErrorsByNumber(done, util.data("../example-ramls/blog-users2/blog-users.raml"), 2, 1)
//
//    }))
//    it("platform2", (done => {
//      util.testErrors(done, util.data("../example-ramls/platform2/api.raml"), Array(), true)
//
//    }))
//
//  }))
//  describe("https connection tests", (() => {
//    this.timeout(30000)
//    it("https 0.8", (done => {
//      util.testErrors(done, util.data("parser/https/tr1.raml"))
//
//    }))
//    it("https 1.0", (done => {
//      util.testErrors(done, util.data("parser/https/tr2.raml"))
//
//    }))
//
//  }))
//  describe("Transformers tests", (() => {
//    this.timeout(30000)
//    it("All transformer from spec should be valid.", (done => {
//      util.testErrors(done, util.data("parser/transformers/t1.raml"), Array("Unknown function applied to parameter: '!\\w+'"))
//
//    }))
//
//  }))
//  describe("Security Schemes tests", (() => {
//    this.timeout(30000)
//    it("should fail if not all required settings specified", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss1/securityScheme.raml"), Array("Missing required property '\\w+'"))
//
//    }))
//    it("should pass when extra non-required settings specified", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss2/securityScheme.raml"))
//
//    }))
//    it("should pass when settings contains array property(0.8)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss3/securityScheme.raml"))
//
//    }))
//    it("should fail when settings contains duplicate required properties(0.8)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss4/securityScheme.raml"), Array("property already used: 'accessTokenUri'", "property already used: 'accessTokenUri'"))
//
//    }))
//    it("should fail when settings contains duplicate required array properties(0.8)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss5/securityScheme.raml"), Array("property already used: 'authorizationGrants'", "property already used: 'authorizationGrants'", "property already used: 'authorizationGrants'"))
//
//    }))
//    it("should fail when settings contains duplicate non-required properties(0.8)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss6/securityScheme.raml"), Array("property already used: 'aaa'", "property already used: 'aaa'"))
//
//    }))
//    it("should pass when settings contains required array property(1.0)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss7/securityScheme.raml"))
//
//    }))
//    it("should fail when settings contains duplicate required properties(1.0)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss8/securityScheme.raml"), Array("property already used: 'accessTokenUri'", "property already used: 'accessTokenUri'"))
//
//    }))
//    it("should fail when settings contains duplicate required array properties(1.0)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss9/securityScheme.raml"), Array("property already used: 'authorizationGrants'", "property already used: 'authorizationGrants'"))
//
//    }))
//    it("null-value security schema should be allowed for Api(0.8)", (done => {
//      util.testErrors(done, util.data("parser/securitySchemes/ss10/securityScheme.raml"))
//
//    }))
//    it("grant type validation", (done => {
//      util.testErrors(done, util.data("parser/custom/oath2.raml"), Array("'authorizationGrants' value should be one of 'authorization_code', 'implicit', 'password', 'client_credentials' or to be an abolute URI"))
//
//    }))
//    it("security scheme should be a seq in 0.8", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/shemeShouldBeASeq.raml"), 1)
//
//    }))
//
//  }))
//  describe("Parser regression tests", (() => {
//    this.timeout(30000)
//    it("basic type expression cases should pass validation", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/basic.raml"))
//
//    }))
//    it("inplace types", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/inplace.raml"))
//
//    }))
//    it("template vars0", (done => {
//      util.testErrors(done, util.data("parser/templates/unknown.raml"))
//
//    }))
//    it("example validation", (done => {
//      util.testErrors(done, util.data("parser/examples/ex1.raml"), Array("Can not parse JSON example: Unexpected token d"))
//
//    }))
//    it("example validation json against schema", (done => {
//      util.testErrors(done, util.data("parser/examples/ex2.raml"), Array("Content is not valid according to schema"))
//
//    }))
//    it("example validation yaml against schema", (done => {
//      util.testErrors(done, util.data("parser/examples/ex3.raml"), Array("Content is not valid according to schema: Additional properties not allowed: vlue"))
//
//    }))
//    it("example validation yaml against basic type", (done => {
//      util.testErrors(done, util.data("parser/examples/ex4.raml"), Array("Required property 'c' is missing"))
//
//    }))
//    it("example validation yaml against inherited type", (done => {
//      util.testErrors(done, util.data("parser/examples/ex5.raml"), Array("Required property 'c' is missing"))
//
//    }))
//    it("example validation yaml against array", (done => {
//      util.testErrors(done, util.data("parser/examples/ex6.raml"), Array("Required property 'c' is missing"))
//
//    }))
//    it("example in model", (done => {
//      util.testErrors(done, util.data("parser/examples/ex7.raml"), Array("Expected type 'string' but got 'number'", "Expected type 'string' but got 'number'", "Required property 'c' is missing"))
//
//    }))
//    it("another kind of examples", (done => {
//      util.testErrors(done, util.data("parser/examples/ex13.raml"), Array("Expected type 'boolean' but got 'number'"))
//
//    }))
//    it("example in parameter", (done => {
//      util.testErrors(done, util.data("parser/examples/ex8.raml"), Array("Expected type 'boolean' but got 'string'"))
//
//    }))
//    it("checking that node is actually primitive", (done => {
//      util.testErrors(done, util.data("parser/examples/ex9.raml"), Array("Expected type 'string' but got 'object'"))
//
//    }))
//    it("map", (done => {
//      util.testErrors(done, util.data("parser/examples/ex10.raml"))
//
//    }))
//    it("map1", (done => {
//      util.testErrors(done, util.data("parser/examples/ex11.raml"), Array("Expected type 'number' but got 'string'"))
//
//    }))
//    it("map2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex12.raml"), Array("Expected type 'number' but got 'string'"))
//
//    }))
//    it("objects are closed", (done => {
//      util.testErrors(done, util.data("parser/examples/ex14.raml"), Array("Unknown property: 'z'"))
//
//    }))
//    it("enums restriction", (done => {
//      util.testErrors(done, util.data("parser/examples/ex15.raml"), Array("value should be one of: 'val1', 'val2', '3'"))
//
//    }))
//    it("array facets", (done => {
//      util.testErrors(done, util.data("parser/examples/ex16.raml"), Array("'Person.items.minItems=5' i.e. array items count should not be less than 5", "'Person.items2.maxItems=3' i.e. array items count should not be more than 3", "items should be unique"))
//
//    }))
//    it("array facets2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex17.raml"))
//
//    }))
//    it("array facets3", (done => {
//      util.testErrors(done, util.data("parser/examples/ex18.raml"), Array("'SmallArray.minItems=5' i.e. array items count should not be less than 5"))
//
//    }))
//    it("array facets4", (done => {
//      util.testErrors(done, util.data("parser/examples/ex19.raml"), Array("'SmallArray.minItems=5' i.e. array items count should not be less than 5"))
//
//    }))
//    it("object facets1", (done => {
//      util.testErrors(done, util.data("parser/examples/ex20.raml"), Array("'MyType.minProperties=2' i.e. object properties count should not be less than 2"))
//
//    }))
//    it("object facets2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex21.raml"), Array("'MyType1.minProperties=2' i.e. object properties count should not be less than 2"))
//
//    }))
//    it("object facets3", (done => {
//      util.testErrors(done, util.data("parser/examples/ex22.raml"), Array("'MyType1.maxProperties=1' i.e. object properties count should not be more than 1"))
//
//    }))
//    it("object facets4", (done => {
//      util.testErrors(done, util.data("parser/examples/ex23.raml"))
//
//    }))
//    it("object facets5", (done => {
//      util.testErrors(done, util.data("parser/examples/ex24.raml"), Array("'MyType1.minProperties=3' i.e. object properties count should not be less than 3"))
//
//    }))
//    it("string facets1", (done => {
//      util.testErrors(done, util.data("parser/examples/ex25.raml"), Array("'MyType1.minLength=5' i.e. string length should not be less than 5", "'MyType2.maxLength=3' i.e. string length should not be more than 3"))
//
//    }))
//    it("string facets2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex26.raml"))
//
//    }))
//    it("string facets3", (done => {
//      util.testErrors(done, util.data("parser/examples/ex27.raml"), Array("'MyType1.minLength=5' i.e. string length should not be less than 5"))
//
//    }))
//    it("string facets4", (done => {
//      util.testErrors(done, util.data("parser/examples/ex28.raml"), Array("string should match to '\\.5'"))
//
//    }))
//    it("number facets1", (done => {
//      util.testErrors(done, util.data("parser/examples/ex29.raml"), Array("'MyType1.minimum=5' i.e. value should not be less than 5", "'MyType1.minimum=5' i.e. value should not be less than 5"))
//
//    }))
//    it("number facets2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex30.raml"))
//
//    }))
//    it("self rec types", (done => {
//      util.testErrors(done, util.data("parser/examples/ex31.raml"))
//
//    }))
//    it("media type", (done => {
//      util.testErrors(done, util.data("parser/examples/ex32.raml"), Array("Can not parse JSON example: Unexpected token p"))
//
//    }))
//    it("number 0", (done => {
//      util.testErrors(done, util.data("parser/examples/ex33.raml"))
//
//    }))
//    it("example inside of inplace type", (done => {
//      util.testErrors(done, util.data("parser/examples/ex34.raml"), Array("Required property 'x' is missing", "Unknown property: 'x2'"))
//
//    }))
//    it("aws example", (done => {
//      util.testErrors(done, util.data("parser/examples/ex35.raml"))
//
//    }))
//    it("multi unions", (done => {
//      util.testErrors(done, util.data("parser/examples/ex36.raml"))
//
//    }))
//    it("seq and normal mix", (done => {
//      util.testErrors(done, util.data("parser/custom/seqMix.raml"))
//
//    }))
//    it("scalars in examples are parsed correctly", (done => {
//      util.testErrors(done, util.data("parser/examples/ex42.raml"))
//
//    }))
//    it("low level transform understands anchors", (done => {
//      util.testErrors(done, util.data("parser/examples/ex43.raml"))
//
//    }))
//    it("0.8 style of absolute path resolving", (done => {
//      util.testErrors(done, util.data("parser/custom/res08.raml"))
//
//    }))
//    it("example is string 0.8", (done => {
//      util.testErrors(done, util.data("parser/examples/ex44.raml"), Array("Scalar is expected here"))
//
//    }))
//    it("enums values restriction", (done => {
//      util.testErrors(done, util.data("parser/examples/ex37.raml"))
//
//    }))
//    it("anonymous type examples validation test 1", (done => {
//      util.testErrors(done, util.data("parser/examples/ex38.raml"))
//
//    }))
//    it("anonymous type examples validation test 2", (done => {
//      util.testErrors(done, util.data("parser/examples/ex39.raml"))
//
//    }))
//    it("example's property validation", (done => {
//      util.testErrorsByNumber(done, util.data("parser/examples/ex40.raml"), 1)
//
//    }))
//    it("example's union type property validation", (done => {
//      util.testErrorsByNumber(done, util.data("parser/examples/ex41.raml"), 0)
//
//    }))
//    it("union type in schema", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/unions/api.raml"))
//
//    }))
//    it("uri parameters1", (done => {
//      util.testErrors(done, util.data("parser/uris/u1.raml"), Array("Base uri parameter unused"))
//
//    }))
//    it("uri parameters2", (done => {
//      util.testErrors(done, util.data("parser/uris/u2.raml"), Array("Uri parameter unused"))
//
//    }))
//    it("uri parameters3", (done => {
//      util.testErrors(done, util.data("parser/uris/u3.raml"), Array("Unmatched '{'"))
//
//    }))
//    it("uri parameters4", (done => {
//      util.testErrors(done, util.data("parser/uris/u4.raml"), Array("Unmatched '{'"))
//
//    }))
//    it("mediaType1", (done => {
//      util.testErrors(done, util.data("parser/media/m1.raml"), Array("invalid media type"))
//
//    }))
//    it("mediaType2", (done => {
//      util.testErrors(done, util.data("parser/media/m2.raml"), Array("invalid media type"))
//
//    }))
//    it("mediaType3", (done => {
//      util.testErrors(done, util.data("parser/media/m3.raml"), Array("Form related media types can not be used in responses"))
//
//    }))
//    it("annotations1", (done => {
//      util.testErrors(done, util.data("parser/annotations/a.raml"), Array("value should be one of: 'W', 'A'"))
//
//    }))
//    it("annotations2", (done => {
//      util.testErrors(done, util.data("parser/annotations/a2.raml"), Array("Expected type 'boolean' but got 'string'"))
//
//    }))
//    it("annotations3", (done => {
//      util.testErrors(done, util.data("parser/annotations/a3.raml"), Array("Required property 'items' is missing"))
//
//    }))
//    it("annotations4", (done => {
//      util.testErrors(done, util.data("parser/annotations/a4.raml"))
//
//    }))
//    it("annotations5", (done => {
//      util.testErrors(done, util.data("parser/annotations/a5.raml"), Array("Required property 'y' is missing"))
//
//    }))
//    it("annotations6", (done => {
//      util.testErrors(done, util.data("parser/annotations/a6.raml"))
//
//    }))
//    it("annotations7", (done => {
//      util.testErrors(done, util.data("parser/annotations/a7.raml"), Array("Expected type 'boolean' but got 'string'"))
//
//    }))
//    it("annotations8", (done => {
//      util.testErrors(done, util.data("parser/annotations/a8.raml"))
//
//    }))
//    it("annotations9", (done => {
//      util.testErrors(done, util.data("parser/annotations/a9.raml"), Array("Required property 'ee' is missing"))
//
//    }))
//    it("annotations10", (done => {
//      util.testErrors(done, util.data("parser/annotations/a10.raml"))
//
//    }))
//    it("annotations11", (done => {
//      util.testErrors(done, util.data("parser/annotations/a11.raml"), Array("Expected type 'object' but got 'string'"))
//
//    }))
//    it("annotations12", (done => {
//      util.testErrors(done, util.data("parser/annotations/a12.raml"), Array("Expected type 'number' but got 'string'"))
//
//    }))
//    it("annotations13", (done => {
//      util.testErrors(done, util.data("parser/annotations/a13.raml"))
//
//    }))
//    it("annotations14", (done => {
//      util.testErrors(done, util.data("parser/annotations/a14.raml"))
//
//    }))
//    it("annotations15", (done => {
//      util.testErrors(done, util.data("parser/annotations/a15.raml"), Array("Resource property already used: '(meta)'", "Resource property already used: '(meta)'"))
//
//    }))
//    it("annotations16", (done => {
//      util.testErrors(done, util.data("parser/annotations/a16.raml"))
//
//    }))
//    it("annotations17", (done => {
//      util.testErrors(done, util.data("parser/annotations/a17.raml"), Array("Null or undefined value is not allowed", "Header 'header1' already exists", "Header 'header1' already exists"))
//
//    }))
//    it("annotations18", (done => {
//      util.testErrors(done, util.data("parser/annotations/a18.raml"))
//
//    }))
//    it("annotations19", (done => {
//      util.testErrors(done, util.data("parser/annotations/a19.raml"), Array("inheriting from unknown type"))
//
//    }))
//    it("annotations21", (done => {
//      util.testErrors(done, util.data("parser/annotations/a21.raml"))
//
//    }))
//    it("annotations22", (done => {
//      util.testErrors(done, util.data("parser/annotations/a22.raml"))
//
//    }))
//    it("annotations23", (done => {
//      util.testErrors(done, util.data("parser/annotations/a23.raml"))
//
//    }))
//    it("annotations24", (done => {
//      util.testErrors(done, util.data("parser/annotations/a24.raml"))
//
//    }))
//    it("annotations25", (done => {
//      util.testErrors(done, util.data("parser/annotations/a25.raml"))
//
//    }))
//    it("annotations26 (annotated scalar)", (done => {
//      util.testErrors(done, util.data("parser/annotations/a26.raml"))
//
//    }))
//    it("annotations27 (annotated scalar (validation))", (done => {
//      util.testErrors(done, util.data("parser/annotations/a27.raml"), Array("Expected type 'number' but got 'string'"))
//
//    }))
//    it("annotations28 (annotated scalar (unknown))", (done => {
//      util.testErrors(done, util.data("parser/annotations/a28.raml"), Array("unknown annotation: 'z2'"))
//
//    }))
//    it("properties shortcut", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/p.raml"))
//
//    }))
//    it("status", (done => {
//      util.testErrors(done, util.data("parser/status/s1.raml"), Array("Status code should be 3 digits number."))
//
//    }))
//    it("node names", (done => {
//      util.testErrors(done, util.data("parser/nodenames/n1.raml"), Array("Resource type 'x' already exists", "Resource property already used: 'description'", "Resource type 'x' already exists", "Resource property already used: 'description'"))
//
//    }))
//    it("node names2", (done => {
//      util.testErrors(done, util.data("parser/nodenames/n2.raml"), Array("Resource '/frfr' already exists", "Api property already used: 'resourceTypes'", "Resource '/frfr' already exists", "Api property already used: 'resourceTypes'"))
//
//    }))
//    it("recurrent errors", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr.raml"), Array("recurrent array type definition", "recurrent array type definition"))
//
//    }))
//    it("recurrent errors1", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr1.raml"), Array("recurrent type definition"))
//
//    }))
//    it("recurrent errors2", (done => {
//      util.testErrorsByNumber(done, util.data("parser/typexpressions/tr2.raml"), 2, 1)
//
//    }))
//    it("recurrent errors3 ", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr3.raml"), Array("recurrent type as an option of union type", "recurrent type as an option of union type", "recurrent type definition"))
//
//    }))
//    it("recurrent errors4", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr4.raml"), Array("recurrent array type definition", "recurrent array type definition"))
//
//    }))
//    it("recurrent errors5", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr14/test.raml"))
//
//    }))
//    it("schema types 1", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr5.raml"))
//
//    }))
//    it("inheritance rules1", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri1.raml"))
//
//    }))
//    it("inheritance rules2", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri2.raml"))
//
//    }))
//    it("multiple default media types", (done => {
//      util.testErrors(done, util.data("parser/media/m4.raml"))
//
//    }))
//    it("inheritance rules3", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri3.raml"), Array("Restrictions conflict", "Restrictions conflict"))
//
//    }))
//    it("inheritance rules4", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri4.raml"))
//
//    }))
//    it("inheritance rules5", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri5.raml"), Array("Restrictions conflict"))
//
//    }))
//    it("inheritance rules6", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri6.raml"), Array("Restrictions conflict"))
//
//    }))
//    it("inheritance rules7", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/ri7.raml"), Array("Restrictions conflict"))
//
//    }))
//    it("schemas are types 1", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr6.raml"), Array("inheriting from unknown type"))
//
//    }))
//    it("type deps", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr7.raml"), Array("Required property 'element' is missing"))
//
//    }))
//    it("inplace types 00", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr8.raml"), Array("Null or undefined value is not allowed"))
//
//    }))
//    it("unique keys", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr9.raml"), Array("Keys should be unique"))
//
//    }))
//    it("runtime types value", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr10.raml"), Array("Required property 'y' is missing"))
//
//    }))
//    it("runtime types value1", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr11.raml"), Array("Expected type 'object' but got 'string'"))
//
//    }))
//    it("runtime types value2", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr12.raml"))
//
//    }))
//    it("union can be object at same moment sometimes", (done => {
//      util.testErrors(done, util.data("parser/typexpressions/tr14.raml"))
//
//    }))
//    it("no unknown facets in union type are allowed", (done => {
//      util.testErrorsByNumber(done, util.data("parser/typexpressions/tr15.raml"), 1)
//
//    }))
//    it("sequence composition works in 0.8", (done => {
//      util.testErrors(done, util.data("parser/custom/seq.raml"))
//
//    }))
//    it("sequence composition does not works in 1.0", (done => {
//      util.testErrors(done, util.data("parser/custom/seq1.raml"), Array("Unknown node: 'a'", "Unknown node: 'b'", "'traits' should be a map in RAML 1.0"))
//
//    }))
//    it("empty 'traits' array is prohibited in 1.0", (done => {
//      util.testErrors(done, util.data("parser/custom/seq2.raml"), Array("'traits' should be a map in RAML 1.0"))
//
//    }))
//    it("authorization grant is any absolute uri", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/grantIsAnyAbsoluteUri.raml"), 0)
//
//    }))
//    it("empty schema is ok in 0.8", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/emptySchema.raml"), 0)
//
//    }))
//    it("properties are map in 1.0", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/propMap.raml"), 1)
//
//    }))
//    it("schema is yml", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/schemaIsyml.raml"), 0)
//
//    }))
//    it("null tag support", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/nullTag.raml"), 0)
//
//    }))
//    it("r2untime types value2", (done => {
//      util.testErrorsByNumber(done, util.data("parser/typexpressions/tr13.raml"), 1, 1)
//
//    }))
//    it("date time format is checked in super types", (done => {
//      util.testErrorsByNumber(done, util.data("parser/annotations/a31.raml"), 0)
//
//    }))
//    it("date time format is checked in super types (negative)", (done => {
//      util.testErrorsByNumber(done, util.data("parser/annotations/a32.raml"), 1)
//
//    }))
//    it("unknown annotation in example", (done => {
//      util.testErrors(done, util.data("parser/annotations/a35.raml"), Array("using unknown annotation type"))
//
//    }))
//    it("custom api", (done => {
//      util.testErrors(done, util.data("parser/custom/api.raml"), Array("Missing required property 'title'"))
//
//    }))
//    it("discriminator can only be used at top level", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/discTop.raml"), 1)
//
//    }))
//    it("schemas and types are mutually exclusive", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/schemasAndTypes.raml"), 1)
//
//    }))
//    it("naming rules", (done => {
//      util.testErrors(done, util.data("parser/custom/naming1.raml"), Array("Type 'Person' already exists", "Trait 'qq' already exists", "Resource '/ee' already exists", "Type 'Person' already exists", "Trait 'qq' already exists", "Resource '/ee' already exists"))
//
//    }))
//    it("resource types test with types", (done => {
//      util.testErrors(done, util.data("parser/custom/rtypes.raml"))
//
//    }))
//    it("resource path name uses rightmost segment", (done => {
//      util.testErrors(done, util.data("parser/resourceType/resType023.raml"))
//
//    }))
//    it("form parameters are properties", (done => {
//      util.testErrors(done, util.data("parser/custom/noForm.raml"))
//
//    }))
//    it("forms can not be in responses", (done => {
//      util.testErrors(done, util.data("parser/custom/noForm2.raml"), Array("Form related media types can not be used in responses"))
//
//    }))
//    it("APIKey", (done => {
//      util.testErrors(done, util.data("parser/custom/apiKey.raml"))
//
//    }))
//    it("Oath1Sig", (done => {
//      util.testErrors(done, util.data("parser/custom/oath1sig.raml"))
//
//    }))
//    it("regexp validation", (done => {
//      util.testErrors(done, util.data("parser/custom/regexp.raml"), Array("Unterminated group"))
//
//    }))
//    it("regexp validation 2", (done => {
//      util.testErrors(done, util.data("parser/custom/regexp2.raml"), Array("Unterminated group"))
//
//    }))
//    it("regexp validation 3", (done => {
//      util.testErrors(done, util.data("parser/custom/regexp3.raml"), Array("Unterminated group"))
//
//    }))
//    it("spaces in keys", (done => {
//      util.testErrors(done, util.data("parser/custom/keysSpace.raml"), Array("Keys should not have spaces '\\w+  '"))
//
//    }))
//    it("facets11", (done => {
//      util.testErrors(done, util.data("parser/facets/f1.raml"))
//
//    }))
//    it("facets1", (done => {
//      util.testErrors(done, util.data("parser/facets/f2.raml"), Array("Expected type 'string' but got 'number'"))
//
//    }))
//    it("redeclare buildin", (done => {
//      util.testErrors(done, util.data("parser/facets/f3.raml"), Array("redefining a built in type: datetime"))
//
//    }))
//    it("custom facets validator", (done => {
//      util.testErrors(done, util.data("commonLibrary/api.raml"), Array("Expected type 'string' but got 'number'", "Expected type 'string' but got 'number'"))
//
//    }))
//    it("custom facets validator2", (done => {
//      util.testErrors(done, util.data("commonLibrary/api2.raml"), Array())
//
//    }))
//    it("overloading1", (done => {
//      util.testErrors(done, util.data("parser/overloading/o1.raml"), Array("Method 'get' already exists", "Method 'get' already exists"))
//
//    }))
//    it("overloading2", (done => {
//      util.testErrors(done, util.data("parser/overloading/o2.raml"), Array())
//
//    }))
//    it("overloading3", (done => {
//      util.testErrors(done, util.data("parser/overloading/o3.raml"), Array("Resource '/{id}' already exists", "Resource '/{id}' already exists"))
//
//    }))
//    it("overloading4", (done => {
//      util.testErrors(done, util.data("parser/overloading/o4.raml"), Array())
//
//    }))
//    it("overloading7", (done => {
//      util.testErrors(done, util.data("parser/overloading/o7.raml"), Array())
//
//    }))
//    it("override1", (done => {
//      util.testErrors(done, util.data("parser/inheritance/i1.raml"), Array("Restrictions conflict"))
//
//    }))
//    it("override2", (done => {
//      util.testErrors(done, util.data("parser/inheritance/i2.raml"), Array("Facet 'q' can not be overriden"))
//
//    }))
//    it("override3", (done => {
//      util.testErrors(done, util.data("parser/inheritance/i3.raml"))
//
//    }))
//    it("overlay1", (done => {
//      util.testErrors(done, util.data("parser/overlay/o1/NewOverlay.raml"))
//
//    }))
//    it("overlay2", (done => {
//      util.testErrors(done, util.data("parser/overlay/o2/NewOverlay.raml"), Array("The '.env-org-pair2' node does not match any node of the master api."))
//
//    }))
//    it("Overlay: title", (done => {
//      util.testErrors(done, util.data("parser/overlay/o3/NewOverlay.raml"))
//
//    }))
//    it("Overlay: displayName", (done => {
//      util.testErrors(done, util.data("parser/overlay/o4/NewOverlay.raml"))
//
//    }))
//    it("Overlay: annotation types", (done => {
//      util.testErrors(done, util.data("parser/overlay/o5/NewOverlay.raml"))
//
//    }))
//    it("Overlay: types", (done => {
//      util.testErrors(done, util.data("parser/overlay/o6/NewOverlay.raml"))
//
//    }))
//    it("Overlay: schema", (done => {
//      util.testErrors(done, util.data("parser/overlay/o7/NewOverlay.raml"))
//
//    }))
//    it("Overlay: annotations", (done => {
//      util.testErrors(done, util.data("parser/overlay/o8/NewOverlay.raml"))
//
//    }))
//    it("Overlay: usage", (done => {
//      util.testErrors(done, util.data("parser/overlay/o9/NewOverlay.raml"))
//
//    }))
//    it("Overlay: documentation1", (done => {
//      util.testErrors(done, util.data("parser/overlay/o10/NewOverlay.raml"))
//
//    }))
//    it("Overlay: documentation2", (done => {
//      util.testErrors(done, util.data("parser/overlay/o11/NewOverlay.raml"))
//
//    }))
//    it("Overlay: documentation3", (done => {
//      util.testErrors(done, util.data("parser/overlay/o12/NewOverlay.raml"))
//
//    }))
//    it("Overlay: examples1", (done => {
//      util.testErrors(done, util.data("parser/overlay/o13/NewOverlay.raml"))
//
//    }))
//    it("Overlay: examples2", (done => {
//      util.testErrors(done, util.data("parser/overlay/o14/NewOverlay.raml"))
//
//    }))
//    it("Overlay: examples3", (done => {
//      util.testErrors(done, util.data("parser/overlay/o15/NewOverlay.raml"))
//
//    }))
//    it("Overlay: example1", (done => {
//      util.testErrors(done, util.data("parser/overlay/o16/NewOverlay.raml"))
//
//    }))
//    it("Overlay: example2", (done => {
//      util.testErrors(done, util.data("parser/overlay/o17/NewOverlay.raml"))
//
//    }))
//    it("Overlay: top-level illegal property", (done => {
//      util.testErrors(done, util.data("parser/overlay/o18/NewOverlay.raml"), Array("Property 'version' is not allowed to be overriden or added in overlays"))
//
//    }))
//    it("Overlay: sub-level illegal property", (done => {
//      util.testErrors(done, util.data("parser/overlay/o19/NewOverlay.raml"), Array("Property 'default' is not allowed to be overriden or added in overlays"))
//
//    }))
//    it("Overlay: top-level illegal node", (done => {
//      util.testErrors(done, util.data("parser/overlay/o20/NewOverlay.raml"), Array("The './resource2' node does not match any node of the master api."))
//
//    }))
//    it("Overlay: sub-level illegal node 1", (done => {
//      util.testErrors(done, util.data("parser/overlay/o21/NewOverlay.raml"), Array("The './resource./resource2' node does not match any node of the master api."))
//
//    }))
//    it("Overlay: sub-level illegal node 2", (done => {
//      util.testErrors(done, util.data("parser/overlay/o22/NewOverlay.raml"), Array("The './resource.post' node does not match any node of the master api."))
//
//    }))
//    it("Security Scheme Fragment: new security scheme", (done => {
//      util.testErrors(done, util.data("parser/securityschemefragments/ss1/securitySchemeFragment.raml"))
//
//    }))
//    it("library is not user class", (done => {
//      util.testErrors(done, util.data("parser/raml/raml.raml"), Array("It is only allowed to use scalar properties as discriminators"))
//
//    }))
//    it("library from christian", (done => {
//      util.testErrors(done, util.data("parser/libraries/christian/api.raml"))
//
//    }))
//    it("library in resource type fragment", (done => {
//      util.testErrors(done, util.data("parser/libraries/fragment/api.raml"))
//
//    }))
//    it("library in resource type fragment", (done => {
//      util.testErrors(done, util.data("parser/libraries/fragment/api.raml"))
//
//    }))
//    it("nested uses", (done => {
//      util.testErrors(done, util.data("parser/libraries/nestedUses/index.raml"))
//
//    }))
//    it("library require 1", (done => {
//      util.testErrors(done, util.data("parser/libraries/require/a.raml"))
//
//    }))
//    it("library require 2", (done => {
//      util.testErrors(done, util.data("parser/libraries/require/b.raml"))
//
//    }))
//    it("more complex union types1", (done => {
//      util.testErrors(done, util.data("parser/union/apigateway-aws-overlay.raml"))
//
//    }))
//    it("more complex union types2", (done => {
//      util.testErrors(done, util.data("parser/union/unionSample.raml"))
//
//    }))
//    it("external 1", (done => {
//      util.testErrors(done, util.data("parser/external/e1.raml"), Array("Content is not valid according to schema: Missing required property: id"))
//
//    }))
//    it("external 2", (done => {
//      util.testErrors(done, util.data("parser/external/e2.raml"))
//
//    }))
//    it("strange names in parameters", (done => {
//      util.testErrors(done, util.data("parser/custom/strangeParamNames.raml"))
//
//    }))
//    it("should pass without exceptions 1", (done => {
//      util.testErrorsByNumber(done, util.data("parser/api/api29.raml"), 1)
//
//    }))
//    it("should pass without exceptions 2", (done => {
//      util.testErrorsByNumber(done, util.data("parser/api/api30/api.raml"), 2)
//
//    }))
//    it("empty type include should produce no error", (done => {
//      util.testErrors(done, util.data("parser/type/t30.raml"))
//
//    }))
//
//  }))
//  describe("Include tests + typesystem", (() => {
//    this.timeout(30000)
//    it("Include test", (done => {
//      util.testErrorsByNumber(done, util.data("parser/include/includeTypes.raml"))
//
//    }))
//    it("Combination of empty include with expansion", (done => {
//      util.testErrors(done, util.data("parser/include/emptyInclude.raml"), Array("JS-YAML: !include without value", "Can not resolve null"))
//
//    }))
//
//  }))
//  describe("Property override tests", (() => {
//    this.timeout(30000)
//    it("Planets", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test1.raml"), Array("'enum' facet value must be defined by array"))
//
//    }))
//    it("User type properties: correct", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test2.raml"))
//
//    }))
//    it("User type properties: incorrect", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test3.raml"), Array("Restrictions conflict"))
//
//    }))
//    it("Value type properties 1", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test4.raml"))
//
//    }))
//    it("Value type properties 2", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test5.raml"))
//
//    }))
//    it("Value type properties 3", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test6.raml"))
//
//    }))
//    it("Required property overridden as optional", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test7.raml"), Array("Can not override required property 'testProperty' to be optional"))
//
//    }))
//    it("Value type properties 4", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test8.raml"))
//
//    }))
//    it("Facet override", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test9.raml"), Array("Facet 'test' can not be overriden", "missing required facets"))
//
//    }))
//    it("Optional property overridden as required", (done => {
//      util.testErrors(done, util.data("parser/propertyOverride/test10.raml"))
//
//    }))
//    it("existing include should not report any errros", (done => {
//      util.testErrors(done, util.data("parser/custom/includes2.raml"))
//
//    }))
//    it("should parse types which are valid only after expansion", (done => {
//      util.testErrors(done, util.data("parser/templates/validAfterExpansion.raml"))
//
//    }))
//    it("should not accept resouces  which are not valid only after expansion", (done => {
//      util.testErrorsByNumber(done, util.data("parser/templates/invalidAfterExpansion.raml"), 1)
//
//    }))
//    it("resource definition should be a map", (done => {
//      util.testErrors(done, util.data("parser/custom/resShouldBeMap.raml"), Array("Resource definition should be a map"))
//
//    }))
//    it("documentation should be a sequence", (done => {
//      util.testErrors(done, util.data("parser/custom/docShouldBeSequence.raml"), Array("Property 'documentation' should be a sequence"))
//
//    }))
//    it("missed title value should report only one message", (done => {
//      util.testErrors(done, util.data("parser/custom/missedTitle.raml"), Array("Value is not provided for required property 'title'"))
//
//    }))
//    it("expander not halted by this sample any more", (done => {
//      util.testErrorsByNumber(done, util.data("parser/custom/expanderHalt.raml"), 10)
//
//    }))
//
//  }))
//  describe("Optional template parameters tests", (() => {
//    this.timeout(30000)
//    it("Should not report error on unspecified parameter, which is not used after expansion #1.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api01.raml"))
//
//    }))
//    it("Should report error on unspecified parameter, which is used after expansion #1.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api02.raml"), Array("Value is not provided for parameter: 'param1'"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #2.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api03.raml"))
//
//    }))
//    it("Should report error on unspecified parameter, which is used after expansion #2.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api04.raml"), Array("Value is not provided for parameter: 'param1'"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #3.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api05.raml"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #4.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api06.raml"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #5.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api07.raml"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #6.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api08.raml"))
//
//    }))
//    it("Should not report error on unspecified parameter, which is not used after expansion #7.", (done => {
//      util.testErrors(done, util.data("parser/optionalTemplateParameters/api09.raml"))
//
//    }))
//    it("Only methods are permitted to be optional in sense of templates expansion.", (done => {
//      util.testErrors(done, util.data("parser/illegalOptionalParameters/api01.raml"), Array("Only method nodes can be optional"))
//
//    }))
//
//  }))
//  describe("RAML10/Dead Loop Tests/Includes", (() => {
//    this.timeout(30000)
//    it("test001", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test001/api.raml"), Array("Recursive definition"))
//
//    }))
//    it("test002", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test002/api.raml"), Array("Recursive definition"))
//
//    }))
//    it("test003", (done => {
//      util.testErrorsByNumber(done, util.data("./parser/deadLoopTests/Includes/test003/file1.raml"), 2)
//
//    }))
//    it("test004", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test002/api.raml"), Array("Recursive definition"))
//
//    }))
//
//  }))
//  describe("RAML10/Dead Loop Tests/Libraries", (() => {
//    this.timeout(30000)
//    it("test001", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test001/lib.raml"))
//
//    }))
//    it("test002", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test002/lib.raml"))
//
//    }))
//    it("test003", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test003/lib1.raml"))
//
//    }))
//    it("test003", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test003/lib2.raml"))
//
//    }))
//    it("test004", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test004/lib1.raml"))
//
//    }))
//    it("test004", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test004/lib2.raml"))
//
//    }))
//
//  }))
//  describe("RAML10/Dead Loop Tests/ResourceTypes", (() => {
//    this.timeout(30000)
//    it("test001", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test001/api.raml"), Array("Resource type definition contains cycle"))
//
//    }))
//    it("test002", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test002/lib1.raml"))
//
//    }))
//    it("test002", (done => {
//      util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test002/lib2.raml"))
//
//    }))
//
//  }))
//  after((() => {
//    util.stopConnection()
//
//  }))
//
//
//}
