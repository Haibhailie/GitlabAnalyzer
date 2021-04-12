import { IFileData, TMemberData, TMergeData } from '../../types'
import jsonFetcher from '../../utils/jsonFetcher'
import { IGeneralTypeScoring, IUserConfig } from '../UserConfigContext'
import {
  IGNORE_COMMIT,
  IGNORE_COMMIT_FILE,
  IGNORE_MR,
  IGNORE_MR_FILE,
  UPDATE_START_TIME,
  UPDATE_END_TIME,
  UPDATE_GENERAL_WEIGHT,
  UPDATE_EXTENSION,
  TProjectReducer,
  IProjectState,
  ILoc,
  GET_PROJECT,
  TFiles,
  TCommits,
  TLocCategory,
  ISumOfCommitsScore,
  TMergeRequests,
  TMembers,
} from './types'
import objectEquals from 'fast-deep-equal'

const addOrSub = (isCurrentlyIgnored: boolean) => (isCurrentlyIgnored ? 1 : -1)

const calcScore = (scores: ILoc) =>
  Object.values(scores).reduce((accum, score) => accum + score, 0)

const weightReverseMap: Record<string, TLocCategory> = {
  'New line of code': 'additions',
  'Deleting a line': 'deletions',
  'Comment/blank': 'comments',
  'Spacing change': 'whitespaces',
  'Syntax only': 'syntaxes',
}

const calcScores = (loc: ILoc, weights: IGeneralTypeScoring[]): ILoc => {
  const weightMap: Record<string, number> = {}

  weights.forEach(({ type, value }) => {
    if (type in weightReverseMap) weightMap[weightReverseMap[type]] = value
  })

  return {
    additions: loc.additions * (weightMap.additions ?? 1),
    deletions: loc.deletions * (weightMap.deletions ?? 0.2),
    comments: loc.comments * (weightMap.comments ?? 0),
    syntaxes: loc.syntaxes * (weightMap.syntaxes ?? 0.2),
    whitespaces: loc.whitespaces * (weightMap.whitespaces ?? 0),
  }
}

