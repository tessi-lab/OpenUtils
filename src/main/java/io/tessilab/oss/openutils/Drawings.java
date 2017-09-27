package io.tessilab.oss.openutils;

import io.tessilab.oss.openutils.hocr.HocrContentArea;
import io.tessilab.oss.openutils.hocr.HocrLine;
import io.tessilab.oss.openutils.hocr.HocrPage;
import io.tessilab.oss.openutils.hocr.HocrParagraph;
import io.tessilab.oss.openutils.hocr.HocrSection;
import io.tessilab.oss.openutils.hocr.HocrXWord;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Drawings {

    private static final Logger LOGGER = LogManager.getLogger(Drawings.class);

    private Drawings() {
        // private dummy constructor
    }

    /* OPEN AND WRITE */

    /**
     * @param imagePathFile
     * @return A buffered image containing the image of the file path. null if
     *         there was an error during the opening.
     */
    public static BufferedImage openImage(String imagePathFile) {
        File file = new File(imagePathFile);
        try {
            BufferedImage img = ImageIO.read(file);

            int w = img.getWidth(null);
            int h = img.getHeight(null);
            BufferedImage buffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            Graphics g = buffImg.getGraphics();
            g.drawImage(img, 0, 0, null);

            return buffImg;
        } catch (Exception e) {
            LOGGER.debug("Expection while opening an image", e);
            return null;
        }
    }

    public static void writeImage(String destPath, BufferedImage buffImg) {
        // Write output file
        File outputfile = new File(destPath);
        try {
            ImageIO.write(buffImg, "png", outputfile);
            LOGGER.trace("{} created...", outputfile);
        } catch (IOException ioe) {
            LOGGER.trace("Expection while opening an image", ioe);
        }
    }

    /* BASIC METHODS */

    public static BufferedImage drawRectangle(BufferedImage buffImg, Rectangle rect, Color color, int sizeStroke, boolean filled) {
        Graphics2D g2D = buffImg.createGraphics();

        // draw page boundingbox
        g2D.setStroke(new BasicStroke(sizeStroke));
        g2D.setColor(color);
        if (filled)
            g2D.fillRect(rect.x, rect.y, rect.width, rect.height);
        else
            g2D.drawRect(rect.x, rect.y, rect.width, rect.height);
        return buffImg;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    /* MORE ADVANCED DRAWING METHODS */

    public static BufferedImage drawAllPageBBoxes(BufferedImage buffImg, HocrPage page, boolean fillRect) {
        // Draw the page boundingbox
        Drawings.drawRectangle(buffImg, page.getBBox(), Color.gray, 10, fillRect);

        // Draw boundingboxes recursively
        Iterator<HocrSection> hcaIte = page.getChilds().iterator();
        while (hcaIte.hasNext()) {
            HocrContentArea hca = (HocrContentArea) hcaIte.next();
            Drawings.drawRectangle(buffImg, hca.getBBox(), Color.green, 7, fillRect);

            Iterator<HocrSection> hpIte = hca.getChilds().iterator();
            while (hpIte.hasNext()) {
                HocrParagraph hp = (HocrParagraph) hpIte.next();
                Drawings.drawRectangle(buffImg, hp.getBBox(), Color.yellow, 5, fillRect);

                Iterator<HocrSection> hlIte = hp.getChilds().iterator();
                while (hlIte.hasNext()) {
                    HocrLine hl = (HocrLine) hlIte.next();
                    Drawings.drawRectangle(buffImg, hl.getBBox(), Color.orange, 3, fillRect);

                    Iterator<HocrSection> hxwIte = hl.getChilds().iterator();
                    while (hxwIte.hasNext()) {
                        HocrXWord hxw = (HocrXWord) hxwIte.next();
                        Drawings.drawRectangle(buffImg, hxw.getBBox(), new Color(1, 0, 0, 0.75f), 5, true);
                        Drawings.drawRectangle(buffImg, hxw.getBBox(), Color.black, 5, false);
                    }
                }
            }
        }
        return buffImg;
    }

    public static void drawAllWordsBBoxes(Stream<HocrXWord> words, BufferedImage buffImg, Color color) {
        Graphics2D g2D = buffImg.createGraphics();
        g2D.setFont(new Font("courier", 1, 30));
        words.forEach(hxw -> {
            Drawings.drawRectangle(buffImg, hxw.getBBox(), color, 6, true);
            Drawings.drawRectangle(buffImg, hxw.getBBox(), Color.green.darker(), 6, false);
            g2D.setColor(Color.BLUE);
            g2D.drawString(hxw.getValue(), (int) hxw.getX1(), (int) hxw.getY1());
        });
    }

    public static void drawAllWordsBBoxes(Stream<HocrXWord> words, BufferedImage buffImg) {
        drawAllWordsBBoxes(words, buffImg, new Color(1, 0, 0, 0.5f));
    }

    public static void drawAllAssociations(Rectangle word, Stream<HocrXWord> stream, BufferedImage buffImg, Color color) {
        Graphics2D g2D = buffImg.createGraphics();
        g2D.setStroke(new BasicStroke(6f));
        g2D.setColor(color);
        stream.forEach(hxw -> g2D.drawLine((int) word.getCenterX(), (int) word.getCenterY(), (int) hxw.getCenterX(), (int) hxw.getCenterY()));
    }
}
