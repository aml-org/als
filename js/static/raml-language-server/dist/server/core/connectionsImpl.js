"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var AbstractServerConnection = (function () {
    function AbstractServerConnection() {
        this.openDocumentListeners = [];
        this.changeDocumentListeners = [];
        this.closeDocumentListeners = [];
        this.documentStructureListeners = [];
        this.documentCompletionListeners = [];
        this.openDeclarationListeners = [];
        this.findreferencesListeners = [];
        this.markOccurrencesListeners = [];
        this.renameListeners = [];
        this.documentDetailsListeners = [];
        this.changeDetailValueListeners = [];
        this.changePositionListeners = [];
        this.serverConfigurationListeners = [];
        this.calculateEditorContextActionsListeners = [];
        this.getAllEditorContextActionsListeners = [];
        this.executeContextActionListeners = [];
        this.executeDetailsActionListeners = [];
    }
    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onOpenDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.openDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onChangeDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.changeDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onCloseDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.closeDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onDocumentStructure = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentStructureListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onDocumentCompletion = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentCompletionListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onOpenDeclaration = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.openDeclarationListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onFindReferences = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.findreferencesListeners, listener, unsubsribe);
    };
    /**
     * Reports new calculated structure when available.
     * @param report - structure report.
     */
    AbstractServerConnection.prototype.structureAvailable = function (report) {
        // we dont need it
    };
    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    AbstractServerConnection.prototype.detailsAvailable = function (report) {
        // we dont need it
    };
    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    AbstractServerConnection.prototype.onMarkOccurrences = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.markOccurrencesListeners, listener, unsubsribe);
    };
    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    AbstractServerConnection.prototype.onRename = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.renameListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onDocumentDetails = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentDetailsListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener for specific action execution.
     * @param uri - document uri
     * @param actionId - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractServerConnection.prototype.onExecuteDetailsAction = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.executeDetailsActionListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document details value change request.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    AbstractServerConnection.prototype.onChangeDetailValue = function (listener, unsubsribe) {
        this.addListener(this.changeDetailValueListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     */
    AbstractServerConnection.prototype.onChangePosition = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.changePositionListeners, listener, unsubsribe);
    };
    /**
     * Calculates the list of executable actions available in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     * @param target - option target argument.
     *
     * "TARGET_RAML_EDITOR_NODE" and "TARGET_RAML_TREE_VIEWER_NODE" are potential values
     * for actions based on the editor state and tree viewer state.
     * "TARGET_RAML_EDITOR_NODE" is default.
     */
    AbstractServerConnection.prototype.onCalculateEditorContextActions = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.calculateEditorContextActionsListeners, listener, unsubsribe);
    };
    /**
     * Calculates the list of all available executable actions.
     */
    AbstractServerConnection.prototype.onAllEditorContextActions = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.getAllEditorContextActionsListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractServerConnection.prototype.onExecuteContextAction = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.executeContextActionListeners, listener, unsubsribe);
    };
    /**
     * Sets server configuration.
     * @param loggerSettings
     */
    AbstractServerConnection.prototype.onSetServerConfiguration = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.serverConfigurationListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    AbstractServerConnection.prototype.displayActionUI = function (uiDisplayRequest) {
        return Promise.reject(new Error("displayActionUI not implemented"));
        // TODO implement this
    };
    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    AbstractServerConnection.prototype.addListener = function (memberListeners, listener, unsubscribe) {
        if (unsubscribe === void 0) { unsubscribe = false; }
        if (unsubscribe) {
            var index = memberListeners.indexOf(listener);
            if (index !== -1) {
                memberListeners.splice(index, 1);
            }
        }
        else {
            memberListeners.push(listener);
        }
    };
    return AbstractServerConnection;
}());
exports.AbstractServerConnection = AbstractServerConnection;
//# sourceMappingURL=connectionsImpl.js.map