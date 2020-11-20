# IntelliJ ALS-Client
##### Here is a quick example on how you can run ALS as a pure LSP in IntelliJ
_This example requires having the [ALS jar ready](../../readme.md#java-generation), and Java 1.8 or newer in PATH, as well as [IntelliJ LSP Plugin](https://plugins.jetbrains.com/plugin/10209-lsp-support)._

### Steps

1. With the [IntelliJ LSP Plugin](https://plugins.jetbrains.com/plugin/10209-lsp-support) installed, go to `Preferences -> Languages & Frameworks -> Language Server Protocol -> Server Definitions`
2. Add a new server as a `Raw command`, selecting the desired extension (in this example `raml`) and inserting the following command: `java -jar /path/to/als-server.jar --systemStream`
![IntelliJ Configuration](../../images/intellij/configuration.png)
_--systemStream parameter will have communication go through standard input/output instead of sockets_

### Running

![IntelliJ Example](../../images/intellij/up-running.gif)