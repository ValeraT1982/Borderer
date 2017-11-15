package com.Core;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class CopyMetadataHelper {
    public static void copyExifMetadata(final File sourceFile, final File destinationFile)
            throws IOException {
        OutputStream os = null;
        try {
            TiffOutputSet sourceOutputSet = getOutputSet(sourceFile);
            TiffOutputDirectory sourceOutputDirectory = sourceOutputSet.getOrCreateExifDirectory();

            TiffOutputSet destinationOutputSet = getOutputSet(destinationFile);
            TiffOutputDirectory destinationOutputDirectory = destinationOutputSet.getOrCreateExifDirectory();

            for (TiffOutputField field : sourceOutputDirectory.getFields()) {
                destinationOutputDirectory.add(field);
            }

            String tempFileName = destinationFile.getPath() + ".temp";
            File tempFile = new File(tempFileName);
            FileOutputStream fos = new FileOutputStream(new File(tempFileName));
            os = new BufferedOutputStream(fos);

            new ExifRewriter().updateExifMetadataLossless(destinationFile, os, destinationOutputSet);

            FileUtils.copyFile(tempFile, destinationFile);
            FileUtils.forceDelete(tempFile);
        } catch (Exception ex) {

        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    private static TiffOutputSet getOutputSet(File file) throws ImageWriteException, IOException, ImageReadException {
        TiffOutputSet outputSet = null;
        final IImageMetadata metadata = Imaging.getMetadata(file);
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (null != jpegMetadata) {
            final TiffImageMetadata exif = jpegMetadata.getExif();
            if (null != exif) {
                outputSet = exif.getOutputSet();
            }
        }

        if (outputSet == null) {
            outputSet = new TiffOutputSet();
        }

        return outputSet;
    }
}