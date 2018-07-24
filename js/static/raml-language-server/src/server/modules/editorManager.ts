// This module handles contents of editors opened in IDE and provides this information to other modules

import {
    IServerConnection
} from "../core/connections";

import {
    IChangedDocument,
    IDocumentChangeExecutor,
    ILogger,
    IOpenedDocument
} from "../../common/typeInterfaces";

import {
    IAbstractTextEditor,
    IAbstractTextEditorWithCursor,
    IEditorTextBuffer,
    IServerModule,
    IPoint,
    IRange
} from "./commonInterfaces";

export interface IEditorManagerModule extends IServerModule {
    launch(): void;
    getEditor(uri: string): IAbstractTextEditorWithCursor;
    onChangeDocument(listener: (document: IChangedDocument) => void, unsubscribe?: boolean);

    /**
     * Sets document change executor to use when editor buffer text modification
     * methods are being called.
     * @param executor
     */
    setDocumentChangeExecutor(executor: IDocumentChangeExecutor): void;
}

export function createManager(connection: IServerConnection): IEditorManagerModule {
    return new EditorManager(connection);
}

class TextBufferInfo implements IEditorTextBuffer {
    // TODO add border checks

    private text = "";
    private lineLengths: number[];

    constructor(private uri: string, private editorManager: EditorManager,
                private logger: ILogger) {
    }

    /**
     * Gets offset from the beginning of the document by the position
     * @param position
     */
    public characterIndexForPosition(position: IPoint): number {
        let lineStartOffset = 0;

        for (let i = 0; i <= position.row - 1; i++) {
            lineStartOffset += this.lineLengths[i];
        }

        const result = lineStartOffset + position.column;

        this.logger.debugDetail(
            "characterIndexForPosition:" +
            ": [" + position.row + ":" + position.column + "] = " + result,
            "EditorManager", "TextBufferInfo#characterIndexForPosition");

        return result;
    }

    /**
     * Gets position by the offset from the beginning of the document.
     * @param offset
     */
    public positionForCharacterIndex(offset: number): IPoint {
        let pos = offset;

        for (let i = 0 ; i < this.lineLengths.length; i++) {
            const lineLength = this.lineLengths[i];
            if (pos < lineLength) {

                this.logger.debugDetail(
                    "positionForCharacterIndex:" + offset +
                        ": [" + i + ":" + pos + "]",
                    "EditorManager", "TextBufferInfo#positionForCharacterIndex");

                return {
                    row: i,
                    column: pos
                };
            }
            pos -= lineLength;
        }
        if (pos === 0) {

            const row = this.lineLengths.length - 1;
            const column = this.lineLengths[this.lineLengths.length - 1];

            this.logger.debugDetail(
                "positionForCharacterIndex:" + offset +
                ": [" + row + ":" + column + "]",
                "EditorManager", "TextBufferInfo#positionForCharacterIndex");

            return {
                row,
                column
            };
        }

        const errorMessage = `Character position exceeds text length: ${offset} > + ${this.text.length}`;

        this.logger.error(errorMessage,
            "EditorManager", "TextBufferInfo#positionForCharacterIndex");

        throw new Error(errorMessage);
    }

    /**
     * Gets a range for the row number.
     * @param row - row number
     * @param includeNewline - whether to include new line character(s).
     */
    public rangeForRow(row: number, includeNewline?: boolean): IRange {
        this.logger.debugDetail(
            "rangeForRow start:" + row,
            "EditorManager", "TextBufferInfo#rangeForRow");

        let lineStartOffset = 0;

        for (let i = 0; i < row - 1; i++) {
            lineStartOffset += this.lineLengths[i];
        }

        const lineLength = this.lineLengths[row];

        const startPoint = {
            row,
            column : 0
        };

        const endPoint = {
            row,
            column : lineLength
        };

        this.logger.debugDetail(
            "rangeForRow return:" + row + ": [" + startPoint.row + ":" + startPoint.column + "]"
            + ",[" + endPoint.row + ":" + endPoint.column + "]",
            "EditorManager", "TextBufferInfo#rangeForRow");

        return {
            start: startPoint,
            end: endPoint
        };
    }

