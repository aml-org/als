#!/bin/bash

cd ./als-server/js

echo 'SHACLValidator = require("amf-shacl-node")' > als-server.js
echo 'Ajv = require("ajv")' >> als-server.js
cat ./target/artifact/als-server.js >> als-server.js
chmod a+x als-server.js

cd ../..