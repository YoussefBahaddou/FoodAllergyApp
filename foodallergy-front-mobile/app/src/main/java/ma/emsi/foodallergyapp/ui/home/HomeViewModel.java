package ma.emsi.foodallergyapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Bienvenue dans Food Allergy App\n\nVotre assistant personnel pour détecter les allergènes dans vos aliments");
    }

    public LiveData<String> getText() {
        return mText;
    }
}