    /**
     * Gets text in range.
     * @param range
     */
    public getTextInRange(range: IRange): string {
        const startOffset = this.characterIndexForPosition(range.start);
        const endOffset = this.characterIndexForPosition(range.end);

        const result = this.text.substring(startOffset, endOffset);

        this.logger.debugDetail(
            "Text in range: [" + range.start.row + ":" + range.start.column + "]"
                + ",[" + range.end.row + ":" + range.end.column + "]:\n" + result,
            "EditorManager", "TextBufferInfo#getTextInRange");

        return result;
    }

    /**
     * Sets (replacing if needed) text in range
     * @param range - text range
     * @param text - text to set
     * @param normalizeLineEndings - whether to convert line endings to the ones standard for this document.
     */
    public setTextInRange(range: IRange, text: string, normalizeLineEndings?: boolean): IRange {
        this.logger.debug(
            "Setting text in range: [" + range.start.row + ":" + range.start.column + "] ,"
                + "[" + range.end.row + ":" + range.end.column + "]\n" + text,
            "EditorManager", "TextBufferInfo#setTextInRange");

        const startOffset = range ? this.characterIndexForPosition(range.start) : 0;
        const endOffset = range ? this.characterIndexForPosition(range.end) : text.length;

        this.logger.debugDetail(
            "Found range in absolute coords: [" + startOffset + ":" + endOffset + "]",
            "EditorManager", "TextBufferInfo#setTextInRange");

        const startText = startOffset > 0 ? this.text.substring(0, startOffset) : "";
        const endText = endOffset < this.text.length ? this.text.substring(endOffset) : "";

        this.logger.debugDetail(
            "Start text is:\n" + startText,
            "EditorManager", "TextBufferInfo#setTextInRange");

        this.logger.debugDetail(
            "End text is:\n" + endText,
            "EditorManager", "TextBufferInfo#setTextInRange");

        this.setText(startText + text + endText);

        this.logger.debugDetail(
            "Final text is:\n" + this.text,
            "EditorManager", "TextBufferInfo#setTextInRange");

        return null;
    }

    /**
     * Returns buffer text.
     */
    public getText(): string {
        return this.text;
    }

    /**
     * Gets buffer end.
     */
    public getEndPosition(): IPoint {
        return this.positionForCharacterIndex(this.text.length - 1);
    }

    public setText(text: string): void {
        this.text = text != null ? text : "";
        this.initMapping();

        // reporting the change to the client, if possible.
        if (this.editorManager && this.editorManager.getDocumentChangeExecutor()) {
            this.editorManager.getDocumentChangeExecutor().changeDocument({

                uri: this.uri,

                text: this.text
            });
        } else {
            this.logger.error(
                "Can not report document change to the client as there is no executor",
                "EditorManager", "TextBufferInfo#setText");
        }
    }

    public initMapping() {

        this.lineLengths = [];

        let ind = 0;
        const l = this.text.length;
        for (let i = 0 ; i < l; i++) {

            if (this.text.charAt(i) === "\r") {
                if (i < l - 1 && this.text.charAt(i + 1) === "\n") {
                    this.lineLengths.push(i - ind + 2);
                    ind = i + 2;
                    i++;
                } else {
                    this.lineLengths.push(i - ind + 1);
                    ind = i + 1;
                }
            } else if (this.text.charAt(i) === "\n") {
                this.lineLengths.push(i - ind + 1);
                ind = i + 1;
            }
        }
        this.lineLengths.push(l - ind);
    }
}

class TextEditorInfo implements IAbstractTextEditorWithCursor {

    private buffer: TextBufferInfo;
    private cursorPosition: number;

    constructor(private uri: string, private version: number, text: string,
                editorManager: EditorManager, logger: ILogger) {

        this.buffer = new TextBufferInfo(uri, editorManager, logger);
        this.buffer.setText(text);
    }

    public setCursorPosition(position) {
        this.cursorPosition = position;
    }

    /**
     * Returns current cursor position
     */
    public getCursorBufferPosition(): IPoint {
        if (this.buffer == null || this.cursorPosition == null) {
            return {
                row: 0,
                column: 0
            };
        }

        return this.getBuffer().positionForCharacterIndex(this.cursorPosition);
    }

