declare var describe;
declare var it;
declare var require;

import util = require("./util");

beforeEach(function () {
    util.sleep(100);
});

describe('Parser integration tests', function() {
    this.timeout(30000);
    
    it ("Instagram",function(done){
       util.testErrors(done, util.data("../example-ramls/Instagram/api.raml"), ["Content is not valid according to schema: Expected type string but found type null", "Content is not valid according to schema: Expected type string but found type null", "Content is not valid according to schema: Expected type object but found type null", "Content is not valid according to schema: Expected type object but found type null"]);
    });
    it ("Omni",function(done){
        util.testErrors(done, util.data("../example-ramls/omni/api.raml"));
    });
    it ("Omni 0.8",function(done){
        util.testErrors(done, util.data("../example-ramls/omni08/api.raml"));
    });

    it ("Cosmetics Overlay",function(done){
        util.testErrors(done, util.data("../example-ramls/cosmetics/hypermedia.raml"));
    });
    it ("Cosmetics Extension",function(done){
        util.testErrors(done, util.data("../example-ramls/cosmetics/hypermedia1.raml"));
    });
    // it ("Instagram 1.0",function(done){
    //     util.testErrors(util.data("../example-ramls/Instagram/api.raml"),[
    //         "Content is not valid according to schema: Expected type string but found type null",
    //         "Content is not valid according to schema: Expected type string but found type null",
    //         "Content is not valid according to schema: Expected type object but found type null",
    //         "Content is not valid according to schema: Expected type object but found type null"
    //     ]);
    // });
    it ("Exchange",function(done){
        util.testErrors(done, util.data("../example-ramls/exchange/api.raml"));
    });
    it ("core services",function(done){
        util.testErrors(done, util.data("../example-ramls/core-services/api.raml"),
            [
                "Can not parse JSON example: Unexpected token '{'",
                "Can not parse JSON example: Cannot tokenize symbol 'O'",
                "Can not parse JSON example: Cannot tokenize symbol 'O'"
            ]);
    });
    it ("cloudhub new logging",function(done){
        util.testErrors(done, util.data("../example-ramls/cloudhub-new-logging/api.raml"),["Can not parse JSON example: Unexpected token '}'"]);
    });
    it ("audit logging",function(done){
        util.testErrors(done, util.data("../example-ramls/audit-logging-query/api.raml"));
    });
    it ("application monitoring",function(done){
        util.testErrors(done, util.data("../example-ramls/application-monitoring/api.raml"),
            [
                "Unrecognized schema: 'appmonitor'"
            ]);
    });

    it ("lib1",function(done){
        util.testErrors(done, util.data("../example-ramls/blog-users1/blog-users.raml"));
    });
    it ("lib2",function(done){
        util.testErrorsByNumber(done, util.data("../example-ramls/blog-users2/blog-users.raml"),2,1);
    });
    it ("lib3",function(done){
        util.testErrorsByNumber(done, util.data("../example-ramls/blog-users2/blog-users.raml"),2,1);
    });
    it ("platform2",function(done){
        util.testErrors(done, util.data("../example-ramls/platform2/api.raml"),[],true);
    });
});

describe('https connection tests',function(){
    this.timeout(30000);
    
    it ("https 0.8",function(done){
        util.testErrors(done, util.data("parser/https/tr1.raml"));
    });

    it ("https 1.0",function(done){
        util.testErrors(done, util.data("parser/https/tr2.raml"));
    });
});

describe('Transformers tests',function(){
    this.timeout(30000);
    
    it ("All transformer from spec should be valid.",function(done){
        util.testErrors(done, util.data("parser/transformers/t1.raml"), ["Unknown function applied to parameter: \'!\\w+\'"]);
    });
});

