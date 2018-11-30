# High-level AST module
## Interface
### Core
`org.mulesoft.high.level.Core` class is the entry point to the high-level AST.

`init()` method returning void/unit future must be called before starting the work with the system.

After that one of the following methods should be used to build AST:

`buildModel(unit:BaseUnit,platform:Platform):Future[IProject]`

or 

`buildModel(unit:BaseUnit,fsResolver:IFSProvider):Future[IProject]`

The first method accepts AMF Platform, the second requires a version of file system provider instead.

File system provider contains a number of self-explanatory methods, which are called by the builders to find files and directories:
```
trait IFSProvider {

    def content(fullPath: String): Future[String]

    def dirName(fullPath: String): String

    def existsAsync(path: String): Future[Boolean]

    def resolve(contextPath: String, relativePath: String): Option[String]

    def isDirectory(fullPath: String): Boolean

    def readDirAsync(path: String): Future[Seq[String]]

    def isDirectoryAsync(path: String): Future[Boolean]
}
```

Essentially, both `buildModel` methods require an access to the file system, the only difference is the interface, which represents it.

The result of both methods is an instance of IProject:

```
trait IProject {

    def rootASTUnit: IASTUnit

    def rootPath: String

    def units: Map[String, ASTUnit]

    def types: ITypeCollectionBundle

    def language: Vendor

    def fsProvider: IFSProvider

    def resolve(absBasePath: String, path: String): Option[IASTUnit]

    def resolvePath(path: String, p: String): Option[String]
}
```

Here the most interesting properties are the root AST unit, and the map of all units.

```
trait IASTUnit {

    def universe:IUniverse

    def baseUnit:BaseUnit

    def dependencies: Map[String,DependencyEntry[_ <: IASTUnit]]

    def dependants: Map[String,DependencyEntry[_ <: IASTUnit]]

    def types: ITypeCollection

    def project:IProject

    def rootNode:IHighLevelNode

    def path:String

    def positionsMapper:IPositionsMapper

    def text:String

    def resolve(path:String): Option[IASTUnit]
}
```

For each unit, the root of the AST tree is represented by the `rootNode` property of `IHighLevelNode`, the nodes are described in the next section.

`universe` property points to the global collection of types, `types` property reflects types being used in the unit.
Types are described further below.

## Nodes

Nodes are represented by the `IParseResult` general nodes and the descendants of `IHighLevelNode` and `IAttribute`.
The difference between those two is that attributes have name and value, while general nodes are more complex.

```
trait IParseResult extends IHasExtra {

    def amfNode: AmfObject

    def amfBaseUnit: BaseUnit

    def root: Option[IHighLevelNode]

    def parent: Option[IHighLevelNode]

    def setParent(node: IHighLevelNode): Unit

    def children: Seq[IParseResult]

    def isAttr: Boolean

    def asAttr: Option[IAttribute]

    def isElement: Boolean

    def asElement: Option[IHighLevelNode]

    def isUnknown: Boolean

    def property: Option[IProperty]

    def printDetails(indent: String=""): String

    def printDetails: String = printDetails()

    def astUnit: IASTUnit

    def sourceInfo:ISourceInfo
}
```

The most interesting properties of general node is its link to amd nodes, an ability to check its children, convert it to element (`IHighLevelNode`) or attribute (`IAttribute`), and to check its property pointing to the type system.
 
It is also possible to check node source by using `sourceInfo`

```
trait IHighLevelNode extends IParseResult {
    
    def amfNode: AmfObject

    def localType: Option[ITypeDefinition]

    def definition: ITypeDefinition

    def attribute(n: String): Option[IAttribute]

    def attributeValue(n: String): Option[Any]

    def attributes: Seq[IAttribute]

    def attributes(n: String): Seq[IAttribute]

    def elements: Seq[IHighLevelNode]

    def element(n: String): Option[IHighLevelNode]

    def elements(n: String): Seq[IHighLevelNode]
...
```
High-level nodes can be checked for child elements and attributes.
Also each node has `definition` property pointing to the node definition in terms of type system, and potentially `localType`, which is this node's interpreted type (mostly used for user-defined types, annotations etc)

```
trait IAttribute extends IParseResult {

    def name: String

    def definition: Option[ITypeDefinition]

    def value: Option[Any]
...
```

For attributes name and value are the most important properties.

## Types

Types are represented by `ITypeDefinition` trait.

```
trait ITypeDefinition extends INamedEntity with IHasExtra {
    def key: Option[NamedId]

    def superTypes: Seq[ITypeDefinition]

    def subTypes: Seq[ITypeDefinition]

    def allSubTypes: Seq[ITypeDefinition]

    def allSuperTypes: Seq[ITypeDefinition]

    def properties: Seq[IProperty]

    def facet(n: String): Option[IProperty]

    def allProperties(visited: scala.collection.Map[String,ITypeDefinition]): Seq[IProperty]

    def allProperties: Seq[IProperty]

    def allFacets: Seq[IProperty]

    def allFacets(visited: scala.collection.Map[String,ITypeDefinition]): Seq[IProperty]

    def facets: Seq[IProperty]

    def isValueType: Boolean

    def hasValueTypeInHierarchy: Boolean

    def isArray: Boolean

    def isObject: Boolean

    def hasArrayInHierarchy: Boolean

    def array: Option[IArrayType]

    def arrayInHierarchy: Option[IArrayType]

    def isUnion: Boolean

    def hasUnionInHierarchy: Boolean

    def union: Option[IUnionType]

    def unionInHierarchy: Option[IUnionType]

    def isAnnotationType: Boolean

    def annotationType: Option[IAnnotationType]

    def hasStructure: Boolean

    def isExternal: Boolean

    def hasExternalInHierarchy: Boolean

    def external: Option[IExternalType]

    def externalInHierarchy: Option[IExternalType]

    def isBuiltIn: Boolean

    def universe: IUniverse

    def isAssignableFrom(typeName: String): Boolean

    def property(name: String): Option[IProperty]

    def requiredProperties: Seq[IProperty]

    def getFixedFacets: scala.collection.Map[String,Any]

    def fixedFacets: scala.collection.Map[String,Any]

    def allFixedFacets: scala.collection.Map[String,Any]

    def fixedBuiltInFacets: scala.collection.Map[String,Any]

    def allFixedBuiltInFacets: scala.collection.Map[String,Any]

    def printDetails(indent: String, settings: IPrintDetailsSettings): String

    def printDetails(indent: String): String

    def printDetails: String

    def isGenuineUserDefinedType: Boolean

    def hasGenuineUserDefinedTypeInHierarchy: Boolean

    def genuineUserDefinedTypeInHierarchy: Option[ITypeDefinition]

    def kind: Seq[String]

    def isTopLevel: Boolean

    def isUserDefined: Boolean
}

```

Note that this entity represents not only types declared in `types` section of RAML. Type is a more general abstract. Any annotation is a type. Any type property also has its own type, often anonymous.

Types can be checked for its hierarchy using `superTypes` and `subTypes` properties.

Types has lots of `is...` properties to determine the type's kind.

Types can be requested for facets using `fixedFacets`, `facet` and similar properties.

Types can be checked for being user defined.
 

# RAML Node tables
This links list RAML 1.0 and RAML 0.8 node type tables:

[RAML 1.0](./RAML10Classes.html)

[RAML 0.8](./RAML08Classes.html)