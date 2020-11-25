# How to contribute to ALS

First off, thanks for taking the time to contribute! The following is a all you need to know in order to contribute to ALS.

## Contributing

### Overview
- Open a new GitHub pull request with the change (if you're contributing for the first time on a Salesforce project, you will be asked to sign Salesforce CLA)
- Ensure the PR description clearly describes the problem and solution. Include the relevant issue number if applicable.
- Before submitting, please read the [Code contributions](#code-contributions) section below to understand the technical contribution requirements.

### Did you find a bug?
- Ensure the bug was not already reported by searching on GitHub under Issues
- If you're unable to find an open issue addressing the problem, open a new one. Be sure to include a title and clear description, as much relevant information as possible, 
and a code sample or an executable test case demonstrating the expected 
behavior that is not occurring
- Relevant information includes communication traces between client and server (ALS), and the corresponding Logs

## Development setup

**Requirements:**
* Scala 2.12.11
* sbt 1.3.8
* NodeJS

Cloning the repo:
```sh
$ git clone https://github.com/mulesoft/als.git
```

Generating npm artifacts (at `als/als-server/js/node-package/`):

```sh
$ sbt buildJsServerLibrary
```

Generating Java artifacts (at `als/als-server/jvm/target/scala-2.12/als-server-assembly-{VERSION}}.jar`):

```sh
$ sbt serverJVM/assembly
```

**Notes:**
- this file is the actual LSP server jar
- to run this from the client application, you must create a local socket and provide the port as a parameter. See the [ALS as a LSP Server](../readme.md#als-as-a-lsp-server) section of the README to understand how ALS interacts with IDEs
- JS server development is still in progress, taking advantage of ScalaJS code-reusability features, all features may not work

### Version control branching
- Always branch from `master` branch to ensure you are updated with the latest release
- Don’t submit unrelated changes in the same branch/pull request
- If you need to update your branch because of changes in `master` you should always **rebase**, not **merge**
- You should always be up-to-date with the latest changes in `master`

### Code formatting

We use [Scalafmt](https://scalameta.org/scalafmt/) to format our code! Please format your code before opening a Pull Request.

### Running and writing tests

**Important**: Please include tests with any code contributions

Writing tests before the implementation is strongly encouraged. 

To run tests:
```sh
$ sbt test
```

#### Test coverage

Contributions must comply with a minimum of 80% coverage rate.

To run a coverage report of the whole project:
```sh
$ sbt clean coverage testJVM coverageReport
```

## Architecture

### ALS as a LSP Server
AML Language Server (ALS) integrates different modules with specific objectives, in one server module which orchestrates functionalities while respecting the [Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview).

![Modules](../images/LSP-diagram.png)

The Server Module communicates through a socket towards the client.

In order to achieve this, the client must create a Server Socket, and provide the selected port at the server initialization

![Modules](../images/LSP-complete-diagram.png)

Thanks to the LSP architecture, the client is completely agnostic of the Server language, which means that the `als-server.jar` can be used in a JS or Python plugin.

```sh
$ java -jar server.jar --port {XXXX}
```
During the server initialization process, it will connect to the provided Socket `(localhost, {port})` (connection which the client should accept)

Through this channel, the [LSP protocol](https://microsoft.github.io/language-server-protocol/specification) is respected. Server and client exchange information about accepted actions, and notifications/request can occur

- [Features and modules](./features/features.md)
- [Navigation features](./features/navigation.md)
- [Eye-Candy features](./features/eyecandy.md)

### Internal dependency references
```
https://github.com/aml-org/amf
https://github.com/aml-org/amf-aml
https://github.com/aml-org/amf-core
https://github.com/aml-org/syaml
https://github.com/aml-org/scala-common
```
