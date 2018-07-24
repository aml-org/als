import utils = require("../../common/utils")
import index = require("../../index")
import fs = require("fs")
import chaiModule = require("chai");
var assert:any = <any>chaiModule.assert

describe("Suggestion tests", function() {
    it("test0", function (done) {
        this.timeout(30000);

        testCompletionByEntryEnd('basic/LibraryExample/slack.raml', done, 'CH11\n    ', 'queryParameters, headers, queryString, responses, body, protocols, is, securedBy, displayName');
    });

    it("test1", function (done) {
        testCompletionByEntryEnd('basic/test1.raml', done, 'juan: s', 'string, SimpleType1, SimpleType2, SimpleType');
    });

    it("test2", function (done) {
        testCompletionByEntryStart('basic/test1.raml', done, 'ost:', 'put, post, patch');
    });

    it("test3", function (done) {
        testCompletionByEntryEnd('basic/test2.raml', done, '\n              a', 'age, addresses');
    });

    it("Built-in types reference completion for a property definition", function (done) {
        testCompletionByEntryEnd('basic/test3.raml', done, '\n                type: ', 'TestType, array, union, object, string, boolean, number, integer, date-only, time-only, datetime-only, datetime, file, null');
    });

    it("Built-in types reference completion for a property shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test4.raml', done, '\n            property: ', 'Define Inline, TestType, array, union, object, string, boolean, number, integer, date-only, time-only, datetime-only, datetime, file, null');
    });

    it("Built-in types reference completion for a type definition", function (done) {
        testCompletionByEntryEnd('basic/test5.raml', done, '\n      type: ', 'array, union, object, string, boolean, number, integer, date-only, time-only, datetime-only, datetime, file, null');
    });

    it("Built-in types reference completion for a type shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test6.raml', done, '\n    TestType: ', 'Define Inline, array, union, object, string, boolean, number, integer, date-only, time-only, datetime-only, datetime, file, null');
    });

    it("User-defined types reference completion for a type shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test7.raml', done, '\n  TestType1: Tes', 'TestType, TestTypeUnion, TestTypePrimitive, TestType2, TestTypeWithInheritance');
    });

    it("User-defined types reference completion for a type definition", function (done) {
        testCompletionByEntryEnd('basic/test7.raml', done, '\n    type: Tes', 'TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestTypeWithInheritance');
    });

    it("User-defined types reference completion for a property shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test7.raml', done, '\n      property: Tes', 'TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestTypeWithInheritance, TestType2');
    });

    it("User-defined types reference completion for a property definition", function (done) {
        testCompletionByEntryEnd('basic/test7.raml', done, '\n        type: Tes', 'TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestType2, TestTypeWithInheritance');
    });

    it("User-defined types reference completion for a object union type shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test8.raml', done, '\n  TestType1: TestTypePrimitive | Tes', 'TestTypeObject, TestType, TestTypeUnion, TestTypePrimitive, TestType2, TestTypeWithInheritance');
    });

    it("User-defined types reference completion for a object union type definition", function (done) {
        testCompletionByEntryEnd('basic/test9.raml', done, '\n    type: TestTypeUnion | Tes', 'TestTypeObject, TestType, TestTypeUnion, TestTypePrimitive, TestType2, TestTypeWithInheritance');
    });

    it("User-defined types reference completion for a property union type shortcut definition", function (done) {
        testCompletionByEntryEnd('basic/test9.raml', done, '\n      property: TestTypeUnion | Tes', 'TestTypeObject, TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestTypeWithInheritance, TestType2');
    });

    it("User-defined types reference completion for a property union type definition", function (done) {
        testCompletionByEntryEnd('basic/test9.raml', done, '\n        type: TestTypeUnion | Tes', 'TestTypeObject, TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestTypeWithInheritance, TestType2');
    });

    // #2603 This completion kind DOES NOT WORK in the api-workbench rc2
    it("User-defined types reference completion for a object type shortcut inheritance definition. BUG#2603. SWITCHED OFF. FIXME", function (done) {
        done();
        //testCompletionByEntryEnd('basic/test10.raml', '\n  TestType1: [PartTwo, Tes', 'TestTypeObject, TestType, TestTypeWithInheritance, TestType2');
    });

    // #2609 this completion does not contains all items.
    it("User-defined types reference completion for a object type inheritance definition. BUG#2609 #2612. FIXME", function (done) {
        //Correct test
        //testCompletionByEntryEnd('basic/test10.raml', '\n    type: [PartOne, Tes', 'TestTypeObject, TestType, TestTypeUnion, TestType1, TestTypeWithInheritance, TestType2, TestType3');
        testCompletionByEntryEnd('basic/test10.raml', done, '\n    type: [PartOne, Tes', 'TestTypeObject, TestType1, TestType3, TestTypeWithInheritance');
    });

    //this test contains impossible suggestions
    it("User-defined types reference completion for a property shortcut inheritance definition. BUG#2611. FIXME", function (done) {
        testCompletionByEntryEnd('basic/test10.raml', done, '\n      property: [PartOne, Tes', 'TestTypeObject, TestType1, TestType2, TestType3, TestTypeWithInheritance');
    });

    //this test contains impossible suggestions
    it("User-defined types reference completion for a property inheritance definition. BUG#2612 #2611. FIXME", function (done) {
        //testCompletionByEntryEnd('basic/test10.raml', '\n        type: [PartOne, Tes', 'TestTypeObject, TestType, TestTypeUnion, TestType1, TestTypeWithInheritance, TestType2, TestType3');
        testCompletionByEntryEnd('basic/test10.raml', done, '\n        type: [PartOne, Tes', 'TestTypeObject, TestType1, TestType2, TestType3, TestTypeWithInheritance');
    });

    it("Using Discriminator. BUG#1820 FIXME", function (done) {
        done();
        //testCompletionByEntryEnd('basic/test24.raml', '\n    discriminator: k', 'kind');
    });

    it("User-defined Facets.", function (done) {
        testCompletionByEntryEnd('basic/test25.raml', done, '\n    no', 'noHolidays, notOnlyFutureDates');
    });

    it("Resource types reference completion", function (done) {
        testCompletionByEntryEnd('basic/test11.raml', done, '\n  type: ', 'resourceTypeType, resourceTypeAnother');
    });

    it("Traits reference completion", function (done) {
        testCompletionByEntryEnd('basic/test12.raml', done, '\n    is: ', 'TestTrait, TraitWithBody');
    });

    it("Traits reference completion without used traits.", function (done) {
        testCompletionByEntryEnd('basic/test12.raml', done, '\n      is:  [TestTrait, T', 'TraitWithBody');
    });

    it("Resource type with parameters reference completion", function (done) {
        testCompletionByEntryEnd('basic/test13.raml', done, '\n  type: T', 'TestResorceType, TestResorceTypeTwo');
    });

    it("Resource type parameters type reference completion", function (done) {
        testCompletionByEntryEnd('basic/test13.raml', done, '\n    type:  { TestResorceTypeTwo: {objectName : Tes', 'TestTypeObject, TestType, TestTypeUnion, TestTypePrimitive, TestType1, TestTypeWithInheritance, TestType2, TestType3');
    });

    it("Resource type parameters schema reference completion.", function (done) {
        testCompletionByEntryEnd('basic/test14.raml', done, '\n  type:  { TestResorceType: {objectName : Tes', 'TestSchema');
    });

    it("Resource type parameters function singularize", function (done) {
        testCompletionByEntryEnd('basic/test28.raml', done, '\n            type: <<resourcePathName | !singu', 'singularize');
    });

    it("Resource type parameters function pluralize", function (done) {
        testCompletionByEntryEnd('basic/test28.raml', done, '\n            type: <<resourcePathName | !pl', 'pluralize');
    });

    it("Resource type parameters functions: uppercase, uppercamelcase, upperhyphencase, upperunderscorecase", function (done) {
        testCompletionByEntryEnd('basic/test28.raml', done, '\n            type: <<resourcePathName | !upp', 'uppercase, uppercamelcase, upperhyphencase, upperunderscorecase');
    });

    it("Resource type parameters functions: lowercase, lowercamelcase, lowerhyphencase, lowerunderscorecase", function (done) {
        testCompletionByEntryEnd('basic/test28.raml', done, '\n            type: <<resourcePathName | !low', 'lowercase, lowercamelcase, lowerhyphencase, lowerunderscorecase');
    });

    it("Trait parameters reference completion", function (done) {
        testCompletionByEntryEnd('basic/test15.raml', done, '\n    is: ', 'TestTrait');
    });

    it("Annotation reference completion", function (done) {
        testCompletionByEntryEnd('basic/test16.raml', done, '\n (tes', 'testAnnotation');
    });

    it("Completion for include keyword", function (done) {
        testCompletionByEntryEnd('basic/test17.raml', done, '\n  include: !i', 'include');
    });

    it("Completion for include path", function (done) {
        testCompletionByEntryEnd('basic/test17.raml', done, '\n  comic: !include ./XKCD/s', 'schemas/');
    });

    it("Completion for include files", function (done) {
        testCompletionByEntryEnd('basic/test17.raml', done, '\n  comic: !include ./XKCD/schemas/com', 'comic-schema.json');
    });

    it("Example completion", function (done) {
        testCompletionByEntryEnd('basic/test18.raml', done, '\n      k', 'kind');
    });

    it("Multiple Examples Properties", function (done) {
        testCompletionByEntryEnd('basic/test26.raml', done, '\n        des', 'description');
    });

    it("minLength facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        minLen', 'minLength');
    });

    it("maxLength facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        maxLen', 'maxLength');
    });

    it("example facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        exa', 'example, examples');
    });

    it("enum facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        enu', 'enum');
    });

    it("default facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        defa', 'default');
    });

    it("displayName facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        displ', 'displayName');
    });

    it("description facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        descri', 'description');
    });

    it("required facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        req', 'required');
    });

    it("pattern facet completion", function (done) {
        testCompletionByEntryEnd('basic/test19.raml', done, '\n        patte', 'pattern');
    });

    it("Root nodes: types, traits, title", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nt', 'types, traits, title');
    });

    it("Root nodes: description, documentation", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nd', 'description, documentation');
    });

    it("Root node: version", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nv', 'version');
    });

    it("Root nodes: baseUri, baseUriParameters", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nb', 'baseUri, baseUriParameters');
    });

    it("Root node: protocols", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\np', 'protocols');
    });

    it("Root node: mediaType", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nm', 'mediaType');
    });

    it("Root nodes: schemas, securitySchemes, securedBy", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\ns', 'schemas, securitySchemes, securedBy');
    });

    it("Root node: resourceTypes", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nr', 'resourceTypes');
    });

    it("Root node: annotationTypes", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\na', 'annotationTypes');
    });

    it("Root node: uses", function (done) {
        testCompletionByEntryEnd('basic/test20.raml', done, '\nu', 'uses');
    });

    it("Base URI parameter used in the baseUri node", function (done) {
        testCompletionByEntryEnd('basic/test21.raml', done, '\n  b', 'bucketName: \n    ');
    });

    it("URI parameter used in the resource path", function (done) {
        testCompletionByEntryEnd('basic/test21.raml', done, '\n    se', 'serverId: \n      , serviceId: \n      ');
    });

    it("Allowed protocols: ", function (done) {
        testCompletionByEntryEnd('basic/test21.raml', done, '\nprotocols: H', 'HTTP, HTTPS');
    });

    it("Default Media Types", function (done) {
        testCompletionByEntryEnd('basic/test22.raml', done, '\nmediaType: ', 'application/json, application/xml, application/x-www-form-urlencoded, multipart/form-data');
    });

    it("Several Default Media Types", function (done) {
        testCompletionByEntryEnd('basic/test23.raml', done, '\nmediaType: [multipart/form-data, a', 'application/json, application/xml, application/x-www-form-urlencoded');
    });

    // it("Request body media type 1", function (done) {
    //     testCompletionByEntryEnd('basic/test24.raml', done, '\n      appl', 'application/xml, application/json, application/x-www-form-urlencoded');
    // });

    it("Request body media type 2", function (done) {
        testCompletionByEntryEnd('basic/test24.raml', done, '\n      mul', 'multipart/form-data');
    });

    it("Response body media type", function (done) {
        testCompletionByEntryEnd('basic/test24.raml', done, '\n          app', 'application/json, application/xml');
    });

    it("Default Security", function (done) {
        testCompletionByEntryEnd('basic/test23.raml', done, '\nsecuredBy: [ o', 'oauth_2_0, oauth_1_0');
    });

    it("Resource Security", function (done) {
        testCompletionByEntryEnd('basic/test23.raml', done, '\n  securedBy: o', 'oauth_2_0, oauth_1_0');
    });

    //2613 Completion suggests already used trait items
    it("Resource several Security BUG#2613 FIXME", function (done) {
        //testCompletionByEntryEnd('basic/test23.raml', '\n  securedBy: [oauth_2_0, oa', 'oauth_1_0');
        testCompletionByEntryEnd('basic/test23.raml', done, '\n  securedBy: [oauth_2_0, oa', 'oauth_2_0, oauth_1_0');
    });

    it("Resource node 'is:'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  i', 'is');
    });

    it("Resource node 'type:'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  t', 'type, trace');
    });

    it("Resource nodes 'description, displayName, delete'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  d', 'description, displayName, delete');
    });

    it("Resource node 'get:'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  g', 'get');
    });

    it("Resource nodes 'put, post, patch'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  p', 'put, post, patch');
    });

    it("Resource node 'uriParameters'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  u', 'uriParameters');
    });

    it("Resource node 'options'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  o', 'options');
    });

    it("Resource node 'head'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  h', 'head');
    });

    it("Resource annotation", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n  (annot', 'annotaion');
    });

    it("Method Security", function (done) {
        testCompletionByEntryEnd('basic/test23.raml', done, '\n    securedBy: o', 'oauth_2_0, oauth_1_0');
    });

    //2613 Completion suggests already used trait items
    it("Method several Security  BUG#2613 FIXME", function (done) {
        //testCompletionByEntryEnd('basic/test23.raml', '\n      securedBy: [oauth_2_0, o', 'oauth_1_0');
        testCompletionByEntryEnd('basic/test23.raml', done, '\n      securedBy: [oauth_2_0, o', 'oauth_2_0, oauth_1_0');
    });

    it("Method nodes 'queryString, queryParameters'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    q', 'queryParameters, queryString');
    });

    it("Method request 'headers'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    h', 'headers');
    });

    it("Method request 'headers' items completion", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n      Las', 'Last-Modified, Last-Event-ID');
    });

    it("Method request 'body'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    b', 'body');
    });

    it("Method node 'responses'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    re', 'responses');
    });

    it("Method node 'protocols'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    pro', 'protocols');
    });

    it("Method node 'is'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    i', 'is');
    });

    it("Method node 'description, displayName'", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    d', 'description, displayName');
    });

    it("Method annotation", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n    (annot', 'annotaion');
    });

    it("Response codes", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n      1', '100, 101, 102');
    });

    it("Response headers", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n        he', 'headers');
    });

    it("Method response 'headers' items completion", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n          Las', 'Last-Modified, Last-Event-ID');
    });

    it("Response body", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n        bo', 'body');
    });

    it("Response body mimeTypes", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n          app', 'application/json, application/xml');
    });

    it("Response description", function (done) {
        testCompletionByEntryEnd('basic/test27.raml', done, '\n        des', 'description');
    });

    it("Security Scheme Declaration: type", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    ty', 'type');
    });

    it("Security Scheme Declaration: type value test 1", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    type: O', 'OAuth 2.0, OAuth 1.0');
    });

    it("Security Scheme Declaration: type value test 2", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    type: P', 'Pass Through');
    });

    it("Security Scheme Declaration: type value test 3", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    type: Ba', 'Basic Authentication');
    });

    it("Security Scheme Declaration: type value test 4", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    type: Dig', 'Digest Authentication');
    });

    it("Security Scheme Declaration: type value test 5", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    type: x', 'x-{other}');
    });

    it("Security Scheme Declaration: settings", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    sett', 'settings');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings nodes authorizationGrants, accessTokenUri, authorizationUri", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      au', 'authorizationGrants, accessTokenUri, authorizationUri');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings node scopes", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      sco', 'scopes');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings node authorizationGrants item authorization_code", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      authorizationGrants: [ au', 'authorization_code');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings node authorizationGrants item password", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      authorizationGrants: [ authorization_code, pas', 'password');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings node authorizationGrants item implicit", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      authorizationGrants: [ authorization_code, password, impl', 'implicit');
    });

    it("Security Scheme Declaration: OAuth 2.0 settings node authorizationGrants item client_credentials", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      authorizationGrants: [ authorization_code, password, implicit, cli', 'client_credentials');
    });

    it("Security Scheme Declaration: description, describedBy", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n    descr', 'description, describedBy');
    });

    it("Security Scheme Declaration: OAuth 2.0 describedBy node headers", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      hea', 'headers');
    });

    it("Security Scheme Declaration: OAuth 2.0 describedBy node headers item", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n        X', 'X-Frame-Options');
    });

    it("Security Scheme Declaration: OAuth 2.0 describedBy nodes querySring, queryParameters", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      quer', 'queryParameters, queryString');
    });

    it("Security Scheme Declaration: OAuth 2.0 describedBy node responses", function (done) {
        testCompletionByEntryEnd('basic/test29.raml', done, '\n      respo', 'responses');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node requestTokenUri", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      req', 'requestTokenUri');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node authorizationUri", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      aut', 'authorizationUri');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node tokenCredentialsUri", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      tok', 'tokenCredentialsUri');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node signatures", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      sig', 'signatures');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node signatures item PLAINTEXT", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      signatures: [ PLAI', 'PLAINTEXT');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node signatures item HMAC-SHA1", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      signatures: [ PLAINTEXT, HM', 'HMAC-SHA1');
    });

    it("Security Scheme Declaration: OAuth 1.0 settings node signatures item RSA-SHA1", function (done) {
        testCompletionByEntryEnd('basic/test30.raml', done, '\n      signatures: [ PLAINTEXT, HMAC-SHA1, RS', 'RSA-SHA1');
    });

    it("Declaring Annotation Type node 'type'", function (done) {
        testCompletionByEntryEnd('basic/test31.raml', done, '\n    ty', 'type');
    });

    it("Declaring Annotation Type node 'type' values", function (done) {
        testCompletionByEntryEnd('basic/test31.raml', done, '\n    type: str', 'strong, string');
    });

    it("Declaring Annotation Type node 'displayName'", function (done) {
        testCompletionByEntryEnd('basic/test31.raml', done, '\n    disp', 'displayName');
    });

    it("Declaring Annotation Type node 'allowedTargets'", function (done) {
        testCompletionByEntryEnd('basic/test31.raml', done, '\n    allow', 'allowedTargets');
    });

    it("Declaring Annotation Type node 'allowedTargets' items", function (done) {
        testCompletionByEntryEnd('basic/test31.raml', done, '\n    allowedTargets: Resou', 'Resource, ResourceType');
    });

    it("Typed Fragments: DataType, DocumentationItem", function (done) {
        testCompletionByEntryEnd('basic/test32.raml', done, '\n#%RAML 1.0 D', 'DataType, DocumentationItem');
    });

    it("Typed Fragments: DocumentationItem fragment node 'title'", function (done) {
        testCompletionByEntryEnd('basic/test32.raml', done, '\ntit', 'title');
    });

    it("Typed Fragments: DocumentationItem fragment node 'content'", function (done) {
        testCompletionByEntryEnd('basic/test32.raml', done, '\ncont', 'content');
    });

    it("Typed Fragments: DocumentationItem fragment node 'uses'", function (done) {
        testCompletionByEntryEnd('basic/test32.raml', done, '\nus', 'uses');
    });

    it("Typed Fragments: DataType fragment node 'uses'", function (done) {
        testCompletionByEntryEnd('basic/test33.raml', done, '\nus', 'uses');
    });

    it("Typed Fragments: NamedExample", function (done) {
        testCompletionByEntryEnd('basic/test34.raml', done, '\n#%RAML 1.0 Nam', 'NamedExample');
    });

    it("Typed Fragments: ResourceType", function (done) {
        testCompletionByEntryEnd('basic/test35.raml', done, '\n#%RAML 1.0 ResourceT', 'ResourceType');
    });

    it("Typed Fragments: Trait", function (done) {
        testCompletionByEntryEnd('basic/test36.raml', done, '\n#%RAML 1.0 Tra', 'Trait');
    });

    it("Typed Fragments: SecurityScheme", function (done) {
        testCompletionByEntryEnd('basic/test37.raml', done, '\n#%RAML 1.0 SecuritySc', 'SecurityScheme');
    });

    it("Typed Fragments: SecurityScheme node 'type'", function (done) {
        testCompletionByEntryEnd('basic/test37.raml', done, '\nty', 'type');
    });

    it("Typed Fragments: AnnotationTypeDeclaration", function (done) {
        testCompletionByEntryEnd('basic/test38.raml', done, '\n#%RAML 1.0 AnnotationTyp', 'AnnotationTypeDeclaration');
    });

    it("Typed Fragments: Library", function (done) {
        testCompletionByEntryEnd('basic/test39.raml', done, '\n#%RAML 1.0 Li', 'Library');
    });

    it("Typed Fragments: Overlay", function (done) {
        testCompletionByEntryEnd('basic/test40.raml', done, '\n#%RAML 1.0 Ove', 'Overlay');
    });

    it("Typed Fragments: Overlay node 'extends'", function (done) {
        testCompletionByEntryEnd('basic/test40.raml', done, '\next', 'extends');
    });

    it("Typed Fragments: Extension", function (done) {
        testCompletionByEntryEnd('basic/test41.raml', done, '\n#%RAML 1.0 Exte', 'Extension');
    });

    it("Typed Fragments: Extension node 'extends'", function (done) {
        testCompletionByEntryEnd('basic/test41.raml', done, '\next', 'extends');
    });

    it("test43", function (done) {
        testCompletionByEntryEnd('basic/test43.raml', done, 'X', 'XKCD/');
    });

    it("test60", function (done) {
        testCompletionByEntryEnd('basic/test60_overlay.raml', done, 'test60', '');
    });

    it("test61", function (done) {
        testCompletionByEntryStart('basic/test61.raml', done, '#marker', 'is, type, description, securedBy, uriParameters, displayName, get, put, post, delete, options, head, patch, trace, connect');
    });
});

