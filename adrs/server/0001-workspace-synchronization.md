# 1. Workspace synchronization

Date: 2020-04-14

## Status

Accepted

## Context

Workspace instantiation and Unit parsing run in independent futures.
This results in some edge cases where when removing or adding a workspace resulted on a unit being placed in the wrong workspace,
due to the fact the workspace was not yet removed/added to the workspace content manager list in WorkspaceManager.

### Example

One of the adopters initializes als-server with a rootPath `file:///ws1/` Immediately after sending initialization message sends an `OPEN_FILE("file:///ws1/dialect.yaml")`.
Futures may happen in the following order:
- Starts to instance WCM for `file:///ws1/`
- Starts parsing `file:///ws1/dialect.yaml`
- Finishes parsing `file:///ws1/dialect.yaml` and saves the unit in the default workspace (because workspace `file:///ws1/` is not yet on the list)
- Finishes instancing WCM `file:///ws1/` and adds it to the workspace list
- Requests unit for `file:///ws1/dialect.yaml`, as the prefix `file:///ws1/` matches looks for the unit there, but the unit was saved on the default workspace so we get a UnitNotFoundException.

## Decision

- We will block WorkspaceManager when making changes to the workspace list, to wait for the latest changes to workspace list before parsing or getting a unit.
- We will also block WCM from processing when doing this to prevent unnecessary parsing.
