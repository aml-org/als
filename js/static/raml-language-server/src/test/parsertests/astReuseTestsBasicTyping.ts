declare var describe;
declare var it;
declare var require;

import util = require("./util");

describe('Typing simulation tests',function() {
    this.timeout(100000);
    
    it("Resource type", function (done) {
        test(done, "ASTReuseTests/test01/api.raml");
    });

    it("Super type", function (done) {
        test(done, "ASTReuseTests/test02/api.raml");
    });

    it("Additional properties for a response mime type", function (done) {
        test(done, "ASTReuseTests/test03/api.raml");
    });

    it("Uri parameter facet value", function (done) {
        test(done, "ASTReuseTests/test04/api.raml");
    });

    it("Resource description", function (done) {
        test(done, "ASTReuseTests/test05/api.raml");
    });

    it("Header name in the method", function (done) {
        test(done, "ASTReuseTests/test06/api.raml");
    });

    it("Trait parameter value", function (done) {
        test(done, "ASTReuseTests/test07/api.raml");
    });

    it("Method securedBy value", function (done) {
        test(done, "ASTReuseTests/test08/api.raml");
    });

    it("Resource annotation", function (done) {
        test(done, "ASTReuseTests/test09/api.raml");
    });

    it("Resource type annotation for a resource with the same annotation", function (done) {
        test(done, "ASTReuseTests/test10/api.raml");
    });

    it("Resource type: method response change 1", function (done) {
        test(done, "ASTReuseTests/test11/api.raml");
    });

    it("Inherited method", function (done) {
        test(done, "ASTReuseTests/test12/api.raml");
    });

    it("Resource type method trait content", function (done) {
        test(done, "ASTReuseTests/test13/api.raml");
    });

    it("Resource type parameter name", function (done) {
        test(done, "ASTReuseTests/test14/api.raml");
    });

    it("Object annotation", function (done) {
        test(done, "ASTReuseTests/test15/api.raml");
    });

    it("Property declaration type", function (done) {
        test(done, "ASTReuseTests/test16/api.raml");
    });

    it("Property declaration type", function (done) {
        test(done, "ASTReuseTests/test17/api.raml");
    });

    it("Resource type: supertype", function (done) {
        test(done, "ASTReuseTests/test18/api.raml");
    });

    it('Parse simple resource type with request body', function (done) {
        test(done, 'parser/resourceType/resType01.raml');
    });

    it('Parse simple resource type with response body', function (done) {
        test(done, 'parser/resourceType/resType02.raml');
    });

    it('Parse resource type with response body inherited from user defined type', function (done) {
        test(done, 'parser/resourceType/resType03.raml');
    });

    it('Parse resource type with request body inherited from user defined type', function (done) {
        test(done, 'parser/resourceType/resType04.raml');
    });

    it('Parse resource type with uri parameters', function (done) {
        test(done, 'parser/resourceType/resType05.raml');
    });

    it('Parse applying resource type with uri parameters', function (done) {
        test(done, 'parser/resourceType/resType06.raml');
    });

    it('Parse resource inherited from simple resource type with request body', function (done) {
        test(done, 'parser/resourceType/resType07.raml');
    });

    it('Parse resource inherited from simple resource type with response body', function (done) {
        test(done, 'parser/resourceType/resType08.raml');
    });

    it('Parse resource inherited from resource type with response body inherited from user defined type', function (done) {
        test(done, 'parser/resourceType/resType09.raml');
    });

    it('Parse resource inherited from resource type with request body inherited from user defined type', function (done) {
        test(done, 'parser/resourceType/resType10.raml');
    });

    it('Parse schema item as parameter', function (done) {
        test(done, 'parser/resourceType/resType17.raml');
    });

    it('Parse type item as parameter', function (done) {
        test(done, 'parser/resourceType/resType18.raml');
    });

    it('Should fail on parameter non exist value', function (done) {
        test(done, 'parser/resourceType/resType19.raml');
    });

    it('Parse all resource types methods defined in the HTTP version 1.1 specification [RFC2616] and its extension, RFC5789 [RFC5789]', function (done) {
        test(done, 'parser/resourceType/resType20.raml');
    });

    it('Parse resource type method with response body', function (done) {
        test(done, 'parser/resourceType/resType15.raml');
    });

    it('Parse resource type method with request body.', function (done) {
        test(done, 'parser/resourceType/resType16.raml');
    });

    it('New methods test 0.8.', function (done) {
        test(done, 'parser/resourceType/resType11.raml');
    });

    it('New methods test 1.0.', function (done) {
        test(done, 'parser/resourceType/resType12.raml');
    });

    it('Disabled body test 0.8.', function (done) {
        test(done, 'parser/resourceType/resType13.raml');
    });

    it('Disabled body test 1.0.', function (done) {
        test(done, 'parser/resourceType/resType14.raml');
    });

    it('Parse trait with header and validate it', function (done) {
        test(done, 'parser/trait/trait01.raml');
    });

    it('Parse trait with query parameter and validate it', function (done) {
        test(done, 'parser/trait/trait02.raml');
    });

    it('Parse trait with body', function (done) {
        test(done, 'parser/trait/trait03.raml');
    });

    it('Parse traits with parameters', function (done) {
        test(done, 'parser/trait/trait04.raml');
    });

    it('Parse traits with boolean parameters', function (done) {
        test(done, 'parser/trait/trait05.raml');
    });

    it('Parse traits with number parameters', function (done) {
        test(done, 'parser/trait/trait06.raml');
    });

    it('Parse object properties', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType01.raml');
    });

    it('Parse minimum number of properties', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType02.raml');
    });

    it('Parse maximum number of properties', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType03.raml');
    });

    it('Parse property required option', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType04.raml');
    });

    it('Parse property default option', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType06.raml');
    });

    it('Parse object types that inherit from other object types', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType07.raml');
    });

    it('Parse object inherit from more than one type', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType08.raml');
    });

    it('Parse shortcut scalar type property declaration', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType09.raml');
    });

    it('Parse maps type declaration', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType10.raml');
    });

    it('Parse restricting the set of valid keys by specifying a regular expression', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType11.raml');
    });

    it('Should not parse alternatively use additionalProperties', function (done) {
        test(done, 'ASTReuseTests/library/objectTypes/oType12.raml');
    });
    
    it('Parse array of scalar types declaration', function (done) {
        test(done, 'ASTReuseTests/library/arrayTypes/aType01.raml');
    });

    it('Parse array of complex types declaration', function (done) {
        test(done, 'ASTReuseTests/library/arrayTypes/aType02.raml');
    });

    it('Parse type inherited from several user defined types declaration', function (done) {
        test(done, 'ASTReuseTests/library/objectTypeInheritance/oti02.raml');
    });

    it('Parse type inherited from several user defined types shortcut declaration', function (done) {
        test(done, 'ASTReuseTests/library/objectTypeInheritance/oti03.raml');
    });

    it('Parse inheritance which should works in the types and in the mimeTypes', function (done) {
        test(done, 'ASTReuseTests/library/objectTypeInheritance/oti04.raml');
    });

    it('Should check that does not allowed to specify current type or type that extends current while declaring property of current type', function (done) {
        test(done, 'ASTReuseTests/library/objectTypeInheritance/oti07.raml');
    });

    it('Parse included json schema', function (done) {
        test(done, 'ASTReuseTests/library/externalTypes/eType01.raml');
    });
});

after(function() {
    util.stopConnection();
});

function test(done, specPath:string) {
    util.startTyping(util.data(specPath).replace(/\\/g,'/'), (error?) => {
        error ? done(error) : done();
    });
}