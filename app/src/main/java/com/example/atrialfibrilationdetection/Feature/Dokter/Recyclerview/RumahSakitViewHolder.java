package com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atrialfibrilationdetection.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class RumahSakitViewHolder extends GroupViewHolder {

    private TextView textView;
    private ImageView arrow;

    public RumahSakitViewHolder(View view){
        super(view);

        textView = view.findViewById(R.id.nama_rumahsakit);
        arrow = view.findViewById(R.id.arrow);
    }

    public void bind(RumahSakit rumahSakit){
        textView.setText(rumahSakit.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }
}
