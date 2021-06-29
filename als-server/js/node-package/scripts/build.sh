#!/bin/bash

mkdir -p lib
cd lib

rm -f als-server.js

echo 'Ajv = require("ajv")' >> als-server.js
cat ../../target/artifact/als-server.js >> als-server.js
chmod a+x als-server.js

rm -f als-server.min.js

echo 'Ajv = require("ajv")' >> als-server.min.js
cat ../../target/artifact/als-server.min.js >> als-server.min.js
chmod a+x als-server.min.js

cd ..

npm run build:dist

exit $?
