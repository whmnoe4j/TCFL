package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nightwind.tcfl.AvatarOnClickListener;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.FriendsActivity;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wind on 2014/12/3.
 */
public class FriendsAdapter  extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private static Context mContext;

    private List<User> mUsers;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public FriendsAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public int id;
        public ImageView avatar;
        public TextView username;
        public TextView sign;
        public ImageView online;

        public ViewHolder(View itemView) {
            super(itemView);

            RelativeLayout view = (RelativeLayout) itemView.findViewById(R.id.friendItem);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FriendsActivity)mContext).onFragmentInteraction(String.valueOf(username.getText()));
                }
            });

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            sign = (TextView) itemView.findViewById(R.id.sign);
            online = (ImageView) itemView.findViewById(R.id.online);
        }
    }


    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        String username = mUsers.get(position).getUsername();

        holder.username.setText(username);
        holder.sign.setText(mUsers.get(position).getInfo());

        int color = mUsers.get(position).getOnline() == 1 ? Color.GREEN : Color.RED;
        holder.online.setBackgroundColor(color);

        //从服务器加载图片
//        imageLoader.displayImage(Dummy.getImgURLList()[position%8], holder.avatar, options);
        imageLoader.displayImage(mUsers.get(position).getAvatarUrl(), holder.avatar, options);
        holder.avatar.setOnClickListener(new AvatarOnClickListener(mContext, username));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

}
