import path = require("path")
import suiteUtil = require("./testSuiteUtils")

var args = process.argv;
var structureInputDataDir = null;
var structureMochaFile = null;
var detailsInputDataDir = null;
var detailsMochaFile = null;

var dataRoot = null;

for(var i = 0 ; i < args.length ; i++){
    if(i < args.length-1) {
        if (args[i] == "-structureInputDataDir") {
            structureInputDataDir = args[++i];
        }
        else if (args[i] == "-structureMochaFile") {
            structureMochaFile = args[++i];
        }
        else if (args[i] == "-dataRoot") {
            dataRoot = args[++i];
        }
    }
}

if(structureInputDataDir==null){
    structureInputDataDir = path.resolve(suiteUtil.projectFolder(),"src/test/data/structure");
}

if(structureMochaFile==null){
    structureMochaFile = path.resolve(suiteUtil.projectFolder(),"src/test/structure_suite.ts");
}

if(detailsInputDataDir==null){
    detailsInputDataDir = path.resolve(suiteUtil.projectFolder(),"src/test/data/details");
}

if(detailsMochaFile==null){
    detailsMochaFile = path.resolve(suiteUtil.projectFolder(),"src/test/details_suite.ts");
}

if(dataRoot==null){
    dataRoot = path.resolve(suiteUtil.projectFolder(),"src/test/data")
}

suiteUtil.generateSuite(
    structureInputDataDir,
    structureMochaFile,
    dataRoot,
    'Outline Test Set',
    'testOutline'
);

suiteUtil.generateSuite(
    detailsInputDataDir,
    detailsMochaFile,
    dataRoot,
    'Details Test Set',
    'testDetails'
);

