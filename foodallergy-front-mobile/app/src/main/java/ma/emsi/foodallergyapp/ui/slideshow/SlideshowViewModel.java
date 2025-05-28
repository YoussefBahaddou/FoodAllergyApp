package ma.emsi.foodallergyapp.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mIsLoading;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mIsLoading = new MutableLiveData<>();

        mText.setValue("Gestion des allergies\n\nConfigurez vos allergies alimentaires pour recevoir des alertes personnalisées lors du scan de produits.");
        mIsLoading.setValue(false);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void setLoading(boolean isLoading) {
        mIsLoading.setValue(isLoading);
    }

    public void updateText(String newText) {
        mText.setValue(newText);
    }
}