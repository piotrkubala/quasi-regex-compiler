package pl.edu.agh.kis.validation;

import pl.edu.agh.kis.model.Model;

/**
 * Validate model generated by pattern.
 */
public interface Validator {
    void validate(Model model);
}
