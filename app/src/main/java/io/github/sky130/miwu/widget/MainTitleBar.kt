package io.github.sky130.miwu.widget;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.sky130.miwu.R;

public class MainTitleBar extends LinearLayout {
    public TextView a;
    public String b;

    public MainTitleBar(Context var1, AttributeSet var2) {
        super(var1, var2);
        TypedArray var10009 = var1.obtainStyledAttributes(var2, R.styleable.MiTitleBar, 0, 0);
        this.b = var10009.getString(R.styleable.MiTitleBar_title);
        var10009.recycle();
        this.setGravity(16);
        View.inflate(var1, R.layout.main_title_bar, this);
        this.setPadding(var1.getResources().getDimensionPixelSize(R.dimen.content_horizontal_distance), 0, 0, 0);
        this.a = this.findViewById(R.id.textView);
        this.a.setText(this.b);
        this.a.setSelected(true);
        this.a.setMarqueeRepeatLimit(-1);
    }

    public void setTitle(String var1) {
        this.a.setText(var1);
    }

    public TextView getTitleTextView() {
        return this.a;
    }
}