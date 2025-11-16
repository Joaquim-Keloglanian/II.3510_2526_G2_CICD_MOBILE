package com.example.deviseapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.deviseapp.data.RateRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RateRepository(app)

    private val _currencies = MutableLiveData<List<String>>(repo.getSupportedCurrencies())
    val currencies: LiveData<List<String>> = _currencies

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _fromAmount = MutableLiveData<String>("")
    val fromAmount: LiveData<String> = _fromAmount

    private val _toAmount = MutableLiveData<String>("")
    val toAmount: LiveData<String> = _toAmount

    private var latestRates: Map<String, Double>? = null
    private var currentBase: String = "EUR"
    private var conversionJob: Job? = null
    private val formatter = DecimalFormat("#.##")

    enum class AmountField { FROM, TO }

    init {
        viewModelScope.launch {
            runCatching { repo.getRates(base = currentBase) }
        }
    }

    fun refreshRates(base: String, force: Boolean = false) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                currentBase = base
                latestRates = repo.getRates(base = base, forceRefresh = force)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur réseau"
            } finally {
                _loading.value = false
            }
        }
    }

    fun onAmountChanged(
        field: AmountField,
        amountText: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        if (field == AmountField.FROM) {
            _fromAmount.value = amountText
        } else {
            _toAmount.value = amountText
        }

        if (amountText.isBlank()) {
            if (field == AmountField.FROM) {
                _toAmount.value = ""
            } else {
                _fromAmount.value = ""
            }
            return
        }

        val amount = amountText.toDoubleOrNull() ?: return
        if (fromCurrency == toCurrency) {
            if (field == AmountField.FROM) {
                _toAmount.value = amountText
            } else {
                _fromAmount.value = amountText
            }
            return
        }

        conversionJob?.cancel()
        conversionJob = viewModelScope.launch {
            try {
                val base = if (field == AmountField.FROM) fromCurrency else toCurrency
                val target = if (field == AmountField.FROM) toCurrency else fromCurrency
                val rates = repo.getRates(base = base, forceRefresh = false)
                latestRates = rates
                currentBase = base
                val rate = rates[target] ?: return@launch
                val converted = formatter.format(amount * rate)
                if (field == AmountField.FROM) {
                    _toAmount.postValue(converted)
                } else {
                    _fromAmount.postValue(converted)
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Erreur réseau")
            }
        }
    }

    fun forceRefresh(base: String) {
        refreshRates(base, force = true)
    }
}


