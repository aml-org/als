declare var require;

import util = require("./util");

var caseNames: string[] = [
    'FULL_LIFECYCLE',
    'LOADING',
    'LOADING_THEN_STRUCTURE'
]

caseNames.forEach(caseName => {
    util.addCase("Jira-v2/api.raml", caseName);
    util.addCase("LinkedIn-v1/api.raml", caseName);
    util.addCase("MarketingCloudAPIServices-v1/api.raml", caseName);
    util.addCase("Z_System_API-v1.0.0.1/Z_System_API-v1.0.0/api.raml", caseName);
    util.addCase("Zendesk-v2/api.raml", caseName);
    util.addCase("huge_api/api.raml", caseName, "initial version");
    util.addCase("huge_api_0/api.raml", caseName, "refactored version(libraries instead of thouthands of includes)");
    util.addCase("huge_api_1/api.raml", caseName, "without resources");
    util.addCase("huge_api_2/api.raml", caseName, "without resources, without primitives");
    util.addCase("huge_api_3/api.raml", caseName, "without resources, without primitives, simplified elements");
    util.addCase("huge_api_4/api.raml", caseName, "without resources, without primitives, simplified elements, removed params in traits and resource types");
    util.addCase("huge_api_5/api.raml", caseName, "without resources, without primitives, simplified elements, removed params in traits and resource types, yaml removed");
    util.addCase("huge_api_6/api.raml", caseName, "without resources, without primitives, simplified elements, removed params in traits and resource types, yaml removed, dataTypes removed");
    util.addCase("huge_api_7/api.raml", caseName, "without resources, without primitives, simplified elements, dataTypes only");
});

util.runCases();