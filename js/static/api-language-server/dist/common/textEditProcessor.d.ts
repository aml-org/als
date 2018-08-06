import { ITextEdit } from "./typeInterfaces";
/**
 * Applies single text edit to the document contents.
 * @param oldContents
 * @param edit
 */
export declare function applyDocumentEdit(oldContents: string, edit: ITextEdit): string;
/**
 * Apply a number of text edits to the document contents.
 * @param oldContents
 * @param edits
 * @returns {string}
 */
export declare function applyDocumentEdits(oldContents: string, edits: ITextEdit[]): string;
