package com.wangdao.our.spread_2.fragment_myteam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wangdao.our.spread_2.ExampleApplication;
import com.wangdao.our.spread_2.R;
import com.wangdao.our.spread_2.bean.Team;
import com.wangdao.our.spread_2.slide_widget.AllUrl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/12 0012.
 * 我的团队--全部成员
 */
public class MyTeam_All extends Fragment{


    private HttpPost httpPost;
    private HttpResponse httpResponse = null;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private AllUrl allurl = new AllUrl();


    private View myView;
    private Context myContext;
    private LayoutInflater myInflater;

    private ListView lv_myTeam_all;
    private MyTeam2_Handler myTeam2_handler = new MyTeam2_Handler();
    private List<Team> list_team = new ArrayList<>();
    private TextView tv_erro;
    private MyTeamAdapter my2Adapter ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_myteam_all,null);
        myContext = this.getActivity();
        myInflater = inflater;

        initView();

        my2Adapter = new MyTeamAdapter(list_team);
        lv_myTeam_all.setAdapter(my2Adapter);
        initData();

        return myView;
    }

    private void initView(){
        tv_erro = (TextView) myView.findViewById(R.id.fragment_myteam_tv_erro);
        lv_myTeam_all = (ListView) myView.findViewById(R.id.activity_myteam_lv);
    }






    /**
     * 初始化数据
     */
    private String initDataResult = "网络异常";
    private void initData(){
        httpPost = new HttpPost(allurl.getUserTeam());
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("user", myContext.MODE_PRIVATE);
        String mToken = sharedPreferences.getString("user_token", "");
        params.add(new BasicNameValuePair("user_token", mToken));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject jo = new JSONObject(result);
                        initDataResult = jo.getString("info");
                        if(jo.getString("status").equals("1")){

                            JSONArray ja = jo.getJSONArray("data");
                            for(int i = 0;i<ja.length();i++){
                                JSONObject jo_2 = ja.getJSONObject(i);
                                Team uTeam = new Team();
                                uTeam.setAddTime(jo_2.getString("create_time"));
                                JSONObject jo_3 = jo_2.getJSONObject("userinfo");
                                uTeam.setIcon_url(jo_3.getString("avatar256"));
                                uTeam.setName(jo_3.getString("nickname"));

                                list_team.add(uTeam);
                            }

                            myTeam2_handler.sendEmptyMessage(1);
                        }else{
                            myTeam2_handler.sendEmptyMessage(2);
                        }

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class MyTeam2_Handler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取数据成功
                case 1:
                    my2Adapter.notifyDataSetChanged();
                    tv_erro.setVisibility(View.GONE);
                    break;
                //获取数据失败
                case 2:
                    tv_erro.setText(initDataResult);
                    tv_erro.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


    class MyTeamAdapter extends BaseAdapter {
        MyTeamHolder mtHoledr = null;

        private List<Team> teams;
        public MyTeamAdapter(List<Team> list_team){
            this.teams = list_team;
        }

        @Override
        public int getCount() {
            return teams.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = myInflater.inflate(R.layout.item_myteam,null);
                mtHoledr = new MyTeamHolder();
                mtHoledr.tIv_icon = (ImageView) convertView.findViewById(R.id.item_myteam_iv_icon);
                mtHoledr.tTv_name = (TextView) convertView.findViewById(R.id.item_myteam_tv_name);
                mtHoledr.tTv_time = (TextView) convertView.findViewById(R.id.item_myteam_tv_time);
                mtHoledr.tTv_time_lately = (TextView) convertView.findViewById(R.id.item_myteam_tv_time_lately);
                mtHoledr.tTv_member = (TextView) convertView.findViewById(R.id.item_myteam_member);
                convertView.setTag(mtHoledr);
            }else{
                mtHoledr = (MyTeamHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(teams.get(position).getIcon_url() == null ? "" : teams.get(position).getIcon_url(), mtHoledr.tIv_icon,
                    ExampleApplication.getInstance().getOptions(R.drawable.nopilc_2));

            mtHoledr.tTv_name.setText(teams.get(position).getName());
            mtHoledr.tTv_time.setText(teams.get(position).getAddTime());

            return convertView;
        }
    }

    class MyTeamHolder{
        ImageView tIv_icon;
        TextView tTv_name;
        TextView tTv_time;
        TextView tTv_time_lately;
        TextView tTv_member;
    }
}