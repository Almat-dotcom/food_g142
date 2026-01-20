package kz.bitlab.car5G142.service;

import kz.bitlab.car5G142.entity.Food;
import kz.bitlab.car5G142.entity.Ingredient;
import kz.bitlab.car5G142.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<Ingredient> removeRedundantIngredients(Food food) {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        ingredients.removeAll(food.getIngredients());
        return ingredients;
    }

    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }
}
