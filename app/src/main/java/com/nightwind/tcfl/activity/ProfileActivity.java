package com.nightwind.tcfl.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ProfileActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private Toolbar mToolbar;
    private GestureDetector mGestureDetector;

    private final int verticalMinDistance = 50;
    private final int minVelocity = 0;

    private User mUser;

    private ImageView mIVAvatar;
    private TextView mTVUsername;
    private TextView mTVSign;
    private TextView mTVLevel;
    private TextView mTVAge;
    private TextView mTVSex;
    private TextView mTVWork;
    private TextView mTVEdu;
    private TextView mTVHobby;

    private ViewGroup mVGStartChat;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private UserController mUserController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUserController = new UserController(this);

        //未登录，跳转登录界面
        if (mUserController.getSelfUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }

        if (getIntent() != null) {
            String username = getIntent().getStringExtra("username");
            if (username != null) {
                Log.d("ProfileActivity getIntent", username);
                mUser = mUserController.getUser(username);
            } else {
                mUser = mUserController.getSelfUser();
            }
            //SharedPrf存在，但数据库不存在了，且没连接网络，显示空的
            if (mUser == null) {
                mUser = new User();
            }
        }


        //初始化工具栏
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("Profile");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //滑动关闭监听器
        mGestureDetector = new GestureDetector(this, this);

        //修改信息
        mIVAvatar = (ImageView) findViewById(R.id.avatar);
        mTVUsername = (TextView) findViewById(R.id.username);
        mTVSign = (TextView) findViewById(R.id.sign);
        mTVLevel = (TextView) findViewById(R.id.tv_level);
        mTVAge = (TextView) findViewById(R.id.tv_age);
        mTVSex = (TextView) findViewById(R.id.tv_sex);
        mTVWork = (TextView) findViewById(R.id.tv_work);
        mTVEdu = (TextView) findViewById(R.id.tv_edu);
        mTVHobby = (TextView) findViewById(R.id.tv_hobby);

        mVGStartChat = (ViewGroup) findViewById(R.id.rl_start_chat);

//        mIVAvatar.setOnClickListener(new AvatarOnClickListener(this, mUser.getUsername()));
        imageLoader.displayImage(mUser.getAvatarUrl(), mIVAvatar, options);

        mTVUsername.setText(mUser.getUsername());
        mTVSign.setText(mUser.getInfo());
        mTVLevel.setText(String.valueOf(mUser.getLevel()));
        mTVAge.setText(String.valueOf(mUser.getAge()));
        mTVSex.setText(mUser.getSex() == 0 ? "男" : "女");
        mTVWork.setText(mUser.getWork());
        mTVEdu.setText(mUser.getEduString());
        mTVHobby.setText(mUser.getHobby());

        if (mUser != null && mUserController.getSelfUser() != null && mUser.getUid() == mUserController.getSelfUser().getUid()) {
            mVGStartChat.setVisibility(View.GONE);
        } else {
            mVGStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                    intent.putExtra("isFromProfile", true);
                    intent.putExtra("username", mUser.getUsername());
                    ProfileActivity.this.startActivity(intent);
                    ProfileActivity.this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SUCCESS) {
            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserController.closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }
        return super.onOptionsItemSelected(item);
    }





    //右划关闭

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    //向右滑动关闭activity
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float absdx = Math.abs(e2.getX() - e1.getX());
        float absdy = Math.abs(e2.getY() - e1.getY());
//        System.out.println("x` = " + (e1.getX() - e2.getX()) + " y` = " + Math.abs(e1.getY() - e2.getY()));

        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

        } else if (absdx > 1.5*absdy && e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        return false;
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
