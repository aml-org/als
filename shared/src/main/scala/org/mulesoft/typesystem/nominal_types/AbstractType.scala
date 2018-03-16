package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces._
import org.mulesoft.typesystem.nominal_interfaces.extras.{BuiltInExtra, TopLevelExtra, UserDefinedExtra}
import org.mulesoft.typesystem.typesystem_interfaces.Extra

import scala.collection.mutable.ListBuffer
import scala.collection.Map
import scala.collection.mutable

class AbstractType(_name:String, _universe:IUniverse = EmptyUniverse, _path: String="")
    extends Described(_name) with ITypeDefinition with HasExtra {

    var _key: Option[NamedId] = None
    var _isCustom: Boolean = _
    var _customProperties: ListBuffer[IProperty] = ListBuffer()

    def properties: Seq[IProperty] = scala.collection.immutable.List()

    def externalInHierarchy: Option[ExternalType] = allSuperTypes.find(x => x.isInstanceOf[ExternalType]).asInstanceOf[Option[ExternalType]]

    var _props: Option[List[IProperty]] = None
    var _allFacets: Option[List[IProperty]] = None
    var _facets: ListBuffer[IProperty] = ListBuffer()

    var _isLocked: Boolean = false

    def isLocked:Boolean = _isLocked

    def lock():Unit = _isLocked = true

    def addFacet(q: IProperty):Unit = _facets +=q

    def allFacets:Seq[IProperty] = allFacets(scala.collection.mutable.Map[String,ITypeDefinition]())

    def allFacets(visited:scala.collection.Map[String,ITypeDefinition]):Seq[IProperty] = allFacets(scala.collection.mutable.Map[String,ITypeDefinition]()++=visited)

    def allFacets(ps: scala.collection.mutable.Map[String,ITypeDefinition]
                 =scala.collection.mutable.Map[String,ITypeDefinition]()):Seq[IProperty] = {

        if (_allFacets.isDefined) {
            _allFacets.get
        }
        else {
            if (nameId.isDefined && ps.contains(nameId.get)) {
                List[IProperty]()
            }
            else {
                if(typeId.isDefined) {
                    ps.put(typeId.get, this)
                }
                var n:scala.collection.mutable.Map[String,IProperty]
                        = scala.collection.mutable.Map[String,IProperty]()

                if (superTypes.nonEmpty) {
                    superTypes.foreach({
                        case at:AbstractType => at.allFacets(ps).foreach(y=>y.nameId.foreach(n.put(_,y)))
                        case _ =>
                    })
                }
                _facets.foreach(x=>x.nameId.foreach(n.put(_,x)))
                _allFacets = Some(n.values.toList)
                _allFacets.get
            }
        }
    }

    def facets: Seq[IProperty] = _facets.clone()

    def facet(name: String):Option[IProperty] = allFacets().find(x => x.nameId == name)

    def typeId: Option[String] = nameId

    def allProperties:Seq[IProperty] = allProperties(scala.collection.mutable.Map[String,ITypeDefinition]())

    def allProperties(visited: scala.collection.Map[String,ITypeDefinition]): Seq[IProperty] = allProperties(scala.collection.mutable.Map[String,ITypeDefinition]()++=visited)

    def allProperties(ps: scala.collection.mutable.Map[String,ITypeDefinition]):Seq[IProperty] = {
        if (this._props.isDefined) {
            this._props.get
        }
        else {
            var uniqueTypeId = NominalTypesIndex.getUniqueTypeId(this)
            if (uniqueTypeId.isDefined&&ps.contains(uniqueTypeId.get)) {
                List[IProperty]()
            }
            if(uniqueTypeId.isDefined) {
                ps(uniqueTypeId.get) = this
            }
            var n:scala.collection.mutable.Map[String,IProperty]
                    = scala.collection.mutable.Map[String,IProperty]()
            if (superTypes.nonEmpty) {
                superTypes.foreach({
                    case at:AbstractType => at.allProperties(ps).foreach(y=>y.nameId.foreach(n.put(_,y)))
                    case t:ITypeDefinition => t.allProperties.foreach(y=>y.nameId.foreach(n.put(_,y)))
                })
            }
//            (this.fixedFacets()).keys.foreach {
//                fresh3 =>
//                    var x = zeroOfMyType
//                        = fresh3 {
//                        n.remove(x)
//
//                    }
//            }
            properties.foreach(x => x.nameId.foreach(n.put(_,x)))
            this._props = Option(n.values.toList)
            _props.get
        }
    }

    def property(propName: String): Option[IProperty] = allProperties.find(_.nameId.contains(propName))

    def hasValueTypeInHierarchy:Boolean = allSuperTypes.exists(x => x.hasValueTypeInHierarchy)


    def isAnnotationType: Boolean = false

    def hasStructure: Boolean = false

    def key: Option[NamedId] = {
        if (_key.isEmpty && nameId.isDefined) {
           // _key = universe.matched.get(this.nameId.get)
        }
        _key
    }

    private var _superTypes: ListBuffer[ITypeDefinition] = ListBuffer()
    private var _subTypes: ListBuffer[ITypeDefinition] = ListBuffer()
    //var _requirements: List[ValueRequirement] = List()

    var _fixedFacets: scala.collection.mutable.Map[String,Any]
            = scala.collection.mutable.Map[String,Any]()

    var _fixedBuildInFacets: scala.collection.mutable.Map[String,Any]
            = scala.collection.mutable.Map[String,Any]()

    def hasArrayInHierarchy:Boolean = allSuperTypes.exists(x => x.isInstanceOf[Array])

    def arrayInHierarchy: Option[Array] = allSuperTypes.find(x => x.isInstanceOf[Array]).asInstanceOf[Option[Array]]

    var uc: Boolean = false

    def unionInHierarchy: Option[Union] = allSuperTypes.find(x => x.isInstanceOf[Union]).asInstanceOf[Option[Union]]

    def hasExternalInHierarchy: Boolean = allSuperTypes.exists(x => x.isInstanceOf[ExternalType])

    def hasUnionInHierarchy:Boolean = allSuperTypes.exists(x => x.isInstanceOf[Union])

    def fixFacet(name: String, v: Any, builtIn: Boolean = false):Unit = {
        if (builtIn) {
            _fixedBuildInFacets.put(name,v)
        }
        else {
            this._fixedFacets.put(name,v)
        }
    }

    private var _af: Option[scala.collection.mutable.Map[String,Any]] = None

    private var _abf: Option[scala.collection.mutable.Map[String,Any]] = None

    def getFixedFacets: scala.collection.Map[String,Any] = fixedFacets

    def fixedFacets: scala.collection.Map[String,Any] = collectFixedFacets(false)

    def fixedBuiltInFacets: scala.collection.Map[String,Any] = collectFixedFacets(true)

    def collectFixedFacets(builtIn: Boolean): scala.collection.Map[String,Any] = {
        var facetsMap = if (builtIn) _fixedBuildInFacets else _fixedFacets
        var result: scala.collection.mutable.Map[String,Any]
                = scala.collection.mutable.Map[String,Any]()

        result ++= facetsMap
        contributeFacets(result)
        result
    }

    def allFixedFacets: scala.collection.Map[String,Any] = collectAllFixedFacets(false)

    def allFixedBuiltInFacets: scala.collection.Map[String,Any] = collectAllFixedFacets(true)

    def collectAllFixedFacets(builtIn: Boolean): scala.collection.Map[String,Any] = {

        if (builtIn && _abf.isDefined) {
            _abf.get
        }
        else if (!builtIn && _af.isDefined) {
            _af.get
        }
        else {
            var mm = scala.collection.mutable.Map[String,Any]()
            var sp = ListBuffer[ITypeDefinition]() ++= allSuperTypes += this
            if(builtIn){
                sp.foreach(x=> mm ++= x.fixedBuiltInFacets)
                _abf = Some(mm)
            }
            else{
                sp.foreach(x=> mm ++= x.fixedFacets)
                _af = Some(mm)
            }
            mm
        }
    }

    private def contributeFacets(x: scala.collection.mutable.Map[String,Any]):Unit = {}

    var _nameAtRuntime: Option[String] = None

    def getPath:String = _path

    def setNameAtRuntime(name: String):Unit = _nameAtRuntime = Option(name)

    def getNameAtRuntimeOption[Stirng] = _nameAtRuntime

    def universe: IUniverse = _universe

    def superTypes: Seq[ITypeDefinition] = ListBuffer() ++= _superTypes

    def isAssignableFrom(typeName: String): Boolean = {
        if (nameId.isDefined && nameId.get == typeName) {
            !this.isUserDefined
        }
        else {
            allSuperTypes.exists(x=>x.nameId.nonEmpty && x.nameId.get==typeName)
        }
    }

    def annotationType: Option[IAnnotationType] = None

    def subTypes: Seq[ITypeDefinition] = ListBuffer() ++= _subTypes

    def allSubTypes: Seq[ITypeDefinition] = {
        var rs = ListBuffer[ITypeDefinition]()
        subTypes.foreach(x => {
            rs += x
            rs ++= x.allSubTypes
        })
        rs.distinct
    }

    var _allSupers: Option[Seq[ITypeDefinition]] = None

    def allSuperTypes: Seq[ITypeDefinition] = {
        if (_allSupers.isDefined) {
            _allSupers.get
        }
        else {
            var rs = ListBuffer[ITypeDefinition]()
            allSuperTypesRecurrent(this, rs)
            _allSupers = Some(rs.distinct)
            _allSupers.get
        }
    }

    def allSuperTypesRecurrent(t: ITypeDefinition, result: ListBuffer[ITypeDefinition], m:scala.collection.mutable.Map[String,ITypeDefinition] = scala.collection.mutable.Map[String,ITypeDefinition]()):Unit = {
        t.superTypes.foreach(x => {
            var uniqueTypeId = NominalTypesIndex.getUniqueTypeId(x.asInstanceOf[AbstractType])
            if (uniqueTypeId.isEmpty) {
//                var adapter = x.getAdapter(InheritedType)
//                (uniqueTypeId = (((adapter && ((adapter.id() + "")))) || ""))
            }
            if (uniqueTypeId.isDefined && !m.contains(uniqueTypeId.get)) {
                result += x
                m.put(uniqueTypeId.get,x)
                allSuperTypesRecurrent(x, result,m)
            }
        })
    }

    def addSuperType(q: ITypeDefinition):Unit = {
        q match {
            case at:AbstractType =>
                if(!at.isLocked) {
                    at._subTypes += this
                }
            case _ =>
        }
        _superTypes += q
    }

//    def addRequirement(name: String, value: String) = {
//        this._requirements.push(new ValueRequirement(name, value))
//
//    }
//
//    def valueRequirements() = {
//        return this._requirements
//
//    }

    def requiredProperties: Seq[IProperty] = allProperties.filter(_.isRequired)

    def printDetails: String = printDetails("")

    def printDetails(indent: String): String = printDetails(indent,IPrintDetailsSettings())

    def printDetails(
            indent: String,
            settings:IPrintDetailsSettings): String = {

        var standardIndent = "  "
        var result:String = ""
        var className = this.getTypeClassName
        var nameIdValue = nameId.getOrElse("")
        result = result + s"$indent$nameIdValue[$className]\n"
        if(this.isArray){
            this.asInstanceOf[Array].componentType.foreach(
                ct=>result += s"$indent${standardIndent}Component type: ${ct.nameId.getOrElse("")}\n")
        }
        if (properties.nonEmpty && !settings.hideProperties) {
            result = result + s"$indent${standardIndent}Properties:\n"
            properties.foreach(property => {
                var propertyRangeOpt = property.range
                if(propertyRangeOpt.isDefined) {
                    var propertyType = ""
                    var componentType= ""
                    var propertyRange = propertyRangeOpt.get
                    if (propertyRange.isInstanceOf[Described]) {
                        propertyType = propertyType + propertyRange.asInstanceOf[Described].nameId.getOrElse("")
                    }
                    if (propertyRange.isInstanceOf[AbstractType]) {
                        propertyType = propertyType + s"[${propertyRange.asInstanceOf[AbstractType].getTypeClassName}]"
                    }
                    if(propertyRange.isArray){
                        propertyRange.asInstanceOf[Array].componentType.foreach(
                            ct => componentType = s"$indent${standardIndent*3}Component type: ${ct.nameId.getOrElse("")}\n")
                    }
                    result = result + s"$indent$standardIndent$standardIndent${property.nameId.getOrElse("")}: $propertyType\n$componentType"
                }
            })
        }
        var stArr = superTypes
        var filteredSuperTypes = stArr
        if (stArr.nonEmpty && !settings.printStandardSuperclasses) {
            filteredSuperTypes = stArr.filter(st => {
                var name = ""
                var `type` = ""
                st match {
                    case at:AbstractType =>
                        name = if (at.nameId.isDefined) at.nameId.get else ""
                        `type` = at.getTypeClassName
                    case d:Described => name = if (d.nameId.isDefined) d.nameId.get else ""
                    case _ =>
                }
                !isStandardSuperclass(name, `type`)
            })

        }
        if (filteredSuperTypes.nonEmpty) {
            result = result + s"$indent${standardIndent}Super types:\n"
            filteredSuperTypes.foreach(superType => {
                result = result + superType.printDetails(indent + standardIndent + standardIndent, settings)
            })
        }
        result.toString
    }

    def getTypeClassName: String = {
        getClass.getCanonicalName
    }

    def isStandardSuperclass(nId: String, className: String):Boolean = {
        if ((nId == "TypeDeclaration") && (className == "NodeClass"))
            true
        if ((nId == "ObjectTypeDeclaration") && (className == "NodeClass"))
            true
        if ((nId == "RAMLLanguageElement") && (className == "NodeClass"))
            true
        false
    }

//    def examples(collectFromSupertype: Boolean): Array[IExpandableExample] = {
//        return ebuilder.exampleFromNominal(this, collectFromSupertype)
//    }

    def isGenuineUserDefinedType: Boolean = {
        if (this.isBuiltIn) false
        if (properties.nonEmpty||fixedFacets.nonEmpty||fixedBuiltInFacets.nonEmpty) {
            true
        }
        else {
            isTopLevel && this.nameId.isDefined && this.nameId.get.nonEmpty
        }
    }

    def genuineUserDefinedTypeInHierarchy: Option[ITypeDefinition] = if (isGenuineUserDefinedType) Some(this) else allSuperTypes.find(_.isGenuineUserDefinedType)

    def hasGenuineUserDefinedTypeInHierarchy: Boolean = allSuperTypes.exists(_.isGenuineUserDefinedType)

    def customProperties: Seq[IProperty] = ListBuffer() ++= _customProperties

    def allCustomProperties(): Seq[IProperty] = {
        var props:ListBuffer[IProperty] = ListBuffer()
        superTypes.foreach(props ++= _.asInstanceOf[AbstractType].allCustomProperties)
        props ++= customProperties
        props
    }

    def registerCustomProperty(p: IProperty):Unit = {
        if (p.domain.isDefined && p.domain.get != this) {
            throw new Error("messageRegistry.SHOULD_BE_ALREADY_OWNED.message")
        }
        if (this._customProperties.contains(p)) {
            throw new Error("messageRegistry.ALREADY_INCLUDED.message")
        }
        this._customProperties += p
    }

    def setCustom(`val`: Boolean):Unit = _isCustom = `val`

    def isCustom: Boolean = _isCustom

    def isUnion = false

    def union: Option[IUnionType] = None

    def isExternal = false

    def external: Option[IExternalType] = None

    def isArray:Boolean = false

    def isObject:Boolean = {
        if (nameId.isDefined && nameId.get == "object") {
            true
        }
        else {
            allSuperTypes.exists(_.isObject)
        }
    }

    def array: Option[IArrayType] = None

    def isValueType:Boolean = false

    def kind(): Seq[String] = {
        var result: ListBuffer[String] = ListBuffer()
        if (isObject) {
            result += "object"
        }
        if (isArray) {
            result += "array"
        }
        if (isValueType) {
            result += "value"
        }
        if (isUnion) {
            result += "union"
        }
        if (isAnnotationType) {
            result += "annotation"
        }
        if (isExternal) {
            result += "external"
        }
        result
    }

    def isBuiltIn: Boolean = getExtra(BuiltInExtra).isDefined

    def isTopLevel: Boolean = getExtra(TopLevelExtra).isDefined

    def isUserDefined: Boolean = getExtra(UserDefinedExtra).isDefined

//    def getExtraAdapter(): IHasExtra = {
//        if (this.getAdapters()) {
//            var extraAdapter = _underscore_.find(this.getAdapters(), (adapter => {
//                if (((((adapter.asInstanceOf[Any]).getExtra && (typeof(((adapter.asInstanceOf[Any]).getExtra)) == "function")) && (adapter.asInstanceOf[Any]).putExtra) && (typeof(((adapter.asInstanceOf[Any]).putExtra)) == "function"))) {
//                    return true
//
//                }
//
//            }))
//            return extraAdapter.asInstanceOf[IHasExtra]
//
//        }
//        return null
//
//    }
}

object AbstractType{
    val UNDEFINED = new StructuredType("undefined")
}
