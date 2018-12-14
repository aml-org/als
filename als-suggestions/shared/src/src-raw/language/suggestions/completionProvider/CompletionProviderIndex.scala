package org.mulesoft.als.suggestions.completionProvider

import org.mulesoft.als.suggestions.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.completionProviderInterfaces.IFSProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IEditorStateProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.IASTProvider;
import org.mulesoft.als.suggestions.completionProviderInterfaces.FSResolverExt;
import org.mulesoft.als.suggestions.completionProviderInterfaces.Suggestion;
import org.mulesoft.als.suggestions.logger.LoggerIndex
import org.mulesoft.als.suggestions.completionProvider.CompletionRequest;
import org.mulesoft.als.suggestions.completionProvider.CompletionProvider;
import org.mulesoft.als.suggestions.completionProvider.ResolvedProvider;
import org.mulesoft.als.suggestions.completionProvider.ProviderBasedResolver;

class CompletionProviderIndex{

def require(s: String): Any
var categories = require( "../resources/categories.json" )
var _logger: ILogger = null
def setLogger(logger: ILogger) = {
 (_logger=logger)
 
}
def getLogger(): ILogger = {
 if (_logger)
return _logger
return new EmptyLogger()
 
}
def suggest(editorState: IEditorStateProvider, fsProvider: IFSProvider, astProvider: IASTProvider = null): Array[Suggestion] = {
 var completionRequest = new CompletionRequest( editorState )
var completionProvider = new CompletionProvider( fsProvider, astProvider )
return completionProvider.suggest( completionRequest, true )
 
}
def suggestAsync(editorState: IEditorStateProvider, fsProvider: IFSProvider): Promise[Array[Suggestion]] = {
 var completionRequest = new CompletionRequest( editorState )
var completionProvider = new CompletionProvider( fsProvider )
return completionProvider.suggestAsync( completionRequest, true )
 
}
def categoryByRanges(suggestion: String, parentRange: ITypeDefinition, propertyRange: ITypeDefinition): String = {
 var categoryNames: Array[String] = Object.keys( categories )
{
var i = 0
while( (i<categoryNames.length)) {
 {
 var categoryName = categoryNames(i)
var issues = Object.keys( categories(categoryName) )
{
var j = 0
while( (j<issues.length)) {
 {
 var issueName = issues(j)
if ((issueName!==suggestion)) {
 continue
 
}
var issue = categories(categoryName)(issueName)
var propertyIs = (issue.is||Array())
var parentIs = (issue.parentIs||Array())
if ((propertyRange&&_underscore_.find( propertyIs, (( name: String ) =>  isRangeAssignable( propertyRange, name )) ))) {
 return categoryName
 
}
if ((parentRange&&_underscore_.find( parentIs, (( name: String ) =>  isRangeAssignable( parentRange, name )) ))) {
 return categoryName
 
}
 
}
 (j+= 1)
}
}
 
}
 (i+= 1)
}
}
return "unknown"
 
}
def isRangeAssignable(`type`: ITypeDefinition, defCode: String) = {
 var keys = defCode.split( "." )
var defObject: Any = parserApi.universes
{
var i = 0
while( (i<keys.length)) {
 {
 (defObject=defObject(keys(i)))
 
}
 (i+= 1)
}
}
return `type`.isAssignableFrom( defObject.name )
 
}
def doSuggest(request: CompletionRequest, provider: CompletionProvider): Array[Suggestion] = {
 var preParsedAST: IParseResult = null
if (provider.astProvider) {
 (preParsedAST=provider.astProvider.getASTRoot())
 
}
var result = getSuggestions( request, provider, preParsedAST )
if (result)
return result
return Array()
 
}
def doSuggestAsync(request: CompletionRequest, provider: CompletionProvider): Promise[Array[Suggestion]] = {
 getLogger().debug( ((("Suggestions request for prefix: "+request.prefix())+" and value prefix: ")+request.valuePrefix()), "completionProvider", "doSuggestAsync" )
(request.async=true)
(request.promises=Array())
var fsResolver = (provider.contentProvider.asInstanceOf[Any]).fsResolver
if ((!fsResolver)) {
 (fsResolver=new ProviderBasedResolver( provider.contentProvider ))
 
}
var apiPromise = parserApi.parseRAML( modifiedContent( request ), Map( "fsResolver" -> fsResolver,
"filePath" -> request.content.getPath() ) )
var suggestionsPromise = apiPromise.then( (api =>  getSuggestions( request, provider, findAtOffsetInNode( request.content.getOffset(), api.highLevel() ) )) )
var requestSuggestionsPromise = suggestionsPromise.then( (( suggestions: Array[Suggestion] ) =>  {
 return Promise.all( Array( suggestions ).concat( request.promises.asInstanceOf[Array[Any]] ) )
 
}) )
var finalPromise = requestSuggestionsPromise.then( (( arrays: Array[Array[Suggestion]] ) =>  {
 var result: Array[Suggestion] = Array()
 arrays.forEach( ((suggestions =>  {
 if ((!suggestions)) {
 return
 
}
 (result=result.concat( suggestions ))
 
})) )
 return result
 
}) )
return finalPromise
 
}
def getSuggestions(request: CompletionRequest, provider: CompletionProvider, preParsedAst: IParseResult = undefined, project: IProject): Array[Suggestion] = {
 getLogger().debugDetail( ((("Suggestions request for prefix: "+request.prefix())+" and value prefix: ")+request.valuePrefix()), "completionProvider", "getSuggestions" )
(provider.currentRequest=request)
try {
 if ((provider.level>100)) {
 return
 
}
(provider.level+= 1)
var offset = request.content.getOffset()
var text = request.content.getText()
var kind = completionKind( request )
getLogger().debugDetail( ("Determined completion kind: "+kind), "completionProvider", "getSuggestions" )
var node: IHighLevelNode = ((if (preParsedAst) preParsedAst else getAstNode( request, provider.contentProvider, true, true, project ))).asInstanceOf[IHighLevelNode]
var hlnode: IHighLevelNode = node
if ((kind===parserApi.search.LocationKind.DIRECTIVE_COMPLETION)) {
 return Array( Map( "text" -> "include" ) )
 
}
if ((kind===parserApi.search.LocationKind.ANNOTATION_COMPLETION)) {
 var declarations = parserApi.search.globalDeclarations( hlnode ).filter( (x =>  parserApi.universeHelpers.isAnnotationTypesProperty( x.property() )) )
return declarations.map( (x =>  {
 return Map( "text" -> parserApi.search.qName( x, hlnode ),
"annotation" -> true )
 
}) )
 
}
if ((kind===parserApi.search.LocationKind.VERSION_COMPLETION)) {
 return ramlVersionCompletion( request )
 
}
if ((kind===parserApi.search.LocationKind.INCOMMENT)) {
 return Array()
 
}
if ((node===null)) {
 return Array()
 
}
var hasNewLine = false
{
var position = (offset-1)
while( (position>=hlnode.lowLevel().start())) {
 {
 var ch = text(position)
if (((ch=="\r")||(ch=="\n"))) {
 (hasNewLine=true)
break()
 
}
 
}
 (position-= 1)
}
}
var cmi = offset
{
var pm = (offset-1)
while( (pm>=0)) {
 {
 var c = text(pm)
if (((c===" ")||(c==="\t"))) {
 (cmi=pm)
continue
 
}
break()
 
}
 (pm-= 1)
}
}
var attr = _underscore_.find( hlnode.attrs(), (x =>  (((x.lowLevel().start()<cmi)&&(x.lowLevel().end()>=cmi))&&(!x.property().getAdapter( parserApi.ds.RAMLPropertyService ).isKey()))) )
if ((!attr)) {
 var p = _underscore_.find( hlnode.definition().allProperties(), (p =>  (p.asInstanceOf[Property]).canBeValue()) )
if ((!hasNewLine)) {
 if (((p&&(kind==parserApi.search.LocationKind.VALUE_COMPLETION))&&parserApi.universeHelpers.isTypeProperty( p ))) {
 if ((hlnode.children().length==1)) {
 (attr=parserApi.stubs.createASTPropImpl( hlnode.lowLevel(), hlnode, p.range(), p ))
 
}
 
}
 
}
else {
 var cm = _underscore_.find( hlnode.lowLevel().children(), (x =>  ((x.start()<offset)&&(x.end()>=offset))) )
if (cm) {
 var p = _underscore_.find( hlnode.definition().allProperties(), (p =>  (p.nameId()==cm.key())) )
if (p) {
 var il = getIndent( cm.keyStart(), cm.unit().contents() )
var il2 = getIndent( offset, cm.unit().contents() )
if ((il2.length>(il.length+1))) {
 var isValue = p.range().hasValueTypeInHierarchy()
if (isValue) {
 (attr=parserApi.stubs.createVirtualASTPropImpl( cm, hlnode, p.range(), p ))
 
}
else {
 if ((cm.children().length>0)) {
 (hlnode=parserApi.stubs.createVirtualNodeImpl( cm.children()(0), hlnode, p.range().asInstanceOf[Any], p ))
 
}
 
}
 
}
 
}
 
}
 
}
 
}
if ((kind==parserApi.search.LocationKind.PATH_COMPLETION)) {
 return pathCompletion( request, provider.contentProvider, attr, hlnode, false )
 
}
if ((attr&&(((kind===parserApi.search.LocationKind.KEY_COMPLETION)||(kind===parserApi.search.LocationKind.SEQUENCE_KEY_COPLETION))))) {
 var txt = ""
{
var position = (offset-1)
while( (position>=0)) {
 {
 var ch = text(position)
if (((ch=="\r")||(ch=="\n"))) {
 break()
 
}
(txt=(ch+txt))
 
}
 (position-= 1)
}
}
(txt=txt.trim())
if ((txt!=attr.name())) {
 (kind=parserApi.search.LocationKind.VALUE_COMPLETION)
 
}
 
}
if ((kind==parserApi.search.LocationKind.VALUE_COMPLETION)) {
 var attrParent = ((attr&&(attr.asInstanceOf[Any]).parent)&&(attr.asInstanceOf[Any]).parent())
var parentPropertyOfAttr = ((attrParent&&attrParent.property)&&attrParent.property())
var attrParentType = ((attrParent&&attrParent.definition)&&attrParent.definition())
var isExtendableParent = (attrParentType&&((universeHelpers.isExtensionType( attrParentType )||universeHelpers.isOverlayType( attrParentType ))))
var attrPropertyName = (((attr&&attr.property)&&attr.property())&&attr.property().nameId())
var isExtendsProperty = (((attrPropertyName===universeModule.Universe10.Overlay.properties.extends.name)||(attrPropertyName===universeModule.Universe10.Extension.properties.extends.name)))
if ((attrParentType&&attrParentType.isAssignableFrom( parserApi.universes.Universe10.TypeDeclaration.name ))) {
 if ((attrPropertyName===universeModule.Universe10.ObjectTypeDeclaration.properties.discriminator.name)) {
 var actualType = (attrParent.localType&&attrParent.localType())
var typeProps = (((actualType&&actualType.allProperties()))||Array())
(typeProps=typeProps.filter( (( typeProp: Any ) =>  {
 return (typeProp.isPrimitive&&typeProp.isPrimitive())
 
}) ))
return typeProps.map( (( typeProp: Any ) =>  {
 var propertyName = typeProp.nameId()
 return Map( "text" -> propertyName,
"displayText" -> propertyName,
"description" -> typeProp.description(),
"category" -> categoryByRanges( propertyName, attrParentType, typeProp.range() ) )
 
}) )
 
}
 
}
if ((isExtendableParent&&isExtendsProperty)) {
 return pathCompletion( request, provider.contentProvider, attr, hlnode, false )
 
}
if ((parentPropertyOfAttr&&(universeHelpers.asInstanceOf[Any]).isUsesProperty( parentPropertyOfAttr ))) {
 return pathCompletion( request, provider.contentProvider, attr, hlnode, false )
 
}
var proposals = valueCompletion( node, attr, request, provider )
if ((!attr)) {
 if (((!proposals)||(proposals.length==0))) {
 if ((!hasNewLine)) {
 if (hlnode.definition().getAdapter( parserApi.ds.RAMLService ).isUserDefined()) {
 return propertyCompletion( hlnode, request, mv, defNode, hasNewLine )
 
}
 
}
 
}
 
}
if (((attr&&attr.property())&&(((attr.property().asInstanceOf[Property]).getAdapter( parserApi.ds.RAMLPropertyService ).isTypeExpr()||(attr.property().asInstanceOf[Property]).isAnnotation())))) {
 if ((!proposals)) {
 (proposals=Array())
 
}
(proposals=proposals.filter( (x =>  {
 var proposalText = getProposalText( x )
 if ((proposalText===hlnode.name())) {
 return false
 
}
 return true
 
}) ))
var pref = request.valuePrefix()
var nmi = pref.lastIndexOf( "." )
if (nmi) {
 (pref=pref.substr( 0, (nmi+1) ))
 
}
else {
 (pref=null)
 
}
if (pref) {
 (proposals=proposals.filter( (x =>  (getProposalText( x ).indexOf( pref )==0)) ))
proposals.forEach( (x =>  updateProposalText( x, getProposalText( x ).substring( pref.length ) )) )
 
}
 
}
if (proposals) {
 if ((text((offset-1))==":")) {
 proposals.forEach( (x =>  {
 if (x.extra) {
 (x.extra=(" "+x.extra))
 
}
else {
 (x.extra=" ")
 
}
 
}) )
 
}
if ((request.prefix().indexOf( "[" )!=(-1))) {
 request.setPrefix( "" )
proposals.forEach( (x =>  {
 (x.text=(": [ "+x.displayText))
 
}) )
 
}
else if (isSquareBracketExpected( attr )) {
 (proposals=proposals.filter( (proposed =>  (!isSiblingExists( attr, proposed.displayText ))) ))
var ending = ""
var initialPosition = offset
{
var i = initialPosition
while( ((i<text.length)&&(!java.util.regex.Pattern.compile(raw"""[\t\n\r]""").test( text(i) )))) {
 {
 (ending+=text(i))
if ((ending.replace( java.util.regex.Pattern.compile(raw"""\s""", "g"), "" )===":")) {
 proposals.forEach( (x =>  {
 (x.text=x.displayText)
 (x.snippet=null)
 (x.extra=null)
 
}) )
break()
 
}
 
}
 (i+= 1)
}
}
var isOpenSquarePresent = false
(initialPosition=(offset-1))
{
var i = initialPosition
while( ((i>=0)&&(!java.util.regex.Pattern.compile(raw"""[\t\n\r]""").test( text(i) )))) {
 {
 if ((text(i)==="[")) {
 (isOpenSquarePresent=true)
break()
 
}
 
}
 (i-= 1)
}
}
if ((!isOpenSquarePresent)) {
 proposals.forEach( (x =>  {
 if (((!request.valuePrefix())&&x.snippet)) {
 (x.text=x.displayText)
(x.snippet=(("["+x.snippet)+"]"))
return
 
}
 (x.extra=" [")
 (x.text=(((x.snippet||x.displayText))+"]"))
 (x.snippet=null)
 
}) )
 
}
 
}
else {
 var ending = ""
var initialPosition = offset
{
var i = initialPosition
while( ((i<text.length)&&(!java.util.regex.Pattern.compile(raw"""[\t\n\r]""").test( text(i) )))) {
 {
 (ending+=text(i))
if ((ending.replace( java.util.regex.Pattern.compile(raw"""\s""", "g"), "" )===":")) {
 proposals.forEach( (x =>  {
 (x.text=x.displayText)
 (x.snippet=null)
 (x.extra=null)
 
}) )
break()
 
}
 
}
 (i+= 1)
}
}
proposals.forEach( (x =>  {
 if (((x.isResourceType&&(!request.valuePrefix()))&&x.snippet)) {
 (x.snippet=(x.extra+x.snippet))
(x.extra=null)
(x.text=x.displayText)
 
}
 
}) )
 
}
 
}
if ((((!hasNewLine)&&proposals)&&(proposals.length>0))) {
 (proposals=addDefineInlineProposal2( proposals, hlnode.lowLevel().start(), text ))
 
}
if ((((proposals&&isInResourceDescription( attr ))&&request.prefix())&&(request.prefix().length>0))) {
 var canBeTemplate = false
var canBeTransform1 = 0
var canBeTransform2 = 0
var txt = ""
{
var position = (offset-1)
while( (position>=0)) {
 {
 var ch = text(position)
if (((ch==="\r")||(ch==="\n"))) {
 break()
 
}
if (((ch==="<")&&(text((position-1))==="<"))) {
 (canBeTemplate=true)
break()
 
}
if ((ch==="!")) {
 (canBeTransform1+= 1)
 
}
if (((ch==="|")&&(canBeTransform1===1))) {
 (canBeTransform2+= 1)
 
}
(txt=(ch+txt))
 
}
 (position-= 1)
}
}
if (((canBeTemplate&&(canBeTransform1===1))&&(canBeTransform2===1))) {
 var leftPart = new RegExp( (java.util.regex.Pattern.compile(raw"""\|\s*!\s*""").source+request.prefix()) )
if (leftPart.test( txt )) {
 (proposals=addTransformers( proposals, request.prefix() ))
 
}
 
}
 
}
return proposals
 
}
if (((kind==search.LocationKind.KEY_COMPLETION)||(((((kind==search.LocationKind.SEQUENCE_KEY_COPLETION)&&(offset>0))&&(text.charAt( (offset-1) )!="-"))&&(text.charAt( (offset-1) )!=" "))))) {
 if ((node.isAttr()||node.isImplicit())) {
 throw new Error( "Should be highlevel node at this place" ) 
}
if (search.isExampleNode( hlnode )) {
 return examplePropertyCompletion( hlnode, request, provider )
 
}
if (((hlnode.property()&&universeHelpers.isUriParametersProperty( hlnode.property() ))&&(hlnode.definition()instanceofdef.NodeClass))) {
 var nm = hlnode.parent().attr( "relativeUri" )
if ((nm&&(hlnode.name().substring( 0, (hlnode.name().length-1) )==request.valuePrefix()))) {
 var runtime: Array[String] = parserApi.utils.parseUrl( nm.value() )
if ((runtimeinstanceofArray)) {
 if (runtime) {
 if (isColonNeeded( offset, text )) {
 var rs: Array[{   var text: String
 }] = runtime.map( (x =>  {
 return Map( "text" -> (((x+": \n")+getIndent2( offset, text ))+"  ") )
 
}) )
 
}
else {
 var rs: Array[{   var text: String
 }] = runtime.map( (x =>  {
 return Map( "text" -> x )
 
}) )
 
}
return rs
 
}
 
}
 
}
 
}
if (((hlnode.property()&&universeHelpers.isBaseUriParametersProperty( hlnode.property() ))&&(hlnode.definition()instanceofdef.NodeClass))) {
 var nm = hlnode.root().attr( universeModule.Universe10.Api.properties.baseUri.name )
if ((nm&&(hlnode.name().substring( 0, (hlnode.name().length-1) )==request.valuePrefix()))) {
 var runtime: Array[String] = parserApi.utils.parseUrl( nm.value() )
if ((runtimeinstanceofArray)) {
 if (runtime) {
 if (isColonNeeded( offset, text )) {
 var rs: Array[{   var text: String
 }] = runtime.map( (x =>  {
 return Map( "text" -> (((x+": \n")+getIndent2( offset, text ))+"  ") )
 
}) )
 
}
else {
 var rs: Array[{   var text: String
 }] = runtime.map( (x =>  {
 return Map( "text" -> x )
 
}) )
 
}
return rs
 
}
 
}
 
}
 
}
if ((hlnode.property()&&universeHelpers.isResourcesProperty( hlnode.property() ))) {
 var nm = hlnode.attr( "relativeUri" )
if (((nm&&(hlnode.name().substring( 0, (hlnode.name().length-1) )==request.valuePrefix()))&&(request.valuePrefix()!==""))) {
 if ((nm&&(nm.value().indexOf( "{" )!=(-1)))) {
 return Array( Map( "text" -> "mediaTypeExtension}" ) )
 
}
return Array()
 
}
 
}
var mv = (hlnode.property()&&hlnode.property().isMultiValue())
if ((hlnode.lowLevel().keyEnd()<offset)) {
 (mv=false)
 
}
var defNode = true
if (mv) {
 var ce = (hlnode.definition().asInstanceOf[NodeClass]).getAdapter( services.RAMLService ).getCanInherit()
if (ce) {
 var context = hlnode.computedValue( ce(0) )
if (context) {
 (defNode=true)
(mv=false)
 
}
 
}
 
}
return propertyCompletion( hlnode, request, mv, defNode )
 
}
return Array()
 
} finally {
 (provider.level-= 1)
 
}
 
}
def ramlVersionCompletion(request: CompletionRequest): Array[Suggestion] = {
 var prop = Array( "RAML 0.8", "RAML 1.0" )
var rs: Array[Suggestion] = Array()
var text = request.content.getText()
var offset = request.content.getOffset()
var start = text.substr( 0, offset )
if ((start.indexOf( "#%RAML 1.0 " )==0)) {
 var list = Array( "DocumentationItem", "DataType", "NamedExample", "ResourceType", "Trait", "SecurityScheme", "AnnotationTypeDeclaration", "Library", "Overlay", "Extension" )
return list.map( (x =>  {
 return Map( "text" -> x )
 
}) )
 
}
prop.forEach( (x =>  {
 if (((("#%"+x)).indexOf( start )!=0)) {
 return
 
}
 if ((text.trim().indexOf( "#%" )==0)) {
 if ((request.prefix().indexOf( "R" )!=(-1))) {
 rs.push( Map( "displayText" -> x,
"text" -> x ) )
 
}
else {
 var pref = text.substring( 2, offset )
if ((x.indexOf( pref )==0)) {
 if (((request.prefix()=="1")||(request.prefix()=="0"))) {
 rs.push( Map( "displayText" -> x,
"text" -> (request.prefix()+x.substr( (offset-2) )) ) )
 
}
else {
 rs.push( Map( "displayText" -> x,
"text" -> x.substr( (offset-2) ) ).asInstanceOf[Suggestion] )
 
}
 
}
 
}
 
}
else {
 rs.push( Map( "displayText" -> x,
"text" -> x,
"extra" -> "%" ).asInstanceOf[Suggestion] )
 
}
 
}) )
return rs
 
}
;
def completionKind(request: CompletionRequest) = {
 return parserApi.search.determineCompletionKind( request.content.getText(), request.content.getOffset() )
 
}
def getAstNode(request: CompletionRequest, contentProvider: IFSProvider, clearLastChar: Boolean = true, allowNull: Boolean = true, oldProject: IProject): IParseResult = {
 var newProjectId: String = contentProvider.contentDirName( request.content )
var fsResolver = (contentProvider.asInstanceOf[Any]).fsResolver.asInstanceOf[FSResolverExt]
if ((!fsResolver)) {
 (fsResolver=new ProviderBasedResolver( contentProvider ))
 
}
var project: Any = (oldProject||parserApi.project.createProject( newProjectId, fsResolver ))
var offset = request.content.getOffset()
var text = request.content.getText()
var kind = completionKind( request )
if (((kind===parserApi.search.LocationKind.KEY_COMPLETION)&&clearLastChar)) {
 (text=((text.substring( 0, offset )+"k:")+text.substring( offset )))
 
}
var unit = project.setCachedUnitContent( request.content.getBaseName(), text )
var ast = unit.highLevel().asInstanceOf[IHighLevelNode]
var actualOffset = offset
{
var currentOffset = (offset-1)
while( (currentOffset>=0)) {
 {
 var symbol = text(currentOffset)
if (((symbol===" ")||(symbol==="\t"))) {
 (actualOffset=(currentOffset-1))
continue
 
}
break()
 
}
 (currentOffset-= 1)
}
}
var astNode = ast.findElementAtOffset( actualOffset )
if (((astNode&&astNode.root())&&(astNode.root()===astNode))) {
 var lastChild = findLastChild( astNode )
if (((lastChild&&lastChild.lowLevel())&&((lastChild.lowLevel().end()<=offset)))) {
 (astNode=lastChild)
 
}
 
}
if (((!allowNull)&&(!astNode))) {
 return ast
 
}
if ((astNode&&search.isExampleNode( astNode ))) {
 var exampleEnd = astNode.lowLevel().end()
if (((exampleEnd===actualOffset)&&(text(exampleEnd)==="\n"))) {
 (astNode=astNode.parent())
 
}
 
}
return astNode
 
}
def findLastChild(node: IHighLevelNode): IHighLevelNode = {
 if ((!node)) {
 return null
 
}
if (node.lowLevel()) {
 var result = node
node.elements().forEach( (child =>  {
 if ((child.lowLevel().unit()!=node.lowLevel().unit())) {
 return
 
}
 var lastChild = findLastChild( child )
 if (lastChild) {
 (result=lastChild)
 
}
 
}) )
return result
 
}
return null
 
}
def modifiedContent(request: CompletionRequest): String = {
 var offset = request.content.getOffset()
var text = request.content.getText()
var kind = completionKind( request )
if ((kind===parserApi.search.LocationKind.KEY_COMPLETION)) {
 (text=((text.substring( 0, offset )+"k:")+text.substring( offset )))
 
}
return text
 
}
def findAtOffsetInNode(offset: Int, node: IHighLevelNode): IParseResult = {
 var actualOffset = offset
var text = node.lowLevel().unit().contents()
{
var currentOffset = (offset-1)
while( (currentOffset>=0)) {
 {
 var symbol = text(currentOffset)
if (((symbol===" ")||(symbol==="\t"))) {
 (actualOffset=(currentOffset-1))
continue
 
}
break()
 
}
 (currentOffset-= 1)
}
}
var astNode = node.findElementAtOffset( actualOffset )
if (((astNode&&astNode.root())&&(astNode.root()===astNode))) {
 var lastChild = findLastChild( astNode )
if (((lastChild&&lastChild.lowLevel())&&((lastChild.lowLevel().end()<=offset)))) {
 (astNode=lastChild)
 
}
 
}
return astNode
 
}
def getIndent(offset: Int, text: String): String = {
 var spaces = ""
{
var i = (offset-1)
while( (i>=0)) {
 {
 var c = text.charAt( i )
if (((c==" ")||(c=="\t"))) {
 if (spaces) {
 (spaces+=c)
 
}
else {
 (spaces=c)
 
}
 
}
else if (((c=="\r")||(c=="\n"))) {
 return spaces
 
}
else if (spaces) {
 return ""
 
}
 
}
 (i-= 1)
}
}
return ""
 
}
def getIndentWithSequenc(offset: Int, text: String): String = {
 var spaces = ""
{
var i = (offset-1)
while( (i>=0)) {
 {
 var c = text.charAt( i )
if ((((c==" ")||(c=="\t"))||(c=="-"))) {
 if (spaces) {
 (spaces+=c)
 
}
else {
 (spaces=c)
 
}
 
}
else if (((c=="\r")||(c=="\n"))) {
 return spaces
 
}
else if (spaces) {
 return ""
 
}
 
}
 (i-= 1)
}
}
return ""
 
}
def getIndent2(offset: Int, text: String): String = {
 var spaces = ""
{
var i = (offset-1)
while( (i>=0)) {
 {
 var c = text.charAt( i )
if (((c==" ")||(c=="\t"))) {
 if (spaces) {
 (spaces+=c)
 
}
else {
 (spaces=c)
 
}
 
}
else if (((c=="\r")||(c=="\n"))) {
 return spaces
 
}
 
}
 (i-= 1)
}
}
 
}
def pathCompletion(request: CompletionRequest, contentProvider: IFSProvider, attr: IAttribute, hlNode: IHighLevelNode, custom: Boolean) = {
 var prefix = request.valuePrefix()
if ((prefix.indexOf( "#" )===(-1))) {
 return pathPartCompletion( request, contentProvider, attr, hlNode, custom )
 
}
else {
 return pathReferencePartCompletion( request, contentProvider, attr, hlNode, custom )
 
}
 
}
def pathPartCompletion(request: CompletionRequest, contentProvider: IFSProvider, attr: IAttribute, hlNode: IHighLevelNode, custom: Boolean) = {
 getLogger().debug( ("Path part completion for prefix: "+request.valuePrefix()), "completionProvider", "pathPartCompletion" )
var prefix = request.valuePrefix()
var dn: ( String | Promise[String] ) = contentProvider.contentDirName( request.content )
getLogger().debugDetail( ("Directory name is: "+dn), "completionProvider", "pathPartCompletion" )
var ll = contentProvider.resolve( dn.asInstanceOf[String], (if ((prefix.indexOf( "/" )===0)) (("."+prefix)) else prefix) )
var indexOfDot = ll.lastIndexOf( "." )
var indexOfSlash = ll.lastIndexOf( "/" )
if ((!(((indexOfDot>0)&&(((indexOfDot>indexOfSlash)||(indexOfSlash<0))))))) {
 (indexOfDot=(-1))
 
}
var typedPath = ll
if (ll) {
 (dn=contentProvider.dirName( ll ))
if (request.async) {
 (dn=contentProvider.existsAsync( ll ).then( (isExists =>  {
 if ((!isExists)) {
 return contentProvider.dirName( ll )
 
}
 return contentProvider.isDirectoryAsync( ll ).then( (isDirectory =>  {
 if ((!isDirectory)) {
 return contentProvider.dirName( ll )
 
}
 return ll
 
}) )
 
}) ))
 
}
else if ((contentProvider.exists( ll )&&contentProvider.isDirectory( ll ))) {
 (dn=ll)
 
}
 
}
var res: Array[Any] = Array()
var known = (!custom)
if (attr) {
 if (custom) {
 if ((attr.name()==="example")) {
 (res=res.concat( fromDir( prefix, dn, "examples", contentProvider, request.promises ) ))
(known=true)
 
}
if (((attr.name()==="value")&&parserApi.universeHelpers.isGlobalSchemaType( attr.parent().definition() ))) {
 (res=res.concat( fromDir( prefix, dn, "schemas", contentProvider, request.promises ) ))
(known=true)
 
}
 
}
 
}
if ((!attr)) {
 if (custom) {
 if (parserApi.universeHelpers.isTraitType( hlNode.definition() )) {
 (res=res.concat( fromDir( prefix, dn, "traits", contentProvider, request.promises ) ))
(known=true)
 
}
if (parserApi.universeHelpers.isResourceTypeType( hlNode.definition() )) {
 (res=res.concat( fromDir( prefix, dn, "resourceTypes", contentProvider, request.promises ) ))
(known=true)
 
}
if (parserApi.universeHelpers.isSecuritySchemaType( hlNode.definition() )) {
 (res=res.concat( fromDir( prefix, dn, "securitySchemes", contentProvider, request.promises ) ))
(known=true)
 
}
if (parserApi.universeHelpers.isGlobalSchemaType( hlNode.definition() )) {
 (res=res.concat( fromDir( prefix, dn, "schemas", contentProvider, request.promises ) ))
(known=true)
 
}
 
}
 
}
if (((!known)||(!custom))) {
 if (request.async) {
 filtredDirContentAsync( dn, typedPath, indexOfDot, contentProvider, request.promises, request.content.getPath() )
 
}
else if ((contentProvider.exists( dn.asInstanceOf[String] )&&contentProvider.isDirectory( dn.asInstanceOf[String] ))) {
 var dirContent = contentProvider.readDir( dn.asInstanceOf[String] )
(res=res.concat( dirContent.filter( (x =>  {
 try {
 var fullPath = contentProvider.resolve( dn.asInstanceOf[String], x )
if ((fullPath===request.content.getPath())) {
 return false
 
}
if ((fullPath.indexOf( typedPath )===0)) {
 return true
 
}
 
} catch { case exception: Throwable => {
 return false
 
}}
 
}) ).map( (x =>  {
 var fullPath = contentProvider.resolve( dn.asInstanceOf[String], x )
 var needSlash = (contentProvider.exists( fullPath )&&contentProvider.isDirectory( fullPath ))
 return Map( "text" -> (if ((indexOfDot>0)) fullPath.substr( (indexOfDot+1) ) else ((x+((if (needSlash) "/" else ""))))) )
 
}) ) ))
 
}
 
}
return res
 
}
def filtredDirContentAsync(dirName: ( String | Promise[String] ), typedPath: String, indexOfDot: Int, contentProvider: IFSProvider, promises: Array[Promise[Array[Any]]], excludePath: String): Unit = {
 if (promises) {
 var asString: String = zeroOfMyType
var exists = (dirName.asInstanceOf[Promise[String]]).then( (dirNameStr =>  {
 (asString=dirNameStr.asInstanceOf[String])
 return contentProvider.existsAsync( dirNameStr )
 
}) )
var dirContent = exists.then( (isExists =>  {
 if ((!isExists)) {
 return Array()
 
}
 return contentProvider.isDirectoryAsync( asString ).then( (isDir =>  {
 if ((!isDir)) {
 return Array()
 
}
 return contentProvider.readDirAsync( asString ).then( (dirContent =>  {
 var res = dirContent.filter( (x =>  {
 try {
 var fullPath = contentProvider.resolve( asString, x )
if ((fullPath===excludePath)) {
 return false
 
}
if ((fullPath.indexOf( typedPath )===0)) {
 return true
 
}
 
} catch { case exception: Throwable => {
 return false
 
}}
 
}) ).map( (x =>  {
 var fullPath = contentProvider.resolve( asString, x )
 return contentProvider.existsAsync( fullPath ).then( (exist =>  {
 return contentProvider.isDirectoryAsync( fullPath ).then( (isDir =>  {
 var needSlash = (exist&&isDir)
 return Map( "text" -> (if ((indexOfDot>0)) fullPath.substr( (indexOfDot+1) ) else ((x+((if (needSlash) "/" else ""))))) )
 
}) )
 
}) )
 
}) )
 return Promise.all( res )
 
}) )
 
}) )
 
}) )
promises.push( dirContent )
 
}
 
}
def fromDir(prefix: String, dn: ( String | Promise[String] ), dirToLook: String, contentProvider: IFSProvider, promises: Array[Promise[Array[Any]]]) = {
 if (promises) {
 var existsPromise = (dn.asInstanceOf[Promise[String]]).then( (dirName =>  {
 var pss = contentProvider.resolve( dirName.asInstanceOf[String], dirToLook )
 return contentProvider.existsAsync( pss )
 
}) )
var proposalsPromise = existsPromise.then( (result =>  {
 if (result) {
 return contentProvider.readDirAsync( pss ).then( (dirNames =>  {
 var proposals = dirNames.map( (x =>  {
 return Map( "text" -> x,
"replacementPrefix" -> prefix,
"extra" -> (("./"+dirToLook)+"/") )
 
}) )
 return proposals
 
}) )
 
}
 return Array()
 
}) )
promises.push( proposalsPromise )
return Array()
 
}
var pss = contentProvider.resolve( dn.asInstanceOf[String], dirToLook )
if (contentProvider.exists( pss )) {
 var dirContent = contentProvider.readDir( pss )
var proposals = dirContent.map( (x =>  {
 return Map( "text" -> x,
"replacementPrefix" -> prefix,
"extra" -> (("./"+dirToLook)+"/") )
 
}) )
return proposals
 
}
return Array()
 
}
def pathReferencePartCompletion(request: CompletionRequest, contentProvider: IFSProvider, attr: IAttribute, hlNode: IHighLevelNode, custom: Boolean) = {
 var prefix = request.valuePrefix()
var includePath = parserApi.schema.getIncludePath( prefix )
var includeReference = parserApi.schema.getIncludeReference( prefix )
if (((!includePath)||(!includeReference))) {
 return Array()
 
}
if ((!attr)) {
 return Array()
 
}
var includeUnit = attr.lowLevel().unit().resolve( includePath )
if ((!includeUnit)) {
 return Array()
 
}
var content = includeUnit.contents()
if ((!content)) {
 return Array()
 
}
try {
 var proposals = parserApi.schema.completeReference( includePath, includeReference, content )
return proposals.map( (proposal =>  {
 return Map( "text" -> proposal )
 
}) )
 
} catch { case Error: Throwable => {
 console.log( Error )
 
}}
return Array()
 
}
def isColonNeeded(offset: Int, text: String): Boolean = {
 var needColon = true
{
var i = (if ((offset>0)) (offset-1) else 0)
while( (i<text.length)) {
 {
 var chr = text.charAt( i )
if ((((chr==" ")||(chr=="\r"))||(chr=="\n"))) {
 break()
 
}
if ((chr==":")) {
 (needColon=false)
 
}
 
}
 (i+= 1)
}
}
return needColon
 
}
def isAllowed(node: IHighLevelNode, x: IProperty) = {
 var ok = true
(x.asInstanceOf[Property]).getContextRequirements().forEach( (y =>  {
 if ((y.name.indexOf( "(" )!==(-1))) {
 return
 
}
 var vl = node.computedValue( y.name )
 if (vl) {
 (ok=(ok&&((vl==y.value))))
 
}
else {
 if (y.value) {
 (ok=false)
 
}
 
}
 
}) )
return ok
 
}
def filterPropertyCompletion(node: IHighLevelNode, property: IProperty, existing: {   def apply(name: String): Boolean
  /* def update() -- if you need it */
 }): Boolean = {
 if ((!((((!property.getAdapter( parserApi.ds.RAMLPropertyService ).isKey())&&(!property.getAdapter( parserApi.ds.RAMLPropertyService ).isMerged()))&&(!property.getAdapter( services.RAMLPropertyService ).isSystem()))))) {
 return false
 
}
if ((!(isAllowed( node, property )))) {
 return false
 
}
if ((!((!existing(property.nameId()))))) {
 return false
 
}
if ((!((!(property.asInstanceOf[Property]).isAnnotation())))) {
 return false
 
}
if ((((((property.nameId()==parserApi.universes.Universe10.TypeDeclaration.properties.allowedTargets.name)&&property.domain().key())&&(property.domain().key()==parserApi.universes.Universe10.TypeDeclaration))&&node.localType())&&(!node.localType().isAnnotationType()))) {
 return false
 
}
return true
 
}
def propertyCompletion(node: IHighLevelNode, request: CompletionRequest, mv: Boolean, c: Boolean, hasNewLine: Boolean = true) = {
 var hlnode = node
var notAKey = false
var onlyKey = false
var text = request.content.getText()
var offset = request.content.getOffset()
var rootWrapper: Any = (hlnode.root()&&hlnode.root().wrapperNode())
var isDefaultMedia = (((rootWrapper&&rootWrapper.mediaType)&&rootWrapper.mediaType())&&((rootWrapper.mediaType().length>0)))
var isDefaultBodyProperty = (((isDefaultMedia&&hlnode.property)&&hlnode.property())&&parserApi.universeHelpers.isBodyProperty( hlnode.property() ))
if (hasNewLine) {
 var is = getIndentWithSequenc( node.lowLevel().keyStart(), text )
if ((is==undefined)) {
 (is="")
 
}
var i2s = getIndentWithSequenc( offset, text )
var i1 = is.length
var i2 = i2s.length
if ((((i1==i2)&&node.parent())&&(!isDefaultBodyProperty))) {
 if (node.property().getAdapter( parserApi.ds.RAMLPropertyService ).isMerged()) {
 (hlnode=hlnode.parent())
 
}
else {
 (notAKey=false)
(onlyKey=true)
 
}
 
}
else if ((i2>i1)) {
 (notAKey=true)
if ((i2>=(i1+4))) {
 (onlyKey=true)
(notAKey=false)
 
}
 
}
else if ((i1!==i2)) {
 while (((i2<=i1)&&hlnode.parent())) {
{
 (hlnode=hlnode.parent())
var indent = getIndentWithSequenc( hlnode.lowLevel().keyStart(), text )
(i1=(((indent&&indent.length))||0))
 
}
}
(notAKey=true)
 
}
 
}
var needColon = isColonNeeded( offset, text )
var ks = (if (needColon) ": " else "")
var props = hlnode.definition().allProperties()
var existing: {   def apply(name: String): Boolean
  /* def update() -- if you need it */
 } = Map(
)
hlnode.attrs().forEach( (x =>  {
 (existing(x.name())=true)
 
}) )
(props=props.filter( (x =>  filterPropertyCompletion( hlnode, x, existing )) ))
if (hlnode.definition().isAssignableFrom( parserApi.universes.Universe10.TypeDeclaration.name )) {
 if ((!hlnode.definition().isAssignableFrom( "ObjectTypeDeclaration" ))) {
 if ((!hlnode.attr( "type" ))) {
 var q = hlnode.definition().universe().`type`( "ObjectTypeDeclaration" )
if (q) {
 props.push( (q.asInstanceOf[NodeClass]).property( "properties" ) )
 
}
 
}
 
}
 
}
var rs: Array[Suggestion] = Array()
if (((((!mv)||isDefaultBodyProperty))&&(!onlyKey))) {
 (rs=props.map( (x =>  {
 var complextionText = (x.nameId()+ks)
 if (x.range().isAssignableFrom( universeModule.Universe10.ExampleSpec.name )) {
 (complextionText=complextionText.trim())
 
}
else if (((!x.range().hasValueTypeInHierarchy())&&needColon)) {
 (complextionText+=(("\n"+getIndent( offset, text ))+"  "))
 
}
 return Map( "text" -> complextionText,
"displayText" -> x.nameId(),
"description" -> x.description(),
"category" -> categoryByRanges( x.nameId(), node.definition(), x.range() ) )
 
}) ))
 
}
if (c) {
 hlnode.definition().allProperties().filter( (x =>  (x.getAdapter( parserApi.ds.RAMLPropertyService ).isMerged()||(x.asInstanceOf[Property]).isFromParentKey())) ).forEach( (p =>  {
 if (onlyKey) {
 if ((!(p.asInstanceOf[Property]).isFromParentKey())) {
 return
 
}
 
}
 if (notAKey) {
 if ((p.asInstanceOf[Property]).isFromParentKey()) {
 return
 
}
 
}
 var prop = (p.asInstanceOf[Property])
 var oftenKeys = (p.asInstanceOf[Property]).getOftenKeys()
 if ((!oftenKeys)) {
 var sug = (p.asInstanceOf[Property]).suggester()
if (sug) {
 (oftenKeys=sug( hlnode ))
 
}
 
}
 if ((!oftenKeys)) {
 (oftenKeys=p.enumOptions())
 
}
 if ((hlnode.property()&&parserApi.universeHelpers.isBodyProperty( hlnode.property() ))) {
 if ((!oftenKeys)) {
 if (parserApi.universeHelpers.isResponseType( hlnode.property().domain() )) {
 (oftenKeys=Array( "application/json", "application/xml" ))
 
}
if ((parserApi.universeHelpers.isMethodBaseType( hlnode.property().domain() )||parserApi.universeHelpers.isMethodType( hlnode.property().domain() ))) {
 (oftenKeys=Array( "application/json", "application/xml", "multipart/form-data", "application/x-www-form-urlencoded" ))
 
}
 
}
 
}
 if (oftenKeys) {
 oftenKeys.forEach( (oftenKey =>  {
 var original = oftenKey
 var cs = prop.valueDocProvider()
 var description = ""
 if (cs) {
 (description=cs( oftenKey ))
 
}
 var proposedContainsSlash = ((oftenKey.indexOf( "/" )>=0))
 var requestContainsSlash = ((request.valuePrefix()&&(request.valuePrefix().indexOf( "/" )>=0)))
 var actualValue = (if (requestContainsSlash) oftenKey.replace( request.valuePrefix(), "" ) else oftenKey)
 var textValue = (actualValue+((if (needColon) ((((":"+"\n")+getIndent( offset, text ))+"  ")) else "")))
 var prefixValue = (if (proposedContainsSlash) request.valuePrefix() else null)
 rs.push( Map( "text" -> textValue,
"description" -> description,
"displayText" -> oftenKey,
"prefix" -> (if (requestContainsSlash) prefixValue else null),
"category" -> categoryByRanges( original, hlnode.definition(), prop.range() ) ) )
 
}) )
 
}
 
}) )
 
}
return rs
 
}
def isUnexspected(symbol: String): Boolean = {
 if ((symbol==="'")) {
 return true
 
}
if ((symbol==="\"")) {
 return true
 
}
return false
 
}
def isValueBroken(request: CompletionRequest) = {
 var text = request.content.getText()
var offset = request.content.getOffset()
var prefix = request.prefix()
var beginning = text.substring( 0, offset )
var value = beginning.substring( (beginning.lastIndexOf( ":" )+1) ).trim()
if ((!value.length)) {
 return false
 
}
if ((value((value.length-1))===",")) {
 if ((value.indexOf( "[" )<0)) {
 return true
 
}
 
}
if ((beginning((beginning.length-1))===" ")) {
 if (java.util.regex.Pattern.compile(raw"""^\w$$""").test( value((value.length-1)) )) {
 return true
 
}
else if ((value((value.length-1))===",")) {
 if ((value.indexOf( "[" )<0)) {
 return true
 
}
 
}
 
}
if (java.util.regex.Pattern.compile(raw"""^\w+$$""").test( prefix )) {
 (value=value.substring( 0, value.lastIndexOf( prefix ) ).trim())
if (java.util.regex.Pattern.compile(raw"""^\w$$""").test( value((value.length-1)) )) {
 return true
 
}
else if ((value((value.length-1))===",")) {
 if ((value.indexOf( "[" )<0)) {
 return true
 
}
 
}
 
}
if (isUnexspected( value((value.length-1)) )) {
 return true
 
}
return false
 
}
def valueCompletion(node: IParseResult, attr: IAttribute, request: CompletionRequest, provider: CompletionProvider) = {
 var hlnode = node.asInstanceOf[IHighLevelNode]
var text = request.content.getText()
var offset = request.content.getOffset()
if (isValueBroken( request )) {
 return Array()
 
}
if (attr) {
 var p: IProperty = attr.property()
var vl = attr.value()
if (((typeof(vl)==="object")&&vl)) {
 var innerNode = vl.toHighLevel().asInstanceOf[IHighLevelNode]
if (innerNode) {
 return getSuggestions( provider.currentRequest, provider, findASTNodeByOffset( innerNode, request ) )
 
}
else if (parserApi.search.isExampleNodeContent( attr )) {
 var contentType = parserApi.search.findExampleContentType( attr )
if (contentType) {
 var documentationRoot: IHighLevelNode = parserApi.search.parseDocumentationContent( attr, contentType.asInstanceOf[INodeDefinition] )
if (documentationRoot) {
 return getSuggestions( provider.currentRequest, provider, findASTNodeByOffset( documentationRoot, request ) )
 
}
 
}
 
}
 
}
if (p) {
 var vls = enumValues( p.asInstanceOf[Property], hlnode )
if ((p.asInstanceOf[Property]).isAllowNull()) {
 vls.push( Map( "text" -> "null",
"description" -> "null means - that no value is allowed" ) )
 
}
if (((!vls)||(vls.length==0))) {
 var oftenKeys = (p.asInstanceOf[Property]).getOftenKeys()
if (oftenKeys) {
 return oftenKeys.map( (x =>  {
 return Map( "text" -> x,
"displayText" -> x )
 
}) )
 
}
 
}
if ((universeHelpers.isExampleProperty( p )&&universeHelpers.isBodyLikeType( hlnode.definition() ))) {
 if ((!testVal( attr.value(), offset, text ))) {
 return
 
}
var rs = pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./examples/")
 (x.displayText=("!include ./examples/"+x.text))
 return x
 
}) )
(rs=addDefineInlineProposal( rs, attr.lowLevel().start(), text ))
return rs
 
}
if ((universeHelpers.isValueProperty( p )&&universeHelpers.isGlobalSchemaType( hlnode.definition() ))) {
 if ((!testVal( attr.value(), offset, text ))) {
 return
 
}
(rs=pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./schemas/")
 (x.displayText=("!include ./schemas/"+x.text))
 return x
 
}) ))
(rs=addDefineInlineProposal( rs, attr.lowLevel().start(), text ))
 
}
if (vls) {
 return vls
 
}
 
}
return Array()
 
}
else {
 if (universeHelpers.isGlobalSchemaType( hlnode.definition() )) {
 (rs=pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./schemas/")
 (x.displayText=("!include ./schemas/"+x.text))
 return x
 
}) ))
(rs=addDefineInlineProposal( rs, hlnode.lowLevel().start(), text ))
 
}
if (universeHelpers.isTraitType( hlnode.definition() )) {
 (rs=pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./traits/")
 (x.displayText=("!include ./traits/"+x.text))
 return x
 
}) ))
(rs=addDefineInlineProposal2( rs, hlnode.lowLevel().start(), text ))
return rs
 
}
if (universeHelpers.isResourceTypeType( hlnode.definition() )) {
 var rs = pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./resourceTypes/")
 (x.displayText=("!include ./resourceTypes/"+x.text))
 return x
 
}) )
(rs=addDefineInlineProposal2( rs, hlnode.lowLevel().start(), text ))
return rs
 
}
if (universeHelpers.isSecuritySchemaType( hlnode.definition() )) {
 var rs = pathCompletion( request, provider.contentProvider, attr, hlnode, true ).map( (x =>  {
 ((x.asInstanceOf[Any]).extra="!include ./securitySchemes/")
 (x.displayText=("!include ./securitySchemes/"+x.text))
 return x
 
}) )
(rs=addDefineInlineProposal2( rs, hlnode.lowLevel().start(), text ))
return rs
 
}
if (universeHelpers.isExampleSpecType( hlnode.definition() )) {
 return examplePropertyCompletion( hlnode, request, provider )
 
}
 
}
 
}
def findASTNodeByOffset(ast: IHighLevelNode, request: CompletionRequest): IParseResult = {
 var text = request.content.getText()
var cm = request.content.getOffset()
{
var pm = (cm-1)
while( (pm>=0)) {
 {
 var c = text(pm)
if (((c==" ")||(c=="\t"))) {
 (cm=pm)
continue
 
}
break()
 
}
 (pm-= 1)
}
}
var astNode = ast.findElementAtOffset( cm )
return astNode
 
}
def enumValues(property: Property, parentNode: IHighLevelNode): Array[Suggestion] = {
 if (parentNode) {
 if (property.getAdapter( parserApi.ds.RAMLPropertyService ).isTypeExpr()) {
 var associatedType = parentNode.associatedType()
var parentDefinition = parentNode.definition()
var noArraysOrPrimitives: Any = zeroOfMyType
var typeProperty = parentNode.attr( parserApi.universes.Universe10.TypeDeclaration.properties.`type`.name )
var typePropertyValue = (typeProperty&&typeProperty.value())
var typeProperties = (parentNode.children()&&parentNode.children().filter( (child =>  (child.isAttr()&&parserApi.universeHelpers.isTypeProperty( child.property() ))) ))
var visibleScopes: Array[String] = Array()
var api: Any = ((parentNode&&parentNode.root)&&parentNode.root())
(((api&&api.lowLevel())&&api.lowLevel().unit())&&visibleScopes.push( api.lowLevel().unit().absolutePath() ))
((((api&&api.wrapperNode)&&api.wrapperNode())&&api.wrapperNode().uses)&&api.wrapperNode().uses().forEach( (( usesDeclaration: Any ) =>  {
 if (((usesDeclaration&&usesDeclaration.value)&&usesDeclaration.value())) {
 var resolvedUnit = api.lowLevel().unit().resolve( usesDeclaration.value() )
if (resolvedUnit) {
 visibleScopes.push( resolvedUnit.absolutePath() )
 
}
 
}
 
}) ))
var definitionNodes = parserApi.search.globalDeclarations( parentNode ).filter( (node =>  {
 var nodeLocation = node.lowLevel().unit().absolutePath()
 if ((visibleScopes.indexOf( nodeLocation )<0)) {
 return false
 
}
 if (parserApi.universeHelpers.isGlobalSchemaType( node.definition() )) {
 return true
 
}
 var superTypesOfProposed = node.definition().allSuperTypes()
 if (_underscore_.find( superTypesOfProposed, (supertype =>  parserApi.universeHelpers.isTypeDeclarationType( supertype )) )) {
 var isMultiValue = ((((typePropertyValue&&property)&&property.isMultiValue())&&typeProperties)&&(typeProperties.length>1))
if (isMultiValue) {
 if ((!associatedType)) {
 try {
 (associatedType=parentNode.localType())
 
} catch { case exception: Throwable => {
 console.log( exception )
 
}}
 
}
if ((associatedType&&(!parentDefinition.hasUnionInHierarchy()))) {
 var supertypes = associatedType.superTypes().filter( (supertype =>  (!supertype.isAssignableFrom( "unknown" ))) )
if (supertypes) {
 var isExtendsObject = _underscore_.find( supertypes, (supertype =>  isObject( supertype )) )
var isExtendsPrimitive = _underscore_.find( supertypes, (supertype =>  isPrimitive( supertype )) )
var isExtendsArray = (_underscore_.find( supertypes, (supertype =>  isArray( supertype )) )||((parentDefinition&&isArray( parentDefinition ))))
var noObjects = (isExtendsArray||isExtendsPrimitive)
(noArraysOrPrimitives=(isExtendsObject||noObjects))
if (_underscore_.find( supertypes, (supertype =>  (parserApi.search.qName( node, parentNode )===supertype.nameId())) )) {
 return false
 
}
if ((noArraysOrPrimitives&&((isPrimitive( node.definition() )||isArray( node.definition() ))))) {
 return false
 
}
if ((noObjects&&isObject( node.definition() ))) {
 return false
 
}
 
}
 
}
if (parentDefinition.hasUnionInHierarchy()) {
 var unionClasses = allClassesForUnion( parentDefinition )
if (_underscore_.find( unionClasses, (unionPart =>  (parserApi.search.qName( node, parentNode )===(unionPart.asInstanceOf[NodeClass]).nameId())) )) {
 return false
 
}
 
}
 
}
return true
 
}
 return (universeHelpers.isTypeDeclarationType( node.definition() )&&(node.property().nameId()==="models"))
 
}) )
var result = definitionNodes.map( (node =>  {
 return Map( "text" -> search.qName( node, parentNode ),
"description" -> "" )
 
}) )
var typeDeclarationType = property.domain().universe().`type`( "TypeDeclaration" )
if (typeDeclarationType) {
 var subTypes = typeDeclarationType.allSubTypes()
(result=result.concat( subTypes.filter( (subType =>  {
 if ((noArraysOrPrimitives&&((isPrimitive( subType )||isArray( subType ))))) {
 return false
 
}
 return true
 
}) ).map( (subType =>  {
 return Map( "text" -> (subType.asInstanceOf[NodeClass]).getAdapter( services.RAMLService ).descriminatorValue(),
"description" -> (subType.asInstanceOf[NodeClass]).description() )
 
}) ) ))
 
}
return result
 
}
if (universeHelpers.isSchemaStringType( property.range() )) {
 if ((property.range().universe().version()==="RAML10")) {
 var definitionNodes = search.globalDeclarations( parentNode ).filter( (node =>  {
 if (universeHelpers.isGlobalSchemaType( node.definition() )) {
 return true
 
}
 var superTypesOfProposed = node.definition().allSuperTypes()
 if (_underscore_.find( superTypesOfProposed, (x =>  universeHelpers.isTypeDeclarationType( x )) )) {
 return true
 
}
 return (universeHelpers.isTypeDeclarationType( node.definition() )&&(node.property().nameId()==="models"))
 
}) )
var result = definitionNodes.map( (node =>  {
 return Map( "text" -> search.qName( node, parentNode ),
"description" -> "" )
 
}) )
var subTypes = search.subTypesWithLocals( property.domain().universe().`type`( "TypeDeclaration" ), parentNode )
(result=result.concat( subTypes.map( (subType =>  {
 return Map( "text" -> (subType.asInstanceOf[NodeClass]).getAdapter( services.RAMLService ).descriminatorValue(),
"description" -> (subType.asInstanceOf[NodeClass]).description() )
 
}) ) ))
return result
 
}
 
}
if (property.isDescriminator()) {
 var subTypes = search.subTypesWithLocals( property.domain(), parentNode )
return subTypes.map( (subType =>  {
 var suggestionText = (subType.asInstanceOf[NodeClass]).getAdapter( services.RAMLService ).descriminatorValue()
 return Map( "text" -> suggestionText,
"description" -> (subType.asInstanceOf[NodeClass]).description(),
"category" -> categoryByRanges( suggestionText, property.domain(), null ) )
 
}) )
 
}
if (property.isReference()) {
 return search.nodesDeclaringType( property.referencesTo(), parentNode ).map( (subType =>  {
 return nodeToProposalInfo( subType, parentNode )
 
}) ).asInstanceOf[Any]
 
}
if (property.range().hasValueTypeInHierarchy()) {
 var valueTypeAdapter = property.range().getAdapter( services.RAMLService )
if ((valueTypeAdapter.globallyDeclaredBy().length>0)) {
 var definitionNodes = search.globalDeclarations( parentNode ).filter( (proposedNode =>  {
 var proposedDefinition = proposedNode.definition()
 return (_underscore_.find( valueTypeAdapter.globallyDeclaredBy(), (globalDefinition =>  (globalDefinition==proposedDefinition)) )!=null)
 
}) )
return definitionNodes.map( (proposedNode =>  nodeToProposalInfo( proposedNode, parentNode )) ).asInstanceOf[Any]
 
}
if (universeHelpers.isBooleanTypeType( property.range() )) {
 return Array( "false", "true" ).map( (value =>  {
 return Map( "text" -> value )
 
}) ).asInstanceOf[Any]
 
}
var propertyNode = ((property.asInstanceOf[Any]).node&&(property.asInstanceOf[Any]).node())
if (propertyNode) {
 var suggestions: Array[Any] = _underscore_.filter( propertyNode.children(), (( child: Any ) =>  {
 return (((child.name&&child.value)&&child.property())&&universeHelpers.isEnumProperty( child.property() ))
 
}) ).map( (( child: Any ) =>  (Map( "text" -> (child.asInstanceOf[Any]).value() ))) )
return suggestions
 
}
 
}
 
}
return search.enumValues( property, parentNode ).map( (proposed =>  {
 return Map( "text" -> proposed,
"category" -> categoryByRanges( proposed, (parentNode&&parentNode.definition()), null ) )
 
}) ).asInstanceOf[Array[Suggestion]]
 
}
def isPrimitive(definition: Any) = {
 var isPrimitive = ((((!definition.isArray())&&(!isObject( definition )))&&(!definition.hasUnionInHierarchy()))&&(definition.key()!==universeModule.Universe10.TypeDeclaration))
return isPrimitive
 
}
def isObject(definition: Any) = {
 return (definition.isAssignableFrom( universeModule.Universe10.ObjectTypeDeclaration.name )||definition.isAssignableFrom( "object" ))
 
}
def isArray(definition: Any) = {
 return definition.isAssignableFrom( universeModule.Universe10.ArrayTypeDeclaration.name )
 
}
def allClassesForUnion(definition: Any): Array[Any] = {
 var result: Array[Any] = Array()
if (((!definition)||(!definition.isUnion()))) {
 return (if (definition) Array( definition ) else result)
 
}
if (definition.left) {
 result.push( definition.left )
return result.concat( allClassesForUnion( definition.right ) )
 
}
 
}
def addDefineInlineProposal(rs: Array[Any], offset: Int, text: String) = {
 (rs=Array( Map( "displayText" -> "Define Inline",
"text" -> (("|\n"+leadingIndent( (offset-1), text ))+"  ") ) ).concat( rs ))
return rs
 
}
def addDefineInlineProposal2(rs: Array[Any], offset: Int, text: String) = {
 (rs=Array( Map( "displayText" -> "Define Inline",
"text" -> (("\n"+leadingIndent( (offset-1), text ))+"  ") ) ).concat( rs ))
return rs
 
}
def leadingIndent(pos: Int, text: String) = {
 var leading = ""
while ((pos>0)) {
{
 var ch = text(pos)
if ((((ch=="\r")||(ch=="\n"))||(((ch!=" ")&&(ch!="-")))))
break()
(leading=(leading+" "))
(pos-= 1)
 
}
}
return leading
 
}
;
def getProposalText(proposal: {   var text: String
  var snippet: String
  var displayText: String
 }): String = {
 if (proposal.text) {
 return proposal.text
 
}
if (proposal.snippet) {
 return proposal.snippet
 
}
return proposal.displayText
 
}
def updateProposalText(proposal: {   var text: String
  var snippet: String
  var displayText: String
 }, textToUpdateWith: String) = {
 if (proposal.text) {
 (proposal.text=textToUpdateWith)
return
 
}
if (proposal.snippet) {
 (proposal.snippet=textToUpdateWith)
return
 
}
(proposal.displayText=textToUpdateWith)
 
}
def isSiblingExists(attr: Any, siblingName: Any) = {
 var parent = (attr.parent&&attr.parent())
if ((!parent)) {
 return false
 
}
var propertyName = (attr.name&&attr.name())
if ((!propertyName)) {
 return false
 
}
var siblings = (parent.attributes&&parent.attributes( propertyName ))
if ((!siblings)) {
 return false
 
}
if ((siblings.length===0)) {
 return false
 
}
var names: Array[Any] = Array()
siblings.forEach( (( sibling: Any ) =>  {
 var name = (((sibling.value&&sibling.value())&&sibling.value().valueName)&&sibling.value().valueName())
 if ((!name)) {
 return
 
}
 names.push( name )
 
}) )
return _underscore_.find( names, (name =>  (siblingName===name)) )
 
}
def isSquareBracketExpected(attr: Any) = {
 if ((!attr)) {
 return false
 
}
if ((!attr.definition())) {
 return false
 
}
if ((!attr.property())) {
 return false
 
}
if ((!attr.definition().isAssignableFrom( universeModule.Universe10.TraitRef.name ))) {
 return false
 
}
return true
 
}
def isInResourceDescription(obj: Any): Any = {
 var definition = (if ((obj&&obj.definition)) obj.definition() else null)
if (definition) {
 var name = definition.nameId()
if ((name==="Api")) {
 return false
 
}
if (((name==="ResourceType")||(name==="Trait"))) {
 return true
 
}
var parent = obj.parent()
if ((!parent)) {
 return false
 
}
return isInResourceDescription( parent )
 
}
return false
 
}
var transformers = parserApi.utils.getTransformerNames()
var addTransformers = (( proposals: Any, prefix: Any ) =>  {
 var result: Array[Any] = Array()
 transformers.filter( (transformer =>  {
 return (transformer.indexOf( prefix )===0)
 
}) ).forEach( (transformer =>  {
 result.push( Map( "displayText" -> transformer,
"text" -> transformer ) )
 
}) )
 return result.concat( proposals )
 
})
def testVal(vl: String, offset: Int, text: String) = {
 if ((vl&&(vl.length>0))) {
 var q = vl.trim()
if ((q.indexOf( "{" )==0)) {
 return false
 
}
if ((q.indexOf( "<" )==0)) {
 return false
 
}
if ((q.indexOf( "[" )==0)) {
 return false
 
}
 
}
{
var i = offset
while( (i>=0)) {
 {
 var c = text(i)
if ((c==":")) {
 return true
 
}
if ((c=="|")) {
 return false
 
}
if ((c=="'")) {
 return false
 
}
if ((c=="\"")) {
 return false
 
}
 
}
 (i-= 1)
}
}
return true
 
}
def nodeToProposalInfo(x: IHighLevelNode, c: IHighLevelNode) = {
 var isResourceType = false
var d = x.attr( "description" )
var ds = ""
if (d) {
 (ds=d.value())
 
}
else {
 (d=x.attr( "usage" ))
if (d) {
 (ds=d.value())
 
}
 
}
var tr = x.localType()
var req = tr.allProperties().filter( (x =>  (x.isRequired()&&(!x.getAdapter( services.RAMLPropertyService ).isKey()))) )
var txt = search.qName( x, c )
if ((!universeHelpers.isAnnotationTypeType( x.definition() ))) {
 if ((req.length>0)) {
 (txt+=": {")
(txt+=(req.map( (x =>  (x.nameId()+" : ")) ).join( ", " )+"}"))
var extra = ""
if (universeHelpers.isResourceTypeType( x.definition() )) {
 (txt=((""+txt)+" }"))
(extra=" { ")
(isResourceType=true)
 
}
 
}
 
}
return Map( "displayText" -> search.qName( x, c ),
"snippet" -> txt,
"description" -> ds,
"extra" -> extra,
"isResourceType" -> isResourceType )
 
}
def examplePropertyCompletion(node: Any, request: CompletionRequest, provider: CompletionProvider) = {
 if ((!search.isExampleNode( node ))) {
 return Array()
 
}
var contentType = search.findExampleContentType( node )
if ((!contentType))
return Array()
var parsedExample = search.parseStructuredExample( node, contentType )
if ((!parsedExample))
return Array()
var project = (((node&&node.lowLevel())&&node.lowLevel().unit())&&node.lowLevel().unit().project())
return getSuggestions( request, provider, findASTNodeByOffset( parsedExample, request ), project )
 
}
def postProcess(providerSuggestions: Any, request: CompletionRequest) = {
 var prepared: Array[Any] = postProcess1( providerSuggestions, request )
var added: Array[String] = Array()
var result: Array[Any] = Array()
prepared.forEach( (item =>  {
 var value = suggestionValue( item )
 if ((added.indexOf( value )<0)) {
 result.push( item )
added.push( value )
 
}
 
}) )
return result
 
}
def postProcess1(providerSuggestions: Any, request: CompletionRequest) = {
 var hasDeprecations: Any = zeroOfMyType
var hasEmpty: Any = zeroOfMyType
var suggestion: Any = zeroOfMyType
var _i: Any = zeroOfMyType
var _len: Any = zeroOfMyType
if ((providerSuggestions==null)) {
 return
 
}
if (hasDeprecations) {
 (providerSuggestions=providerSuggestions.map( (( suggestion: Any ) =>  {
 var newSuggestion: Any = zeroOfMyType
var _ref1: Any = zeroOfMyType
var _ref2: Any = zeroOfMyType
 (newSuggestion=Map( "text" -> (if ((((_ref1=suggestion.text))!=null)) _ref1 else suggestion.word),
"snippet" -> suggestion.snippet,
"replacementPrefix" -> (if ((((_ref2=suggestion.replacementPrefix))!=null)) _ref2 else suggestion.prefix),
"className" -> suggestion.className,
"type" -> suggestion.`type` ))
 if ((((newSuggestion.rightLabelHTML==null))&&suggestion.renderLabelAsHtml)) {
 (newSuggestion.rightLabelHTML=suggestion.label)
 
}
 if ((((newSuggestion.rightLabel==null))&&(!suggestion.renderLabelAsHtml))) {
 (newSuggestion.rightLabel=suggestion.label)
 
}
 return newSuggestion
 
}) ))
 
}
(hasEmpty=false)
{
((_i=0),(_len=providerSuggestions.length))
while( (_i<_len)) {
 {
 (suggestion=providerSuggestions(_i))
if ((!((suggestion.snippet||suggestion.text)))) {
 (hasEmpty=true)
 
}
if ((suggestion.replacementPrefix==null)) {
 (suggestion.replacementPrefix=getDefaultReplacementPrefix( request.prefix() ))
 
}
 
}
 (_i+= 1)
}
}
if (hasEmpty) {
 (providerSuggestions=((() =>  {
 var _j: Any = zeroOfMyType
var _len1: Any = zeroOfMyType
var _results: Any = zeroOfMyType
 (_results=Array())
 {
((_j=0),(_len1=providerSuggestions.length))
while( (_j<_len1)) {
 {
 (suggestion=providerSuggestions(_j))
if ((suggestion.snippet||suggestion.text)) {
 _results.push( suggestion )
 
}
 
}
 (_j+= 1)
}
}
 return _results
 
}))())
 
}
(providerSuggestions=filterSuggestions( providerSuggestions, request ))
return providerSuggestions
 
}
var fuzzaldrinProvider = require( "fuzzaldrin-plus" )
def filterSuggestions(suggestions: Array[Any], _arg: Any) = {
 var firstCharIsMatch: Any = zeroOfMyType
var i: Any = zeroOfMyType
var prefix: Any = zeroOfMyType
var prefixIsEmpty: Any = zeroOfMyType
var results: Any = zeroOfMyType
var score: Any = zeroOfMyType
var suggestion: Any = zeroOfMyType
var suggestionPrefix: Any = zeroOfMyType
var text: Any = zeroOfMyType
var _i: Any = zeroOfMyType
var _len: Any = zeroOfMyType
var _ref1: Any = zeroOfMyType
(prefix=_arg.prefix())
(results=Array())
{
((i=(_i=0)),(_len=suggestions.length))
while( (_i<_len)) {
 {
 (suggestion=suggestions(i))
(suggestion.sortScore=(Math.max( (((-i)/10)+3), 0 )+1))
(suggestion.score=null)
(text=(suggestion.snippet||suggestion.text))
(suggestionPrefix=(if ((((_ref1=suggestion.replacementPrefix))!=null)) _ref1 else prefix))
(prefixIsEmpty=((!suggestionPrefix)||(suggestionPrefix===" ")))
(firstCharIsMatch=((!prefixIsEmpty)&&(suggestionPrefix(0).toLowerCase()===text(0).toLowerCase())))
if (prefixIsEmpty) {
 results.push( suggestion )
 
}
if ((firstCharIsMatch&&(((score=fuzzaldrinProvider.score( text, suggestionPrefix )))>0))) {
 (suggestion.score=(score*suggestion.sortScore))
results.push( suggestion )
 
}
 
}
 (i=(++_i))
}
}
results.sort( reverseSortOnScoreComparator )
return results
 
}
var wordPrefixRegex = java.util.regex.Pattern.compile(raw"""^\w+[\w-]*$$""")
def reverseSortOnScoreComparator(a: Any, b: Any) = {
 var _ref1: Any = zeroOfMyType
var _ref2: Any = zeroOfMyType
return (((if ((((_ref1=b.score))!=null)) _ref1 else b.sortScore))-((if ((((_ref2=a.score))!=null)) _ref2 else a.sortScore)))
 
}
;
def getDefaultReplacementPrefix(prefix: Any) = {
 if (wordPrefixRegex.test( prefix )) {
 return prefix
 
}
else {
 return ""
 
}
 
}
;
def suggestionValue(suggestion: Any): String = {
 return (((suggestion&&((suggestion.displayText||suggestion.text))))||null)
 
}
var prefixRegex = java.util.regex.Pattern.compile(raw"""(\b|['"~`!@#\$$%^&*\(\)\{\}\[\]=\+,\/\?>])((\w+[\w-]*)|([.:;[{(< ]+))$$""")
def getPrefix(request: CompletionRequest): String = {
 var line: String = zeroOfMyType
var _ref1: Any = zeroOfMyType
(line=getLine( request ))
return (((if ((((_ref1=prefixRegex.exec( line )))!=null)) _ref1(2) else ))||"")
 
}
def getLine(request: CompletionRequest): String = {
 var offset: Int = request.content.getOffset()
var text: String = request.content.getText()
{
var i = (offset-1)
while( (i>=0)) {
 {
 var c = text.charAt( i )
if (((((c==="\r")||(c==="\n"))||(c===" "))||(c==="\t"))) {
 return text.substring( (i+1), offset )
 
}
 
}
 (i-= 1)
}
}
return ""
 
}
def getContentProvider(resolver: FSResolverExt): IFSProvider = {
 return new ResolvedProvider( resolver )
 
}


}
