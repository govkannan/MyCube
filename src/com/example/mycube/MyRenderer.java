package com.example.mycube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class MyRenderer implements Renderer {
	
	private MyCube myCube;
	
	private float[] projectMatrix = new float[16];

	public MyRenderer() {
		
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		 GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		 if (myCube == null) {
			 myCube = new MyCube();
		 }
		 myCube.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		    GLES20.glViewport(0, 0, width, height);
		    
		    MyGameEngine.myFrustum = new MyFrustum(width, height);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		
		GLES20.glClearColor(0f, 0f, 0f, 1.0f);
		MyGameEngine.myCamera = new MyCamera();
		
	}

}
