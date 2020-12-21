# 1. Structure completion by node Vendor

Date: 2020-12-15

## Status

Accepted

## Context
Implemented from: [integration](../amf-integration/0001-vendor-for-node.md)

## Decision

We will use the most specific vendor to the node, to match against the dialect and lookup the expected structure.

## Consequences

Suggestion differences when there is an inlined element from another spec (for example raml types inside AsyncAPI 2.0)
