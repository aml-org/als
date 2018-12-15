package org.mulesoft.als.suggestions.resources

object Oas20Categories {

  val value =
    """
          |{
          |  "docs" : {
          |    "description": {"is": ["MarkdownString"]},
          |	   "termsOfService": {"parentIs": ["InfoObject"]},
          |	   "version": {"parentIs": ["InfoObject"]},
          |	   "contact": {"parentIs": ["InfoObject"]},
          |	   "license": {"parentIs": ["InfoObject"]},
          |    "title": {"parentIs": ["InfoObject", "SchemaObject"]},
          |	   "name": {"parentIs": ["ContactObject", "LicenseObject", "TagObject"]},
          |	   "url": {"parentIs": ["ContactObject", "LicenseObject", "ExternalDocumentationObject"]},
          |	   "email": {"parentIs": ["ContactObject"]},
          |	   "tags": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "summary": {"parentIs": ["OperationObject"]},
          |	   "info": {"parentIs": ["SwaggerObject"]},
          |	   "example" : {"parentIs":["CommonParameterObject", "SchemaObject"]},
          |    "examples" : {"parentIs":["ResponseObject"]},
          |	   "externalDocs": {"parentIs": ["SwaggerObject", "SchemaObject", "OperationObject"]}
          |  },
          |
          |  "parameters": {
          |    "parameters": {"parentIs": ["SwaggerObject", "PathItemObject", "OperationObject"]},
          |    "type": {"parentIs": ["ItemsObject"]},
          |    "format": {"parentIs": ["ItemsObject"]},
          |    "items": {"parentIs": ["ItemsObject"]},
          |    "collectionFormat": {"parentIs": ["ItemsObject"]},
          |    "default": {"parentIs": ["ItemsObject"]},
          |    "maximum": {"parentIs": ["ItemsObject"]},
          |	   "exclusiveMaximum" : {"parentIs":["ItemsObject"]},
          |	   "minimum" : {"parentIs":["ItemsObject"]},
          |	   "exclusiveMinimum" : {"parentIs":["ItemsObject"]},
          |   	"maxLength" : {"parentIs":["ItemsObject"]},
          |	   "minLength" : {"parentIs":["ItemsObject"]},
          |	   "pattern" : {"parentIs":["ItemsObject"]},
          |	   "maxItems" : {"parentIs":["ItemsObject"]},
          |   	"minItems" : {"parentIs":["ItemsObject"]},
          |	   "uniqueItems" : {"parentIs":["ItemsObject"]},
          |	   "enum" : {"parentIs":["ItemsObject"]},
          |	   "multipleOf" : {"parentIs":["ItemsObject"]},
          |	   "name" : {"parentIs":["ParameterObject", "HeaderObject"]},
          |    "in" : {"parentIs":["ParameterObject"]},
          |    "required" : {"parentIs":["ParameterObject"]},
          |    "allowEmptyValue" : {"parentIs":["CommonParameterObject"]},
          |	   "headers" : {"parentIs": ["ResponseObject"]}
          |  },
          |
          |  "schemas": {
          |    "schema" : {"parentIs":["BodyParameterObject", "ResponseObject"]},
          |    "definitions": {"parentIs": ["SwaggerObject"]},
          |    "$ref": {"parentIs": ["SchemaObject"]},
          |    "required": {"parentIs": ["SchemaObject"]},
          |	   "properties": {"parentIs": ["SchemaObject"]},
          |   	"$allOf": {"parentIs": ["SchemaObject"]},
          |	   "additionalProperties": {"parentIs": ["SchemaObject"]},
          |	   "items": {"parentIs": ["SchemaObject"]},
          |	   "discriminator": {"parentIs": ["SchemaObject"]},
          |   	"readOnly": {"parentIs": ["SchemaObject"]},
          |   	"xml": {"parentIs": ["SchemaObject"]},
          |   	"type": {"parentIs": ["SchemaObject"]},
          |	   "attribute": {"parentIs": ["XMLObject"]},
          |   	"wrapped": {"parentIs": ["XMLObject"]},
          |	   "name": {"parentIs": ["XMLObject"]},
          |	   "namespace": {"parentIs": ["XMLObject"]},
          |	   "prefix": {"parentIs": ["XMLObject"]}
          |  },
          |
          |  "root": {
          |    "host": {"parentIs": ["SwaggerObject"]},
          |    "basePath": {"parentIs": ["SwaggerObject"]},
          |	   "paths": {"parentIs": ["SwaggerObject"]},
          |	   "consumes": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "produces": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "schemes": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "securityDefinitions": {"parentIs": ["SwaggerObject"]}
          |  },
          |
          |  "responses": {
          |    "responses": {"parentIs": ["SwaggerObject", "OperationObject"]}
          |  },
          |
          |  "security" : {
          |    "security": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "type": {"parentIs": ["SecurityDefinitionObject"]},
          |    "name": {"parentIs": ["ApiKey"]},
          |    "in": {"parentIs": ["ApiKey"]},
          |    "flow": {"parentIs": ["OAuth2"]},
          |    "authorizationUrl": {"parentIs": ["OAuth2"]},
          |    "tokenUrl": {"parentIs": ["OAuth2"]},
          |    "scopes": {"parentIs": ["OAuth2"]}
          |  },
          |
          |
          |  "methods" : {
          |    "options": {"is": ["OperationObject"]},
          |    "get": {"is": ["OperationObject"]},
          |    "head": {"is": ["OperationObject"]},
          |    "post": {"is": ["OperationObject"]},
          |    "put": {"is": ["OperationObject"]},
          |    "delete": {"is": ["OperationObject"]},
          |    "trace": {"is": ["OperationObject"]},
          |    "connect": {"is": ["OperationObject"]},
          |    "patch": {"is": ["OperationObject"]},
          |    "operationId": {"parentIs": ["OperationObject"]},
          |	   "deprecated": {"parentIs": ["OperationObject"]}
          |  },
          |
          |
          |  "protocols": {
          |    "HTTP": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "HTTPS": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "WSS": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "WS": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "http": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "https": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |	   "wss": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "ws": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |  },
          |
          |  "body": {
          |    "application/json": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "application/x-www-form-urlencoded": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "application/xml": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "multipart/form-data": {"parentIs": ["SwaggerObject", "OperationObject"]},
          |    "body": {"parentIs":["SwaggerObject", "OperationObject"]}
          |  }
          |}
        """.stripMargin

}
