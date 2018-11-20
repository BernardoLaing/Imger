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
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author berna
 */
public class GreyScaler extends RecursiveAction{

    public final int tresh = 10000;
    private int rStart, rEnd;
    private int cStart, cEnd;
    BufferedImage img, out;
    
    public GreyScaler(int rs, int re, int cs, int ce, BufferedImage i){
        rStart = rs;
        rEnd = re;
        cStart = cs;
        cEnd = ce;
        img = i;
    }
    
    private void computeDirectly(){
        for (int i = rStart; i < rEnd; i++) {
            for (int j = 0; j < cEnd; j++) {
                int rgb = img.getRGB(i, j);
                // Get red
                int r = (rgb >> 16) & 0xff;
                // Get green
                int g = (rgb >> 8) & 0xff;
                //Get blue
                int b = rgb & 0xff;
                // Get greyness
                int grayness = (r + g + b) / 3;
                rgb = (grayness << 16) + (grayness << 8) + grayness;
                img.setRGB(i, j, rgb);
            }
        }
    }
    
    @Override
    protected void compute() {
        if((rEnd - rStart) < tresh){
            computeDirectly();
        }else{
            int mid = (rStart + rEnd) / 2;
            invokeAll(new GreyScaler(rStart, mid, cStart, cEnd, img),
                        new GreyScaler(mid, rEnd, cStart, cEnd, img));
        }
    }
    
}
