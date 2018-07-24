declare var describe;
declare var it;
declare var require;

import util = require("./util");

beforeEach(function () {
    util.sleep(100);
});

describe('API parsing', function() {
    this.timeout(30000);

    it('Should parse title', function (done) {
        util.testErrors(done, util.data('parser/api/api01.raml'));
    });

    it('Should parse baseUri', function (done) {
        util.testErrors(done, util.data('parser/api/api02.raml'));
    });

    it('Should parse protocols', function (done) {
        util.testErrors(done, util.data('parser/api/api03.raml'));
    });

    it('Should parse baseUriParameters', function (done) {
        util.testErrors(done, util.data('parser/api/api04.raml'));
    });
    it('Should not allow using sequences in global map declarations', function (done) {
        util.testErrors(done, util.data('parser/api/api01-r.raml'), ["'resourceTypes' should be a map in RAML 1.0"]);
    });
    it('Should parse mediaType', function (done) {
        util.testErrors(done, util.data('parser/api/api05.raml'));
    });

    it('Should fail without title', function (done) {
        util.testErrors(done, util.data('parser/api/api06.raml'), ["Missing required property"]);
    });

    it('Should parse version', function (done) {
        util.testErrors(done, util.data('parser/api/api07.raml'));
    });

    it('Should fail if title is array', function (done) {
        util.testErrors(done, util.data('parser/api/api08.raml'), ["property 'title' must be a string"]);
    });

    it('Should fail if title is map', function (done) {
        util.testErrors(done, util.data('parser/api/api09.raml'), ["property 'title' must be a string"]);
    });

    it('Should succeed if title is longer than 48 chars', function (done) {
        util.testErrors(done, util.data('parser/api/api10.raml'));
    });

    it('Should allow number title', function (done) {
        util.testErrors(done, util.data('parser/api/api11.raml'));
    });

    it('Should fail if there is a root property with wrong displayName', function (done) {
        util.testErrors(done, util.data('parser/api/api12.raml'), ["Unknown node: '\\w+'"]);
    });

    it('Should fail if there is a root property with array', function (done) {
        util.testErrors(done, util.data('parser/api/api13.raml'), ["Unknown node: '\\[\\w+\\]'"]);
    });

    it('Should fail if include not found', function (done) {
        util.testErrors(done, util.data('parser/api/api14.raml'), ["Can not resolve relative.md"]);
    });

    it('RAML 1.0 parser should reject URI parameters declared by sequences', function (done) {
        util.testErrors(done, util.data('parser/api/api15.raml'), ["In RAML 1.0 base uri parameter is not allowed to have sequence as definition"]);
    });

    it('Should parse resource description', function (done) {
        util.testErrors(done, util.data('parser/api/api27.raml'));
    });

    it('Should parse resource description with markdown', function (done) {
        util.testErrors(done, util.data('parser/api/api28.raml'));
    });
});

describe('URI', function() {
    this.timeout(30000);

    it('Should fail when declaring a URI parameter not on the baseUri', function (done) {
        util.testErrors(done, util.data('parser/api/api16.raml'), ["Base uri parameter unused"]);
    });

    it('Should fail when declaring a property inside a URI parameter that is not valid', function (done) {
        util.testErrors(done, util.data('parser/api/api17.raml'), ["specifying unknown facet: 'wrongPropertyName'"]);
    });

    it('Should not fail when declaring an enum with duplicated values', function (done) {
        util.testErrors(done, util.data('parser/api/api18.raml'));
    });

    it('Should fail when declaring a URI parameter with an invalid type', function (done) {
        util.testErrors(done, util.data('parser/api/api19.raml'), ["inheriting from unknown type"]);
    });

    it('Should succeed when declaring a URI parameter with a string type', function (done) {
        util.testErrors(done, util.data('parser/api/api20.raml'));
    });

    it('Should succeed when declaring a URI parameter with a number type', function (done) {
        util.testErrors(done, util.data('parser/api/api21.raml'));
    });

    it('Should succeed when declaring a URI parameter with a integer type', function (done) {
        util.testErrors(done, util.data('parser/api/api22.raml'));
    });

    it('Should succeed when declaring a URI parameter with a date type', function (done) {
        util.testErrors(done, util.data('parser/api/api23.raml'));
    });

    it('Should succeed when declaring a URI parameter with a file type', function (done) {
        util.testErrors(done, util.data('parser/api/api24.raml'));
    });

    it('Should succeed when declaring a URI parameter with a boolean type', function (done) {
        util.testErrors(done, util.data('parser/api/api25.raml'));
    });

    it('Should fail if baseUri value its not really a URI', function (done) {
        util.testErrors(done, util.data('parser/api/api26.raml'), ["Unmatched '{'"]);
    });
});

