package org.mulesoft.high.level.dialect.dialects

import amf.ProfileName
import org.mulesoft.high.level.AsyncAPIProfile
import org.mulesoft.high.level.dialect.DialectConf

object AsyncAPIDialect extends DialectConf {

  override val rootUrl: String = "file:///asyncapi/dialect.yaml"

  private val vocabularyContent: String =
    """
      |#%Vocabulary 1.0
      |
      |vocabulary: Asynchronous API
      |base: http://www.asyncapi.com/v1/vocabulary#
      |
      |external:
      |  schema-org: http://schema.org/
      |  oas: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#
      |
      |classTerms:
      |
      |  Message:
      |    displayName: Message
      |    description: |
      |      A message is a piece of information a process will send to a message broker.
      |      It MUST contain headers and payload.
      |
      |  Topic:
      |    displayName: Topic
      |    description: |
      |      A topic is a routing key used by the message broker to deliver messages to the subscribed processes.
      |      Depending on the protocol used, a message MAY include the topic in its headers.
      |
      |  MessageBroker:
      |    displayName: Message Broker
      |    description: |
      |      A message broker is a system in charge of message exchange.
      |      It MAY provide additional features, such as message queueing, storage or processing.
      |
      |   # Extensions here
      |
      |  AsyncAPI:
      |    extends: schema-org.Service
      |    displayName: Async API
      |    description: |
      |       An application programming interface accessible over some kind of asynchronous technology.
      |
      |  StringMapEntry:
      |    displayName: String to string map entry
      |
      |propertyTerms:
      |
      |  topicTemplate:
      |    extends: oas.pathTemplating
      |    displayName: topic template
      |    description: Topic templating refers to the usage of curly braces ({}) to mark a section of a topic as replaceable.
      |
      |  subscribe:
      |    displayName: subscribe
      |    description: A consumer is a process subscribed to a message broker and consumes messages from it.
      |
      |  publish:
      |    displayName: publish
      |    description: A producer is a process publishing messages to a message broker.
      |
      |  keyString:
      |    displayName: key string
      |
      |  valueString:
      |    displayName: value string
      |
      |  mapProperty:
      |    displayName: a string to string map
      |
    """.stripMargin
  private val dialectContent: String =
    """
      |#%Dialect 1.0
      |
      |dialect: AsyncAPI
      |version: 0.6
      |
      |uses:
      |  async: vocabulary.yaml
      |
      |external:
      |  schema-org: http://schema.org/
      |  oas: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1#
      |  json-schema: http://json-schema.org/schema#
      |
      |nodeMappings:
      |
      |  AsyncAPIObject:
      |    classTerm: async.AsyncAPI
      |    mapping:
      |      asyncapi:
      |        propertyTerm: schema-org.version
      |        range: string
      |        pattern: 1.0.0
      |        mandatory: true
      |      baseTopic:
      |        propertyTerm: async.topicTemplate
      |        range: string
      |      info:
      |        propertyTerm: oas.info
      |        range: InfoObject
      |        mandatory: true
      |      servers:
      |        propertyTerm: oas.server
      |        range: ServerObject
      |        allowMultiple: true
      |        patch: upsert
      |      topics:
      |        propertyTerm: oas.topics
      |        range: TopicItemObject
      |        mandatory: true
      |        mapKey: template
      |      security:
      |        propertyTerm: oas.securityRequirements
      |        range: SecurityRequirementObject
      |        allowMultiple: true
      |        patch: upsert
      |      externalDocs:
      |        propertyTerm: schema-org.documentation
      |        range: ExternalDocumentationObject
      |      simpleMap:
      |        propertyTerm: async.mapProperty
      |        range: MapEntry
      |        mapKey: key
      |        mapValue: value
      |
      |  InfoObject:
      |    classTerm: schema-org.CreativeWork
      |    mapping:
      |      title:
      |        propertyTerm: schema-org.name
      |        range: string
      |        mandatory: true
      |      version:
      |        propertyTerm: schema-org.version
      |        range: string
      |        mandatory: true
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      termsOfService:
      |        propertyTerm: schema-org.termsOfService
      |        range: link
      |      contact:
      |        propertyTerm: schema-org.provider
      |        range: ContactObject
      |      license:
      |        propertyTerm: schema-org.license
      |        range: LicenseObject
      |
      |  ContactObject:
      |    classTerm: schema-org.Organization
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      url:
      |        propertyTerm: schema-org.url
      |        range: link
      |      email:
      |        propertyTerm: schema-org.email
      |        range: string
      |        pattern: ^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$
      |
      |  LicenseObject:
      |    classTerm: schema-org.License
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |        mandatory: true
      |      url:
      |        propertyTerm: schema-org.url
      |        range: string
      |
      |  ServerObject:
      |    classTerm: oas.Server
      |    mapping:
      |      url:
      |        propertyTerm: oas.pathTemplating
      |        range: string
      |        mandatory: true
      |        unique: true
      |      scheme:
      |        propertyTerm: oas.urlScheme
      |        range: string
      |        mandatory: true
      |        enum: [amqp, amqps, mqtt, mqtts, ws, wss, stomp, stomps ]
      |        unique: true
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      variables:
      |        propertyTerm: oas.templateVars
      |        range: TemplateVariableObject
      |        mapKey: name
      |
      |  TemplateVariableObject:
      |    classTerm: oas.TemplateVariable
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |        unique: true
      |      enum:
      |        propertyTerm: oas.variableValueEnum
      |        range: string
      |        allowMultiple: true
      |      default:
      |        propertyTerm: oas.variableValueDefault
      |        range: string
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |
      |  TopicItemObject:
      |    classTerm: async.Topic
      |    mapping:
      |      template:
      |        propertyTerm: async.topicTemplate
      |        range: string
      |        mandatory: true
      |        unique: true
      |      subscribe:
      |        propertyTerm: async.subscribe
      |        range: MessageObject
      |      publish:
      |        propertyTerm: async.publish
      |        range: MessageObject
      |
      |  MessageObject:
      |    classTerm: async.Message
      |    mapping:
      |      summary:
      |        propertyTerm: oas.summary
      |        range: string
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      payload:
      |        propertyTerm: oas.schema
      |        range: SchemaObject
      |      tags:
      |        propertyTerm: oas.tag
      |        range: TagObject
      |        allowMultiple: true
      |      externalDocs:
      |        propertyTerm: schema-org.documentation
      |        range: ExternalDocumentationObject
      |      headers:
      |        propertyTerm: oas.header
      |        range: SchemaObject
      |      simpleMap:
      |        propertyTerm: async.mapProperty
      |        range: MapEntry
      |        mapKey: key
      |        mapValue: value
      |
      |  # Temporary
      |  SchemaObject:
      |    classTerm: json-schema.Schema
      |    mapping:
      |      type:
      |        propertyTerm: json-schema.type
      |        range: string
      |        enum: [array, boolean, integer, "null", number, object, string]
      |        allowMultiple: true
      |      id:
      |        propertyTerm: json-schema.id
      |        range: string
      |      "$schema":
      |        propertyTerm: json-schema.version
      |        range: string
      |      title:
      |        propertyTerm: schema-org.title
      |        range: string
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      default:
      |        propertyTerm: json-schema.default
      |        range: any
      |      multipleOf:
      |        propertyTerm: json-schema.multipleOf
      |        range: number
      |      maximum:
      |        propertyTerm: json-schema.maximum
      |        range: number
      |      minimum:
      |        propertyTerm: json-schema.minimum
      |        range: number
      |      exclusiveMinimum:
      |        propertyTerm: json-schema.exclusiveMinimum
      |        range: number
      |      exclusiveMaximum:
      |        propertyTerm: json-schema.exclusiveMaximum
      |        range: number
      |      maxLength:
      |        propertyTerm: json-schema.maxLength
      |        range: number
      |      minLength:
      |        propertyTerm: json-schema.minLength
      |        range: number
      |      pattern:
      |        propertyTerm: json-schema.pattern
      |        range: string
      |      additionalItems:
      |        propertyTerm: json-schema.additionalItems
      |        range: boolean
      |      items:
      |        propertyTerm: json-schema.items
      |        range: SchemaObject
      |        allowMultiple: true
      |      maxItems:
      |        propertyTerm: json-schema.maxItems
      |        range: boolean
      |      minItems:
      |        propertyTerm: json-schema.minItems
      |        range: boolean
      |      uniqueItems:
      |        propertyTerm: json-schema.uniqueItems
      |        range: boolean
      |      maxProperties:
      |        propertyTerm: json-schema.maxProperties
      |        range: number
      |      minProperties:
      |        propertyTerm: json-schema.minProperties
      |        range: number
      |      required:
      |        propertyTerm: json-schema.required
      |        range: string
      |        allowMultiple: true
      |      properties:
      |        propertyTerm: json-schema.properties
      |        range: SchemaObject
      |        mapKey: key
      |      patternProperties:
      |        propertyTerm: json-schema.patternProperties
      |        range: SchemaObject
      |        mapKey: key
      |      key:
      |        propertyTerm: json-schema.propertyKey
      |        range: string
      |      xml:
      |        propertyTerm: oas.xmlMapping
      |        range: XmlObject
      |
      |  TagObject:
      |    classTerm: oas.Tag
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |        mandatory: true
      |        unique: true
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |
      |  ExternalDocumentationObject:
      |    classTerm: oas.ExternalDocumentation
      |    mapping:
      |      url:
      |        propertyTerm: schema-org.url
      |        range: link
      |        mandatory: true
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |
      |  SecuritySchemeObject:
      |    classTerm: oas.SecurityScheme
      |    mapping:
      |      type:
      |        propertyTerm: oas.securitySchemeType
      |        range: string
      |        enum: [userPassword, apiKey, X509, symmetricEncryption, asymmetricEncryption, httpApiKey, http]
      |        mandatory: true
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |        mandatory: false
      |      in:
      |        propertyTerm: oas.securitySchemeTarget
      |        range: string
      |        mandatory: true
      |        enum: [user, password, apiKey, query, header, cookie, httpApiKey]
      |      scheme:
      |        propertyTerm: oas.securitySchemeTargetScheme
      |        range: string
      |        mandatory: true
      |      bearerFormat:
      |        propertyTerm: oas.bearerFormat
      |        range: string
      |
      |  SecurityRequirementObject:
      |    classTerm: oas.SecurityRequirement
      |    mapping:
      |      scheme:
      |        propertyTerm: oas.requiredSecurityScheme
      |        range: SecuritySchemeObject
      |      scopes:
      |        propertyTerm: oas.securityRequirementScope
      |        range: string
      |        allowMultiple: true
      |
      |  XmlObject:
      |    classTerm: oas.XmlMapping
      |    mapping:
      |      name:
      |        propertyTerm: oas.xmlName
      |        range: string
      |      namespace:
      |        propertyTerm: oas.xmlNamespace
      |        range: string
      |      prefix:
      |        propertyTerm: oas.xmlPrefix
      |        range: string
      |      attribute:
      |        propertyTerm: oas.xmlAttribute
      |        range: string
      |      wrapped:
      |        propertyTerm: oas.xmlWrapped
      |        range: boolean
      |
      |  MapEntry:
      |    classTerm: async.StringMapEntry
      |    mapping:
      |      key:
      |        propertyTerm: async.keyString
      |        range: string
      |      value:
      |        propertyTerm: async.valueString
      |        range: string
      |
      |documents:
      |  root:
      |    encodes: AsyncAPIObject
      |    declares:
      |      schemas: SchemaObject
      |      messages: MessageObject
      |      securitySchemes: SecuritySchemeObject
      |  fragments:
      |    encodes:
      |      Schema: SchemaObject
      |      Message: MessageObject
      |      SecurityScheme: SecuritySchemeObject
      |  library:
      |    declares:
      |      schemas: SchemaObject
      |      messages: MessageObject
      |      securitySchemes: SecuritySchemeObject
      |
    """.stripMargin

  override val files: Map[String, String] =
    Map(
      "file:///asyncapi/dialect.yaml"    -> dialectContent,
      "file:///asyncapi/vocabulary.yaml" -> vocabularyContent
    )
  override val profileName: ProfileName = AsyncAPIProfile
}
