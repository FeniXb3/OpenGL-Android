package com.example.glpj1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class GlRenderer implements GLSurfaceView.Renderer {
	// New class members
	private final FloatBuffer mTriangle1Vertices;
	 
	private final int mBytesPerFloat = Float.SIZE/8;
	 
	private int mColorHandle;
	
	private int mPositionHandle;
     
    private final int mStrideBytes = 7 * mBytesPerFloat;
     
    private final int mPositionOffset = 0;
     
    private final int mPositionDataSize = 3;
     
    private final int mColorOffset = 3;
     
    private final int mColorDataSize = 4;
	 
	
	final String vertexShader =
			"attribute vec4 vVertex;     \n"     
		  + "attribute vec4 vColor;        \n"     
		 
		  + "varying vec4 vVaryingColor;          \n"     
		 
		  + "void main()                    \n"  
		  + "{                              \n"
		  + "   vVaryingColor  = vColor;          \n"     
		                                            
		  + "   gl_Position = vVertex;   \n"     
		  + "}                              \n";   
	 
	final String fragmentShader =
			"precision mediump float; \n"
		  + "varying  vec4 vVaryingColor;       \n"    
		                                            
		  + "void main()                    \n"     
		  + "{                              \n"
		  + "   gl_FragColor  = vVaryingColor;     \n"     
		  + "}                              \n";

	
	
	/**
	 * Initialize the model data.
	 */
	public GlRenderer()
	{
	    // This triangle is red, green, and blue.
	    final float[] triangle1VerticesData = {
	            // X, Y, Z,
	            // R, G, B, A
	    		-0.5f, 0.0f, 1.0f,
	            1.0f, 0.0f, 0.0f, 1.0f,
	 
	            0.5f, 0.0f, 1.0f,
	            1.0f, 0.0f, 0.0f, 1.0f,
	 
	            0.0f, 0.5f, -1.0f,
	            1.0f, 0.0f, 0.0f, 1.0f
	    };
	    
	    
	    // Initialize the buffers.
	    mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
	    .order(ByteOrder.nativeOrder()).asFloatBuffer();
	 

	    mTriangle1Vertices.put(triangle1VerticesData).position(0);
	}
	
	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer The buffer containing the vertex data.
	 */
	private void drawTriangle(final FloatBuffer aTriangleBuffer)
	{
	    // Pass in the position information
	    aTriangleBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	           mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aTriangleBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        drawTriangle(mTriangle1Vertices);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		int size = Math.min(width, height);
		int max = Math.max(width, height);
		// set viewport to center
	    GLES20.glViewport((max-height)/2, (max-width)/2, size, size);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
	    GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
	    
	 // Load in the vertex shader.
	    int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
	     
	    if (vertexShaderHandle != 0)
	    {
	        // Pass in the shader source.
	        GLES20.glShaderSource(vertexShaderHandle, vertexShader);
	     
	        // Compile the shader.
	        GLES20.glCompileShader(vertexShaderHandle);
	     
	        // Get the compilation status.
	        final int[] compileStatus = new int[1];
	        GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	     
	        // If the compilation failed, delete the shader.
	        if (compileStatus[0] == 0)
	        {
	            GLES20.glDeleteShader(vertexShaderHandle);
	            vertexShaderHandle = 0;
	        }
	    }
	    
	 // Load in the vertex shader.
	    int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
	     
	    if (fragmentShaderHandle != 0)
	    {
	        // Pass in the shader source.
	        GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
	     
	        // Compile the shader.
	        GLES20.glCompileShader(fragmentShaderHandle);
	     
	        // Get the compilation status.
	        final int[] compileStatus = new int[1];
	        GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	     
	        // If the compilation failed, delete the shader.
	        if (compileStatus[0] == 0)
	        {
	            GLES20.glDeleteShader(fragmentShaderHandle);
	            fragmentShaderHandle = 0;
	        }
	    }
	     
	    if (fragmentShaderHandle == 0)
	    {
	        throw new RuntimeException("Error creating fragment shader.");
	    }
	    
	 // Create a program object and store the handle to it.
	    int programHandle = GLES20.glCreateProgram();
	     
	    if (programHandle != 0)
	    {
	        // Bind the vertex shader to the program.
	        GLES20.glAttachShader(programHandle, vertexShaderHandle);
	     
	        // Bind the fragment shader to the program.
	        GLES20.glAttachShader(programHandle, fragmentShaderHandle);
	     
	        // Bind attributes
	        GLES20.glBindAttribLocation(programHandle, 0, "vVertex");
	        GLES20.glBindAttribLocation(programHandle, 1, "vColor");
	     
	        // Link the two shaders together into a program.
	        GLES20.glLinkProgram(programHandle);
	     
	        // Get the link status.
	        final int[] linkStatus = new int[1];
	        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
	     
	        // If the link failed, delete the program.
	        if (linkStatus[0] == 0)
	        {
	            GLES20.glDeleteProgram(programHandle);
	            programHandle = 0;
	        }
	    }
	     
	    if (programHandle == 0)
	    {
	        throw new RuntimeException("Error creating program.");
	    }
	    
	    mPositionHandle = GLES20.glGetAttribLocation(programHandle, "vVertex");
	    if(mPositionHandle == -1) {
	    	throw new RuntimeException("vVertex could not be found");
	    }
	    
	    mColorHandle = GLES20.glGetAttribLocation(programHandle, "vColor");
	    if(mColorHandle == -1) {
	    	throw new RuntimeException("vColor could not be found");
	    }
	    
	    // Tell OpenGL to use this program when rendering.
	    GLES20.glUseProgram(programHandle);
	}
}
