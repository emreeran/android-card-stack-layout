package com.emreeran.cardstack.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emreeran.cardstack.CardStackLayout;

public class MainActivity extends AppCompatActivity {

    CardStackLayout mCardStackLayout;

    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStackLayout = findViewById(R.id.activity_main_card_stack_layout);
        addCardsWithAdapter();
//        addCardsManually();
    }

    private void addCardsManually() {
        final LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (mIndex = 0; mIndex < 5; mIndex++) {
            View view = layoutInflater.inflate(R.layout.card_item, mCardStackLayout, false);
            TextView textView = view.findViewById(R.id.card_item_text_view);
            String text = "Card " + (mIndex + 1);
            textView.setText(text);
            mCardStackLayout.addCard(view);
        }

        mCardStackLayout.addOnCardRemovedListener(direction -> {
            View view = layoutInflater.inflate(R.layout.card_item, mCardStackLayout, false);
            TextView textView = view.findViewById(R.id.card_item_text_view);
            String text = "Card " + (mIndex + 1);
            textView.setText(text);
            mCardStackLayout.addCard(view);
            mIndex++;
        });
    }

    private void addCardsWithAdapter() {
        CardAdapter adapter = new CardAdapter();
        mCardStackLayout.setAdapter(adapter);
    }

    private class CardAdapter extends CardStackLayout.CardStackAdapter<CardHolder> {
        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
            return new CardHolder(view);
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.setViews(position);
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    private class CardHolder extends CardStackLayout.ViewHolder {
        TextView mTextView;

        Button mButton;

        CardHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.card_item_text_view);
            mButton = itemView.findViewById(R.id.card_item_button);
        }

        void setViews(int position) {
            String text = "Card " + (position + 1);
            mTextView.setText(text);
            final int pos = position + 1;

            itemView.setOnClickListener(view -> Toast.makeText(MainActivity.this, "clicked card " + pos, Toast.LENGTH_SHORT).show());

            mButton.setOnClickListener(v -> Toast.makeText(MainActivity.this, "clicked button " + pos, Toast.LENGTH_SHORT).show());
        }
    }
}
