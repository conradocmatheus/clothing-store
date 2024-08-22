package app.clotheStore.repository;

import app.clotheStore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameStartingWith(String name);


    List<Product> findByPriceGreaterThanEqual(Double price);
}