// eslint-disable-next-line
const fakeMrs = `[{"mergeRequestId":14,"title":"Add src folder","author":"Grace Luo","userId":14,"description":"","time":1615635862541,"webUrl":"http://gitlab.example.com/djso/test-project/-/merge_requests/14","commits":[{"id":"2e374e308773a6cd59a0be0b04cb9bb71855bc5b","title":"Add a src folder","message":"Add a src folder\\n","author":"Grace Luo","authorEmail":"grace_luo@sfu.ca","time":1615635681000,"webUrl":"http://gitlab.example.com/djso/test-project/-/commit/2e374e308773a6cd59a0be0b04cb9bb71855bc5b","numAdditions":0,"numDeletions":0,"total":0,"diffs":"diff --git a/.DS_Store b/dev/null\\n--- a/.DS_Store\\n+++ b/dev/null\\nBinary files a/.DS_Store and /dev/null differ\\n","isIgnored":false,"files":[{"name":"dev/null","extension":"Unknown","isIgnored":false,"fileScore":{"totalScore":0.0,"scoreAdditions":0,"scoreDeletions":0,"scoreBlankAdditions":0,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":0,"numDeletions":0,"numBlankAdditions":0,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/.DS_Store b/dev/null","lineType":"HEADER"},{"diffLine":"--- a/.DS_Store","lineType":"HEADER"},{"diffLine":"+++ b/dev/null","lineType":"HEADER"},{"diffLine":"Binary files a/.DS_Store and /dev/null differ","lineType":"UNCHANGED"}]}],"score":0.0},{"id":"5c6126b3b18ebc430966f9f1299a0123638fab54","title":"Add a src folder","message":"Add a src folder\\n","author":"Grace Luo","authorEmail":"grace_luo@sfu.ca","time":1615635661000,"webUrl":"http://gitlab.example.com/djso/test-project/-/commit/5c6126b3b18ebc430966f9f1299a0123638fab54","numAdditions":222,"numDeletions":0,"total":222,"diffs":"diff --git a/dev/null b/.DS_Store\\n--- a/dev/null\\n+++ b/.DS_Store\\nBinary files /dev/null and b/.DS_Store differ\\ndiff --git a/dev/null b/src/App.css\\n--- a/dev/null\\n+++ b/src/App.css\\n@@ -0,0 +1,38 @@\\n+.App {\\n+  text-align: center;\\n+}\\n+\\n+.App-logo {\\n+  height: 40vmin;\\n+  pointer-events: none;\\n+}\\n+\\n+@media (prefers-reduced-motion: no-preference) {\\n+  .App-logo {\\n+    animation: App-logo-spin infinite 20s linear;\\n+  }\\n+}\\n+\\n+.App-header {\\n+  background-color: #282c34;\\n+  min-height: 100vh;\\n+  display: flex;\\n+  flex-direction: column;\\n+  align-items: center;\\n+  justify-content: center;\\n+  font-size: calc(10px + 2vmin);\\n+  color: white;\\n+}\\n+\\n+.App-link {\\n+  color: #61dafb;\\n+}\\n+\\n+@keyframes App-logo-spin {\\n+  from {\\n+    transform: rotate(0deg);\\n+  }\\n+  to {\\n+    transform: rotate(360deg);\\n+  }\\n+}\\ndiff --git a/dev/null b/src/App.test.tsx\\n--- a/dev/null\\n+++ b/src/App.test.tsx\\n@@ -0,0 +1,9 @@\\n+import React from \\u0027react\\u0027;\\n+import { render, screen } from \\u0027@testing-library/react\\u0027;\\n+import App from \\u0027./App\\u0027;\\n+\\n+test(\\u0027renders learn react link\\u0027, () \\u003d\\u003e {\\n+  render(\\u003cApp /\\u003e);\\n+  const linkElement \\u003d screen.getByText(/learn react/i);\\n+  expect(linkElement).toBeInTheDocument();\\n+});\\ndiff --git a/dev/null b/src/App.tsx\\n--- a/dev/null\\n+++ b/src/App.tsx\\n@@ -0,0 +1,28 @@\\n+import React from \\"react\\";\\n+import { Counter } from \\"./Counter\\";\\n+import { TextField } from \\"./TextField\\";\\n+\\n+const App: React.FC \\u003d () \\u003d\\u003e {\\n+  return (\\n+    \\u003cdiv\\u003e\\n+      {/* \\u003cTextField\\n+        text\\u003d\\"hii\\"\\n+        person\\u003d{{ firstName: \\"\\", lastName: \\"\\" }}\\n+        length\\u003d{50}\\n+        handleChange\\u003d{(e) \\u003d\\u003e {\\n+          e.preventDefault();\\n+        }}\\n+      /\\u003e */}\\n+      \\u003cCounter\\u003e\\n+        {(count, setCount) \\u003d\\u003e (\\n+          \\u003cdiv\\u003e\\n+            {count}\\n+            \\u003cbutton onClick\\u003d{() \\u003d\\u003e setCount(count + 1)}\\u003e+\\u003c/button\\u003e\\n+          \\u003c/div\\u003e\\n+        )}\\n+      \\u003c/Counter\\u003e\\n+    \\u003c/div\\u003e\\n+  );\\n+};\\n+\\n+export default App;\\ndiff --git a/dev/null b/src/Counter.tsx\\n--- a/dev/null\\n+++ b/src/Counter.tsx\\n@@ -0,0 +1,14 @@\\n+import React, { useState } from \\"react\\";\\n+\\n+interface Props {\\n+  children: (\\n+    count: number,\\n+    setCount: React.Dispatch\\u003cReact.SetStateAction\\u003cnumber\\u003e\\u003e\\n+  ) \\u003d\\u003e JSX.Element | null;\\n+}\\n+\\n+export const Counter: React.FC\\u003cProps\\u003e \\u003d ({ children }) \\u003d\\u003e {\\n+  const [count, setCount] \\u003d useState(0);\\n+\\n+  return \\u003cdiv\\u003e{children(count, setCount)}\\u003c/div\\u003e;\\n+};\\ndiff --git a/dev/null b/src/ReducerExample.tsx\\n--- a/dev/null\\n+++ b/src/ReducerExample.tsx\\n@@ -0,0 +1,50 @@\\n+import React, { useReducer } from \\"react\\";\\n+\\n+type Actions \\u003d\\n+  | { type: \\"add\\"; text: string }\\n+  | {\\n+      type: \\"remove\\";\\n+      idx: number;\\n+    };\\n+\\n+interface Todo {\\n+  text: string;\\n+  complete: boolean;\\n+}\\n+\\n+type State \\u003d Todo[];\\n+\\n+const TodoReducer \\u003d (state: State, action: Actions) \\u003d\\u003e {\\n+  switch (action.type) {\\n+    case \\"add\\":\\n+      return [...state, { text: action.text, complete: false }];\\n+    case \\"remove\\":\\n+      return state.filter((_, i) \\u003d\\u003e action.idx !\\u003d\\u003d i);\\n+    default:\\n+      return state;\\n+  }\\n+};\\n+\\n+export const ReducerExample: React.FC \\u003d () \\u003d\\u003e {\\n+  const [todos, dispatch] \\u003d useReducer(TodoReducer, []);\\n+\\n+  return (\\n+    \\u003cdiv\\u003e\\n+      {JSON.stringify(todos)}\\n+      \\u003cbutton\\n+        onClick\\u003d{() \\u003d\\u003e {\\n+          dispatch({ type: \\"add\\", text: \\"...\\" });\\n+        }}\\n+      \\u003e\\n+        +\\n+      \\u003c/button\\u003e\\n+      \\u003cbutton\\n+        onClick\\u003d{() \\u003d\\u003e {\\n+          dispatch({ type: \\"remove\\", idx: 5 });\\n+        }}\\n+      \\u003e\\n+        -\\n+      \\u003c/button\\u003e\\n+    \\u003c/div\\u003e\\n+  );\\n+};\\ndiff --git a/dev/null b/src/TextField.tsx\\n--- a/dev/null\\n+++ b/src/TextField.tsx\\n@@ -0,0 +1,31 @@\\n+import React, { useState, useRef } from \\"react\\";\\n+\\n+interface Person {\\n+  firstName: string;\\n+  lastName: string;\\n+}\\n+\\n+interface Props {\\n+  text: string;\\n+  ok?: boolean;\\n+  length: number;\\n+  handleChange: (event: React.ChangeEvent\\u003cHTMLInputElement\\u003e) \\u003d\\u003e void;\\n+  person: Person;\\n+}\\n+\\n+interface TextNode {\\n+  text: string;\\n+}\\n+\\n+export const TextField: React.FC\\u003cProps\\u003e \\u003d ({ handleChange }) \\u003d\\u003e {\\n+  //   const [count, setCount] \\u003d useState\\u003cnumber | null\\u003e(5);\\n+  const [message, setMessage] \\u003d useState\\u003cTextNode\\u003e({ text: \\"hello\\" });\\n+\\n+  const inputRef \\u003d useRef\\u003cHTMLInputElement\\u003e(null);\\n+\\n+  return (\\n+    \\u003cdiv\\u003e\\n+      \\u003cinput ref\\u003d{inputRef} onChange\\u003d{handleChange} /\\u003e\\n+    \\u003c/div\\u003e\\n+  );\\n+};\\ndiff --git a/dev/null b/src/index.css\\n--- a/dev/null\\n+++ b/src/index.css\\n@@ -0,0 +1,13 @@\\n+body {\\n+  margin: 0;\\n+  font-family: -apple-system, BlinkMacSystemFont, \\u0027Segoe UI\\u0027, \\u0027Roboto\\u0027, \\u0027Oxygen\\u0027,\\n+    \\u0027Ubuntu\\u0027, \\u0027Cantarell\\u0027, \\u0027Fira Sans\\u0027, \\u0027Droid Sans\\u0027, \\u0027Helvetica Neue\\u0027,\\n+    sans-serif;\\n+  -webkit-font-smoothing: antialiased;\\n+  -moz-osx-font-smoothing: grayscale;\\n+}\\n+\\n+code {\\n+  font-family: source-code-pro, Menlo, Monaco, Consolas, \\u0027Courier New\\u0027,\\n+    monospace;\\n+}\\ndiff --git a/dev/null b/src/index.tsx\\n--- a/dev/null\\n+++ b/src/index.tsx\\n@@ -0,0 +1,17 @@\\n+import React from \\u0027react\\u0027;\\n+import ReactDOM from \\u0027react-dom\\u0027;\\n+import \\u0027./index.css\\u0027;\\n+import App from \\u0027./App\\u0027;\\n+import reportWebVitals from \\u0027./reportWebVitals\\u0027;\\n+\\n+ReactDOM.render(\\n+  \\u003cReact.StrictMode\\u003e\\n+    \\u003cApp /\\u003e\\n+  \\u003c/React.StrictMode\\u003e,\\n+  document.getElementById(\\u0027root\\u0027)\\n+);\\n+\\n+// If you want to start measuring performance in your app, pass a function\\n+// to log results (for example: reportWebVitals(console.log))\\n+// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals\\n+reportWebVitals();\\ndiff --git a/dev/null b/src/logo.svg\\n--- a/dev/null\\n+++ b/src/logo.svg\\n@@ -0,0 +1 @@\\n+\\u003csvg xmlns\\u003d\\"http://www.w3.org/2000/svg\\" viewBox\\u003d\\"0 0 841.9 595.3\\"\\u003e\\u003cg fill\\u003d\\"#61DAFB\\"\\u003e\\u003cpath d\\u003d\\"M666.3 296.5c0-32.5-40.7-63.3-103.1-82.4 14.4-63.6 8-114.2-20.2-130.4-6.5-3.8-14.1-5.6-22.4-5.6v22.3c4.6 0 8.3.9 11.4 2.6 13.6 7.8 19.5 37.5 14.9 75.7-1.1 9.4-2.9 19.3-5.1 29.4-19.6-4.8-41-8.5-63.5-10.9-13.5-18.5-27.5-35.3-41.6-50 32.6-30.3 63.2-46.9 84-46.9V78c-27.5 0-63.5 19.6-99.9 53.6-36.4-33.8-72.4-53.2-99.9-53.2v22.3c20.7 0 51.4 16.5 84 46.6-14 14.7-28 31.4-41.3 49.9-22.6 2.4-44 6.1-63.6 11-2.3-10-4-19.7-5.2-29-4.7-38.2 1.1-67.9 14.6-75.8 3-1.8 6.9-2.6 11.5-2.6V78.5c-8.4 0-16 1.8-22.6 5.6-28.1 16.2-34.4 66.7-19.9 130.1-62.2 19.2-102.7 49.9-102.7 82.3 0 32.5 40.7 63.3 103.1 82.4-14.4 63.6-8 114.2 20.2 130.4 6.5 3.8 14.1 5.6 22.5 5.6 27.5 0 63.5-19.6 99.9-53.6 36.4 33.8 72.4 53.2 99.9 53.2 8.4 0 16-1.8 22.6-5.6 28.1-16.2 34.4-66.7 19.9-130.1 62-19.1 102.5-49.9 102.5-82.3zm-130.2-66.7c-3.7 12.9-8.3 26.2-13.5 39.5-4.1-8-8.4-16-13.1-24-4.6-8-9.5-15.8-14.4-23.4 14.2 2.1 27.9 4.7 41 7.9zm-45.8 106.5c-7.8 13.5-15.8 26.3-24.1 38.2-14.9 1.3-30 2-45.2 2-15.1 0-30.2-.7-45-1.9-8.3-11.9-16.4-24.6-24.2-38-7.6-13.1-14.5-26.4-20.8-39.8 6.2-13.4 13.2-26.8 20.7-39.9 7.8-13.5 15.8-26.3 24.1-38.2 14.9-1.3 30-2 45.2-2 15.1 0 30.2.7 45 1.9 8.3 11.9 16.4 24.6 24.2 38 7.6 13.1 14.5 26.4 20.8 39.8-6.3 13.4-13.2 26.8-20.7 39.9zm32.3-13c5.4 13.4 10 26.8 13.8 39.8-13.1 3.2-26.9 5.9-41.2 8 4.9-7.7 9.8-15.6 14.4-23.7 4.6-8 8.9-16.1 13-24.1zM421.2 430c-9.3-9.6-18.6-20.3-27.8-32 9 .4 18.2.7 27.5.7 9.4 0 18.7-.2 27.8-.7-9 11.7-18.3 22.4-27.5 32zm-74.4-58.9c-14.2-2.1-27.9-4.7-41-7.9 3.7-12.9 8.3-26.2 13.5-39.5 4.1 8 8.4 16 13.1 24 4.7 8 9.5 15.8 14.4 23.4zM420.7 163c9.3 9.6 18.6 20.3 27.8 32-9-.4-18.2-.7-27.5-.7-9.4 0-18.7.2-27.8.7 9-11.7 18.3-22.4 27.5-32zm-74 58.9c-4.9 7.7-9.8 15.6-14.4 23.7-4.6 8-8.9 16-13 24-5.4-13.4-10-26.8-13.8-39.8 13.1-3.1 26.9-5.8 41.2-7.9zm-90.5 125.2c-35.4-15.1-58.3-34.9-58.3-50.6 0-15.7 22.9-35.6 58.3-50.6 8.6-3.7 18-7 27.7-10.1 5.7 19.6 13.2 40 22.5 60.9-9.2 20.8-16.6 41.1-22.2 60.6-9.9-3.1-19.3-6.5-28-10.2zM310 490c-13.6-7.8-19.5-37.5-14.9-75.7 1.1-9.4 2.9-19.3 5.1-29.4 19.6 4.8 41 8.5 63.5 10.9 13.5 18.5 27.5 35.3 41.6 50-32.6 30.3-63.2 46.9-84 46.9-4.5-.1-8.3-1-11.3-2.7zm237.2-76.2c4.7 38.2-1.1 67.9-14.6 75.8-3 1.8-6.9 2.6-11.5 2.6-20.7 0-51.4-16.5-84-46.6 14-14.7 28-31.4 41.3-49.9 22.6-2.4 44-6.1 63.6-11 2.3 10.1 4.1 19.8 5.2 29.1zm38.5-66.7c-8.6 3.7-18 7-27.7 10.1-5.7-19.6-13.2-40-22.5-60.9 9.2-20.8 16.6-41.1 22.2-60.6 9.9 3.1 19.3 6.5 28.1 10.2 35.4 15.1 58.3 34.9 58.3 50.6-.1 15.7-23 35.6-58.4 50.6zM320.8 78.4z\\"/\\u003e\\u003ccircle cx\\u003d\\"420.9\\" cy\\u003d\\"296.5\\" r\\u003d\\"45.7\\"/\\u003e\\u003cpath d\\u003d\\"M520.5 78.1z\\"/\\u003e\\u003c/g\\u003e\\u003c/svg\\u003e\\n\\\\ No newline at end of file\\ndiff --git a/dev/null b/src/react-app-env.d.ts\\n--- a/dev/null\\n+++ b/src/react-app-env.d.ts\\n@@ -0,0 +1 @@\\n+/// \\u003creference types\\u003d\\"react-scripts\\" /\\u003e\\ndiff --git a/dev/null b/src/reportWebVitals.ts\\n--- a/dev/null\\n+++ b/src/reportWebVitals.ts\\n@@ -0,0 +1,15 @@\\n+import { ReportHandler } from \\u0027web-vitals\\u0027;\\n+\\n+const reportWebVitals \\u003d (onPerfEntry?: ReportHandler) \\u003d\\u003e {\\n+  if (onPerfEntry \\u0026\\u0026 onPerfEntry instanceof Function) {\\n+    import(\\u0027web-vitals\\u0027).then(({ getCLS, getFID, getFCP, getLCP, getTTFB }) \\u003d\\u003e {\\n+      getCLS(onPerfEntry);\\n+      getFID(onPerfEntry);\\n+      getFCP(onPerfEntry);\\n+      getLCP(onPerfEntry);\\n+      getTTFB(onPerfEntry);\\n+    });\\n+  }\\n+};\\n+\\n+export default reportWebVitals;\\ndiff --git a/dev/null b/src/setupTests.ts\\n--- a/dev/null\\n+++ b/src/setupTests.ts\\n@@ -0,0 +1,5 @@\\n+// jest-dom adds custom jest matchers for asserting on DOM nodes.\\n+// allows you to do things like:\\n+// expect(element).toHaveTextContent(/react/i)\\n+// learn more: https://github.com/testing-library/jest-dom\\n+import \\u0027@testing-library/jest-dom\\u0027;\\n","isIgnored":false,"files":[{"name":".DS_Store","extension":"DS_Store","isIgnored":false,"fileScore":{"totalScore":0.0,"scoreAdditions":0,"scoreDeletions":0,"scoreBlankAdditions":0,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":0,"numDeletions":0,"numBlankAdditions":0,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/.DS_Store","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/.DS_Store","lineType":"HEADER"},{"diffLine":"Binary files /dev/null and b/.DS_Store differ","lineType":"UNCHANGED"}]},{"name":"src/App.css","extension":"css","isIgnored":false,"fileScore":{"totalScore":33.0,"scoreAdditions":33,"scoreDeletions":0,"scoreBlankAdditions":5,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":33,"numDeletions":0,"numBlankAdditions":5,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.css","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.css","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,38 @@","lineType":"HEADER"},{"diffLine":"+.App {","lineType":"ADDITION"},{"diffLine":"+  text-align: center;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-logo {","lineType":"ADDITION"},{"diffLine":"+  height: 40vmin;","lineType":"ADDITION"},{"diffLine":"+  pointer-events: none;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+@media (prefers-reduced-motion: no-preference) {","lineType":"ADDITION"},{"diffLine":"+  .App-logo {","lineType":"ADDITION"},{"diffLine":"+    animation: App-logo-spin infinite 20s linear;","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-header {","lineType":"ADDITION"},{"diffLine":"+  background-color: #282c34;","lineType":"ADDITION"},{"diffLine":"+  min-height: 100vh;","lineType":"ADDITION"},{"diffLine":"+  display: flex;","lineType":"ADDITION"},{"diffLine":"+  flex-direction: column;","lineType":"ADDITION"},{"diffLine":"+  align-items: center;","lineType":"ADDITION"},{"diffLine":"+  justify-content: center;","lineType":"ADDITION"},{"diffLine":"+  font-size: calc(10px + 2vmin);","lineType":"ADDITION"},{"diffLine":"+  color: white;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-link {","lineType":"ADDITION"},{"diffLine":"+  color: #61dafb;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+@keyframes App-logo-spin {","lineType":"ADDITION"},{"diffLine":"+  from {","lineType":"ADDITION"},{"diffLine":"+    transform: rotate(0deg);","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+  to {","lineType":"ADDITION"},{"diffLine":"+    transform: rotate(360deg);","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"}]},{"name":"src/App.test.tsx","extension":"test.tsx","isIgnored":false,"fileScore":{"totalScore":41.0,"scoreAdditions":41,"scoreDeletions":0,"scoreBlankAdditions":6,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":41,"numDeletions":0,"numBlankAdditions":6,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.test.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.test.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,9 @@","lineType":"HEADER"},{"diffLine":"+import React from \\u0027react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import { render, screen } from \\u0027@testing-library/react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import App from \\u0027./App\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+test(\\u0027renders learn react link\\u0027, () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  render(\\u003cApp /\\u003e);","lineType":"ADDITION"},{"diffLine":"+  const linkElement \\u003d screen.getByText(/learn react/i);","lineType":"ADDITION"},{"diffLine":"+  expect(linkElement).toBeInTheDocument();","lineType":"ADDITION"},{"diffLine":"+});","lineType":"ADDITION"}]},{"name":"src/App.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":67.0,"scoreAdditions":67,"scoreDeletions":0,"scoreBlankAdditions":8,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":67,"numDeletions":0,"numBlankAdditions":8,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,28 @@","lineType":"HEADER"},{"diffLine":"+import React from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+import { Counter } from \\"./Counter\\";","lineType":"ADDITION"},{"diffLine":"+import { TextField } from \\"./TextField\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const App: React.FC \\u003d () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      {/* \\u003cTextField","lineType":"ADDITION"},{"diffLine":"+        text\\u003d\\"hii\\"","lineType":"ADDITION"},{"diffLine":"+        person\\u003d{{ firstName: \\"\\", lastName: \\"\\" }}","lineType":"ADDITION"},{"diffLine":"+        length\\u003d{50}","lineType":"ADDITION"},{"diffLine":"+        handleChange\\u003d{(e) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          e.preventDefault();","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      /\\u003e */}","lineType":"ADDITION"},{"diffLine":"+      \\u003cCounter\\u003e","lineType":"ADDITION"},{"diffLine":"+        {(count, setCount) \\u003d\\u003e (","lineType":"ADDITION"},{"diffLine":"+          \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+            {count}","lineType":"ADDITION"},{"diffLine":"+            \\u003cbutton onClick\\u003d{() \\u003d\\u003e setCount(count + 1)}\\u003e+\\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+          \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+        )}","lineType":"ADDITION"},{"diffLine":"+      \\u003c/Counter\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export default App;","lineType":"ADDITION"}]},{"name":"src/Counter.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":78.0,"scoreAdditions":78,"scoreDeletions":0,"scoreBlankAdditions":11,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":78,"numDeletions":0,"numBlankAdditions":11,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/Counter.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/Counter.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,14 @@","lineType":"HEADER"},{"diffLine":"+import React, { useState } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Props {","lineType":"ADDITION"},{"diffLine":"+  children: (","lineType":"ADDITION"},{"diffLine":"+    count: number,","lineType":"ADDITION"},{"diffLine":"+    setCount: React.Dispatch\\u003cReact.SetStateAction\\u003cnumber\\u003e\\u003e","lineType":"ADDITION"},{"diffLine":"+  ) \\u003d\\u003e JSX.Element | null;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const Counter: React.FC\\u003cProps\\u003e \\u003d ({ children }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  const [count, setCount] \\u003d useState(0);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return \\u003cdiv\\u003e{children(count, setCount)}\\u003c/div\\u003e;","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/ReducerExample.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":122.0,"scoreAdditions":122,"scoreDeletions":0,"scoreBlankAdditions":17,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":122,"numDeletions":0,"numBlankAdditions":17,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/ReducerExample.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/ReducerExample.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,50 @@","lineType":"HEADER"},{"diffLine":"+import React, { useReducer } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+type Actions \\u003d","lineType":"ADDITION"},{"diffLine":"+  | { type: \\"add\\"; text: string }","lineType":"ADDITION"},{"diffLine":"+  | {","lineType":"ADDITION"},{"diffLine":"+      type: \\"remove\\";","lineType":"ADDITION"},{"diffLine":"+      idx: number;","lineType":"ADDITION"},{"diffLine":"+    };","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Todo {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+  complete: boolean;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+type State \\u003d Todo[];","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const TodoReducer \\u003d (state: State, action: Actions) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  switch (action.type) {","lineType":"ADDITION"},{"diffLine":"+    case \\"add\\":","lineType":"ADDITION"},{"diffLine":"+      return [...state, { text: action.text, complete: false }];","lineType":"ADDITION"},{"diffLine":"+    case \\"remove\\":","lineType":"ADDITION"},{"diffLine":"+      return state.filter((_, i) \\u003d\\u003e action.idx !\\u003d\\u003d i);","lineType":"ADDITION"},{"diffLine":"+    default:","lineType":"ADDITION"},{"diffLine":"+      return state;","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const ReducerExample: React.FC \\u003d () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  const [todos, dispatch] \\u003d useReducer(TodoReducer, []);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      {JSON.stringify(todos)}","lineType":"ADDITION"},{"diffLine":"+      \\u003cbutton","lineType":"ADDITION"},{"diffLine":"+        onClick\\u003d{() \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          dispatch({ type: \\"add\\", text: \\"...\\" });","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      \\u003e","lineType":"ADDITION"},{"diffLine":"+        +","lineType":"ADDITION"},{"diffLine":"+      \\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+      \\u003cbutton","lineType":"ADDITION"},{"diffLine":"+        onClick\\u003d{() \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          dispatch({ type: \\"remove\\", idx: 5 });","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      \\u003e","lineType":"ADDITION"},{"diffLine":"+        -","lineType":"ADDITION"},{"diffLine":"+      \\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/TextField.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":147.0,"scoreAdditions":147,"scoreDeletions":0,"scoreBlankAdditions":23,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":147,"numDeletions":0,"numBlankAdditions":23,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/TextField.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/TextField.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,31 @@","lineType":"HEADER"},{"diffLine":"+import React, { useState, useRef } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Person {","lineType":"ADDITION"},{"diffLine":"+  firstName: string;","lineType":"ADDITION"},{"diffLine":"+  lastName: string;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Props {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+  ok?: boolean;","lineType":"ADDITION"},{"diffLine":"+  length: number;","lineType":"ADDITION"},{"diffLine":"+  handleChange: (event: React.ChangeEvent\\u003cHTMLInputElement\\u003e) \\u003d\\u003e void;","lineType":"ADDITION"},{"diffLine":"+  person: Person;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface TextNode {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const TextField: React.FC\\u003cProps\\u003e \\u003d ({ handleChange }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  //   const [count, setCount] \\u003d useState\\u003cnumber | null\\u003e(5);","lineType":"ADDITION"},{"diffLine":"+  const [message, setMessage] \\u003d useState\\u003cTextNode\\u003e({ text: \\"hello\\" });","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  const inputRef \\u003d useRef\\u003cHTMLInputElement\\u003e(null);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      \\u003cinput ref\\u003d{inputRef} onChange\\u003d{handleChange} /\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/index.css","extension":"css","isIgnored":false,"fileScore":{"totalScore":159.0,"scoreAdditions":159,"scoreDeletions":0,"scoreBlankAdditions":24,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":159,"numDeletions":0,"numBlankAdditions":24,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/index.css","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/index.css","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,13 @@","lineType":"HEADER"},{"diffLine":"+body {","lineType":"ADDITION"},{"diffLine":"+  margin: 0;","lineType":"ADDITION"},{"diffLine":"+  font-family: -apple-system, BlinkMacSystemFont, \\u0027Segoe UI\\u0027, \\u0027Roboto\\u0027, \\u0027Oxygen\\u0027,","lineType":"ADDITION"},{"diffLine":"+    \\u0027Ubuntu\\u0027, \\u0027Cantarell\\u0027, \\u0027Fira Sans\\u0027, \\u0027Droid Sans\\u0027, \\u0027Helvetica Neue\\u0027,","lineType":"ADDITION"},{"diffLine":"+    sans-serif;","lineType":"ADDITION"},{"diffLine":"+  -webkit-font-smoothing: antialiased;","lineType":"ADDITION"},{"diffLine":"+  -moz-osx-font-smoothing: grayscale;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+code {","lineType":"ADDITION"},{"diffLine":"+  font-family: source-code-pro, Menlo, Monaco, Consolas, \\u0027Courier New\\u0027,","lineType":"ADDITION"},{"diffLine":"+    monospace;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"}]},{"name":"src/index.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":174.0,"scoreAdditions":174,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":174,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/index.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/index.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,17 @@","lineType":"HEADER"},{"diffLine":"+import React from \\u0027react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import ReactDOM from \\u0027react-dom\\u0027;","lineType":"ADDITION"},{"diffLine":"+import \\u0027./index.css\\u0027;","lineType":"ADDITION"},{"diffLine":"+import App from \\u0027./App\\u0027;","lineType":"ADDITION"},{"diffLine":"+import reportWebVitals from \\u0027./reportWebVitals\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+ReactDOM.render(","lineType":"ADDITION"},{"diffLine":"+  \\u003cReact.StrictMode\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003cApp /\\u003e","lineType":"ADDITION"},{"diffLine":"+  \\u003c/React.StrictMode\\u003e,","lineType":"ADDITION"},{"diffLine":"+  document.getElementById(\\u0027root\\u0027)","lineType":"ADDITION"},{"diffLine":"+);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+// If you want to start measuring performance in your app, pass a function","lineType":"ADDITION"},{"diffLine":"+// to log results (for example: reportWebVitals(console.log))","lineType":"ADDITION"},{"diffLine":"+// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals","lineType":"ADDITION"},{"diffLine":"+reportWebVitals();","lineType":"ADDITION"}]},{"name":"src/logo.svg","extension":"svg","isIgnored":false,"fileScore":{"totalScore":175.0,"scoreAdditions":175,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":175,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/logo.svg","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/logo.svg","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1 @@","lineType":"HEADER"},{"diffLine":"+\\u003csvg xmlns\\u003d\\"http://www.w3.org/2000/svg\\" viewBox\\u003d\\"0 0 841.9 595.3\\"\\u003e\\u003cg fill\\u003d\\"#61DAFB\\"\\u003e\\u003cpath d\\u003d\\"M666.3 296.5c0-32.5-40.7-63.3-103.1-82.4 14.4-63.6 8-114.2-20.2-130.4-6.5-3.8-14.1-5.6-22.4-5.6v22.3c4.6 0 8.3.9 11.4 2.6 13.6 7.8 19.5 37.5 14.9 75.7-1.1 9.4-2.9 19.3-5.1 29.4-19.6-4.8-41-8.5-63.5-10.9-13.5-18.5-27.5-35.3-41.6-50 32.6-30.3 63.2-46.9 84-46.9V78c-27.5 0-63.5 19.6-99.9 53.6-36.4-33.8-72.4-53.2-99.9-53.2v22.3c20.7 0 51.4 16.5 84 46.6-14 14.7-28 31.4-41.3 49.9-22.6 2.4-44 6.1-63.6 11-2.3-10-4-19.7-5.2-29-4.7-38.2 1.1-67.9 14.6-75.8 3-1.8 6.9-2.6 11.5-2.6V78.5c-8.4 0-16 1.8-22.6 5.6-28.1 16.2-34.4 66.7-19.9 130.1-62.2 19.2-102.7 49.9-102.7 82.3 0 32.5 40.7 63.3 103.1 82.4-14.4 63.6-8 114.2 20.2 130.4 6.5 3.8 14.1 5.6 22.5 5.6 27.5 0 63.5-19.6 99.9-53.6 36.4 33.8 72.4 53.2 99.9 53.2 8.4 0 16-1.8 22.6-5.6 28.1-16.2 34.4-66.7 19.9-130.1 62-19.1 102.5-49.9 102.5-82.3zm-130.2-66.7c-3.7 12.9-8.3 26.2-13.5 39.5-4.1-8-8.4-16-13.1-24-4.6-8-9.5-15.8-14.4-23.4 14.2 2.1 27.9 4.7 41 7.9zm-45.8 106.5c-7.8 13.5-15.8 26.3-24.1 38.2-14.9 1.3-30 2-45.2 2-15.1 0-30.2-.7-45-1.9-8.3-11.9-16.4-24.6-24.2-38-7.6-13.1-14.5-26.4-20.8-39.8 6.2-13.4 13.2-26.8 20.7-39.9 7.8-13.5 15.8-26.3 24.1-38.2 14.9-1.3 30-2 45.2-2 15.1 0 30.2.7 45 1.9 8.3 11.9 16.4 24.6 24.2 38 7.6 13.1 14.5 26.4 20.8 39.8-6.3 13.4-13.2 26.8-20.7 39.9zm32.3-13c5.4 13.4 10 26.8 13.8 39.8-13.1 3.2-26.9 5.9-41.2 8 4.9-7.7 9.8-15.6 14.4-23.7 4.6-8 8.9-16.1 13-24.1zM421.2 430c-9.3-9.6-18.6-20.3-27.8-32 9 .4 18.2.7 27.5.7 9.4 0 18.7-.2 27.8-.7-9 11.7-18.3 22.4-27.5 32zm-74.4-58.9c-14.2-2.1-27.9-4.7-41-7.9 3.7-12.9 8.3-26.2 13.5-39.5 4.1 8 8.4 16 13.1 24 4.7 8 9.5 15.8 14.4 23.4zM420.7 163c9.3 9.6 18.6 20.3 27.8 32-9-.4-18.2-.7-27.5-.7-9.4 0-18.7.2-27.8.7 9-11.7 18.3-22.4 27.5-32zm-74 58.9c-4.9 7.7-9.8 15.6-14.4 23.7-4.6 8-8.9 16-13 24-5.4-13.4-10-26.8-13.8-39.8 13.1-3.1 26.9-5.8 41.2-7.9zm-90.5 125.2c-35.4-15.1-58.3-34.9-58.3-50.6 0-15.7 22.9-35.6 58.3-50.6 8.6-3.7 18-7 27.7-10.1 5.7 19.6 13.2 40 22.5 60.9-9.2 20.8-16.6 41.1-22.2 60.6-9.9-3.1-19.3-6.5-28-10.2zM310 490c-13.6-7.8-19.5-37.5-14.9-75.7 1.1-9.4 2.9-19.3 5.1-29.4 19.6 4.8 41 8.5 63.5 10.9 13.5 18.5 27.5 35.3 41.6 50-32.6 30.3-63.2 46.9-84 46.9-4.5-.1-8.3-1-11.3-2.7zm237.2-76.2c4.7 38.2-1.1 67.9-14.6 75.8-3 1.8-6.9 2.6-11.5 2.6-20.7 0-51.4-16.5-84-46.6 14-14.7 28-31.4 41.3-49.9 22.6-2.4 44-6.1 63.6-11 2.3 10.1 4.1 19.8 5.2 29.1zm38.5-66.7c-8.6 3.7-18 7-27.7 10.1-5.7-19.6-13.2-40-22.5-60.9 9.2-20.8 16.6-41.1 22.2-60.6 9.9 3.1 19.3 6.5 28.1 10.2 35.4 15.1 58.3 34.9 58.3 50.6-.1 15.7-23 35.6-58.4 50.6zM320.8 78.4z\\"/\\u003e\\u003ccircle cx\\u003d\\"420.9\\" cy\\u003d\\"296.5\\" r\\u003d\\"45.7\\"/\\u003e\\u003cpath d\\u003d\\"M520.5 78.1z\\"/\\u003e\\u003c/g\\u003e\\u003c/svg\\u003e","lineType":"ADDITION"},{"diffLine":"","lineType":"UNCHANGED"}]},{"name":"src/react-app-env.d.ts","extension":"d.ts","isIgnored":false,"fileScore":{"totalScore":176.0,"scoreAdditions":176,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":176,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/react-app-env.d.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/react-app-env.d.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1 @@","lineType":"HEADER"},{"diffLine":"+/// \\u003creference types\\u003d\\"react-scripts\\" /\\u003e","lineType":"ADDITION"}]},{"name":"src/reportWebVitals.ts","extension":"ts","isIgnored":false,"fileScore":{"totalScore":189.0,"scoreAdditions":189,"scoreDeletions":0,"scoreBlankAdditions":28,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":189,"numDeletions":0,"numBlankAdditions":28,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/reportWebVitals.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/reportWebVitals.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,15 @@","lineType":"HEADER"},{"diffLine":"+import { ReportHandler } from \\u0027web-vitals\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const reportWebVitals \\u003d (onPerfEntry?: ReportHandler) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  if (onPerfEntry \\u0026\\u0026 onPerfEntry instanceof Function) {","lineType":"ADDITION"},{"diffLine":"+    import(\\u0027web-vitals\\u0027).then(({ getCLS, getFID, getFCP, getLCP, getTTFB }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+      getCLS(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getFID(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getFCP(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getLCP(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getTTFB(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+    });","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export default reportWebVitals;","lineType":"ADDITION"}]},{"name":"src/setupTests.ts","extension":"ts","isIgnored":false,"fileScore":{"totalScore":194.0,"scoreAdditions":194,"scoreDeletions":0,"scoreBlankAdditions":28,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":194,"numDeletions":0,"numBlankAdditions":28,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/setupTests.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/setupTests.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,5 @@","lineType":"HEADER"},{"diffLine":"+// jest-dom adds custom jest matchers for asserting on DOM nodes.","lineType":"ADDITION"},{"diffLine":"+// allows you to do things like:","lineType":"ADDITION"},{"diffLine":"+// expect(element).toHaveTextContent(/react/i)","lineType":"ADDITION"},{"diffLine":"+// learn more: https://github.com/testing-library/jest-dom","lineType":"ADDITION"},{"diffLine":"+import \\u0027@testing-library/jest-dom\\u0027;","lineType":"ADDITION"}]}],"score":1555.0}],"committerNames":["grace_luo@sfu.ca"],"sumOfCommitsScore":1555.0,"isIgnored":false,"files":[{"name":"src/App.css","extension":"css","isIgnored":false,"fileScore":{"totalScore":33.0,"scoreAdditions":33,"scoreDeletions":0,"scoreBlankAdditions":5,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":33,"numDeletions":0,"numBlankAdditions":5,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.css","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.css","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,38 @@","lineType":"HEADER"},{"diffLine":"+.App {","lineType":"ADDITION"},{"diffLine":"+  text-align: center;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-logo {","lineType":"ADDITION"},{"diffLine":"+  height: 40vmin;","lineType":"ADDITION"},{"diffLine":"+  pointer-events: none;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+@media (prefers-reduced-motion: no-preference) {","lineType":"ADDITION"},{"diffLine":"+  .App-logo {","lineType":"ADDITION"},{"diffLine":"+    animation: App-logo-spin infinite 20s linear;","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-header {","lineType":"ADDITION"},{"diffLine":"+  background-color: #282c34;","lineType":"ADDITION"},{"diffLine":"+  min-height: 100vh;","lineType":"ADDITION"},{"diffLine":"+  display: flex;","lineType":"ADDITION"},{"diffLine":"+  flex-direction: column;","lineType":"ADDITION"},{"diffLine":"+  align-items: center;","lineType":"ADDITION"},{"diffLine":"+  justify-content: center;","lineType":"ADDITION"},{"diffLine":"+  font-size: calc(10px + 2vmin);","lineType":"ADDITION"},{"diffLine":"+  color: white;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+.App-link {","lineType":"ADDITION"},{"diffLine":"+  color: #61dafb;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+@keyframes App-logo-spin {","lineType":"ADDITION"},{"diffLine":"+  from {","lineType":"ADDITION"},{"diffLine":"+    transform: rotate(0deg);","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+  to {","lineType":"ADDITION"},{"diffLine":"+    transform: rotate(360deg);","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"}]},{"name":"src/App.test.tsx","extension":"test.tsx","isIgnored":false,"fileScore":{"totalScore":41.0,"scoreAdditions":41,"scoreDeletions":0,"scoreBlankAdditions":6,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":41,"numDeletions":0,"numBlankAdditions":6,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.test.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.test.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,9 @@","lineType":"HEADER"},{"diffLine":"+import React from \\u0027react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import { render, screen } from \\u0027@testing-library/react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import App from \\u0027./App\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+test(\\u0027renders learn react link\\u0027, () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  render(\\u003cApp /\\u003e);","lineType":"ADDITION"},{"diffLine":"+  const linkElement \\u003d screen.getByText(/learn react/i);","lineType":"ADDITION"},{"diffLine":"+  expect(linkElement).toBeInTheDocument();","lineType":"ADDITION"},{"diffLine":"+});","lineType":"ADDITION"}]},{"name":"src/App.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":67.0,"scoreAdditions":67,"scoreDeletions":0,"scoreBlankAdditions":8,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":67,"numDeletions":0,"numBlankAdditions":8,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/App.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/App.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,28 @@","lineType":"HEADER"},{"diffLine":"+import React from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+import { Counter } from \\"./Counter\\";","lineType":"ADDITION"},{"diffLine":"+import { TextField } from \\"./TextField\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const App: React.FC \\u003d () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      {/* \\u003cTextField","lineType":"ADDITION"},{"diffLine":"+        text\\u003d\\"hii\\"","lineType":"ADDITION"},{"diffLine":"+        person\\u003d{{ firstName: \\"\\", lastName: \\"\\" }}","lineType":"ADDITION"},{"diffLine":"+        length\\u003d{50}","lineType":"ADDITION"},{"diffLine":"+        handleChange\\u003d{(e) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          e.preventDefault();","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      /\\u003e */}","lineType":"ADDITION"},{"diffLine":"+      \\u003cCounter\\u003e","lineType":"ADDITION"},{"diffLine":"+        {(count, setCount) \\u003d\\u003e (","lineType":"ADDITION"},{"diffLine":"+          \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+            {count}","lineType":"ADDITION"},{"diffLine":"+            \\u003cbutton onClick\\u003d{() \\u003d\\u003e setCount(count + 1)}\\u003e+\\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+          \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+        )}","lineType":"ADDITION"},{"diffLine":"+      \\u003c/Counter\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export default App;","lineType":"ADDITION"}]},{"name":"src/Counter.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":78.0,"scoreAdditions":78,"scoreDeletions":0,"scoreBlankAdditions":11,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":78,"numDeletions":0,"numBlankAdditions":11,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/Counter.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/Counter.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,14 @@","lineType":"HEADER"},{"diffLine":"+import React, { useState } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Props {","lineType":"ADDITION"},{"diffLine":"+  children: (","lineType":"ADDITION"},{"diffLine":"+    count: number,","lineType":"ADDITION"},{"diffLine":"+    setCount: React.Dispatch\\u003cReact.SetStateAction\\u003cnumber\\u003e\\u003e","lineType":"ADDITION"},{"diffLine":"+  ) \\u003d\\u003e JSX.Element | null;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const Counter: React.FC\\u003cProps\\u003e \\u003d ({ children }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  const [count, setCount] \\u003d useState(0);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return \\u003cdiv\\u003e{children(count, setCount)}\\u003c/div\\u003e;","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/ReducerExample.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":122.0,"scoreAdditions":122,"scoreDeletions":0,"scoreBlankAdditions":17,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":122,"numDeletions":0,"numBlankAdditions":17,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/ReducerExample.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/ReducerExample.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,50 @@","lineType":"HEADER"},{"diffLine":"+import React, { useReducer } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+type Actions \\u003d","lineType":"ADDITION"},{"diffLine":"+  | { type: \\"add\\"; text: string }","lineType":"ADDITION"},{"diffLine":"+  | {","lineType":"ADDITION"},{"diffLine":"+      type: \\"remove\\";","lineType":"ADDITION"},{"diffLine":"+      idx: number;","lineType":"ADDITION"},{"diffLine":"+    };","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Todo {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+  complete: boolean;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+type State \\u003d Todo[];","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const TodoReducer \\u003d (state: State, action: Actions) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  switch (action.type) {","lineType":"ADDITION"},{"diffLine":"+    case \\"add\\":","lineType":"ADDITION"},{"diffLine":"+      return [...state, { text: action.text, complete: false }];","lineType":"ADDITION"},{"diffLine":"+    case \\"remove\\":","lineType":"ADDITION"},{"diffLine":"+      return state.filter((_, i) \\u003d\\u003e action.idx !\\u003d\\u003d i);","lineType":"ADDITION"},{"diffLine":"+    default:","lineType":"ADDITION"},{"diffLine":"+      return state;","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const ReducerExample: React.FC \\u003d () \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  const [todos, dispatch] \\u003d useReducer(TodoReducer, []);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      {JSON.stringify(todos)}","lineType":"ADDITION"},{"diffLine":"+      \\u003cbutton","lineType":"ADDITION"},{"diffLine":"+        onClick\\u003d{() \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          dispatch({ type: \\"add\\", text: \\"...\\" });","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      \\u003e","lineType":"ADDITION"},{"diffLine":"+        +","lineType":"ADDITION"},{"diffLine":"+      \\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+      \\u003cbutton","lineType":"ADDITION"},{"diffLine":"+        onClick\\u003d{() \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+          dispatch({ type: \\"remove\\", idx: 5 });","lineType":"ADDITION"},{"diffLine":"+        }}","lineType":"ADDITION"},{"diffLine":"+      \\u003e","lineType":"ADDITION"},{"diffLine":"+        -","lineType":"ADDITION"},{"diffLine":"+      \\u003c/button\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/TextField.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":147.0,"scoreAdditions":147,"scoreDeletions":0,"scoreBlankAdditions":23,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":147,"numDeletions":0,"numBlankAdditions":23,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/TextField.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/TextField.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,31 @@","lineType":"HEADER"},{"diffLine":"+import React, { useState, useRef } from \\"react\\";","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Person {","lineType":"ADDITION"},{"diffLine":"+  firstName: string;","lineType":"ADDITION"},{"diffLine":"+  lastName: string;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface Props {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+  ok?: boolean;","lineType":"ADDITION"},{"diffLine":"+  length: number;","lineType":"ADDITION"},{"diffLine":"+  handleChange: (event: React.ChangeEvent\\u003cHTMLInputElement\\u003e) \\u003d\\u003e void;","lineType":"ADDITION"},{"diffLine":"+  person: Person;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+interface TextNode {","lineType":"ADDITION"},{"diffLine":"+  text: string;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export const TextField: React.FC\\u003cProps\\u003e \\u003d ({ handleChange }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  //   const [count, setCount] \\u003d useState\\u003cnumber | null\\u003e(5);","lineType":"ADDITION"},{"diffLine":"+  const [message, setMessage] \\u003d useState\\u003cTextNode\\u003e({ text: \\"hello\\" });","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  const inputRef \\u003d useRef\\u003cHTMLInputElement\\u003e(null);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+  return (","lineType":"ADDITION"},{"diffLine":"+    \\u003cdiv\\u003e","lineType":"ADDITION"},{"diffLine":"+      \\u003cinput ref\\u003d{inputRef} onChange\\u003d{handleChange} /\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003c/div\\u003e","lineType":"ADDITION"},{"diffLine":"+  );","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"}]},{"name":"src/index.css","extension":"css","isIgnored":false,"fileScore":{"totalScore":159.0,"scoreAdditions":159,"scoreDeletions":0,"scoreBlankAdditions":24,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":159,"numDeletions":0,"numBlankAdditions":24,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/index.css","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/index.css","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,13 @@","lineType":"HEADER"},{"diffLine":"+body {","lineType":"ADDITION"},{"diffLine":"+  margin: 0;","lineType":"ADDITION"},{"diffLine":"+  font-family: -apple-system, BlinkMacSystemFont, \\u0027Segoe UI\\u0027, \\u0027Roboto\\u0027, \\u0027Oxygen\\u0027,","lineType":"ADDITION"},{"diffLine":"+    \\u0027Ubuntu\\u0027, \\u0027Cantarell\\u0027, \\u0027Fira Sans\\u0027, \\u0027Droid Sans\\u0027, \\u0027Helvetica Neue\\u0027,","lineType":"ADDITION"},{"diffLine":"+    sans-serif;","lineType":"ADDITION"},{"diffLine":"+  -webkit-font-smoothing: antialiased;","lineType":"ADDITION"},{"diffLine":"+  -moz-osx-font-smoothing: grayscale;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+code {","lineType":"ADDITION"},{"diffLine":"+  font-family: source-code-pro, Menlo, Monaco, Consolas, \\u0027Courier New\\u0027,","lineType":"ADDITION"},{"diffLine":"+    monospace;","lineType":"ADDITION"},{"diffLine":"+}","lineType":"ADDITION"}]},{"name":"src/index.tsx","extension":"tsx","isIgnored":false,"fileScore":{"totalScore":174.0,"scoreAdditions":174,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":174,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/index.tsx","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/index.tsx","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,17 @@","lineType":"HEADER"},{"diffLine":"+import React from \\u0027react\\u0027;","lineType":"ADDITION"},{"diffLine":"+import ReactDOM from \\u0027react-dom\\u0027;","lineType":"ADDITION"},{"diffLine":"+import \\u0027./index.css\\u0027;","lineType":"ADDITION"},{"diffLine":"+import App from \\u0027./App\\u0027;","lineType":"ADDITION"},{"diffLine":"+import reportWebVitals from \\u0027./reportWebVitals\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+ReactDOM.render(","lineType":"ADDITION"},{"diffLine":"+  \\u003cReact.StrictMode\\u003e","lineType":"ADDITION"},{"diffLine":"+    \\u003cApp /\\u003e","lineType":"ADDITION"},{"diffLine":"+  \\u003c/React.StrictMode\\u003e,","lineType":"ADDITION"},{"diffLine":"+  document.getElementById(\\u0027root\\u0027)","lineType":"ADDITION"},{"diffLine":"+);","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+// If you want to start measuring performance in your app, pass a function","lineType":"ADDITION"},{"diffLine":"+// to log results (for example: reportWebVitals(console.log))","lineType":"ADDITION"},{"diffLine":"+// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals","lineType":"ADDITION"},{"diffLine":"+reportWebVitals();","lineType":"ADDITION"}]},{"name":"src/logo.svg","extension":"svg","isIgnored":false,"fileScore":{"totalScore":175.0,"scoreAdditions":175,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":175,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/logo.svg","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/logo.svg","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1 @@","lineType":"HEADER"},{"diffLine":"+\\u003csvg xmlns\\u003d\\"http://www.w3.org/2000/svg\\" viewBox\\u003d\\"0 0 841.9 595.3\\"\\u003e\\u003cg fill\\u003d\\"#61DAFB\\"\\u003e\\u003cpath d\\u003d\\"M666.3 296.5c0-32.5-40.7-63.3-103.1-82.4 14.4-63.6 8-114.2-20.2-130.4-6.5-3.8-14.1-5.6-22.4-5.6v22.3c4.6 0 8.3.9 11.4 2.6 13.6 7.8 19.5 37.5 14.9 75.7-1.1 9.4-2.9 19.3-5.1 29.4-19.6-4.8-41-8.5-63.5-10.9-13.5-18.5-27.5-35.3-41.6-50 32.6-30.3 63.2-46.9 84-46.9V78c-27.5 0-63.5 19.6-99.9 53.6-36.4-33.8-72.4-53.2-99.9-53.2v22.3c20.7 0 51.4 16.5 84 46.6-14 14.7-28 31.4-41.3 49.9-22.6 2.4-44 6.1-63.6 11-2.3-10-4-19.7-5.2-29-4.7-38.2 1.1-67.9 14.6-75.8 3-1.8 6.9-2.6 11.5-2.6V78.5c-8.4 0-16 1.8-22.6 5.6-28.1 16.2-34.4 66.7-19.9 130.1-62.2 19.2-102.7 49.9-102.7 82.3 0 32.5 40.7 63.3 103.1 82.4-14.4 63.6-8 114.2 20.2 130.4 6.5 3.8 14.1 5.6 22.5 5.6 27.5 0 63.5-19.6 99.9-53.6 36.4 33.8 72.4 53.2 99.9 53.2 8.4 0 16-1.8 22.6-5.6 28.1-16.2 34.4-66.7 19.9-130.1 62-19.1 102.5-49.9 102.5-82.3zm-130.2-66.7c-3.7 12.9-8.3 26.2-13.5 39.5-4.1-8-8.4-16-13.1-24-4.6-8-9.5-15.8-14.4-23.4 14.2 2.1 27.9 4.7 41 7.9zm-45.8 106.5c-7.8 13.5-15.8 26.3-24.1 38.2-14.9 1.3-30 2-45.2 2-15.1 0-30.2-.7-45-1.9-8.3-11.9-16.4-24.6-24.2-38-7.6-13.1-14.5-26.4-20.8-39.8 6.2-13.4 13.2-26.8 20.7-39.9 7.8-13.5 15.8-26.3 24.1-38.2 14.9-1.3 30-2 45.2-2 15.1 0 30.2.7 45 1.9 8.3 11.9 16.4 24.6 24.2 38 7.6 13.1 14.5 26.4 20.8 39.8-6.3 13.4-13.2 26.8-20.7 39.9zm32.3-13c5.4 13.4 10 26.8 13.8 39.8-13.1 3.2-26.9 5.9-41.2 8 4.9-7.7 9.8-15.6 14.4-23.7 4.6-8 8.9-16.1 13-24.1zM421.2 430c-9.3-9.6-18.6-20.3-27.8-32 9 .4 18.2.7 27.5.7 9.4 0 18.7-.2 27.8-.7-9 11.7-18.3 22.4-27.5 32zm-74.4-58.9c-14.2-2.1-27.9-4.7-41-7.9 3.7-12.9 8.3-26.2 13.5-39.5 4.1 8 8.4 16 13.1 24 4.7 8 9.5 15.8 14.4 23.4zM420.7 163c9.3 9.6 18.6 20.3 27.8 32-9-.4-18.2-.7-27.5-.7-9.4 0-18.7.2-27.8.7 9-11.7 18.3-22.4 27.5-32zm-74 58.9c-4.9 7.7-9.8 15.6-14.4 23.7-4.6 8-8.9 16-13 24-5.4-13.4-10-26.8-13.8-39.8 13.1-3.1 26.9-5.8 41.2-7.9zm-90.5 125.2c-35.4-15.1-58.3-34.9-58.3-50.6 0-15.7 22.9-35.6 58.3-50.6 8.6-3.7 18-7 27.7-10.1 5.7 19.6 13.2 40 22.5 60.9-9.2 20.8-16.6 41.1-22.2 60.6-9.9-3.1-19.3-6.5-28-10.2zM310 490c-13.6-7.8-19.5-37.5-14.9-75.7 1.1-9.4 2.9-19.3 5.1-29.4 19.6 4.8 41 8.5 63.5 10.9 13.5 18.5 27.5 35.3 41.6 50-32.6 30.3-63.2 46.9-84 46.9-4.5-.1-8.3-1-11.3-2.7zm237.2-76.2c4.7 38.2-1.1 67.9-14.6 75.8-3 1.8-6.9 2.6-11.5 2.6-20.7 0-51.4-16.5-84-46.6 14-14.7 28-31.4 41.3-49.9 22.6-2.4 44-6.1 63.6-11 2.3 10.1 4.1 19.8 5.2 29.1zm38.5-66.7c-8.6 3.7-18 7-27.7 10.1-5.7-19.6-13.2-40-22.5-60.9 9.2-20.8 16.6-41.1 22.2-60.6 9.9 3.1 19.3 6.5 28.1 10.2 35.4 15.1 58.3 34.9 58.3 50.6-.1 15.7-23 35.6-58.4 50.6zM320.8 78.4z\\"/\\u003e\\u003ccircle cx\\u003d\\"420.9\\" cy\\u003d\\"296.5\\" r\\u003d\\"45.7\\"/\\u003e\\u003cpath d\\u003d\\"M520.5 78.1z\\"/\\u003e\\u003c/g\\u003e\\u003c/svg\\u003e","lineType":"ADDITION"},{"diffLine":"","lineType":"UNCHANGED"}]},{"name":"src/react-app-env.d.ts","extension":"d.ts","isIgnored":false,"fileScore":{"totalScore":176.0,"scoreAdditions":176,"scoreDeletions":0,"scoreBlankAdditions":26,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":176,"numDeletions":0,"numBlankAdditions":26,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/react-app-env.d.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/react-app-env.d.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1 @@","lineType":"HEADER"},{"diffLine":"+/// \\u003creference types\\u003d\\"react-scripts\\" /\\u003e","lineType":"ADDITION"}]},{"name":"src/reportWebVitals.ts","extension":"ts","isIgnored":false,"fileScore":{"totalScore":189.0,"scoreAdditions":189,"scoreDeletions":0,"scoreBlankAdditions":28,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":189,"numDeletions":0,"numBlankAdditions":28,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/reportWebVitals.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/reportWebVitals.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,15 @@","lineType":"HEADER"},{"diffLine":"+import { ReportHandler } from \\u0027web-vitals\\u0027;","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+const reportWebVitals \\u003d (onPerfEntry?: ReportHandler) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+  if (onPerfEntry \\u0026\\u0026 onPerfEntry instanceof Function) {","lineType":"ADDITION"},{"diffLine":"+    import(\\u0027web-vitals\\u0027).then(({ getCLS, getFID, getFCP, getLCP, getTTFB }) \\u003d\\u003e {","lineType":"ADDITION"},{"diffLine":"+      getCLS(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getFID(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getFCP(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getLCP(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+      getTTFB(onPerfEntry);","lineType":"ADDITION"},{"diffLine":"+    });","lineType":"ADDITION"},{"diffLine":"+  }","lineType":"ADDITION"},{"diffLine":"+};","lineType":"ADDITION"},{"diffLine":"+","lineType":"ADDITION_BLANK"},{"diffLine":"+export default reportWebVitals;","lineType":"ADDITION"}]},{"name":"src/setupTests.ts","extension":"ts","isIgnored":false,"fileScore":{"totalScore":194.0,"scoreAdditions":194,"scoreDeletions":0,"scoreBlankAdditions":28,"scoreSyntaxChanges":0,"scoreSpacingChanges":0},"linesOfCodeChanges":{"numAdditions":194,"numDeletions":0,"numBlankAdditions":28,"numSyntaxChanges":0,"numSpacingChanges":0},"fileDiffs":[{"diffLine":"diff --git a/dev/null b/src/setupTests.ts","lineType":"HEADER"},{"diffLine":"--- a/dev/null","lineType":"HEADER"},{"diffLine":"+++ b/src/setupTests.ts","lineType":"HEADER"},{"diffLine":"@@ -0,0 +1,5 @@","lineType":"HEADER"},{"diffLine":"+// jest-dom adds custom jest matchers for asserting on DOM nodes.","lineType":"ADDITION"},{"diffLine":"+// allows you to do things like:","lineType":"ADDITION"},{"diffLine":"+// expect(element).toHaveTextContent(/react/i)","lineType":"ADDITION"},{"diffLine":"+// learn more: https://github.com/testing-library/jest-dom","lineType":"ADDITION"},{"diffLine":"+import \\u0027@testing-library/jest-dom\\u0027;","lineType":"ADDITION"}]}]}]`

