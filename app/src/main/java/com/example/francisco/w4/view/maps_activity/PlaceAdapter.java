package com.example.francisco.w4.view.maps_activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.francisco.w4.R;
import com.example.francisco.w4.Util.CONSTANTS;
import com.example.francisco.w4.model.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 8/27/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private static final String TAG = "Adapter";
    List<Result> placeList = new ArrayList<>();
    Context context;
    private int lastPosition = -1;

    public PlaceAdapter(List<Result> placeList) {
        this.placeList = placeList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvText, tvText2,tvText3,tvText5;
        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            tvText2 = (TextView) itemView.findViewById(R.id.tvText2);
            tvText3 = (TextView) itemView.findViewById(R.id.tvText3);
            tvText5 = (TextView) itemView.findViewById(R.id.tvText5);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.places, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result item = placeList.get(position);
        Log.d(TAG, "onBindViewHolder: " + item.getName());
        holder.tvText.setText(item.getName());
        holder.tvText2.setText(item.getVicinity());
        holder.tvText3.setText(item.getTypes().get(0).toString());
        holder.tvText5.setText((String.valueOf(item.getRating())));
        if(item.getPhotos()!=null&&item.getPhotos().size()>0) {
            String a = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="+CONSTANTS.PHOTO_MAX_WIDTH+"&photoreference=" + item.getPhotos().get(0).getPhotoReference() + "&key=" + CONSTANTS.KEY;
            Picasso.with(holder.itemView.getContext()).load(a).into(holder.ivImage);
        }
        else
            Picasso.with(holder.itemView.getContext()).load(item.getIcon()).into(holder.ivImage);
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Name: " + item.getName()+"\n";
                if(item.getGeometry()!= null && item.getGeometry().getLocation() != null && item.getGeometry().getLocation().getLat() != null)
                    message += "Latitude: " + item.getGeometry().getLocation().getLat() + "\n";
                if( item.getGeometry()!= null && item.getGeometry().getLocation() != null && item.getGeometry().getLocation().getLng() != null)
                    message += "Longitude: " + item.getGeometry().getLocation().getLng() + "\n";

                if( item.getOpeningHours() != null && item.getOpeningHours().getOpenNow() != null)
                    message += "Opening hours: " + item.getOpeningHours().getOpenNow() + "\n";

                if(item.getRating() != null)
                    message += "Rating: " + item.getRating() + "\n";

                if(item.getVicinity() != null)
                    message += "Vicinity: " + item.getVicinity();
                ;

                CustomDialogClass cdd=new CustomDialogClass((Activity)context, message);
                cdd.show();
            }
        });

    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        placeList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(placeList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}
