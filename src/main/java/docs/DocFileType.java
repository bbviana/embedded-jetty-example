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

/**
 * @author bbviana
 */
enum DocFileType {
    HTML("html"),

    MARKDOWN("md");

    String fileExtension;

    DocFileType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static DocFileType getType(String fileName) {
        String fileExtension = extractFileExtension(fileName);
        if (HTML.fileExtension.equals(fileExtension)) {
            return HTML;
        }

        if (MARKDOWN.fileExtension.equals(fileExtension)) {
            return MARKDOWN;
        }

        throw new IllegalArgumentException("Não foi possível identificar o tipo do arquivo " + fileName);
    }

    private static String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("Nome do arquivo não contem entensão: " + fileName);
        }
        return fileName.substring(lastDotIndex + 1);
    }
}
