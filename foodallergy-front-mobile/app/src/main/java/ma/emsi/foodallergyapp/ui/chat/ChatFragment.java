package ma.emsi.foodallergyapp.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ma.emsi.foodallergyapp.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup UI
        setupUI();
        setupObservers();

        return root;
    }

    private void setupUI() {
        // TODO: Implement chat UI setup
        // For now, show a placeholder message
        TextView textView = new TextView(requireContext());
        textView.setText("Chat feature coming soon!");
        textView.setTextSize(18);
        textView.setPadding(32, 32, 32, 32);

        // If you have a container in your layout, add the text view to it
        // Otherwise, you can set it as the root view
    }

    private void setupObservers() {
        // TODO: Observe chat data when implemented
        chatViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            // Update UI with chat data
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}