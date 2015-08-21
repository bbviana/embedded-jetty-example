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

import com.google.common.io.Files;
import org.markdown4j.Markdown4jProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import static com.google.common.base.Charsets.UTF_8;
import static docs.DocFileType.HTML;
import static docs.DocFileType.MARKDOWN;
import static java.lang.String.format;
import static java.lang.System.getProperty;

/**
 * @author bbviana
 */
class DocsManager {

    private static final String LOCAL_REPOSITORY = getProperty("user.home") + "/.m2/repository/";

    private static final String ZIP_FILE_PREFIX = "docs-";

    private static final String ZIP_FILE_EXTENSION = "war";

    private String localRepository;

    private RemoteServerFetcher remoteServerFetcher;

    private DocInfo info;

    public DocsManager(RemoteServerFetcher remoteServerFetcher, DocInfo info) {
        this.remoteServerFetcher = remoteServerFetcher;
        this.localRepository = LOCAL_REPOSITORY;
        this.info = info;
    }

    public DocsManager(RemoteServerFetcher remoteServerFetcher, String localRepository, DocInfo info) {
        this.remoteServerFetcher = remoteServerFetcher;
        this.localRepository = localRepository;
        this.info = info;
    }

    public String load() throws FileNotFoundException {
        downloadOrUnpackIfNecessary();

        // procura o arquivo com a extensão passada pelo client
        File file = new File(explodedFolder() + info.getFileName());
        if (file.exists()) {
            return convert(file, info.fileType);
        }

        // se não achou, ou não foi passada uma extensão ou o arquivo com a extensão passada não existe,
        // mas pode existir outro arquivo com mesmo nome e outra extensão

        file = new File(explodedFolder() + info.getFileNameHTML());
        if (file.exists()) {
            return convert(file, HTML);
        }

        file = new File(explodedFolder() + info.getFileNameMD());
        if (file.exists()) {
            return convert(file, MARKDOWN);
        }

        throw new FileNotFoundException("Arquivo não encontrado: " + info.getFileName());
    }

    static String convert(File file, DocFileType fileType) {
        try {
            if (fileType == HTML) {
                return Files.toString(file, UTF_8);
            }

            if (fileType == MARKDOWN) {
                return new Markdown4jProcessor().process(file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new IllegalArgumentException("Formato de arquivo não reconhecido: " + fileType);
    }

    private void downloadOrUnpackIfNecessary() {
        File explodedFolder = new File(explodedFolder());
        File zipFile = zipFile();

        // não faz download se já foi descompactado
        if (!zipFile.exists() && !explodedFolder.exists()) {
            URL url = downloadFromRemoteServer();
            zipFile = ZipFiles.saveURLToFile(url, zipFileName(true), new File(downloadFolder()));
        }

        // se nao foi descompactado ainda (pode ter sido criado com mvn install)
        if (!explodedFolder.exists()) {
            unpackZipFile(zipFile, explodedFolder);
        }
    }

    private URL downloadFromRemoteServer() {
        // br/com/touchtec/twf/docs/1.0.0/docs-1.0.0.war
        String url = info.fullPath() + zipFileName(true);
        return remoteServerFetcher.fetch(url);
    }

    private void unpackZipFile(File zipFile, File targetFolder) {
        System.out.println(format("Descompactando arquivo %s no diretório %s", zipFile.getName(), targetFolder.getName()));
        ZipFiles.unpack(zipFile, targetFolder);
    }

    // {localRepository}/br/com/touchtec/twf/docs/1.0.0/docs-1.0.0.war
    private File zipFile() {
        return new File(downloadFolder() + zipFileName(true));
    }

    // docs-1.0.0 ou docs-1.0.0.war
    private String zipFileName(boolean includeExtension) {
        return ZIP_FILE_PREFIX + info.version + (includeExtension ? "." + ZIP_FILE_EXTENSION : "");
    }

    // {localRepository}/br/com/touchtec/twf/docs/1.0.0/
    private String downloadFolder() {
        return localRepository + info.fullPath();
    }

    // {localRepository}/br/com/touchtec/twf/docs/1.0.0/docs-1.0.0/
    private String explodedFolder() {
        return downloadFolder() + zipFileName(false) + "/";
    }

    ////////////////////////////////////////////////////////////////////////////////
    // CLASSES AUXILIARES
    ////////////////////////////////////////////////////////////////////////////////

    interface RemoteServerFetcher {
        URL fetch(String url);
    }

    static class NexusRemoteServerFetcher implements RemoteServerFetcher {
        private static final String NEXUS_URL = "http://nexus.touchtec.com.br/service/local/repositories/releases/content/";

        public URL fetch(String url) {
            String nexusURL = NEXUS_URL + url;
            login("bbviana", "bbviana");

            System.out.println("Fazendo download da documentação: " + nexusURL);

            try {
                return new URL(nexusURL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static void login(final String login, final String passowrd) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, passowrd.toCharArray());
                }
            });
        }
    }
}
