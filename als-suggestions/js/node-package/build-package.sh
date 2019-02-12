#!/bin/bash

mkdir -p lib
cd lib

rm -f als-suggestions.js
rm -f als-suggestions.min.js
echo 'SHACLValidator = require("amf-shacl-node")' > als-suggestions.js
echo 'Ajv = require("ajv")' >> als-suggestions.js
cat ../../target/scala-2.12/als-suggestions-fastopt.js >> als-suggestions.js
echo 'SHACLValidator = require("amf-shacl-node")' > als-suggestions.min.js
echo 'Ajv = require("ajv")' >> als-suggestions.min.js
cat ../../target/scala-2.12/als-suggestions-opt.js >> als-suggestions.min.js
chmod a+x als-suggestions.js
chmod a+x als-suggestions.min.js

cd ..