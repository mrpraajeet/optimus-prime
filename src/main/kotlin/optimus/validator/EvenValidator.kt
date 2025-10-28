package optimus.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EvenValidator : ConstraintValidator<Even, Number> {
    override fun isValid(value: Number?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return value.toLong() % 2 == 0L
    }
}
