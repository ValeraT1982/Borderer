package com;

import com.Core.CopyMetadataHelper;
import com.Core.ImageBorderer;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException, ImageWriteException, ImageReadException {
        String current = new java.io.File( "." ).getCanonicalPath();
        int border = 10;
        final File folder = new File(current);
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                String extension = FilenameUtils.getExtension(fileEntry.getName());
                if (extension.toLowerCase().equals("jpg") || extension.toLowerCase().equals("jpeg")){
                    String outputPath = FilenameUtils.concat(current,"bordered\\" + fileEntry.getName());
                    BufferedImage img = ImageIO.read(fileEntry);
                    BufferedImage outputImg = ImageBorderer.border(img, border);
                    File outputFile = new File(outputPath);
                    if (!Files.exists(Paths.get(outputFile.getParent()))){
                        Files.createDirectory(Paths.get(outputFile.getParent()));
                    }

                    ImageIO.write(outputImg, extension, outputFile);
                    //CopyMetadataHelper.changeExifMetadata(fileEntry, outputFile);
                }
            }
        }
    }
}