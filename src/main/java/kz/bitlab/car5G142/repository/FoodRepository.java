package kz.bitlab.car5G142.repository;

import kz.bitlab.car5G142.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findAllByFullNameContainsIgnoreCaseAndCaloriesLessThanEqual(String name, int cal);

    List<Food> findAllByFullNameContainsIgnoreCase(String name);

    List<Food> findAllByCaloriesLessThanEqual(Integer cal);

    @Query("select f from Food f " +
            "where :fullName is null or lower(f.fullName) LIKE %:fullName% " +
            "and :cal is null or f.calories<=:cal ")
    List<Food> findAlmat(@Param(value = "fullName") String fullName, @Param(value = "cal") Integer calor);

    Page<Food> findAll(Specification<Food> specification, Pageable pageable);
}
