declare var describe;
declare var it;
declare var require;

import util = require("./util");

beforeEach(function () {
    util.sleep(100);
});

describe('XML parsing tests', function() {
    this.timeout(30000);

    it("XML parsing tests 1" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test1/apiValid.raml"), 0);
    })
    it("XML parsing tests 2" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test1/apiInvalid1.raml"), 1);


    })
    it("XML parsing tests 3" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test1/apiInvalid2.raml"), 1);
    })
    it("XML parsing tests 4" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test1/apiInvalid3.raml"), 1);
    })
    it("XML parsing tests 5" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test1/apiInvalid4.raml"), 0);
    })


    it("XML parsing tests 6" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiValid.raml"), 0);
    })
    it("XML parsing tests 7" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiInvalid1.raml"), 1);
    })
    it("XML parsing tests 8" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiInvalid2.raml"), 1);
    })
    it("XML parsing tests 9" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiInvalid3.raml"), 1);
    })
    it("XML parsing tests 10" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiInvalid4.raml"), 1);
    })
    it("XML parsing tests 11" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test2/apiInvalid5.raml"), 1);
    })

    it("XML parsing tests 12" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiValid.raml"), 0);
    })
    it("XML parsing tests 13" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid1.raml"), 1);
    })
    it("XML parsing tests 14" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid2.raml"), 1);
    })
    it("XML parsing tests 15" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid3.raml"), 2);
    })
    it("XML parsing tests 16" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid4.raml"), 1);
    })
    it("XML parsing tests 17" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid5.raml"), 1);
    })
    it("XML parsing tests 18" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid6.raml"), 1);
    })
    it("XML parsing tests 19" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test3/apiInvalid7.raml"), 1);
    })

    it("XML parsing tests 20" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test4/apiValid.raml"), 0);
    })
    it("XML parsing tests 21" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test4/apiInvalid1.raml"), 1);
    })
    it("XML parsing tests 22" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test4/apiInvalid2.raml"), 1);
    })
    it("XML parsing tests 23" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xmlfacets/test4/apiInvalid3.raml"), 1);
    })
});

