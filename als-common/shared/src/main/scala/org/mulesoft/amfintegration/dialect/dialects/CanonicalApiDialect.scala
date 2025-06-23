package org.mulesoft.amfintegration.dialect.dialects

object CanonicalApiDialect extends InMemoryDialect {
  override val name: String = "webapi-spec"
  override val fileContent: String = CanonicalApiContent.part1 + CanonicalApiContent.part2
}

private object CanonicalApiContent {
  val part1: String = """#%Dialect 1.0
                        |
                        |dialect: WebAPI Spec
                        |version: 1.0
                        |external:
                        |  shacl: http://www.w3.org/ns/shacl#
                        |  rdfs: http://www.w3.org/2000/01/rdf-schema#
                        |  raml-shapes: http://vocabularies/data_shapes.yaml#
                        |  security: http://vocabularies/security.yaml#
                        |  apiBinding: http://vocabularies/api_binding.yaml#
                        |  data: http://vocabularies/data_model.yaml#
                        |  doc: http://vocabularies/aml_doc.yaml#
                        |  apiContract: http://vocabularies/api_contract.yaml#
                        |  core: http://vocabularies/core.yaml#
                        |  meta: http://vocabularies/aml_meta.yaml#
                        |
                        |nodeMappings:
                        |
                        |  API:
                        |    classTerm: apiContract.API
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      identifier:
                        |        propertyTerm: core.identifier
                        |        range: string
                        |      server:
                        |        propertyTerm: apiContract.server
                        |        range: Server
                        |        allowMultiple: true
                        |      accepts:
                        |        propertyTerm: apiContract.accepts
                        |        range: string
                        |        allowMultiple: true
                        |      contentType:
                        |        propertyTerm: apiContract.contentType
                        |        range: string
                        |        allowMultiple: true
                        |      scheme:
                        |        propertyTerm: apiContract.scheme
                        |        range: string
                        |        allowMultiple: true
                        |      version:
                        |        propertyTerm: core.version
                        |        range: string
                        |      termsOfService:
                        |        propertyTerm: core.termsOfService
                        |        range: string
                        |      provider:
                        |        propertyTerm: core.provider
                        |        range: Organization
                        |      license:
                        |        propertyTerm: core.license
                        |        range: License
                        |      documentation:
                        |        propertyTerm: core.documentation
                        |        range: CreativeWork
                        |        allowMultiple: true
                        |      endpoint:
                        |        propertyTerm: apiContract.endpoint
                        |        range: EndPoint
                        |        allowMultiple: true
                        |      security:
                        |        propertyTerm: security.security
                        |        range: SecurityRequirement
                        |        allowMultiple: true
                        |      tag:
                        |        propertyTerm: apiContract.tag
                        |        range: Tag
                        |        allowMultiple: true
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  APIKeySettings:
                        |    classTerm: security.ApiKeySettings
                        |    extends: Settings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      in:
                        |        propertyTerm: security.in
                        |        range: string
                        |
                        |
                        |  AbstractDeclaration:
                        |    classTerm: doc.AbstractDeclaration
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      dataNode:
                        |        propertyTerm: doc.dataNode
                        |        range: DataNodeUnion
                        |      variable:
                        |        propertyTerm: doc.variable
                        |        range: string
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Amqp091ChannelBinding:
                        |    classTerm: apiBinding.Amqp091ChannelBinding
                        |    extends: ChannelBinding
                        |    mapping:
                        |      is:
                        |        propertyTerm: apiBinding.is
                        |        range: string
                        |      exchange:
                        |        propertyTerm: apiBinding.exchange
                        |        range: Amqp091ChannelExchange
                        |      queue:
                        |        propertyTerm: apiBinding.queue
                        |        range: Amqp091Queue
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  Amqp091ChannelExchange:
                        |    classTerm: apiBinding.Amqp091ChannelExchange
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      durable:
                        |        propertyTerm: apiBinding.durable
                        |        range: boolean
                        |      autoDelete:
                        |        propertyTerm: apiBinding.autoDelete
                        |        range: boolean
                        |      vhost:
                        |        propertyTerm: apiBinding.vhost
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Amqp091MessageBinding:
                        |    classTerm: apiBinding.Amqp091MessageBinding
                        |    extends: MessageBinding
                        |    mapping:
                        |      contentEncoding:
                        |        propertyTerm: apiBinding.contentEncoding
                        |        range: string
                        |      messageType:
                        |        propertyTerm: apiBinding.messageType
                        |        range: string
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  Amqp091OperationBinding:
                        |    classTerm: apiBinding.Amqp091OperationBinding
                        |    extends: OperationBinding
                        |    mapping:
                        |      expiration:
                        |        propertyTerm: apiBinding.expiration
                        |        range: integer
                        |      userId:
                        |        propertyTerm: apiBinding.userId
                        |        range: string
                        |      cc:
                        |        propertyTerm: apiBinding.cc
                        |        range: string
                        |        allowMultiple: true
                        |      priority:
                        |        propertyTerm: apiBinding.priority
                        |        range: integer
                        |      deliveryMode:
                        |        propertyTerm: apiBinding.deliveryMode
                        |        range: integer
                        |      mandatory:
                        |        propertyTerm: apiBinding.mandatory
                        |        range: boolean
                        |      bcc:
                        |        propertyTerm: apiBinding.bcc
                        |        range: string
                        |        allowMultiple: true
                        |      replyTo:
                        |        propertyTerm: apiBinding.replyTo
                        |        range: string
                        |      timestamp:
                        |        propertyTerm: apiBinding.timestamp
                        |        range: boolean
                        |      ack:
                        |        propertyTerm: apiBinding.ack
                        |        range: boolean
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  Amqp091Queue:
                        |    classTerm: apiBinding.Amqp091ChannelQueue
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      durable:
                        |        propertyTerm: apiBinding.durable
                        |        range: boolean
                        |      exclusive:
                        |        propertyTerm: apiBinding.exclusive
                        |        range: boolean
                        |      autoDelete:
                        |        propertyTerm: apiBinding.autoDelete
                        |        range: boolean
                        |      vhost:
                        |        propertyTerm: apiBinding.vhost
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  AnnotationTypeFragment:
                        |    classTerm: apiContract.AnnotationTypeDeclarationFragment
                        |    extends: Fragment
                        |
                        |
                        |  AnyShape:
                        |    classTerm: raml-shapes.AnyShape
                        |    extends: Shape
                        |    mapping:
                        |      documentation:
                        |        propertyTerm: core.documentation
                        |        range: CreativeWork
                        |      XmlSerialization:
                        |        propertyTerm: raml-shapes.xmlSerialization
                        |        range: XMLSerializer
                        |      comment:
                        |        propertyTerm: core.comment
                        |        range: string
                        |      examples:
                        |        propertyTerm: apiContract.examples
                        |        range: Example
                        |        allowMultiple: true
                        |      raw:
                        |        propertyTerm: doc.raw
                        |        range: string
                        |      referenceId:
                        |        propertyTerm: doc.reference-id
                        |        range: link
                        |      location:
                        |        propertyTerm: doc.location
                        |        range: string
                        |
                        |
                        |  ArrayNode:
                        |    classTerm: data.Array
                        |    extends: DataNode
                        |    mapping:
                        |      member:
                        |        propertyTerm: rdfs.member
                        |        range: DataNodeUnion
                        |        allowMultiple: true
                        |
                        |
                        |  ArrayShape:
                        |    classTerm: raml-shapes.ArrayShape
                        |    extends: AnyShape
                        |    mapping:
                        |      items:
                        |        propertyTerm: raml-shapes.items
                        |        range: DataShapesUnion
                        |      contains:
                        |        propertyTerm: raml-shapes.contains
                        |        range: DataShapesUnion
                        |      minCount:
                        |        propertyTerm: shacl.minCount
                        |        range: integer
                        |      maxCount:
                        |        propertyTerm: shacl.maxCount
                        |        range: integer
                        |      uniqueItems:
                        |        propertyTerm: raml-shapes.uniqueItems
                        |        range: boolean
                        |      collectionFormat:
                        |        propertyTerm: raml-shapes.collectionFormat
                        |        range: string
                        |      unevaluatedItems:
                        |        propertyTerm: raml-shapes.unevaluatedItems
                        |        range: boolean
                        |      unevaluatedItemsSchema:
                        |        propertyTerm: raml-shapes.unevaluatedItemsSchema
                        |        range: DataShapesUnion
                        |      qualifiedMinCount:
                        |        propertyTerm: shacl.qualifiedMinCount
                        |        range: integer
                        |      qualifiedMaxCount:
                        |        propertyTerm: shacl.qualifiedMaxCount
                        |        range: integer
                        |
                        |
                        |  AsyncAPI:
                        |    classTerm: apiContract.AsyncAPI
                        |    extends: API
                        |
                        |
                        |  BaseUnit:
                        |    classTerm: doc.Unit
                        |    mapping:
                        |
                        |      location:
                        |        propertyTerm: doc.location
                        |        range: string
                        |      version:
                        |        propertyTerm: doc.version
                        |        range: string
                        |      references:
                        |        propertyTerm: doc.references
                        |        range: ParsedUnitUnion
                        |        allowMultiple: true
                        |      usage:
                        |        propertyTerm: doc.usage
                        |        range: string
                        |      describedBy:
                        |        propertyTerm: meta.describedBy
                        |        range: link
                        |      root:
                        |        propertyTerm: doc.root
                        |        range: boolean
                        |
                        |
                        |  Callback:
                        |    classTerm: apiContract.Callback
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      expression:
                        |        propertyTerm: apiContract.expression
                        |        range: string
                        |      endpoint:
                        |        propertyTerm: apiContract.endpoint
                        |        range: EndPoint
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ChannelBinding:
                        |    classTerm: apiBinding.ChannelBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ChannelBindings:
                        |    classTerm: apiBinding.ChannelBindings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      bindings:
                        |        propertyTerm: apiBinding.bindings
                        |        range: ChannelBindingUnion
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  CorrelationId:
                        |    classTerm: core.CorrelationId
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      location:
                        |        propertyTerm: core.location
                        |        range: string
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  CreativeWork:
                        |    classTerm: core.CreativeWork
                        |    mapping:
                        |      url:
                        |        propertyTerm: core.url
                        |        range: link
                        |      title:
                        |        propertyTerm: core.title
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  CustomDomainProperty:
                        |    classTerm: doc.DomainProperty
                        |    mapping:
                        |      domain:
                        |        propertyTerm: rdfs.domain
                        |        range: link
                        |        allowMultiple: true
                        |      schema:
                        |        propertyTerm: raml-shapes.schema
                        |        range: DataShapesUnion
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |
                        |
                        |  DataNode:
                        |    classTerm: data.Node
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  DataTypeFragment:
                        |    classTerm: raml-shapes.DataTypeFragment
                        |    extends: Fragment
                        |
                        |
                        |  DiscriminatorValueMapping:
                        |    classTerm: raml-shapes.DiscriminatorValueMapping
                        |    mapping:
                        |      discriminatorValue:
                        |        propertyTerm: raml-shapes.discriminatorValue
                        |        range: string
                        |      discriminatorValueTarget:
                        |        propertyTerm: raml-shapes.discriminatorValueTarget
                        |        range: DataShapesUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Document:
                        |    classTerm: doc.Document
                        |    extends: BaseUnit
                        |    mapping:
                        |      encodes:
                        |        propertyTerm: doc.encodes
                        |        range: DomainElementUnion
                        |      declares:
                        |        propertyTerm: doc.declares
                        |        range: DomainElementUnion
                        |        allowMultiple: true
                        |
                        |
                        |  DocumentExtension:
                        |    classTerm: doc.DocumentExtension
                        |    extends: BaseUnit
                        |    mapping:
                        |      encodes:
                        |        propertyTerm: doc.encodes
                        |        range: DomainElementUnion
                        |      declares:
                        |        propertyTerm: doc.declares
                        |        range: DomainElementUnion
                        |        allowMultiple: true
                        |
                        |
                        |  DocumentationItemFragment:
                        |    classTerm: apiContract.UserDocumentationFragment
                        |    extends: Fragment
                        |
                        |
                        |  DomainExtension:
                        |    classTerm: apiContract.DomainExtension
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.extensionName
                        |        range: string
                        |      definedBy:
                        |        propertyTerm: doc.definedBy
                        |        range: CustomDomainProperty
                        |      extension:
                        |        propertyTerm: doc.extension
                        |        range: DataNodeUnion
                        |      element:
                        |        propertyTerm: doc.element
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  EmptyBinding:
                        |    classTerm: apiBinding.EmptyBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Encoding:
                        |    classTerm: apiContract.Encoding
                        |    mapping:
                        |      propertyName:
                        |        propertyTerm: apiContract.propertyName
                        |        range: string
                        |      contentType:
                        |        propertyTerm: apiContract.contentType
                        |        range: string
                        |      header:
                        |        propertyTerm: apiContract.header
                        |        range: Parameter
                        |        allowMultiple: true
                        |      style:
                        |        propertyTerm: apiContract.style
                        |        range: string
                        |      explode:
                        |        propertyTerm: apiContract.explode
                        |        range: boolean
                        |      allowReserved:
                        |        propertyTerm: apiContract.allowReserved
                        |        range: boolean
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  EndPoint:
                        |    classTerm: apiContract.EndPoint
                        |    mapping:
                        |      extends:
                        |        propertyTerm: doc.extends
                        |        typeDiscriminatorName: type
                        |        typeDiscriminator:
                        |          AppliedResourceType: ParametrizedResourceType
                        |          AppliedTrait: ParametrizedTrait
                        |        range:
                        |          - ParametrizedResourceType
                        |          - ParametrizedTrait
                        |        allowMultiple: true
                        |
                        |      path:
                        |        propertyTerm: apiContract.path
                        |        range: string
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      summary:
                        |        propertyTerm: core.summary
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      supportedOperation:
                        |        propertyTerm: apiContract.supportedOperation
                        |        range: Operation
                        |        allowMultiple: true
                        |      parameter:
                        |        propertyTerm: apiContract.parameter
                        |        range: Parameter
                        |        allowMultiple: true
                        |      payload:
                        |        propertyTerm: apiContract.payload
                        |        range: Payload
                        |        allowMultiple: true
                        |      servers:
                        |        propertyTerm: apiContract.server
                        |        range: Server
                        |        allowMultiple: true
                        |      security:
                        |        propertyTerm: security.security
                        |        range: SecurityRequirement
                        |        allowMultiple: true
                        |      binding:
                        |        propertyTerm: apiBinding.binding
                        |        range: ChannelBindings
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Example:
                        |    classTerm: apiContract.Example
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      displayName:
                        |        propertyTerm: core.displayName
                        |        range: string
                        |      guiSummary:
                        |        propertyTerm: apiContract.guiSummary
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      externalValue:
                        |        propertyTerm: doc.externalValue
                        |        range: string
                        |      strict:
                        |        propertyTerm: doc.strict
                        |        range: boolean
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |      structuredValue:
                        |        propertyTerm: doc.structuredValue
                        |        range: DataNodeUnion
                        |      raw:
                        |        propertyTerm: doc.raw
                        |        range: string
                        |      referenceId:
                        |        propertyTerm: doc.reference-id
                        |        range: link
                        |      location:
                        |        propertyTerm: doc.location
                        |        range: string
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Extension:
                        |    classTerm: apiContract.Extension
                        |    extends: Document
                        |
                        |
                        |  ExternalDomainElement:
                        |    classTerm: doc.ExternalDomainElement
                        |    mapping:
                        |      raw:
                        |        propertyTerm: doc.raw
                        |        range: string
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ExternalFragment:
                        |    classTerm: doc.ExternalFragment
                        |    extends: Fragment
                        |
                        |
                        |  ExternalSourceElement:
                        |    classTerm: doc.ExternalSource
                        |    mapping:
                        |      raw:
                        |        propertyTerm: doc.raw
                        |        range: string
                        |      referenceId:
                        |        propertyTerm: doc.reference-id
                        |        range: link
                        |      location:
                        |        propertyTerm: doc.location
                        |        range: string
                        |
                        |
                        |  FileShape:
                        |    classTerm: raml-shapes.FileShape
                        |    extends: AnyShape
                        |    mapping:
                        |      fileType:
                        |        propertyTerm: raml-shapes.fileType
                        |        range: string
                        |        allowMultiple: true
                        |      pattern:
                        |        propertyTerm: shacl.pattern
                        |        range: string
                        |      minLength:
                        |        propertyTerm: shacl.minLength
                        |        range: integer
                        |      maxLength:
                        |        propertyTerm: shacl.maxLength
                        |        range: integer
                        |      minInclusive:
                        |        propertyTerm: shacl.minInclusive
                        |        range: double
                        |      max.Inclusive:
                        |        propertyTerm: shacl.maxInclusive
                        |        range: double
                        |      min.Exclusive:
                        |        propertyTerm: shacl.minExclusive
                        |        range: boolean
                        |      max.Exclusive:
                        |        propertyTerm: shacl.maxExclusive
                        |        range: boolean
                        |      format:
                        |        propertyTerm: raml-shapes.format
                        |        range: string
                        |      multipleOf:
                        |        propertyTerm: raml-shapes.multipleOf
                        |        range: double
                        |
                        |
                        |  Fragment:
                        |    classTerm: doc.Fragment
                        |    extends: BaseUnit
                        |    mapping:
                        |      encodes:
                        |        propertyTerm: doc.encodes
                        |        range: DomainElementUnion
                        |
                        |
                        |  HttpAPIKeySettings:
                        |    classTerm: security.HttpApiKeySettings
                        |    extends: Settings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      in:
                        |        propertyTerm: security.in
                        |        range: string
                        |
                        |
                        |  HttpMessageBinding:
                        |    classTerm: apiBinding.HttpMessageBinding
                        |    extends: MessageBinding
                        |    mapping:
                        |      headers:
                        |        propertyTerm: apiBinding.headers
                        |        range: DataShapesUnion
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  HttpOperationBinding:
                        |    classTerm: apiBinding.HttpOperationBinding
                        |    extends: OperationBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.operationType
                        |        range: string
                        |      method:
                        |        propertyTerm: apiBinding.method
                        |        range: string
                        |      query:
                        |        propertyTerm: apiBinding.query
                        |        range: DataShapesUnion
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  HttpSettings:
                        |    classTerm: security.HttpSettings
                        |    extends: Settings
                        |    mapping:
                        |      scheme:
                        |        propertyTerm: security.scheme
                        |        range: string
                        |      bearerFormat:
                        |        propertyTerm: security.bearerFormat
                        |        range: string
                        |
                        |
                        |  IriTemplate:
                        |    classTerm: apiContract.IriTemplateMapping
                        |    mapping:
                        |      templateVariable:
                        |        propertyTerm: apiContract.templateVariable
                        |        range: string
                        |      linkExpression:
                        |        propertyTerm: apiContract.linkExpression
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  KafkaMessageBinding:
                        |    classTerm: apiBinding.KafkaMessageBinding
                        |    extends: MessageBinding
                        |    mapping:
                        |      key:
                        |        propertyTerm: apiBinding.messageKey
                        |        range: DataShapesUnion
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  KafkaOperationBinding:
                        |    classTerm: apiBinding.KafkaOperationBinding
                        |    extends: OperationBinding
                        |    mapping:
                        |      groupId:
                        |        propertyTerm: apiBinding.groupId
                        |        range: DataShapesUnion
                        |      clientId:
                        |        propertyTerm: apiBinding.clientId
                        |        range: DataShapesUnion
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  License:
                        |    classTerm: core.License
                        |    mapping:
                        |      url:
                        |        propertyTerm: core.url
                        |        range: link
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  LinkNode:
                        |    classTerm: data.Link
                        |    extends: DataNode
                        |    mapping:
                        |      value:
                        |        propertyTerm: data.value
                        |        range: string
                        |
                        |
                        |  MatrixShape:
                        |    classTerm: raml-shapes.MatrixShape
                        |    extends: ArrayShape
                        |
                        |
                        |  Message:
                        |    classTerm: apiContract.Message
                        |    mapping:
                        |      extends:
                        |        propertyTerm: doc.extends
                        |        range: Message
                        |        allowMultiple: true
                        |
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      payload:
                        |        propertyTerm: apiContract.payload
                        |        range: Payload
                        |        allowMultiple: true
                        |      correlationId:
                        |        propertyTerm: core.correlationId
                        |        range: CorrelationId
                        |      displayName:
                        |        propertyTerm: core.displayName
                        |        range: string
                        |      title:
                        |        propertyTerm: core.title
                        |        range: string
                        |      summary:
                        |        propertyTerm: core.summary
                        |        range: string
                        |      header:
                        |        propertyTerm: apiContract.header
                        |        range: Parameter
                        |        allowMultiple: true
                        |      binding:
                        |        propertyTerm: apiBinding.binding
                        |        range: MessageBindings
                        |      tag:
                        |        propertyTerm: apiContract.tag
                        |        range: Tag
                        |        allowMultiple: true
                        |      examples:
                        |        propertyTerm: apiContract.examples
                        |        range: Example
                        |        allowMultiple: true
                        |      documentation:
                        |        propertyTerm: core.documentation
                        |        range: CreativeWork
                        |      isAbstract:
                        |        propertyTerm: apiContract.isAbstract
                        |        range: boolean
                        |      headerExamples:
                        |        propertyTerm: apiContract.headerExamples
                        |        range: Example
                        |        allowMultiple: true
                        |      headerSchema:
                        |        propertyTerm: apiContract.headerSchema
                        |        range: NodeShape
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  MessageBinding:
                        |    classTerm: apiBinding.MessageBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  MessageBindings:
                        |    classTerm: apiBinding.MessageBindings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      bindings:
                        |        propertyTerm: apiBinding.bindings
                        |        range: MessageBindingUnion
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Module:
                        |    classTerm: doc.Module
                        |    extends: BaseUnit
                        |    mapping:
                        |      declares:
                        |        propertyTerm: doc.declares
                        |        range: DomainElementUnion
                        |        allowMultiple: true
                        |
                        |
                        |  MqttMessageBinding:
                        |    classTerm: apiBinding.MqttMessageBinding
                        |    extends: MessageBinding
                        |    mapping:
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  MqttOperationBinding:
                        |    classTerm: apiBinding.MqttOperationBinding
                        |    extends: OperationBinding
                        |    mapping:
                        |      qos:
                        |        propertyTerm: apiBinding.qos
                        |        range: integer
                        |      retain:
                        |        propertyTerm: apiBinding.retain
                        |        range: boolean
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  MqttServerBinding:
                        |    classTerm: apiBinding.MqttServerBinding
                        |    extends: ServerBinding
                        |    mapping:
                        |      clientId:
                        |        propertyTerm: apiBinding.clientId
                        |        range: string
                        |      cleanSession:
                        |        propertyTerm: apiBinding.cleanSession
                        |        range: boolean
                        |      lastWill:
                        |        propertyTerm: apiBinding.lastWill
                        |        range: MqttServerLastWill
                        |      keepAlive:
                        |        propertyTerm: apiBinding.keepAlive
                        |        range: integer
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  MqttServerLastWill:
                        |    classTerm: apiBinding.MqttServerLastWill
                        |    mapping:
                        |      topic:
                        |        propertyTerm: apiBinding.topic
                        |        range: string
                        |      qos:
                        |        propertyTerm: apiBinding.qos
                        |        range: integer
                        |      retain:
                        |        propertyTerm: apiBinding.retain
                        |        range: boolean
                        |      message:
                        |        propertyTerm: apiBinding.message
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  NamedExampleFragment:
                        |    classTerm: apiContract.NamedExampleFragment
                        |    extends: Fragment
                        |
                        |
                        |  NilShape:
                        |    classTerm: raml-shapes.NilShape
                        |    extends: AnyShape
                        |""".stripMargin

