package com.doubeye.commons.utils.photographic;

import com.doubeye.commons.utils.file.FileUtils;
//import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


/**
 * @author doubeye
 * @version 1.0, 0
 * 图像工具
 */
@SuppressWarnings("unused | WeakerAccess")
public class PhotographicUtils {
    /**
     * 获得一个图片的平均颜色
     *
     * @param bi 图像对象
     * @param x0 左上角横坐标起点
     * @param y0 做上角纵坐标起点
     * @param w  图片宽度
     * @param h  图片高度
     * @return 图片的平均颜色
     */
    public static Color averageColor(BufferedImage bi, int x0, int y0, int w,
                                     int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumR = 0, sumG = 0, sumB = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumR += pixel.getRed();
                sumG += pixel.getGreen();
                sumB += pixel.getBlue();
            }
        }
        int num = w * h;
        return new Color((int) sumR / num, (int) sumG / num, (int) sumB / num);
    }

    /**
     * 获得图像的平均值
     *
     * @param image 图像对象
     * @return 平均值
     */
    public static Color averageColor(BufferedImage image) {
        return averageColor(image, 0, 0, image.getWidth(), image.getHeight());
    }
    //* TODO 放到单元测试中
    public static void main(String[] args) throws IOException {
        String imageString1 = "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";
        BufferedImage image1 = convertBase64DataToImage(imageString1);
        String imageString2 = "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";
        BufferedImage image2 = convertBase64DataToImage(imageString2);
        BufferedImage merged = mergeVertical(new BufferedImage[]{image2, image1});
        FileUtils.toFile("d:/testMergeData.png", merged, "png");
    }


    /**
     * 将Base64编码的图片字符串（用于Data URI scheme）转换为图片对象，
     * @param base64ImgData Base64编码的图片字符串
     * @return 图片对象
     * @throws IOException IO异常
     */
    public static BufferedImage convertBase64DataToImage(String base64ImgData) throws IOException {
        Base64.getDecoder().decode(base64ImgData);
        //BASE64Decoder d = new BASE64Decoder();
        //byte[] bs = d.decodeBuffer(base64ImgData);
        byte[] bs = Base64.getDecoder().decode(base64ImgData);
        InputStream in = new ByteArrayInputStream(bs, 0, bs.length);
        return ImageIO.read(in);
    }

    /**
     * 纵向合并多个图像，图片的宽度为最大宽度
     * @param images 要合并的图像对象
     * @return 合并后的图像
     */
    public static BufferedImage mergeVertical(BufferedImage[] images) {
        int currentHeight = 0;
        // 获得最大宽度
        int width = 0;
        //整个的高度
        int height = 0;
        for (BufferedImage image : images) {
            width = Math.max(image.getWidth(), width);
            height += image.getHeight();
        }
        //进行合并
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        for (BufferedImage image : images) {
            int[] imageArray = new int[image.getWidth() * image.getHeight()];
            imageArray = image.getRGB(0, 0, image.getWidth(), image.getHeight(), imageArray, 0, width);
            result.setRGB(0, currentHeight, width, image.getHeight(), imageArray, 0, width);
            currentHeight += image.getHeight();
        }
        return result;
    }

}
