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
public class ColorFilterer extends RecursiveAction{
    
    protected static int threshold   = 1000;
    private int[] src;
    private int start;
    private int length;
    private int color;
    private int[] dest;
    
    public ColorFilterer(int[] sr, int s, int l, int c, int[] d){
        src = sr;
        start = s;
        length = l;
        color = c;
        dest = d;
    }

    private void computeDirectly(){
        for(int i = start; i < start+length; i++){
            dest[i] = (src[i] & color);
        }
    }
    
    @Override
    protected void compute() {
        if(length < threshold){
            computeDirectly();
            return;
        }
        int split = length / 2;
        invokeAll( new ColorFilterer(src, start, split, color, dest),
                    new ColorFilterer(src, start+split, length-split, color, dest));
    }
    
}
