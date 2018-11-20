package imgerapi;


import java.util.concurrent.RecursiveAction;

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

/**
 *
 * @author berna
 */
public class Blurer extends RecursiveAction{
    
    protected static int threshold   = 10000;
    
    private int[] src;
    private int start;
    private int length;
    private int[] dest;
    
    private int blurSize;
    
    public Blurer(int[] sr, int st, int l, int[] d, int bs){
        src = sr;
        start = st;
        length = l;
        dest = d;
        if((bs % 2) == 0)
            bs += 1;
        blurSize = bs;
    }
    
    private void computeDirectly(){
        int sidePixels = (blurSize -1) / 2;
        for(int i = start; i < start+length; i++){
            //Get average
            float r = 0, g = 0, b = 0;
            for(int mi = -sidePixels; mi <= sidePixels; mi++){
                int mindex = Math.min(Math.max(mi + i, 0), src.length -1);
                int pixel = src[mindex];
                r += (float)((pixel & 0x00ff0000) >> 16)    / blurSize;
                g += (float)((pixel & 0x0000ff00) >> 8)     / blurSize;
                b += (float)((pixel & 0x000000ff) >> 0)     / blurSize;
            }
            
            int destPixel = (0xff000000) 
                            |(((int)r) << 16) 
                            |(((int)g) << 8)  
                            |(((int)b) << 0);
            dest[i] = destPixel;
        }
    }

    @Override
    protected void compute() {
        if(length < threshold){
            computeDirectly();
            return;
        }
        int split = length / 2;
        invokeAll(new Blurer(src, start, split, dest, blurSize),
                    new Blurer(src, start+split,  length - split, dest, blurSize));
    }
    
}
