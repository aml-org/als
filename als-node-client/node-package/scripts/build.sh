@@ -1,12 +0,0 @@
#!/bin/bash

mkdir -p dist
cd dist

rm -f als-node-client.js
cp ../tmp/als-node-client.js/main.js als-node-client.js
chmod a+x als-node-client.js

rm -f als-node-client.min.js
cp ../tmp/als-node-client.min.js/main.js als-node-client.min.js
chmod a+x als-node-client.min.js

cd ..