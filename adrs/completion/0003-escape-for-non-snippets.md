# 1. Structure completion by node Vendor

Date: 2022-11-28

## Status

Accepted

## Context
Reported bug in [this ticket](https://gus.lightning.force.com/lightning/r/ADM_Work__c/a07EE000017Kf7TYAS/view)

## Decision
Fix the suggestions starting with dollar sign ($) using this regex to capture groups: ```(?<!\{)(\$)(?=\D\w+)```

## Consequences

