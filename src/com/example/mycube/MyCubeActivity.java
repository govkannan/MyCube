package com.example.mycube;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MyCubeActivity extends Activity {

	private MyGLSurfaceView mySurfaceView;
	public static MyGameEngine myGameEngine;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mySurfaceView = new MyGLSurfaceView(this);
		
		MyGameEngine.parent = this;
		
		// find out if OPENGl ES2.0 is supported
		ActivityManager aManager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
		ConfigurationInfo info = aManager.getDeviceConfigurationInfo();
		
		if (info.reqGlEsVersion >= 0x20000) {
			
			mySurfaceView.setEGLContextClientVersion(2);
			mySurfaceView.setRenderer(new MyRenderer());
			
			mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			
			Thread  th = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							mySurfaceView.requestRender();
							Thread.sleep(40);
							
						}catch (Exception ex) { ex.printStackTrace();}
					}
					
				}
			});
			
			th.start();
			
		} else {
			// version 2 is not supported
		}
		
		
		setContentView(mySurfaceView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.my_cube, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mySurfaceView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mySurfaceView.onPause();
	}
	

}
