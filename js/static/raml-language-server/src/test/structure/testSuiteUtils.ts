import utils = require("../../common/utils")
import index = require("../../index")
import fs = require("fs")
import path = require("path")
import parser = require("raml-1-parser")
import assert = require("assert")
import _ = require("underscore")

export function data(filepath: string): string {
    var datadir =  path.resolve(projectFolder(), 'src/test/data');
    return path.resolve(datadir, filepath).replace(/\\/g,'/');
}

var pathReplacer = function (str1:string,str2:string) {
    var l = str1.length;
    return function (key, value) {
        if(value) {
            if (typeof(value) == "object") {
                for (var k of Object.keys(value)) {
                    if (k.substring(0, l) == str1) {
                        var newKey = str2 + k.substring(l);
                        var val = value[k];
                        delete value[k];
                        value[newKey] = val;
                    }
                }
            }
            else if (typeof(value) == "string") {
                value = value.split(str1).join(str2);
            }
        }
        return value;
    };
};

var serializeTestJSON = function (outlineJsonPath:string, json:any) {
    var copy = JSON.parse(JSON.stringify(json));
    var rootPath = "file://"+data("").replace(/\\/g,"/");
    var replacer = pathReplacer(rootPath,"__$$ROOT_PATH__");
    fs.writeFileSync(outlineJsonPath, JSON.stringify(copy, replacer, 2));
};
var readTestJSON = function (outlineJsonPath:string) {
    var rootPath = "file://"+data("").replace(/\\/g,"/");
    var replacer = pathReplacer("__$$ROOT_PATH__",rootPath);
    return JSON.parse(fs.readFileSync(outlineJsonPath).toString(),replacer);
};

export function compare(arg0:any,arg1:any,path:string=''):Diff[] {

    var diffs:Diff[] = [];
    if(arg0==null){
        if(arg1!=null) {
            diffs.push(new Diff(path, arg0, arg1, 'Defined/undefined mismatch'));
            return diffs;
        }
    }
    else if(arg1==null){
        diffs.push(new Diff(path,arg0,arg1,'Defined/undefined mismatch'));
        return diffs;
    }
    else if(Array.isArray(arg0)){
        if(!Array.isArray(arg1)){
            diffs.push(new Diff(path,arg0,arg1,'Array/' + typeof(arg1)+' mismatch'));
            return diffs;
        }
        else {
            var l0 = arg0.length;
            var l1 = arg1.length;
            if (l1 != l0) {
                diffs.push(new Diff(path, arg0, arg1, 'Array lengths mismatch'));
                return diffs;
            }
            var l = Math.min(l0, l1);
            for (var i = 0; i < l; i++) {
                diffs = diffs.concat(compare(arg0[i], arg1[i], path + '[' + i + ']'));
            }
        }
    }
    else if(arg0 instanceof Object){
        if(!(arg1 instanceof Object)){
            diffs.push(new Diff(path,arg0,arg1,'Object/' + typeof(arg1)+' mismatch'));
            return diffs;
        }
        else {
            var keys0 = Object.keys(arg0);
            var keys1 = Object.keys(arg1);
            var map:{[key:string]:boolean} = {}
            for (var i = 0; i < keys0.length; i++) {
                var key = keys0[i];
                map[key] = true;
                var val0 = arg0[key];
                var val1 = arg1[key];
                diffs = diffs.concat(compare(val0, val1, path + '/' + key));
            }
            for (var i = 0; i < keys1.length; i++) {
                var key = keys1[i];
                if (map[key]) {
                    continue;
                }
                var val0 = arg0[key];
                var val1 = arg1[key];
                diffs = diffs.concat(compare(val0, val1, path + '/' + key));
            }
        }
    }
    else {
        if(arg0 !== arg1){
            diffs.push(new Diff(path,arg0,arg1,'Inequal values'));
        }
    }
    return diffs;
}

export class Diff{
    constructor(public path:string, public value0:any, public value1:any, public comment:string) {
        this.path = path;
        this.value0 = value0;
        this.value1 = value1;
        this.comment=comment;
    }

