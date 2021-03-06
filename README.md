# [GitLab Analyzer](http://cmpt373-1211-09.cmpt.sfu.ca/)

> Analyze GitLab projects like never before.

GitLab Analyzer is an analysis tool to analyze user contributions to a GitLab project. This piece of software is especially useful for instructors and teaching assistants who want to grade students' contribution to their team projects using various metrics, such as - code contribution through commits and merge requests, GitLab issues and code review comments, and so on.

- Homepage: http://cmpt373-1211-09.cmpt.sfu.ca/
- Source: https://csil-git1.cs.surrey.sfu.ca/373-2021-1-Orcus/gitlabanalyzer

## Run instructions

1. Go to http://cmpt373-1211-09.cmpt.sfu.ca/.
2. You will be presented with a login page, where you can either login using a GitLab username/password or a Personal Access Token (PAT). Right now, our application is targeted to our self-managed instance of a GitLab server running on our team VM so you will only be able to login with a GitLab account registered on our server. Here are test credentials you can use for now:
   - Personal Access Token: `w2ZgdZrWwBYjKNppFnFL`
3. On successful authentication, you will see the homepage with a list of GitLab projects you have access to.
4. Click on `Analyze` next to any project to view project details.
5. Once on the project page, you will be presented with two tabs:
   - `Summary` tab contains a summary of some interesting project statistics and a graph of the number of commits and merge requests made in the last week (customizable timeline coming later!).
   - `Members` tab displays all the project members.
6. In upcoming iterations, you will be able to click on `Analyze {memberFirstName}` next to each member to analyze their contributions to the project selected.

## Directory structure

```bash
.
├── WikiDocs
│   ├── Contribution-Guide.md
│   ├── Dev-Environment-Setup-Guide.md
│   └── ...
├── api-spec            # API specifications
├── src
│   ├── client          # Client source files
│   └── server          # Server source files
├── .gitlab-ci.yml
├── LICENSE
└── README.md

```

## Build directions

Dev environment setup guide and build directions can be found [here](Wikidocs/Dev-Environment-Setup-Guide.md).

## Dependencies

### Front-end dependencies

- React: (`17.0.1`) - https://reactjs.org/
- Lodash: (`4.17.20`) - https://lodash.com/
- ReactDOM: (`17.0.1`) - https://www.npmjs.com/package/react-dom
- React Router: (`5.2.0`) - https://reactrouter.com/web/guides/quick-start
- React Scripts: (`4.0.1`) - https://www.npmjs.com/package/react-scripts
- Recharts: (`2.0.7`) - https://recharts.org/en-US/
- Typescript: (`4.1.3`) - https://www.typescriptlang.org/
- MaterialUI
   - Core : (`5.0.0-alpha.27`) - https://www.npmjs.com/package/@material-ui/core
   - Lab: (`5.0.0-alpha.27`) - https://www.npmjs.com/package/@material-ui/lab
   - Pickers: (`3.3.10`) - https://www.npmjs.com/package/@material-ui/pickers
- date-fns (`2.19.0`) - https://www.npmjs.com/package/date-fns
- Emotion (`11.1.5`) - https://www.npmjs.com/package/emotion
- parse-diff (`0.8.1`) - https://www.npmjs.com/package/parse-diff

### Back-end dependencies

- Spring Boot (`2.4.2`) - https://spring.io/projects/spring-boot
- GitLab4J™ API (`4.15.7`) - https://github.com/gitlab4j/gitlab4j-api
- Google GSON (`2.8.6`) - https://github.com/google/gson
- Java JWT: JSON Web Token for Java and Android (`0.11.2`) - https://github.com/jwtk/jjwt
- Apache Commons Lang (`3.11`) - https://commons.apache.org/proper/commons-lang/

### Testing dependencies

- Mockito (Core) (`3.8.0`) - (https://github.com/mockito/mockito)
- Mockito (Inline) (`3.8.0`) - (https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#mockito-inline)
