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

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * This class is performing the rendering process.
 *
 * @author Heiko Brumme
 */
public class Renderer {
    public static final int VERTICES_BUFFER_SIZE = (int) Math.pow(2, 16);
    
    private boolean useCubeMap;
    
    private Camera camera;
    private Matrix4f model;
    
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    private int primitive = GL_TRIANGLES;
    
    public Renderer(Camera camera) {
    	this(camera, false);
    }
    
    public Renderer(Camera camera, boolean useCubeMap) {
    	this.camera = camera;
    	this.useCubeMap = useCubeMap;
    }
    
    /**
     * Initializes the renderer.
     */
    public void init() {
    	/* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();

        /* Generate Vertex Buffer Object */
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        /* Create FloatBuffer */
        vertices = BufferUtils.createFloatBuffer(VERTICES_BUFFER_SIZE);

        /* Upload null data to allocate storage for the VBO */
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_STREAM_DRAW);

        /* Initialize variables */
        numVertices = 0;
        drawing = false;

        /* Load shaders */
        if(!useCubeMap) {
        	 vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "resources/default_vertex.glsl");
             fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "resources/default_fragment.glsl");
        } else {
        	vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "resources/cubemap_vertex.glsl");
            fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "resources/cubemap_fragment.glsl");
        }

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.bindFragmentDataLocation(0, "fragColor");

        program.link();
        program.use();
        
        /* Specify Vertex Pointers */
        specifyVertexAttributes();

        /* Set texture uniform */
        int uniTex = program.getUniformLocation("texImage");
        program.setUniform(uniTex, 0);
        
        /* Enable blending */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /* Enable Z-Buffer */
        glEnable(GL_DEPTH_TEST);
    }
    
    /**
     * Begin rendering.
     */
    public void begin() {
        if (drawing) {
            throw new IllegalStateException("Renderer is already drawing!");
        }
        drawing = true;
        numVertices = 0;
        model = new Matrix4f();
    }

    /**
     * End rendering.
     */
    public void end() {
        if (!drawing) {
            throw new IllegalStateException("Renderer isn't drawing!");
        }
        drawing = false;
        flush();
    }

    /**
     * Flushes the data to the GPU to let it get rendered.
     */
    public void flush() {
        if (numVertices > 0) {
            /* Get width and height of framebuffer */
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            int width = widthBuffer.get();
            int height = heightBuffer.get();

            /* Flip vertices buffer */
            vertices.flip();

            /* Re-bind & Re-use program */
            if (vao != null) {
                vao.bind();
            } else {
                vbo.bind(GL_ARRAY_BUFFER);
                specifyVertexAttributes();
            }
            program.use();
            
            /* Set model matrix */
            int uniModel = program.getUniformLocation("model");
            program.setUniform(uniModel, model);
            
            /* Set projection matrix to perspective matrix */
	        Matrix4f projection = Matrix4f.perspective(camera.getFov(), (float) width/height, camera.getNear(), camera.getFar());
            int uniProjection = program.getUniformLocation("projection");
            program.setUniform(uniProjection, Matrix4f.scale(-1, 1, 1).multiply(projection));

            /* Set view matrix to camera */
            Matrix4f view = Matrix4f.lookAt(camera.getEye(), camera.getCenter(), camera.getUp());
            int uniView = program.getUniformLocation("view");
            program.setUniform(uniView, view);
            
            /* Upload the new vertex data */
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(GL_ARRAY_BUFFER, 0, vertices);

            /* Draw batch */
            glDrawArrays(primitive, 0, numVertices);

            /* Clear vertex data for next batch */
            vertices.clear();
            numVertices = 0;
        }
    }

    /**
     * Make sure that at least count vertices are available
     * 
     * @param count the number of vertices to reserve
     */
    public void reserveVertices(int count) {
    	if(vertices.remaining() < count * 11) {
    		/* We need more space in the buffer, so flush it */
            flush();
    	}
    }
  
    /**
     * Draw a vertex with the currently bound texture on specified
     * coordinates.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     * @param s coordinate
     * @param t coordinate
     */
    public void addVertex(float x, float y, float z, float s, float t) {        
        addVertex(x, y, z, s, t, Color.WHITE);
    }
    
    /**
     * Draw a vertex with the currently bound texture on specified
     * coordinates.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     * @param s coordinate
     * @param t coordinate
     * @param c The color to use
     */
    public void addVertex(float x, float y, float z, float s, float t, Color c) {
    	Vector3f normal = new Vector3f(x, y, z).normalize();
    	vertices.put(x).put(y).put(z).put(c.getRed() / 255f).put(c.getGreen() / 255f).put(c.getBlue() / 255f).put(s).put(t).put(normal.x).put(normal.y).put(normal.z);
        numVertices++;
    }
    
	public void drawSurface(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		reserveVertices(6);
		
		addVertex(x1, y1, z1, 0, 0);
		addVertex(x2, y2, z2, 1, 0);
		addVertex(x3, y3, z3, 0, 1);
		
		addVertex(x2, y2, z2, 1, 0);
		addVertex(x3, y3, z3, 0, 1);
		addVertex(x4, y4, z4, 1, 1);
	}

    /**
     * Dispose renderer and clean up its used data.
     */
    public void dispose() {
        if (vao != null) {
            vao.delete();
        }
        vbo.delete();
        vertexShader.delete();
        fragmentShader.delete();
        program.delete();
    }
    
    /**
     * Specifies the vertex pointers.
     */
    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 11 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 11 * Float.BYTES, 3 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = program.getAttributeLocation("texcoord");
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 11 * Float.BYTES, 6 * Float.BYTES);
        
        /* Specify Normal Pointer */
        int normalAttrib = program.getAttributeLocation("normal");
        program.enableVertexAttribute(normalAttrib);
        program.pointVertexAttribute(normalAttrib, 3, 11 * Float.BYTES, 8 * Float.BYTES);
    }

	/**
	 * Set the model matrix
	 * 
	 * @param model the model matrix to set
	 */
	public void setModel(Matrix4f model) {
		this.model = model;
	}
	
	/**
	 * Set the drawing primitive
	 * 
	 * @param primitive the primitive to use
	 */
	public void setPrimitive(int primitive) {
		this.primitive = primitive;
	}
}