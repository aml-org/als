"use strict";
// This module handles contents of editors opened in IDE and provides this information to other modules
Object.defineProperty(exports, "__esModule", { value: true });
function createManager(connection) {
    return new EditorManager(connection);
}
exports.createManager = createManager;
var TextBufferInfo = (function () {
    function TextBufferInfo(uri, editorManager, logger) {
        this.uri = uri;
        this.editorManager = editorManager;
        this.logger = logger;
        // TODO add border checks
        this.text = "";
    }
    /**
     * Gets offset from the beginning of the document by the position
     * @param position
     */
    TextBufferInfo.prototype.characterIndexForPosition = function (position) {
        var lineStartOffset = 0;
        for (var i = 0; i <= position.row - 1; i++) {
            lineStartOffset += this.lineLengths[i];
        }
        var result = lineStartOffset + position.column;
        this.logger.debugDetail("characterIndexForPosition:" +
            ": [" + position.row + ":" + position.column + "] = " + result, "EditorManager", "TextBufferInfo#characterIndexForPosition");
        return result;
    };
    /**
     * Gets position by the offset from the beginning of the document.
     * @param offset
     */
    TextBufferInfo.prototype.positionForCharacterIndex = function (offset) {
        var pos = offset;
        for (var i = 0; i < this.lineLengths.length; i++) {
            var lineLength = this.lineLengths[i];
            if (pos < lineLength) {
                this.logger.debugDetail("positionForCharacterIndex:" + offset +
                    ": [" + i + ":" + pos + "]", "EditorManager", "TextBufferInfo#positionForCharacterIndex");
                return {
                    row: i,
                    column: pos
                };
            }
            pos -= lineLength;
        }
        if (pos === 0) {
            var row = this.lineLengths.length - 1;
            var column = this.lineLengths[this.lineLengths.length - 1];
            this.logger.debugDetail("positionForCharacterIndex:" + offset +
                ": [" + row + ":" + column + "]", "EditorManager", "TextBufferInfo#positionForCharacterIndex");
            return {
                row: row,
                column: column
            };
        }
        var errorMessage = "Character position exceeds text length: " + offset + " > + " + this.text.length;
        this.logger.error(errorMessage, "EditorManager", "TextBufferInfo#positionForCharacterIndex");
        throw new Error(errorMessage);
    };
    /**
     * Gets a range for the row number.
     * @param row - row number
     * @param includeNewline - whether to include new line character(s).
     */
    TextBufferInfo.prototype.rangeForRow = function (row, includeNewline) {
        this.logger.debugDetail("rangeForRow start:" + row, "EditorManager", "TextBufferInfo#rangeForRow");
        var lineStartOffset = 0;
        for (var i = 0; i < row - 1; i++) {
            lineStartOffset += this.lineLengths[i];
        }
        var lineLength = this.lineLengths[row];
        var startPoint = {
            row: row,
            column: 0
        };
        var endPoint = {
            row: row,
            column: lineLength
        };
        this.logger.debugDetail("rangeForRow return:" + row + ": [" + startPoint.row + ":" + startPoint.column + "]"
            + ",[" + endPoint.row + ":" + endPoint.column + "]", "EditorManager", "TextBufferInfo#rangeForRow");
        return {
            start: startPoint,
            end: endPoint
        };
    };
    /**
     * Gets text in range.
     * @param range
     */
    TextBufferInfo.prototype.getTextInRange = function (range) {
        var startOffset = this.characterIndexForPosition(range.start);
        var endOffset = this.characterIndexForPosition(range.end);
        var result = this.text.substring(startOffset, endOffset);
        this.logger.debugDetail("Text in range: [" + range.start.row + ":" + range.start.column + "]"
            + ",[" + range.end.row + ":" + range.end.column + "]:\n" + result, "EditorManager", "TextBufferInfo#getTextInRange");
        return result;
    };
    /**
     * Sets (replacing if needed) text in range
     * @param range - text range
     * @param text - text to set
     * @param normalizeLineEndings - whether to convert line endings to the ones standard for this document.
     */
    TextBufferInfo.prototype.setTextInRange = function (range, text, normalizeLineEndings) {
        this.logger.debug("Setting text in range: [" + range.start.row + ":" + range.start.column + "] ,"
            + "[" + range.end.row + ":" + range.end.column + "]\n" + text, "EditorManager", "TextBufferInfo#setTextInRange");
        var startOffset = range ? this.characterIndexForPosition(range.start) : 0;
        var endOffset = range ? this.characterIndexForPosition(range.end) : text.length;
        this.logger.debugDetail("Found range in absolute coords: [" + startOffset + ":" + endOffset + "]", "EditorManager", "TextBufferInfo#setTextInRange");
        var startText = startOffset > 0 ? this.text.substring(0, startOffset) : "";
        var endText = endOffset < this.text.length ? this.text.substring(endOffset) : "";
        this.logger.debugDetail("Start text is:\n" + startText, "EditorManager", "TextBufferInfo#setTextInRange");
        this.logger.debugDetail("End text is:\n" + endText, "EditorManager", "TextBufferInfo#setTextInRange");
        this.setText(startText + text + endText);
        this.logger.debugDetail("Final text is:\n" + this.text, "EditorManager", "TextBufferInfo#setTextInRange");
        return null;
    };
    /**
     * Returns buffer text.
     */
    TextBufferInfo.prototype.getText = function () {
        return this.text;
    };
    /**
     * Gets buffer end.
     */
    TextBufferInfo.prototype.getEndPosition = function () {
        return this.positionForCharacterIndex(this.text.length - 1);
    };
    TextBufferInfo.prototype.setText = function (text) {
        this.text = text != null ? text : "";
        this.initMapping();
        // reporting the change to the client, if possible.
        if (this.editorManager && this.editorManager.getDocumentChangeExecutor()) {
            this.editorManager.getDocumentChangeExecutor().changeDocument({
                uri: this.uri,
                text: this.text
            });
        }
        else {
            this.logger.error("Can not report document change to the client as there is no executor", "EditorManager", "TextBufferInfo#setText");
        }
    };
    TextBufferInfo.prototype.initMapping = function () {
        this.lineLengths = [];
        var ind = 0;
        var l = this.text.length;
        for (var i = 0; i < l; i++) {
            if (this.text.charAt(i) === "\r") {
                if (i < l - 1 && this.text.charAt(i + 1) === "\n") {
                    this.lineLengths.push(i - ind + 2);
                    ind = i + 2;
                    i++;
                }
                else {
                    this.lineLengths.push(i - ind + 1);
                    ind = i + 1;
                }
            }
            else if (this.text.charAt(i) === "\n") {
                this.lineLengths.push(i - ind + 1);
                ind = i + 1;
            }
        }
        this.lineLengths.push(l - ind);
    };
    return TextBufferInfo;
}());
var TextEditorInfo = (function () {
    function TextEditorInfo(uri, version, text, editorManager, logger) {
        this.uri = uri;
        this.version = version;
        this.buffer = new TextBufferInfo(uri, editorManager, logger);
        this.buffer.setText(text);
    }
    TextEditorInfo.prototype.setCursorPosition = function (position) {
        this.cursorPosition = position;
    };
    /**
     * Returns current cursor position
     */
    TextEditorInfo.prototype.getCursorBufferPosition = function () {
        if (this.buffer == null || this.cursorPosition == null) {
            return {
                row: 0,
                column: 0
            };
        }
        return this.getBuffer().positionForCharacterIndex(this.cursorPosition);
    };
    /**
     * Returns current cursor position, integer, starting from 0
     */
    TextEditorInfo.prototype.getCursorPosition = function () {
        if (!this.cursorPosition) {
            return 0;
        }
        return this.cursorPosition;
    };
    /**
     * Returns complete text of the document opened in the editor.
     */
    TextEditorInfo.prototype.getText = function () {
        return this.buffer.getText();
    };
    /**
     * Gets text buffer for the editor.
     */
    TextEditorInfo.prototype.getBuffer = function () {
        return this.buffer;
    };
    /**
     * Gets file path.
     */
    TextEditorInfo.prototype.getPath = function () {
        return this.uri;
    };
    /**
     * Sets editor text.
     * @param text
     */
    TextEditorInfo.prototype.setText = function (text) {
        this.buffer.setText(text);
    };
    /**
     * Returns document version, if any.
     */
    TextEditorInfo.prototype.getVersion = function () {
        return this.version;
    };
    return TextEditorInfo;
}());
var EditorManager = (function () {
    function EditorManager(connection) {
        this.connection = connection;
        this.uriToEditor = {};
        this.documentChangeListeners = [];
        this.documentChangeExecutor = null;
    }
    EditorManager.prototype.launch = function () {
        var _this = this;
        this.connection.onOpenDocument(function (document) { _this.onOpenDocument(document); });
        this.connection.onChangeDocument(function (document) { _this.documentWasChanged(document); });
        this.connection.onChangePosition(function (uri, position) {
            var editor = _this.getEditor(uri);
            if (!editor) {
                return;
            }
            editor.setCursorPosition(position);
        });
        this.connection.onCloseDocument(function (uri) { _this.onCloseDocument(uri); });
    };
    /**
     * Returns unique module name.
     */
    EditorManager.prototype.getModuleName = function () {
        return "EDITOR_MANAGER";
    };
    EditorManager.prototype.onChangeDocument = function (listener, unsubscribe) {
        if (unsubscribe === void 0) { unsubscribe = false; }
        if (unsubscribe) {
            var index = this.documentChangeListeners.indexOf(listener);
            if (index !== -1) {
                this.documentChangeListeners.splice(index, 1);
            }
        }
        else {
            this.documentChangeListeners.push(listener);
        }
    };
    EditorManager.prototype.getEditor = function (uri) {
        return this.uriToEditor[uri];
    };
    EditorManager.prototype.onOpenDocument = function (document) {
        this.connection.debug("Document is opened", "EditorManager", "onOpenDocument");
        this.uriToEditor[document.uri] =
            new TextEditorInfo(document.uri, document.version, document.text, this, this.connection);
        for (var _i = 0, _a = this.documentChangeListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(document);
        }
    };
    /**
     * Sets document change executor to use when editor buffer text modification
     * methods are being called.
     * @param executor
     */
    EditorManager.prototype.setDocumentChangeExecutor = function (executor) {
        this.documentChangeExecutor = executor;
    };
    EditorManager.prototype.getDocumentChangeExecutor = function () {
        return this.documentChangeExecutor;
    };
    EditorManager.prototype.documentWasChanged = function (document) {
        this.connection.debug("Document is changed", "EditorManager", "onChangeDocument");
        this.connection.debugDetail("Text is:\n " + document.text, "EditorManager", "onChangeDocument");
        var current = this.uriToEditor[document.uri];
        if (current) {
            var currentVersion = current.getVersion();
            if (currentVersion && document.version && currentVersion === document.version) {
                this.connection.debugDetail("Version of the reported change is equal to the previous one", "EditorManager", "onChangeDocument");
                return;
            }
            var currentText = current.getText();
            if (document.version < currentVersion && document.text === currentText) {
                this.connection.debugDetail("No changes detected", "EditorManager", "onChangeDocument");
                return;
            }
        }
        this.uriToEditor[document.uri] =
            new TextEditorInfo(document.uri, document.version, document.text, this, this.connection);
        for (var _i = 0, _a = this.documentChangeListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(document);
        }
    };
    EditorManager.prototype.onCloseDocument = function (uri) {
        delete this.uriToEditor[uri];
    };
    return EditorManager;
}());
//# sourceMappingURL=editorManager.js.map