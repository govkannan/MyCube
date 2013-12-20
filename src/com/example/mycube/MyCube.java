package com.example.mycube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyCube {
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;
	private FloatBuffer mCubeTextureCoordinates;
	
	static final int COORDS_PER_VERTEX = 3;
	
	final String vertexShaderCode =
       "uniform mat4 u_MVPMatrix;      \n"                // A constant representing the combined model/view/projection matrix.
	 + "attribute vec2 a_TexCoordinate;   \n"			// Per-vertex texture coordinate information we will pass in.		
            
      + "attribute vec4 a_position;     \n"                // Per-vertex position information we will pass in.
      + "attribute vec4 a_color;        \n"                // Per-vertex color information we will pass in.                          
      + "varying vec2 v_TexCoordinate;  \n"  // This will be passed into the fragment shader."
      + "varying vec4 v_color;          \n"                // This will be passed into the fragment shader.
      
      + "void main()                    \n"                // The entry point for our vertex shader.
      + "{                              \n"
      + "   v_color = a_color;          \n"                // Pass the color through to the fragment shader. 
      + " v_TexCoordinate = a_TexCoordinate;  \n"      // It will be interpolated across the triangle.
      + "   gl_Position = u_MVPMatrix   \n"         // gl_Position is a special variable used to store the final position.
      + "               * a_position;   \n"     // Multiply the vertex by the matrix to get the final point in                                                                                              
      + "}                              \n";    // normalized screen coordinates.
	
	final String fragmentShaderCode =
		    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
            // precision in the fragment shader.
			+ " uniform sampler2D u_Texture;   \n" // The input texture."            // triangle per fragment.
			+ "varying vec4 v_color;          \n"     // This is the color from the vertex shader interpolated across the
			+ "varying vec2 v_TexCoordinate;  \n"   // Interpolated texture coordinate per fragment."
			+ "void main()                    \n"     // The entry point for our fragment shader.
			+ "{                              \n"
			+ "   gl_FragColor = v_color * texture2D(u_Texture, v_TexCoordinate);     \n"     // Pass the color directly through the pipeline.c
//			+ "   gl_FragColor = v_color;      \n"     // Pass the color directly through the pipeline.c
			+ "}                              \n";
	private float[] vertices = 
		{
//            -0.5f, -0.5f, -0.5f,
////            1.0f, 0.0f, 0.0f, 1.0f,
// 
//            0.5f, -0.5f, -0.5f,
////            0.0f, 0.0f, 1.0f, 1.0f,
// 
//            0.5f, 0.5f, -0.5f,
////            0.0f, 1.0f, 0.0f, 1.0f,
//            
//            -0.5f, 0.5f, -0.5f
////            1.0f, 1.0f, 0.0f, 1.0f
//
//			
//            -0.5f, -0.5f, 0.5f,
////          1.0f, 0.0f, 0.0f, 1.0f,
//
//          0.5f, -0.5f, 0.5f,
////          0.0f, 0.0f, 1.0f, 1.0f,
//
//          0.5f, 0.5f, 0.5f,
////          0.0f, 1.0f, 0.0f, 1.0f,
//          
//          -0.5f, 0.5f, 0.5f
////          1.0f, 1.0f, 0.0f, 1.0f

            -0.7f, -0.7f, -0.7f,
            0.0f,  1.0f,  0.0f,  1.0f,
            
            0.7f, -0.7f, -0.7f,
            1.0f,  0.5f,  0.0f,  1.0f,
            
            0.7f,  0.7f, -0.7f,
            1.0f,  0.5f,  0.0f,  1.0f,
            
            -0.7f, 0.7f, -0.7f,
            1.0f,  0.0f,  0.0f,  1.0f,
            
            -0.7f, -0.7f,  0.7f,
            1.0f,  0.0f,  0.0f,  1.0f,
            
            0.7f, -0.7f,  0.7f,
            0.0f,  0.0f,  1.0f,  1.0f,
            
            0.7f,  0.7f,  0.7f,
            1.0f,  0.0f,  1.0f,  1.0f,
            
            -0.7f,  0.7f,  0.7f,
            1.0f,  1.0f,  1.0f,  1.0f
	            
	            
		};
	
//	private short[] drawOrder = { 0,1,3,1,2,3, 
//			7,6,4, 6,5,4
//			};
	
	private short[] drawOrder = { 
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,   // something wrong
            3, 7, 4, 3, 4, 0,
            4, 7, 6, 4, 6, 5,
            3, 0, 1, 3, 1, 2
            };
	
	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[] cubeTextureCoordinateData =
	{
	        // Front face
	        0.0f, 1.0f,
	        0.0f, 0.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f,

	        1.0f, 0.0f,
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 1.0f,

	        1.0f, 0.0f,
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f,
	        0.0f, 1.0f,
	        
	        0.0f, 1.0f,
	        1.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 0.0f,

	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 1.0f,
	        0.0f, 0.0f,
	        1.0f, 1.0f,
	        1.0f, 0.0f,

	        1.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 0.0f,
	        1.0f, 1.0f,
	        0.0f, 0.0f,
	        0.0f, 1.0f
	};
	 
	
	private int glProgram =0, mPositionHandle, mMVPMatrixHandle,mColorHandle, aTextureHandle, mTextureUniformHandle ;
	
	private int mTextureDataHandle;
	
	private float[] color = { 1.0f, 1.0f, 1.0f, 1.0f};
	private float[] mMVPMatrix = new float[16];
	
	private float[]   mModelMatrix = new float[16];
	
	private float scalevalue = 1.0f;
	private float scaleInc = -0.01f;
	

	
	
	
	public MyCube() {
		vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(vertices).position(0);
		
		drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		drawListBuffer.put(drawOrder).position(0);
		
	     mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4)
	                .order(ByteOrder.nativeOrder()).asFloatBuffer();
	     mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
		
		 int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		 int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		 glProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
		 GLES20.glAttachShader(glProgram, vertexShader);   // add the vertex shader to program
		 GLES20.glAttachShader(glProgram, fragmentShader); // add the fragment shader to program
		 GLES20.glLinkProgram(glProgram);
		 
		  final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(glProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(glProgram);
		        glProgram = 0;
		    }
		 
		if (glProgram == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}
		 
		 mMVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "u_MVPMatrix");
		 mPositionHandle = GLES20.glGetAttribLocation(glProgram, "a_position");
		 mColorHandle = GLES20.glGetAttribLocation(glProgram, "a_color");
		 aTextureHandle  = GLES20.glGetAttribLocation(glProgram, "a_TexCoordinate");
		 mTextureUniformHandle = GLES20.glGetUniformLocation(glProgram, "u_Texture");
		 
		 
		 mTextureDataHandle  = loadTexture(R.drawable.lord_balaji);
	}
	
	
	int loadShader (int shaderType, String shaderString) {
		
		 // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(shaderType);
	    
	    

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderString);
	    GLES20.glCompileShader(shader);
	    
	    final int[] compileStatus = new int[1];
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	 
	    // If the compilation failed, delete the shader.
	    if (compileStatus[0] == 0)
	    {
	        GLES20.glDeleteShader(shader);
	        shader = 0;
	    }
	 
	    if (shader == 0)
	    {
	    	throw new RuntimeException("Error creating  shader." + shaderString );
	    }

	    return shader;
	}
	
	public void draw() {
	    // Add program to OpenGL ES environment
		
		GLES20.glDisable(GLES20.GL_CULL_FACE);
	    GLES20.glUseProgram(glProgram);

	    // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);    	    
	    
	    // Pass in the position information
	    vertexBuffer.position(0);
	    
	    GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
	            28, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
//	    // Pass in the color information
	    vertexBuffer.position(3);
	    GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
	            28, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	    
	    mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false, 
                        0, mCubeTextureCoordinates);
        
        GLES20.glEnableVertexAttribArray(aTextureHandle);	    
	    
