package com.example.mi.srtest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.image.sr.ImageSuperResolution;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;
import com.huawei.hiai.vision.visionkit.image.sr.SuperResolutionConfiguration;
import com.ksyun.ai.sr.Constants;
import com.ksyun.ai.sr.ImageAIKit;
import com.xiaomi.sr.models.MaceSRModel;

import java.io.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "SRTest-MainActivity";

    private ViewPager pager;
    private ArrayList<ImageBean> img = new ArrayList<>();
    private ImgAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        img = getPath();
        VisionBase.init(getApplicationContext(), ConnectManager.getInstance().getmConnectionCallback());
        init();
    }

    private void init(){
        pager = findViewById(R.id.viewpager);
        imgAdapter = new ImgAdapter(this, img);
        pager.setAdapter(imgAdapter);
        pager.setCurrentItem(0);
    }


    public class ImgAdapter extends PagerAdapter {

        private ArrayList<ImageBean> imgs;
        private Context context;

        public ImgAdapter(Context context, ArrayList imgs) {
            this.context = context;
            this.imgs = imgs;
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.imageiew,container,false);
            ImageView imageView1 = view.findViewById(R.id.img1);
            ImageView imageView2 = view.findViewById(R.id.img2);
            Bitmap bitmap = getBMP(imgs.get(position).getPath());
            Bitmap bitmap1 = ImageAIKit.processImage(bitmap, Constants.MODEL_TYPE_2X);
            Bitmap bitmap2 = MaceSRModel.processImage(bitmap, 0);
            imageView1.setImageBitmap(bitmap1);
            imageView2.setImageBitmap(bitmap2);
            container.addView(view);
            return view;
        }
    }

    private Bitmap getBMP(File file){
        BufferedInputStream in = null;
        Bitmap BMP = null;
        try{
            in = new BufferedInputStream(new FileInputStream(file));
            BMP = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "程序异常！", Toast.LENGTH_SHORT).show();
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return BMP;
    }

    private ArrayList<ImageBean> getPath(){
        ArrayList<ImageBean> al = new ArrayList<>();
        File myPhotos = new File("/sdcard/yuantu/");
        if (myPhotos.exists() && myPhotos.isDirectory()) {
            Log.e(TAG, "dir exists");
            File[] files = myPhotos.listFiles();
            if (files != null) {
                Log.e(TAG, "files exists and size = " + files.length);
                for (File file : files) {
                    if(file.exists() && file.isFile() && isImage(file)){
                        al.add(new ImageBean(file, 0));
                    }
                }
            }
        }
        return al;
    }

    private boolean isImage(File file){
        String[] strs = {".jpg",".png"};
        for (String str : strs) {
            Log.e(TAG, file.getName());
            if (file.getName().endsWith(str)) {
                return true;
            }
        }
        return false;
    }

    private Bitmap getHuaWeiSR(Bitmap bitmap) {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);

        // 创建超分对象
        // Create SR object
        ImageSuperResolution superResolution = new ImageSuperResolution(this);

        // 准备超分配置
        // Prepare SR configuration
        SuperResolutionConfiguration paras = new SuperResolutionConfiguration(
                SuperResolutionConfiguration.SISR_SCALE_3X,
                SuperResolutionConfiguration.SISR_QUALITY_HIGH);

        // 设置超分
        // Config SR
        superResolution.setSuperResolutionConfiguration(paras);

        // 执行超分
        // Run SR
        ImageResult result = superResolution.doSuperResolution(frame, null);
        return result.getBitmap();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
