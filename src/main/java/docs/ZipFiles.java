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

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.common.io.Closeables.closeQuietly;

/**
 * Utilitário para manipular arquivos zip.
 *
 * @author bbviana
 */
class ZipFiles {

    static File saveURLToFile(URL url, String fileName, File targetFolder) {
        buildFolder(targetFolder);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(url.openStream(), 1024);

            File zip = new File(targetFolder, fileName);
            out = new BufferedOutputStream(new FileOutputStream(zip));

            copyInputStream(in, out);

            return zip;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    static void unpack(File fileToUnpack, File targetFolder) {
        if (!fileToUnpack.exists()) {
            throw new RuntimeException(fileToUnpack.getAbsolutePath() + " não existe");
        }

        buildFolder(targetFolder);

        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(fileToUnpack);

            for (Enumeration entries = zipFile.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File file = new File(targetFolder, File.separator + entry.getName());

                buildFolder(file.getParentFile());

                if (entry.isDirectory()) {
                    buildFolder(file);
                } else {
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                    copyInputStream(zipFile.getInputStream(entry), out);
                    closeQuietly(out);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeZipQuietly(zipFile);
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out) {
        // não é necessário fechar os streams, pois eles são fechados pelo método que invocou
        byte[] buffer = new byte[1024];

        try {
            int len = in.read(buffer);
            while (len >= 0) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void buildFolder(File folder) {
        boolean folderWasCreated = folder.exists() || folder.mkdirs();
        if (!folderWasCreated) {
            throw new RuntimeException("Não foi possível criar o diretório: " + folder);
        }
    }

    private static void closeZipQuietly(ZipFile zipFile) {
        try {
            assert zipFile != null;
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