describe('Resource parsing', function() {
    this.timeout(30000);
    
    it('Should parse simple resource with response body', function(done){
        util.testErrors(done, util.data('parser/resource/res01.raml'));
    });

    it('Should parse simple resource with request body', function(done){
        util.testErrors(done, util.data('parser/resource/res02.raml'));
    });

    it('Should parse resource with response body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resource/res03.raml'));
    });

    it('Should parse simple resource with request body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resource/res04.raml'));
    });

    it('Should parse simple resource with uri parameter', function(done){
        util.testErrors(done, util.data('parser/resource/res05.raml'));
    });

    it('Should parse complex resource with nested resources with several uri parameters', function(done){
        util.testErrors(done, util.data('parser/resource/res06.raml'));
    });

    it('Should parse resource with uri parameter inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resource/res07.raml'));
    });

    it('All parameters declared in uriParameters section should match with any of parameters specified in segment', function(done){
        util.testErrors(done, util.data('parser/resource/res08.raml'), ["Uri parameter unused"]);
    });

    it('Should fail when declaring a URI parameter not on the resource URI', function(done){
        util.testErrors(done, util.data('parser/resource/res13.raml'), ["Uri parameter unused"]);
    });

    it('New methods test 0.8.', function(done){
        util.testErrors(done, util.data('parser/resource/res09.raml'));
    });

    it('Disabled body test 0.8.', function(done){
        util.testErrors(done, util.data('parser/resource/res12.raml'), ["Request body is disabled for 'trace' method"]);
    });
});

describe('Resource type', function(){
    this.timeout(30000);
    
    it('Should parse simple resource type with request body', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType01.raml'));
    });

    it('Should parse simple resource type with response body', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType02.raml'));
    });

    it('Should parse resource type with response body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType03.raml'));
    });

    it('Should parse resource type with request body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType04.raml'));
    });

    it('Should parse resource type with uri parameters', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType05.raml'));
    });

    it('Should parse applying resource type with uri parameters', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType06.raml'));
    });

    it('Should parse resource inherited from simple resource type with request body', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType07.raml'));
    });

    it('Should parse resource inherited from simple resource type with response body', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType08.raml'));
    });

    it('Should parse resource inherited from resource type with response body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType09.raml'));
    });

    it('Should parse resource inherited from resource type with request body inherited from user defined type', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType10.raml'));
    });

    it('Should parse schema item as parameter', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType17.raml'));
    });

    it('Should parse type item as parameter', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType18.raml'));
    });

    it('Should parse all resource types methods defined in the HTTP version 1.1 specification [RFC2616] and its extension, RFC5789 [RFC5789]', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType20.raml'));
    });

    it('Should parse resource type method with response body', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType15.raml'));
    });

    it('Should parse resource type method with request body.', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType16.raml'));
    });

    it('New methods test 0.8.', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType11.raml'));
    });

    it('Disabled body test 0.8.', function(done){
        util.testErrors(done, util.data('parser/resourceType/resType13.raml'), ["Request body is disabled for 'trace' method"]);
    });
});

describe('Method', function(){
    this.timeout(30000);
    
    it('Should parse simple method with response body', function(done){
        util.testErrors(done, util.data('parser/method/meth01.raml'));
    });

    it('Should parse simple method with request body', function(done){
        util.testErrors(done, util.data('parser/method/meth02.raml'));
    });

    it('Only reserved method names are applicable', function(done){
        util.testErrors(done, util.data('parser/method/meth03.raml'), ["Unknown node: 'set'"]);
    });

    it('Should parse header and check that it validates correctly', function(done){
        util.testErrors(done, util.data('parser/method/meth04.raml'));
    });

    it('Should validates query parameters correctly', function(done){
        util.testErrors(done, util.data('parser/method/meth05.raml'));
    });

    it('Should allows to set single protocol value and validate it', function(done){
        util.testErrors(done, util.data('parser/method/meth06.raml'));
    });

    it('Should allows to set array protocol value and validate it 2', function(done){
        util.testErrors(done, util.data('parser/method/meth07.raml'));
    });

    it('Should check that allowed only \'HTTP\' and \'HTTPS\' values', function(done){
        util.testErrors(done, util.data('parser/method/meth08.raml'), ["Invalid value: 'FTP'. Allowed values are: 'HTTP', 'HTTPS'","Invalid value: 'SMTP'. Allowed values are: 'HTTP', 'HTTPS'"]);
    });

    it('Should parse body mimeType', function(done){
        util.testErrors(done, util.data('parser/method/meth09.raml'));
    });

    it('Check that custom mime types are applicable', function(done){
        util.testErrors(done, util.data('parser/method/meth10.raml'));
    });

    it('Should parse pair \'mimeType:type\'.', function(done){
        util.testErrors(done, util.data('parser/method/meth11.raml'));
    });

    it('Should parse a body with \'mimeType\' and keywords \'type\', \'schema\', \'properties\', \'example\'', function(done){
        util.testErrors(done, util.data('parser/method/meth12.raml'));
    });

    it('Test 13', function(done){
        util.testErrors(done, util.data('parser/method/meth13.raml'), ["'queryParameters' is already specified.", "'queryString' is already specified."]);
    });

    it('Test 14', function(done){
        util.testErrors(done, util.data('parser/method/meth14.raml'), ["'queryParameters' is already specified.", "'queryString' is already specified."]);
    });
});

