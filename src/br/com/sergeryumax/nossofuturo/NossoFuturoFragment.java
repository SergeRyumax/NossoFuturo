package br.com.sergeryumax.nossofuturo;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


public class NossoFuturoFragment extends Fragment implements OnClickListener, OnCompletionListener{

	private View faceLeft;
	private View faceRight;
	private int widthCenter;
	private int heightScreen;
	private int widthScreen;
	private Button myVerFuturoBtn;
	private Handler myHandler;
	private View heartImg;
	private View initialContent;
	private View videoContent;
	private VideoView videoPlayerComp;
	private View video_content_mask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nosso_futuro,
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
		Bitmap pesoaRightRounded = ImageUtil.roundCornerImage(pessoaLeft, 360);
		
		((ImageView)faceRight).setImageBitmap(pesoaRightRounded);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		heightScreen = displaymetrics.heightPixels;
		widthScreen = displaymetrics.widthPixels;
		widthCenter = (widthScreen / 2) - 140;
	}

	private void prepareVideoSection(View rootView) {
		videoContent = rootView.findViewById(R.id.video_content);
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
				heartImg.animate().alpha(1.f).setDuration(500);
//				heartImg.animate().rotationYBy(180).setDuration(1500).start();
				
//				ObjectAnimator.ofFloat(heartImg, "marginTop", (-1)*heightScreen/2).start();
//				ObjectAnimator.ofFloat(heartImg, "marginBottom", (-1)*heightScreen/2).start();
//				ObjectAnimator.ofFloat(heartImg, "marginLeft", (-1)*widthScreen/2).start();
//				ObjectAnimator.ofFloat(heartImg, "marginRight", (-1)*widthScreen/2).start();
				
//				ObjectAnimator.ofInt(heartImg, "minimumHeight", heightScreen).start();
//				ObjectAnimator.ofInt(heartImg, "minimumWidth", widthScreen).start();
				
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
						resizeAnim.setDuration(2000);
						resizeAnim.setFillAfter(true);
						resizeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
						resizeAnim.setAnimationListener(new AnimationListener() {
							@Override public void onAnimationStart(Animation animation) {}
							@Override public void onAnimationRepeat(Animation animation) { }
							@Override
							public void onAnimationEnd(Animation animation) {
								initialContent.setVisibility(View.GONE);
								videoContent.setVisibility(View.VISIBLE);
								startVideo();
							}
						});
						heartImg.startAnimation(resizeAnim);
						initialContent.animate().alpha(0f).setDuration(1000).start();
					}
				});
				
			}
		}, 1500);
	}
	
	private void startVideo() {
		videoPlayerComp.requestFocus();
		videoPlayerComp.start();
		video_content_mask.animate().alpha(0f).setDuration(1000).start();
	}
	
	private void askQuestion() {
		video_content_mask.animate().alpha(1f).setDuration(300).start();
	}
	
}
