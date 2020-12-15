# 1. Vendor for Node

Date: 2020-12-15

## Status

Accepted

## Context
We need to contemplate cases in which the vendor by which a node is processed switches from its parents.
Such is the case for example in RAML types defined inside an AsyncAPI 2 spec. 

## Decision

Based on [ALS-1323: Sync on how we should inject RAML Type facets in Async](https://www.mulesoft.org/jira/browse/ALS-1323) we look for the AMF Annotation (_DefinedByVendor_) which marks a switch in the used vendor for nested nodes.

## Consequences
Specific nodes will now correlate to the most specific vendor, which allows us to provide different behaviour on actions (_for example "extract to fragment"_) and completion (matching the structure with the correct dialect)

Implemented: [completion](../completion/0002-type-suggestion-by-vendor.md)