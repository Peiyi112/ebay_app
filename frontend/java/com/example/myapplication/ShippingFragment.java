package com.example.myapplication;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.RequestQueue;


public class ShippingFragment extends Fragment {
    private Shareviewmodel viewModel;
    private RequestQueue requestQueue;
    public static ShippingFragment newInstance() {
        return new ShippingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shipping, container, false);
        Log.d("servergenurl", "isme");
        viewModel = new ViewModelProvider(requireActivity()).get(Shareviewmodel.class);
        try {
            viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
                Log.d("ShippingFragment", "Store Name: " + product.getStoreNAME());
                Log.d("ShippingFragment", "Feedback Score: " + product.getfeedbackscore());
                TextView storename = view.findViewById(R.id.storename);
                if(product.getStoreNAME()!=null) {
                    Spannable content= new SpannableString(product.getStoreNAME());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    storename.setText(content);
                    storename.setSelected(true);
                    storename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(product.getStoreURL()));
                            startActivity(i);
                        }
                    });
                }
                else{
                    LinearLayout storenamerow=view.findViewById(R.id.storenamerow);
                    storenamerow.setVisibility(View.GONE);
                }



                TextView fdscore = view.findViewById(R.id.fdscore);
                if(product.getfeedbackscore()!=null) {
                    fdscore.setText(product.getfeedbackscore());
                }
                else{
                    LinearLayout scorerow=view.findViewById(R.id.scorerow);
                    scorerow.setVisibility(View.GONE);
                }

                ImageView feedbacjstar = view.findViewById(R.id.feedbacjstar);

                Log.d("servergenurl", product.getfeedbackstar());
                if(product.getfeedbackstar()!=null) {
                    switch (product.getfeedbackstar()) {
                        case "None":
                            feedbacjstar.setVisibility(View.GONE);
                            break;
                        case "Yellow":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.YELLOW);
                            break;
                        case "Blue":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.BLUE);
                            break;
                        case "Turquoise":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.parseColor("#30D5C8")); //
                            break;
                        case "Purple":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.parseColor("#800080")); //
                            break;
                        case "Red":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.RED); //
                            break;
                        case "Green":
                            feedbacjstar.setImageResource(R.drawable.star_circle_outline);
                            feedbacjstar.setColorFilter(Color.GREEN); //
                        case "YellowShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.YELLOW);
                            break;
                        case "TurquoiseShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.parseColor("#30D5C8")); //
                            break;
                        case "PurpleShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.parseColor("#800080")); //
                            break;
                        case "RedShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.RED); //
                            break;
                        case "GreenShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.GREEN); //
                        case "SilverShooting":
                            feedbacjstar.setImageResource(R.drawable.star_circle);
                            feedbacjstar.setColorFilter(Color.parseColor("#C0C0C0")); //
                        default:
                            feedbacjstar.setVisibility(View.GONE); // Hide the ImageView for unknown cases
                            LinearLayout fbstarrow=view.findViewById(R.id.fbstarrow);
                            fbstarrow.setVisibility(View.GONE);
                            break;
                    }
                }
                else{
                    LinearLayout fbstarrow=view.findViewById(R.id.fbstarrow);
                    fbstarrow.setVisibility(View.GONE);
                }

                TextView popularity = view.findViewById(R.id.popularity);
                if(product.getpopularity()!=null) {
                    popularity.setText(product.getpopularity().substring(0,2)+"%");
                }else{
                    LinearLayout populrow=view.findViewById(R.id.populrow);
                    populrow.setVisibility(View.GONE);
                }

                TextView shipcost = view.findViewById(R.id.shipcost);
                if(product.getshipcost()!=null) {
                    if(product.getshipcost()=="Free Shipping"){
                    shipcost.setText("Free");}
                    else{
                        shipcost.setText(product.getshipcost());
                    }
                }
                else{
                    LinearLayout shipcostrow=view.findViewById(R.id.shipcostrow);
                    shipcostrow.setVisibility(View.GONE);
                }

                TextView gloship = view.findViewById(R.id.gloship);
                if(product.getglobalship()!=null) {
                    if(product.getglobalship()=="false"){
                        gloship.setText("No");
                    }
                    else{
                        gloship.setText("Yes");
                    }
                }else{
                    LinearLayout globalrow=view.findViewById(R.id.globalrow);
                    globalrow.setVisibility(View.GONE);
                }

                TextView handletime = view.findViewById(R.id.handletime);
                if(product.gethandletime()!=null){
                    handletime.setText(product.gethandletime()+" day");
                }else{
                    LinearLayout handlerow=view.findViewById(R.id.handlerow);
                    handlerow.setVisibility(View.GONE);
                }

                TextView conddd = view.findViewById(R.id.condition);
                if(product.getconditiondes()!=null) {
                    conddd.setText(product.getconditiondes());
                }else{
                    LinearLayout conditionrow=view.findViewById(R.id.conditionrow);
                    conditionrow.setVisibility(View.GONE);
                }

                ///
                TextView policy = view.findViewById(R.id.policy);
                if(product.getpolicy()!=null) {
                    policy.setText(product.getpolicy());
                }else{
                    LinearLayout policyrow=view.findViewById(R.id.policyrow);
                    policyrow.setVisibility(View.GONE);
                }
                TextView returnswith = view.findViewById(R.id.returnswith);
                if(product.getReturnswithin()!=null){
                    returnswith.setText(product.getReturnswithin());
                }else{
                    LinearLayout reutrnwitrow=view.findViewById(R.id.reutrnwitrow);
                    reutrnwitrow.setVisibility(View.GONE);
                };
                TextView refundmode = view.findViewById(R.id.refundmode);
                if(product.getrefund()!=null){
                    refundmode.setText(product.getrefund());
                }else{
                    LinearLayout refundrow=view.findViewById(R.id.refundrow);
                    refundrow.setVisibility(View.GONE);
                }
                TextView shipby = view.findViewById(R.id.shipby);
                if(product.getshipby()!=null){
                    shipby.setText(product.getshipby());
                }else{
                    LinearLayout shipbyrow=view.findViewById(R.id.shipbyrow);
                    shipbyrow.setVisibility(View.GONE);
                }



            });
        }catch(Exception R){
            Log.d("servergenurl", R.toString());
        }



        return view;
    }




}