// describe('JSON schemes tests', function() {
//     this.timeout(30000);
//
//     it("JSON Scheme test 1" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test1/apiValid.raml"));
//     })
//     it("JSON Scheme test 2" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test1/apiInvalid.raml"), ["Missing required property: name"]);
//     })
//     it("JSON Scheme test 3" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test2/apiValid.raml"));
//     })
//     it("JSON Scheme test 4" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test2/apiInvalid.raml"), ["Missing required property: name"]);
//     })
//     it("JSON Scheme test 5" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test3/apiValid.raml"));
//     })
//     it("JSON Scheme test 6" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test3/apiInvalid.raml"), ["Missing required property: name"]);
//     })
//     it("JSON Scheme test 7" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test4/apiValid.raml"));
//     })
//     it("JSON Scheme test 8" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test4/apiInvalid.raml"), ["Missing required property: name"]);
//     })
//     it("JSON Scheme test 9" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test5/apiValid.raml"));
//     })
//     it("JSON Scheme test 10" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test5/apiInvalid.raml"), ["Missing required property: innerTypeName"]);
//     })
//     it("JSON Scheme test 11" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test6/apiValid.raml"));
//     })
//     it("JSON Scheme test 12" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test6/apiInvalid.raml"), ["Missing required property: innerTypeName"]);
//     })
//     it("JSON Scheme test 13" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test7/apiValid.raml"));
//     })
//     it("JSON Scheme test 14" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test7/apiInvalid.raml"), ["Missing required property: innerTypeName"]);
//     })
//     it("JSON Scheme test 15" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test8/apiValid.raml"));
//     })
//     it("JSON Scheme test 16" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test8/apiInvalid.raml"), ["Missing required property: childName"]);
//     })
//     it("JSON Scheme test 17" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test9/apiValid.raml"));
//     })
//     it("JSON Scheme test 18" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test9/apiInvalid.raml"), ["Missing required property: childName"]);
//     })
//     it("JSON Scheme test 19" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test10/apiValid.raml"));
//     })
//     it("JSON Scheme test 20" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test10/apiInvalid.raml"), ["Missing required property: innerTypeName"]);
//     })
//     it("JSON Scheme test 21" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test11/apiValid.raml"));
//     })
//     it("JSON Scheme test 22" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test11/apiInvalid.raml"), ["Missing required property: innerTypeName"]);
//     })
//     it("JSON Scheme test 23" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test8/apiValid0.raml"));
//     })
//     it("JSON Scheme test 24" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test8/apiInvalid0.raml"), ["Missing required property: childName"]);
//     })
//
//     it("Ignore unknown strig format 1" ,function(done) {
//         util.testErrors(done, util.data("schema/ignoreFormats1.raml"));
//     })
//     it("Ignore unknown number format 1" ,function(done) {
//         util.testErrors(done, util.data("schema/ignoreFormats2.raml"));
//     })
//     it("String instead of object as property definition" ,function(done) {
//         util.testErrors(done, util.data("schema/invalidProperty.raml"),["(Invalid JSON schema: Unexpected value '\\[object Object\\]')|(Schema validation exception: Object\\.keys called on non-object)"]);
//     })
//     it("JOSN schema test Pets 10-3-inline-rtype-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/10-3-inline-rtype-included-schema-filename.raml"));
//     });
//     it("JOSN schema test Pets 10-1-included-rtype-included-schema-flat.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/10-1-included-rtype-included-schema-flat.raml"));
//     });
//     it("JOSN schema test Pets 10-2-included-rtype-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/10-2-included-rtype-included-schema-filename.raml"));
//     });
//     it("JOSN schema test Pets 08-1-included-rtype-included-schema-flat.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/08-1-included-rtype-included-schema-flat.raml"));
//     });
//     it("JOSN schema test Pets 08-2-included-rtype-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/08-2-included-rtype-included-schema-filename.raml"));
//     });
//     it("JOSN schema test Pets 08-3-inline-rtype-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/08-3-inline-rtype-included-schema-filename.raml"));
//     });
//     it("JOSN schema test Pets 08-4-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/08-4-included-schema-filename.raml"));
//     });
//     it("JOSN schema test Pets 10-4-included-schema-filename.raml" ,function(done) {
//         util.testErrors(done, util.data("parser/jsonscheme/test-pets/ramls/10-4-included-schema-filename.raml"));
//     });
// });

describe('XSD schemes tests', function() {
    this.timeout(30000);

    it("XSD Scheme test 1" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test1/apiValid.raml"), 0);
    })
    it("XSD Scheme test 2" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test1/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 3" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test2/apiValid.raml"), 0);
    })
    it("XSD Scheme test 4" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test2/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 5" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test3/apiValid.raml"), 0);
    })
    it("XSD Scheme test 6" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test3/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 7" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test4/apiValid.raml"), 0);
    })
    it("XSD Scheme test 8" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test4/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 9" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test5/apiValid.raml"), 0);
    })
    it("XSD Scheme test 10" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test5/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 11" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test6/apiValid.raml"), 0);
    })
    it("XSD Scheme test 12" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test6/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 13" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test7/apiValid.raml"), 0);
    })
    it("XSD Scheme test 14" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test7/apiInvalid.raml"), 1);
    })
    it("XSD Scheme test 15" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test8/apiValid.raml"), 0);
    })
    it("XSD Scheme test 16" ,function(done) {
        util.testErrorsByNumber(done, util.data("parser/xsdscheme/test8/apiInvalid.raml"), 1);
    })
    it("Empty schemas must not be reported as unresolved" ,function(done) {
        util.testErrors(done, util.data("parser/schemas/emptySchemaTest/api.raml"));
    })
});