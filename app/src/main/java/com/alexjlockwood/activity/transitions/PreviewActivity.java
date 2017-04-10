package com.alexjlockwood.activity.transitions;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;
import static com.alexjlockwood.activity.transitions.Constants.ALBUM_NAMES;
import static com.alexjlockwood.activity.transitions.MainActivity.EXTRA_CURRENT_ALBUM_POSITION;
import static com.alexjlockwood.activity.transitions.MainActivity.EXTRA_STARTING_ALBUM_POSITION;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     2017/4/6
 * Description:
 * Fix History:
 * =============================
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName()+"Test";
    private static final boolean DEBUG = false;

    private static final String STATE_CURRENT_PAGE_POSITION = "state_current_page_position";

    private DetailsFragment mCurrentDetailsFragment;
    private int mCurrentPosition;
    private int mStartingPosition;
    private boolean mIsReturning;
    public boolean mIsEntering = false;
    private ImagePageAdapter mImagePageAdapter;
    private ViewPager mViewPager;
   // private ImageView shareImageView;//用于实现共享动画的ImageView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initShareEnter();
        setContentView(R.layout.activity_preview);
        mStartingPosition = getIntent().getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0);
        if (savedInstanceState == null) {
            mCurrentPosition = mStartingPosition;
        } else {
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mImagePageAdapter = new ImagePageAdapter(this);
        mImagePageAdapter.setStartPosition(mCurrentPosition);
        mViewPager.setAdapter(mImagePageAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });


        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(TAG,"onTransitionStart:"+new Date().toString());
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d(TAG,"onTransitionEnd:"+new Date().toString());
                if(mIsEntering){
                   mImagePageAdapter.handlerEnterEnd();
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    /**
     *初始化共享元素
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initShareEnter(){
        if(!Utils.isLOLLIPOP())
            return;
        mIsEntering = true;
       //shareImageView = (ImageView) this.findViewById(R.id.shareImageView);

        final SharedElementCallback mCallback = new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                //返回动画，重新绑定
                if(mIsReturning ){
//                    final String shareName = ALBUM_NAMES[mCurrentPosition];
                    //View view = mImagePageAdapter.getShareViewByPosition(mViewPager,mCurrentPosition);
                    View view = mImagePageAdapter.shareView;
                    if(view != null){
                        names.clear();
                        names.add(view.getTransitionName());
                        sharedElements.clear();
                        sharedElements.put(view.getTransitionName(),view);
                    }
                }
            }
         };
        this.postponeEnterTransition();
        this.setEnterSharedElementCallback(mCallback);

      //  String url = ALBUM_IMAGE_URLS[mCurrentPosition];
      //  handlerEnterStart(url);
    }


    @Override
    public void onBackPressed() {
       mImagePageAdapter.handlerExitStart(mViewPager,mCurrentPosition);
        super.onBackPressed();
    }

    @Override
    public void finishAfterTransition() {
        Log.d(TAG,"finishAfterTransition"+new Date().toString());
        mIsReturning = true;
        Intent data = new Intent();
        data.putExtra(EXTRA_STARTING_ALBUM_POSITION, mStartingPosition);
        data.putExtra(EXTRA_CURRENT_ALBUM_POSITION, mCurrentPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }


    /**
     * 处理进入动画
     */
//    public void handlerEnterStart(String url){
//        mViewPager.setVisibility(View.GONE);
//        shareImageView.setVisibility(View.VISIBLE);
//
//    }

//    public void handlerEnterEnd(){
//        shareImageView.setImageDrawable(null);
//        shareImageView.setVisibility(View.GONE);
//        mViewPager.setVisibility(View.VISIBLE);
//    }

//    public void handlerExitStart(){
//        mViewPager.setVisibility(View.GONE);
//        mViewPager.removeAllViews();
//
//        String albumImageUrl = ALBUM_IMAGE_URLS[mCurrentPosition];
//        Picasso.with(this).load(albumImageUrl).into(shareImageView);
//        shareImageView
//        shareImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        shareImageView.setVisibility(View.VISIBLE);
//
//    }


    public static class ImagePageAdapter extends PagerAdapter{
        private LayoutInflater mLayoutInflater;
        private WeakReference<PreviewActivity> mWeakReference ;
        private int startPosition;
        public ImagePageAdapter(PreviewActivity previewActivity){
            this.mLayoutInflater = LayoutInflater.from(previewActivity);
            mWeakReference  = new WeakReference<PreviewActivity>(previewActivity);
        }
        private ImageView shareView;

        public void setStartPosition(int position){
            startPosition = position;
        }

        @Override
        public int getCount() {
            return Constants.ALBUM_NAMES.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

       // private Map<Integer,PhotoViewAttacher> mPhotoViewAttacherMap = new HashMap<>();

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
           // final ImageView view =  (ImageView) mLayoutInflater.inflate(R.layout.adapter_preview,container,false);
            PhotoView view = new PhotoView(mWeakReference.get());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);


            String albumImageUrl = ALBUM_IMAGE_URLS[position];
            String albumName = ALBUM_NAMES[position];
            if(Utils.isLOLLIPOP())
                view.setTransitionName(albumName);

            if(mWeakReference.get().mIsEntering && position == startPosition){
                shareView = view;
                //Log.d(TAG,"进入详情页："+view.getTransitionName());
                RequestCreator albumImageRequest = Picasso.with(mWeakReference.get()).load(albumImageUrl);
                //渲染图片成功
                albumImageRequest.into(shareView, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });
            }
            else{
                Picasso.with(mWeakReference.get()).load(albumImageUrl).into(view);
               // PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(view);
                //mPhotoViewAttacherMap.put(position,photoViewAttacher);
                //view.requestLayout();
            }


            view.setTag(position);
            container.addView(view);
            return view;
        }

        public ImageView getShareViewByPosition(ViewPager viewPager,int position){
            ImageView view = (ImageView)viewPager.findViewWithTag(position);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ViewGroup.LayoutParams params  = view.getLayoutParams();
            Log.d(TAG,"width"+params.width);
            Log.d(TAG,"height"+params.height);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        private void startPostponedEnterTransition() {
            if(shareView == null)
                return;
            shareView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        shareView.getViewTreeObserver().removeOnPreDrawListener(this);
                        mWeakReference.get().startPostponedEnterTransition();
                        return true;
                    }
                });
        }

        /**
         * 进入动画结束
         */
        public void handlerEnterEnd() {
            if(shareView == null)
                return;
           // PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(shareView);
           // mPhotoViewAttacherMap.put(startPosition,photoViewAttacher);
        }

        public void handlerExitStart(ViewPager viewPager,int position){
            shareView = getShareViewByPosition(viewPager,position);
            //PhotoViewAttacher photoViewAttacher = mPhotoViewAttacherMap.get(position);
            //shareView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //photoViewAttacher = null;
            //photoViewAttacher.cleanup();
           // photoViewAttacher = null;
        }


    }

}
