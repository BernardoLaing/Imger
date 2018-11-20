/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgerapi;

import java.util.concurrent.RecursiveAction;

/**
 *
 * @author berna
 */
public class Downscaler extends RecursiveAction{
    
    protected static int threshold   = 100;
    
    private int[] src;
    private int width;
    private int height;
    private int yStart;
    private int[] dest;
    
    public Downscaler(int[] sr, int w, int h, int y, int[] d){
        src = sr;
        height = h;
        width = w;
        yStart = y;
        dest = d;
    }
    
    /*
    int destPixel = (0xff000000) 
                            |(((int)r) << 16) 
                            |(((int)g) << 8)  
                            |(((int)b) << 0);
            dest[i] = destPixel;
    */
    
    private void computeDirectly(){
        for(int y = yStart; y < height + yStart; y++){
            for(int x = 0; x < width; x++){
                int index = (y * width) + x;
                int avg = 0;
                float rt = 0, gt = 0, bt = 0;
                for(int r = 0; r < 2; r++){
                    for(int c = 0; c < 2; c++){
                        int srcIndex = ((2*y+r) * 2*width) + (x*2) + c;
                        rt += (float)((src[srcIndex] & 0x00ff0000) >> 16);
                        gt += (float)((src[srcIndex] & 0x0000ff00) >> 8);
                        bt += (float)((src[srcIndex] & 0x000000ff));
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
    }

    @Override
    protected void compute() {
        if(height < threshold){
            computeDirectly();
            return;
        }
        int split = height / 2;
        invokeAll(new Downscaler(src, width, split, yStart, dest),
                    new Downscaler(src, width, split, yStart + split, dest));
        
    }
    
}
