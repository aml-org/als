package org.mulesoft.als.suggestions.test.raml10

class FragmentTypeTest extends RAML10Test {

    test("'#%RAML' completion"){
        this.runTest("fragmentType/test001.raml", Set(" ResourceType", " Trait", " AnnotationTypeDeclaration", " DataType", " DocumentationItem", " NamedExample", " Extension", " SecurityScheme", " Overlay", " Library"))
    }

    test("'#%RAML ' completion"){
        this.runTest("fragmentType/test002.raml", Set("ResourceType", "Trait", "AnnotationTypeDeclaration", "DataType", "DocumentationItem", "NamedExample", "Extension", "SecurityScheme", "Overlay", "Library"))
    }

    test("'#%RAML D' completion"){
        this.runTest("fragmentType/test003.raml", Set("DataType", "DocumentationItem"))
    }
}
