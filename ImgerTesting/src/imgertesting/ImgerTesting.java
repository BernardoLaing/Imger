/*
 * ImgerTesting does some performance tests on the ImgerAPI Library.
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
package imgertesting;

import imgerapi.ImgerAPI;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author berna
 */
public class ImgerTesting {

    public static final int N = 75;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedImage img = null;
        BufferedImage out =  null;
        long startTime, stopTime, total = 0;
        
        img = ImageIO.read(new File("image-75.png"));
        
        // Blurring
//        System.out.println("-------------------------------------------");
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.SequentialBlur(img, 15);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Sequential Blur: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("sequentialBlur.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        total = 0;
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.Blur(img, 15);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Parallel Blur: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("parallelBlur.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        // Downscale
//        System.out.println("-------------------------------------------");
//        total = 0;
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.SequentialDownscale(img);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Sequential Downscale: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("sequentialDownscale.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        total = 0;
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.Downscale(img);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Parallel Downscale: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("parallelDownscale.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        // ColorFilter
        System.out.println("-------------------------------------------");
        total = 0;
        for(int i = 0; i < N; i++){
            startTime = System.currentTimeMillis();
            out = ImgerAPI.SequentialColorFilter(img, 0xff00ff00);
            stopTime = System.currentTimeMillis();
            total += (stopTime - startTime);
        }
        total /= N;
        System.out.println("Sequential ColorFilter: " + total + " ms average");
        
        try {
            ImageIO.write(out, "png", new File("sequentialColorFilter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        total = 0;
        for(int i = 0; i < N; i++){
            startTime = System.currentTimeMillis();
            out = ImgerAPI.ColorFilter(img, 0xff00ff00);
            stopTime = System.currentTimeMillis();
            total += (stopTime - startTime);
        }
        total /= N;
        System.out.println("Parallel ColorFilter: " + total + " ms average");
        
        try {
            ImageIO.write(out, "png", new File("parallelColorFilter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        // Greyscale
        System.out.println("-------------------------------------------");
        total = 0;
        for(int i = 0; i < N; i++){
            startTime = System.currentTimeMillis();
            out = ImgerAPI.SequentialGreyScale(img);
            stopTime = System.currentTimeMillis();
            total += (stopTime - startTime);
        }
        total /= N;
        System.out.println("Sequential Greyscale: " + total + " ms average");
        
        try {
            ImageIO.write(out, "png", new File("sequentialGreyscale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        total = 0;
        for(int i = 0; i < N; i++){
            startTime = System.currentTimeMillis();
            out = ImgerAPI.GreyScale(img);
            stopTime = System.currentTimeMillis();
            total += (stopTime - startTime);
        }
        total /= N;
        System.out.println("Parallel Greyscale: " + total + " ms average");
        
        try {
            ImageIO.write(out, "png", new File("parallelGreyscale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Resize
//        System.out.println("-------------------------------------------");
//        total = 0;
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.SequentialResize(img, 2);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Sequential Resize: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("sequentialResize.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        total = 0;
//        for(int i = 0; i < N; i++){
//            startTime = System.currentTimeMillis();
//            out = ImgerAPI.Resize(img, 2);
//            stopTime = System.currentTimeMillis();
//            total += (stopTime - startTime);
//        }
//        total /= N;
//        System.out.println("Parallel Resize: " + total + " ms average");
//        
//        try {
//            ImageIO.write(out, "png", new File("parallelResize.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    
}
