package kz.bitlab.car5G142.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class FoodController {
    private final FoodRepository foodRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final IngredientRepository ingredientRepository;


    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(name = "name", required = false) String name,
                       @RequestParam(name = "calories", required = false) Integer cal,
                       @RequestParam(name = "max_price", required = false) Integer price,
                       @RequestParam(name = "country_id", required = false) Long countryId,
                       @RequestParam(name = "ingredient_id", required = false) Long ingredientId,
                       @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
                       @RequestParam(name = "size", defaultValue = "5") Integer sizeValue,
                       @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
                       @RequestParam(name = "sort_order", defaultValue = "ASC") String sortOrder
    ) {
        Page<Food> foodsPage;
        Sort sort=Sort.by(sortOrder.equals("ASC")? Sort.Direction.ASC: Sort.Direction.DESC, sortBy);
        PageRequest req = PageRequest.of(pageNumber, sizeValue, sort);

        //Query

//        if (name != null && cal!=null) {
//            foods = foodRepository.findAllByFullNameContainsIgnoreCaseAndCaloriesLessThanEqual(name,cal);
//        } else if(name!=null){
//            foods = foodRepository.findAlmat(name.toLowerCase());
//        }else if(cal!=null){
//            foods = foodRepository.findAllByCaloriesLessThanEqual(cal);
//        }else {
//            foods = foodRepository.findAll();
//        }
        Specification<Food> specification = FoodSpecification.queryFood(name, cal, price, countryId, ingredientId);
        foodsPage = foodRepository.findAll(specification, req);
//        model.addAttribute("sortBy")

        model.addAttribute("foods", foodsPage);
        model.addAttribute("countries", manufacturerRepository.findAll());
        model.addAttribute("ingredients", ingredientRepository.findAll());

        List<Integer> pageNumbers = IntStream.range(0, foodsPage.getTotalPages()).boxed().toList();
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("size", sizeValue);
        model.addAttribute("lastPage", foodsPage.getTotalPages() - 1);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("chosenCountry", countryId);
        model.addAttribute("chosenIngr", ingredientId);
        return "foods";
    }

    @PostMapping("add-food")
    public String add(Food food) {
        foodRepository.save(food);
        return "redirect:/";
    }

    @GetMapping("view-food")
    public String view(@RequestParam(name = "id") Long id,
                       Model model) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));
        model.addAttribute("st", food);
        model.addAttribute("countries", manufacturerRepository.findAll());
        List<Ingredient> ingredients = ingredientRepository.findAll();
        ingredients.removeAll(food.getIngredients());
        model.addAttribute("availableIngredients", ingredients);
        return "edit-ffod";
    }

    @PostMapping("delete-food")
    public String deleteFood(@RequestParam(name = "id") Long id) {
//        Food food=foodRepository.findById(id).orElse(null);
//        foodRepository.delete(food);
        foodRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("edit-food")
    public String editFood(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "fullName") String fullName,
                           @RequestParam(name = "calories") Integer calories,
                           @RequestParam(name = "amounts") Integer amounts,
                           @RequestParam(name = "price") Integer price,
                           @RequestParam(name = "manufacturer") Long manufId) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));
        Manufacturer manufacturer = manufacturerRepository.findById(manufId).orElseThrow(() -> new RuntimeException("Country not found"));
        food.setAmounts(amounts);
        food.setFullName(fullName);
        food.setCalories(calories);
        food.setPrice(price);
        food.setManufacturer(manufacturer);
        foodRepository.save(food);
        return "redirect:/";
    }

    @PostMapping("/assign-ingredient")
    public String assign(@RequestParam(name = "ingredient_id") Long ingredientId,
                         @RequestParam(name = "food_id") Long foodId) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new RuntimeException("Ingredient not found"));

        food.getIngredients().add(ingredient);
        foodRepository.save(food);
        return "redirect:/view-food?id=" + food.getId();
    }

    @PostMapping("/delete-ingredient")
    public String deleteIngr(@RequestParam(name = "ingredient_id") Long ingredientId,
                             @RequestParam(name = "food_id") Long foodId) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new RuntimeException("Ingredient not found"));

        food.getIngredients().remove(ingredient);
        foodRepository.save(food);
        return "redirect:/view-food?id=" + food.getId();
    }


}
