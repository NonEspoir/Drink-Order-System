package com.example.drink_order_system;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderFragment extends Fragment {
    private ArrayList<Drinks> drinks_array = new ArrayList<Drinks>(); //可选的饮品列表
    private ArrayList<LeftBean> titles_array = new ArrayList<LeftBean>(); //饮品类别列表
    private RecyclerView right_listView; //右侧饮品列表
    private RecyclerView left_listView; //左侧类别列表
    private LinearLayoutManager right_llM;
    private TextView right_title;
    private SearchView searchView;

    private AlertDialog chooseDialog = null;
    private AlertDialog.Builder builder = null;
    private View view_choose;

    private Context mContext = this.getActivity();

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        SearchView mSearch = (SearchView) view.findViewById(R.id.my_search);
        int id = mSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView text_search = (TextView) mSearch.findViewById(id);
        text_search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        right_title = (TextView) view.findViewById(R.id.Top_drinkType);

        right_listView = (RecyclerView) view.findViewById(R.id.rec_right);
        left_listView = (RecyclerView) view.findViewById(R.id.rec_left);
        searchView = (SearchView) view.findViewById(R.id.my_search);
        builder = new AlertDialog.Builder(this.getActivity());
        view_choose = inflater.inflate(R.layout.dialogue_choose, null, false);
        builder.setView(view_choose);
        builder.setCancelable(false);
        chooseDialog = builder.create();

        view_choose.findViewById(R.id.button_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.dismiss();
            }
        });

        view_choose.findViewById(R.id.button_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String size = "中杯";
                String temperature = "全冰";
                String sugar = "全糖";
                RadioGroup radiogroup = (RadioGroup) view_choose.findViewById(R.id.radioGroup_size);
                for (int i = 0; i < radiogroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radiogroup.getChildAt(i);
                    if (rd.isChecked()) {
                        size = String.valueOf(rd.getText());
                    }
                }
                radiogroup = (RadioGroup) view_choose.findViewById(R.id.radioGroup_ice);
                for (int i = 0; i < radiogroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radiogroup.getChildAt(i);
                    if (rd.isChecked()) {
                        temperature = String.valueOf(rd.getText());
                    }
                }
                radiogroup = (RadioGroup) view_choose.findViewById(R.id.radioGroup_sugar);
                for (int i = 0; i < radiogroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radiogroup.getChildAt(i);
                    if (rd.isChecked()) {
                        sugar = String.valueOf(rd.getText());
                    }
                }
                TextView drinkName = view_choose.findViewById(R.id.choose_drinkName);
                //写买进购物车的逻辑
                System.out.println("drinkName:" + String.valueOf(drinkName.getText()).split("  #")[0]);
                Drinks drink = new Drinks(Integer.parseInt(String.valueOf(drinkName.getText()).split("  #")[1]));
                Flavor flavor = new Flavor(size, temperature, sugar);
                TextView numberTV = (TextView) view_choose.findViewById(R.id.textView_drinkNumber);
                int number = Integer.parseInt((String) numberTV.getText());
                Ordered_drinks od = new Ordered_drinks(drink, flavor, number);
                chooseDialog.dismiss();
            }
        });

        view_choose.findViewById(R.id.button_subtract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView numberText = (TextView) view_choose.findViewById(R.id.textView_drinkNumber);
                int i = Integer.parseInt(String.valueOf(numberText.getText()));
                if (i > 1) {
                    i--;
                }
                numberText.setText(String.valueOf(i));
            }
        });

        view_choose.findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView numberText = (TextView) view_choose.findViewById(R.id.textView_drinkNumber);
                int i = Integer.parseInt(String.valueOf(numberText.getText()));
                if (i < 100) {
                    i++;
                }
                numberText.setText(String.valueOf(i));
            }
        });

        initData();
        right_llM = new LinearLayoutManager(this.getActivity());
        right_listView.setLayoutManager(right_llM);
        RightAdapter rightAdapter = new RightAdapter(inflater, drinks_array);
        right_listView.setAdapter(rightAdapter);

        titles_array.get(0).setSelect(true);
        left_listView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        LeftAdapter leftAdapter = new LeftAdapter(titles_array);
        left_listView.setAdapter(leftAdapter);

        right_listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstItemPosition = right_llM.findFirstVisibleItemPosition();
                leftAdapter.setCurrentPosition(firstItemPosition);
                if (leftAdapter.getCurrentTitle() != "") {
                    right_title.setText(leftAdapter.getCurrentTitle());
                }
            }
        });


        leftAdapter.setOnItemClickListener(new LeftAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int rightPosition) {
                if (right_llM != null) {
                    right_llM.scrollToPositionWithOffset(rightPosition, 0);
                }
            }
        });

        rightAdapter.buttonSetOnClick(new RightAdapter.MyClickListener() {
            @Override
            public void onclick(View v, int position) {
                chooseDialog.show();
                if (view_choose != null) {
                    Drinks drink = drinks_array.get(position);
                    ImageView img = view_choose.findViewById(R.id.choose_drink_img);
                    img.setImageResource(drink.getImageResId() - 1);
                    TextView name = view_choose.findViewById(R.id.choose_drinkName);
                    name.setText(drink.get_name() + "  #" + (drink.get_number() + 1));
                    TextView intro = view_choose.findViewById(R.id.choose_drinkIntro);
                    intro.setText(drink.get_introduction());
                    TextView drink_number = view_choose.findViewById(R.id.textView_drinkNumber);
                    drink_number.setText("1");
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                for (int i = 0; i < drinks_array.size(); i++) {
                    if (drinks_array.get(i).get_name().contains(queryText)) {
                        if (right_llM != null) {
                            right_llM.scrollToPositionWithOffset(i, 0);
                            break;
                        }
                    }
                }
                return true;
            }
        });
        return view;
    }

    private void initData() {
        drinks_array.add(new Drinks("生椰拿铁", "\uD83E\uDD65 经典好椰",
                13.9f, "3年突破7亿杯，原创YYDS!", R.drawable.fresh_coconut_latte_logo));
        drinks_array.add(new Drinks("冰吸生椰拿铁",
                14.9f, "0乳糖|超凉感，嘴巴里面开空调", R.drawable.iced_fresh_coconut_latte_logo));
        drinks_array.add(new Drinks("轻咖椰子水",
                16.9f, "0脂| 100%椰子水，含电解质", R.drawable.light_coffee_coconut_water_logo));
        drinks_array.add(new Drinks("椰青冰萃美式",
                15.9f, "0脂| 小众宝藏，100%椰子水", R.drawable.coconut_iced_americano_logo));

        drinks_array.add(new Drinks("加浓美式","\u2615 美式家族",
                12.9f, "浓醇加倍，臻选IIAC金奖配方", R.drawable.extra_strong_americano_logo));
        drinks_array.add(new Drinks("大理石美式",
                13.5f, "个性特调|上半口醇厚，下半口清爽", R.drawable.marble_americano_logo));
        drinks_array.add(new Drinks("橙C美式",
                14.2f, "斯巴达勇士推荐|0脂真果汁低负担", R.drawable.orange_c_americano_logo));
        drinks_array.add(new Drinks("橘金气泡美式",
                14.8f, "0脂低糖低卡|个性特调经典回归", R.drawable.golden_orange_sparkling_americano_logo));


        drinks_array.add(new Drinks("丝绒拿铁","\uD83E\uDD64 超爱拿铁",
                16.0f, "0植脂末|丝滑感提升20.99%", R.drawable.velvet_latte_logo));
        drinks_array.add(new Drinks("小白梨拿铁",
                17.5f, "超级0卡糖，清润轻盈", R.drawable.white_pear_latte_logo));
        drinks_array.add(new Drinks("大话西瓜拿铁",
                16.5f, "0卡糖低负担，爱你一万年!", R.drawable.watermelon_latte_logo));
        drinks_array.add(new Drinks("马斯卡彭生酪拿铁",
                18.0f, "含丹麦芝士，奶味提升24%", R.drawable.mascarpone_cheese_latte_logo));


        drinks_array.add(new Drinks("轻咖柠檬茶","\uD83C\uDF51 饭后茶憩",
                15.8f, "0脂真果汁低负担，添加生豆咖啡液", R.drawable.light_coffee_lemon_tea_logo));
        drinks_array.add(new Drinks("冰镇杨梅瑞纳冰",
                14.5f, "清爽爆款|含10颗东魁杨梅", R.drawable.iced_bayberry_slush_logo));
        drinks_array.add(new Drinks("加柚瑞纳冰",
                14.0f, "柚子轻酪风味，今天加“柚“不加班", R.drawable.grapefruit_slush_logo));
        drinks_array.add(new Drinks("杨梅冰茶",
                13.5f, "0咖啡真果汁低负担，时令东魁杨梅", R.drawable.bayberry_iced_tea_logo));

        for (int i = 0; i < drinks_array.size(); i++) {
            Drinks temp = drinks_array.get(i);
            if (temp.get_type() != null) {
                titles_array.add(new LeftBean(i, temp.get_type()));
            }
        }
    }
}