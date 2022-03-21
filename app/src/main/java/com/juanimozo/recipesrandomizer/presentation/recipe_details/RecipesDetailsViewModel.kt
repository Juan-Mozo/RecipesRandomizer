package com.juanimozo.recipesrandomizer.presentation.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanimozo.recipesrandomizer.core.util.Resource
import com.juanimozo.recipesrandomizer.domain.use_case.RecipeUseCases
import com.juanimozo.recipesrandomizer.presentation.recipe_details.state.NewRecipeState
import com.juanimozo.recipesrandomizer.presentation.recipe_details.state.SimilarRecipesState
import com.juanimozo.recipesrandomizer.presentation.util.EmptyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases
): ViewModel() {

    private val _similarRecipesState = MutableStateFlow(SimilarRecipesState(emptyList(), false))
    val similarRecipesState: StateFlow<SimilarRecipesState> = _similarRecipesState

    private val _newRecipe = MutableStateFlow(NewRecipeState(EmptyModel.emptyRecipe))
    val newRecipe: StateFlow<NewRecipeState> = _newRecipe

    private var getSimilarRecipesJob: Job? = null
    private var getRecipeInfoJob: Job? = null

    fun getSimilarRecipes(id: Int) {
        getSimilarRecipesJob?.cancel()
        getSimilarRecipesJob = viewModelScope.launch {
            recipeUseCases.getSimilarRecipesUseCase(id)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _similarRecipesState.value = similarRecipesState.value.copy(
                                recipes = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _similarRecipesState.value = similarRecipesState.value.copy(
                                recipes = result.data ?: emptyList(),
                                isLoading = true
                            )
                        }
                        is Resource.Error -> {
                            _similarRecipesState.value = similarRecipesState.value.copy(
                                recipes = result.data ?: emptyList(),
                                isLoading = false
                            )
                            // ToDo:: -VM- *1* / Priority: M
                            // Description: Agregar error
                        }
                    }

                }
        }
    }

    fun getRecipeInformation(id: Int) {
        getRecipeInfoJob?.cancel()
        getRecipeInfoJob = viewModelScope.launch {
            recipeUseCases.getRecipeInfoUseCase(id)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _newRecipe.value = newRecipe.value.copy(
                                recipe = result.data ?: EmptyModel.emptyRecipe
                            )
                        }
                        is Resource.Loading -> {
                            _newRecipe.value = newRecipe.value.copy(
                                recipe = result.data ?: EmptyModel.emptyRecipe
                            )
                        }
                        is Resource.Error -> {
                            _newRecipe.value = newRecipe.value.copy(
                                recipe = result.data ?: EmptyModel.emptyRecipe
                            )
                            // ToDo:: -VM- *1* / Priority: M
                            // Description: Agregar error
                        }
                    }

                }
        }
    }
}