  val part2: String = """
                        |  NodeShape:
                        |    classTerm: shacl.NodeShape
                        |    extends: AnyShape
                        |    mapping:
                        |      minProperties:
                        |        propertyTerm: raml-shapes.minProperties
                        |        range: integer
                        |      maxProperties:
                        |        propertyTerm: raml-shapes.maxProperties
                        |        range: integer
                        |      closed:
                        |        propertyTerm: shacl.closed
                        |        range: boolean
                        |      additionalPropertiesSchema:
                        |        propertyTerm: shacl.additionalPropertiesSchema
                        |        range: DataShapesUnion
                        |      discriminator:
                        |        propertyTerm: raml-shapes.discriminator
                        |        range: string
                        |      discriminatorValue:
                        |        propertyTerm: raml-shapes.discriminatorValue
                        |        range: string
                        |      discriminatorMapping:
                        |        propertyTerm: raml-shapes.discriminatorMapping
                        |        range: IriTemplate
                        |        allowMultiple: true
                        |      discriminatorValueMapping:
                        |        propertyTerm: raml-shapes.discriminatorValueMapping
                        |        range: DiscriminatorValueMapping
                        |        allowMultiple: true
                        |      property:
                        |        propertyTerm: shacl.property
                        |        range: PropertyShape
                        |        allowMultiple: true
                        |      propertyNames:
                        |        propertyTerm: shacl.propertyNames
                        |        range: DataShapesUnion
                        |      dependencies:
                        |        propertyTerm: raml-shapes.dependencies
                        |        range: PropertyDependencies
                        |        allowMultiple: true
                        |      schemaDependencies:
                        |        propertyTerm: raml-shapes.schemaDependencies
                        |        range: SchemaDependencies
                        |        allowMultiple: true
                        |      unevaluatedProperties:
                        |        propertyTerm: raml-shapes.unevaluatedProperties
                        |        range: boolean
                        |      unevaluatedPropertiesSchema:
                        |        propertyTerm: raml-shapes.unevaluatedPropertiesSchema
                        |        range: DataShapesUnion
                        |
                        |
                        |  OAuth1Settings:
                        |    classTerm: security.OAuth1Settings
                        |    extends: Settings
                        |    mapping:
                        |      requestTokenURI:
                        |        propertyTerm: security.requestTokenUri
                        |        range: string
                        |      authorizationURI:
                        |        propertyTerm: security.authorizationUri
                        |        range: string
                        |      tokenCredentialsURI:
                        |        propertyTerm: security.tokenCredentialsUri
                        |        range: string
                        |      signature:
                        |        propertyTerm: security.signature
                        |        range: string
                        |        allowMultiple: true
                        |
                        |
                        |  OAuth2Flow:
                        |    classTerm: security.OAuth2Flow
                        |    mapping:
                        |      authorizationURI:
                        |        propertyTerm: security.authorizationUri
                        |        range: string
                        |      accessTokenURI:
                        |        propertyTerm: security.accessTokenUri
                        |        range: string
                        |      flow:
                        |        propertyTerm: security.flow
                        |        range: string
                        |      refreshURI:
                        |        propertyTerm: security.refreshUri
                        |        range: string
                        |      scope:
                        |        propertyTerm: security.scope
                        |        range: Scope
                        |        allowMultiple: true
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  OAuth2Settings:
                        |    classTerm: security.OAuth2Settings
                        |    extends: Settings
                        |    mapping:
                        |      authorizationGrant:
                        |        propertyTerm: security.authorizationGrant
                        |        range: string
                        |        allowMultiple: true
                        |      flows:
                        |        propertyTerm: security.flows
                        |        range: OAuth2Flow
                        |        allowMultiple: true
                        |
                        |
                        |  ObjectNode:
                        |    classTerm: data.Object
                        |    extends: DataNode
                        |    mapping:
                        |      properties:
                        |        propertyTerm: data.properties
                        |        range: PropertyNode
                        |        allowMultiple: true
                        |
                        |
                        |  OpenIDSettings:
                        |    classTerm: security.OpenIdConnectSettings
                        |    extends: Settings
                        |    mapping:
                        |      openIdConnectUrl:
                        |        propertyTerm: security.openIdConnectUrl
                        |        range: string
                        |      scope:
                        |        propertyTerm: security.scope
                        |        range: Scope
                        |        allowMultiple: true
                        |
                        |
                        |  Operation:
                        |    classTerm: apiContract.Operation
                        |    mapping:
                        |      extends:
                        |        propertyTerm: doc.extends
                        |        typeDiscriminatorName: type
                        |        typeDiscriminator:
                        |          SimpleTrait: Operation
                        |          AppliedTrait: ParametrizedTrait
                        |        range:
                        |          - Operation
                        |          - ParametrizedTrait
                        |        allowMultiple: true
                        |
                        |      method:
                        |        propertyTerm: apiContract.method
                        |        range: string
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      deprecated:
                        |        propertyTerm: core.deprecated
                        |        range: boolean
                        |      guiSummary:
                        |        propertyTerm: apiContract.guiSummary
                        |        range: string
                        |      documentation:
                        |        propertyTerm: core.documentation
                        |        range: CreativeWork
                        |      scheme:
                        |        propertyTerm: apiContract.scheme
                        |        range: string
                        |        allowMultiple: true
                        |      accepts:
                        |        propertyTerm: apiContract.accepts
                        |        range: string
                        |        allowMultiple: true
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |        allowMultiple: true
                        |      expects:
                        |        propertyTerm: apiContract.expects
                        |        range: Request
                        |        allowMultiple: true
                        |      returns:
                        |        propertyTerm: apiContract.returns
                        |        range: Response
                        |        allowMultiple: true
                        |      security:
                        |        propertyTerm: security.security
                        |        range: SecurityRequirement
                        |        allowMultiple: true
                        |      tag:
                        |        propertyTerm: apiContract.tag
                        |        range: Tag
                        |        allowMultiple: true
                        |      callback:
                        |        propertyTerm: apiContract.callback
                        |        range: Callback
                        |        allowMultiple: true
                        |      server:
                        |        propertyTerm: apiContract.server
                        |        range: Server
                        |        allowMultiple: true
                        |      binding:
                        |        propertyTerm: apiBinding.binding
                        |        range: OperationBindings
                        |      isAbstract:
                        |        propertyTerm: apiContract.isAbstract
                        |        range: boolean
                        |      operationId:
                        |        propertyTerm: apiContract.operationId
                        |        range: string
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  OperationBinding:
                        |    classTerm: apiBinding.OperationBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  OperationBindings:
                        |    classTerm: apiBinding.OperationBindings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      bindings:
                        |        propertyTerm: apiBinding.bindings
                        |        range: OperationBindingUnion
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Organization:
                        |    classTerm: core.Organization
                        |    mapping:
                        |      url:
                        |        propertyTerm: core.url
                        |        range: link
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      email:
                        |        propertyTerm: core.email
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Overlay:
                        |    classTerm: apiContract.Overlay
                        |    extends: Document
                        |
                        |
                        |  Parameter:
                        |    classTerm: apiContract.Parameter
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      paramName:
                        |        propertyTerm: apiContract.paramName
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      required:
                        |        propertyTerm: apiContract.required
                        |        range: boolean
                        |      deprecated:
                        |        propertyTerm: doc.deprecated
                        |        range: boolean
                        |      allowEmptyValue:
                        |        propertyTerm: apiContract.allowEmptyValue
                        |        range: boolean
                        |      style:
                        |        propertyTerm: apiContract.style
                        |        range: string
                        |      explode:
                        |        propertyTerm: apiContract.explode
                        |        range: boolean
                        |      allowReserved:
                        |        propertyTerm: apiContract.allowReserved
                        |        range: boolean
                        |      binding:
                        |        propertyTerm: apiContract.binding
                        |        range: string
                        |      schema:
                        |        propertyTerm: raml-shapes.schema
                        |        range: DataShapesUnion
                        |      payload:
                        |        propertyTerm: apiContract.payload
                        |        range: Payload
                        |        allowMultiple: true
                        |      examples:
                        |        propertyTerm: apiContract.examples
                        |        range: Example
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ParametrizedDeclaration:
                        |    classTerm: doc.ParametrizedDeclaration
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      target:
                        |        propertyTerm: doc.target
                        |        range: AbstractDeclarationUnion
                        |      variable:
                        |        propertyTerm: doc.variable
                        |        range: VariableValue
                        |        allowMultiple: true
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ParametrizedResourceType:
                        |    classTerm: apiContract.ParametrizedResourceType
                        |    extends: ParametrizedDeclaration
                        |
                        |
                        |  ParametrizedSecurityScheme:
                        |    classTerm: security.ParametrizedSecurityScheme
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      scheme:
                        |        propertyTerm: security.scheme
                        |        range: SecurityScheme
                        |      settings:
                        |        propertyTerm: security.settings
                        |        range: SecuritySettingsUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ParametrizedTrait:
                        |    classTerm: apiContract.ParametrizedTrait
                        |    extends: ParametrizedDeclaration
                        |
                        |
                        |  Payload:
                        |    classTerm: apiContract.Payload
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |      schemaMediaType:
                        |        propertyTerm: apiContract.schemaMediaType
                        |        range: string
                        |      schema:
                        |        propertyTerm: raml-shapes.schema
                        |        range: DataShapesUnion
                        |      examples:
                        |        propertyTerm: apiContract.examples
                        |        range: Example
                        |        allowMultiple: true
                        |      encoding:
                        |        propertyTerm: apiContract.encoding
                        |        range: Encoding
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  PayloadFragment:
                        |    classTerm: apiContract.PayloadFragment
                        |    extends: Fragment
                        |    mapping:
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |
                        |
                        |  PropertyDependencies:
                        |    classTerm: raml-shapes.PropertyDependencies
                        |    mapping:
                        |      propertySource:
                        |        propertyTerm: raml-shapes.propertySource
                        |        range: string
                        |      propertyTarget:
                        |        propertyTerm: raml-shapes.propertyTarget
                        |        range: string
                        |        allowMultiple: true
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  PropertyNode:
                        |    classTerm: data.Property
                        |    extends: DataNode
                        |    mapping:
                        |      range:
                        |        propertyTerm: data.range
                        |        range: DataNodeUnion
                        |
                        |
                        |  PropertyShape:
                        |    classTerm: shacl.PropertyShape
                        |    extends: Shape
                        |    mapping:
                        |      path:
                        |        propertyTerm: shacl.path
                        |        range: link
                        |      range:
                        |        propertyTerm: raml-shapes.range
                        |        range: DataShapesUnion
                        |      minCount:
                        |        propertyTerm: shacl.minCount
                        |        range: integer
                        |      maxCount:
                        |        propertyTerm: shacl.maxCount
                        |        range: integer
                        |      patternName:
                        |        propertyTerm: raml-shapes.patternName
                        |        range: string
                        |
                        |
                        |  RecursiveShape:
                        |    classTerm: raml-shapes.RecursiveShape
                        |    extends: Shape
                        |    mapping:
                        |      fixpoint:
                        |        propertyTerm: raml-shapes.fixPoint
                        |        range: link
                        |
                        |
                        |  Request:
                        |    classTerm: apiContract.Request
                        |    extends: Message
                        |    mapping:
                        |      required:
                        |        propertyTerm: apiContract.required
                        |        range: boolean
                        |      parameter:
                        |        propertyTerm: apiContract.parameter
                        |        range: Parameter
                        |        allowMultiple: true
                        |      queryString:
                        |        propertyTerm: apiContract.queryString
                        |        range: DataShapesUnion
                        |      uriParameter:
                        |        propertyTerm: apiContract.uriParameter
                        |        range: Parameter
                        |        allowMultiple: true
                        |      cookieParameter:
                        |        propertyTerm: apiContract.cookieParameter
                        |        range: Parameter
                        |        allowMultiple: true
                        |
                        |
                        |  ResourceType:
                        |    classTerm: apiContract.ResourceType
                        |    extends: AbstractDeclaration
                        |
                        |
                        |  ResourceTypeFragment:
                        |    classTerm: apiContract.ResourceTypeFragment
                        |    extends: Fragment
                        |
                        |
                        |  Response:
                        |    classTerm: apiContract.Response
                        |    extends: Message
                        |    mapping:
                        |      statusCode:
                        |        propertyTerm: apiContract.statusCode
                        |        range: string
                        |      links:
                        |        propertyTerm: apiContract.link
                        |        range: TemplatedLink
                        |        allowMultiple: true
                        |
                        |
                        |  ScalarNode:
                        |    classTerm: data.Scalar
                        |    extends: DataNode
                        |    mapping:
                        |      value:
                        |        propertyTerm: data.value
                        |        range: string
                        |      dataType:
                        |        propertyTerm: shacl.datatype
                        |        range: link
                        |
                        |
                        |  ScalarShape:
                        |    classTerm: raml-shapes.ScalarShape
                        |    extends: AnyShape
                        |    mapping:
                        |      datatype:
                        |        propertyTerm: shacl.datatype
                        |        range: link
                        |      encoding:
                        |        propertyTerm: raml-shapes.encoding
                        |        range: string
                        |      mediaType:
                        |        propertyTerm: raml-shapes.mediaType
                        |        range: string
                        |      contentSchema:
                        |        propertyTerm: raml-shapes.contentSchema
                        |        range: DataShapesUnion
                        |      pattern:
                        |        propertyTerm: shacl.pattern
                        |        range: string
                        |      minLength:
                        |        propertyTerm: shacl.minLength
                        |        range: integer
                        |      maxLength:
                        |        propertyTerm: shacl.maxLength
                        |        range: integer
                        |      minInclusive:
                        |        propertyTerm: shacl.minInclusive
                        |        range: double
                        |      max.Inclusive:
                        |        propertyTerm: shacl.maxInclusive
                        |        range: double
                        |      min.Exclusive:
                        |        propertyTerm: shacl.minExclusive
                        |        range: boolean
                        |      max.Exclusive:
                        |        propertyTerm: shacl.maxExclusive
                        |        range: boolean
                        |      format:
                        |        propertyTerm: raml-shapes.format
                        |        range: string
                        |      multipleOf:
                        |        propertyTerm: raml-shapes.multipleOf
                        |        range: double
                        |
                        |
                        |  SchemaDependencies:
                        |    classTerm: raml-shapes.SchemaDependencies
                        |    mapping:
                        |      propertySource:
                        |        propertyTerm: raml-shapes.propertySource
                        |        range: string
                        |      schemaTarget:
                        |        propertyTerm: raml-shapes.schemaTarget
                        |        range: DataShapesUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  SchemaShape:
                        |    classTerm: raml-shapes.SchemaShape
                        |    extends: AnyShape
                        |    mapping:
                        |      mediaType:
                        |        propertyTerm: core.mediaType
                        |        range: string
                        |
                        |
                        |  Scope:
                        |    classTerm: security.Scope
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  SecurityRequirement:
                        |    classTerm: security.SecurityRequirement
                        |    mapping:
                        |      schemes:
                        |        propertyTerm: security.schemes
                        |        range: ParametrizedSecurityScheme
                        |        allowMultiple: true
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  SecurityScheme:
                        |    classTerm: security.SecurityScheme
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      type:
                        |        propertyTerm: security.type
                        |        range: string
                        |      displayName:
                        |        propertyTerm: core.displayName
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      header:
                        |        propertyTerm: apiContract.header
                        |        range: Parameter
                        |        allowMultiple: true
                        |      parameter:
                        |        propertyTerm: apiContract.parameter
                        |        range: Parameter
                        |        allowMultiple: true
                        |      response:
                        |        propertyTerm: apiContract.response
                        |        range: Response
                        |        allowMultiple: true
                        |      settings:
                        |        propertyTerm: security.settings
                        |        range: SecuritySettingsUnion
                        |      queryString:
                        |        propertyTerm: apiContract.queryString
                        |        range: DataShapesUnion
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  SecuritySchemeFragment:
                        |    classTerm: security.SecuritySchemeFragment
                        |    extends: Fragment
                        |
                        |
                        |  Server:
                        |    classTerm: apiContract.Server
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      urlTemplate:
                        |        propertyTerm: core.urlTemplate
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      variable:
                        |        propertyTerm: apiContract.variable
                        |        range: Parameter
                        |        allowMultiple: true
                        |      protocol:
                        |        propertyTerm: apiContract.protocol
                        |        range: string
                        |      protocolVersion:
                        |        propertyTerm: apiContract.protocolVersion
                        |        range: string
                        |      security:
                        |        propertyTerm: security.security
                        |        range: SecurityRequirement
                        |        allowMultiple: true
                        |      binding:
                        |        propertyTerm: apiBinding.binding
                        |        range: ServerBindings
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ServerBinding:
                        |    classTerm: apiBinding.ServerBinding
                        |    mapping:
                        |      type:
                        |        propertyTerm: apiBinding.type
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ServerBindings:
                        |    classTerm: apiBinding.ServerBindings
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      bindings:
                        |        propertyTerm: apiBinding.bindings
                        |        range: ServerBindingUnion
                        |        allowMultiple: true
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Settings:
                        |    classTerm: security.Settings
                        |    mapping:
                        |      additionalProperties:
                        |        propertyTerm: security.additionalProperties
                        |        range: DataNodeUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Shape:
                        |    classTerm: shacl.Shape
                        |    mapping:
                        |      name:
                        |        propertyTerm: shacl.name
                        |        range: string
                        |      displayName:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      defaultValue:
                        |        propertyTerm: shacl.defaultValue
                        |        range: DataNodeUnion
                        |      in:
                        |        propertyTerm: shacl.in
                        |        range: DataNodeUnion
                        |        allowMultiple: true
                        |        sorted: true
                        |      inherits:
                        |        propertyTerm: raml-shapes.inherits
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |      defaultValueString:
                        |        propertyTerm: shacl.defaultValueStr
                        |        range: string
                        |      not:
                        |        propertyTerm: shacl.not
                        |        range: DataShapesUnion
                        |      and:
                        |        propertyTerm: shacl.and
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |      or:
                        |        propertyTerm: shacl.or
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |      exclusiveOr:
                        |        propertyTerm: shacl.xone
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |      inheritanceClosure:
                        |        propertyTerm: raml-shapes.closure
                        |        range: link
                        |        allowMultiple: true
                        |      if:
                        |        propertyTerm: shacl.if
                        |        range: DataShapesUnion
                        |      then:
                        |        propertyTerm: shacl.then
                        |        range: DataShapesUnion
                        |      else:
                        |        propertyTerm: shacl.else
                        |        range: DataShapesUnion
                        |      readOnly:
                        |        propertyTerm: raml-shapes.readOnly
                        |        range: boolean
                        |      writeOnly:
                        |        propertyTerm: raml-shapes.writeOnly
                        |        range: boolean
                        |      deprecated:
                        |        propertyTerm: raml-shapes.deprecated
                        |        range: boolean
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  ShapeExtension:
                        |    classTerm: apiContract.ShapeExtension
                        |    mapping:
                        |      definedBy:
                        |        propertyTerm: doc.definedBy
                        |        range: CustomDomainProperty
                        |      extension:
                        |        propertyTerm: doc.extension
                        |        range: DataNodeUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Tag:
                        |    classTerm: apiContract.Tag
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      documentation:
                        |        propertyTerm: core.documentation
                        |        range: CreativeWork
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  TemplatedLink:
                        |    classTerm: apiContract.TemplatedLink
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      template:
                        |        propertyTerm: apiContract.template
                        |        range: string
                        |      operationId:
                        |        propertyTerm: apiContract.operationId
                        |        range: string
                        |      mapping:
                        |        propertyTerm: apiContract.mapping
                        |        range: IriTemplate
                        |        allowMultiple: true
                        |      requestBody:
                        |        propertyTerm: apiContract.requestBody
                        |        range: string
                        |      description:
                        |        propertyTerm: core.description
                        |        range: string
                        |      server:
                        |        propertyTerm: apiContract.server
                        |        range: Server
                        |      designLink:
                        |        propertyTerm: doc.design-link-target
                        |        range: link
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  Trait:
                        |    classTerm: apiContract.Trait
                        |    extends: AbstractDeclaration
                        |
                        |
                        |  TraitFragment:
                        |    classTerm: apiContract.TraitFragment
                        |    extends: Fragment
                        |
                        |
                        |  TupleShape:
                        |    classTerm: raml-shapes.TupleShape
                        |    extends: ArrayShape
                        |    mapping:
                        |      items:
                        |        propertyTerm: raml-shapes.items
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |        sorted: true
                        |      closedItems:
                        |        propertyTerm: raml-shapes.closedItems
                        |        range: boolean
                        |      additionalItemsSchema:
                        |        propertyTerm: raml-shapes.additionalItemsSchema
                        |        range: DataShapesUnion
                        |
                        |
                        |  UnionShape:
                        |    classTerm: raml-shapes.UnionShape
                        |    extends: AnyShape
                        |    mapping:
                        |      anyOf:
                        |        propertyTerm: raml-shapes.anyOf
                        |        range: DataShapesUnion
                        |        allowMultiple: true
                        |
                        |
                        |  VariableValue:
                        |    classTerm: doc.VariableValue
                        |    mapping:
                        |      name:
                        |        propertyTerm: core.name
                        |        range: string
                        |      value:
                        |        propertyTerm: doc.value
                        |        range: DataNodeUnion
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |
                        |  WebAPI:
                        |    classTerm: apiContract.WebAPI
                        |    extends: API
                        |
                        |
                        |  WebSocketsChannelBinding:
                        |    classTerm: apiBinding.WebSocketsChannelBinding
                        |    extends: ChannelBinding
                        |    mapping:
                        |      method:
                        |        propertyTerm: apiBinding.method
                        |        range: string
                        |      query:
                        |        propertyTerm: apiBinding.query
                        |        range: DataShapesUnion
                        |      headers:
                        |        propertyTerm: apiBinding.headers
                        |        range: DataShapesUnion
                        |      bindingVersion:
                        |        propertyTerm: apiBinding.bindingVersion
                        |        range: string
                        |
                        |
                        |  XMLSerializer:
                        |    classTerm: raml-shapes.XMLSerializer
                        |    mapping:
                        |      xmlAttribute:
                        |        propertyTerm: raml-shapes.xmlAttribute
                        |        range: boolean
                        |      xmlWrapped:
                        |        propertyTerm: raml-shapes.xmlWrapped
                        |        range: boolean
                        |      xmlName:
                        |        propertyTerm: raml-shapes.xmlName
                        |        range: string
                        |      xmlNamespace:
                        |        propertyTerm: raml-shapes.xmlNamespace
                        |        range: string
                        |      xmlPrefix:
                        |        propertyTerm: raml-shapes.xmlPrefix
                        |        range: string
                        |      designAnnotations:
                        |        propertyTerm: doc.designAnnotation
                        |        range: DomainExtension
                        |        allowMultiple: true
                        |
                        |documents:
                        |  root:
                        |    encodes: ParsedUnitUnion""".stripMargin
}
