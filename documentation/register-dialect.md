# AML - Register a Dialect or Semantic extension
### Register a dialect and start working on its custom instances
#### Hot reload enabled
To register a new Dialect and enable tooling in its dialect instances, it is sufficient to open the Dialect file if `hotReload` is enabled.

By receiving this `openFile`, ALS sends the dialect to AMF in order to parse and register it.

Once registered, you can work freely with any instance of this file for as long as this ALS instance is alive.

If you restart ALS, you will need to register the Dialect once again in order to get the desired tooling.

![VS Code Example](../images/vscode/register-dialect.gif)

Keep in mind that each change in the dialect will impact on the fly in ALS.

#### Hot reload disabled
To register a dialect when hot reload is disabled you should use the `indexDialect` [command](./features/custom-messages.md#indexdialect-command) to register a dialect globally
or the `didChangeConfiguration` [command](./features/custom-messages.md#didchangeconfiguration) to register to a workspace