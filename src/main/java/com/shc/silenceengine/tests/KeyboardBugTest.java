/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Sri Harsha Chilakapati
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.shc.silenceengine.tests;

import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.utils.TimeUtils;

/**
 * @author Sri Harsha Chilakapati
 */
public class KeyboardBugTest extends Game
{
    private int    count     = 0;
    private double startTime = -1;

    public static void main(String[] args)
    {
        new KeyboardBugTest().start();
    }

    @Override
    public void init()
    {
        Keyboard.registerTextListener(this::textInputListener);
    }

    @Override
    public void update(float delta)
    {
        if (Keyboard.isClicked('O', Keyboard.KEY_LEFT_SHIFT, Keyboard.KEY_LEFT_ALT))
            System.out.println("SHIFT-ALT-O");

        if (Keyboard.isClicked(Keyboard.KEY_ESCAPE))
            Game.end();

        if (Keyboard.isClicked(Keyboard.KEY_UP))
        {
            if (startTime < 1)
                startTime = TimeUtils.currentMillis();

            System.out.println(++count);

            if (count >= 10)
            {
                double endTime = TimeUtils.currentMillis();
                System.out.println("It took you " + (endTime - startTime) + " millis to press 10 ticks");

                startTime = TimeUtils.currentMillis();
                count = 0;
            }
        }
    }

    private void textInputListener(char[] chars, int codePoint, int mods)
    {
        System.out.print(chars);
    }
}
