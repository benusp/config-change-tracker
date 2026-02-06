### API tests
Simple API tests are provided as `.http` files in the `api-tests` folder.
They can be executed using IntelliJ IDEA HTTP Client or VS Code REST Client.

Tests demonstrate:
- creating a configuration change
- storing the returned change ID as a variable
- list all config changes with option to filter by rule or change type
- list all config changes with option to filter by timestamp
- health endpoint
- metrics endpoint
