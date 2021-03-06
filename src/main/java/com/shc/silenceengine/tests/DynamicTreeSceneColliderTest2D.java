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

import com.shc.silenceengine.collision.broadphase.DynamicTree2D;
import com.shc.silenceengine.collision.colliders.SceneCollider2D;
import com.shc.silenceengine.core.Display;
import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.Sprite;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.graphics.opengl.GL3Context;
import com.shc.silenceengine.graphics.opengl.Texture;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.math.geom2d.Rectangle;
import com.shc.silenceengine.scene.Scene2D;
import com.shc.silenceengine.scene.entity.Entity2D;

/**
 * @author Sri Harsha Chilakapati
 */
public class DynamicTreeSceneColliderTest2D extends Game
{
    private Scene2D         scene;
    private SceneCollider2D collider;
    private OrthoCam        cam;

    private Texture playerTexture;
    private Texture boxTexture;

    private Sprite boxSprite;
    private Sprite playerSprite;

    public static void main(String[] args)
    {
        new DynamicTreeSceneColliderTest2D().start();
    }

    public void init()
    {
        Display.setTitle("DynamicTree Collider Test 2D");

        playerTexture = Texture.fromColor(Color.DARK_RED, 48, 48);
        boxTexture = Texture.fromColor(Color.CORN_FLOWER_BLUE, 48, 48);

        boxSprite = new Sprite(boxTexture);
        playerSprite = new Sprite(playerTexture);

        GL3Context.clearColor(Color.DARK_SLATE_GRAY);

        cam = new OrthoCam().initProjection(Display.getWidth(), Display.getHeight());

        // Create and initialize the scene
        scene = new Scene2D();
        for (int i = 0; i < 20; i++)
        {
            scene.addChild(new Box(new Vector2(48 * i, 0)));
            scene.addChild(new Box(new Vector2(0, 48 * i)));

            scene.addChild(new Box(new Vector2(48 * i, 48 * 19)));
            scene.addChild(new Box(new Vector2(48 * 19, 48 * i)));
        }
        scene.addChild(new Player(new Vector2(Display.getWidth() / 2 - 24, Display.getHeight() / 2 - 24)));

        // Create the SceneCollider and set the scene
        collider = new SceneCollider2D(new DynamicTree2D());
        collider.setScene(scene);

        // Register entities for collisions
        collider.register(Player.class, Box.class);

        System.out.println(scene.getEntities().size());
    }

    public void resize()
    {
        cam.initProjection(Display.getWidth(), Display.getHeight());
    }

    public void update(float delta)
    {
        if (Keyboard.isPressed(Keyboard.KEY_ESCAPE))
            end();

        // Update the scene and check for collisions
        scene.update(delta);
        collider.checkCollisions();

        Display.setTitle("Total Memory: " + (getTotalMemory() / 1048576) +
                         "MB / Free Memory: " + (getFreeMemory() / 1048576) +
                         "MB / Used Memory: " + (getUsedMemory() / 1048576) + "MB" +
                         "/ RC: " + SilenceEngine.graphics.renderCallsPerFrame);
    }

    public void render(float delta, Batcher batcher)
    {
        cam.apply();
        scene.render(delta);
    }

    public void dispose()
    {
        scene.destroy();
        playerTexture.dispose();
        boxTexture.dispose();
    }

    public class Box extends Entity2D
    {
        public Box(Vector2 position)
        {
            super(boxSprite, new Rectangle(48, 48));
            setPosition(position);
        }
    }

    public class Player extends Entity2D
    {
        public Player(Vector2 position)
        {
            super(boxSprite, new Rectangle(48, 48));
            setPosition(position);

            setSprite(playerSprite);
        }

        public void update(float delta)
        {
            float speed = 4;

            if (Keyboard.isPressed(Keyboard.KEY_UP))
                getVelocity().y = -speed;

            if (Keyboard.isPressed(Keyboard.KEY_DOWN))
                getVelocity().y = +speed;

            if (Keyboard.isPressed(Keyboard.KEY_LEFT))
                getVelocity().x = -speed;

            if (Keyboard.isPressed(Keyboard.KEY_RIGHT))
                getVelocity().x = +speed;

            cam.center(getPolygon().getCenter());

            rotate(90 * delta);
        }

        public void collision(Entity2D other)
        {
            alignNextTo(other);
            bounce(other);
        }
    }
}
