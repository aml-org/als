package org.mulesoft.amfintegration.dialect.dialects.validations

import org.mulesoft.amfintegration.dialect.dialects.RawInMemoryDialect

object RawValidationProfileDialect extends RawInMemoryDialect {
  val name: String = "validation-profile"
  lazy val yaml: String =
    """#%Dialect 1.0
      |
      |dialect: Validation Profile
      |version: 1.0
      |usage: Dialect to describe validations over JSON-LD documents
      |external:
      |  schema-org: "http://schema.org/"
      |  shacl: "http://www.w3.org/ns/shacl#"
      |  validation: "http://a.ml/vocabularies/amf-validation#"
      |nodeMappings:
      |  conditionalNode:
      |    classTerm: shacl.RegoConstraint
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      if:
      |        propertyTerm: validation.if
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        mandatory: true
      |      then:
      |        propertyTerm: validation.then
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        mandatory: true
      |  inlinedRegoNode:
      |    classTerm: shacl.RegoConstraint
      |    mapping:
      |      rego:
      |        range: string
      |        propertyTerm: validation.code
      |        mandatory: true
      |  regoModuleNode:
      |    classTerm: shacl.RegoConstraint
      |    mapping:
      |      message:
      |        range: string
      |        propertyTerm: shacl.message
      |      code:
      |        range: string
      |        propertyTerm: validation.code
      |        mandatory: true
      |      libraries:
      |        propertyTerm: shacl.codeLibrary
      |        range: string
      |        allowMultiple: true
      |      name:
      |        propertyTerm: shacl.codeFunctioName
      |        range: string
      |  propertyConstraintNode:
      |    classTerm: shacl.PropertyShape
      |    mapping:
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      name:
      |        propertyTerm: validation.ramlPropertyId
      |        mandatory: true
      |        range: string
      |      pattern:
      |        propertyTerm: shacl.pattern
      |        range: string
      |      maxCount:
      |        propertyTerm: shacl.maxCount
      |        range: integer
      |      minCount:
      |        propertyTerm: shacl.minCount
      |        range: integer
      |      minExclusive:
      |        propertyTerm: shacl.minExclusive
      |        range: number
      |      maxExclusive:
      |        propertyTerm: shacl.maxExclusive
      |        range: number
      |      minInclusive:
      |        propertyTerm: shacl.minInclusive
      |        range: number
      |      maxInclusive:
      |        propertyTerm: shacl.maxInclusive
      |        range: number
      |      datatype:
      |        propertyTerm: shacl.datatype
      |        range: string
      |      in:
      |        propertyTerm: shacl.in
      |        allowMultiple: true
      |        range: any
      |      nested:
      |        propertyTerm: shacl.node
      |        range: shapeValidationNode
      |      atLeast:
      |        propertyTerm: shacl.atLeastNode
      |        range: qualifiedShapeValidationNode
      |      atMost:
      |        propertyTerm: shacl.atMostNode
      |        range: qualifiedShapeValidationNode
      |      equalsToProperty:
      |        propertyTerm: shacl.equals
      |        range: string
      |      disjointWithProperty:
      |        propertyTerm: shacl.disjoint
      |        range: string
      |      lessThanProperty:
      |        propertyTerm: shacl.lessThan
      |        range: string
      |      lessThanOrEqualsToProperty:
      |        propertyTerm: shacl.lessThanOrEquals
      |        range: string
      |      rego:
      |        propertyTerm: validation.code
      |        range: string
      |      regoModule:
      |        propertyTerm: validation.codeModule
      |        range: regoModuleNode
      |
      |  regoValidationNode:
      |    classTerm: validation.FunctionValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      libraries:
      |        propertyTerm: shacl.codeLibrary
      |        range: string
      |        allowMultiple: true
      |      rego:
      |        mandatory: true
      |        propertyTerm: validation.code
      |        range: string
      |
      |  regoModuleValidationNode:
      |    classTerm: validation.FunctionValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      libraries:
      |        propertyTerm: shacl.codeLibrary
      |        range: string
      |        allowMultiple: true
      |      regoModule:
      |        mandatory: true
      |        propertyTerm: validation.codeModule
      |        range: regoModuleNode
      |
      |  qualifiedShapeValidationNode:
      |    classTerm: validation.QualifiedShapevalidationNode
      |    mapping:
      |      count:
      |        propertyTerm: shacl.count
      |        range: integer
      |        mandatory: true
      |      validation:
      |        propertyTerm: shacl.valueShape
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        mandatory: true
      |  shapeValidationNode:
      |    classTerm: validation.ShapeValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      classConstraints:
      |        propertyTerm: shacl.class
      |        range: string
      |        allowMultiple: true
      |      propertyConstraints:
      |        mandatory: true
      |        propertyTerm: shacl.property
      |        mapKey: name
      |        range: propertyConstraintNode
      |  andShapeValidationNode:
      |    classTerm: validation.AndShapeValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      and:
      |        propertyTerm: shacl.and
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        allowMultiple: true
      |        mandatory: true
      |  notShapeValidationNode:
      |    classTerm: validation.NotShapeValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      not:
      |        propertyTerm: shacl.not
      |        mandatory: true
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |  orShapeValidationNode:
      |    classTerm: validation.OrShapeValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      or:
      |        mandatory: true
      |        propertyTerm: shacl.or
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        allowMultiple: true
      |  xoneShapeValidationNode:
      |    classTerm: validation.XoneShapeValidation
      |    mapping:
      |      name:
      |        propertyTerm: schema-org.name
      |        range: string
      |      message:
      |        propertyTerm: shacl.message
      |        range: string
      |      targetClass:
      |        propertyTerm: validation.ramlClassId
      |        range: string
      |        allowMultiple: true
      |      xone:
      |        mandatory: true
      |        propertyTerm: shacl.xone
      |        range: [ conditionalNode, shapeValidationNode, inlinedRegoNode, regoModuleNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |        allowMultiple: true
      |
      |  ramlPrefixNode:
      |    classTerm: validation.RamlPrefix
      |    mapping:
      |      prefix:
      |        propertyTerm: validation.ramlPrefixName
      |        range: string
      |      uri:
      |        propertyTerm: validation.ramlPrefixUri
      |        range: string
      |
      |  profileNode:
      |    classTerm: validation.Profile
      |    mapping:
      |      prefixes:
      |        propertyTerm: validation.ramlPrefixes
      |        mapKey: prefix
      |        mapValue: uri
      |        range: ramlPrefixNode
      |      profile:
      |        propertyTerm: schema-org.name
      |        mandatory: true
      |        range: string
      |      description:
      |        propertyTerm: schema-org.description
      |        range: string
      |      extends:
      |        propertyTerm: validation.extendsProfile
      |        range: string
      |      violation:
      |        propertyTerm: validation.setSeverityViolation
      |        range: string
      |        allowMultiple: true
      |      info:
      |        propertyTerm: validation.setSeverityInfo
      |        range: string
      |        allowMultiple: true
      |      warning:
      |        propertyTerm: validation.setSeverityWarning
      |        range: string
      |        allowMultiple: true
      |      disabled:
      |        propertyTerm: validation.disableValidation
      |        range: string
      |        allowMultiple: true
      |      validations:
      |        propertyTerm: validation.validations
      |        mapKey: name
      |        range: [ conditionalNode, shapeValidationNode, regoValidationNode, regoModuleValidationNode, xoneShapeValidationNode, orShapeValidationNode, notShapeValidationNode, andShapeValidationNode ]
      |documents:
      |  fragments:
      |    encodes:
      |      RegoValidation: regoModuleValidationNode
      |
      |  library:
      |    declares:
      |      functions: regoModuleValidationNode
      |
      |  root:
      |    encodes: profileNode""".stripMargin

}
