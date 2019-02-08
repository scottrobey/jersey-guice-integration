package org.example.jerseyguice;

import java.net.URI;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.ServletScopes;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        log("Jetty Server Starting Up ...");

        log("Setting up Guice...");
        Injector injector = Guice.createInjector(new MyModule(), new MyServletModule());

        JerseyGuiceComponentProvider.install(injector);

        final URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
        final Server server = JettyHttpContainerFactory.createServer(baseUri, new Application(), false);

        final ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        root.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        server.start();

        log("Server started: " + baseUri.toURL());
        server.join();
    }

    static void log(String msg) {
        System.out.println(msg);
    }

    static void logError(String msg, Throwable t) {
        System.out.println(msg);
        t.printStackTrace();
    }

    // https://jersey.github.io/documentation/latest/deployment.html
    public static class Application extends ResourceConfig {
        public Application() {
            packages(getClass().getPackageName());

            // was unable to register the ComponentProvider this way, which would have been ideal, so used META-INF/services SPI instead
            //register(JerseyGuiceComponentProvider.class);
        }
    }

    public static class MyModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(MySingleton.class).in(Scopes.SINGLETON);
            bind(PerRequestService.class).in(ServletScopes.REQUEST);
        }
    }

    public static final class MyServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(ServletContainer.class).in(Singleton.class);
            serve("/*").with(new ServletContainer(new JettyServer.Application()));
        }
    }

}
