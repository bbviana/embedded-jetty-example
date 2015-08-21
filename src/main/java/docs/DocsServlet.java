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
package docs;

import docs.DocsManager.NexusRemoteServerFetcher;
import docs.DocsManager.RemoteServerFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.google.common.io.Closeables.closeQuietly;

/**
 * @author bbviana
 */
public class DocsServlet extends HttpServlet {

    private RemoteServerFetcher remoteServerFetcher = new NexusRemoteServerFetcher();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getParameter("file");
        String content;

        // cliente especificou um arquivo (utilizado em modo DEV)
        if (filePath != null) {
            content = loadFile(filePath);
        } else {
            DocInfo info = DocInfo.parse(req.getRequestURI());
            content = new DocsManager(remoteServerFetcher, info).load();
        }

        content = applyTemplate(content);
        cached(resp).getWriter().write(content);
    }

    private String applyTemplate(String content) throws IOException {
        InputStream templateIS = null;
        Document template = null;
        try {
            templateIS = this.getServletContext().getResourceAsStream("/twfc/docs/template.html");
            template = Jsoup.parse(templateIS, "UTF-8", "");
        } finally {
            closeQuietly(templateIS);
        }

        template.getElementById("content").html(content);
        return template.toString();
    }

    private String loadFile(String filePath) throws IOException {
        File file = new File(filePath);
        return DocsManager.convert(file, DocFileType.getType(filePath));
    }

    private static HttpServletResponse cached(HttpServletResponse response) {
        long oneYearInSeconds = 6 * 30 * 24 * 60 * 60;
        long nowMS = new Date().getTime();
        long expiry = nowMS + oneYearInSeconds * 1000;
        response.setDateHeader("Expires", expiry);
        response.setHeader("Cache-Control", "max-age=" + oneYearInSeconds);
        return response;
    }

}