const fakeMembers = `[{"id":14,"displayName":"Dummy User","username":"dummyUsername","role":"MAINTAINER","webUrl":"http://gitlab.example.com/dummyUsername","committerEmails":[],"mergeRequestDocIds":[],"notes":[{"id":1625,"content":"\\n\\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam consectetur, dolor at suscipit dapibus, est libero pretium lectus, quis dictum felis felis ut justo. Vivamus efficitur lectus fringilla feugiat aliquet. Mauris commodo varius pharetra. Mauris vehicula feugiat est nec tempus. Nullam commodo dignissim dui ut interdum. Vestibulum congue elit ac augue varius, at convallis magna euismod. Sed bibendum sodales volutpat. Sed tempus ligula ut tortor rhoncus, quis auctor turpis congue. Praesent et volutpat tortor. Etiam id facilisis lectus. Aliquam erat volutpat. Mauris odio turpis, tincidunt ut convallis non, gravida et nisi. Morbi iaculis vestibulum rutrum. Donec porta tellus ut venenatis tincidunt. In placerat commodo dolor, quis posuere est.\\n\\nQuisque ultrices rutrum lectus, condimentum finibus sapien consectetur vitae. Nam a suscipit sapien. Nam ut metus id velit fermentum finibus eget sit amet lacus. Proin odio mi, malesuada id convallis vitae, iaculis at sapien. Sed lectus odio, varius in mauris at, luctus luctus erat. Aenean iaculis ex a tincidunt sagittis. Sed ullamcorper, risus eget congue blandit, nisl nisi tempor mauris, ac molestie risus ipsum eget mi. Etiam ultricies, dui sed faucibus ullamcorper, nisl est mollis lorem, a interdum turpis turpis et leo.","wordCount":191,"date":1617259004221,"context":"Issue","webUrl":"http://gitlab.example.com/djso/test-project/-/issues/6","parentAuthor":"Self"},{"id":1624,"content":"Lorem ipsum dolor sit amet, vis ne ignota habemus. Eu quo doctus scribentur, ea dicat sanctus principes vim. Dolor graecis quaestio ius ei, oratio quaestio has id. Quo ad fastidii indoctum intellegebat.\\n\\nEam an animal meliore habemus, cu nam nobis mentitum. Ut his choro epicurei abhorreant, vix cu cibo dicat, id mea aeterno inimicus. Nec quem causae cotidieque in. Cu brute necessitatibus duo, an ignota probatus pro. Magna laoreet et quo, te eum probo option, nam ad tollit legere nostro. Cu vix esse consetetur.","wordCount":84,"date":1617258897659,"context":"Issue","webUrl":"http://gitlab.example.com/djso/test-project/-/issues/6","parentAuthor":"Self"},{"id":1619,"content":"comment 2","wordCount":2,"date":1616402602084,"context":"Issue","webUrl":"http://gitlab.example.com/djso/test-project/-/issues/6","parentAuthor":"Self"},{"id":1618,"content":"comment 1","wordCount":2,"date":1616402597048,"context":"Issue","webUrl":"http://gitlab.example.com/djso/test-project/-/issues/6","parentAuthor":"Self"}]}]`
// files
//  commit
//    fileScore, commitScore (maybe ignore commit if new score 0), mrSumOfCommits
//
//  mr
//    fileScore, mrScore,
//

