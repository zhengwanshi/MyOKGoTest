package com.example.myokhttptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by zhengyg on 2018/4/7.
 */

public class OKHttpListAdapter extends BaseAdapter {
    private final Context context;
    private final List<DataBean.TrailersBean> datas;

    public OKHttpListAdapter(Context context, List<DataBean.TrailersBean>datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView ==null){
            convertView = View.inflate(context,R.layout.item_okhttp_list_image,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        //根据位置得到数据
        DataBean.TrailersBean itemData = datas.get(position);
        viewHolder.tv_name.setText(itemData.getMovieName());
        viewHolder.tv_desc.setText(itemData.getVideoTitle());

        OkGo.<Bitmap>get(itemData.getCoverImg())
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        Log.e(TAG, "onSuccess: "+position);
                        viewHolder.iv_icon.setImageBitmap(response.body());
                    }

                    @Override
                    public void onError(Response<Bitmap> response) {
                        super.onError(response);
                        Log.e(TAG, "onError: " +position);
                    }
                });
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}
