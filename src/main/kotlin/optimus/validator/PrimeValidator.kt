package optimus.validator

import optimus.service.PrimeService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class PrimeValidator(private val primeService: PrimeService) : ConstraintValidator<Prime, Number> {
    override fun isValid(value: Number?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return primeService.isPrime(value.toLong())
    }
}
