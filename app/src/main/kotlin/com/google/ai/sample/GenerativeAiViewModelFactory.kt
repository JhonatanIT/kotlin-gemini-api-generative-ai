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

package com.google.ai.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.sample.feature.chat.ChatViewModel
import com.google.ai.sample.feature.multimodal.PhotoReasoningViewModel
import com.google.ai.sample.feature.structuredoutput.StructuredOutputViewModel
import com.google.ai.sample.feature.text.SummarizeViewModel

val GenerativeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val config = generationConfig {
            temperature = 0.2f
        }

        return with(modelClass) {
            when {
                isAssignableFrom(SummarizeViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-2.0-flash-thinking-exp-1219` AI model
                    // for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-2.0-flash",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    SummarizeViewModel(generativeModel)
                }

                isAssignableFrom(StructuredOutputViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-2.0-flash-thinking-exp-1219` AI model
                    // for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-2.0-flash",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = generationConfig {
                            responseMimeType = "application/json"
                            responseSchema = Schema(
                                name = "recipes",
                                description = "List of recipes",
                                type = FunctionType.ARRAY,
                                items = Schema(
                                    name = "recipe",
                                    description = "A recipe",
                                    type = FunctionType.OBJECT,
                                    properties = mapOf(
                                        "recipeName" to Schema(
                                            name = "recipeName",
                                            description = "Name of the recipe",
                                            type = FunctionType.STRING,
                                            nullable = false
                                        ),
                                    ),
                                    required = listOf("recipeName")
                                ),
                            )
                        }
                    )
                    StructuredOutputViewModel(generativeModel)
                }

                isAssignableFrom(PhotoReasoningViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-2.0-flash-thinking-exp-1219` AI model
                    // for multimodal text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-2.0-flash",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    PhotoReasoningViewModel(generativeModel)
                }

                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-2.0-flash-thinking-exp-1219` AI model for chat
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-2.0-flash",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    ChatViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}
