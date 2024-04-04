package com.example.geoshare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingClient;
import com.example.geoshare.PurchaseOption;
import com.example.geoshare.R;

import java.util.List;

public class PurchaseOptionsAdapter extends RecyclerView.Adapter<PurchaseOptionsAdapter.PurchaseOptionViewHolder> {

    private List<PurchaseOption> purchaseOptions;
    private OnPurchaseOptionClickListener clickListener;
    private BillingClient billingClient;

    public interface OnPurchaseOptionClickListener {
        void onPurchaseOptionClicked(PurchaseOption purchaseOption);
    }

    public PurchaseOptionsAdapter(List<PurchaseOption> purchaseOptions, OnPurchaseOptionClickListener clickListener, BillingClient billingClient) {
        this.purchaseOptions = purchaseOptions;
        this.clickListener = clickListener;
        this.billingClient = billingClient;
    }

    @Override
    public PurchaseOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_option, parent, false);
        return new PurchaseOptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PurchaseOptionViewHolder holder, int position) {
        PurchaseOption purchaseOption = purchaseOptions.get(position);
        holder.packageNameTextView.setText(purchaseOption.getName());
        holder.packagePriceTextView.setText(purchaseOption.getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPurchaseOptionClicked(purchaseOption);
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchaseOptions.size();
    }

    public static class PurchaseOptionViewHolder extends RecyclerView.ViewHolder {
        public TextView packageNameTextView;
        public TextView packagePriceTextView;

        public PurchaseOptionViewHolder(View itemView) {
            super(itemView);
            packageNameTextView = itemView.findViewById(R.id.packageNameTextView);
            packagePriceTextView = itemView.findViewById(R.id.packagePriceTextView);
        }
    }

    // Commented out the simulatePurchaseSuccess method as it contains commented code and logic for initiating a purchase flow which is complex to implement without the full context.
    // Implement your purchase simulation logic here as per your requirements.
}
