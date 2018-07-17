# Setup

```
git clone https://github.com/mulesoft/als.git
```

## Dependencies setup

This step is optional if ALS dependencies are being grabbed from nexus and required if building dependencies from sources.

Currently als-outline is not published in nexus due to travis issues, so building from sources is the only option.

Checking out and publishing als-hl locally:
```
git clone https://github.com/mulesoft/als-hl.git
cd als-hl
sbt publish-local
cd ..
```

Checking out and publishing als-suggestions locally:
```
git clone https://github.com/mulesoft/als-suggestions.git
cd als-suggestions
sbt publish-local
cd ..
```

Checking out and publishing als-outline locally:
```
git clone https://github.com/mulesoft/als-outline.git
cd als-outline
sbt publish-local
cd ..
```

Note: this instruction assumes AMF being available from nexus.

# Build

## Compilation

Launching compilation manually is optional, it is being automatically launched in artifact generation steps. 
```
sbt compile
```

## JavaScript generation

Generate JS artifact as `js/target/artifact/serverProcess.js` file

```
sbt buildJS
```

This file is being used in Atom client as external server NPM process.
## Java generation

Generate Java artifact as `jvm/target/scala-2.12/server.jar` file

```
sbt coreJVM/assembly
```
This file is being used in Eclipse client in a separate server thread.
