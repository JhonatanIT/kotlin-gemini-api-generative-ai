/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.sample.feature.structuredoutput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StructuredOutputViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<StructuredOutputUiState> =
        MutableStateFlow(StructuredOutputUiState.Initial)
    val uiState: StateFlow<StructuredOutputUiState> =
        _uiState.asStateFlow()

    fun structuredOutput(prompt: String) {
        _uiState.value = StructuredOutputUiState.Loading

        viewModelScope.launch {
            // Non-streaming
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = StructuredOutputUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = StructuredOutputUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun structuredOutputStreaming(prompt: String) {
        _uiState.value = StructuredOutputUiState.Loading

        viewModelScope.launch {
            try {
                var outputContent = ""
                generativeModel.generateContentStream(prompt)
                    .collect { response ->
                        outputContent += response.text
                        _uiState.value = StructuredOutputUiState.Success(outputContent)
                    }
            } catch (e: Exception) {
                _uiState.value = StructuredOutputUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}
