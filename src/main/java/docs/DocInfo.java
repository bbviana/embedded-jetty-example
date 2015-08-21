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

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.removeStart;

/**
 * @author bbviana
 */
class DocInfo {
    static final String DEFAULT_GROUP_ID = "br/com/touchtec/";

    String artifact;
    String version;
    String file;
    DocFileType fileType;
    String fullPath;

    DocInfo(String artifact, String version, String file) {
        this.artifact = artifact;
        this.version = version;
        // remove extensão
        this.file = file.substring(0, file.lastIndexOf("."));
        this.fileType = DocFileType.getType(file);
        this.fullPath = DEFAULT_GROUP_ID + artifact + "/docs/" + version + "/";
    }

    /**
     * @param uri /docs/twf/1.0.0/index.html, /docs/twf/1.0.0/
     */
    static DocInfo parse(String uri) {
        String parsedUri = removeStart(uri, "/docs/");

        String artifact = nextPart(parsedUri);
        parsedUri = removeStart(parsedUri, artifact);
        parsedUri = removeStart(parsedUri, "/");

        String version = nextPart(parsedUri);
        parsedUri = removeStart(parsedUri, version);
        parsedUri = removeStart(parsedUri, "/");

        String file = parsedUri;
        file = "".equals(file) ? "index.html" : file;

        return new DocInfo(artifact, version, file);
    }

    private static String nextPart(String entirePart) {
        int slashIndex = entirePart.indexOf("/");
        if (slashIndex == -1) {
            return entirePart;
        }

        return entirePart.substring(0, slashIndex);
    }

    /**
     * @return file.html, file.md
     */
    String getFileName() {
        return file + "." + fileType.fileExtension;
    }

    /**
     * @return file.html
     */
    String getFileNameHTML() {
        return file + ".html";
    }

    /**
     * @return file.md
     */
    String getFileNameMD() {
        return file + ".md";
    }

    /**
     * @return br/com/touchtec/twf/docs/1.0.0/
     */
    String fullPath() {
        return fullPath;
    }

    @Override
    public String toString() {
        return format("artifact: %s, version: %s, file: %s", artifact, version, file);
    }
}
