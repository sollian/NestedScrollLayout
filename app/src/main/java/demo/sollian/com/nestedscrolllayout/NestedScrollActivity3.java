package demo.sollian.com.nestedscrolllayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NestedScrollActivity3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_scroll3);
    }

    public void click(View view) {
        Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
    }
}
