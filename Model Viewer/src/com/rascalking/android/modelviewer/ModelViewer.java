/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rascalking.android.modelviewer;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * This sample shows how to check for OpenGL ES 2.0 support at runtime, and then
 * use either OpenGL ES 1.0 or OpenGL ES 2.0, as appropriate.
 */
public class ModelViewer extends Activity implements SensorEventListener {
	private ViewSwitcher mViewSwitcher;
	private TextView mLoadingStatus;
    private GLSurfaceView mGLSurfaceView;
    private ModelRenderer mRenderer;
    private final static String TAG = "ModelViewer";
    
    private class ModelLoaderTask extends AsyncTask<String, String, RenderModel> {
		private ModelViewer mModelViewer;
		private final static String TAG = "ModelLoaderTask";

		public ModelLoaderTask(ModelViewer modelViewer) {
    		mModelViewer = modelViewer;    		
    	}
    	
		@Override
		protected void onPreExecute() {
			mModelViewer.setLoadingStatus("Loading...");
			mModelViewer.showLoading();
		}

		@Override
		protected RenderModel doInBackground(String... params) {
			String modelName = params[0];
			
			String msg = "Loading " + modelName;
			Log.d(TAG, msg);
			publishProgress(msg);
			
			ObjModel objModel = ObjLoader.loadObject(mModelViewer, modelName);
			msg = "ObjModel loaded, converting to RenderModel";
			Log.d(TAG, msg);
			publishProgress(msg);
			
			RenderModel renderModel = new RenderModel(objModel);
			msg = "Converted to RenderModel, displaying";
			Log.d(TAG, msg);
			publishProgress(msg);
			
			return renderModel;
		}
		
    	@Override
		protected void onPostExecute(RenderModel result) {
			Log.d(mModelViewer.TAG, "Model loaded");
    		mModelViewer.showModel(result);
		}

		@Override
		protected void onProgressUpdate(String... values) {
    		Log.d(mModelViewer.TAG, "Progress update: "+values[0]);
    		//mModelViewer.setLoadingStatus(values[0]);
		}
    }

    
    private void loadModel(int modelNameIndex) {
    	loadModel(getResources().getStringArray(R.array.models)[modelNameIndex]);
    }

    private void loadModel(String modelName) {
    	mRenderer = null;
    	mGLSurfaceView = null;
    	new ModelLoaderTask(this).execute(modelName);
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // no title bar, please
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // make the switcher the root view
        setContentView(R.layout.main);
        
        // find the view objects we care about
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
        mViewSwitcher.addView(getLayoutInflater().inflate(R.layout.loading, null), 0);
        mLoadingStatus = (TextView) findViewById(R.id.loading_view_status);
        
        // register for ambient light events
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> lightSensors = sm.getSensorList(Sensor.TYPE_LIGHT);
        for (int i=0; i<lightSensors.size(); i++)
        	sm.registerListener(this, lightSensors.get(i), SensorManager.SENSOR_DELAY_GAME);

        // load the first model we know about
        loadModel(0);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.select:
			new AlertDialog.Builder(this)
				.setTitle("Select a model")
				.setItems(R.array.models,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int i) {
						loadModel(i);
					}
				})
			.show();
			return true;
			// More items go here (if any) ...
		}
		return false;
	}

	@Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }
	@Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        if (mGLSurfaceView != null) {
        	mGLSurfaceView.onResume();
        }
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		// in a lit room at night, the intensity is ~ 0.003, so multiply by 100
		float intensity = 100.0f * event.values[0] / event.sensor.getMaximumRange();
		Log.i(TAG, "Light intensity changed to " + intensity);
		if (mRenderer != null) {
			mRenderer.setAmbientLight(intensity);
		}
	}
	
	public void setLoadingStatus(String status) {
		mLoadingStatus.setText(status);
	}
	
	public void showLoading() {
		mViewSwitcher.setDisplayedChild(0);
	}

	public void showModel(RenderModel model) {
    	Log.d(TAG, "Showing model");
    	
    	mRenderer = new ModelRenderer(this, model);
    	mGLSurfaceView = new TouchSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRenderer);
        if (mViewSwitcher.getChildCount() > 1) {
        	mViewSwitcher.removeViewAt(1);
        }
        mViewSwitcher.addView(mGLSurfaceView, 1);
        mViewSwitcher.setDisplayedChild(1);
    }

/*	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
	}*/
}
