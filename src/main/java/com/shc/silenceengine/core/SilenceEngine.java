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

package com.shc.silenceengine.core;

import com.shc.silenceengine.audio.AudioEngine;
import com.shc.silenceengine.collision.CollisionEngine;
import com.shc.silenceengine.core.glfw.GLFW3;
import com.shc.silenceengine.graphics.GraphicsEngine;
import com.shc.silenceengine.input.InputEngine;
import com.shc.silenceengine.math.Vector4;
import com.shc.silenceengine.utils.NativesLoader;

import org.lwjgl.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sri Harsha Chilakapati
 * @author Josh "ShadowLordAlpha"
 */
public final class SilenceEngine implements IEngine
{
    private static SilenceEngine instance;
    private static Platform      platform;
    private static Logger        logger   = LoggerFactory.getLogger(SilenceEngine.class);

    public static GraphicsEngine  graphics  = new GraphicsEngine();
    public static AudioEngine     audio     = new AudioEngine();
    public static CollisionEngine collision = new CollisionEngine();
    public static InputEngine     input     = new InputEngine();

    private SilenceEngine()
    {
    }

    public static IEngine getInstance()
    {
        if (instance == null)
            instance = new SilenceEngine();

        return instance;
    }

    public static Platform getPlatform()
    {
        if (platform == null)
        {
            final String OS = System.getProperty("os.name").toLowerCase();
            final String ARCH = System.getProperty("os.arch").toLowerCase();

            boolean isWindows = OS.contains("windows");
            boolean isLinux = OS.contains("linux");
            boolean isMac = OS.contains("mac");
            boolean is64Bit = ARCH.equals("amd64") || ARCH.equals("x86_64");

            platform = Platform.UNKNOWN;

            if (isWindows) platform = is64Bit ? Platform.WINDOWS_64 : Platform.WINDOWS_32;
            if (isLinux) platform = is64Bit ? Platform.LINUX_64 : Platform.LINUX_32;
            if (isMac) platform = Platform.MACOSX;
        }

        return platform;
    }
    
    public static Logger getStaticLogger() 
    {
        return logger;
    }

    public static String getVersion()
    {
        return "0.0.4a";
    }

    public static Vector4 getVersionVector()
    {
        return new Vector4(0, 0, 4, 'a');
    }

    @Override
    public void init()
    {
        logger.info("Initializing SilenceEngine. Platform identified as " + getPlatform());

        if (getPlatform() == Platform.MACOSX)
        {
        	 logger.info("Running AWT fix on Mac OS X, needed for LWJGL to run");

            // We need to start AWT in Headless mode, Needed for AWT to work on OS X
            System.setProperty("java.awt.headless", "true");
        }

        logger.info("Initializing LWJGL library. Extracting natives");

        // Load LWJGL natives
        NativesLoader.loadLWJGL();

        logger.info("LWJGL version " + Sys.getVersion() + " is initialised");

        // Initialize GLFW
        if (!GLFW3.init())
            throw new SilenceException("Error initializing GLFW. Your system is unsupported.");

        // Set the error callback
        GLFW3.setErrorCallback(((error, description) -> System.out.println(error + ": " + description)));

        // Initialize other engines
        graphics.init();
        audio.init();
        collision.init();
        input.init();

        logger.info("SilenceEngine version " + getVersion() + " was initialized successfully");
    }

    @Override
    public void beginFrame()
    {
        graphics.beginFrame();
        audio.beginFrame();
        collision.beginFrame();
        input.beginFrame();
    }

    @Override
    public void endFrame()
    {
        graphics.endFrame();
        audio.endFrame();
        collision.endFrame();
        input.endFrame();
    }

    @Override
    public void dispose()
    {
        audio.dispose();
        collision.dispose();
        input.dispose();
        graphics.dispose();

        logger.info("Terminating GLFW library");
        GLFW3.terminate();

        logger.info("SilenceEngine version " + getVersion() + " was successfully terminated");
    }

    public enum Platform
    {
        WINDOWS_32, WINDOWS_64, MACOSX, LINUX_32, LINUX_64, UNKNOWN
    }

    static
    {
        // Every exception occurs after SilenceException, even
        // the uncaught exceptions are thrown as runtime exceptions
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
        {
            try
            {
                // No need to rethrow SilenceException
                if (e instanceof SilenceException)
                    throw e;

                SilenceException.reThrow(e);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
        });
    }
}