    message(label0?:string,label1?:string):string{
        label0 = label0||"value0";
        label1 = label1||"value1";

        var strValue0:string = "undefined";
        var strValue1:string = "undefined";
        if(this.value0!=null) {
            try {
                strValue0 = JSON.stringify(this.value0, null, 2).trim();
            }
            catch (err) {
                strValue0 = this.value0.toString();
            }
        }
        if(this.value1!=null) {
            try {
                strValue1 = JSON.stringify(this.value1, null, 2).trim();
            }
            catch (err) {
                strValue1 = this.value1.toString();
            }
        }

        return `path: ${this.path}
comment: ${this.comment}
${label0}: ${strValue0}
${label1}: ${strValue1}`
    }
}

var connection;

export function stopConnection() {
    if(connection) {
        connection.stop();
    }
}

function getOutlineJSONAsync(apiPath:string, callback: (result: Object, error: any) => void): void{
    apiPath = resolve(apiPath);
    let content = fs.readFileSync(apiPath).toString();

    connection = index.getNodeClientConnection();

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.positionChanged(apiPath, 0);

    connection.getStructure(apiPath).then(result=>{
        connection.documentClosed(apiPath);
        callback(result, null);
    }, ee => {
        callback(null, ee);
    });
}

function resolve(testPath: string): string {
    return utils.resolve( __dirname, '../../../src/test/data/' + testPath);
}

export function testOutline (
    apiPath:string, done: any, extensions?:string[],
    outlineJsonPath?:string,
    regenerateJSON:boolean=false,
    callTests:boolean=true):void{

    getOutlineJSONAsync(apiPath, (result, error) => {
        if(error) {
            done(error);
            
            return;
        }
        
        try{
            assert(testOutlineStructure(apiPath, result, extensions, outlineJsonPath, regenerateJSON, callTests));
            done();
        } catch (exception){
            done(exception);
        }
    });

}

function testOutlineStructure (
    apiPath:string, json: any, extensions?:string[],
    outlineJsonPath?:string,
    regenerateJSON:boolean=false,
    callTests:boolean=true): boolean{

    if(apiPath){
        apiPath = data(apiPath);
    }
    if(extensions){
        extensions = extensions.map(x=>data(x));
    }
    if(!outlineJsonPath){
        outlineJsonPath = defaultJSONPath(apiPath);
    }
    else{
        outlineJsonPath = data(outlineJsonPath);
    }
    var api = parser.loadRAMLSync(apiPath,extensions);
    var expanded = api;

    (<any>expanded).setAttributeDefaults(true);

    if(!outlineJsonPath){
        outlineJsonPath = defaultJSONPath(apiPath);
    }

    if(regenerateJSON) {
        serializeTestJSON(outlineJsonPath, json);
    }
    if(!fs.existsSync(outlineJsonPath)){
        serializeTestJSON(outlineJsonPath, json);
        if(!callTests){
            console.log("OUTLINE JSON GENERATED: " + outlineJsonPath);
            return;
        }
        console.warn("FAILED TO FIND OUTLINE JSON: " + outlineJsonPath);
    }
    if(!callTests){
        return;
    }


    var outlineJson:any = readTestJSON(outlineJsonPath);
    var pathRegExp = new RegExp('/errors\\[\\d+\\]/path');
    var messageRegExp = new RegExp('/errors\\[\\d+\\]/message');
    var diff = compare(json,outlineJson).filter(x=>{
        if(x.path.match(pathRegExp)){
            return false;
        }

        return true;
    });

    var diffArr = [];
    if(diff.length==0){
        return true;
    }
    else{
        // console.log("DIFFERENCE DETECTED FOR " + outlineJsonPath);
        // console.log(diff.map(x=>x.message("actual","expected")).join("\n\n"));

        return false;
    }
}