describe('Security Schemes tests', function() {
    this.timeout(30000);
    
    it ("should fail if not all required settings specified" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss1/securityScheme.raml"), ["Missing required property \'\\w+\'"]);
    })
    it ("should pass when extra non-required settings specified" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss2/securityScheme.raml"));
    })

    it ("should pass when settings contains array property(0.8)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss3/securityScheme.raml"));
    })
    it ("should fail when settings contains duplicate required properties(0.8)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss4/securityScheme.raml"),["property already used: 'accessTokenUri'", "property already used: 'accessTokenUri'"]);
    })
    it ("should fail when settings contains duplicate required array properties(0.8)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss5/securityScheme.raml"), ["property already used: 'authorizationGrants'", "property already used: 'authorizationGrants'", "property already used: 'authorizationGrants'"]);
    })
    it ("should fail when settings contains duplicate non-required properties(0.8)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss6/securityScheme.raml"), ["property already used: 'aaa'", "property already used: 'aaa'"]);
    })

    it ("should pass when settings contains required array property(1.0)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss7/securityScheme.raml"));
    })
    it ("should fail when settings contains duplicate required properties(1.0)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss8/securityScheme.raml"), ["property already used: 'accessTokenUri'", "property already used: 'accessTokenUri'"]);
    })
    it ("should fail when settings contains duplicate required array properties(1.0)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss9/securityScheme.raml"), ["property already used: 'authorizationGrants'",  "property already used: 'authorizationGrants'"]);
    })
    it ("null-value security schema should be allowed for Api(0.8)" ,function(done){
        util.testErrors(done, util.data("parser/securitySchemes/ss10/securityScheme.raml"));
    })
    it ("grant type validation" ,function(done){
        util.testErrors(done, util.data("parser/custom/oath2.raml"),["'authorizationGrants' value should be one of 'authorization_code', 'implicit', 'password', 'client_credentials' or to be an abolute URI"]);
    })
    it ("security scheme should be a seq in 0.8" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/shemeShouldBeASeq.raml"),1);
    })
});
describe('Parser regression tests', function() {
    this.timeout(30000);
    
    it ("basic type expression cases should pass validation" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/basic.raml"));
    })
    it ("inplace types" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/inplace.raml"));
    })
    it ("template vars0" ,function(done){
        util.testErrors(done, util.data("parser/templates/unknown.raml"));
    })
    it ("example validation" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex1.raml"), ["Can not parse JSON example: Unexpected token d"]);
    })
    it ("example validation json against schema" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex2.raml"), ["Content is not valid according to schema"]);
    })
    it ("example validation yaml against schema" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex3.raml"), ["Content is not valid according to schema: Additional properties not allowed: vlue"]);
    })
    it ("example validation yaml against basic type" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex4.raml"),["Required property 'c' is missing"]);
    })
    it ("example validation yaml against inherited type" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex5.raml"), ["Required property 'c' is missing"]);
    })
    it ("example validation yaml against array" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex6.raml"),["Required property 'c' is missing"]);
    })
    it ("example in model" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex7.raml"), ["Expected type 'string' but got 'number'","Expected type 'string' but got 'number'","Required property 'c' is missing"]);
    })
    it ("another kind of examples" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex13.raml"), ["Expected type 'boolean' but got 'number'"]);
    })
    it ("example in parameter" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex8.raml"), ["Expected type 'boolean' but got 'string'"]);
    })

    it ("checking that node is actually primitive" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex9.raml"), ["Expected type 'string' but got 'object'"]);
    })
    it ("map" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex10.raml"));
    })
    it ("map1" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex11.raml"), ["Expected type 'number' but got 'string'"]);
    })
    it ("map2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex12.raml"),["Expected type 'number' but got 'string'"]);
    })
    it ("objects are closed" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex14.raml"), ["Unknown property: 'z'"]);
    })
    it ("enums restriction" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex15.raml"),["value should be one of: 'val1', 'val2', '3'"]);
    })
    it ("array facets" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex16.raml"), ["'Person.items.minItems=5' i.e. array items count should not be less than 5", "'Person.items2.maxItems=3' i.e. array items count should not be more than 3", "items should be unique"]);
    })
    it ("array facets2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex17.raml"));
    })
    it ("array facets3" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex18.raml"), ["'SmallArray.minItems=5' i.e. array items count should not be less than 5"]);
    })
    it ("array facets4" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex19.raml"),["'SmallArray.minItems=5' i.e. array items count should not be less than 5"]);
    })
    it ("object facets1" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex20.raml"), ["'MyType.minProperties=2' i.e. object properties count should not be less than 2"]);
    })
    it ("object facets2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex21.raml"), ["'MyType1.minProperties=2' i.e. object properties count should not be less than 2"]);
    })
    it ("object facets3" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex22.raml"), ["'MyType1.maxProperties=1' i.e. object properties count should not be more than 1"]);
    })
    it ("object facets4" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex23.raml"));
    })
    it ("object facets5" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex24.raml"), ["'MyType1.minProperties=3' i.e. object properties count should not be less than 3"]);
    })
    it ("string facets1" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex25.raml"), ["'MyType1.minLength=5' i.e. string length should not be less than 5", "'MyType2.maxLength=3' i.e. string length should not be more than 3"]);
    })
    it ("string facets2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex26.raml"));
    })
    it ("string facets3" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex27.raml"), ["'MyType1.minLength=5' i.e. string length should not be less than 5"]);
    })
    it ("string facets4" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex28.raml"), ["string should match to '\\.5'"]);
    })
    it ("number facets1" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex29.raml"),["'MyType1.minimum=5' i.e. value should not be less than 5", "'MyType1.minimum=5' i.e. value should not be less than 5"]);
    })

    it ("number facets2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex30.raml"));
    })
    it ("self rec types" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex31.raml"));
    })
    it ("media type" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex32.raml"),["Can not parse JSON example: Unexpected token p"]);
    })
    it ("number 0" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex33.raml"));
    })
    it ("example inside of inplace type" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex34.raml"), ["Required property 'x' is missing","Unknown property: 'x2'"]);
    })
    it ("aws example" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex35.raml"));
    })
    it ("multi unions" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex36.raml"));
    })
    it ("seq and normal mix" ,function(done){
        util.testErrors(done, util.data("parser/custom/seqMix.raml"));
    })
    it ("scalars in examples are parsed correctly" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex42.raml"));
    })
    it ("low level transform understands anchors" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex43.raml"));
    })
    it ("0.8 style of absolute path resolving" ,function(done){
        util.testErrors(done, util.data("parser/custom/res08.raml"));
    })
    it ("example is string 0.8" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex44.raml"),["Scalar is expected here"]);
    })
    it ("enums values restriction" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex37.raml"));
    })
    it ("anonymous type examples validation test 1" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex38.raml"));
    })

    it ("anonymous type examples validation test 2" ,function(done){
        util.testErrors(done, util.data("parser/examples/ex39.raml"));
    })
    it ("example's property validation" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/examples/ex40.raml"),1);
    })
    it ("example's union type property validation" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/examples/ex41.raml"),0);
    })
    it ("union type in schema" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/unions/api.raml"));
    })
    it ("uri parameters1" ,function(done){
        util.testErrors(done, util.data("parser/uris/u1.raml"), ["Base uri parameter unused"]);
    })
    it ("uri parameters2" ,function(done){
        util.testErrors(done, util.data("parser/uris/u2.raml"), ["Uri parameter unused"]);
    })
    it ("uri parameters3" ,function(done){
        util.testErrors(done, util.data("parser/uris/u3.raml"), ["Unmatched '{'"]);
    })
    it ("uri parameters4" ,function(done){
        util.testErrors(done, util.data("parser/uris/u4.raml"), ["Unmatched '{'"]);
    })
    it ("mediaType1" ,function(done){
        util.testErrors(done, util.data("parser/media/m1.raml"), ["invalid media type"]);
    })
    it ("mediaType2" ,function(done){
        util.testErrors(done, util.data("parser/media/m2.raml"), ["invalid media type"]);
    })
    it ("mediaType3" ,function(done){
        util.testErrors(done, util.data("parser/media/m3.raml"), ["Form related media types can not be used in responses"]);
    })
    it ("annotations1" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a.raml"), ["value should be one of: 'W', 'A'"]);
    })
    it ("annotations2" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a2.raml"), ["Expected type 'boolean' but got 'string'"]);
    })
    it ("annotations3" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a3.raml"), ["Required property 'items' is missing"]);
    })
    it ("annotations4" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a4.raml"));
    })
    it ("annotations5" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a5.raml"), ["Required property 'y' is missing"]);
    })
    it ("annotations6" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a6.raml"));
    })
    it ("annotations7" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a7.raml"), ["Expected type 'boolean' but got 'string'"]);
    })
    it ("annotations8" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a8.raml"));
    })
    it ("annotations9" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a9.raml"), ["Required property 'ee' is missing"]);
    })
    it ("annotations10" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a10.raml"));
    })
    it ("annotations11" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a11.raml"), ["Expected type 'object' but got 'string'"]);
    })
    it ("annotations12" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a12.raml"), ["Expected type 'number' but got 'string'"]);
    })
    it ("annotations13" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a13.raml"));
    })
    it ("annotations14" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a14.raml"));
    })
    it ("annotations15" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a15.raml"), ["Resource property already used: '(meta)'", "Resource property already used: '(meta)'"]);
    })
    it ("annotations16" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a16.raml"));
    })
    it ("annotations17" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a17.raml"), ["Null or undefined value is not allowed","Header 'header1' already exists","Header 'header1' already exists"]);
    })
    it ("annotations18" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a18.raml"));
    })
    it ("annotations19" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a19.raml"), ["inheriting from unknown type"]);
    })
    it ("annotations21" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a21.raml"));
    })
    it ("annotations22" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a22.raml"));
    })
    it ("annotations23" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a23.raml"));
    })
    it ("annotations24" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a24.raml"));
    })
    it ("annotations25" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a25.raml"));
    })
    it ("annotations26 (annotated scalar)" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a26.raml"));
    })
    it ("annotations27 (annotated scalar (validation))" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a27.raml"),["Expected type 'number' but got 'string'"]);
    })
    it ("annotations28 (annotated scalar (unknown))" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a28.raml"),["unknown annotation: 'z2'"]);
    })
    it ("properties shortcut" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/p.raml"));
    })
    it ("status" ,function(done){
        util.testErrors(done, util.data("parser/status/s1.raml"),["Status code should be 3 digits number."]);
    })
    it ("node names" ,function(done){
        util.testErrors(done, util.data("parser/nodenames/n1.raml"), ["Resource type 'x' already exists","Resource property already used: 'description'","Resource type 'x' already exists","Resource property already used: 'description'"]);
    })
    it ("node names2" ,function(done){
        util.testErrors(done, util.data("parser/nodenames/n2.raml"),["Resource '/frfr' already exists","Api property already used: 'resourceTypes'","Resource '/frfr' already exists","Api property already used: 'resourceTypes'"]);
    })
    it ("recurrent errors" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr.raml"), ["recurrent array type definition","recurrent array type definition"]);
    })
    it ("recurrent errors1" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr1.raml"),["recurrent type definition"] );
    })
    it ("recurrent errors2" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/typexpressions/tr2.raml"),2,1);
    })

    it ("recurrent errors3 " ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr3.raml"),["recurrent type as an option of union type", "recurrent type as an option of union type", "recurrent type definition"]);
    })

    it ("recurrent errors4" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr4.raml"),["recurrent array type definition", "recurrent array type definition"]);
    })
    it ("recurrent errors5" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr14/test.raml"));
    })
    it ("schema types 1" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr5.raml"));
    })
    it ("inheritance rules1" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri1.raml"));
    })
    it ("inheritance rules2" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri2.raml"));
    })
    it ("multiple default media types" ,function(done){
        util.testErrors(done, util.data("parser/media/m4.raml"));
    })
    it("inheritance rules3" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri3.raml"), ["Restrictions conflict","Restrictions conflict"]);
    })
    it ("inheritance rules4" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri4.raml"));
    })
    it ("inheritance rules5" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri5.raml"),["Restrictions conflict"]);
    })
    it ("inheritance rules6" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri6.raml"), ["Restrictions conflict"]);
    })
    it ("inheritance rules7" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/ri7.raml"),["Restrictions conflict"]);
    })
    it ("schemas are types 1" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr6.raml"), ["inheriting from unknown type"]);
    })
    it ("type deps" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr7.raml"),["Required property 'element' is missing"]);
    })
    it ("inplace types 00" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr8.raml"),["Null or undefined value is not allowed"]);
    })
    it ("unique keys" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr9.raml"),["Keys should be unique"]);
    })
    it ("runtime types value" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr10.raml"),["Required property 'y' is missing"]);
    })
    it ("runtime types value1" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr11.raml"),["Expected type 'object' but got 'string'"]);
    })
    it ("runtime types value2" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr12.raml"));
    })
    it ("union can be object at same moment sometimes" ,function(done){
        util.testErrors(done, util.data("parser/typexpressions/tr14.raml"));
    })
    it ("no unknown facets in union type are allowed" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/typexpressions/tr15.raml"),1);
    })
    it ("sequence composition works in 0.8" ,function(done){
        util.testErrors(done, util.data("parser/custom/seq.raml"));
    })
    it ("sequence composition does not works in 1.0" ,function(done){
        util.testErrors(done, util.data("parser/custom/seq1.raml"),["Unknown node: 'a'","Unknown node: 'b'","'traits' should be a map in RAML 1.0"]);
    })
    it ("empty 'traits' array is prohibited in 1.0" ,function(done){
        util.testErrors(done, util.data("parser/custom/seq2.raml"),["'traits' should be a map in RAML 1.0"]);
    })
    it ("authorization grant is any absolute uri" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/grantIsAnyAbsoluteUri.raml"),0);
    })
    it ("empty schema is ok in 0.8" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/emptySchema.raml"),0);
    })
    it ("properties are map in 1.0" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/propMap.raml"),1);
    })
    it ("schema is yml" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/schemaIsyml.raml"),0);
    })

    it ("null tag support" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/nullTag.raml"),0);
    })
    it ("r2untime types value2" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/typexpressions/tr13.raml"),1,1);
    })
    it ("date time format is checked in super types" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/annotations/a31.raml"),0);
    })
    it ("date time format is checked in super types (negative)" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/annotations/a32.raml"),1);
    })
    it ("unknown annotation in example" ,function(done){
        util.testErrors(done, util.data("parser/annotations/a35.raml"),["using unknown annotation type"]);
    })
    it ("custom api" ,function(done){
        util.testErrors(done, util.data("parser/custom/api.raml"), ["Missing required property 'title'"]);
    })
    it ("discriminator can only be used at top level" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/discTop.raml"), 1);
    })
    it ("schemas and types are mutually exclusive" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/schemasAndTypes.raml"), 1);
    })
    it ("naming rules" ,function(done){
        util.testErrors(done, util.data("parser/custom/naming1.raml"),["Type 'Person' already exists", "Trait 'qq' already exists", "Resource '/ee' already exists","Type 'Person' already exists", "Trait 'qq' already exists", "Resource '/ee' already exists"]);
    })
    it ("resource types test with types" ,function(done){
        util.testErrors(done, util.data("parser/custom/rtypes.raml"));
    })
    it ("resource path name uses rightmost segment" ,function(done){
        util.testErrors(done, util.data("parser/resourceType/resType023.raml"));
    })
    it ("form parameters are properties" ,function(done){
        util.testErrors(done, util.data("parser/custom/noForm.raml"));
    })
    it ("forms can not be in responses" ,function(done){
        util.testErrors(done, util.data("parser/custom/noForm2.raml"),["Form related media types can not be used in responses"]);
    })
    it ("APIKey" ,function(done){
        util.testErrors(done, util.data("parser/custom/apiKey.raml"));
    })
    it ("Oath1Sig" ,function(done){
        util.testErrors(done, util.data("parser/custom/oath1sig.raml"));
    })
    it ("regexp validation" ,function(done){
        util.testErrors(done, util.data("parser/custom/regexp.raml"),["Unterminated group"]);
    })
    it ("regexp validation 2" ,function(done){
        util.testErrors(done, util.data("parser/custom/regexp2.raml"), ["Unterminated group"]);
    })
    it ("regexp validation 3" ,function(done){
        util.testErrors(done, util.data("parser/custom/regexp3.raml"), ["Unterminated group"]);
    })
    it ("spaces in keys" ,function(done){
        util.testErrors(done, util.data("parser/custom/keysSpace.raml"),["Keys should not have spaces '\\w+  '"]);
    })
    it ("facets11" ,function(done){
        util.testErrors(done, util.data("parser/facets/f1.raml"));
    })
    it ("facets1" ,function(done){
        util.testErrors(done, util.data("parser/facets/f2.raml"), ["Expected type 'string' but got 'number'"]);
    })
    it ("redeclare buildin" ,function(done){
        util.testErrors(done, util.data("parser/facets/f3.raml"),["redefining a built in type: datetime"]);
    })
    it ("custom facets validator" ,function(done){
        util.testErrors(done, util.data("commonLibrary/api.raml"), ["Expected type 'string' but got 'number'","Expected type 'string' but got 'number'"]);
    })
    it ("custom facets validator2" ,function(done){
        util.testErrors(done, util.data("commonLibrary/api2.raml"),[]);
    })
    it ("overloading1" ,function(done){
        util.testErrors(done, util.data("parser/overloading/o1.raml"),["Method 'get' already exists","Method 'get' already exists"]);
    })
    it ("overloading2" ,function(done){
        util.testErrors(done, util.data("parser/overloading/o2.raml"),[]);
    })
    it ("overloading3" ,function(done){
        util.testErrors(done, util.data("parser/overloading/o3.raml"),["Resource '/{id}' already exists","Resource '/{id}' already exists"]);
    })
    it ("overloading4" ,function(done){
        util.testErrors(done, util.data("parser/overloading/o4.raml"),[]);
    })

    it ("overloading7" ,function(done){
        util.testErrors(done, util.data("parser/overloading/o7.raml"),[]);
    })

    it ("override1" ,function(done){
        util.testErrors(done, util.data("parser/inheritance/i1.raml"), ["Restrictions conflict"]);
    })
    it ("override2" ,function(done){
        util.testErrors(done, util.data("parser/inheritance/i2.raml"),["Facet 'q' can not be overriden"]);
    })
    it ("override3" ,function(done){
        util.testErrors(done, util.data("parser/inheritance/i3.raml"));
    })
    it ("overlay1" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o1/NewOverlay.raml"));
    })
    it ("overlay2" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o2/NewOverlay.raml"),["The '.env-org-pair2' node does not match any node of the master api."]);
    })
    it ("Overlay: title" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o3/NewOverlay.raml"));
    })
    it ("Overlay: displayName" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o4/NewOverlay.raml"));
    })
    it ("Overlay: annotation types" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o5/NewOverlay.raml"));
    })
    it ("Overlay: types" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o6/NewOverlay.raml"));
    })
    it ("Overlay: schema" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o7/NewOverlay.raml"));
    })
    it ("Overlay: annotations" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o8/NewOverlay.raml"));
    })
    it ("Overlay: usage" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o9/NewOverlay.raml"));
    })
    it ("Overlay: documentation1" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o10/NewOverlay.raml"));
    })
    it ("Overlay: documentation2" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o11/NewOverlay.raml"));
    })
    it ("Overlay: documentation3" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o12/NewOverlay.raml"));
    })
    it ("Overlay: examples1" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o13/NewOverlay.raml"));
    })
    it ("Overlay: examples2" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o14/NewOverlay.raml"));
    })
    it ("Overlay: examples3" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o15/NewOverlay.raml"));
    })
    it ("Overlay: example1" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o16/NewOverlay.raml"));
    })
    it ("Overlay: example2" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o17/NewOverlay.raml"));
    })
    it ("Overlay: top-level illegal property" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o18/NewOverlay.raml"), ["Property 'version' is not allowed to be overriden or added in overlays"]);
    })
    it ("Overlay: sub-level illegal property" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o19/NewOverlay.raml"), ["Property 'default' is not allowed to be overriden or added in overlays"]);
    })
    it ("Overlay: top-level illegal node" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o20/NewOverlay.raml"),["The './resource2' node does not match any node of the master api."]);
    })
    it ("Overlay: sub-level illegal node 1" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o21/NewOverlay.raml"),["The './resource./resource2' node does not match any node of the master api."]);
    })
    it ("Overlay: sub-level illegal node 2" ,function(done){
        util.testErrors(done, util.data("parser/overlay/o22/NewOverlay.raml"),["The './resource.post' node does not match any node of the master api."]);
    })

    it ("Security Scheme Fragment: new security scheme" ,function(done){
        util.testErrors(done, util.data("parser/securityschemefragments/ss1/securitySchemeFragment.raml"));
    })

    it ("library is not user class" ,function(done){
        util.testErrors(done, util.data("parser/raml/raml.raml"),["It is only allowed to use scalar properties as discriminators"]);
    })
    it ("library from christian" ,function(done){
        util.testErrors(done, util.data("parser/libraries/christian/api.raml"));
    })
    it ("library in resource type fragment" ,function(done){
        util.testErrors(done, util.data("parser/libraries/fragment/api.raml"));
    })
    it ("library in resource type fragment" ,function(done){
        util.testErrors(done, util.data("parser/libraries/fragment/api.raml"));
    })
    it ("nested uses" ,function(done){
        util.testErrors(done, util.data("parser/libraries/nestedUses/index.raml"));
    })
    it ("library require 1" ,function(done){
        util.testErrors(done, util.data("parser/libraries/require/a.raml"));
    })
    it ("library require 2" ,function(done){
        util.testErrors(done, util.data("parser/libraries/require/b.raml"));
    })
    it ("more complex union types1",function(done){
        util.testErrors(done, util.data("parser/union/apigateway-aws-overlay.raml"));

    })
    it ("more complex union types2",function(done){
        util.testErrors(done, util.data("parser/union/unionSample.raml"));

    })
    it ("external 1" ,function(done){
        util.testErrors(done, util.data("parser/external/e1.raml"),["Content is not valid according to schema: Missing required property: id"]);
    })
    it ("external 2" ,function(done){
        util.testErrors(done, util.data("parser/external/e2.raml"));
    })

    it ("strange names in parameters" ,function(done){
        util.testErrors(done, util.data("parser/custom/strangeParamNames.raml"));
    })
    it ("should pass without exceptions 1" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/api/api29.raml"), 1);
    })
    it ("should pass without exceptions 2" ,function(done){
        util.testErrorsByNumber(done, util.data("parser/api/api30/api.raml"), 2);
    })

    it ("empty type include should produce no error" ,function(done){
        util.testErrors(done, util.data("parser/type/t30.raml"));
    })
});

