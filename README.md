# jersey-guice-integration
Example project demonstrating Guice integration with new Jersey versions

Using Jersey Docs and Examples:
https://github.com/jersey/jersey/tree/master/examples

Jersey has built-in support for Guice in most recent versions, via the GuiceBridge and GuiceIntoHK2Bridge, as described here:  https://javaee.github.io/hk2/guice-bridge.html
The problem is that for projects that want to delegate DI to Guice completely, it's not obvious where to boot-strap the Jersey container to plug-in the Guice injector.
This project uses a Jersey ComponentProvider as the entry point to register the Guice Injector with the Jersey InjectionManager

Documentation for Jersey-Jetty integration and ComponentProvider: https://jersey.github.io/documentation/latest/deployment.html

References: 
* https://jersey.github.io/documentation/latest/ioc.html#d0e17202

Guice:
* Working with servlets: https://github.com/google/guice/wiki/Servlets
* And ServletModule: https://github.com/google/guice/wiki/ServletModule - not using the GuiceServletContextListener because can initialize 
  programmatically using Jetty 
