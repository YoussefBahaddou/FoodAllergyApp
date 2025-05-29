package ma.emsi.foodallergyapp.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ma.emsi.foodallergyapp.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView();
        setupInputHandling();
        observeViewModel();
    }

    private void setupViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        binding.recyclerMessages.setLayoutManager(layoutManager);
        binding.recyclerMessages.setAdapter(chatAdapter);
    }

    private void setupInputHandling() {
        // Send button click
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Enter key press
        binding.editMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Clear chat button
        binding.btnClearChat.setOnClickListener(v -> {
            chatViewModel.clearMessages();
            Toast.makeText(getContext(), "Chat cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendMessage() {
        String messageText = binding.editMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            chatViewModel.sendMessage(messageText);
            binding.editMessage.setText("");
        }
    }

    private void observeViewModel() {
        // Observe messages
        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.setMessages(messages);
            if (!messages.isEmpty()) {
                binding.recyclerMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        // Observe loading state
        chatViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSend.setEnabled(!isLoading);
            binding.textStatus.setText(isLoading ? "Typing..." : "Online");
        });

        // Observe errors
        chatViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
