package org.ismskism.springshell.service;


import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class CompressionService {

  private static final int BUFFER_SIZE = 4096;

  public void zipTarget(String sourcePath, String zipPath) {
    try {
      FileOutputStream fos = new FileOutputStream(zipPath);
      ZipOutputStream zos = new ZipOutputStream(fos);
      File file = new File(sourcePath);
      zipFileOrDirectory(file, file.getName(), zos);
      zos.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void unzipFile(String zipPath, String destinationPath) {
    try {
      File destDir = new File(destinationPath);
      byte[] buffer = new byte[BUFFER_SIZE];
      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath));
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        File newFile = newFile(destDir, zipEntry);
        if (zipEntry.isDirectory()) {
          if (!newFile.isDirectory() && !newFile.mkdirs()) {
            throw new IOException("Failed to create directory: " + newFile);
          }
        } else {
          File parent = newFile.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent);
          }
          FileOutputStream fos = new FileOutputStream(newFile);
          int length;
          while ((length = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
          }
          fos.close();
        }
        zis.closeEntry();
        zipEntry = zis.getNextEntry();
      }
      zis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void zipFileOrDirectory(File file, String entryName, ZipOutputStream zos)
      throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File f : files) {
          zipFileOrDirectory(f, entryName + File.separator + f.getName(), zos);
        }
      }
    } else {
      zipFile(file, entryName, zos);
    }
  }

  private void zipFile(File file, String entryName, ZipOutputStream zos)
      throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    FileInputStream fis = new FileInputStream(file);
    zos.putNextEntry(new ZipEntry(entryName));
    int length;
    while ((length = fis.read(buffer)) > 0) {
      zos.write(buffer, 0, length);
    }
    zos.closeEntry();
    fis.close();
  }

  private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());
    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();
    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target directory: " + zipEntry.getName());
    }
    return destFile;
  }

}