    /**
     * Returns current cursor position, integer, starting from 0
     */
    public getCursorPosition(): number {
        if (!this.cursorPosition) {
            return 0;
        }

        return this.cursorPosition;
    }

    /**
     * Returns complete text of the document opened in the editor.
     */
    public getText(): string {
        return this.buffer.getText();
    }

    /**
     * Gets text buffer for the editor.
     */
    public getBuffer(): IEditorTextBuffer {
        return this.buffer;
    }

    /**
     * Gets file path.
     */
    public getPath() {
        return this.uri;
    }

    /**
     * Sets editor text.
     * @param text
     */
    public setText(text: string) {
        this.buffer.setText(text);
    }

    /**
     * Returns document version, if any.
     */
    public getVersion(): number {
        return this.version;
    }
}

class EditorManager implements IEditorManagerModule {

    private uriToEditor: {[uri: string]: TextEditorInfo} = {};
    private documentChangeListeners: {(document: IChangedDocument): void}[] = [];
    private documentChangeExecutor: IDocumentChangeExecutor = null;

    constructor(private connection: IServerConnection) {
    }

    public launch(): void {
        this.connection.onOpenDocument(
            (document: IOpenedDocument) => {this.onOpenDocument(document); }
        );

        this.connection.onChangeDocument(
            (document: IChangedDocument) => {this.documentWasChanged(document); }
        );

        this.connection.onChangePosition((uri, position) => {
            const editor = this.getEditor(uri);
            if (!editor) {
                return;
            }

            (editor as TextEditorInfo).setCursorPosition(position);
        });

        this.connection.onCloseDocument(
            (uri: string) => {this.onCloseDocument(uri); }
        );
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "EDITOR_MANAGER";
    }

    public onChangeDocument(listener: (document: IChangedDocument) => void, unsubscribe = false) {
        if (unsubscribe) {
            const index = this.documentChangeListeners.indexOf(listener);
            if (index !== -1) {
                this.documentChangeListeners.splice(index, 1);
            }
        } else {
            this.documentChangeListeners.push(listener);
        }
    }

    public getEditor(uri: string): IAbstractTextEditorWithCursor {
        return this.uriToEditor[uri];
    }

    public onOpenDocument(document: IOpenedDocument): void {
        this.connection.debug("Document is opened",
            "EditorManager", "onOpenDocument");

        this.uriToEditor[document.uri] =
            new TextEditorInfo(document.uri, document.version, document.text,
                this, this.connection);

        for (const listener of this.documentChangeListeners) {
            listener(document);
        }
    }

    /**
     * Sets document change executor to use when editor buffer text modification
     * methods are being called.
     * @param executor
     */
    public setDocumentChangeExecutor(executor: IDocumentChangeExecutor): void {
        this.documentChangeExecutor = executor;
    }

    public getDocumentChangeExecutor(): IDocumentChangeExecutor {
        return this.documentChangeExecutor;
    }

    public documentWasChanged(document: IChangedDocument): void {

        this.connection.debug("Document is changed",
            "EditorManager", "onChangeDocument");
        this.connection.debugDetail(`Text is:\n ` + document.text, "EditorManager", "onChangeDocument");

        const current = this.uriToEditor[document.uri];
        if (current) {

            const currentVersion = current.getVersion();
            if (currentVersion && document.version && currentVersion === document.version) {
                this.connection.debugDetail("Version of the reported change is equal to the previous one",
                    "EditorManager", "onChangeDocument");
                return;
            }

            const currentText = current.getText();
            if (document.version < currentVersion && document.text === currentText) {
                this.connection.debugDetail("No changes detected", "EditorManager", "onChangeDocument");
                return;
            }
        }

        this.uriToEditor[document.uri] =
            new TextEditorInfo(document.uri, document.version, document.text,
                this, this.connection);
        for (const listener of this.documentChangeListeners) {
            listener(document);
        }
    }

    public onCloseDocument(uri: string): void {
        delete this.uriToEditor[uri];
    }

}