describe('Trait', function(){
    this.timeout(30000);
    
    it('Should parse trait with header and validate it', function(done){
        util.testErrors(done, util.data('parser/trait/trait01.raml'));
    });

    it('Should parse trait with query parameter and validate it', function(done){
        util.testErrors(done, util.data('parser/trait/trait02.raml'));
    });

    it('Should parse trait with body', function(done){
        util.testErrors(done, util.data('parser/trait/trait03.raml'));
    });

    it('Should parse traits with parameters', function(done){
        util.testErrors(done, util.data('parser/trait/trait04.raml'));
    });

    it('Should parse traits with boolean parameters', function(done){
        util.testErrors(done, util.data('parser/trait/trait05.raml'));
    });

    it('Should parse traits with number parameters', function(done){
        util.testErrors(done, util.data('parser/trait/trait06.raml'));
    });
});

describe('Method response', function(){
    this.timeout(30000);
    
    it('Should parse response code', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp01.raml'));
    });

    it('Should parse response body mimeType', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp02.raml'));
    });

    it('Custom mime types are applicable', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp03.raml'));
    });

    it('Should allows to set response body with pair \'mimeType:type\'', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp04.raml'));
    });

    it('Should allows to set response body with \'mimeType\' and keywords \'type\', \'schema\', \'properties\', \'example\'', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp05.raml'));
    });

    it('Should allows to set response body with JSON schema', function(done){
        util.testErrors(done, util.data('parser/methodResponse/methResp06.raml'));
    });
});

describe('Security scheme declaration', function(){
    this.timeout(30000);
    
    it('Should parse security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme01.raml'));
    });

    it('Should parse security scheme description', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme02.raml'));
    });

    it('Should parse security scheme describedBy', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme03.raml'));
    });

    it('Should parse security scheme settings', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme04.raml'));
    });
});

describe('Security Scheme types', function(){
    this.timeout(30000);
    
    it('Should parse OAuth 1.0 security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme05.raml'));
    });

    it('Should parse OAuth 1.0 security scheme type settings', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme06.raml'));
    });

    it('Should parse OAuth 2.0 security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme07.raml'));
    });

    it('Should parse OAuth 2.0 security scheme type settings', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme08.raml'));
    });

    it('Should parse BasicSecurityScheme security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme09.raml'));
    });

    it('Should parse Pass Through security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme11.raml'));
    });

    it('Should parse x- security scheme type', function(done){
        util.testErrors(done, util.data('parser/securitySchemes/qa/securityScheme12.raml'));
    });
});