export function getRamlFirstLine(content:string):RegExpMatchArray{
    return content.match(/^\s*#%RAML\s+(\d\.\d)\s*(\w*)\s*$/m);
}

export function projectFolder() {
    var folder =  __dirname;
    while (!fs.existsSync(path.resolve(folder, "package.json"))) {
        folder = path.resolve(folder, "../");
    }
    return folder;
};

export class Test{

    constructor(
        public _masterPath:string,
        public _extensionsAndOverlays?:string[],
        public _jsonPath?:string){}

    masterPath():string{  return this._masterPath; }

    extensionsAndOverlays():string[]{ return this._extensionsAndOverlays; }

    jsonPath():string{ return this._jsonPath; }
}


export enum RamlFileKind{
    API, LIBRARY, EXTENSION, OVERLAY, FRAGMENT
}

export class RamlFile{

    constructor(
        private _absPath:string,
        private _kind:RamlFileKind,
        private _ver:string,
        private _extends?:string){}

    absolutePath():string{
        return this._absPath.replace(/\\/g,'/');
    }

    kind():RamlFileKind{
        return this._kind;
    }

    version():string{
        return this._ver;
    }

    extends():string{
        return this._extends.replace(/\\/g,'/');
    }
}

export class DirectoryContent{

    constructor(private dirAbsPath:string, private files:RamlFile[]){}

    absolutePath():string{
        return this.dirAbsPath.replace(/\\/g,'/');
    }

    allRamlFiles():RamlFile[]{
        return this.files;
    }

    extensionsAndOverlays():RamlFile[]{
        return this.files.filter(x=>x.kind()==RamlFileKind.EXTENSION||x.kind()==RamlFileKind.OVERLAY);
    }

    masterAPIs():RamlFile[]{
        return this.files.filter(x=>x.kind()==RamlFileKind.API);
    }

    fragments():RamlFile[]{
        return this.files.filter(x=>x.kind()==RamlFileKind.FRAGMENT);
    }

    libraries():RamlFile[]{
        return this.files.filter(x=>x.kind()==RamlFileKind.LIBRARY);
    }

    hasCleanAPIsOnly():boolean{
        return this.extensionsAndOverlays().length==0 && this.masterAPIs().length>0;
    }

    hasSingleExtensionOrOverlay():boolean{
        return this.extensionsAndOverlays().length==1 && this.masterAPIs().length>0;
    }

    hasExtensionsOrOverlaysAppliedToSingleAPI():boolean{
        return this.extensionsAndOverlays().length>0 && this.masterAPIs().length==1;
    }

    hasFragmentsOnly():boolean{
        return this.fragments().length == this.files.length;
    }

    hasLibraries():boolean{
        return this.libraries().length>0;
    }

    topExtensionOrOverlay():RamlFile{
        var arr = this.extensionsAndOverlays();
        var map = {};
        for(var x of arr){
            map[x.absolutePath()] = x;
        }
        for(var x of arr){
            var ext = x.extends();
            delete map[ext];
        }
        var keys = Object.keys(map);
        if(keys.length != 1){
            return null;
        }
        return map[keys[0]];
    }
}

function extractMasterRef(filePath:string){
    var raml = parser.loadRAMLSync(filePath,null);
    var extendsStr = raml.highLevel().attrValue("extends");
    if(!extendsStr){
        return null;
    }
    var result = path.resolve(path.dirname(filePath),extendsStr);
    return result;
}

export function extractContent(folderAbsPath:string):DirectoryContent{

    if(!fs.lstatSync(folderAbsPath).isDirectory()){
        return null;
    }

    var ramlFileNames = fs.readdirSync(folderAbsPath).filter(x=>path.extname(x).toLowerCase()==".raml");
    if(ramlFileNames.length==0){
        return null;
    }
    var ramlFilesAbsPaths = ramlFileNames.map(x=>path.resolve(folderAbsPath,x));
    var ramlFiles:RamlFile[] = [];
    for(var f of ramlFilesAbsPaths){
        var content = fs.readFileSync(f).toString();
        var ramlFirstLine = getRamlFirstLine(content);
        if(!ramlFirstLine||ramlFirstLine.length<2){
            continue;
        }
        var verStr = ramlFirstLine[1];
        var version = (verStr == "0.8") ? "RAML08" : "RAML10";
        var ramlFileType = "API";
        if(ramlFirstLine.length>2&&ramlFirstLine[2].trim().length>0){
            ramlFileType = ramlFirstLine[2].toUpperCase();
        }
        var kind = RamlFileKind[ramlFileType];
        if(kind==null){
            kind = RamlFileKind.FRAGMENT;
        }
        var extendsPath=null;
        if(kind==RamlFileKind.EXTENSION||kind==RamlFileKind.OVERLAY){
            extendsPath = extractMasterRef(f);
        }
        var ramlFile = new RamlFile(f,kind,version,extendsPath);
        ramlFiles.push(ramlFile);
    }
    if(ramlFiles.length==0){
        return null;
    }
    return new DirectoryContent(folderAbsPath,ramlFiles);
}

export function iterateFolder(folderAbsPath:string,result:DirectoryContent[]=[]):DirectoryContent[]{

    if(!fs.lstatSync(folderAbsPath).isDirectory()){
        return;
    }

    var dirContent = extractContent(folderAbsPath);
    if(dirContent!=null){
        result.push(dirContent);
        return result;
    }

    for(var ch of fs.readdirSync(folderAbsPath)){
        var childAbsPath = path.resolve(folderAbsPath,ch);
        if(fs.lstatSync(childAbsPath).isDirectory()){
            iterateFolder(childAbsPath,result);
        }
    }
    return result;
}

export function defaultJSONPath(apiPath:string) {
    var dir = path.dirname(apiPath);
    var fileName = path.basename(apiPath).replace(".raml", "-outline.json");
    var str = path.resolve(dir, fileName);
    return str;
};

function orderExtensionsAndOverlaysByIndex(ramlFiles:RamlFile[]):RamlFile[]{

    var indToFileMap:{[key:string]:RamlFile} = {};
    var pathToIndMap:{[key:string]:number} = {};
    for(var rf of ramlFiles){
        var fPath = rf.absolutePath();
        var fName = path.basename(fPath);
        var indStr = fName.replace(/([a-zA-Z]*)(\d*)(\.raml)/,"$2");
        indStr = indStr == "" ? "0" : "" + parseInt(indStr);
        var ind = parseInt(indStr);
        if(indToFileMap[indStr]){
            return null;
        }
        indToFileMap[indStr] = rf;
        pathToIndMap[rf.absolutePath()] = ind;
    }
    var sorted = _.sortBy(ramlFiles,x=>{
        return pathToIndMap[x.absolutePath()];
    });

    return sorted;
}

export function getTests(dirContent:DirectoryContent):Test[] {

    var result:Test[] = [];
    if (dirContent.hasCleanAPIsOnly()) {
        result = dirContent.masterAPIs().map(x=>new Test(x.absolutePath()));
    }
    else if (dirContent.hasSingleExtensionOrOverlay()) {
        result = dirContent.extensionsAndOverlays().map(x=>{
            var jsonPath = defaultJSONPath(x.extends());
            return new Test(x.absolutePath(),null,jsonPath);
        });
    }
    else if (dirContent.hasLibraries() && dirContent.masterAPIs().length == 0) {
        result = dirContent.libraries().map(x=>new Test(x.absolutePath()));
    }
    else if (dirContent.hasFragmentsOnly()) {
        result = dirContent.fragments().map(x=>new Test(x.absolutePath()));
    }
    else if (dirContent.hasExtensionsOrOverlaysAppliedToSingleAPI()) {
        var ordered = orderExtensionsAndOverlaysByIndex(dirContent.extensionsAndOverlays());
        if (ordered) {
            var apiPath = ordered[0].extends();
            var extensionsAndOverlays = ordered.map(x=>x.absolutePath());
            result = [ new Test(apiPath, extensionsAndOverlays) ];
        }
        else{
            var topExt = dirContent.topExtensionOrOverlay();
            if(topExt != null){
                result = [ new Test(topExt.absolutePath()) ];
            }
        }
    }
    return result;
}

function suiteTitle(absPath:string,dataRoot:string){

    var title = absPath.substring(dataRoot.length);
    if(title.length>0&&title.charAt(0)=="/"){
        title = title.substring(1);
    }
    return title;
}

function dumpSuite(title:string,dataRoot:string,tests:Test[],testMethodName:string):string{

    var dumpedTests = tests.map(x=>dumpTest(x,dataRoot,testMethodName));

    var testsStr = dumpedTests.join("\n\n");
    return`describe('${title}',function(){
    
${testsStr}
    
});`
}

function dumpTest(test:Test,dataRoot:string,testMethod:string):string{

    var relMasterPath = path.relative(dataRoot,test.masterPath()).replace(/\\/g,'/');;

    var args = [ `"${relMasterPath}"` ];

    if(test.extensionsAndOverlays()) {
        var relArr = test.extensionsAndOverlays().map(x=>path.relative(dataRoot, x).replace(/\\/g,'/'));
        if (relArr.length > 0) {
            args.push("[ " + relArr.map(x=>`"${x}"`).join(", ") + " ]");
        }
    }
    var jsonPath = test.jsonPath() ? path.relative(dataRoot,test.jsonPath()).replace(/\\/g,'/'):null;
    if(jsonPath!=null){
        if(!test.extensionsAndOverlays()){
            args.push("null");
        }
        args.push(`"${jsonPath}"`);
    }

    // var testMethod = 'testOutline';

    return`    it("${path.basename(path.dirname(test.masterPath()))}/${path.basename(test.masterPath())}", function () {
        this.timeout(15000);
        testSuiteUtil.${testMethod}(${args.join(", ")});
    });`
}

var toIncludePath = function (workingFolder:any, absPath:any) {
    var relPath = path.relative(workingFolder, absPath).replace(/\\/g, "/");
    if (!relPath || relPath.charAt(0)!=".") {
        relPath = "./" + relPath;
    }
    return relPath;
};

function fileContent(suiteStrings:string[],filePath:string,title:string) {

    var folder = projectFolder();
    var dstFolder = path.dirname(filePath);

    var suiteUtilPath = path.resolve(folder,"./src/test/scripts/testSuiteUtils");
    var typingsPath = path.resolve(folder,"typings/main.d.ts");
    var relSuiteUtilPath = toIncludePath(dstFolder, suiteUtilPath);
    var relTypingsPath = toIncludePath(dstFolder,typingsPath);
    return `/**
 * The file is generated. Manual changes will be overridden by the next build.
 */
/// <reference path="${relTypingsPath}" />
import testSuiteUtil = require("${relSuiteUtilPath}")

describe('${title}',function(){

${suiteStrings.join("\n\n")}

});

`
};

export function generateSuite(
    folderAbsPath:string,
    dstPath:string,
    dataRoot:string,
    mochaSuiteTitle:string,
    testMethodName:string){

    var dirs = iterateFolder(folderAbsPath);
    var map:{[key:string]:Test[]} = {};
    for(var dir of dirs){
        var tests = getTests(dir);
        if(tests.length>0){
            var suiteFolder = path.resolve(dir.absolutePath(),"../").replace(/\\/g,'/');
            var arr = map[suiteFolder];
            if(!arr){
                arr = [];
                map[suiteFolder] = arr;
            }
            for(var t of tests){
                arr.push(t);
            }
        }
    }

    var suitePaths = Object.keys(map).sort();
    var suiteStrings:string[] = [];
    for(var suitePath of suitePaths){
        var title = suiteTitle(suitePath,folderAbsPath);
        if(title==null){
            continue;
        }
        var suiteStr = dumpSuite(title,dataRoot,map[suitePath],testMethodName);
        suiteStrings.push(suiteStr);
    }
    var content = fileContent(suiteStrings,dstPath,mochaSuiteTitle);
    fs.writeFileSync(dstPath,content);
}

export function sleep(milliseconds) {
    var start = new Date().getTime();

    while(true) {
        if((new Date().getTime() - start) > milliseconds) {
            break;
        }
    }
}

function detailsPositionByRAMLFile(path) : number {
    var originalContent = fs.readFileSync(path).toString();
    return originalContent.indexOf("*");
}

function fixedDetailsRAML(path: string) : string {
    if (!fs.existsSync(path)){
        return null;
    }
    try {
        var originalContent = fs.readFileSync(path).toString();
        var markerPosition = originalContent.indexOf("*");
        if (markerPosition == -1) return originalContent;

        var contentsStart = originalContent.substring(0, markerPosition);
        var contentsEnd = markerPosition<originalContent.length-1?originalContent.substring(markerPosition+1):"";

        var resultContents = contentsStart+contentsEnd;
        return resultContents;
    } catch (e){
        return null;
    }
}

function getDetailsJSONAsync(apiPath:string, callback: (result: Object, error: any) => void): void{
    apiPath = resolve(apiPath);
    let content = fixedDetailsRAML(apiPath);
    let position = detailsPositionByRAMLFile(apiPath);

    connection = index.getNodeClientConnection();

    // connection.setLoggerConfiguration({
    //
    //     allowedComponents: [
    //         "NodeProcessServerConnection",
    //         "DetailsManager",
    //         "server"
    //     ],
    //     maxSeverity: 0,
    //     maxMessageLength: 500
    // });

    // connection.setLoggerConfiguration({
    //     maxSeverity: 4,
    //     maxMessageLength: 50
    // });

    connection.setServerConfiguration({
        modulesConfiguration: {
            enableDetailsModule: true,
            enableCustomActionsModule: false
        }
    })

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.getDetails(apiPath, position).then(result=>{
        connection.documentClosed(apiPath);
        callback(result, null);
    }, ee => {
        callback(null, ee);
    });
}

export function testDetails (
    apiPath:string, done: any, extensions?:string[],
    detailsJsonPath?:string,
    regenerateJSON:boolean=false,
    callTests:boolean=true):void{



    getDetailsJSONAsync(apiPath, (result, error) => {
        if(error) {
            done(error);

            return;
        }

        try{
            assert(testDetailsStructure(apiPath, result, extensions, detailsJsonPath, regenerateJSON, callTests));
            done();
        } catch (exception){
            done(exception);
        }
    });

}

export function testDetailsStructure (
    apiPath:string, json: any, extensions?:string[],
    detailsJsonPath?:string,
    regenerateJSON:boolean=false,
    callTests:boolean=true):boolean{

    if(apiPath){
        apiPath = data(apiPath);
    }

    if(extensions){
        extensions = extensions.map(x=>data(x));
    }
    if(!detailsJsonPath){
        detailsJsonPath = defaultJSONPath(apiPath);
    }
    else{
        detailsJsonPath = data(detailsJsonPath);
    }

    if(!detailsJsonPath){
        detailsJsonPath = defaultJSONPath(apiPath);
    }

    if(regenerateJSON) {
        serializeTestJSON(detailsJsonPath, json);
    }
    if(!fs.existsSync(detailsJsonPath)){
        serializeTestJSON(detailsJsonPath, json);
        if(!callTests){
            console.log("OUTLINE JSON GENERATED: " + detailsJsonPath);
            return;
        }
        console.warn("FAILED TO FIND OUTLINE JSON: " + detailsJsonPath);
    }
    if(!callTests){
        return;
    }


    var outlineJson:any = readTestJSON(detailsJsonPath);

    var pathRegExp = new RegExp('/errors\\[\\d+\\]/path');
    var messageRegExp = new RegExp('/errors\\[\\d+\\]/message');
    var diff = compare(json,outlineJson).filter(x=>{
        if(x.path.match(pathRegExp)){
            return false;
        }

        return true;
    });

    var diffArr = [];
    if(diff.length==0){
        assert(true);
        return true;
    }
    else{
        console.log("DIFFERENCE DETECTED FOR " + detailsJsonPath);
        console.log(diff.map(x=>x.message("actual","expected")).join("\n\n"));

        console.log("ORIGINAL:")
        console.log(JSON.stringify(outlineJson, null, 2))

        console.log("TEST RESULT:")
        console.log(JSON.stringify(json, null, 2))

        assert(false);
        return false;
    }
}