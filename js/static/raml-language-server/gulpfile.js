var gulp = require('gulp');
var mocha = require('gulp-mocha');
var join = require('path').join;
var fs = require('fs');
var path = require('path');
var istanbul = require('gulp-istanbul');

var testFiles = [
    "dist/test/suggestions/suggestionsTests.js",
    "dist/test/parsertests/parserTests.js",
    "dist/test/parsertests/parserTests2.js",
    "dist/test/structure/structureTests.js",
    "dist/test/structure/detailsTests.js",
    "dist/test/fixedActions/fixedActionTests.js"
];

var testFilesComplete = [
    "dist/test/suggestions/suggestionsTests.js",
    "dist/test/parsertests/parserTests.js",
    "dist/test/parsertests/parserTests2.js",
    "dist/test/parsertests/parserTests3.js",
    "dist/test/structure/structureTests.js",
    "dist/test/structure/detailsTests.js",
    "dist/test/parsertests/astReuseTestsBasicTyping.js",
    "dist/test/fixedActions/fixedActionTests.js",
    "dist/test/longevity/editorManagerTests.js",
    "dist/test/longevity/validationManagerTests.js",
    "dist/test/longevity/completionManagerTests.js",
    "dist/test/longevity/detailsManagerTests.js",
    "dist/test/longevity/structureManagerTests.js"
];

gulp.task('test', function() {
    global.isExpanded = null;
    
    return gulp.src(testFiles, {
        read: false
    }).pipe(mocha({
        bail: false,
        reporter: 'spec'
    }));
});

gulp.task('testComplete', function() {
    global.isExpanded = null;

    return gulp.src(testFilesComplete, {
        read: false
    }).pipe(mocha({
        bail: false,
        reporter: 'spec'
    }));
});