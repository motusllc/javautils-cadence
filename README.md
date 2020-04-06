# javautils-cadence
Helper library to use with Java Cadence client from your friends at Motus, LLC.

## What is this?
Contains an abstract class (`WorkerStarterSuper`) that can be extended to get the following functionality:
1. Automatic Cadence domain registration on startup
2. Worker factory that will register passed in Cadence Workflow types
3. Worker factory that will register passed in Cadence Activity objects
4. Heartbeat job that will run to ensure that a connection to Cadence exists, and if not, restart the Worker factory.
