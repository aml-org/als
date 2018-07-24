/**
 * Configuration of actions module.
 */
export interface IActionsConfiguration {

    /**
     * Whether to report actions that require external UI in action-related searches.
     */
    enableUIActions?: boolean;
}

export interface IModulesConfiguration {

    /**
     * If true, will make Details module enabled, false otherwise, if absent, make no changes.
     */
    enableDetailsModule?: boolean;

    /**
     * If true, will make Custom Actions module enabled, false otherwise, if absent, make no changes.
     */
    enableCustomActionsModule?: boolean;

    /**
     * If true, will make AST Manager module enabled, false otherwise, if absent, make no changes.
     */
    enableASTManagerModule?: boolean;

    /**
     * If true, will make Completion Manager module enabled, false otherwise, if absent, make no changes.
     */
    enableCompletionManagerModule?: boolean;

    /**
     * If true, will make Editor Manager module enabled, false otherwise, if absent, make no changes.
     */
    enableEditorManagerModule?: boolean;

    /**
     * If true, will make Fixed Actions module enabled, false otherwise, if absent, make no changes.
     */
    enableFixedActionsModule?: boolean;

    /**
     * If true, will make Structure Manager module enabled, false otherwise, if absent, make no changes.
     */
    enableStructureManagerModule?: boolean;

    /**
     * If true, will make Validation Manager module enabled, false otherwise, if absent, make no changes.
     */
    enableValidationManagerModule?: boolean;

    /**
     * If true, will make all modules enabled, false otherwise, if absent, make no changes.
     */
    allModules?: boolean;
}

/**
 * Server configuration.
 */
export interface IServerConfiguration {

    /**
     * Sets custom actions module configuration.
     */
    actionsConfiguration?: IActionsConfiguration;

    /**
     * Sets server modules configuration.
     */
    modulesConfiguration?: IModulesConfiguration;
}
