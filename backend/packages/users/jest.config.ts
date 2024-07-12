import { JestConfigWithTsJest } from "ts-jest"

const jestConfig: JestConfigWithTsJest = {
  testEnvironment: "node",
  transform: {
    "^.+.tsx?$": ["ts-jest", {}],
  },
  moduleNameMapper: {
    "@logisty/users/(.*)": "<rootDir>/src/$1",
  },
  testPathIgnorePatterns: ["/node_modules/", "/dist/"],
}

export default jestConfig