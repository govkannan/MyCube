package com.example.mycube;

import android.opengl.Matrix;

public class MyCamera {
	
	 	float eyeX = 0.0f;
	    float eyeY = 0.0f;
	    float eyeZ = 7.0f;
	 
	    // We are looking toward the distance
	    float centerX = 0.0f;
	    float centerY = 0.0f;
	    float centerZ = -5.0f;
	 
	    // Set our up vector. This is where our head would be pointing were we holding the camera.
	     float upX = 0.0f;
	     float upY = 1.0f;
	     float upZ = 0.0f;
	     
	     public float[] mViewMatrix = new float[16];
	     
	     public MyCamera() {
	    	 Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	     }
	     
	     public float[] init() {
		     Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		     return mViewMatrix;
	     }
	     
	     public float[] getViewMatrix() {
	    	 return mViewMatrix;
	     }
	     
	 
}
