package com.emreeran.cardstack.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emreeran.cardstack.CardStackLayout;

public class MainActivity extends AppCompatActivity {

    CardStackLayout mCardStackLayout;

    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStackLayout = (CardStackLayout) findViewById(R.id.activity_main_card_stack_layout);
        addCardsWithAdapter();
//        addCardsManually();
    }

    private void addCardsManually() {
        final LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (mIndex = 0; mIndex < 5; mIndex++) {
            View view = layoutInflater.inflate(R.layout.card_item, mCardStackLayout, false);
            TextView textView = (TextView) view.findViewById(R.id.card_item_text_view);
            String text = "Card " + (mIndex + 1);
            textView.setText(text);
            mCardStackLayout.addCard(view);
        }

        mCardStackLayout.setOnCardCountChangedListener(new CardStackLayout.OnCardCountChangedListener() {
            @Override
            public void onAdd(int cardCount) {

            }

            @Override
            public void onRemove(int cardCount) {
                View view = layoutInflater.inflate(R.layout.card_item, mCardStackLayout, false);
                TextView textView = (TextView) view.findViewById(R.id.card_item_text_view);
                String text = "Card " + (mIndex + 1);
                textView.setText(text);
                mCardStackLayout.addCard(view);
                mIndex++;
            }
        });
    }

    private void addCardsWithAdapter() {
        CardAdapter adapter = new CardAdapter();
        mCardStackLayout.setAdapter(adapter);
    }

    private class CardAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CardHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                holder = new CardHolder(convertView);
                convertView.setTag(holder);
            }

            holder = (CardHolder) convertView.getTag();
            holder.setViews(position);

            return convertView;
        }
    }

    private class CardHolder {
        TextView mTextView;

        CardHolder(View view) {
            mTextView = (TextView) view.findViewById(R.id.card_item_text_view);
        }

        void setViews(int position) {
            String text = "Card " + (position + 1);
            mTextView.setText(text);
        }
    }
}