describe("Include tests + typesystem",function(){
    this.timeout(30000);
    
    it("Include test" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/include/includeTypes.raml"));
    })

    it("Combination of empty include with expansion" ,function(done) {
        util.testErrors(done, util.data("parser/include/emptyInclude.raml"),["JS-YAML: !include without value", "Can not resolve null"]);
    })
})

describe('Property override tests',function(){
    this.timeout(30000);
    
    it ("Planets",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test1.raml"),["'enum' facet value must be defined by array"]);
    });

    it ("User type properties: correct",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test2.raml"));
    });

    it ("User type properties: incorrect",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test3.raml"), ["Restrictions conflict"]);
    });

    it ("Value type properties 1",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test4.raml"));
    });

    it ("Value type properties 2",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test5.raml"));
    });

    it ("Value type properties 3",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test6.raml"));
    });

    it ("Required property overridden as optional",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test7.raml"), ["Can not override required property 'testProperty' to be optional"]);
    });

    it ("Value type properties 4",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test8.raml"));
    });

    it ("Facet override",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test9.raml"), ["Facet 'test' can not be overriden","missing required facets"]);
    });

    it ("Optional property overridden as required",function(done){
        util.testErrors(done, util.data("parser/propertyOverride/test10.raml"));
    });

    it ("existing include should not report any errros",function(done){
        util.testErrors(done, util.data("parser/custom/includes2.raml"));
    });
    it ("should parse types which are valid only after expansion",function(done){
        util.testErrors(done, util.data("parser/templates/validAfterExpansion.raml"));
    });
    it ("should not accept resouces  which are not valid only after expansion",function(done){
        util.testErrorsByNumber(done, util.data("parser/templates/invalidAfterExpansion.raml"),1);
    });
    it ("resource definition should be a map",function(done){
        util.testErrors(done, util.data("parser/custom/resShouldBeMap.raml"),["Resource definition should be a map"]);
    });
    it ("documentation should be a sequence",function(done){
        util.testErrors(done, util.data("parser/custom/docShouldBeSequence.raml"),["Property 'documentation' should be a sequence"]);
    });
    it ("missed title value should report only one message",function(done){
        util.testErrors(done, util.data("parser/custom/missedTitle.raml"),["Value is not provided for required property 'title'"]);
    });
    it ("expander not halted by this sample any more",function(done){
        util.testErrorsByNumber(done, util.data("parser/custom/expanderHalt.raml"),10);
    });
});

