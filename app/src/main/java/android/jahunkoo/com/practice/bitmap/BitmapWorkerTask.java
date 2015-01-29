package android.jahunkoo.com.practice.bitmap;

/**
 * Created by Jahun Koo on 2015-01-29.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * 이미지를 불러와 imageView에 내려꽂아주는 역할을 수행
 */
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private Context mContext;
    private int data = 0;


    public BitmapWorkerTask(Context context, ImageView imageView) {
        this.mContext = context;
        this.imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return BitmapUtil.decodeSampledBitmapFromResource(mContext.getResources(), data, 100, 100);
    }

    /**
     * The last step is updating onPostExecute() in BitmapWorkerTask
     * so that it checks if the task is cancelled and if the current task matches the one associated with the ImageView
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap = null;
        }

        if(imageViewReference != null && bitmap != null){
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);

            if(this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * A BitmapDrawable is used so that a placeholder image can be displayed in the ImageView while the task completes
     * 즉 BitmapDrawable은 background process가 진행되는 동안 기본이미지(a placeholder image)를 보여주는 기능을 갖고 있다.
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        /**
         *
         * @param res
         * @param bitmap A placeholder image (로딩될동안 보여질 기본이미지)
         * @param bitmapWorkerTask
         */
        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            this.bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * 인자값으로 들어온 ImageView객체를 보고
     * ImageView -> (AsyncDrawable)Drawable -> BitmapWorkerTask가 같은 progress를 수행중인건지 판단한다.
     * 동일한 이미지를 불러오는 작업중이라면 false를, 그렇지 않은 경우에는 true를 반환한다.
     * @param data
     * @param imageView
     * @return
     */
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if(bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;

            //If bitmapData is not yet set or it differs from the new data
            if(bitmapData == 0 || bitmapData != data) {
                //Cancel previous task
                bitmapWorkerTask.cancel(true);
            }else {
                //The same work is already in progress
                return false;
            }
        }
        return true;
    }


    /**
     * A helper method, getBitmapWorkerTask(), is used above to retrieve the task associated with a particular ImageView
     * 인자값으로 들어온 ImageView안에 들어있는 Drawable객체가 AsyncDrawable이면, 그 안에 있는 BitmapWorkerTask를 반환한다.
     * @param imageView
     * @return BitmapWorkerTask
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if(imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof  AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }



}
