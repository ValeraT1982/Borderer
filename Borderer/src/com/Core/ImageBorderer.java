package com.Core;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageBorderer {
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
}
