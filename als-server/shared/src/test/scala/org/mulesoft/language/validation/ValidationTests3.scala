//package org.mulesoft.language.validation
//
//import org.mulesoft.language.server.test.parsertests.util.UtilIndex
//
//object ParserTests3{
//
//var describe = zeroOfMyType
//var it = zeroOfMyType
//var require = zeroOfMyType
//beforeEach( (() =>  {
// util.sleep( 100 )
//
//}) )
//describe( "XML parsing tests", (() =>  {
// this.timeout( 30000 )
// it( "XML parsing tests 1", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test1/apiValid.raml" ), 0 )
//
//}) )
// it( "XML parsing tests 2", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test1/apiInvalid1.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 3", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test1/apiInvalid2.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 4", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test1/apiInvalid3.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 5", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test1/apiInvalid4.raml" ), 0 )
//
//}) )
// it( "XML parsing tests 6", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiValid.raml" ), 0 )
//
//}) )
// it( "XML parsing tests 7", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiInvalid1.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 8", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiInvalid2.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 9", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiInvalid3.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 10", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiInvalid4.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 11", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test2/apiInvalid5.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 12", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiValid.raml" ), 0 )
//
//}) )
// it( "XML parsing tests 13", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid1.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 14", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid2.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 15", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid3.raml" ), 2 )
//
//}) )
// it( "XML parsing tests 16", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid4.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 17", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid5.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 18", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid6.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 19", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test3/apiInvalid7.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 20", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test4/apiValid.raml" ), 0 )
//
//}) )
// it( "XML parsing tests 21", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test4/apiInvalid1.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 22", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test4/apiInvalid2.raml" ), 1 )
//
//}) )
// it( "XML parsing tests 23", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xmlfacets/test4/apiInvalid3.raml" ), 1 )
//
//}) )
//
//}) )
//describe( "XSD schemes tests", (() =>  {
// this.timeout( 30000 )
// it( "XSD Scheme test 1", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test1/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 2", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test1/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 3", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test2/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 4", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test2/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 5", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test3/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 6", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test3/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 7", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test4/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 8", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test4/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 9", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test5/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 10", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test5/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 11", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test6/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 12", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test6/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 13", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test7/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 14", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test7/apiInvalid.raml" ), 1 )
//
//}) )
// it( "XSD Scheme test 15", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test8/apiValid.raml" ), 0 )
//
//}) )
// it( "XSD Scheme test 16", (done =>  {
// util.testErrorsByNumber( done, util.data( "parser/xsdscheme/test8/apiInvalid.raml" ), 1 )
//
//}) )
// it( "Empty schemas must not be reported as unresolved", (done =>  {
// util.testErrors( done, util.data( "parser/schemas/emptySchemaTest/api.raml" ) )
//
//}) )
//
//}) )
//
//
//}
