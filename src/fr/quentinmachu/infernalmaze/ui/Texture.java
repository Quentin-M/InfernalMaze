/*
 * The MIT License (MIT)
 *
 * Copyright © 2014, Heiko Brumme
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
package fr.quentinmachu.infernalmaze.ui;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;

/**
 * This class represents a texture.
 *
 * @author Heiko Brumme
 */
public class Texture {

    /**
     * Stores the handle of the texture.
     */
    private final int id;

    /**
     * Width of the texture.
     */
    private final int width;
    /**
     * Height of the texture.
     */
    private final int height;

    /**
     * Is the texture a cube map ?
     */
    private final boolean isCubeMap;

    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width
     *            Width of the texture
     * @param height
     *            Height of the texture
     * @param data
     *            Picture Data in RGBA format
     * @param isCubeMap
     *            Defines whether the texture is a cube map or a 2D texture
     */
    public Texture(int width, int height, ByteBuffer data) {
	id = glGenTextures();
	this.width = width;
	this.height = height;
	this.isCubeMap = false;

	glBindTexture(GL_TEXTURE_2D, id);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

    public Texture(int width, int height, ByteBuffer data0, ByteBuffer data1, ByteBuffer data2, ByteBuffer data3, ByteBuffer data4, ByteBuffer data5) {
	id = glGenTextures();
	this.width = width;
	this.height = height;
	this.isCubeMap = true;

	glBindTexture(GL_TEXTURE_CUBE_MAP, id);

	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 0);

	glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data0);
	glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data1);
	glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data2);
	glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data3);
	glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data4);
	glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGBA8, width / 4, height / 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, data5);
    }

    /**
     * Binds the texture.
     */
    public void bind() {
	if (!isCubeMap)
	    glBindTexture(GL_TEXTURE_2D, id);
	else
	    glBindTexture(GL_TEXTURE_CUBE_MAP, id);
    }

    /**
     * Delete the texture.
     */
    public void delete() {
	glDeleteTextures(id);
    }

    /**
     * Gets the texture width.
     *
     * @return Texture width
     */
    public int getWidth() {
	return width;
    }

    /**
     * Gets the texture height.
     *
     * @return Texture height
     */
    public int getHeight() {
	return height;
    }

    /**
     * Load texture from file.
     *
     * @param path
     *            File path of the texture
     * @return Texture from specified file
     */
    public static Texture loadTexture(String path) {
	return Texture.loadTexture(path, false);
    }

    /**
     * Load texture from file.
     *
     * @param path
     *            File path of the texture
     * @param isCubeMap
     *            Defines whether the texture is a cube map or a 2D texture
     * @return Texture from specified file
     */
    public static Texture loadTexture(String path, boolean isCubeMap) {
	BufferedImage image = null;
	try {
	    InputStream in = Texture.class.getResourceAsStream(path);
	    image = ImageIO.read(in);
	} catch (IOException|IllegalArgumentException ex) {
	    throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator() + ex.getMessage());
	}
	if (image != null) {
	    /* Flip image Horizontal to get the origin to bottom left */
	    AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
	    transform.translate(0, -image.getHeight());
	    AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    image = operation.filter(image, null);

	    /* Get width and height of image */
	    int width = image.getWidth();
	    int height = image.getHeight();

	    /* Get pixel data of image */
	    if (!isCubeMap) {
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		/* Put pixel data into a ByteBuffer */
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
		    for (int x = 0; x < width; x++) {
			/* Pixel as RGBA: 0xAARRGGBB */
			int pixel = pixels[y * width + x];
			/* Red component 0xAARRGGBB >> 16 = 0x0000AARR */
			buffer.put((byte) ((pixel >> 16) & 0xFF));
			/* Green component 0xAARRGGBB >> 8 = 0x00AARRGG */
			buffer.put((byte) ((pixel >> 8) & 0xFF));
			/* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
			buffer.put((byte) (pixel & 0xFF));
			/* Alpha component 0xAARRGGBB >> 24 = 0x000000AA */
			buffer.put((byte) ((pixel >> 24) & 0xFF));
		    }
		}
		/* Do not forget to flip the buffer! */
		buffer.flip();

		return new Texture(width, height, buffer);
	    } else {
		int[] pixels = new int[(width / 4) * (height / 3)];

		ByteBuffer[] buffers = new ByteBuffer[6];
		for (int i = 0; i < 6; i++) {
		    if (i == 0)
			image.getRGB(width / 4, 0, width / 4, height / 3, pixels, 0, width / 4);
		    else if (i == 1)
			image.getRGB(0, height / 3, width / 4, height / 3, pixels, 0, width / 4);
		    else if (i == 2)
			image.getRGB(width / 4, height / 3, width / 4, height / 3, pixels, 0, width / 4);
		    else if (i == 3)
			image.getRGB(2 * width / 4, height / 3, width / 4, height / 3, pixels, 0, width / 4);
		    else if (i == 4)
			image.getRGB(3 * width / 4, height / 3, width / 4, height / 3, pixels, 0, width / 4);
		    else if (i == 5)
			image.getRGB(width / 4, 2 * height / 3, width / 4, height / 3, pixels, 0, width / 4);

		    /* Put pixel data into a ByteBuffer */
		    buffers[i] = BufferUtils.createByteBuffer((width / 4) * (height / 3) * 4);
		    for (int y = 0; y < height / 3; y++) {
			for (int x = 0; x < width / 4; x++) {
			    /* Pixel as RGBA: 0xAARRGGBB */
			    int pixel = pixels[y * (width / 4) + x];

			    /* Red component 0xAARRGGBB >> 16 = 0x0000AARR */
			    buffers[i].put((byte) ((pixel >> 16) & 0xFF));
			    /* Green component 0xAARRGGBB >> 8 = 0x00AARRGG */
			    buffers[i].put((byte) ((pixel >> 8) & 0xFF));
			    /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
			    buffers[i].put((byte) (pixel & 0xFF));
			    /* Alpha component 0xAARRGGBB >> 24 = 0x000000AA */
			    buffers[i].put((byte) ((pixel >> 24) & 0xFF));
			}
		    }

		    /* Do not forget to flip the buffer! */
		    buffers[i].flip();
		}

		return new Texture(width, height, buffers[0], buffers[1], buffers[2], buffers[3], buffers[4], buffers[5]);
	    }
	} else {
	    throw new RuntimeException("File extension not supported!" + System.lineSeparator() + "The following file extensions " + "are supported: " + Arrays.toString(ImageIO.getReaderFileSuffixes()));
	}
    }
}