function offsetForEntry(entry: string, text: string): number {
    return text.indexOf(entry) + entry.length;
}

function resolve(testPath: string): string {
    return utils.resolve(__dirname, '../../../src/test/data/suggestions/' + testPath);
}

var connection;

export function completionByUniqueEntryAsync(filePath: string, entry: string, begin: boolean = false, callback: (result: string[]) => void): void {
    filePath = resolve(filePath);
    let content = fs.readFileSync(filePath).toString();

    let position = begin ? (content.indexOf(entry)) : offsetForEntry(entry, content);

    connection = index.getNodeClientConnection();

    // connection.setLoggerConfiguration({
    //     allowedComponents: [
    //         "NodeProcessServerConnection",
    //         "CompletionManagerModule",
    //         "completionProvider"
    //     ],
    //     maxSeverity: 4,
    //     maxMessageLength: 500
    // });

    connection.documentOpened({
        uri: filePath,
        text: content
    });

    connection.getSuggestions(filePath, position).then(result => {
        connection.documentClosed(filePath);
        callback(result.map((suggestion) => suggestion.displayText || suggestion.text));
    })
}

function testCompletionByEntryStart(testPath: string, done: any, entry: string, expected: string) {
    completionByUniqueEntryAsync(testPath, entry, true, result => {
        try {
            assert(compareProposals(result, expected));

            done();
        } catch(exception) {
            done(exception);
        }
    });
}

function testCompletionByEntryEnd(testPath: string, done: any, entry: string, expected: string) {
    completionByUniqueEntryAsync(testPath, entry, false, result => {
        try {
            assert(compareProposals(result, expected));

            done();
        } catch(exception) {
            done(exception);
        }
    });
}

after(function() {
    if(connection) {
        connection.stop();
    }
});

function compareProposals(actualProposals: string[], expectedStr: string): boolean {
    const expectedProposals: string[] = expectedStr.length === 0 ? [] : expectedStr.split(", ");

    if (actualProposals.length !== expectedProposals.length) {
        console.log("Expected " + expectedProposals.length
            + " proposals, but got " + actualProposals.length);

        console.log("Expected: " + expectedStr + " , actual: " + actualProposals.join(", "));
        return false;
    }

    for (const expectedProposal of expectedProposals) {
        if (actualProposals.indexOf(expectedProposal) == -1) {
            console.log("Can not find expected proposal " + expectedProposal);
            console.log("Expected: " + expectedStr + " , actual: " + actualProposals.join(", "));

            return false;
        }
    }

    return true;
}