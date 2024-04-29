#!/bin/bash
echo '**** Running script build ****'
mkdir -p lib
cd lib

rm -f als-server.js

cp ../tmp/als-server.js/main.js als-server.js
cp ../tmp/als-server.js/main.js ../dist/als-server.js
chmod a+x als-server.js

rm -f als-server.min.js

cp ../tmp/als-server.min.js/main.js als-server.min.js
cp ../tmp/als-server.min.js/main.js ../dist/als-server.min.js
chmod a+x als-server.min.js

cd ..
pwd
npm run build:dist
exit $?