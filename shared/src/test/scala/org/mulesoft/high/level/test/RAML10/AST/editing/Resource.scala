package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

import scala.collection.mutable.ListBuffer


class Resource extends RAML10ASTEditingTest{

    test("Resource. Editing Relative URl of Parent Resource."){
        var projectOpt:Option[IProject] = None
        runAttributeEditingTest("Resource/resource_relative_uri.raml", project => {
            projectOpt = Some(project)
            project.rootASTUnit.rootNode.elements("resources").head.attribute("relativeUri")
        }, "/resource1/{updated}").map(x=>{
            if(x != succeed){
                x
            }
            else{
                val subresource11 = projectOpt.get.rootASTUnit.rootNode.elements("resources").head.elements("resources").head
                val subresource12 = subresource11.elements("resources").head

                var rp1 = subresource11.attribute("relativeUri").get.value.get
                var rp2 = subresource12.attribute("relativeUri").get.value.get
                val expected1 = "/subresource11"
                val expected2 = "/subresource12"
                var messages:Seq[String] = ((expected1,rp1) :: (expected2,rp2) :: Nil).filter(e=>e._1!=e._2)
                    .map(e=>s"Subresource path expected: '${e._1}', but obtained: '${e._2}'")
                if(messages.isEmpty){
                    succeed
                }
                else {
                    fail(messages.mkString("; "))
                }
            }
        })
    }
}
