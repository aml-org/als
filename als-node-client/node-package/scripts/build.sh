#!/bin/bash

mkdir -p dist
cd dist

rm -f als-node-client.js

echo 'SHACLValidator = require("amf-shacl-node")' > als-node-client.js
echo 'Ajv = require("ajv")' >> als-node-client.js
cat ../../target/scala-2.12/als-node-client-fastopt.js >> als-node-client.js
chmod a+x als-node-client.js

cd ..