describe('Type', function(){
    this.timeout(30000);
    
    it('Should parse type inherited from object declaration', function(done){
        util.testErrors(done, util.data('parser/type/t01.raml'));
    });

    it('Should parse type inherited from object shortcut declaration', function(done){
        util.testErrors(done, util.data('parser/type/t02.raml'));
    });

    it('Should parse scalar type inherited from built-in type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t03.raml'));
    });

    it('Should parse string type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t04.raml'));
    });

    it('Should parse number type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t05.raml'));
    });

    it('Should parse integer type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t06.raml'));
    });

    it('Should parse boolean type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t07.raml'));
    });

    it('Should parse array of scalar types declaration', function(done){
        util.testErrors(done, util.data('parser/type/t08.raml'));
    });

    it('Should parse array of complex types declaration', function(done){
        util.testErrors(done, util.data('parser/type/t09.raml'));
    });

    it('Should validate enum values type', function(done){
        util.testErrors(done, util.data('parser/type/t10.raml'), ["integer is expected"]);
    });

    it('Should parse union type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t11.raml'));
    });

    it('Should parse union type shortcut declaration', function(done){
        util.testErrors(done, util.data('parser/type/t12.raml'));
    });

    it('No properties allowed inside union types declaration', function(done){
        util.testErrors(done, util.data('parser/type/t13.raml'));
    });

    it('Should parse scalar type inherited from user defined type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t16.raml'));
    });

    it('Should parse scalar type inherited from user defined type shortcut declaration', function(done){
        util.testErrors(done, util.data('parser/type/t17.raml'));
    });

    it('Parser should not allow additional properties after shortcut inheritance', function(done){
        util.testErrors(done, util.data('parser/type/t18.raml'),["bad indentation of a mapping entry", "inheriting from unknown type"]);
    });

    it('Should parse type inherited from user defined type declaration', function(done){
        util.testErrors(done, util.data('parser/type/t19.raml'));
    });

    it('Should parse type inherited from several user defined types declaration', function(done){
        util.testErrors(done, util.data('parser/type/t20.raml'));
    });

    it('Repeat facet no longer exists', function(done){
        util.testErrors(done, util.data('parser/type/t28.raml'), ["specifying unknown facet: 'repeat'"]);
    });

    it('Custom facets are recognized', function(done){
        util.testErrors(done, util.data('parser/facets/f4.raml'));
    });

    it('Default values for parameter', function(done){
        util.testErrors(done, util.data('parser/type/t29.raml'), ["integer is expected"]);
    });
});

describe('Annotations', function() {
    this.timeout(30000);
    
    it('Should validate annotation parameters and scope', function(done){
        util.testErrors(done, util.data('parser/annotations/a20.raml'));
    });

    it('Should parse datetime annotation instances', function(done){
        util.testErrors(done, util.data('parser/annotations/a33.raml'));
    });

    it('Should allow annotation fragments', function(done){
        util.testErrors(done, util.data('parser/annotations/a34.raml'));
    });
});

describe('Scalar types', function(){
    this.timeout(30000);
    
    it('Should parse string type declaration',function(done){
        util.testErrors(done, util.data('parser/scalarTypes/sType01.raml'));
    });

    it('Should parse number type declaration',function(done){
        util.testErrorsByNumber(done, util.data('parser/scalarTypes/sType02.raml'),1);
    });

    it('Should parse integer type declaration',function(done){
        util.testErrors(done, util.data('parser/scalarTypes/sType03.raml'));
    });

    it('Should parse boolean type declaration',function(done){
        util.testErrors(done, util.data('parser/scalarTypes/sType04.raml'));
    });

    it('Should parse date type declaration:',function(done){
        util.testErrors(done, util.data('parser/scalarTypes/sType05.raml'));
    });

    it('Should parse file type declaration',function(done){
        util.testErrors(done, util.data('parser/scalarTypes/sType06.raml'));
    });
});

describe('Object types', function(){
    this.timeout(30000);
    
    it('Should parse object properties',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType01.raml'));
    });

    it('Should parse minimum number of properties',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType02.raml'),["Initial_comments.minProperties=2' i.e. object properties count should not be less than 2"]);
    });

    it('Should parse maximum number of properties',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType03.raml'), ["'Initial_comments.maxProperties=4' i.e. object properties count should not be more than 4"]);
    });

    it('Should parse property required option',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType04.raml'),["Required property 'comment_id' is missing"]);
    });

    it('Should parse property default option',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType06.raml'));
    });

    it('Should parse object types that inherit from other object types',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType07.raml'));
    });

    it('Should parse object inherit from more than one type',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType08.raml'));
    });

    it('Should parse shortcut scalar type property declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType09.raml'));
    });

    it('Should parse maps type declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType10.raml'));
    });

    it('Should parse restricting the set of valid keys by specifying a regular expression',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType11.raml'));
    });

    it('Should not parse alternatively use additionalProperties',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType12.raml'), ["Value of 'additionalProperties' facet should be boolean"]);
    });

    it('Should parse inline type expression gets expanded to a proper type declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType14.raml'));
    });

    it('Should parse string is default type when nothing else defined',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType15.raml'));
    });

    it('Should parse object is default type when properties is defined',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType16.raml'));
    });

    it('Should parse shorthand for optional property syntax ',function(done){
        util.testErrors(done, util.data('parser/objectTypes/oType17.raml'));
    });
});

describe('Array types', function(){
    this.timeout(30000);
    
    it('Should parse array of scalar types declaration',function(done){
        util.testErrors(done, util.data('parser/arrayTypes/aType01.raml'));
    });

    it('Should parse array of complex types declaration',function(done){
        util.testErrors(done, util.data('parser/arrayTypes/aType02.raml'));
    });
});

