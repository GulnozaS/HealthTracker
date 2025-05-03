import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SymptomViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val apiService = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApiService::class.java)

    private val apiKey = BuildConfig.GEMINI_API_KEY // Set in build.gradle

    fun analyzeSymptoms(symptoms: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val prompt = """
                    Act as a medical assistant. For these symptoms: $symptoms
                    Provide in this exact format:
                    
                    [Possible Conditions]
                    - Condition 1
                    - Condition 2
                    
                    [Recommended Actions]
                    - Action 1
                    - Action 2
                    
                    [Urgency Level]
                    - Low/Medium/High
                    
                    Disclaimer: I'm not a doctor - consult a professional.
                """.trimIndent()

                val response = apiService.generateContent(
                    apiKey,
                    GeminiRequest(listOf(Content(listOf(Part(prompt))))))
                        val result = response.candidates.first().content.parts.first().text
                            _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val response: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}