# Setting up the build pipeline #

Romero works by downloading test classes from a remote repository, and setting up the test classpath using `URLClassLoader`s. For this to work, you'll need your acceptance tests published to a JAR accessible via HTTP, and any dependencies published to a repository available to the agent.


### Jenkins ###