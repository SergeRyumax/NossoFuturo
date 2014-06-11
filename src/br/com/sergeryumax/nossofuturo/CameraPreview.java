package br.com.sergeryumax.nossofuturo;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
	private SurfaceHolder mHolder;
    private Camera mCamera;
	private boolean canRecord = false;
	private RecordCallback recordCallback;

    public void setCanRecord(boolean canRecord){
		this.canRecord = canRecord;
    }
    public CameraPreview(Context context, Camera camera, RecordCallback recordCallback) {
        super(context);
        mCamera = camera;
		this.recordCallback = recordCallback;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    	Log.d(TAG, "SURFACE surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	if (!canRecord){
        		mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
        	}	
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.d(TAG, "SURFACE surfaceDestroyed");
//        mCamera.stopPreview();
    }
    
    public void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
        
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	if (!canRecord){
    		// If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
        	Log.d(TAG, "SURFACE surfaceChanged");
            if (mHolder.getSurface() == null){
              // preview surface does not exist
              return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
              // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
    	} else {
    		recordCallback.startRecord();
    	}
        
    }
	public void setCamera(Camera mCamera) {
		this.mCamera = mCamera;
	}
}
