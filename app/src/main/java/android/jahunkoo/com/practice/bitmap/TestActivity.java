package android.jahunkoo.com.practice.bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.android.displayingbitmaps.R;

/**
 * Created by Jahun Koo on 2015-01-29.
 */
public class TestActivity extends Activity{

    private Bitmap mPlaceHolderBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceHolderBitmap = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.ic_launcher, 100, 100);
    }

    /**
     * ImageView에 bitmap을 넣어준다.
     * @param resId
     * @param imageView
     */
    /*
    // 이미지 로딩법 1단계
    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(this, imageView);
        task.execute(resId);
    }
    */
    //이미지 로딩법 2단계
    public void loadBitmap(int resId, ImageView imageView) {
        if(BitmapWorkerTask.cancelPotentialWork(resId, imageView)){ //일하지 않는 놈이라면
            BitmapWorkerTask task = new BitmapWorkerTask(this, imageView);
            final BitmapWorkerTask.AsyncDrawable asyncDrawable =
                    new BitmapWorkerTask.AsyncDrawable(getResources(),mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }




}
