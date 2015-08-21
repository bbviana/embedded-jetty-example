/*
 * Copyright (c) 1999-2010 Touch Tecnologia e Informatica Ltda.
 *
 * R. Gomes de Carvalho, 1666, 3o. Andar, Vila Olimpia, Sao Paulo, SP, Brasil.
 *
 * Todos os direitos reservados.
 * Este software e confidencial e de propriedade da Touch Tecnologia e Informatica Ltda. (Informacao Confidencial)
 * As informacoes contidas neste arquivo nao podem ser publicadas, e seu uso esta limitado de acordo
 * com os termos do contrato de licenca.
 */

import docs.DocsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author bbviana
 */
public class EmbeddedJettyMain {

    static Server server;
    static ServletContextHandler context;

    public static void main(String[] args) throws Exception {
        server = new Server(7070);

        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new ExampleServlet()), "/example/*");
        context.addServlet(new ServletHolder(new ShutdownServlet()), "/shutdown/*");
        context.addServlet(new ServletHolder(new DocsServlet()), "/docs/*");

        server.start();
        server.join();
    }
}
