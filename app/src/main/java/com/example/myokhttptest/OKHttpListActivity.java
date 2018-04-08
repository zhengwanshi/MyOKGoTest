package com.example.myokhttptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.myokhttptest.R.id.progressBar;

public class OKHttpListActivity extends AppCompatActivity {

    private static final String TAG = OKHttpListActivity.class.getSimpleName();
    @Bind(R.id.listview)
    ListView mListview;
    @Bind(progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_nodata)
    TextView mTvNodata;
    private String url;
    private OKHttpListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_list);
        ButterKnife.bind(this);
        getDataFromNet();
    }

    private void getDataFromNet() {
        url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
        //得到缓存的数据
//        String saveJson = CacheUtils.getString(this, url);
//        if (!TextUtils.isEmpty(saveJson)) {
//            processData(saveJson);
//        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkGo.<String>get(url)
                        .tag(this)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.e(TAG, "onResponse：complete");

                                //    解析数据和显示数据
                                if (response != null) {
                                    //缓存数据
                                   // CacheUtils.putString(OKHttpListActivity.this, url, response.toString());
                                    processData(response.body());
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);

                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(OKHttpListActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });
            }
        }.start();

    }



    /**
     * 解析和显示数据
     *
     * @param json
     */

    private void processData(String json) {

        //解析数据
        DataBean dataBean = parsedJson(json);
        List<DataBean.TrailersBean> datas = dataBean.getTrailers();

        if (datas != null && datas.size() > 0) {
            //有数据
         mTvNodata.setVisibility(View.GONE);
            //显示适配器
            adapter = new OKHttpListAdapter(OKHttpListActivity.this, datas);
            mListview.setAdapter(adapter);
        } else {
            //没有数据
         mTvNodata.setVisibility(View.VISIBLE);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据
     *
     * @param response
     * @return
     */
    private DataBean parsedJson(String response) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                List<DataBean.TrailersBean> trailers = new ArrayList<>();
                dataBean.setTrailers(trailers);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if (jsonObjectItem != null) {

                        DataBean.TrailersBean mediaItem = new DataBean.TrailersBean();

                        String movieName = jsonObjectItem.optString("movieName");//name
                        mediaItem.setMovieName(movieName);

                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setVideoTitle(videoTitle);

                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setCoverImg(imageUrl);

                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setHightUrl(hightUrl);

                        //把数据添加到集合
                        trailers.add(mediaItem);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataBean;
    }

}
