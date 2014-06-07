package br.com.sergeryumax.nossofuturo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;


public class NossoFuturoFragment extends Fragment implements OnClickListener, OnCompletionListener, RecordCallback{

	private static final String TAG = "NossoFuturoFragment";
	private static final int MEDIA_TYPE_VIDEO = 1000;
	private static final int MEDIA_TYPE_IMAGE = 2000;
	private View faceLeft;
	private View faceRight;
	private int widthCenter;
	private int heightScreen;
	private int widthScreen;
	private Button myVerFuturoBtn;
	private Handler myHandler;
	private View heartImg;
	private View initialContent;
	private RelativeLayout videoContent;
	private VideoView videoPlayerComp;
	private View video_content_mask;
	private Camera mCamera;
	private FrameLayout mPreviewLayer;
	private FrameLayout mMicroPreviewLayer;
	private View rootView;
	private CameraPreview cameraPreview;
	private Camera myCamera;
	private MediaRecorder mMediaRecorder;
	private boolean isRecording = false;
	private static Uri mCurrentVideoUri;
	private MediaPlayer musicIntroPlayer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_nosso_futuro,
				container, false);
		
		myHandler = new Handler();
		
		prepareInitialSection(rootView);
		prepareVideoSection(rootView, Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.video_teste));
		
		return rootView;
	}

	private void prepareInitialSection(View rootView) {
		initialContent = rootView.findViewById(R.id.initial_content);
		myVerFuturoBtn = (Button) rootView.findViewById(R.id.ver_futuro_btn);
		myVerFuturoBtn.setOnClickListener(this);
		
		
		heartImg = rootView.findViewById(R.id.heart_img);
		faceLeft = rootView.findViewById(R.id.face_left_img);
		faceRight = rootView.findViewById(R.id.face_right_img);
		
		Bitmap pessoaLeft = BitmapFactory.decodeResource(getResources(), R.drawable.pessoa_left);
		Bitmap pesoaLeftRounded = ImageUtil.roundCornerImage(pessoaLeft, 360);
		
		((ImageView)faceLeft).setImageBitmap(pesoaLeftRounded);
		
		Bitmap pessoaRight= BitmapFactory.decodeResource(getResources(), R.drawable.pessoa_left);
		Bitmap pesoaRightRounded = ImageUtil.roundCornerImage(pessoaRight, 360);
		
		((ImageView)faceRight).setImageBitmap(pesoaRightRounded);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		heightScreen = displaymetrics.heightPixels;
		widthScreen = displaymetrics.widthPixels;
		widthCenter = (widthScreen / 2) - 140;
		
		Log.d(TAG, "Tamanho tela: " + widthScreen + ":" + heightScreen);
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
		        mCamera = getCameraInstance();
		        return null;
			}
		}.execute();
	}

	private void prepareVideoSection(View rootView, Uri videoUri) {
		mPreviewLayer = (FrameLayout) rootView.findViewById(R.id.camera_preview_layer);
		mMicroPreviewLayer = (FrameLayout) rootView.findViewById(R.id.camera_preview_micro_layer);
		videoContent = (RelativeLayout)rootView.findViewById(R.id.video_content);
		video_content_mask = rootView.findViewById(R.id.video_content_mask);
		
		videoPlayerComp = (VideoView) rootView.findViewById(R.id.videoPlayerComponent);
		videoPlayerComp.setVideoURI(videoUri);
		final MediaController mediaController = new MediaController(getActivity());
		videoPlayerComp.setMediaController(mediaController);
		videoPlayerComp.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				int childs = mediaController.getChildCount();
			    for (int i = 0; i < childs; i++)
			    {
			        View child = mediaController.getChildAt (i);
			        child.setVisibility (View.GONE);
			    }
			}
		});
		
		videoPlayerComp.setOnCompletionListener(this);
	}
	
	private void prepareMusicIntro() {
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				
				musicIntroPlayer = MediaPlayer.create(getActivity(), R.raw.music_intro);
				musicIntroPlayer.setWakeMode(getActivity().getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
				
		        try {
		            musicIntroPlayer.prepare();
		        } catch (IllegalStateException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }

		        musicIntroPlayer.start();
			}
		});
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		askQuestion();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ver_futuro_btn){
			prepareMusicIntro();
			playAnimation();
		}
	}

	private void playAnimation() {
		myVerFuturoBtn.animate().alpha(0f).setDuration(500).start();
		faceLeft.animate().x(widthCenter - faceLeft.getWidth()/2).setDuration(2000).start();
		faceRight.animate().x(widthCenter + faceRight.getWidth()/2).setDuration(2000).start();
		
		myHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				heartImg.setVisibility(View.VISIBLE);
				heartImg.animate().alpha(1.f).setDuration(500).start();
				
				Animation resizeAnim = new ResizeAnimation(heartImg, (int)(heightScreen*0.8f), (int)(widthScreen*0.8f));
				resizeAnim.setDuration(2000);
				resizeAnim.setFillAfter(true);
				resizeAnim.setInterpolator(new DecelerateInterpolator());
				heartImg.startAnimation(resizeAnim);
				resizeAnim.setAnimationListener(new AnimationListener() {
					@Override public void onAnimationStart(Animation animation) {}
					@Override public void onAnimationRepeat(Animation animation) { }
					@Override
					public void onAnimationEnd(Animation animation) {
						Animation resizeAnim = new ResizeAnimation(heartImg, 10, 10);
						resizeAnim.setDuration(1300);
						resizeAnim.setFillAfter(true);
						resizeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
						resizeAnim.setAnimationListener(new AnimationListener() {
							@Override public void onAnimationStart(Animation animation) {}
							@Override public void onAnimationRepeat(Animation animation) { }
							@Override
							public void onAnimationEnd(Animation animation) {
								initialContent.setVisibility(View.GONE);
								videoContent.setVisibility(View.VISIBLE);
								video_content_mask.setVisibility(View.VISIBLE);
								startVideoSection();
							}
						});
						heartImg.startAnimation(resizeAnim);
						initialContent.animate().alpha(0f).setDuration(800).start();
					}
				});
				
			}
		}, 1500);
	}
	
	private void startVideoSection() {
        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(getActivity(), mCamera, this);
        mPreviewLayer.setVisibility(View.VISIBLE);
        mPreviewLayer.addView(cameraPreview);
        
		video_content_mask.animate().alpha(0f).setDuration(1000).withEndAction(new Runnable() {
			@Override
			public void run() {
				myHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						video_content_mask.animate().alpha(1f).setDuration(300).withEndAction(new Runnable() {
							@Override
							public void run() {
								cameraPreview.setCanRecord(true);
								mPreviewLayer.removeAllViews();
								videoContent.removeView(mPreviewLayer);
								mMicroPreviewLayer.addView(cameraPreview);
								videoContent.requestLayout();
								rootView.requestLayout();
								video_content_mask.animate().alpha(0f).setDuration(500).withEndAction(new Runnable() {
									@Override
									public void run() {
										startVideoPlayback();
									}
								}).start();
							}
						}).start();
					}
				}, 2500);
			}
		});
	}
	
	@Override
	public void onPause() {	
		releaseMediaRecorder();
		if (cameraPreview != null)
			cameraPreview.stopPreviewAndFreeCamera();
		
		setVolumeDownGradually();
		
		super.onPause();
	}

	private void setVolumeDownGradually() {
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
				int streamVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
				
				for (int i = streamVolume; i >= 0; i--) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					audio.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
				}
				musicIntroPlayer.stop();
				musicIntroPlayer.release();
			}
		});
	}
	
	private void startVideoPlayback() {
		getActivity().findViewById(R.id.video_content_mask_in).setVisibility(View.VISIBLE);
		videoPlayerComp.setVisibility(View.VISIBLE);
		videoPlayerComp.requestFocus();
		videoPlayerComp.start();
		getActivity().findViewById(R.id.video_content_mask_in).animate().alpha(0f).setDuration(1000).start();
	}
	
	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance(){
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT){
			    try {
			        myCamera = Camera.open(i); // attempt to get a Camera instance
			    }
			    catch (Exception e){
			        // Camera is not available (in use or does not exist)
			    }
			}
		}
		
		if (myCamera == null){
			myCamera = Camera.open();
		}
	    return myCamera; // returns null if camera is unavailable
	}
	
	private void askQuestion() {
		video_content_mask.animate().alpha(1f).setDuration(300).start();
		startStopRecording();
	}
	
	private boolean prepareVideoRecorder(){
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MemesForever");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MemesForever", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Amoi_CasaComigo_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Amoi_CasaComigo_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    mCurrentVideoUri = Uri.fromFile(mediaFile);
	    return mediaFile;
	}

	private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
	
	private void galleryAddPic() {
		Log.d(TAG, "Adiciona na galeria: " + mCurrentVideoUri.getPath());
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    mediaScanIntent.setData(mCurrentVideoUri);
	    this.getActivity().sendBroadcast(mediaScanIntent);
	}
	
	private void startStopRecording(){
		if (isRecording) {
			cameraPreview.setCanRecord(false);
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            galleryAddPic();
            Toast.makeText(getActivity(), "Gravado", Toast.LENGTH_SHORT).show();
            isRecording = false;
        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
	}

	@Override
	public void startRecord() {
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				startStopRecording();
			}
		});
	}
}
