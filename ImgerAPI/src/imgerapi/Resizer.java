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

import java.util.concurrent.RecursiveAction;

/**
 *
 * @author berna
 */
public class Resizer extends RecursiveAction{
    
    protected static int threshold   = 100;
    
    private int[] src;
    private int srcWidth;
    private int srcHeight;
    private int[] dest;
    private int width;
    private int height;
    private int yStart;
    private float factor;
    
    public Resizer(int[] s, int sw, int sh, int[] d, int w, int h, float f, int ys){
        src = s;
        srcWidth = sw;
        srcHeight = sh;
        dest = d;
        width = w;
        height = h;
        factor = f;
        yStart = ys;
    }
    
    private void computeDirectly(){
        for(int y = yStart; y < height + yStart; y++){
            for(int x = 0; x < width; x++){
                int avg = 0;
                float rt = 0, gt = 0, bt = 0;
                for(int r = 0; r < 2; r++){
                    for(int c = 0; c < 2; c++){
                        int srcIndex = (int) (((Math.floor((float)y/factor) + r) * srcWidth) + Math.floor((float)x/factor) + c);
                        srcIndex = Math.min(Math.max(srcIndex, 0), src.length -1);
                        rt += (float)((src[srcIndex] & 0x00ff0000) >> 16);
                        gt += (float)((src[srcIndex] & 0x0000ff00) >> 8);
                        bt += (float)((src[srcIndex] & 0x000000ff));
                    }
                }
                int index = x + (y*width);
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
        invokeAll(new Resizer(src, srcWidth, srcHeight, dest, width, split, factor, yStart),
                    new Resizer(src, srcWidth, srcHeight, dest, width, split, factor, yStart+split));
    }
    
}
