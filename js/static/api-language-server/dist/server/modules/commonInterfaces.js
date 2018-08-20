"use strict";
// This file provides common interfaces for server modules.
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * instanceof for IDisposableModule.
 * @param module
 */
function isDisposableModule(module) {
    return module.dispose && typeof (module.dispose) === "function";
}
exports.isDisposableModule = isDisposableModule;
//# sourceMappingURL=commonInterfaces.js.map