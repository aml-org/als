# 1. Shape resolution on Extract actions

Date: 2020-02-05

## Status

Accepted

## Context
When extracting a Shape to declaration, external fragment or library we need to define whether we will resolve it previous to the extraction or not.
Resolving the shape before extraction will result on local and external shapes referenced by said shape being inlined in the extraction,
possibly duplicating information and removing references to external files. Otherwise, if we don't resolve the shape any referece this
shape has to a local declaration will be invalid upon extraction to a new file.


## Decision

As of now shapes extracted won't be resolved to preserve the original references. The only exception for this case are the actions
that perform a transformation extraction (such as RAML type to JSON Schema) as having a cross spec reference is not ideal. In the future we should
implement our own resolution method to resolve only the local references but leave the external references untouched.


## Consequences
 - Extracted shapes won't be resolved except in some special cases.
 - Upon extraction local references not included in the extraction will be invalid.
 - The extraction will preserve the external references.