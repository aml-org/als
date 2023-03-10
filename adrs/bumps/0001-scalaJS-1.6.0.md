# 1. Bump ScalaJS from 0.6 to 1.1

Date: 2023-03-10

## Status
In Progress

## Context
Bump ScalaJS from 0.6 to 1.1 had a lot of compatibility issues with some plugins and dependencies, like:

- sbt-scalajs: 1.6.0 
- scalatest
- upickle
- node v16.19.0


## Decision
1. We follow AMF suggestion to use `sbt-scalaJS:1.6.0` instead of `1.7.0+` beacuse of _RegEx adaption_
2. We found a `scalatest` version that has compatibility with `scalaJS 1`
3. `upickle` use `scalaJS 1` since `0.9.x`. [See more details](https://mvnrepository.com/artifact/com.lihaoyi/upickle_sjs1_2.12)
4. For node 16 we decide to use `scala-js-nodejs-v14` from `net-exoego`. We had to downgrade to _Node 14_ because this is used only for **Test mode** and for _Node 16_ there was just one version and this unique version requires `sbt-scalajs 1.8.0+`

## Issues
- Changing to latest `Upickle` version fix compatibility with `sbt-scalaJS` but fail all `als-suggestion` tests because the write function of an empty array change from:
`[]` to 
`[ \n ]`
- Some code had to been changed because of deprecated packages
- Some `@JSExport` annotations were changed to `@JSExportTopLevel`
- Workspaces name were removed from exported annotations
- `ScalaJSModuleKind` was replaced with `scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)
.withESFeatures(_.withESVersion(ESVersion.ES2016))
  }`
- Other replacements:
  - `fastOptJS` -> `fastLinkJS`
  - `fullOptJS` -> `fullLinkJS`
  - `artifactPath` -> `scalaJSLinkerOutputDirectory`
- Some commons dependencies do not on JS, so we need to add those using `libraryDependencies` in _JS Settings_ section.



### Internal Work Items
- [W-12658516](https://gus.lightning.force.com/a07EE00001MkxRJYAZ)
- [W-12601018](https://gus.lightning.force.com/a07EE00001LqaxfYAB)

 