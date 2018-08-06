"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var logger_1 = require("./logger");
exports.MessageSeverity = logger_1.MessageSeverity;
var StructureCategories;
(function (StructureCategories) {
    StructureCategories[StructureCategories["ResourcesCategory"] = "Resources"] = "ResourcesCategory";
    StructureCategories[StructureCategories["SchemasAndTypesCategory"] = "Schemas & Types"] = "SchemasAndTypesCategory";
    StructureCategories[StructureCategories["ResourceTypesAndTraitsCategory"] = "Resource Types & Traits"] = "ResourceTypesAndTraitsCategory";
    StructureCategories[StructureCategories["OtherCategory"] = "Other"] = "OtherCategory";
})(StructureCategories = exports.StructureCategories || (exports.StructureCategories = {}));
// TODO rename from currently used atom icons to something more meaningful/universal
var Icons;
(function (Icons) {
    Icons[Icons["ARROW_SMALL_LEFT"] = "ARROW_SMALL_LEFT"] = "ARROW_SMALL_LEFT";
    Icons[Icons["PRIMITIVE_SQUARE"] = "PRIMITIVE_SQUARE"] = "PRIMITIVE_SQUARE";
    Icons[Icons["PRIMITIVE_DOT"] = "PRIMITIVE_DOT"] = "PRIMITIVE_DOT";
    Icons[Icons["FILE_SUBMODULE"] = "FILE_SUBMODULE"] = "FILE_SUBMODULE";
    Icons[Icons["TAG"] = "TAG"] = "TAG";
    Icons[Icons["FILE_BINARY"] = "FILE_BINARY"] = "FILE_BINARY";
    Icons[Icons["BOOK"] = "BOOK"] = "BOOK";
})(Icons = exports.Icons || (exports.Icons = {}));
// TODO rename from currently used atom styles to something more meaningful/universal
var TextStyles;
(function (TextStyles) {
    TextStyles[TextStyles["NORMAL"] = "NORMAL"] = "NORMAL";
    TextStyles[TextStyles["HIGHLIGHT"] = "HIGHLIGHT"] = "HIGHLIGHT";
    TextStyles[TextStyles["WARNING"] = "WARNING"] = "WARNING";
    TextStyles[TextStyles["SUCCESS"] = "SUCCESS"] = "SUCCESS";
})(TextStyles = exports.TextStyles || (exports.TextStyles = {}));
//# sourceMappingURL=typeInterfaces.js.map