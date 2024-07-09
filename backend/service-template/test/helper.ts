const helper = require("fastify-cli/helper.js");
import * as path from "path";
import * as test from "node:test";

export type TestContext = {
  after: typeof test.after;
};

const AppPath = path.join(__dirname, "..", "src", "app.ts");

async function config() {
  return {};
}

async function build(t: TestContext) {
  const argv = [AppPath];

  const app = await helper.build(argv, await config());

  t.after(() => void app.close());

  return app;
}

export { config, build };