//	    GLES20.glUniform4fv(mColorHandle, 1, color, 0);
	    
        Matrix.setIdentityM(mModelMatrix, 0);
        
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        
//        Matrix.scaleM(m, mOffset, x, y, z)
        
//        scalevalue+=scaleInc;
//        if (scalevalue >= 1.0) {
//        	scaleInc = -0.01f;
//        } else if (scalevalue <= 0.1) {
//        	scaleInc = 0.01f;
//        }        
//        Matrix.scaleM(mModelMatrix, 0, scalevalue, scalevalue, scalevalue);
//
        Matrix.rotateM(mModelMatrix, 0, -angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, -angleInDegrees, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, -angleInDegrees, 0.0f, 0.0f, 1.0f);
        

	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix , 0, MyGameEngine.myCamera.mViewMatrix, 0,
	    		mModelMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
//        Matrix.setIdentityM(MyGameEngine.myFrustum.projectMatrix, 0);

	    Matrix.multiplyMM(mMVPMatrix, 0, MyGameEngine.myFrustum.projectMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
//	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
	    drawListBuffer.position(0);
	    
	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, 
	    		drawListBuffer);
//	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	    }
	
	
	public static int loadTexture(final int resourceId)
	{
	    final int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(MyGameEngine.parent.getResources(), resourceId, options);
	 
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    return textureHandle[0];
	}
}
