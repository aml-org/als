package org.mulesoft.als.suggestions.resources

object Raml10Categories {

    val value =
        """
          |{
          |  "docs" : {
          |    "description": {"is": ["MarkdownString"]},
          |    "displayName": {"parentIs": ["ExampleSpec","TypeDeclaration","Trait","MethodBase","AbstractSecurityScheme","ResourceType","Resource"]},
          |    "example": {"parentIs": ["TypeDeclaration"]},
          |    "usage": {"parentIs": ["Library","Overlay","Extension","Trait","ResourceType"]},
          |    "content": {"parentIs": ["DocumentationItem"]},
          |    "documentation": {"parentIs": ["Api","Overlay","Extension"]},
          |    "title": {"parentIs": ["DocumentationItem"]}
          |  },
          |
          |  "parameters": {
          |    "default": {"parentIs": ["TypeDeclaration"]},
          |    "enum": {"parentIs": ["TypeDeclaration"]},
          |    "maximum": {"parentIs": ["TypeDeclaration"]},
          |    "minimum": {"parentIs": ["TypeDeclaration"]},
          |    "maxLength": {"parentIs": ["TypeDeclaration"]},
          |    "minLength": {"parentIs": ["TypeDeclaration"]},
          |    "required": {"parentIs": ["TypeDeclaration"]},
          |    "baseUriParameters" : {"parentIs":["Api","Overlay","Extension"]},
          |    "uriParameters": {"parentIs": ["ResourceType","ResourceBase","Resource"]},
          |    "headers": {"parentIs": ["Response","Trait","MethodBase","Operation","AbstractSecurityScheme"]},
          |    "queryParameters": {"parentIs": ["Trait","MethodBase","Operation","AbstractSecurityScheme"]}
          |  },
          |
          |  "schemas": {
          |    "schema": {"parentIs": ["TypeDeclaration"]},
          |    "schemas": {"parentIs": ["Library","LibraryBase","Api","Overlay","Extension"]}
          |  },
          |
          |  "root": {
          |    "baseUri": {"parentIs": ["Api","Overlay","Extension"]},
          |    "mediaType": {"parentIs": ["Api","Overlay","Extension"]},
          |    "protocols": {"parentIs": ["Api","Overlay","Extension","Trait","MethodBase"]},
          |    "version": {"parentIs": ["Api","Overlay","Extension"]},
          |    "title": {"parentIs": ["Api","Overlay","Extension"]}
          |  },
          |
          |  "responses": {
          |    "responses": {"parentIs": ["Trait","MethodBase","Operation","AbstractSecurityScheme"]}
          |  },
          |
          |  "response": {"body": {"parentIs":["Response"]}},
          |
          |  "security" : {
          |    "securedBy": {"is": ["SecuritySchemeRef"]},
          |    "securitySchemes": {"parentIs": ["Library","LibraryBase","Api","Overlay","Extension"]},
          |    "accessTokenUri": {"parentIs": ["AbstractSecurityScheme"]},
          |    "authorizationGrants": {"parentIs": ["AbstractSecurityScheme"]},
          |    "authorizationUri": {"parentIs": ["Api","Overlay","Extension"]},
          |    "requestTokenUri": {"parentIs": ["Api","Overlay","Extension"]},
          |    "scopes": {"parentIs": ["Api","Overlay","Extension"]},
          |    "describedBy": {"parentIs": ["AbstractSecurityScheme"]},
          |    "settings": {"parentIs": ["AbstractSecurityScheme"]},
          |    "OAuth 1.0": {"parentIs": ["AbstractSecurityScheme"]},
          |    "OAuth 2.0": {"parentIs": ["AbstractSecurityScheme"]},
          |    "Basic Authentication": {"parentIs": ["AbstractSecurityScheme"]},
          |    "Digest Authentication": {"parentIs": ["AbstractSecurityScheme"]},
          |    "type": {"parentIs": ["AbstractSecurityScheme"]}
          |  },
          |
          |  "types and traits": {
          |    "type" : {"parentIs" : ["ResourceType","ResourceBase","Resource"]},
          |    "is": {"is": ["TraitRef"]},
          |    "resourceTypes": {"parentIs": ["Library","LibraryBase","Api","Overlay","Extension"]},
          |    "traits": {"parentIs": ["Library","LibraryBase","Api","Overlay","Extension"]}
          |  },
          |
          |  "methods" : {
          |    "options": {"is": ["MethodBase"]},
          |    "get": {"is": ["MethodBase"]},
          |    "head": {"is": ["MethodBase"]},
          |    "post": {"is": ["MethodBase"]},
          |    "put": {"is": ["MethodBase"]},
          |    "delete": {"is": ["MethodBase"]},
          |    "trace": {"is": ["MethodBase"]},
          |    "connect": {"is": ["MethodBase"]},
          |    "patch": {"is": ["MethodBase"]}
          |  },
          |
          |  "protocols": {
          |    "HTTP": {"parentIs": ["MethodBase"]},
          |    "HTTPS": {"parentIs": ["MethodBase"]}
          |  },
          |
          |  "body": {
          |    "application/json": {"parentIs": ["TypeDeclaration"]},
          |    "application/x-www-form-urlencoded": {"parentIs": ["TypeDeclaration"]},
          |    "application/xml": {"parentIs": ["TypeDeclaration"]},
          |    "multipart/form-data": {"parentIs": ["TypeDeclaration"]},
          |    "body": {"parentIs":["MethodBase"]}
          |  }
          |}
          |
        """.stripMargin

}
