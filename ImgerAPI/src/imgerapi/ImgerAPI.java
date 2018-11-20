/*
 * ImgerAPI does simple parallel image manipulation methods.
    Copyright (C) 2018  Bernardo Laing

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package imgerapi;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.concurrent.ForkJoinPool;

/**
 *  ImgerAPI class provides methods for manipulating BufferedImage instances.
 *  The purpose of the class is to provide an easy way to do simple way to do
 *  image manipulation, such as blurring, converting to greyscale, and resizing,
 *  in an efficient way.
 *  All static methods in this class serve to delegate the processing to other
 *  classes, using Fork/Join technique to improve performance.
 * @author Bernardo Laing Bernal
 */
public class ImgerAPI {
    
    // Parallel Implementations
    public static BufferedImage Blur(BufferedImage srcImg, int blurSize){
        int w = srcImg.getWidth();
        int h = srcImg.getHeight();

        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[srcPixels.length];
        
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new Blurer(srcPixels, 0, srcPixels.length, dest, blurSize));
        
        BufferedImage destImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, w, h, dest, 0, w);
        
        return destImg;
    }
    
    public static BufferedImage GreyScale(BufferedImage img){
        int h = img.getHeight();
        int w = img.getWidth();
        
        BufferedImage destImg = copy(img);
        
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new GreyScaler(0, w, 0, h, destImg));
        return destImg;        
    }
    
    public static BufferedImage Downscale(BufferedImage srcImg){
        int w = srcImg.getWidth();
        int h = srcImg.getHeight();
        
        int destW = w/2;
        int destH = h/2;
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        // Destination will have smaller size than original
        int[] dest = new int[destW*destH];
        
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new Downscaler(srcPixels, destW, destH, 0, dest));
        
        BufferedImage destImg = new BufferedImage(destW, destH, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, destW, destH, dest, 0, destW);
        
        return destImg;
    }
    
    public static BufferedImage ColorFilter(BufferedImage srcImg, int color){
        
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[srcPixels.length];
        
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new ColorFilterer(srcPixels, 0, srcPixels.length, color, dest));
        
        BufferedImage destImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, w, h, dest, 0, w);
        
        return destImg;
    }
    
    public static BufferedImage Resize(BufferedImage srcImg, float scale){
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        
        int destW = (int) (w*scale);
        int destH = (int) (h*scale);
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[destW*destH];
        
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new Resizer(srcPixels, w, h, dest, destW, destH, 2, 0));
        
        BufferedImage destImg = new BufferedImage(destW, destH, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, destW, destH, dest, 0, destW);
        
        return destImg;
        
    }
    
    // Sequential Implementation
    
    
    public static BufferedImage SequentialBlur(BufferedImage srcImg, int blurSize){
        if ((blurSize % 2) == 0){
            blurSize += 1;
        }
        int w = srcImg.getWidth();
        int h = srcImg.getHeight();

        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[srcPixels.length];
        
        int sidePixels = (blurSize -1) / 2;
        for(int i = 0; i < srcPixels.length; i++){
            //Get average of the surrounding pixels
            float r = 0, g = 0, b = 0;
            for(int mi = -sidePixels; mi <= sidePixels; mi++){
                int mindex = Math.min(Math.max(mi + i, 0), srcPixels.length -1);
                int pixel = srcPixels[mindex];
                r += (float)((pixel & 0x00ff0000) >> 16)    / blurSize;
                g += (float)((pixel & 0x0000ff00) >> 8)     / blurSize;
                b += (float)(pixel & 0x000000ff)            / blurSize;
            }
            
            int destPixel = (0xff000000) 
                            |(((int)r) << 16) 
                            |(((int)g) << 8)  
                            |((int)b);
            dest[i] = destPixel;
        }
        
        BufferedImage destImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, w, h, dest, 0, w);
        
        return destImg;
    }
    
    public static BufferedImage SequentialGreyScale(BufferedImage img){
        int h = img.getHeight();
        int w = img.getWidth();
        
        BufferedImage destImg = copy(img);
        
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgb = destImg.getRGB(i, j);
                // Get red
                int r = (rgb >> 16) & 0xff;
                // Get green
                int g = (rgb >> 8) & 0xff;
                //Get blue
                int b = rgb & 0xff;
                // Get greyness
                int grayness = (r + g + b) / 3;
                rgb = (grayness << 16) + (grayness << 8) + grayness;
                destImg.setRGB(i, j, rgb);
            }
        }
        
        return destImg;        
    }
    
    public static BufferedImage SequentialDownscale(BufferedImage srcImg){
        int w = srcImg.getWidth();
        int h = srcImg.getHeight();
        
        int destW = w/2;
        int destH = h/2;
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        // Destination will have smaller size than original
        int[] dest = new int[destW*destH];
        
        for(int y = 0; y < destH; y++){
            for(int x = 0; x < destW; x++){
                int index = (y * destW) + x;
                int avg = 0;
                float rt = 0, gt = 0, bt = 0;
                for(int r = 0; r < 2; r++){
                    for(int c = 0; c < 2; c++){
                        int srcIndex = ((2*y+r) * 2*destW) + (x*2) + c;
                        rt += (float)((srcPixels[srcIndex] & 0x00ff0000) >> 16);
                        gt += (float)((srcPixels[srcIndex] & 0x0000ff00) >> 8);
                        bt += (float)((srcPixels[srcIndex] & 0x000000ff));
                    }
                }
                rt /= 4;
                gt /= 4;
                bt /= 4;
                avg =   (0xff000000)      |
                        (((int)rt) << 16) |
                        (((int)gt) << 8)  |
                        (((int)bt));
                dest[index] = avg;
            }
        }
        
        BufferedImage destImg = new BufferedImage(destW, destH, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, destW, destH, dest, 0, destW);
        
        return destImg;
    }
    
    public static BufferedImage SequentialColorFilter(BufferedImage srcImg, int color){
        
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[srcPixels.length];
        
        for(int i = 0; i < srcPixels.length; i++){
            dest[i] = (srcPixels[i] & color);
        }
        
        BufferedImage destImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, w, h, dest, 0, w);
        
        return destImg;
    }
    
    public static BufferedImage SequentialResize(BufferedImage srcImg, float scale){
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        
        int destW = (int) (w*scale);
        int destH = (int) (h*scale);
        
        int[] srcPixels = srcImg.getRGB(0, 0, w, h, null, 0, w);
        int[] dest = new int[destW*destH];
        
        for(int y = 0; y < destH; y++){
            for(int x = 0; x < destW; x++){
                int avg = 0;
                float rt = 0, gt = 0, bt = 0;
                for(int r = 0; r < 2; r++){
//                   int srcIndex = ((2*y+r) * 2*width) + (x*2) + c;
//                    int srcIndexY = (y/factor * (srcHeight - 1)) + r;
                    for(int c = 0; c < 2; c++){
//                        int srcIndexX = (x/width * (srcWidth - 1)) + c;
//                        int srcIndex = srcIndexX + (srcIndexY * srcWidth);
                        int srcIndex = (int) (((Math.floor(y/scale) + r) * w) + Math.floor(x/scale) + c);
                        srcIndex = Math.min(Math.max(srcIndex, 0), srcPixels.length -1);
                        rt += (float)((srcPixels[srcIndex] & 0x00ff0000) >> 16);
                        gt += (float)((srcPixels[srcIndex] & 0x0000ff00) >> 8);
                        bt += (float)((srcPixels[srcIndex] & 0x000000ff));
                    }
                }
                int index = x + (y*destW);
                rt /= 4;
                gt /= 4;
                bt /= 4;
                avg =   (0xff000000)      |
                        (((int)rt) << 16) |
                        (((int)gt) << 8)  |
                        (((int)bt));
                dest[index] = avg;
            }
        }
        
        BufferedImage destImg = new BufferedImage(destW, destH, BufferedImage.TYPE_INT_ARGB);
        destImg.setRGB(0, 0, destW, destH, dest, 0, destW);
        
        return destImg;
        
    }
    
    //Helpers
    
    private static BufferedImage copy(BufferedImage src){
        ColorModel cm = src.getColorModel();
        boolean isAlphaPre = cm.isAlphaPremultiplied();
        WritableRaster raster = src.copyData(src.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPre, null);
    }    
}