// ignores to mr trickle down commits and files
// un-ignore mr should trickle down to commits and files

// ignores to commit trickle down to files
// un-ignore commit should trickle up to mr and down to files

// un-ignore file should trickle up to commit and mr

const ignoreFile = (
  state: IProjectState,
  mrId: number,
  fileId: string,
  setIgnored: boolean,
  commitId?: string
) => {
  const mr = state.mergeRequests[mrId]
  const commit = !!commitId && mr.commits[commitId]
  const file = commit ? commit.files[fileId] : mr.files[fileId]
  const member = state.members[commit ? commit.userId : mr.userId]
  if (!mr || !file || !member) return state

  const scoreDelta = addOrSub(!setIgnored) * file.score

  if (!setIgnored) {
    mr.isIgnored = false
  }

  file.isIgnored = !setIgnored

  if (commit) {
    commit.isIgnored = commit.isIgnored && setIgnored
    commit.score += scoreDelta
    mr.sumOfCommitsScore[member.id] += scoreDelta
  } else {
    mr.score += scoreDelta
  }

  return { ...state }
}

const ignoreCommit = (
  state: IProjectState,
  mrId: number,
  commitId: string,
  setIgnored: boolean
) => {
  const commit = state.mergeRequests[mrId]?.commits[commitId]
  if (!commit) return state

  Object.keys(commit.files).forEach(fileId => {
    state = ignoreFile(state, mrId, fileId, setIgnored, commitId)
  })

  commit.isIgnored = setIgnored

  return state
}

