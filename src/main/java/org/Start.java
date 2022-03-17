package org;

import io.swagger.jaxrs.config.BeanConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;
import java.net.URI;

//import org.SwaggerConfig;

@SpringBootApplication
public class Start {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/project";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */


    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers


        final ResourceConfig rc = new ResourceConfig().packages("org/services");




        rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

       // rc.register(JerseyApiDeclarationProvider.class);
        //rc.register(JerseyResourceListingProvider.class);
        //rc.register(ApiListingResourceJSON.class);


        BeanConfig beanConfig = new BeanConfig();       // volver a descomentar

        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/project");
        beanConfig.setContact("support@example.com");
        beanConfig.setDescription("REST API for Proyect_personal");
        beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
        beanConfig.setResourcePackage("org/services");
        beanConfig.setTermsOfServiceUrl("http://www.example.com/resources/eula");
        beanConfig.setTitle("REST API");
        beanConfig.setVersion("1.0.0");
        beanConfig.setScan(true);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

    }


    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {


        final HttpServer server = startServer();

        StaticHttpHandler staticHttpHandler = new StaticHttpHandler("./public/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");



        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.out.println(String.format("Para ver informacion del swagger: http://localhost:8080/project/swagger.json"));


        System.in.read();
        //server.shutdownNow();
    }

}
