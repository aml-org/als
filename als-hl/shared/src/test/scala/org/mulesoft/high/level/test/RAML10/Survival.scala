package org.mulesoft.high.level.test.RAML10

import org.mulesoft.high.level.implementation.Project


class Survival extends RAML10ASTTest{

    test("test 001"){
        runTest( "survival/test001/api.raml", project => {

            project match {
                case p:Project => succeed
                case _ => fail("Successfull parsing is expected")
            }
        })
    }
}
