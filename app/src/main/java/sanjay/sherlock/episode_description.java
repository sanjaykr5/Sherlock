package sanjay.sherlock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;


public class episode_description extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String Api_Key = "AIzaSyAwyhs56TGJ0WTgK_Rau4bxsTogYpmuh3M";
    public String Video_Id;
    private String status;
    private static final int Recovery_dialog_request = 1;
    private String TAG = episode_description.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String url;
    HashMap<String, String> episode_Description = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_description);
        Bundle extras = getIntent().getExtras();
        Video_Id = extras.getString("video_url");
        status = extras.getString("status");
        url = extras.getString("_site");
        new GetData().execute();
        YouTubePlayerFragment video_fragment;
        video_fragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.fragment);
        video_fragment.initialize(Api_Key, this);
        new GetImage((ImageView) findViewById(R.id.poster)).execute();
        ImageView ratings = (ImageView) findViewById(R.id.imageView4);
        ratings.setImageResource(R.drawable.btn_rating_star_off_selected);
        int[] images=new int[]{R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5,R.drawable.image6,R.drawable.image7};
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.layout);
        for (int i=0 ; i<7; i++){
            ImageView iv = new ImageView (this);
            iv.setPadding(2, 2, 2, 2);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setBackgroundResource (images[i]);
            linearLayout.addView(iv);
        }
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,YouTubeInitializationResult errorReason){
        if (errorReason.isUserRecoverableError()){
            errorReason.getErrorDialog(this,Recovery_dialog_request).show();
        }
        else {
            String errorMessage=String.format("Error",errorReason.toString());
            Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,YouTubePlayer player,boolean wasRestored){
        if(!wasRestored)
        {
            player.cueVideo(Video_Id);
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode==Recovery_dialog_request){
            getYoutubePlayerProvider().initialize(Api_Key,this);
        }
    }
    protected YouTubePlayer.Provider getYoutubePlayerProvider(){
        return (YouTubePlayerView)findViewById(R.id.fragment);
    }
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(episode_description.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String _name = jsonObj.getString("Title");
                    String _plot = jsonObj.getString("Plot");
                    String _rating = jsonObj.getString("imdbRating");
                    String _votes=jsonObj.getString("imdbVotes");
                    String _time=jsonObj.getString("Runtime");
                    String _url=jsonObj.getString("Poster");
                    String _genre=jsonObj.getString("Genre");
                    episode_Description.put("genre",_genre);
                    episode_Description.put("name", _name);
                    episode_Description.put("image_url",_url);
                    episode_Description.put("plot", _plot);
                    episode_Description.put("rating", _rating);
                    episode_Description.put("time",_time);
                    episode_Description.put("votes",_votes);
                }
                catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            TextView textView=(TextView)findViewById(R.id.name);
            TextView textView1=(TextView)findViewById(R.id.duration);
            TextView textView2=(TextView)findViewById(R.id.rating);
            TextView textView3=(TextView)findViewById(R.id.votes);
            TextView textView4=(TextView)findViewById(R.id.plot);
            TextView textView5=(TextView)findViewById(R.id.genre);
            textView.setText(episode_Description.get("name"));
            textView1.setText(episode_Description.get("time"));
            textView2.setText(episode_Description.get("rating"));
            textView3.setText(episode_Description.get("votes"));
            textView4.setText(episode_Description.get("plot"));
            textView5.setText(episode_Description.get("genre"));
        }
    }
    public class GetImage extends AsyncTask<Void,Void,Bitmap>
    {
        ImageView imageView;
        public GetImage(ImageView imageView)
        {
            this.imageView=imageView;
        }
        protected Bitmap doInBackground(Void...arg0)
        {
            String url=episode_Description.get("image_url");
            Bitmap bitmap=null;
            try {
                InputStream inputStream= new java.net.URL(url).openStream();
                bitmap= BitmapFactory.decodeStream(inputStream);
            }catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result)
        {
            imageView.setImageBitmap(result);
        }
    }

}
