package org.example.jerseyguice;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.google.inject.Injector;

/**
 * https://github.com/jersey/jersey/blob/master/core-server/src/main/java/org/glassfish/jersey/server/spi/ComponentProvider.java
 *
 * Jersey ComponentProvider designed to bootstrap a Guice Injector into the Jersey HK2 InjectionManager.
 * Assumes this class or an extending class has been defined in a META-INF/services file
 *
 */
public class JerseyGuiceComponentProvider implements ComponentProvider {
    static final Logger LOGGER = Logger.getLogger(JerseyGuiceComponentProvider.class.toString());

    static final String PROVIDER_RESOURCE_PATH = "/META-INF/services/org.glassfish.jersey.server.spi.ComponentProvider";

    private static Injector INJECTOR;
    private static Supplier<Injector> INJECTOR_SUPPLIER;

    public static void install(Injector injector) {
        validateComponentProviderMetaInfService();
        INJECTOR = injector;
    }

    public static void register(Supplier<Injector> injectorSupplier) {
        validateComponentProviderMetaInfService();
        INJECTOR_SUPPLIER = injectorSupplier;
    }

    @Override
    public void initialize(final InjectionManager injectionManager) {
        LOGGER.info("Being initialized!");

        final Injector theInjector = getInjector();
        if(theInjector == null) return;

        final ServiceLocator serviceLocator = injectionManager.getInstance(ServiceLocator.class);

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        final GuiceIntoHK2Bridge g2h = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        g2h.bridgeGuiceInjector(theInjector);

        LOGGER.info("GuiceBridge Initialized!");
    }

    @Override
    public boolean bind(final Class<?> component, final Set<Class<?>> providerContracts) {
        // bindings are delegated to Guice via bridge
        return false;
    }

    @Override
    public void done() {
        LOGGER.fine("Done!");
    }

    /**
     * If the static install and register() methods are not desirable, implement Guice Injector setup logic by extending this method,
     * and replace the class defined in the META-INF/services file
     */
    protected Injector getInjector() {
        final Injector theInjector;
        if(INJECTOR != null) {
            LOGGER.fine("Using installed Guice Injector.");
            theInjector = INJECTOR;
        }else if (INJECTOR_SUPPLIER != null) {
            LOGGER.fine("Using Guice Injector Supplier");
            theInjector = INJECTOR_SUPPLIER.get();
        } else {
            theInjector = null;
            LOGGER.warning("No Injector or Injector Supplier has been registered, Jersey-Guice Integration is not setup.");
        }
        return theInjector;
    }

    static void validateComponentProviderMetaInfService() {
        final URL provider = JerseyGuiceComponentProvider.class.getResource(PROVIDER_RESOURCE_PATH);
        if(provider == null) {
            throw new RuntimeException("No Jersey ComponentProvider registered, add file to classpath: " + PROVIDER_RESOURCE_PATH);
        }
        final String content;
        try {
            content = IOUtils.toString(provider, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new RuntimeException("Error reading contents of: " + PROVIDER_RESOURCE_PATH, e);
        }

        try {
            Class c = Class.forName(content);
            if(!ComponentProvider.class.isAssignableFrom(c)) {
                throw new RuntimeException("Class: " + c + " from: " + PROVIDER_RESOURCE_PATH + " does not implement ComponentProvider");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading Class: " + content, e);
        }
    }
}
