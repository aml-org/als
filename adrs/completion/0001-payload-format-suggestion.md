# 1. Payload completion by MediaType

Date: 2020-12-15

## Status

Accepted

## Context

There are specs which may use different spec schemas inside its payload (for example OpenAPI 3.0 and AsyncAPI 2.0).

## Decision

We will identify the corresponding information given a payloads mediaType, matching and suggesting accordingly.