const ignoreMr = (state: IProjectState, mrId: number, setIgnored: boolean) => {
  const mr = state.mergeRequests[mrId]
  if (!mr) return state

  Object.keys(mr.commits).forEach(commitId => {
    state = ignoreCommit(state, mrId, commitId, setIgnored)
  })

  Object.keys(mr.files).forEach(fileId => {
    state = ignoreFile(state, mrId, fileId, setIgnored)
  })

  mr.isIgnored = setIgnored

  return state
}

// const dateZero = new Date(0)

let currentProject: number
let currentConfig: IUserConfig

const getFileLoc = (file: IFileData): ILoc => {
  return {
    additions: file.linesOfCodeChanges.numAdditions,
    deletions: file.linesOfCodeChanges.numDeletions,
    comments: file.linesOfCodeChanges.numBlankAdditions,
    syntaxes: file.linesOfCodeChanges.numSyntaxChanges,
    whitespaces: file.linesOfCodeChanges.numSpacingChanges,
  }
}

const formatMergeRequests = (
  mergeRequests: TMergeData,
  config: IUserConfig
) => {
  const formattedMrs: TMergeRequests = {}

  mergeRequests.forEach(mr => {
    const commits: TCommits = {}
    const mrCommitsScore: ISumOfCommitsScore = {}

    mr.commits.forEach(commit => {
      const files: TFiles = {}
      let commitScore = 0

      commit.files.forEach(file => {
        const loc: ILoc = getFileLoc(file)

        const scores = calcScores(loc, config.generalScores)
        const score = calcScore(scores)

        if (!file.isIgnored) commitScore += score

        files[file.fileId] = {
          ...file,
          loc,
          scores,
          score,
        }
      })

      if (!commit.isIgnored) mrCommitsScore[commit.userId] += commitScore

      commits[commit.id] = {
        ...commit,
        files,
        score: commitScore,
        mrId: mr.mergeRequestId,
      }
    })

    const mrFiles: TFiles = {}
    let mrScore = 0
    let numDeletions = 0
    let numAdditions = 0

    mr.files.forEach(file => {
      const loc: ILoc = getFileLoc(file)

      const scores = calcScores(loc, config.generalScores)
      const score = calcScore(scores)

      const {
        numAdditions: additions,
        numBlankAdditions: blanks,
        numDeletions: deletions,
        numSpacingChanges: spaces,
        numSyntaxChanges: syntaxes,
      } = file.linesOfCodeChanges

      numDeletions += deletions
      numAdditions += additions + blanks + spaces + syntaxes

      if (!file.isIgnored) mrScore += score

      mrFiles[file.fileId] = {
        ...file,
        loc,
        scores,
        score,
      }
    })

    formattedMrs[mr.mergeRequestId] = {
      ...mr,
      commits,
      sumOfCommitsScore: mrCommitsScore,
      numAdditions,
      numDeletions,
      files: mrFiles,
      score: mrScore,
    }
  })

  return formattedMrs
}

