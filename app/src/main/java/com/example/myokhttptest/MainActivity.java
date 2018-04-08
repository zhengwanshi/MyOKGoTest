package com.example.myokhttptest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myokhttptest.R.id.iv_icon;
import static com.example.myokhttptest.R.id.tv_result;

public class MainActivity extends AppCompatActivity {

    private static final int GET = 1;
    private static final int POST =2 ;
    private static final String TAG =MainActivity.class.getSimpleName() ;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Bind(R.id.btn_get_post)
    Button mBtnGetPost;
    @Bind(R.id.btn_get_okhttputils)
    Button mBtnGetOkhttputils;
    @Bind(R.id.btn_downloadfile)
    Button mBtnDownloadfile;
    @Bind(R.id.btn_uploadfile)
    Button mBtnUploadfile;
    @Bind(R.id.btn_image)
    Button mBtnImage;
    @Bind(R.id.btn_image_list)
    Button mBtnImageList;
    @Bind(tv_result)
    TextView mTvResult;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(iv_icon)
    ImageView mIvIcon;
  private OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_get_post, R.id.btn_get_okhttputils, R.id.btn_downloadfile, R.id.btn_uploadfile, R.id.btn_image, R.id.btn_image_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_post://使用原生的okhttp请求网络数据，get和post
                mTvResult.setText("");
                getDataFromPost();//点击事件
                break;
            case R.id.btn_get_okhttputils:

                getDataPostByOkhttpUtils();
                break;
            case R.id.btn_downloadfile://下载文件
                downloadFile();
                break;
            case R.id.btn_uploadfile://文件上传
                multiFileUpload();
                break;
            case R.id.btn_image://请求单张图片
                getImage();
                break;
            case R.id.btn_image_list://请求列表中的图片
                Intent intent = new Intent(MainActivity.this,OKHttpListActivity.class);
                startActivity(intent);

                break;
        }
    }

    private void getImage() {
        mTvResult.setText("");
        String url = "http://images.csdn.net/20150817/1.jpg";

        OkGo.<Bitmap>get(url)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<Bitmap> response) {
                        mIvIcon.setImageBitmap(response.body());
                    }
                });
    }

    private void multiFileUpload() {
        String mBaseUrl = "http://192.168.155.1:8080/FileUpload/FileUploadServlet";
        File file = new File(Environment.getExternalStorageDirectory(), "meinv1.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory(), "video.mp4");
        if (!file.exists()||!file2.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();


        String url = mBaseUrl ;

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    OkGo.post("http://192.168.155.1:8080/FileUpload/FileUploadServlet")
                            .params("mFile",new File(Environment.getExternalStorageDirectory(), "meinv1.jpg"))
                            .params("mFile1",  new File(Environment.getExternalStorageDirectory(), "video.mp4"))
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void downloadFile() {
        String url = "http://vfx.mtime.cn/Video/2016/07/24/mp4/160724055620533327_480.mp4";
//

        OkGo.<File>get( url)
                .execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), "OkGO-test.mp4") {


                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                        Toast.makeText(MainActivity.this, "文件下载成功", Toast.LENGTH_SHORT).show();
                    }

                    //progress.fraction获取当前的下载进度
                    @Override
                    public void downloadProgress(Progress progress) {
                        Log.d("meee",getClass()+":\n"+"progress:"+progress.fraction);
                    }
                });
    }


    /**
     * 使用okhttp-utils的post请求网络文本数据
     */
    public void getDataPostByOkhttpUtils() {
        String url = "http://www.zhiyun-tech.com/App/Rider-M/changelog-zh.txt";

        url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";



        OkGo.<String>post(url)//
                .tag(this)//

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        mTvResult.setText(response.body());
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        mTvResult.setText("POST请求失败");
                    }
                });
    }




    private void getDataFromPost() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                    OkGo.<String>get("http://api.m.mtime.cn/PageSubArea/TrailerList.api")//
                            .tag(this)
                            .execute(new StringCallback(){
                                @Override
                                public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                    mTvResult.setText(response.body());
                                   // Toast.makeText(MainActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(com.lzy.okgo.model.Response<String> response) {
                                    super.onError(response);
                                    mTvResult.setText("GET请求失败");
                                }
                            });

            }
        }.start();
    }
    /**
     * okhttp3的post请求
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
