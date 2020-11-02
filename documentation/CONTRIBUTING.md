## Setup

```
git clone https://github.com/mulesoft/als.git
```

### Mulesoft dependency references

#### Repositories
```
git clone https://github.com/aml-org/amf
git clone https://github.com/aml-org/amf-aml
git clone https://github.com/aml-org/amf-core
git clone https://github.com/mulesoft/syaml
git clone https://github.com/mulesoft/scala-common
```

## Build

#### Compilation

Launching compilation manually is optional, it is being automatically launched in artifact generation steps.
```
sbt compile
```

#### JS generation

Generate npm artifact as `als/als-server/js/node-package/` folder

```
sbt buildJsServerLibrary
```

#### Java generation

Generate Java artifact as `als/als-server/jvm/target/scala-2.12/als-server-assembly-{VERSION}}.jar` file

```
sbt serverJVM/assembly
```
This file is the LSP server jar.
To run this from the client application, you must create a local socket and provide the port as a parameter:

JS server development is in process of being developed, taking advantage of ScalaJS code-reusability features.

#### Generate test coverage report
```
sbt clean coverage testJVM coverageReport
```

### Architecture
#### ALS as a LSP Server
AML Language Server (ALS) integrates different modules with specific objectives, in one server module which orchestrates functionalities while respecting the [Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview).

![Modules](../images/LSP-diagram.png)

The Server Module communicates through a socket towards the client.

In order to achieve this, the client must create a Server Socket, and provide the selected port at the server initialization

![Modules](../images/LSP-complete-diagram.png)

Thanks to the LSP architecture, the client is completely agnostic of the Server language, which means that the `als-server.jar` can be used in a JS or Python plugin.

```
java -jar server.jar --port {XXXX}
```
During the server initialization process, it will connect to the provided Socket `(localhost, {port})` (connection which the client should accept)

Through this channel, the [LSP protocol](https://microsoft.github.io/language-server-protocol/specification) is respected. Server and client exchange information about accepted actions, and notifications/request can occur

##### [Features and modules](./features/features.md)
##### [Navigation features](./features/navigation.md)
##### [Eye-Candy features](./features/eyecandy.md)

## Usage examples

#### [JAVA (LSP4J)](./java-client-example/client.md) sample code

#### [JS (VS Code)](./js-client-example/client.md) sample code

#### Design Center example:
![AD Example](../images/usage-example-ad.gif)

#### Visual Studio Code completion example (LSP):
![VS Code Example](../images/usage-example-vscode.gif)

#### Visual Studio Code document symbol example (LSP):
![VS Code Example](../images/document-symbol-vscode.gif)
****
