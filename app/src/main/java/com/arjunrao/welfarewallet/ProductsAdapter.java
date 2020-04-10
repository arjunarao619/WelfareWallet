package com.arjunrao.welfarewallet;

import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Belal on 3/18/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    Context mCtx;
    List<Product> productList;

    public ProductsAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.product_layout,
                parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        //TODO figure out alternating colors on cardviews
//        if(position %2 == 0)
//        {
//            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        }
//        else
//        {
//            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
//            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
//        }

        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getShortdesc());
        holder.textviewPrice.setText(String.valueOf(product.getPrice()));
        holder.textViewRating.setText(String.valueOf(product.getRating()));
        holder.textViewComments.setText(String.valueOf(product.getComments()));
        holder.textViewBalance.setText(String.valueOf(product.getBalance()));

//        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage(), null));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        //ImageView imageView;
        TextView textViewTitle, textViewShortDesc, textViewRating, textviewPrice,textViewComments,textViewBalance;

        public ProductViewHolder(View itemView) {
            super(itemView);

           // imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textviewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewComments = itemView.findViewById(R.id.textViewComments);
            textViewBalance = itemView.findViewById(R.id.textViewBalance);


        }
    }
}
