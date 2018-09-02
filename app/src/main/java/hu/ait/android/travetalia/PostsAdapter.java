package hu.ait.android.travetalia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.travetalia.data.Post;

/**
 * Created by lanle on 5/9/18.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    //work on this early morning tmr
    private Context context;
    private List<Post> postList;
    private List<String> postKeys;
    private String uid;
    private int lastPosition = -1;


    public PostsAdapter(Context context, String uid) {
        this.context = context;
        this.uid = uid;
        this.postList = new ArrayList<Post>();
        this.postKeys = new ArrayList<String>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_post, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.tvAuthor.setText(
                postList.get(holder.getAdapterPosition()).getAuthor());
        holder.tvLocation.setText(
                postList.get(holder.getAdapterPosition()).getLocation());
        holder.tvCaption.setText(
                postList.get(holder.getAdapterPosition()).getCaption());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePost(holder.getAdapterPosition());
            }
        });
        if (!TextUtils.isEmpty(postList.get(holder.getAdapterPosition()).getImageUrl())) {
            Glide.with(context).load(
                    postList.get(holder.getAdapterPosition()).getImageUrl()
            ).into(holder.ivImage);
            holder.ivImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addPost(Post newPost, String key) {
        postList.add(newPost);
        postKeys.add(key);
        notifyDataSetChanged();
    }
    private void removePost(int adapterPosition) {

        FirebaseDatabase.getInstance().getReference("posts").child(
                postKeys.get(adapterPosition)).removeValue();

        postList.remove(adapterPosition);
        postKeys.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }


    public void removePostByKey(String key) {
        int index = postKeys.indexOf(key);
        if (index != -1) {
            postList.remove(index);
            postKeys.remove(index);
            notifyItemRemoved(index);
        }

    }

    public List<Post> getAllPosts() {
        return postList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvLocation;
        public TextView tvCaption;
        public TextView tvAuthor;
        public ImageView ivDelete;
        public ImageView ivImage;
        public ViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivImage = itemView.findViewById(R.id.ivImage);

        }
    }
}



