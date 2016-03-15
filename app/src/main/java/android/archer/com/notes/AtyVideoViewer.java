package android.archer.com.notes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * @function Video查看器
 * Created by Archer on 2016/3/15.
 */
public class AtyVideoViewer extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vv=new VideoView(this);
        setContentView(vv);



        String path =getIntent().getStringExtra(EXTRA_PATH);
           if (path!=null){
               vv.setVideoPath(path);
           }else {
               finish();
           }
    }



    private VideoView vv;

    public static final String EXTRA_PATH ="PATH";
}
