# Features and modules
## Refactor features
##### The refactor features currently supported include: Formatting, Text Formatting, and a variety of Code Actions.
### [Formatting](https://microsoft.github.io/language-server-protocol/specification#textDocument_formatting)
![Formatting](../../images/vscode/formatting.gif)

### [Range Formatting](https://microsoft.github.io/language-server-protocol/specification#textDocument_rangeFormatting)
![Range Formatting](../../images/vscode/range-formatting.gif)

### [Rename](https://microsoft.github.io/language-server-protocol/specification#textDocument_rename)
![Playground Rename1](../../images/playground/rename.gif)
![Playground Rename2](../../images/playground/rename2.gif)
![Playground Rename3](../../images/playground/rename3.gif)

### [File Rename](./custom-messages.md#renamefile)
This is a [custom method](./custom-messages.md) for ALS. Using this method, you can rename a file and all its references inside the project.
![File Rename](../../images/vscode/file_rename.gif)

### [Code Action](https://microsoft.github.io/language-server-protocol/specification#textDocument_codeAction)
#### Extract Declaration
![Extract Declaration](../../images/vscode/extract_declaration.gif)

#### Delete declaration
![Delete declaration](../../images/vscode/delete.gif)

###### Code Actions just available for RAML at the moment:
#### Extract to Fragment
![Extract to Fragment](../../images/vscode/extract_to_fragment.gif)

#### Extract to Library
![Extract to Library](../../images/vscode/extract_to_library.gif)

#### Extract Resource Type or Trait
![Extract Resource Type](../../images/vscode/extract_resource_type.gif)

#### Convert to Json Schema
![Convert to Json Schema](../../images/vscode/convert_json_schema.gif)

#### Convert to Raml Type
![Convert to Raml Type](../../images/vscode/convert_to_raml.gif)


