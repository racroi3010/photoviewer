package ptv.me.com.photoviewer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ptv.me.com.photoviewer.R;
import ptv.me.com.photoviewer.model.ImageItem;
import ptv.me.com.photoviewer.presenter.ImageLoadingPresenter;
import ptv.me.com.photoviewer.util.ValidationUtil;

/**
 * this adapter is used for filling image on grid layout
 * Created by VS on 12/6/2016.
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {
    private Context mContext;
    private List<ImageItem> mDatas;
    private ImageLoadingPresenter.ILViewer viewer;

    public GalleryImageAdapter(Context mContext, List<ImageItem> mDatas, ImageLoadingPresenter.ILViewer viewer) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.viewer = viewer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageItem item = mDatas.get(position);
        //Picasso.with(mContext).setLoggingEnabled(true);

        //Picasso.with(mContext).load(new File(item.getThumb())).into(holder.imgView);
        if(ValidationUtil.validateURI(item.getThumb())){
            Picasso.with(mContext).load(item.getThumb()).fit().into(holder.imgView);
        } else {
            Picasso.with(mContext).load(new File(item.getThumb())).fit().into(holder.imgView);
        }

        final int pos = position;
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewer.onSelect(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.image_view);

        }
    }
}
