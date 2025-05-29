package ma.emsi.foodallergyapp.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ma.emsi.foodallergyapp.model.ScanHistory;
import ma.emsi.foodallergyapp.repository.HistoryRepository;

public class HistoryViewModel extends ViewModel {

    private MutableLiveData<List<ScanHistory>> scanHistory;
    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> error;
    private HistoryRepository historyRepository;

    public HistoryViewModel() {
        scanHistory = new MutableLiveData<>();
        loading = new MutableLiveData<>();
        error = new MutableLiveData<>();
        historyRepository = new HistoryRepository();
    }

    public LiveData<List<ScanHistory>> getScanHistory() {
        return scanHistory;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadScanHistory(String userId) {
        loading.setValue(true);
        error.setValue(null);

        historyRepository.getUserScanHistory(userId, new HistoryRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<ScanHistory> historyList) {
                loading.setValue(false);
                scanHistory.setValue(historyList);
            }

            @Override
            public void onError(String errorMessage) {
                loading.setValue(false);
                error.setValue(errorMessage);
            }
        });
    }

    public void refreshHistory(String userId) {
        loadScanHistory(userId);
    }
}