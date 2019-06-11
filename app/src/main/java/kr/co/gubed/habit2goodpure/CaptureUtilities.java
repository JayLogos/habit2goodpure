package kr.co.gubed.habit2goodpure;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

class CaptureUtilities {
    private final String PATH_CAPTURE = "/habit2good";

    public void captureView(View view) {
        view.buildDrawingCache();
        Bitmap mCaptureView = view.getDrawingCache();
        FileOutputStream mFileOutputStream;

        String mFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_CAPTURE;
        File mFolder = new File(mFolderPath);
        if(!mFolder.exists()) {
            mFolder.mkdirs();
        }

        String mFilePath = mFolderPath + "/" + System.currentTimeMillis() + ".png";
        File mFileCacheItem = new File(mFilePath);
        try {
            mFileOutputStream = new FileOutputStream(mFileCacheItem);
            mCaptureView.compress(Bitmap.CompressFormat.PNG, 100, mFileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void captureMyRecyclerView(RecyclerView view, int bgColor, int startPosition, int endPosition) {
        Log.i(getClass().getName(), "captureMyRecyclerView startPosition="+startPosition+", endPosition="+endPosition);
        //RecyclerView.Adapter adapter = view.getAdapter();
        HabitItemAdapter adapter = (HabitItemAdapter) view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
/*
            if ( startPosition > endPosition ){
                int tmp = endPosition;
                endPosition = startPosition;
                startPosition = tmp;
            }
*/

            int size = (endPosition - startPosition) + 1;
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);
            for (int i = startPosition; i < (endPosition + size); i++) {
                //RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                HabitItemAdapter.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.ll_expand.setVisibility(View.VISIBLE);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                if(bgColor != 0)
                    holder.itemView.setBackgroundColor(bgColor);
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmapCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            /*
             */
            Log.i(getClass().getName(), "size="+size);
            //for (int i = 0; i < size; i++) {
            for (int i = startPosition; i < (startPosition+size); i++) {
                Bitmap bitmap = bitmapCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }

        String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_CAPTURE;
        Log.i(getClass().getName(), "strFolderPath="+strFolderPath);
        File folder = new File(strFolderPath);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        String strFilePath = strFolderPath + "/" + "h2g_" + System.currentTimeMillis() + ".png";
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            Objects.requireNonNull(bigBitmap).compress(Bitmap.CompressFormat.PNG, 100, out);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+strFilePath));
            view.getContext().sendBroadcast(intent);

            Toast.makeText(view.getContext(), "캡쳐를 완료했습니다. 갤러리에서 확인하세요.", Toast.LENGTH_LONG).show();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(strFilePath));
            view.getContext().startActivity(Intent.createChooser(shareIntent, "공유하기"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            try {
                Objects.requireNonNull(out).close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
