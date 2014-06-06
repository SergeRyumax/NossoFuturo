package br.com.sergeryumax.nossofuturo;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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


public class NossoFuturoFragment extends Fragment implements OnClickListener, OnCompletionListener{

	private static final String TAG = "NossoFuturoFragment";
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_nosso_futuro,
				container, false);
		
		myHandler = new Handler();
		
		prepareInitialSection(rootView);
		prepareVideoSection(rootView);
		
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

	private void prepareVideoSection(View rootView) {
		mPreviewLayer = (FrameLayout) rootView.findViewById(R.id.camera_preview_layer);
		mMicroPreviewLayer = (FrameLayout) rootView.findViewById(R.id.camera_preview_micro_layer);
		videoContent = (RelativeLayout)rootView.findViewById(R.id.video_content);
		video_content_mask = rootView.findViewById(R.id.video_content_mask);
		
		videoPlayerComp = (VideoView) rootView.findViewById(R.id.videoPlayerComponent);
		videoPlayerComp.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.video_teste));
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
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		askQuestion();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ver_futuro_btn){
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
        cameraPreview = new CameraPreview(getActivity(), mCamera);
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
	public void onDestroyView() {
		if (cameraPreview != null)
			cameraPreview.stopPreviewAndFreeCamera();
		super.onDestroyView();
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
	}
	
}
