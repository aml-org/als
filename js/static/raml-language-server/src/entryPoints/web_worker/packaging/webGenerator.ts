import childProcess = require("child_process");
import fs = require("fs");
import mkdirp = require("mkdirp");
import path = require("path");
import rimraf = require("rimraf");
import webpack = require("webpack");

const rootPath = path.join(__dirname, "../../../../");

function createBrowserPackage(minify = false) {
    console.log("Minify: " + minify);

    const sourceClientFile = path.join(rootPath, "/dist/entryPoints/web_worker/client/launch.js");
    const sourceWorkerFile = path.join(rootPath, "/dist/entryPoints/web_worker/server/ramlServerWorker.js");

    const targetFolder = path.join(rootPath, "browserVersion");

    const targetClientFile = path.join(targetFolder, "ramlServerClient.js");
    const targetWorkerFile = path.join(targetFolder, "ramlServerWorker.js");

    mkdirp.sync(targetFolder);

    webPackForBrowser(rootPath, sourceClientFile, sourceWorkerFile, targetClientFile, targetWorkerFile, minify);
}

/**
 *
 * @param parserRootFolder - full path to cloned parser repository root folder
 * @param rootFile - full path to parser index JS file
 * @param targetFileName
 * @param callback
 */
function webPackForBrowser(parserRootFolder: string, sourceClientFile: string, sourceWorkerFile: string,
                           targetClientFile: string, targetWorkerFile: string, minify: boolean) {
    console.log("Preparing to Webpack browser bundle: client.js");

    const plugins = [];
    if (minify) {
        plugins.push(new webpack.optimize.UglifyJsPlugin({
            minimize: true,
            compress: { warnings: false }
        }));
    }

    let relativeSourceClientFile = path.relative(parserRootFolder, sourceClientFile);
    relativeSourceClientFile = "./" + relativeSourceClientFile;

    let relativeSourceWorkerFile = path.relative(parserRootFolder, sourceWorkerFile);
    relativeSourceWorkerFile = "./" + relativeSourceWorkerFile;

    const targetFolder = path.dirname(targetClientFile);
    const baseTargetClientFileName = path.basename(relativeSourceClientFile);
    const baseTargetWorkerFileName = path.basename(relativeSourceWorkerFile);

    const config = {
        context: parserRootFolder,

        entry: {
            client: relativeSourceClientFile,
            worker: relativeSourceWorkerFile
        },

        output: {
            path: targetFolder,

            library: ["RAML", "Server"],

            filename: "[name].bundle.js",

            libraryTarget: "umd"
        },

        plugins,
        resolve: {
            alias: {
                fs: path.resolve(__dirname, "../../../../web-tools/emptyFS.js"),
                'raml-1-parser': path.resolve(__dirname, "../../../../node_modules/raml-1-parser")
            }
        },

        module: {
            loaders: [
                { test: /\.json$/, loader: "json" }
            ]
        },
        externals: [
            {
                "libxml-xsd" : true,
                "ws" : true,
                "typescript" : true,
                "raml-xml-validation": "RAML.XmlValidation",
                "raml-json-validation": "RAML.JsonValidation"
            }
        ],
        node: {
            console: false,
            global: true,
            process: true,
            Buffer: true,
            __filename: true,
            __dirname: true,
            setImmediate: true
        }
    };

    webpack(config, function(err, stats) {
        if (err) {
            console.log(err.message);

            return;
        }

        console.log("Webpack Building Browser Bundle:");

        console.log(stats.toString({reasons : true, errorDetails: true}));
    });
}

export function copyDirSyncRecursive(
    to: string,
    from: string): void {

    if (path.resolve(to) === path.resolve(from)) {
        return;
    }

    if (!fs.lstatSync(from).isDirectory()) {

        mkdirp.sync(path.dirname(to));

        const buffer = fs.readFileSync(from);

        fs.writeFileSync(to, buffer);

        return;
    }

    fs.readdirSync(from).forEach((x) => {

        const fromChild = path.resolve(from, x);

        const toChild = path.resolve(to, x);
        copyDirSyncRecursive(toChild, fromChild);
    });
}

function generateBrowserNpmJSON() {
    const targetNPMPath = path.join(rootPath, "./browser_version_npm");

    const targetPackageJSONPath = path.resolve(targetNPMPath, "./package.json");
    const sourcePackageJsonPath = path.resolve(rootPath, "./package.json");

    const packageJson = JSON.parse(fs.readFileSync(sourcePackageJsonPath).toString());

    const targetJson: any = {};
    targetJson.version = packageJson.version;
    targetJson.name = packageJson.name + "-browser";
    targetJson.main = "client.bundle.js";

    fs.writeFileSync(targetPackageJSONPath, JSON.stringify(targetJson, null, "\t"));
}

function createBrowserNPM() {
    const browserVersionPath = path.join(rootPath, "./browserVersion");
    const browserNpmPath = path.join(rootPath, "./browser_version_npm");

    rimraf.sync(browserNpmPath);

    mkdirp.sync(browserNpmPath);

    generateBrowserNpmJSON();

    copyDirSyncRecursive(browserNpmPath, browserVersionPath);
}

function publishBrowserNPM() {
    const browserNpmPath = path.join(rootPath, "./browser_version_npm");
    childProcess.execSync("cd " + browserNpmPath + " && npm publish");
}

function createAndPublishBrowserNPM() {

    createBrowserNPM();
    publishBrowserNPM();
}

createBrowserPackage();

declare var process: any;
const isNpm = process.argv[process.argv.indexOf("--type") + 1] === "npm";

if (isNpm) {
    createAndPublishBrowserNPM();
}
