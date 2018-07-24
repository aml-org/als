export function findSelection(node, position, content): string {
    if(!node) {
        return null;
    }
    
    var names = ["name", "type", "is", "securedBy", "schema", "annotations"];

    for(var i = 0; i < names.length; i++) {
        var name = names[i];

        var selection = findSelectionValue(node, name, position, content);

        if(selection) {
            return selection;
        }
    }

    var parsedType = node.parsedType && node.parsedType();

    var customFacets = (parsedType.customFacets && parsedType.customFacets()) || [];

    for(var i = 0; i < customFacets.length; i++) {
        if(isValidSelection(content, customFacets[i].facetName(), position)) {
            return customFacets[i].facetName();
        }
    }

    return null;
}

export function reduce(fullContent: string, selection: string, range: {start: number, end: number}): {start: number, end: number} {
    if(!fullContent || !fullContent.trim()) {
        return null;
    }

    if(!selection || !selection.trim()) {
        return null;
    }

    var actualIndex = fullContent.indexOf(selection, range.start);

    if(actualIndex > range.end) {
        return null;
    }

    if(actualIndex < 0) {
        return null;
    }

    while(true) {
        var borders = fullContent.charAt(actualIndex - 1) + fullContent.charAt(actualIndex + selection.length);

        if(isLetter(borders.trim())) {
            actualIndex = fullContent.indexOf(selection, actualIndex + selection.length);

            if(actualIndex > range.end) {
                return null;
            }

            if(actualIndex < 0) {
                return null;
            }
        } else {
            break;
        }
    }

    return {
        start: actualIndex,
        end: actualIndex + selection.length
    }
}

function isLetter(value: string) {
    return /^[a-zA-Z0-9-]+$/.test(value);
}

function isValidSelection(fullContent: string, selection: string, position: number): boolean {
    var startFrom = position - selection.length;

    startFrom = startFrom < 0 ? 0 : startFrom;

    var selectionStart = fullContent.indexOf(selection, startFrom);

    if(selectionStart < 0) {
        return false;
    }

    if(position < selectionStart) {
        return false;
    }

    if(position > selectionStart + selection.length + 1) {
        return false
    }

    return true;
}

function findUnique(names: string[]) {
    var result = names[0];

    names.forEach(name => {
        if(name.length > result.length) {
            result = name;
        }
    })

    return result;
}

function findSelectionValue(node, name, position, content): string {
    var proposals = filterProposals(findSelectionValues(node, name, position)).map(proposal => proposal.trim());

    var valids = [];

    for(var i = 0; i < proposals.length; i++) {
        if(isValidSelection(content, proposals[i], position)) {
            valids.push(proposals[i]);
        }
    }

    if(valids.length === 0) {
        return null;
    }

    return findUnique(valids);
}

function filterProposals(selections: string[]) {
    var result = [];

    selections.forEach(selection => {
        if(!selection) {
            return;
        }

        selection  = removeSquares(selection);

        if(selection.indexOf('|') > 0) {
            var unions = filterProposals(selection.split('|'));

            result = result.concat(unions);

            return;
        }

        var indexOfSquare = selection.indexOf("[");

        if(indexOfSquare > 0) {
            result.push(selection.substr(0, indexOfSquare));
        } else {
            result.push(selection);
        }
    })

    return result;
}

function findSelectionValues(node, name: string, position): string[] {
    var result = [];

    node.attributes(name).forEach(attr => {
        var toAdd = null;

        if(attr && attr.lowLevel() && attr.lowLevel().start() <= position && attr.lowLevel().end() >= position) {
            toAdd = attr.value && attr.value();
        }

        if(toAdd) {
            result.push(toAdd);
        }
    });

    if(node.attrValue(name)) {
        result.push(node.attrValue(name));
    }

    return handleStructuredValues(result);
}

function handleStructuredValues(values: any[]) {
    var result = [];

    values.forEach(value => {
        if(value.valueName) {
            result.push(value.valueName());

            result = result.concat(handleStructuredValues(value.children()));

            return;
        }

        result.push(value);
    })

    return result;
}

function removeSquares(value: string): string {
    return value.split("(").join("").split(")").join("").trim();
}