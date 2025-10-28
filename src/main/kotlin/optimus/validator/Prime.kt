package optimus.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PrimeValidator::class])
@MustBeDocumented
annotation class Prime(
    val message: String = "Input must be a prime number",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
