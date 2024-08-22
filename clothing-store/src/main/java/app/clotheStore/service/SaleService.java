package app.clotheStore.service;

import app.clotheStore.entity.Customer;
import app.clotheStore.entity.Employee;
import app.clotheStore.entity.Product;
import app.clotheStore.entity.Sale;
import app.clotheStore.repository.CustomerRepository;
import app.clotheStore.repository.EmployeeRepository;
import app.clotheStore.repository.ProductRepository;
import app.clotheStore.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProductRepository productRepository;

    // POST
    // Save a Sale
    public String save(Sale sale) {
        if (sale.getCustomer() == null || !customerRepository.existsById(sale.getCustomer().getId())) {
            throw new EntityNotFoundException("Customer not found with ID: " + (sale.getCustomer() != null ? sale.getCustomer().getId() : null));
        }

        if (sale.getEmployee() != null && !employeeRepository.existsById(sale.getEmployee().getId())) {
            throw new EntityNotFoundException("Employee not found with ID: " + sale.getEmployee().getId());
        }

        if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Sale must have at least one product");
        }

        for (Product product : sale.getProducts()) {
            if (!productRepository.existsById(product.getId())) {
                throw new EntityNotFoundException("Product not found with ID: " + product.getId());
            }
        }

        saleRepository.save(sale);
        return "Sale successfully saved with ID: " + sale.getId();
    }

    // DELETE
    //  a Sale
    public String delete(Long id){
        if (saleRepository.existsById(id)){
            this.saleRepository.deleteById(id);
            return "Customer with id: " + id + " deleted";
        } else {
            throw new EntityNotFoundException("Customer with ID: " + id + " not found");
        }
    }

    // GET
    // List all Sales
    public List<Sale> listAll(){
        return this.saleRepository.findAll();
    }

    // GET
    // Find Sale by ID
    public Sale findById(Long id){
        if (saleRepository.existsById(id)){
            return this.saleRepository.findById(id).get();
        } else {
            throw new EntityNotFoundException("Sale with ID: " + id + " not found");
        }
    }

    // Verify Sale existence by ID
    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }
}
