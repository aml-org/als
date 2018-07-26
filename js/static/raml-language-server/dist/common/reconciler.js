"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var PromiseConstructor = require("promise-polyfill");
if (typeof Promise === "undefined" && typeof window !== "undefined") {
    window.Promise = PromiseConstructor;
}
var Reconciler = /** @class */ (function () {
    function Reconciler(logger, timeout) {
        this.logger = logger;
        this.timeout = timeout;
        this.waitingList = [];
        this.runningList = [];
    }
    Reconciler.prototype.schedule = function (runnable) {
        var _this = this;
        this.addToWaitingList(runnable);
        return new Promise(function (resolve, reject) {
            setTimeout(function () {
                _this.logger.debugDetail("Time came to execute " + runnable.toString(), "Reconciler", "schedule");
                if (runnable.isCanceled()) {
                    _this.logger.debugDetail("Runnable " + runnable.toString() + " is cancelled, doing nothing", "Reconciler", "schedule");
                    _this.removeFromWaitingList(runnable);
                    return;
                }
                var currentlyRunning = _this.findConflictingInRunningList(runnable);
                if (currentlyRunning) {
                    // TODO add an additional short timeout parameter to launch the reschedule
                    // at the finish of the currently running task for a short time after it.
                    _this.logger.debugDetail("Conflicting to " + runnable.toString()
                        + " is found in the running list: " + currentlyRunning.toString()
                        + " rescheduling current one.", "Reconciler", "schedule");
                    _this.schedule(runnable);
                    return;
                }
                _this.removeFromWaitingList(runnable);
                _this.addToRunningList(runnable);
                _this.logger.debugDetail("Executing " + runnable.toString(), "Reconciler", "schedule");
                _this.run(runnable).then(function (result) { resolve(result); }, function (error) { reject(error); });
            }, _this.timeout);
        });
    };
    Reconciler.prototype.run = function (runnable) {
        var _this = this;
        return runnable.run().then(function (result) {
            _this.removeFromRunningList(runnable);
            return result;
        }, function (error) {
            _this.removeFromRunningList(runnable);
            throw error;
        });
    };
    /**
     * Adds item to waiting list and removes anything currently in the list,
     * which conflicts with the new item.
     * @param runnable
     */
    Reconciler.prototype.addToWaitingList = function (runnable) {
        var _this = this;
        this.logger.debugDetail("Adding runnable " + runnable.toString() + " to waiting list", "Reconciler", "addToWaitingList");
        this.waitingList = this.waitingList.filter(function (current) {
            _this.logger.debugDetail("Comparing existing runnable " + current.toString() +
                " to the new " + runnable.toString(), "Reconciler", "addToWaitingList");
            var conflicts = runnable.conflicts(current);
            if (conflicts) {
                _this.logger.debugDetail("Runnables are conflicting, canceling existing one", "Reconciler", "addToWaitingList");
                current.cancel();
            }
            return !conflicts;
        });
        this.waitingList.push(runnable);
    };
    /**
     * Removes runnable from the list of running ones.
     * @param runnable
     */
    Reconciler.prototype.removeFromWaitingList = function (runnable) {
        this.logger.debugDetail("Removing " + runnable.toString()
            + " from waiting list", "Reconciler", "removeFromWaitingList");
        var index = this.waitingList.indexOf(runnable);
        if (index !== -1) {
            this.waitingList.splice(index, 1);
        }
    };
    /**
     * Adds runnable to the list of running ones.
     * @param runnable
     */
    Reconciler.prototype.addToRunningList = function (runnable) {
        this.logger.debugDetail("Adding " + runnable.toString()
            + " to running list", "Reconciler", "removeFromWaitingList");
        this.runningList.push(runnable);
    };
    /**
     * Removes runnable from the list of running ones.
     * @param runnable
     */
    Reconciler.prototype.removeFromRunningList = function (runnable) {
        this.logger.debugDetail("Removing " + runnable.toString()
            + " from running list", "Reconciler", "removeFromWaitingList");
        var index = this.runningList.indexOf(runnable);
        if (index !== -1) {
            this.runningList.splice(index, 1);
        }
    };
    /**
     * Finds the first conflicting runnable in the current list.
     * @param runnable
     * @returns {any}
     */
    Reconciler.prototype.findConflictingInRunningList = function (runnable) {
        for (var _i = 0, _a = this.runningList; _i < _a.length; _i++) {
            var current = _a[_i];
            if (runnable.conflicts(current)) {
                return current;
            }
        }
        return null;
    };
    return Reconciler;
}());
exports.Reconciler = Reconciler;
//# sourceMappingURL=reconciler.js.map