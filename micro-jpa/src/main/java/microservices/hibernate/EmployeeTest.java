package microservices.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

public class EmployeeTest {
	
	private static EntityManager em;
	
	public static void main(String[] args) {
		
		EntityManagerFactory emf =
				Persistence.createEntityManagerFactory("EmployeeService");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Employee> query = cb.createCriteriaDelete(Employee.class);
		query.from(Employee.class);
		em.createQuery(query).executeUpdate();
		em.getTransaction().commit();
		createEmployee(1, "Saint", "Peter", "Engineering");
		createEmployee(2, "Jack", "Dorsey", "Imaginea");
		createEmployee(3, "Sam", "Fox", "Imaginea");
		
		em.close();
		emf.close();
	}
	
	private static void createEmployee(int id, String firstName, String lastName, String dept) {
		em.getTransaction().begin();
		Employee emp = new Employee(id, firstName, lastName, dept);
		em.persist(emp);
		em.getTransaction().commit();
	}
}