describe('Optional template parameters tests', function() {
    this.timeout(30000);
    
    it("Should not report error on unspecified parameter, which is not used after expansion #1.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api01.raml"));
    });
    it("Should report error on unspecified parameter, which is used after expansion #1.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api02.raml"),["Value is not provided for parameter: 'param1'"]);
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #2.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api03.raml"));
    });
    it("Should report error on unspecified parameter, which is used after expansion #2.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api04.raml"),["Value is not provided for parameter: 'param1'"]);
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #3.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api05.raml"));
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #4.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api06.raml"));
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #5.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api07.raml"));
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #6.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api08.raml"));
    });
    it("Should not report error on unspecified parameter, which is not used after expansion #7.", function(done) {
        util.testErrors(done, util.data("parser/optionalTemplateParameters/api09.raml"));
    });
    it("Only methods are permitted to be optional in sense of templates expansion.", function(done) {
        util.testErrors(done, util.data("parser/illegalOptionalParameters/api01.raml"),["Only method nodes can be optional"]);
    });
});

describe('RAML10/Dead Loop Tests/Includes',function(){
    this.timeout(30000);
    
    it("test001", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test001/api.raml"),["Recursive definition"]);
    });

    it("test002", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test002/api.raml"),["Recursive definition"]);
    });

    it("test003", function(done) {
        util.testErrorsByNumber(done, util.data("./parser/deadLoopTests/Includes/test003/file1.raml"),2);
    });

    it("test004", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Includes/test002/api.raml"),["Recursive definition"]);
    });

});

