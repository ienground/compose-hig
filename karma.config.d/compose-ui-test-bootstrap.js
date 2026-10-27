const composeUiFs = require("fs");
const composeUiPath = require("path");

const composeUiSkikoBootstrap = composeUiPath.join(
  config.basePath,
  "kotlin",
  "compose-ui-test-bootstrap.mjs",
);

if (composeUiFs.existsSync(composeUiSkikoBootstrap)) {
  config.customContextFile = composeUiPath.join(
    config.basePath,
    "kotlin",
    "compose-ui-test-context.html",
  );

  config.webpack = config.webpack || {};
  config.webpack.externals = Object.assign({}, config.webpack.externals, {
    "./skiko.mjs": 'window["./skiko.mjs"]',
  });

  config.files.unshift(
    {
      pattern: composeUiPath.join(config.basePath, "kotlin", "*.mjs"),
      included: false,
      watched: false,
    },
    {
      pattern: composeUiPath.join(config.basePath, "kotlin", "*.wasm"),
      included: false,
      watched: false,
    },
  );
  config.files.unshift({ pattern: composeUiSkikoBootstrap, type: "module" });
}

config.client = config.client || {};
config.client.mocha = Object.assign({}, config.client.mocha, {
  timeout: 30000,
});
