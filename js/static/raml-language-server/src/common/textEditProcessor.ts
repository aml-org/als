import {
    ITextEdit
} from "./typeInterfaces";

/**
 * Applies single text edit to the document contents.
 * @param oldContents
 * @param edit
 */
export function applyDocumentEdit(oldContents: string, edit: ITextEdit): string {

    if (edit.range.end === 0) {
        return edit.text + oldContents;
    }

    if (edit.range.start >= oldContents.length) {
        return oldContents + edit.text;
    }

    if (edit.range.start < 0 || edit.range.end > oldContents.length) {
        throw new Error("Range of [" + edit.range.start + ":" + edit.range.end
            + "] is not applicable to document of length " + oldContents.length);
    }

    if (edit.range.start >= edit.range.end) {
        throw new Error("Range of [" + edit.range.start + ":" + edit.range.end
            + "] should have end greater than start");
    }

    let beginning = "";
    if (edit.range.start > 0) {
        beginning = oldContents.substring(0, edit.range.start - 1);
    }

    let end = "";
    if (edit.range.end < oldContents.length) {
        end = oldContents.substring(edit.range.end);
    }

    return beginning + edit.text + end;
}

/**
 * Apply a number of text edits to the document contents.
 * @param oldContents
 * @param edits
 * @returns {string}
 */
export function applyDocumentEdits(oldContents: string, edits: ITextEdit[]): string {
    if (edits.length > 1) {
        // TODO implement this
        throw new Error("Unsupported application of more than 1 text editor at once to a single file");
    }

    const newContents = applyDocumentEdit(oldContents, edits[0]);
    return newContents;
}
