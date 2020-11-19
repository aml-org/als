# Sublime Text 3 with ALS
##### Here is a quick example on how you can run ALS as a pure LSP in Sublime Text 3
_This example requires having the [ALS jar ready](../../readme.md#java-generation), and Java 1.8 or newer in PATH, as well as Sublime 3 installed._
### Steps
- Download the LSP plugin for ST3: [instructions here](https://lsp.readthedocs.io/en/latest/#getting-started)
- Create a new sublime syntax by going to `Tools -> Developer -> New Syntax...`. We will set the following fields:
```yaml
name: ALS
file_extensions:
  - raml
  - json
  - yaml
  - yml
scope: source.raml
``` 
And save it (from now on `Packages/ALS/ALS.sublime-syntax`)


- Finally, add the server to the User's LSP Settings by going to `Command Palette -> Preferences: LSP Settings` And adding the following node:
```json
{
	"clients":
	{
		"als":
		{
			"command":
			[
				"java",
				"-jar",
				"/path/to/als-server.jar",
				"--systemStream"
			],
			"enabled": true,
			"languageId": "als",
			"scopes":
			[
				"source.raml",
				"source.json",
				"source.yaml",
				"source.yml"
			],
			"syntaxes":
			[
				"Packages/ALS/ALS.sublime-syntax"
			]
		}
	},
	"log_debug": true,
	"log_payloads": true,
	"log_server": ["panel"],
	"log_stderr": true
}
```
_--systemStream parameter will have communication go through standard input/output instead of sockets_


### Running

![Sublime Example](../../images/sublime/up-running.gif)