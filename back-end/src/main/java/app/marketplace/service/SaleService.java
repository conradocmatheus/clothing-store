package app.marketplace.service;

import app.marketplace.entity.Product;
import app.marketplace.entity.Sale;
import app.marketplace.repository.CustomerRepository;
import app.marketplace.repository.EmployeeRepository;
import app.marketplace.repository.ProductRepository;
import app.marketplace.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    @Transactional
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

        double totalValue = 0;
        for (Product product : sale.getProducts()) {
            // Optional is used here to handle the possibility of a missing product safely
            // It helps avoid null pointer exceptions by explicitly checking for the presence of the value
            Optional<Product> optionalProduct = productRepository.findById(product.getId());
            if (optionalProduct.isPresent()) {
                Product fullProduct = optionalProduct.get();
                if (fullProduct.getPrice() != null) {
                    totalValue += fullProduct.getPrice();
                } else {
                    throw new IllegalArgumentException("Product price cannot be null for product with ID: " + product.getId());
                }
            } else {
                throw new EntityNotFoundException("Product not found with ID: " + product.getId());
            }
        }
        sale.setTotalValue(totalValue);
        saleRepository.save(sale);
        return "Sale successfully saved with ID: " + sale.getId();
    }

    // DELETE
    //  a Sale
    public String delete(Long id) {
        if (saleRepository.existsById(id)) {
            this.saleRepository.deleteById(id);
            return "Sale with id: " + id + " deleted";
        } else {
            throw new EntityNotFoundException("Sale with ID: " + id + " not found");
        }
    }

    // GET
    // List all Sales
    public List<Sale> listAll() {
        return this.saleRepository.findAll();
    }

    // GET
    // Find Sale by ID
    public Sale findById(Long id) {
        if (saleRepository.existsById(id)) {
            return this.saleRepository.findById(id).get();
        } else {
            throw new EntityNotFoundException("Sale with ID: " + id + " not found");
        }
    }

    // GET
    // List sales by shipping address
    public List<Sale> findByShippingAddressStartingWith(String shippingAddress) {
        if (shippingAddress == null || shippingAddress.isEmpty()) {
            throw new IllegalArgumentException("Shipping address cannot be null or empty");
        }
        List<Sale> sales = saleRepository.findByShippingAddressStartingWith(shippingAddress);
        if (sales.isEmpty()) {
            throw new EntityNotFoundException("No sales found with ShippingAddress: " + shippingAddress);
        }
        return sales;
    }

    // GET
    // List sales by employee id
    public List<Sale> findSalesByEmployeeId(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        return saleRepository.findSalesByEmployeeId(employeeId);
    }

    // GET
    // List Sales by Client ID
    public List<Sale> findSalesByCustomerId(Long customerId) {
        List<Sale> sales = saleRepository.findByCustomerId(customerId);
        if (sales.isEmpty()) {
            throw new EntityNotFoundException("No sales found for customer ID: " + customerId);
        }
        return sales;
    }


    // Verify Sale existence by ID
    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }
}
