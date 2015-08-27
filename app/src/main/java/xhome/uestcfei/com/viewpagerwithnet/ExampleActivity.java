package xhome.uestcfei.com.viewpagerwithnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import xhome.uestcfei.com.library.ViewPagerWithNet;

public class ExampleActivity extends AppCompatActivity {

    private ViewPagerWithNet viewPagerWithNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        viewPagerWithNet = (ViewPagerWithNet) findViewById(R.id.viewpager);
        String[] urlList = new String[]{ "http://puu.sh/jQhMm/5b188bdfb7.jpg",
                "http://puu.sh/jQhNe/07afcb2075.jpg",
                "http://puu.sh/jQhO8/c753e161b6.jpg",
                "http://puu.sh/jQhOF/3bd637368b.jpg",
                "http://puu.sh/jQhNe/07afcb2075.jpg",};
        String [] contents = new String[]{
                "这是第一个测试","这是第二个测试","这是第三个测试","这是第四个测试","这是第五个测试"
        };
        //填充数据
        viewPagerWithNet.initData(urlList,contents);
        //开始轮播
        viewPagerWithNet.startPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_example, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
