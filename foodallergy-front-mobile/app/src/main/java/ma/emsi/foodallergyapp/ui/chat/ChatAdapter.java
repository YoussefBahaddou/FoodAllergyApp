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

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
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
        holder.messageText.setText(message.getMessage());
        
        // Set different styles for user and bot messages
        MaterialCardView cardView = (MaterialCardView) holder.messageText.getParent();
        if (message.getType() == ChatMessage.TYPE_USER) {
            cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
            holder.messageText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).leftMargin = 0;
            ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).rightMargin = 50;
        } else {
            cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.secondary));
            holder.messageText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).leftMargin = 50;
            ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).rightMargin = 0;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}