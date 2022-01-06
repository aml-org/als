# Custom Validations

> **WARNING:** Custom Validations features are only present in the JS ALS distribution. The JVM distribution will have support for custom validations in the future.

Custom validation allows users to write their own custom constraints to be used by ALS when validating files.

## Indexing a profile
Custom validation profiles should be indexed per workspace folder by using `didChangeConfiguration` command.

## Usage
After indexing the profile ALS will automatically apply the profile and send the diagnostics derived from the validation.
