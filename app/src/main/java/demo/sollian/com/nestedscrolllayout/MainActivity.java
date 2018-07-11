package demo.sollian.com.nestedscrolllayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View view) {
        startActivity(new Intent(this, NestedScrollActivity.class));
    }

    public void click2(View view) {
        startActivity(new Intent(this, NestedScrollActivity2.class));
    }

    public void click3(View view) {
        startActivity(new Intent(this, NestedScrollActivity3.class));
    }
}
