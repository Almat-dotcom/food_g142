package kz.bitlab.car5G142.controller;

import kz.bitlab.car5G142.entity.Food;
import kz.bitlab.car5G142.entity.Ingredient;
import kz.bitlab.car5G142.entity.Manufacturer;
import kz.bitlab.car5G142.repository.FoodRepository;
import kz.bitlab.car5G142.repository.IngredientRepository;
import kz.bitlab.car5G142.repository.ManufacturerRepository;
import kz.bitlab.car5G142.service.FoodService;
import kz.bitlab.car5G142.service.IngredientService;
import kz.bitlab.car5G142.service.ManufacturerService;
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
    private final FoodService foodService;
    private final IngredientService ingredientService;
    private final ManufacturerService manufacturerService;


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

        Page<Food> foodsPage = foodService.foodsPage(sortOrder, sortBy, pageNumber, sizeValue,
                name, cal, price, countryId, ingredientId);
        List<Integer> pageNumbers = foodService.pageNumbers(foodsPage);
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        List<Ingredient> ingredients = ingredientService.getAll();

        model.addAttribute("foods", foodsPage);
        model.addAttribute("countries", manufacturers);
        model.addAttribute("ingredients", ingredients);
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
        foodService.add(food);
        return "redirect:/";
    }

    @GetMapping("view-food")
    public String view(@RequestParam(name = "id") Long id,
                       Model model) {
        Food food = foodService.getById(id);
        List<Ingredient> ingredients = ingredientService.removeRedundantIngredients(food);
        List<Manufacturer> manufacturers = manufacturerService.getAll();

        model.addAttribute("st", food);
        model.addAttribute("countries", manufacturers);
        model.addAttribute("availableIngredients", ingredients);
        return "edit-ffod";
    }

    @PostMapping("delete-food")
    public String deleteFood(@RequestParam(name = "id") Long id) {
        foodService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("edit-food")
    public String editFood(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "fullName") String fullName,
                           @RequestParam(name = "calories") Integer calories,
                           @RequestParam(name = "amounts") Integer amounts,
                           @RequestParam(name = "price") Integer price,
                           @RequestParam(name = "manufacturer") Long manufId) {
        foodService.editFood(id, manufId, amounts, fullName, calories, price);
        return "redirect:/";
    }

    @PostMapping("/assign-ingredient")
    public String assign(@RequestParam(name = "ingredient_id") Long ingredientId,
                         @RequestParam(name = "food_id") Long foodId) {
        Long id = foodService.addIngredientToFood(foodId, ingredientId);
        return "redirect:/view-food?id=" + id;
    }


    @PostMapping("/delete-ingredient")
    public String deleteIngr(@RequestParam(name = "ingredient_id") Long ingredientId,
                             @RequestParam(name = "food_id") Long foodId) {
        Long id = foodService.deleteIngredient(foodId, ingredientId);
        return "redirect:/view-food?id=" + id;
    }


}
