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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import fr.quentinmachu.infernalmaze.ui.math.Matrix3f;
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
    private Texture texture;
    private Matrix4f model;
    private ArrayList<Light> lights;
    private Material material;
    private Vector3f ambient;
    
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    private int primitive = GL_TRIANGLES;
    
    public Renderer(Camera camera, Texture texture, Material material, Vector3f ambient) {
    	this(camera, texture, material, ambient, false);
    }
    
    public Renderer(Camera camera, Texture texture, Material material, Vector3f ambient, boolean useCubeMap) {
    	this.camera = camera;
    	this.texture = texture;
    	this.useCubeMap = useCubeMap;
    	this.material = material;
    	this.ambient = ambient;
    	
    	lights = new ArrayList<Light>();    	
    	model = new Matrix4f();
    	
    	init();
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

        /* Set texture */
        program.setUniform(program.getUniformLocation("texImage"), 0);
        
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

        texture.bind();
        
        drawing = true;
        numVertices = 0;
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
            program.setUniform(program.getUniformLocation("model"), model);
            program.setUniform(program.getUniformLocation("model_3x3_inv_transp"), new Matrix3f(model).invert().transpose());
            
            /* Set projection matrix to perspective matrix */
            program.setUniform(program.getUniformLocation("projection"), Matrix4f.scale(-1, 1, 1).multiply(Matrix4f.perspective(camera.getFov(), (float) width/height, camera.getNear(), camera.getFar())));

            /* Set view matrix to camera */
            Matrix4f view = Matrix4f.lookAt(camera.getEye(), camera.getCenter(), camera.getUp());
            program.setUniform(program.getUniformLocation("view"),  view);
            program.setUniform(program.getUniformLocation("view_inv"), view.invert());
            
            /* Set the material */
            program.setUniform(program.getUniformLocation("material.ambient"), material.getAmbient());
            program.setUniform(program.getUniformLocation("material.diffuse"), material.getDiffuse());
            program.setUniform(program.getUniformLocation("material.specular"), material.getSpecular());
            program.setUniform(program.getUniformLocation("material.shininess"), material.getShininess());
        	
            /* Set the ambient */
            program.setUniform(program.getUniformLocation("ambient"), ambient);
            
            /* Set the lights */
            program.setUniform(program.getUniformLocation("lightsCount"), lights.size());
            for(int i = 0; i < lights.size(); i++) {
            	program.setUniform(program.getUniformLocation("lights["+i+"].position"), lights.get(i).getPosition());
            	program.setUniform(program.getUniformLocation("lights["+i+"].diffuse"), lights.get(i).getDiffuse());
            	program.setUniform(program.getUniformLocation("lights["+i+"].specular"), lights.get(i).getSpecular());
            	
            	program.setUniform(program.getUniformLocation("lights["+i+"].constantAttenuation"), lights.get(i).getConstantAttenuation());
            	program.setUniform(program.getUniformLocation("lights["+i+"].linearAttenuation"), lights.get(i).getLinearAttenuation());
            	program.setUniform(program.getUniformLocation("lights["+i+"].quadraticAttenuation"), lights.get(i).getQuadraticAttenuation());
            	
            	program.setUniform(program.getUniformLocation("lights["+i+"].spotCutoff"), lights.get(i).getSpotCutoff());
            	program.setUniform(program.getUniformLocation("lights["+i+"].spotExponent"), lights.get(i).getSpotExponent());
            	program.setUniform(program.getUniformLocation("lights["+i+"].spotDirection"), lights.get(i).getSpotDirection());
            }
            
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
     * Draws a vertex with the currently bound texture on specified
     * coordinates.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     * @param nx x normal
     * @param ny y normal
     * @param nz z normal
     * @param s coordinate
     * @param t coordinate
     * @param c The color to use
     */
    public void addVertex(float x, float y, float z, float s, float t, float nx, float ny, float nz) {
    	vertices.put(x).put(y).put(z).put(s).put(t).put(nx).put(ny).put(nz);
        numVertices++;
    }
    
    /**
     * Draws a surface. The points are defined clockwise.
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param x3
     * @param y3
     * @param z3
     * @param x4
     * @param y4
     * @param z4
     */
	public void drawSurface(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		reserveVertices(6);

		Vector3f u1 = new Vector3f(x3-x1, y3-y1, z3-z1);
		Vector3f v1 = new Vector3f(x4-x1, y4-y1, z4-z1);
		Vector3f n1 = new Vector3f(u1.x*v1.z - u1.z*v1.y, u1.z*v1.x - u1.x*v1.z, u1.x*v1.y - u1.y*v1.x);
		addVertex(x1, y1, z1, 0, 0, n1.x, n1.y, n1.z);
		addVertex(x3, y3, z3, 1, 1, n1.x, n1.y, n1.z);
		addVertex(x4, y4, z4, 0, 1, n1.x, n1.y, n1.z);
		
		Vector3f u2 = new Vector3f(x2-x1, y2-y1, z2-z1);
		Vector3f v2 = new Vector3f(x3-x1, y3-y1, z3-z1);
		Vector3f n2 = new Vector3f(u2.x*v2.z - u2.z*v2.y, u2.z*v2.x - u2.x*v2.z, u2.x*v2.y - u2.y*v2.x);
		addVertex(x1, y1, z1, 0, 0, n2.x, n2.y, n2.z);
		addVertex(x2, y2, z2, 1, 0, n2.x, n2.y, n2.z);
		addVertex(x3, y3, z3, 1, 1, n2.x, n2.y, n2.z);
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
        int posAttrib = program.getAttributeLocation("in_position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 8 * Float.BYTES, 0);

        /* Specify Texture Pointer */
        int texAttrib = program.getAttributeLocation("in_texcoord");
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 8 * Float.BYTES, 3 * Float.BYTES);
        
        /* Specify Normal Pointer */
        int normalAttrib = program.getAttributeLocation("in_normal");
        program.enableVertexAttribute(normalAttrib);
        program.pointVertexAttribute(normalAttrib, 3, 8 * Float.BYTES, 5 * Float.BYTES);
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

	/**
	 * @return the lights
	 */
	public void setLights(ArrayList<Light> lights) {
		this.lights = lights;
	}
}