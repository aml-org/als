import util = require("./testSuiteUtils")

beforeEach(function () {
    util.sleep(100);
});

describe('Async outline test set',function() {
    this.timeout(30000);
    
    it("test001/api.raml", function (done) {
        util.testOutline("structure/test001/api.raml", done);
    });

    it("test002/api.raml", function (done) {
        util.testOutline("structure/test002/api.raml", done);
    });

    it("test003/api.raml", function (done) {
        util.testOutline("structure/test003/api.raml", done);
    });

    it("test004/api.raml", function (done) {
        util.testOutline("structure/test004/api.raml", done);
    });

    it("test005/api.raml", function (done) {
        util.testOutline("structure/test005/api.raml", done);
    });

    it("test006/api.raml", function (done) {
        util.testOutline("structure/test006/api.raml", done);
    });

    it("test007/api.raml", function (done) {
        util.testOutline("structure/test007/api.raml", done);
    });

    it("test008/api.raml", function (done) {
        util.testOutline("structure/test008/api.raml", done);
    });

    it("test009/api.raml", function (done) {
        util.testOutline("structure/test009/api.raml", done);
    });

    it("test010/api.raml", function (done) {
        util.testOutline("structure/test010/api.raml", done);
    });

    it("test011/api.raml", function (done) {
        util.testOutline("structure/test011/api.raml", done);
    });

    it("test012/api.raml", function (done) {
        util.testOutline("structure/test012/api.raml", done);
    });

    it("test013/api.raml", function (done) {
        util.testOutline("structure/test013/api.raml", done);
    });

    it("test014/api.raml", function (done) {
        util.testOutline("structure/test014/api.raml", done);
    });

    it("test015/api.raml", function (done) {
        util.testOutline("structure/test015/api.raml", done);
    });

    it("test016/api.raml", function (done) {
        util.testOutline("structure/test016/api.raml", done);
    });

    it("test017/api.raml", function (done) {
        util.testOutline("structure/test017/api.raml", done);
    });

    it("test018/api.raml", function (done) {
        util.testOutline("structure/test018/api.raml", done);
    });

    it("test019/api.raml", function (done) {
        util.testOutline("structure/test019/api.raml", done);
    });

    it("test020/api.raml", function (done) {
        util.testOutline("structure/test020/api.raml", done);
    });

    it("test021/api.raml", function (done) {
        util.testOutline("structure/test021/api.raml", done);
    });

    it("test022/api.raml", function (done) {
        util.testOutline("structure/test022/api.raml", done);
    });

    it("test023/api.raml", function (done) {
        util.testOutline("structure/test023/api.raml", done);
    });

    it("test024/api.raml", function (done) {
        util.testOutline("structure/test024/api.raml", done);
    });

    it("test025/api.raml", function (done) {
        util.testOutline("structure/test025/api.raml", done);
    });

    it("test028/api.raml", function (done) {
        util.testOutline("structure/test028/api.raml", done);
    });

    it("test029/api.raml", function (done) {
        util.testOutline("structure/test029/api.raml", done);
    });

    it("test030/api.raml", function (done) {
        util.testOutline("structure/test030/api.raml", done);
    });

    it("test031/api.raml", function (done) {
        util.testOutline("structure/test031/api.raml", done);
    });
});

after(function() {
    util.stopConnection();
});