// describe('RAML10/Dead Loop Tests/JSONSchemas',function(){
//     this.timeout(30000);
//
//     it("test001", function(done) {
//         util.testErrors(done, util.data("./parser/deadLoopTests/JSONSchemas/test001/api.raml"),["JSON schema contains circular references","JSON schema contains circular references"]);
//     });
//
//     it("test002", function(done) {
//         util.testErrors(done, util.data("./parser/deadLoopTests/JSONSchemas/test002/api.raml"),["JSON schema contains circular references","JSON schema contains circular references"]);
//     });
//
//     it("test003", function(done) {
//         util.testErrors(done, util.data("./parser/deadLoopTests/JSONSchemas/test003/api.raml"),["Can not parse JSON example: Unexpected end of JSON input","Can not parse JSON example: Unexpected end of JSON input"]);
//     });
//
// });

describe('RAML10/Dead Loop Tests/Libraries',function(){
    this.timeout(30000);
    
    it("test001", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test001/lib.raml"));
    });

    it("test002", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test002/lib.raml"));
    });

    it("test003", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test003/lib1.raml"));
    });

    it("test003", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test003/lib2.raml"));
    });

    it("test004", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test004/lib1.raml"));
    });

    it("test004", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/Libraries/test004/lib2.raml"));
    });

});

describe('RAML10/Dead Loop Tests/ResourceTypes',function() {
    this.timeout(30000);
    
    it("test001", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test001/api.raml"),["Resource type definition contains cycle"]);
    });

    it("test002", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test002/lib1.raml"));
    });

    it("test002", function(done) {
        util.testErrors(done, util.data("./parser/deadLoopTests/ResourceTypes/test002/lib2.raml"));
    });
});

after(function() {
    util.stopConnection();
});