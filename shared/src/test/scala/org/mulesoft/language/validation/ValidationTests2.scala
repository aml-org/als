//package org.mulesoft.language.validation
//
//import org.mulesoft.language.server.test.parsertests.util.UtilIndex
//
//object ParserTests2 {
//
//var describe = zeroOfMyType
//var it = zeroOfMyType
//var require = zeroOfMyType
//beforeEach( (() =>  {
// util.sleep( 100 )
//
//}) )
//describe( "API parsing", (() =>  {
// this.timeout( 30000 )
// it( "Should parse title", (done =>  {
// util.testErrors( done, util.data( "parser/api/api01.raml" ) )
//
//}) )
// it( "Should parse baseUri", (done =>  {
// util.testErrors( done, util.data( "parser/api/api02.raml" ) )
//
//}) )
// it( "Should parse protocols", (done =>  {
// util.testErrors( done, util.data( "parser/api/api03.raml" ) )
//
//}) )
// it( "Should parse baseUriParameters", (done =>  {
// util.testErrors( done, util.data( "parser/api/api04.raml" ) )
//
//}) )
// it( "Should not allow using sequences in global map declarations", (done =>  {
// util.testErrors( done, util.data( "parser/api/api01-r.raml" ), Array( "'resourceTypes' should be a map in RAML 1.0" ) )
//
//}) )
// it( "Should parse mediaType", (done =>  {
// util.testErrors( done, util.data( "parser/api/api05.raml" ) )
//
//}) )
// it( "Should fail without title", (done =>  {
// util.testErrors( done, util.data( "parser/api/api06.raml" ), Array( "Missing required property" ) )
//
//}) )
// it( "Should parse version", (done =>  {
// util.testErrors( done, util.data( "parser/api/api07.raml" ) )
//
//}) )
// it( "Should fail if title is array", (done =>  {
// util.testErrors( done, util.data( "parser/api/api08.raml" ), Array( "property 'title' must be a string" ) )
//
//}) )
// it( "Should fail if title is map", (done =>  {
// util.testErrors( done, util.data( "parser/api/api09.raml" ), Array( "property 'title' must be a string" ) )
//
//}) )
// it( "Should succeed if title is longer than 48 chars", (done =>  {
// util.testErrors( done, util.data( "parser/api/api10.raml" ) )
//
//}) )
// it( "Should allow number title", (done =>  {
// util.testErrors( done, util.data( "parser/api/api11.raml" ) )
//
//}) )
// it( "Should fail if there is a root property with wrong displayName", (done =>  {
// util.testErrors( done, util.data( "parser/api/api12.raml" ), Array( "Unknown node: '\\w+'" ) )
//
//}) )
// it( "Should fail if there is a root property with array", (done =>  {
// util.testErrors( done, util.data( "parser/api/api13.raml" ), Array( "Unknown node: '\\[\\w+\\]'" ) )
//
//}) )
// it( "Should fail if include not found", (done =>  {
// util.testErrors( done, util.data( "parser/api/api14.raml" ), Array( "Can not resolve relative.md" ) )
//
//}) )
// it( "RAML 1.0 parser should reject URI parameters declared by sequences", (done =>  {
// util.testErrors( done, util.data( "parser/api/api15.raml" ), Array( "In RAML 1.0 base uri parameter is not allowed to have sequence as definition" ) )
//
//}) )
// it( "Should parse resource description", (done =>  {
// util.testErrors( done, util.data( "parser/api/api27.raml" ) )
//
//}) )
// it( "Should parse resource description with markdown", (done =>  {
// util.testErrors( done, util.data( "parser/api/api28.raml" ) )
//
//}) )
//
//}) )
//describe( "URI", (() =>  {
// this.timeout( 30000 )
// it( "Should fail when declaring a URI parameter not on the baseUri", (done =>  {
// util.testErrors( done, util.data( "parser/api/api16.raml" ), Array( "Base uri parameter unused" ) )
//
//}) )
// it( "Should fail when declaring a property inside a URI parameter that is not valid", (done =>  {
// util.testErrors( done, util.data( "parser/api/api17.raml" ), Array( "specifying unknown facet: 'wrongPropertyName'" ) )
//
//}) )
// it( "Should not fail when declaring an enum with duplicated values", (done =>  {
// util.testErrors( done, util.data( "parser/api/api18.raml" ) )
//
//}) )
// it( "Should fail when declaring a URI parameter with an invalid type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api19.raml" ), Array( "inheriting from unknown type" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a string type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api20.raml" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a number type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api21.raml" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a integer type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api22.raml" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a date type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api23.raml" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a file type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api24.raml" ) )
//
//}) )
// it( "Should succeed when declaring a URI parameter with a boolean type", (done =>  {
// util.testErrors( done, util.data( "parser/api/api25.raml" ) )
//
//}) )
// it( "Should fail if baseUri value its not really a URI", (done =>  {
// util.testErrors( done, util.data( "parser/api/api26.raml" ), Array( "Unmatched '{'" ) )
//
//}) )
//
//}) )
//describe( "Resource parsing", (() =>  {
// this.timeout( 30000 )
// it( "Should parse simple resource with response body", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res01.raml" ) )
//
//}) )
// it( "Should parse simple resource with request body", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res02.raml" ) )
//
//}) )
// it( "Should parse resource with response body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res03.raml" ) )
//
//}) )
// it( "Should parse simple resource with request body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res04.raml" ) )
//
//}) )
// it( "Should parse simple resource with uri parameter", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res05.raml" ) )
//
//}) )
// it( "Should parse complex resource with nested resources with several uri parameters", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res06.raml" ) )
//
//}) )
// it( "Should parse resource with uri parameter inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res07.raml" ) )
//
//}) )
// it( "All parameters declared in uriParameters section should match with any of parameters specified in segment", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res08.raml" ), Array( "Uri parameter unused" ) )
//
//}) )
// it( "Should fail when declaring a URI parameter not on the resource URI", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res13.raml" ), Array( "Uri parameter unused" ) )
//
//}) )
// it( "New methods test 0.8.", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res09.raml" ) )
//
//}) )
// it( "Disabled body test 0.8.", (done =>  {
// util.testErrors( done, util.data( "parser/resource/res12.raml" ), Array( "Request body is disabled for 'trace' method" ) )
//
//}) )
//
//}) )
//describe( "Resource type", (() =>  {
// this.timeout( 30000 )
// it( "Should parse simple resource type with request body", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType01.raml" ) )
//
//}) )
// it( "Should parse simple resource type with response body", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType02.raml" ) )
//
//}) )
// it( "Should parse resource type with response body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType03.raml" ) )
//
//}) )
// it( "Should parse resource type with request body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType04.raml" ) )
//
//}) )
// it( "Should parse resource type with uri parameters", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType05.raml" ) )
//
//}) )
// it( "Should parse applying resource type with uri parameters", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType06.raml" ) )
//
//}) )
// it( "Should parse resource inherited from simple resource type with request body", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType07.raml" ) )
//
//}) )
// it( "Should parse resource inherited from simple resource type with response body", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType08.raml" ) )
//
//}) )
// it( "Should parse resource inherited from resource type with response body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType09.raml" ) )
//
//}) )
// it( "Should parse resource inherited from resource type with request body inherited from user defined type", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType10.raml" ) )
//
//}) )
// it( "Should parse schema item as parameter", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType17.raml" ) )
//
//}) )
// it( "Should parse type item as parameter", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType18.raml" ) )
//
//}) )
// it( "Should parse all resource types methods defined in the HTTP version 1.1 specification [RFC2616] and its extension, RFC5789 [RFC5789]", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType20.raml" ) )
//
//}) )
// it( "Should parse resource type method with response body", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType15.raml" ) )
//
//}) )
// it( "Should parse resource type method with request body.", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType16.raml" ) )
//
//}) )
// it( "New methods test 0.8.", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType11.raml" ) )
//
//}) )
// it( "Disabled body test 0.8.", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType13.raml" ), Array( "Request body is disabled for 'trace' method" ) )
//
//}) )
//
//}) )
//describe( "Method", (() =>  {
// this.timeout( 30000 )
// it( "Should parse simple method with response body", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth01.raml" ) )
//
//}) )
// it( "Should parse simple method with request body", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth02.raml" ) )
//
//}) )
// it( "Only reserved method names are applicable", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth03.raml" ), Array( "Unknown node: 'set'" ) )
//
//}) )
// it( "Should parse header and check that it validates correctly", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth04.raml" ) )
//
//}) )
// it( "Should validates query parameters correctly", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth05.raml" ) )
//
//}) )
// it( "Should allows to set single protocol value and validate it", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth06.raml" ) )
//
//}) )
// it( "Should allows to set array protocol value and validate it 2", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth07.raml" ) )
//
//}) )
// it( "Should check that allowed only 'HTTP' and 'HTTPS' values", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth08.raml" ), Array( "Invalid value: 'FTP'. Allowed values are: 'HTTP', 'HTTPS'", "Invalid value: 'SMTP'. Allowed values are: 'HTTP', 'HTTPS'" ) )
//
//}) )
// it( "Should parse body mimeType", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth09.raml" ) )
//
//}) )
// it( "Check that custom mime types are applicable", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth10.raml" ) )
//
//}) )
// it( "Should parse pair 'mimeType:type'.", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth11.raml" ) )
//
//}) )
// it( "Should parse a body with 'mimeType' and keywords 'type', 'schema', 'properties', 'example'", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth12.raml" ) )
//
//}) )
// it( "Test 13", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth13.raml" ), Array( "'queryParameters' is already specified.", "'queryString' is already specified." ) )
//
//}) )
// it( "Test 14", (done =>  {
// util.testErrors( done, util.data( "parser/method/meth14.raml" ), Array( "'queryParameters' is already specified.", "'queryString' is already specified." ) )
//
//}) )
//
//}) )
//describe( "Trait", (() =>  {
// this.timeout( 30000 )
// it( "Should parse trait with header and validate it", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait01.raml" ) )
//
//}) )
// it( "Should parse trait with query parameter and validate it", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait02.raml" ) )
//
//}) )
// it( "Should parse trait with body", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait03.raml" ) )
//
//}) )
// it( "Should parse traits with parameters", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait04.raml" ) )
//
//}) )
// it( "Should parse traits with boolean parameters", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait05.raml" ) )
//
//}) )
// it( "Should parse traits with number parameters", (done =>  {
// util.testErrors( done, util.data( "parser/trait/trait06.raml" ) )
//
//}) )
//
//}) )
//describe( "Method response", (() =>  {
// this.timeout( 30000 )
// it( "Should parse response code", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp01.raml" ) )
//
//}) )
// it( "Should parse response body mimeType", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp02.raml" ) )
//
//}) )
// it( "Custom mime types are applicable", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp03.raml" ) )
//
//}) )
// it( "Should allows to set response body with pair 'mimeType:type'", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp04.raml" ) )
//
//}) )
// it( "Should allows to set response body with 'mimeType' and keywords 'type', 'schema', 'properties', 'example'", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp05.raml" ) )
//
//}) )
// it( "Should allows to set response body with JSON schema", (done =>  {
// util.testErrors( done, util.data( "parser/methodResponse/methResp06.raml" ) )
//
//}) )
//
//}) )
//describe( "Security scheme declaration", (() =>  {
// this.timeout( 30000 )
// it( "Should parse security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme01.raml" ) )
//
//}) )
// it( "Should parse security scheme description", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme02.raml" ) )
//
//}) )
// it( "Should parse security scheme describedBy", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme03.raml" ) )
//
//}) )
// it( "Should parse security scheme settings", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme04.raml" ) )
//
//}) )
//
//}) )
//describe( "Security Scheme types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse OAuth 1.0 security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme05.raml" ) )
//
//}) )
// it( "Should parse OAuth 1.0 security scheme type settings", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme06.raml" ) )
//
//}) )
// it( "Should parse OAuth 2.0 security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme07.raml" ) )
//
//}) )
// it( "Should parse OAuth 2.0 security scheme type settings", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme08.raml" ) )
//
//}) )
// it( "Should parse BasicSecurityScheme security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme09.raml" ) )
//
//}) )
// it( "Should parse Pass Through security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme11.raml" ) )
//
//}) )
// it( "Should parse x- security scheme type", (done =>  {
// util.testErrors( done, util.data( "parser/securitySchemes/qa/securityScheme12.raml" ) )
//
//}) )
//
//}) )
//describe( "Type", (() =>  {
// this.timeout( 30000 )
// it( "Should parse type inherited from object declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t01.raml" ) )
//
//}) )
// it( "Should parse type inherited from object shortcut declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t02.raml" ) )
//
//}) )
// it( "Should parse scalar type inherited from built-in type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t03.raml" ) )
//
//}) )
// it( "Should parse string type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t04.raml" ) )
//
//}) )
// it( "Should parse number type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t05.raml" ) )
//
//}) )
// it( "Should parse integer type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t06.raml" ) )
//
//}) )
// it( "Should parse boolean type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t07.raml" ) )
//
//}) )
// it( "Should parse array of scalar types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t08.raml" ) )
//
//}) )
// it( "Should parse array of complex types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t09.raml" ) )
//
//}) )
// it( "Should validate enum values type", (done =>  {
// util.testErrors( done, util.data( "parser/type/t10.raml" ), Array( "integer is expected" ) )
//
//}) )
// it( "Should parse union type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t11.raml" ) )
//
//}) )
// it( "Should parse union type shortcut declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t12.raml" ) )
//
//}) )
// it( "No properties allowed inside union types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t13.raml" ) )
//
//}) )
// it( "Should parse scalar type inherited from user defined type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t16.raml" ) )
//
//}) )
// it( "Should parse scalar type inherited from user defined type shortcut declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t17.raml" ) )
//
//}) )
// it( "Parser should not allow additional properties after shortcut inheritance", (done =>  {
// util.testErrors( done, util.data( "parser/type/t18.raml" ), Array( "bad indentation of a mapping entry", "inheriting from unknown type" ) )
//
//}) )
// it( "Should parse type inherited from user defined type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t19.raml" ) )
//
//}) )
// it( "Should parse type inherited from several user defined types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/type/t20.raml" ) )
//
//}) )
// it( "Repeat facet no longer exists", (done =>  {
// util.testErrors( done, util.data( "parser/type/t28.raml" ), Array( "specifying unknown facet: 'repeat'" ) )
//
//}) )
// it( "Custom facets are recognized", (done =>  {
// util.testErrors( done, util.data( "parser/facets/f4.raml" ) )
//
//}) )
// it( "Default values for parameter", (done =>  {
// util.testErrors( done, util.data( "parser/type/t29.raml" ), Array( "integer is expected" ) )
//
//}) )
//
//}) )
//describe( "Annotations", (() =>  {
// this.timeout( 30000 )
// it( "Should validate annotation parameters and scope", (done =>  {
// util.testErrors( done, util.data( "parser/annotations/a20.raml" ) )
//
//}) )
// it( "Should parse datetime annotation instances", (done =>  {
// util.testErrors( done, util.data( "parser/annotations/a33.raml" ) )
//
//}) )
// it( "Should allow annotation fragments", (done =>  {
// util.testErrors( done, util.data( "parser/annotations/a34.raml" ) )
//
//}) )
//
//}) )
//describe( "Scalar types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse string type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/scalarTypes/sType01.raml" ) )
//
//}) )
// it( "Should parse number type declaration", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/scalarTypes/sType02.raml" ), 1 )
//
//}) )
// it( "Should parse integer type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/scalarTypes/sType03.raml" ) )
//
//}) )
// it( "Should parse boolean type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/scalarTypes/sType04.raml" ) )
//
//}) )
// it( "Should parse date type declaration:", (done =>  {
// util.testErrors( done, util.data( "parser/scalarTypes/sType05.raml" ) )
//
//}) )
// it( "Should parse file type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/scalarTypes/sType06.raml" ) )
//
//}) )
//
//}) )
//describe( "Object types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse object properties", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType01.raml" ) )
//
//}) )
// it( "Should parse minimum number of properties", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType02.raml" ), Array( "Initial_comments.minProperties=2' i.e. object properties count should not be less than 2" ) )
//
//}) )
// it( "Should parse maximum number of properties", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType03.raml" ), Array( "'Initial_comments.maxProperties=4' i.e. object properties count should not be more than 4" ) )
//
//}) )
// it( "Should parse property required option", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType04.raml" ), Array( "Required property 'comment_id' is missing" ) )
//
//}) )
// it( "Should parse property default option", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType06.raml" ) )
//
//}) )
// it( "Should parse object types that inherit from other object types", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType07.raml" ) )
//
//}) )
// it( "Should parse object inherit from more than one type", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType08.raml" ) )
//
//}) )
// it( "Should parse shortcut scalar type property declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType09.raml" ) )
//
//}) )
// it( "Should parse maps type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType10.raml" ) )
//
//}) )
// it( "Should parse restricting the set of valid keys by specifying a regular expression", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType11.raml" ) )
//
//}) )
// it( "Should not parse alternatively use additionalProperties", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType12.raml" ), Array( "Value of 'additionalProperties' facet should be boolean" ) )
//
//}) )
// it( "Should parse inline type expression gets expanded to a proper type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType14.raml" ) )
//
//}) )
// it( "Should parse string is default type when nothing else defined", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType15.raml" ) )
//
//}) )
// it( "Should parse object is default type when properties is defined", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType16.raml" ) )
//
//}) )
// it( "Should parse shorthand for optional property syntax ", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypes/oType17.raml" ) )
//
//}) )
//
//}) )
//describe( "Array types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse array of scalar types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/arrayTypes/aType01.raml" ) )
//
//}) )
// it( "Should parse array of complex types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/arrayTypes/aType02.raml" ) )
//
//}) )
//
//}) )
//describe( "Union types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse union type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/unionTypes/uType01.raml" ) )
//
//}) )
// it( "Should parse union type shortcut declaration", (done =>  {
// util.testErrors( done, util.data( "parser/unionTypes/uType02.raml" ) )
//
//}) )
//
//}) )
//describe( "Object type Inheritance", (() =>  {
// this.timeout( 30000 )
// it( "Should parse type inherited from user defined type declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypeInheritance/oti01.raml" ) )
//
//}) )
// it( "Should parse type inherited from several user defined types declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypeInheritance/oti02.raml" ) )
//
//}) )
// it( "Should parse type inherited from several user defined types shortcut declaration", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypeInheritance/oti03.raml" ) )
//
//}) )
// it( "Should parse inheritance which should works in the types and in the mimeTypes", (done =>  {
// util.testErrors( done, util.data( "parser/objectTypeInheritance/oti04.raml" ), Array( "Required property 'baseField' is missing" ) )
//
//}) )
// it( "Should check that no scalar types allowed inside multiple inheritance declaration ", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/objectTypeInheritance/oti06.raml" ), 2 )
//
//}) )
// it( "Should check that does not allowed to specify current type or type that extends current while declaring property of current type", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/objectTypeInheritance/oti07.raml" ), 2 )
//
//}) )
//
//}) )
//describe( "External Types", (() =>  {
// this.timeout( 30000 )
// it( "Should parse included json schema", (done =>  {
// util.testErrors( done, util.data( "parser/externalTypes/eType01.raml" ) )
//
//}) )
// it( "Should parse included xsd schema", (done =>  {
// util.testErrors( done, util.data( "parser/externalTypes/eType02.raml" ) )
//
//}) )
// it( "Should parse only xsd/json schemas", (done =>  {
// util.testErrors( done, util.data( "parser/externalTypes/eType03.raml" ), Array( "inheriting from unknown type" ) )
//
//}) )
// it( "Should validate json schemas", (done =>  {
// util.testErrors( done, util.data( "parser/externalTypes/eType05.raml" ), Array( "Invalid JSON schema: Cannot tokenize symbol 'p'" ) )
//
//}) )
// it( "Should parse json schemas referencing json schemas", (done =>  {
// util.testErrors( done, util.data( "schema/schemas.raml" ) )
//
//}) )
// it( "Should parse json schemas referencing json schemas", (done =>  {
// util.testErrors( done, util.data( "schema/illegalReferenceSchema.raml" ), Array( "Invalid JSON schema: Reference could not be resolved" ) )
//
//}) )
//
//}) )
//describe( "Type expressions", (() =>  {
// this.timeout( 30000 )
// it( "Should parse simplest type expression", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te01.raml" ) )
//
//}) )
// it( "Should parse an array of objects", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te02.raml" ) )
//
//}) )
// it( "Should parse an array of scalars types", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te03.raml" ) )
//
//}) )
// it( "Should parse a bidimensional array of scalars types", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te04.raml" ) )
//
//}) )
// it( "Should parse union type made of members of string OR object", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te05.raml" ) )
//
//}) )
// it( "Should parse an array of the above type", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te06.raml" ) )
//
//}) )
// it( "Should parse type expression with expected type", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te07.raml" ) )
//
//}) )
// it( "Should parse type extended from type expression", (done =>  {
// util.testErrors( done, util.data( "parser/type/typeExpressions/te08.raml" ) )
//
//}) )
//
//}) )
//describe( "Modularization", (() =>  {
// this.timeout( 30000 )
// it( "Should parse library and allows to use library items.", (done =>  {
// util.testErrors( done, util.data( "parser/modularization/m01.raml" ) )
//
//}) )
// it( "Should parse overlay", (done =>  {
// util.testErrors( done, util.data( "parser/modularization/m02_overlay.raml" ) )
//
//}) )
// it( "Should not explode on empty extension", (done =>  {
// util.testErrors( done, util.data( "extensions/empty.raml" ), Array( "Missing required property 'extends'" ) )
//
//}) )
// it( "Should translate errors from invalid api to extension", (done =>  {
// util.testErrors( done, util.data( "extensions/invalidApiExtension.raml" ), Array( "Unknown node: 'unknown'" ) )
//
//}) )
//
//}) )
//describe( "Individual errors", (() =>  {
// this.timeout( 30000 )
// it( "Should not allow API fragment", (done =>  {
// util.testErrors( done, util.data( "parser/fragment/ApiInvalid.raml" ), Array( "Redundant fragment name: 'Api'", "Missing required property 'title'" ) )
//
//}) )
//
//}) )
//describe( "Object values for template parameters tests", (() =>  {
// this.timeout( 30000 )
// it( "Parameter used in key must have scalar value", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType21.raml" ), Array( "Property 'param' must be a string", "Unknown node: '{\"param\":{\"p1\":null,\"p2\":null}}'" ) )
//
//}) )
// it( "Parameter used inside string value must have scalar value", (done =>  {
// util.testErrors( done, util.data( "parser/resourceType/resType22.raml" ), Array( "Property 'param' must be a string" ) )
//
//}) )
//
//}) )
//after( (() =>  {
// util.stopConnection()
//
//}) )
//
//
//}
