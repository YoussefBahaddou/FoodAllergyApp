package ma.emsi.foodallergyapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateLastMessage(ChatMessage message) {
        if (!messages.isEmpty()) {
            messages.set(messages.size() - 1, message);
            notifyItemChanged(messages.size() - 1);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView messageText;
        private TextView timeText;

        MessageViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_message);
            messageText = itemView.findViewById(R.id.text_message);
            timeText = itemView.findViewById(R.id.text_time);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());

            if (message.getTimestamp() != null) {
                timeText.setText(timeFormat.format(message.getTimestamp()));
                timeText.setVisibility(View.VISIBLE);
            } else {
                timeText.setVisibility(View.GONE);
            }

            // Style based on message type
            ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();

            if (message.getType() == ChatMessage.TYPE_USER) {
                // User message - align right, blue background
                cardView.setCardBackgroundColor(itemView.getContext().getResources()
                    .getColor(R.color.primary, null));
                messageText.setTextColor(itemView.getContext().getResources()
                    .getColor(R.color.white, null));
                timeText.setTextColor(itemView.getContext().getResources()
                    .getColor(R.color.white, null));

                layoutParams.leftMargin = 100;
                layoutParams.rightMargin = 16;
            } else {
                // Bot message - align left, gray background
                cardView.setCardBackgroundColor(itemView.getContext().getResources()
                    .getColor(R.color.surface_variant, null));
                messageText.setTextColor(itemView.getContext().getResources()
                    .getColor(R.color.text_primary, null));
                timeText.setTextColor(itemView.getContext().getResources()
                    .getColor(R.color.text_secondary, null));

                layoutParams.leftMargin = 16;
                layoutParams.rightMargin = 100;
            }

            cardView.setLayoutParams(layoutParams);
        }
    }
}