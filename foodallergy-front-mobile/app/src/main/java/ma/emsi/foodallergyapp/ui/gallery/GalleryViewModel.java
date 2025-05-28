package ma.emsi.foodallergyapp.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mIsScanning;
    private final MutableLiveData<String> mScanResult;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mIsScanning = new MutableLiveData<>();
        mScanResult = new MutableLiveData<>();

        mText.setValue("Scanner de produits\n\nUtilisez cette section pour scanner les codes-barres ou rechercher des produits par nom.");
        mIsScanning.setValue(false);
        mScanResult.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Boolean> getIsScanning() {
        return mIsScanning;
    }

    public LiveData<String> getScanResult() {
        return mScanResult;
    }

    public void setScanning(boolean isScanning) {
        mIsScanning.setValue(isScanning);
    }

    public void setScanResult(String result) {
        mScanResult.setValue(result);
    }

    public void updateText(String newText) {
        mText.setValue(newText);
    }
}