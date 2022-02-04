package com.cda.form.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cda.form.model.Form;

public interface FormRepository extends JpaRepository<Form, Long> {

	Form findByName(String name);

	@Query("SELECT f.name FROM Form f ORDER BY f.name ASC") 
    List<String> findNames();
}
