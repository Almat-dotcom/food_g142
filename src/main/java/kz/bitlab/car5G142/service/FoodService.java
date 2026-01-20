package kz.bitlab.car5G142.service;

import kz.bitlab.car5G142.entity.Food;
import kz.bitlab.car5G142.entity.Ingredient;
import kz.bitlab.car5G142.entity.Manufacturer;
import kz.bitlab.car5G142.repository.FoodRepository;
import kz.bitlab.car5G142.repository.IngredientRepository;
import kz.bitlab.car5G142.repository.ManufacturerRepository;
import kz.bitlab.car5G142.specification.FoodSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final ManufacturerRepository manufacturerRepository;

    public Food getById(Long id) {
        return foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));
    }

    public void deleteById(Long id) {
        foodRepository.deleteById(id);
    }

    public Long addIngredientToFood(Long foodId, Long ingredientId) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new RuntimeException("Ingredient not found"));

        food.getIngredients().add(ingredient);
        Food saved = foodRepository.save(food);
        return saved.getId();
    }

    public void add(Food food) {
        foodRepository.save(food);
    }

    public Long deleteIngredient(Long foodId, Long ingredientId) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new RuntimeException("Ingredient not found"));

        food.getIngredients().remove(ingredient);
        foodRepository.save(food);
        return foodId;
    }

    public void editFood(Long id, Long manufId, Integer amounts, String fullName, Integer calories, Integer price) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));
        Manufacturer manufacturer = manufacturerRepository.findById(manufId).orElseThrow(() -> new RuntimeException("Country not found"));
        food.setAmounts(amounts);
        food.setFullName(fullName);
        food.setCalories(calories);
        food.setPrice(price);
        food.setManufacturer(manufacturer);
        foodRepository.save(food);
    }

    public Page<Food> foodsPage(String sortOrder, String sortBy, Integer pageNumber, Integer sizeValue,
                                String name, Integer cal, Integer price, Long countryId, Long ingredientId) {
        Sort sort = Sort.by(sortOrder.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest req = PageRequest.of(pageNumber, sizeValue, sort);
        Specification<Food> specification = FoodSpecification.queryFood(name, cal, price, countryId, ingredientId);
        return foodRepository.findAll(specification, req);
    }

    public List<Integer> pageNumbers(Page<Food> foodsPage) {
        return IntStream.range(0, foodsPage.getTotalPages()).boxed().toList();
    }
}
