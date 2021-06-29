#!/bin/bash

mkdir -p dist
cd dist

rm -f als-node-client.min.js

echo 'Ajv = require("ajv")' >> als-node-client.min.js
cat ../../target/artifact/als-node-client.js >> als-node-client.min.js
chmod a+x als-node-client.min.js

cd ..