package dev.voidedaries.aries.client.render.gif;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GifInspector {

    public static GifAnimation inspect(InputStream inputStream) throws Exception {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

        if (!readers.hasNext()) {
            throw new IllegalStateException("No GIF reader found");
        }

        ImageReader reader = readers.next();

        List<GifAnimation.GifFrame> frames = new ArrayList<>();

        try {
            reader.setInput(ImageIO.createImageInputStream(inputStream));

            int frameCount = reader.getNumImages(true);

            BufferedImage canvas = new BufferedImage(
                reader.getWidth(0),
                reader.getHeight(0),
                BufferedImage.TYPE_INT_ARGB
            );

            for (int i = 0; i < frameCount; i++) {
                IIOMetadata metadata = reader.getImageMetadata(i);

                BufferedImage image = reader.read(i);

                Node root = metadata.getAsTree(metadata.getNativeMetadataFormatName());

                int imageLeft = 0;
                int imageTop = 0;
                int delay = 0;

                Node node = root.getFirstChild();

                while (node != null) {
                    if ("GraphicControlExtension".equals(node.getNodeName())) {
                        NamedNodeMap attributes = node.getAttributes();
                        delay = Integer.parseInt(attributes.getNamedItem("delayTime").getNodeValue());
                    }

                    if ("ImageDescriptor".equals(node.getNodeName())) {
                        NamedNodeMap attributes = node.getAttributes();

                        imageLeft = Integer.parseInt(attributes.getNamedItem("imageLeftPosition").getNodeValue());
                        imageTop = Integer.parseInt(attributes.getNamedItem("imageTopPosition").getNodeValue());
                    }

                    node = node.getNextSibling();
                }

                Graphics2D graphics = canvas.createGraphics();
                graphics.drawImage(image, imageLeft, imageTop, null);
                graphics.dispose();

                BufferedImage frame =
                    new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);

                Graphics2D frameGraphics = frame.createGraphics();
                frameGraphics.drawImage(canvas, 0, 0, null);
                frameGraphics.dispose();

                frames.add(new GifAnimation.GifFrame(frame, delay));

                Graphics2D clearGraphics = canvas.createGraphics();
                clearGraphics.setComposite(AlphaComposite.Clear);
                clearGraphics.fillRect(imageLeft, imageTop, image.getWidth(), image.getHeight());
                clearGraphics.dispose();
            }

            return new GifAnimation(frames);
        } finally {
            reader.dispose();
        }
    }

}
