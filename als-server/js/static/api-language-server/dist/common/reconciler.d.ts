import { ILogger } from "./logger";
export interface Runnable<ResultType> {
    /**
     * Performs the actual business logics.
     * Should resolve the promise when finished.
     */
    run(): Promise<ResultType>;
    /**
     * Whether two runnable conflict with each other.
     * Must work fast as its called often.
     * @param other
     */
    conflicts(other: Runnable<any>): boolean;
    /**
     * Cancels the runnable. run() method should do nothing if launched later,
     * if cancel is called during the run() method execution, run() should stop as soon as it can.
     */
    cancel(): void;
    /**
     * Whether cancel() method was called at least once.
     */
    isCanceled(): boolean;
}
export declare class Reconciler {
    private logger;
    private timeout;
    private waitingList;
    private runningList;
    constructor(logger: ILogger, timeout: number);
    schedule<ResultType>(runnable: Runnable<ResultType>): Promise<ResultType>;
    private run<ResultType>(runnable);
    /**
     * Adds item to waiting list and removes anything currently in the list,
     * which conflicts with the new item.
     * @param runnable
     */
    private addToWaitingList<ResultType>(runnable);
    /**
     * Removes runnable from the list of running ones.
     * @param runnable
     */
    private removeFromWaitingList<ResultType>(runnable);
    /**
     * Adds runnable to the list of running ones.
     * @param runnable
     */
    private addToRunningList<ResultType>(runnable);
    /**
     * Removes runnable from the list of running ones.
     * @param runnable
     */
    private removeFromRunningList<ResultType>(runnable);
    /**
     * Finds the first conflicting runnable in the current list.
     * @param runnable
     * @returns {any}
     */
    private findConflictingInRunningList<ResultType>(runnable);
}
