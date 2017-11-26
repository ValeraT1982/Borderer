package com.Core;

import javafx.util.Pair;
import org.apache.commons.imaging.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageBorderer {
    public static final String DENSITY_UNITS_PIXELS_PER_INCH = "01";

    public static BufferedImage border(BufferedImage img, int border){
        int width = img.getWidth();
        int height = img.getHeight();
        int max = Math.max(width, height);
        int outputSize = max + border * 2;
        BufferedImage outputImg = new BufferedImage(outputSize, outputSize, img.getType());
        Graphics2D graphics = outputImg.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, outputSize, outputSize);
        graphics.drawImage(img, border + (max - width) / 2,  border + (max - height) / 2, width,  height, null);

        return outputImg;
    }

    public static void save(BufferedImage img, File output, Dpi dpi) throws IOException {
        final String formatName = "jpeg";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata, dpi);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(img, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }

    public static Dpi getDPI(File file) throws IOException, ImageReadException {
        final org.apache.sanselan.ImageInfo imageInfo = Sanselan.getImageInfo(file);

        final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
        final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();

        return new Dpi(physicalWidthDpi, physicalHeightDpi);
    }

    private static void setDPI(IIOMetadata metadata, Dpi dpi) throws IIOInvalidTreeException {

        String metadataFormat = "javax_imageio_jpeg_image_1.0";
        IIOMetadataNode root = new IIOMetadataNode(metadataFormat);
        IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
        IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");

        IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
        app0JFIF.setAttribute("majorVersion", "1");
        app0JFIF.setAttribute("minorVersion", "2");
        app0JFIF.setAttribute("thumbWidth", "0");
        app0JFIF.setAttribute("thumbHeight", "0");
        app0JFIF.setAttribute("resUnits", DENSITY_UNITS_PIXELS_PER_INCH);
        app0JFIF.setAttribute("Xdensity", String.valueOf(dpi.getWidthDpi()));
        app0JFIF.setAttribute("Ydensity", String.valueOf(dpi.getHeightDpi()));

        root.appendChild(jpegVariety);
        root.appendChild(markerSequence);
        jpegVariety.appendChild(app0JFIF);

        metadata.mergeTree(metadataFormat, root);
    }
}