const formatMembers = (members: TMemberData, mrs: TMergeRequests) => {
  const formattedMembers: TMembers = {}

  members.forEach(member => {
    let wordCount = 0
    member.notes.forEach(note => {
      wordCount += note.wordCount
    })

    formattedMembers[member.id] = {
      ...member,
      soloMrScore: 0,
      sharedMrScore: 0,
      commitScore: 0,
      numAdditions: 0,
      numDeletions: 0,
      numCommits: 0,
      wordCount,
      numComments: member.notes.length,
      mergeRequests: {},
    }
  })

  Object.values(mrs).forEach(mr => {
    const member = formattedMembers[mr.userId]
    if (!member) return

    if (!mr.isIgnored) {
      if (mr.isSolo) {
        member.soloMrScore += mr.score
      } else {
        member.sharedMrScore += mr.sumOfCommitsScore[member.id]
      }

      Object.values(mr.commits).forEach(commit => {
        if (!commit.isIgnored) {
          member.commitScore += commit.score
          member.numCommits++
        }
      })

      member.numAdditions += mr.numAdditions
      member.numDeletions += mr.numDeletions
    }

    member.mergeRequests[mr.mergeRequestId] = mr
  })

  return formattedMembers
}

const reducer: TProjectReducer = async (state, action) => {
  if (action.type === GET_PROJECT) {
    const { projectId, config } = action

    if (
      projectId === currentProject &&
      objectEquals(currentConfig, config) &&
      state
    )
      return state

    currentProject = projectId
    currentConfig = config

    let mergeRequests
    let members

    // eslint-disable-next-line
    mergeRequests = JSON.parse(fakeMrs)
    // eslint-disable-next-line
    members = JSON.parse(fakeMembers)

    // try {
    //   // mergeRequests = await jsonFetcher<TMergeData>(
    //   //   `/api/project/${projectId}/mergerequests`
    //   // )
    //   // members = await jsonFetcher<TMemberData>(
    //   //   `/api/project/${projectId}/members`
    //   // )
    // } catch {
    //   // return state
    // }

    const formattedMrs = formatMergeRequests(mergeRequests, config)
    const formattedMembers = formatMembers(members, formattedMrs)

    return {
      mergeRequests: formattedMrs,
      members: formattedMembers,
      id: projectId,
    }
  }

  if (!state) return state

  switch (action.type) {
    case UPDATE_START_TIME: {
      return state
    }
    case UPDATE_END_TIME: {
      return state
    }
    case IGNORE_MR: {
      const { mrId, setIgnored } = action
      return ignoreMr(state, mrId, setIgnored)
    }
    case IGNORE_MR_FILE: {
      const { mrId, fileId, setIgnored } = action
      return ignoreFile(state, mrId, fileId, setIgnored)
    }
    case IGNORE_COMMIT: {
      const { mrId, commitId, setIgnored } = action
      return ignoreCommit(state, mrId, commitId, setIgnored)
    }
    case IGNORE_COMMIT_FILE: {
      const { mrId, commitId, setIgnored, fileId } = action
      return ignoreFile(state, mrId, fileId, setIgnored, commitId)
    }
    case UPDATE_GENERAL_WEIGHT: {
      const { category, newWeight } = action

      Object.values(state.mergeRequests).forEach(mr => {
        mr.score = 0
        Object.values(mr.files).forEach(file => {
          file.scores[category] = newWeight * file.loc[category]
          const newScore = calcScore(file.scores)
          file.score = newScore
          mr.score += newScore
        }, 0)
        Object.values(mr.commits).forEach(commit => {
          mr.sumOfCommitsScore[commit.userId] = 0
          commit.score = 0
          Object.values(commit.files).forEach(file => {
            file.scores[category] = newWeight * file.loc[category]
            const newScore = calcScore(file.scores)
            file.score = newScore
            mr.sumOfCommitsScore[commit.userId] += newScore
            commit.score += newScore
          }, 0)
        })
      })
      return { ...state }
    }
    case UPDATE_EXTENSION:
    default: {
      return state
    }
  }
}

export default reducer
