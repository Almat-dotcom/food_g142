package kz.bitlab.car5G142.specification;

import jakarta.persistence.criteria.Join;
import kz.bitlab.car5G142.entity.Food;
import kz.bitlab.car5G142.entity.Ingredient;
import kz.bitlab.car5G142.entity.Manufacturer;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class FoodSpecification {
    public Specification<Food> queryFood(String name, Integer calory,
                                         Integer maxPrice, Long countryId, Long ingredientId) {
        Specification<Food> result = (r, cq, cb) -> {
            return cq.getRestriction();
        };
        if (name != null) {
            result = result.and((r, cq, cb) ->
                    cb.and(cb.like(cb.lower(r.get("fullName")), "%" + name + "%")));
        }

        if (calory != null) {
            result = result.and((r, cq, cb) ->
                    cb.lessThanOrEqualTo(r.get("calories"), calory));
        }

        if (maxPrice != null) {
            result = result.and((r, cq, cb) ->
                    cb.lessThanOrEqualTo(r.get("price"), maxPrice));
        }

//        if(countryId!=null){
//            result=result.and((r, cq, cb) ->
//                r.get("manufacturer").get("id").equalTo(countryId));
//        }

        if (countryId != null) {
            result = result.and((r, cq, cb) -> {
                Join<Food, Manufacturer> m = r.join("manufacturer");
                return cb.equal(m.get("id"), countryId);
            });
        }


        if (ingredientId != null) {
            result = result.and((r, cq, cb) -> {
                // чтобы Food не дублировался
                Join<Food, Ingredient> ing = r.join("ingredients");
                cq.distinct(true);
                return cb.equal(ing.get("id"), ingredientId);
            });
        }

//        if (ingredientId != null) {
//            result = result.and((r, cq, cb) ->
//                    r.get("ingredients").get("id").equalTo(ingredientId));
//        }

        return result;
    }
}
