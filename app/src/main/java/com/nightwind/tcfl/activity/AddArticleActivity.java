package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.tool.BaseTools;

public class AddArticleActivity extends ActionBarActivity {

    private Menu mMenu;
    private Toolbar mToolbar;

    private EditText mETTitle;
    private EditText mETContent;

    private User mSelfUser;
    private int mClassify = 0;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        //先判断是否登录
        Auth auth = new Auth(this);
        if (!auth.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return ;
        }

        if (getIntent() != null) {
            mClassify = getIntent().getIntExtra("classify", 0);
        }

        UserController userController = new UserController(this);
        mSelfUser = userController.getSelfUser();
        userController.closeDB();

        mETTitle = (EditText) findViewById(R.id.et_title);
        mETContent = (EditText) findViewById(R.id.et_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("发布帖子");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SUCCESS) {
                //重启
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class AddArticleTask extends AsyncTask<Article, Void, Integer> {

        @Override
        protected Integer doInBackground(Article... params) {
            ArticleController ac = new ArticleController(AddArticleActivity.this);
            Article article = params[0];
            int id = -1;
            article = ac.addArticleToServer( article.getClassify(), article.getTitle(), article.getContent());
            if (article != null) {
                id = article.getId();
            }
            return id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(AddArticleActivity.this);
//            mDialog.setTitle("发布帖子");
            mDialog.setMessage("正在发布帖子，请稍后...");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
            mDialog.cancel();
            if (id != -1) {
                Toast.makeText(AddArticleActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                setResult(0);
                finish();
            } else {
                Toast.makeText(AddArticleActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_article, menu);
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
        } else if (id == R.id.action_push_article) {
            Article article = new Article();

            String title = String.valueOf(mETTitle.getText());
            String content = String.valueOf(mETContent.getText());

            article.setTitle(title);
            article.setClassify(mClassify);
            article.setContent(content);
            article.setUsername(mSelfUser.getUsername());

            String date = BaseTools.getCurrentDateTime();
            article.setDate(date);

            ArticleController articleController = new ArticleController(this);

//            if (articleController.saveArticle(mClassify, article) != -1) {
//                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
//                setResult(0);
//                finish();
//            } else {
//                Toast.makeText(this, "发布失败", Toast.LENGTH_SHORT).show();
//            }
            new AddArticleTask().execute(article);

        } else if (id == android.R.id.home) {
            setResult(1);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
