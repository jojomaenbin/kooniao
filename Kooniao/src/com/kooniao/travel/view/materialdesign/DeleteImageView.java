package com.kooniao.travel.view.materialdesign;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kooniao.travel.R;
import com.kooniao.travel.model.ProductResource;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by ZZD on 2015/6/1.
 */
public class DeleteImageView extends FrameLayout {
    private ImageView delete,tabimage;
    private LinearLayout progress;
    private List<String> objectList;
    private List<ProductResource> addList;
    private Object object;
    private String path;
    public DeleteImageView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.image_delete_layout, DeleteImageView.this);
        progress= (LinearLayout) findViewById(R.id.progress_layout);
        delete= (ImageView) findViewById(R.id.ib_delete);
        tabimage= (ImageView) findViewById(R.id.image_tab);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectList!=null)
                objectList.remove(object);
                if (addList!=null)
                    addList.remove(object);
                ((LinearLayout)getParent()).removeView(DeleteImageView.this);
            }
        });

    }

    public void showProgress(){
        progress.setVisibility(VISIBLE);
    }
    public void dissProgress(){
        post(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(GONE);
            }
        });
    }

    public void setImageBitmap(Bitmap bitmap){
        tabimage.setImageBitmap(bitmap);
    }
    public void setImagePath(String path){
        this.path=path;
        ImageLoader.getInstance().displayImage(path, tabimage);
    }
    public String getImagePath(){
        return path;
    }

    public List<String> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<String> objectList) {
        this.objectList = objectList;
    }
    public void setProductResourceList(List<ProductResource> objectList) {
        this.addList = objectList;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
