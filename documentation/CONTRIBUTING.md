# How to contribute to ALS

First off, thanks for taking the time to contribute!
The following is a set of guidelines for contributing to ALS:

## Did you find a bug?
- Ensure the bug was not already reported by searching on GitHub under Issues.
- If you're unable to find an open issue addressing the problem, open a new one. Be sure to include a title and clear description, as much relevant information as possible, 
and a code sample or an executable test case demonstrating the expected 
behavior that is not occurring.

## Contributing Changes
- Read and sign the [Contributors License Agreement](#contributors-license-agreement)
- Open a new GitHub pull request with the change.
- Ensure the PR description clearly describes the problem and solution. Include the relevant issue number if applicable.
- Before submitting, please read the [Code contributions](#code-contributions) section to know more about the technical contribution requirements.

## Code Contributions

### Development Requirements
* Scala 2.12.11
* sbt 1.3.8
* Node

### Version control branching
- Always branch from `master` branch to ensure you are updated with the latest release.
- Don’t submit unrelated changes in the same branch/pull request.
- If you need to update your branch because of changes in `master` you should always **rebase**, not **merge**.
- You should always be up-to-date with the latest changes in `master`.

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
sbt clean coverage testJVM coverageReport
```

### Contributors License Agreement

Please [read and accept the Contributors Agreement](https://api-notebook.anypoint.mulesoft.com/notebooks#380297ed0e474010ff43). 
This should automatically create a Github issue with the record of your signature [here](https://github.com/mulesoft/contributor-agreements/issues). 
If for any reason, you do not see your signature there, please contact us.
