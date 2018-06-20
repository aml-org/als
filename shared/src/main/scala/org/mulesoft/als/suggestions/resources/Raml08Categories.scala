package org.mulesoft.als.suggestions.resources

object Raml08Categories {

    val value =
        """
          |{
          |  "docs" : {
          |    "description": {"is": ["MarkdownString"]},
          |    "displayName": {"parentIs": ["Parameter","Resource","ResourceType","Trait"]},
          |    "example": {"parentIs": ["Parameter","BodyLike","XMLBody","JSONBody"]},
          |    "usage": {"parentIs": ["ResourceType","Trait"]},
          |    "content": {"parentIs": ["DocumentationItem"]},
          |    "documentation": {"parentIs": ["Api"]},
          |    "title": {"parentIs": ["DocumentationItem"]}
          |  },
          |
          |  "parameters": {
          |    "default": {"parentIs": ["Parameter"]},
          |    "enum": {"parentIs": ["Parameter"]},
          |    "maximum": {"parentIs": ["Parameter"]},
          |    "minimum": {"parentIs": ["Parameter"]},
          |    "maxLength": {"parentIs": ["Parameter"]},
          |    "minLength": {"parentIs": ["Parameter"]},
          |    "required": {"parentIs": ["Parameter"]},
          |    "baseUriParameters" : {"parentIs":["Api","Resource","ResourceType","MethodBase","Trait","AbstractSecurityScheme"]},
          |    "uriParameters": {"parentIs": ["Api","Resource","ResourceType"]},
          |    "headers": {"parentIs": ["Response","MethodBase","Trait","AbstractSecurityScheme"]},
          |    "queryParameters": {"parentIs": ["MethodBase","Trait","AbstractSecurityScheme"]},
          |    "type": {"parentIs": ["Parameter"]}
          |  },
          |
          |  "schemas": {
          |    "schema": {"parentIs": ["BodyLike","XMLBody","JSONBody"]},
          |    "schemas": {"parentIs": ["Api"]}
          |  },
          |
          |  "root": {
          |    "baseUri": {"parentIs": ["Api"]},
          |    "mediaType": {"parentIs": ["Api"]},
          |    "protocols": {"parentIs": ["Api","MethodBase","Trait","AbstractSecurityScheme"]},
          |    "version": {"parentIs": ["Api"]},
          |    "title": {"parentIs": ["Api"]}
          |  },
          |
          |  "responses": {
          |    "responses": {"parentIs": ["MethodBase","Trait","AbstractSecurityScheme"]}
          |  },
          |
          |  "response": {"body": {"parentIs":["Response"]}},
          |
          |  "security" : {
          |    "securedBy": {"is": ["SecuritySchemeRef"]},
          |    "securitySchemes": {"parentIs": ["Api"]},
          |    "accessTokenUri": {"parentIs": ["AbstractSecurityScheme"]},
          |    "authorizationGrants": {"parentIs": ["AbstractSecurityScheme"]},
          |    "authorizationUri": {"parentIs": ["Api"]},
          |    "requestTokenUri": {"parentIs": ["Api"]},
          |    "scopes": {"parentIs": ["Api"]},
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
          |    "type" : {"parentIs" : ["Resource","ResourceType"]},
          |    "is": {"is": ["TraitRef"]},
          |    "resourceTypes": {"parentIs": ["Api"]},
          |    "traits": {"parentIs": ["Api"]}
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
          |    "HTTP": {"parentIs": [ "MethodBase"]},
          |    "HTTPS": {"parentIs": ["MethodBase"]}
          |  },
          |
          |  "body": {
          |    "application/json": {"parentIs": ["BodyLike"]},
          |    "application/x-www-form-urlencoded": {"parentIs": ["BodyLike"]},
          |    "application/xml": {"parentIs": ["BodyLike"]},
          |    "multipart/form-data": {"parentIs": ["BodyLike"]},
          |    "body": {"parentIs":["MethodBase"]}
          |  }
          |}
          |
        """.stripMargin

}
