package com.example.atrialfibrilationdetection.Feature.Pasien;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atrialfibrilationdetection.R;

import java.util.ArrayList;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<RiwayatModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public RiwayatAdapter(List<RiwayatModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.list_summary,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int position = i;
        final RiwayatModel model = mList.get(i);

        viewHolder.tvItemId.setText(model.getDeviceID());
        viewHolder.tvItemDate.setText(model.getDate());

        int gambar = mActivity.getResources().getIdentifier("ic_wristwatch","drawable",mActivity.getPackageName());
        viewHolder.ivItemRiwayat.setImageResource(gambar);

        viewHolder.cvItemListRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cvItemListRiwayat;
        ImageView ivItemRiwayat;
        TextView tvItemId, tvItemDate;

        public ViewHolder(View itemView) {
            super(itemView);
            cvItemListRiwayat = (CardView) itemView.findViewById(R.id.cvItemListRiwayat);
            ivItemRiwayat = (ImageView) itemView.findViewById(R.id.iv_riwayat);
            tvItemId = (TextView) itemView.findViewById(R.id.tv_device_id);
            tvItemDate = (TextView) itemView.findViewById(R.id.tv_tgl_smr);
        }
    }
}