describe('Union types', function(){
    this.timeout(30000);
    
    it('Should parse union type declaration',function(done){
        util.testErrors(done, util.data('parser/unionTypes/uType01.raml'));
    });

    it('Should parse union type shortcut declaration',function(done){
        util.testErrors(done, util.data('parser/unionTypes/uType02.raml'));
    });
});

describe('Object type Inheritance', function(){
    this.timeout(30000);
    
    it('Should parse type inherited from user defined type declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypeInheritance/oti01.raml'));
    });

    it('Should parse type inherited from several user defined types declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypeInheritance/oti02.raml'));
    });
    
    it('Should parse type inherited from several user defined types shortcut declaration',function(done){
        util.testErrors(done, util.data('parser/objectTypeInheritance/oti03.raml'));
    });
    
    it('Should parse inheritance which should works in the types and in the mimeTypes',function(done){
        util.testErrors(done, util.data('parser/objectTypeInheritance/oti04.raml'),["Required property 'baseField' is missing"]);
    });
    
    it('Should check that no scalar types allowed inside multiple inheritance declaration ',function(done){
        util.testErrorsByNumber(done, util.data('parser/objectTypeInheritance/oti06.raml'),2);
    });

    it('Should check that does not allowed to specify current type or type that extends current while declaring property of current type',function(done){
        util.testErrorsByNumber(done, util.data('parser/objectTypeInheritance/oti07.raml'),2);
    });
});

describe('External Types', function(){
    this.timeout(30000);
    
    it('Should parse included json schema',function(done){
        util.testErrors(done, util.data('parser/externalTypes/eType01.raml'));
    });

    it('Should parse included xsd schema',function(done){
        util.testErrors(done, util.data('parser/externalTypes/eType02.raml'));
    });

    it('Should parse only xsd/json schemas',function(done){
        util.testErrors(done, util.data('parser/externalTypes/eType03.raml'), ["inheriting from unknown type"]);
    });

    it('Should validate json schemas',function(done){
        util.testErrors(done, util.data('parser/externalTypes/eType05.raml'),["Invalid JSON schema: Cannot tokenize symbol 'p'"]);
    });

    it('Should parse json schemas referencing json schemas',function(done){
        util.testErrors(done, util.data('schema/schemas.raml'));
    });

    it('Should parse json schemas referencing json schemas',function(done){
        util.testErrors(done, util.data('schema/illegalReferenceSchema.raml'),["Invalid JSON schema: Reference could not be resolved"]);
    });
});

describe('Type expressions', function(){
    this.timeout(30000);
    
    it('Should parse simplest type expression',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te01.raml'));
    });

    it('Should parse an array of objects',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te02.raml'));
    });

    it('Should parse an array of scalars types',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te03.raml'));
    });

    it('Should parse a bidimensional array of scalars types',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te04.raml'));
    });

    it('Should parse union type made of members of string OR object',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te05.raml'));
    });

    it('Should parse an array of the above type',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te06.raml'));
    });

    it('Should parse type expression with expected type',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te07.raml'));
    });

    it('Should parse type extended from type expression',function(done){
        util.testErrors(done, util.data('parser/type/typeExpressions/te08.raml'));
    });
});

describe('Modularization', function(){
    this.timeout(30000);
    
    it('Should parse library and allows to use library items.',function(done){
        util.testErrors(done, util.data('parser/modularization/m01.raml'));
    });

    it('Should parse overlay',function(done){
        util.testErrors(done, util.data('parser/modularization/m02_overlay.raml'));
    });

    it('Should not explode on empty extension',function(done){
        util.testErrors(done, util.data('extensions/empty.raml'),["Missing required property 'extends'"]);
    });

    it('Should translate errors from invalid api to extension',function(done){
        util.testErrors(done, util.data('extensions/invalidApiExtension.raml'),["Unknown node: 'unknown'"]);
    });
});

describe("Individual errors",function(){
    this.timeout(30000);
    
    it('Should not allow API fragment',function(done){
        util.testErrors(done, util.data('parser/fragment/ApiInvalid.raml'), ["Redundant fragment name: 'Api'", "Missing required property 'title'"]);
    });
})

describe('Object values for template parameters tests', function() {
    this.timeout(30000);
    
    it("Parameter used in key must have scalar value", function(done){
        util.testErrors(done, util.data("parser/resourceType/resType21.raml"), ["Property 'param' must be a string", "Unknown node: '{\"param\":{\"p1\":null,\"p2\":null}}'"]);
    })

    it("Parameter used inside string value must have scalar value", function(done){
        util.testErrors(done, util.data("parser/resourceType/resType22.raml"), ["Property 'param' must be a string"]);
    })
});

after(function() {
    util.stopConnection();
});