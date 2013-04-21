package com.example.glpj2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.text.format.Time;

public class GlRenderer implements GLSurfaceView.Renderer {

	private final FloatBuffer mPyramid;
	
	private int MVPMatrixLocation;
	
	private float[] ProjectionMatrix = new float[16];
	
	private float[] CameraMatrix = new float[16];
	
	private float[] ViewMatrix = new float[16];
	
	private long startTime;
	
	 
	private final int mBytesPerFloat = Float.SIZE/8;
	 
	private int mColorHandle;
	
	private int mPositionHandle;
     
    private final int mStrideBytes = 7 * mBytesPerFloat;
     
    private final int mPositionOffset = 0;
     
    private final int mPositionDataSize = 3;
     
    private final int mColorOffset = 3;
     
    private final int mColorDataSize = 4;
	 
	
	final String vertexShader =
			"uniform mat4 MVPMatrix;      \n" 
		  + "attribute vec4 vVertex;       \n"     
		  + "attribute vec4 vColor;        \n"     
		 
		  + "varying vec4 vVaryingColor;          \n"     
		 
		  + "void main()                    \n"  
		  + "{                              \n"
		  + "   vVaryingColor  = vColor;          \n"     
		                                            
		  + "   gl_Position = MVPMatrix*vVertex;   \n"     
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
		final float[] pyramid = {
				
				//blue triangle				
	            // X, Y, Z,
	            // R, G, B, A
	    		-1.0f, 1.0f, 0.0f,
	            0.0f, 0.0f, 1.0f, 1.0f,
	 
	            -1.0f, -1.0f, 0.0f,
	            0.0f, 0.0f, 1.0f, 1.0f,
	            
	            0.0f, 0.0f, 2.0f,
	            0.0f, 0.0f, 1.0f, 1.0f,

	            // green triangle
	            // X, Y, Z,
	            // R, G, B, A
	    		-1.0f, -1.0f, 0.0f,
	            0.0f, 1.0f, 0.0f, 1.0f,
	 
	            1.0f, -1.0f, 0.0f,
	            0.0f, 1.0f, 0.0f, 1.0f,
	 
	            0.0f, 0.0f, 2.0f,
	            0.0f, 1.0f, 0.0f, 1.0f,
	           
	            // red triangle
	            // X, Y, Z,
	            // R, G, B, A
	    		1.0f, -1.0f, 0.0f,
	            1.0f, 0.0f, 0.0f, 1.0f,
	 
	            1.0f, 1.0f, 0.0f,
	            1.0f, 0.0f, 0.0f, 1.0f,
	 
	            0.0f, 0.0f, 2.0f,
	            1.0f, 0.0f, 0.0f, 1.0f,
	            
	            // yellow triangle
	            // X, Y, Z,
	            // R, G, B, A
	    		1.0f, 1.0f, 0.0f,
	            1.0f, 1.0f, 0.0f, 1.0f,
	 
	            -1.0f, 1.0f, 0.0f,
	            1.0f, 1.0f, 0.0f, 1.0f,
	 
	            0.0f, 0.0f, 2.0f,
	            1.0f, 1.0f, 0.0f, 1.0f,
	            
	            // pyramid base
	            // X, Y, Z,
	            // R, G, B, A
	    		1.0f, 1.0f, 0.0f,
	            0.5f, 0.5f, 0.5f, 1.0f,
	 
	            1.0f, -1.0f, 0.0f,
	            0.5f, 0.5f, 0.5f, 1.0f,
	 
	            -1.0f, -1.0f, 0.0f,
	            0.5f, 0.5f, 0.5f, 1.0f,
	    
	            -1.0f, 1.0f, 0.0f,
	            0.5f, 0.5f, 0.5f, 1.0f,
	            
	            1.0f, 1.0f, 0.0f,
	            0.5f, 0.5f, 0.5f, 1.0f
	    
		};
	 
	 
	    mPyramid = ByteBuffer.allocateDirect(pyramid.length * mBytesPerFloat)
	    .order(ByteOrder.nativeOrder()).asFloatBuffer();
	 

	    mPyramid.put(pyramid).position(0);
	}
	
	
	private void drawPyramid(final FloatBuffer aPyramidBuffer)
	{
	    // Pass in the position information
		 aPyramidBuffer.position(mPositionOffset);
		    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
		           mStrideBytes, aPyramidBuffer);
		 
		    GLES20.glEnableVertexAttribArray(mPositionHandle);
		 
		    // Pass in the color information
		    aPyramidBuffer.position(mColorOffset);
		    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
		            mStrideBytes, aPyramidBuffer);
		 
		    GLES20.glEnableVertexAttribArray(mColorHandle);
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 3, 3);
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 3);
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 9, 3);
		    
		    //w GLES20 niestety nie ma sta³ej GLES20.GL_QUADS  
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 12, 4);
		    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 14, 4);	  
	}
	

	@Override
	public void onDrawFrame(GL10 arg0) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
		

		final long elapsedTime = Math.abs(System.currentTimeMillis() - this.startTime); 
		float angle = ((float)(elapsedTime * Math.PI))/1000;
		
        final float eyeX = (float) (6.8f * Math.cos(angle));
        final float eyeY = (float) (6.0f * Math.sin(angle));
        final float eyeZ = 5.0f; 
     
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;
     
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
     
        Matrix.setLookAtM(CameraMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		
		Matrix.multiplyMM(ViewMatrix, 0, ProjectionMatrix, 0, CameraMatrix, 0);
		GLES20.glUniformMatrix4fv(MVPMatrixLocation, 1, false, ViewMatrix, 0);
        
		drawPyramid(mPyramid);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {   
	    GLES20.glViewport(0, 0, width, height);
	    
	    float ratio = (float) width / height;
	    
	    final float left = -ratio;
	    final float right = ratio;
	    final float bottom = -1.0f;
	    final float top = 1.0f;
	    final float near = 1.f;
	    final float far = 10.0f;
	 
	    Matrix.frustumM(ProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		this.startTime = System.currentTimeMillis();
		
	    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	    
	    //depth buffer
	    GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
        
        //direction for creating polygons
        GLES20.glFrontFace(GLES20.GL_CCW);
        
        //hiding back faces
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE); 
        
     
	    
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
	    
	    MVPMatrixLocation = GLES20.glGetUniformLocation(programHandle, "MVPMatrix");
	    if(MVPMatrixLocation == -1) {
	    	throw new RuntimeException("uniform MVPMatrix could not be found");
	    }
	    
	    mPositionHandle = GLES20.glGetAttribLocation(programHandle, "vVertex");  
	    mColorHandle = GLES20.glGetAttribLocation(programHandle, "vColor");

	    
	    // Tell OpenGL to use this program when rendering.
	    GLES20.glUseProgram(programHandle);
	}
}
